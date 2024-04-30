package action

import java.util.Date

import action.NuoDML.profileResults
import canvas.NuoModifier.ActionType
import canvas.{NuoDataTypeHandler, NuoModifier}
import client.{NuoBqClient, NuoGcsClient, NuoS3Client}
import com.amazonaws.services.machinelearning.model._
import execution.NuoRequestHandler
import execution.NuoRequestHandler.{getMessageResponseRef, sendExecutionRunningMessage}
import logging.{NuoBiller, NuoLogger}
import metadata.MLaaSMetadata.MLDataType
import metadata.NuoRecognitionMetadata.NuoExecutionStatusCode
import metadata.NuoRequestMetadata._
import metadata.StorageMetadata.NuoField
import metadata.{MLaaSMetadata, NuoRecognitionMetadata}
import nlp.grammar.NuoEvaEnglishListener

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.duration._

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 06Nov2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoMLaaS {

  val maxCategoryCount = 100
  val maxCategoryPercent = 1.0

  def prepareAndExecuteModel(nuoUserMessage: NuoUserMessage,
                             nuoEvaEnglishListener: NuoEvaEnglishListener): String = {

    val mlModelDetailsOption = nuoEvaEnglishListener.currAnalysisRecognitionData.NuoMLDetails
    if (mlModelDetailsOption.isEmpty) {
      NuoRequestHandler.reportErrorToUser(new Exception("I could not find the ml model details from recognized data."))
      throw new Exception("Unreachable Code Statement")
    }
    val mlModelDetails = mlModelDetailsOption.get
    var actionType = mlModelDetails.actionType.get
    val sourceTableName = mlModelDetails.sourceTableName.get
    val tempInputTableName = "__IN_" + sourceTableName
    //    val targetTableName = nuoEvaEnglishListener.currAnalysisRecognitionData.QuestionAlias
    val targetTableName = sourceTableName + "_Predictions"
    val targetColumnName = mlModelDetails.targetColumnName.get
    val modelName = NuoRequestHandler.getMd5Hash(mlModelDetails.modelName.get)
    val orderedSourceColumns = mlModelDetails.orderedSourceColumns
    val trainDataPercent = 70.0
    val scoreThresholdPercent = -1
    val modelNameWithTenantId = NuoRequestHandler.getMd5Hash(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + modelName)

    val modelInputFilePrefix =
      if (actionType.equalsIgnoreCase(ActionType.External.MLaaS.TrainModel))
        s"Tenants/${NuoEvaEnglishListener.nuoTenantDetails.tenantId}/$modelName/train/"
      else
        s"Tenants/${NuoEvaEnglishListener.nuoTenantDetails.tenantId}/$modelName/predict/"

    val modelOutputFilePrefix = s"Tenants/${NuoEvaEnglishListener.nuoTenantDetails.tenantId}/Internal/$modelName/output/"

    val trainDataSchemaLocation = s"Tenants/${NuoEvaEnglishListener.nuoTenantDetails.tenantId}/$modelName/train_schema.json"
    val predictDataSchemaLocation = s"Tenants/${NuoEvaEnglishListener.nuoTenantDetails.tenantId}/$modelName/predict_schema.json"
    val profilingTableName = targetTableName + "_profiling"
    val patternTableName = targetTableName + "_pattern"

    var latestStatus = nuoEvaEnglishListener.currAnalysisRecognitionData.NuoExecutionStatuses.maxBy(_.Status)
    var resultMessage = ""
    val timeNow = System.currentTimeMillis()
    var nuoPollingDetails = NuoPollingDetails(
      StartTimeMillis = timeNow + 10000,
      PollingIntervalMillis = 2000,
      EndTimeMillis = timeNow + (30 * 60 * 1000)
    )

    if (latestStatus.Status == NuoExecutionStatusCode.RECOGNITION_COMPLETE) {

      if (actionType.equalsIgnoreCase(ActionType.External.MLaaS.TrainModel)) {

        if (!NuoBqClient.getTableSchema(
          NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
          sourceTableName).exists(_._1.equalsIgnoreCase(targetColumnName))) {

          NuoRequestHandler.reportErrorToUser(new Exception(s"I could not find the target column $targetColumnName in the training data! I cannot train the model."))
          throw new Exception("Unreachable Code Statement")
        }
      }

      var sourceColumns =
        if (actionType.equalsIgnoreCase(ActionType.External.MLaaS.TrainModel)) {

          NuoBqClient
            .getTableSchema(
              NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
              sourceTableName)
            .map(_._1)
        } else {

          orderedSourceColumns
        }

      if (actionType.equalsIgnoreCase(ActionType.External.MLaaS.PredictValue)) {
        sourceColumns = sourceColumns.filterNot(_.equalsIgnoreCase(targetColumnName))
      }

      val rowNumQuery =
        sourceColumns
          .map(ele => s"`$sourceTableName`.`$ele`")
          .mkString(
            "SELECT ",
            ",",
            s", ROW_NUMBER() OVER() as __rownum FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`$sourceTableName`")
      val jobId =
        NuoBqClient
          .executeDMLAndSaveResult(
            datasetName = NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
            tableName = tempInputTableName,
            writeIfEmpty = false,
            append = false,
            querySql = rowNumQuery,
            saveAsView = false
          )

      if (!jobId.equals("-1")) {
        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:=
          NuoRecognitionMetadata.NuoExecutionStatus(
            Status = NuoRecognitionMetadata.NuoExecutionStatusCode.BQ_JOB_STARTED,
            Description = None,
            RefParams = List(jobId))
        NuoRequestHandler.assignStepsCompleted(1, nuoEvaEnglishListener)
      }
    } else if (
      latestStatus.Status == NuoExecutionStatusCode.BQ_JOB_STARTED
        || latestStatus.Status == NuoExecutionStatusCode.BQ_JOB_COMPLETE) {

      if (latestStatus.Status == NuoExecutionStatusCode.BQ_JOB_STARTED
        && NuoBqClient.isBQJobComplete(latestStatus.RefParams.head)) {

        latestStatus = NuoRecognitionMetadata.NuoExecutionStatus(
          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.BQ_JOB_COMPLETE,
          Description = None,
          RefParams = latestStatus.RefParams)


        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:= latestStatus
        NuoRequestHandler.incrementStepsCompleted(nuoEvaEnglishListener)

      }
      if (latestStatus.Status == NuoExecutionStatusCode.BQ_JOB_COMPLETE) {

        val recordCount = NuoBqClient.executeDMLAndGetResult(s"SELECT COUNT(*)" +
          s" FROM `${NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetName}`.`$tempInputTableName`",
          100l)
          .head.head.toInt
        if (NuoBiller.isServiceAllowed(
          actionType,
          if (actionType.equalsIgnoreCase(ActionType.External.MLaaS.PredictValue))
            math.ceil(recordCount / 1000.0).toInt
          else 1
        )) {

          val jobId = NuoBqClient.exportTableToGcs(datasetName = NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
            tableName = tempInputTableName,
            filePrefix = modelInputFilePrefix,
            destinationFormat = "CSV",
            printHeader = "true",
            enableCompression = false)

          if (!jobId.equals("-1")) {

            nuoEvaEnglishListener
              .currAnalysisRecognitionData
              .NuoExecutionStatuses +:=
              NuoRecognitionMetadata.NuoExecutionStatus(
                Status = NuoRecognitionMetadata.NuoExecutionStatusCode.TABLE_EXPORT_JOB_STARTED,
                Description = None,
                RefParams = List(jobId))
          }
        }
      }
    } else if (
      latestStatus.Status == NuoExecutionStatusCode.TABLE_EXPORT_JOB_STARTED
        || latestStatus.Status == NuoExecutionStatusCode.TABLE_EXPORT_JOB_COMPLETE) {

      if (latestStatus.Status == NuoExecutionStatusCode.TABLE_EXPORT_JOB_STARTED
        && NuoBqClient.isBQJobComplete(latestStatus.RefParams.head)) {

        latestStatus = NuoRecognitionMetadata.NuoExecutionStatus(
          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.TABLE_EXPORT_JOB_COMPLETE,
          Description = None,
          RefParams = latestStatus.RefParams)


        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:= latestStatus
        NuoRequestHandler.incrementStepsCompleted(nuoEvaEnglishListener)

      }
      if (latestStatus.Status == NuoExecutionStatusCode.TABLE_EXPORT_JOB_COMPLETE) {

        val expectedSizeBytes = NuoStorage.transferFilesBetweenS3(isImportFromS3 = false,
          isExportToS3 = true,
          s3BucketName = NuoEvaEnglishListener.BucketName.MlaasDataBucketName,
          s3FilePrefix = modelInputFilePrefix,
          gcsBucketName = NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName,
          nuoFilePrefix = modelInputFilePrefix,
          nuoEvaEnglishListener)

        val minWaitTime = System.currentTimeMillis() + math.min(math.ceil(expectedSizeBytes / 3.75 / 1024.0), 60 * 1000.0).toLong
        val maxWaitTime = System.currentTimeMillis() + 540 * 1000l

        //        val expectedSizeBytes = prevRefParams(0)
        //        val previousCount = prevRefParams(1)
        //        val minWaitTime = prevRefParams(2)
        //        val maxWaitTime = prevRefParams(3)
        //        val targetType = prevRefParams(4)
        //        val targetBucket = prevRefParams(5)
        //        val targetPath = prevRefParams(6)

        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:=
          NuoRecognitionMetadata.NuoExecutionStatus(
            Status = NuoRecognitionMetadata.NuoExecutionStatusCode.ML_EXPORT_TO_S3_STARTED,
            Description = None,
            RefParams = List[String](
              expectedSizeBytes.toString,
              "0",
              minWaitTime.toString,
              maxWaitTime.toString,
              "S3",
              NuoEvaEnglishListener.BucketName.MlaasDataBucketName,
              modelInputFilePrefix
            )
          )

      }
    } else if (
      latestStatus.Status == NuoExecutionStatusCode.ML_EXPORT_TO_S3_STARTED
        || latestStatus.Status == NuoExecutionStatusCode.ML_EXPORT_TO_S3_COMPLETE) {

      if (latestStatus.Status == NuoExecutionStatusCode.ML_EXPORT_TO_S3_STARTED
        && NuoStorage.isTransferFunctionComplete(latestStatus.RefParams, nuoEvaEnglishListener)) {

        latestStatus = NuoRecognitionMetadata.NuoExecutionStatus(
          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.ML_EXPORT_TO_S3_COMPLETE,
          Description = None,
          RefParams = latestStatus.RefParams)


        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:= latestStatus
        NuoRequestHandler.incrementStepsCompleted(nuoEvaEnglishListener)

      }
      if (latestStatus.Status == NuoExecutionStatusCode.ML_EXPORT_TO_S3_COMPLETE) {

        val newEvaluationOrPredictionId = {
          if (actionType.equalsIgnoreCase(ActionType.External.MLaaS.TrainModel))
            getNewEvaluationId(modelNameWithTenantId + "_Evaluation")
          else
            getNewPredictionId(modelNameWithTenantId + "_Prediction")
        }
        if (actionType.equalsIgnoreCase(ActionType.External.MLaaS.TrainModel)) {

          val sourceColNamesAndDataTypes = NuoBqClient.getTableSchema(NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId, tempInputTableName)

          val sourceDistinctColCounts = NuoBqClient.executeDMLAndGetResult(querySql = sourceColNamesAndDataTypes
            .map(colNameAndDataType => s"COUNT(DISTINCT `${colNameAndDataType._1}`)")
            .mkString("SELECT ", ",", s", COUNT(*) FROM `${NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetName}`.`$tempInputTableName`"),
            pollTimeMillis = 1000l).head

          val sourceColNameTypeAndCount = sourceColNamesAndDataTypes.zip(sourceDistinctColCounts).map(ele => (ele._1._1, ele._1._2, ele._2.toInt))
          val totalCount = sourceDistinctColCounts.reverse.head.toInt

          /*                """
                            |{
                            |  "excludedAttributeNames": [],
                            |  "grammarName": "1.0",
                            |  "dataFormat": "CSV",
                            |  "rowId": null,
                            |  "dataFileContainsHeader": true,
                            |  "attributes": [
                            |    {
                            |      "attributeName": "age",
                            |      "attributeType": "NUMERIC"
                            |    },
                            |    {
                            |      "attributeName": "job",
                            |      "attributeType": "CATEGORICAL"
                            |    },
                            |      "attributeName": "y",
                            |      "attributeType": "BINARY"
                            |    }
                            |  ],
                            |  "targetAttributeName": "y"
                            |}
                          """.stripMargin
                          */

          val sourceColNameAndMLDataTypes = getInputTableColAndMLDataTypes(tempInputTableName,
            colNameTypeAndCount = sourceColNameTypeAndCount.toList,
            totalCount = totalCount)

          MLaaSMetadata.saveTrainAndPredictDataSchema(trainDataSchemaLocation,
            predictDataSchemaLocation,
            MLaaSMetadata.TrainDataSchema(excludedAttributeNames = List(),
              version = Some("1.0"),
              dataFormat = Some("CSV"),
              rowId = Some("__rownum"),
              dataFileContainsHeader = Some("true"),
              attributes = sourceColNameAndMLDataTypes.map { colNameAndMLDataType =>
                MLaaSMetadata.DataSchemaAttribute(colNameAndMLDataType._1, colNameAndMLDataType._2)
              },
              targetAttributeName = Some(targetColumnName)),
            targetColumnName = targetColumnName)
          //
          //          val recipe =
          //
          //            if (!columns.exists(colMap => "^\\$`.+\\`.`.+\\`\\$$".r.findFirstIn(colMap(NuoModifier.ActionAttrName.ColumnSource)).isDefined)) {
          //              //            """
          //              //              |{
          //              //              |  "outputs": [
          //              //              |    "ALL_BINARY",
          //              //              |    "ALL_CATEGORICAL",
          //              //              |    "quantile_bin(ALL_NUMERIC,50)",
          //              //              |    "lowercase(ALL_TEXT)"
          //              //              |  ]
          //              //              |}
          //              //            """.stripMargin
          //              columns
          //                .filterNot(colMap => colMap(NuoModifier.ActionAttrName.ColumnName).equalsIgnoreCase(targetColumn))
          //                .map { colMap =>
          //                  var exp = NuoExpressionAndVariable.processExpression(expressionInput = colMap(NuoModifier.ActionAttrName.ColumnSource),
          //                    includeSourceName = false,
          //                    nodeId = nodeId,
          //                    nuoTenantDetails)._1
          //                  exp = if (exp.startsWith("`")) exp.substring(1) else exp
          //                  exp = if (exp.endsWith("`")) exp.substring(0, exp.length - 1) else exp
          //                  "\"" + exp + "\""
          //                }.mkString("""{ "outputs": [ """, ",", " ]}")
          //            } else ""

          val modelType = getMLModelType(targetColMLDataType = sourceColNameAndMLDataTypes.find(_._1.equalsIgnoreCase(targetColumnName)).get._2 /*,
            sourceColNameTypeAndCount.find(_._1.equalsIgnoreCase(targetColumn)).get._3*/)

          if (modelType == null)
            throw new Exception(s"I cannot use column $targetColumnName as target column for its ML Data type has been identified as TEXT ")

          /*
          * Create data source for training
          * */
          val trainDataSourceId = getNewDataSourceId(modelNameWithTenantId + MLaaSMetadata.MLDatasetSuffix.trainData)
          createDataSource(dataSourceId = trainDataSourceId,
            dataSourceName = trainDataSourceId,
            percentBegin = 0,
            percentEnd = trainDataPercent.toInt,
            dataSchemaLocation = s"s3://${NuoEvaEnglishListener.BucketName.MlaasDataBucketName}/$trainDataSchemaLocation",
            dataUrl = s"s3://${NuoEvaEnglishListener.BucketName.MlaasDataBucketName}/$modelInputFilePrefix")

          /*
          * Create data source for testing (evaluation)
          * */
          val testDataSourceId = getNewDataSourceId(modelNameWithTenantId + MLaaSMetadata.MLDatasetSuffix.testData)
          createDataSource(dataSourceId = testDataSourceId,
            dataSourceName = testDataSourceId,
            percentBegin = 0, //Because we are using data rearrangement strategy with complement parameter.
            percentEnd = trainDataPercent.toInt,
            dataSchemaLocation = s"s3://${NuoEvaEnglishListener.BucketName.MlaasDataBucketName}/$trainDataSchemaLocation",
            dataUrl = s"s3://${NuoEvaEnglishListener.BucketName.MlaasDataBucketName}/$modelInputFilePrefix")

          val newModelId = getNewModelId(modelNameWithTenantId + "_Train")

          createModel(mlModelId = newModelId,
            mlModelName = newModelId,
            recipe = "",
            modelType = modelType,
            trainDataSourceId = trainDataSourceId)

          createEvaluation(mlModelId = newModelId,
            evaluationId = newEvaluationOrPredictionId,
            evaluationName = newEvaluationOrPredictionId,
            testDataSourceId = testDataSourceId)
          //
          //          if (scoreThresholdPercent.nonEmpty)
          //            setThreshold(newModelId, modelType, scoreThresholdPercent.toFloat,
          //              nuoTenantDetails)

        } else {

          val mlModelId = getLatestMLModelIdWithPrefix(modelNameWithTenantId, null)
          if (mlModelId.isEmpty) {

            NuoRequestHandler.reportErrorToUser(new Exception(s"I could not find the trained model with name $modelName."))
            throw new Exception("Unreachable Code Statement")
          }

          if (scoreThresholdPercent > 0)
            setThreshold(mlModelId, null, scoreThresholdPercent.toFloat)

          NuoS3Client.getS3FileList(NuoEvaEnglishListener.BucketName.MlaasDataBucketName, modelOutputFilePrefix + "batch-prediction/")
            .foreach(fileName => NuoS3Client.deleteS3File(NuoEvaEnglishListener.BucketName.MlaasDataBucketName, fileName))

          NuoGcsClient.listGcsFileNames(NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName, modelOutputFilePrefix, null)
            .foreach(gcsFileName => NuoGcsClient.deleteObject(NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName, gcsFileName))


          /*
          * Create data source for batch prediction
          * */
          val predictDataSourceId = getNewDataSourceId(modelNameWithTenantId + MLaaSMetadata.MLDatasetSuffix.predictData)


          createBatchPrediction(mlModelName = modelNameWithTenantId,
            mlModelId = mlModelId,
            batchPredictionDataSourceId = predictDataSourceId,
            batchPredictionDataSourceName = predictDataSourceId,
            batchPredictionId = newEvaluationOrPredictionId,
            batchPredictionName = newEvaluationOrPredictionId,
            dataBatchSchemaLocation = s"s3://${NuoEvaEnglishListener.BucketName.MlaasDataBucketName}/$predictDataSchemaLocation",
            inputDataUrl = s"s3://${NuoEvaEnglishListener.BucketName.MlaasDataBucketName}/$modelInputFilePrefix",
            s3OutputUrl = s"s3://${NuoEvaEnglishListener.BucketName.MlaasDataBucketName}/$modelOutputFilePrefix")
        }
        val minWaitTime = System.currentTimeMillis() + 2 * 60 * 1000l
        // This is the maximum time we will wait for training job to finish
        // Amazon ML allows maximum of 7 days. We could increase this limit in future if required
        val maxWaitTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000l
        val refreshIntervalMillis = 60 * 1000l
        val timeNow = System.currentTimeMillis()
        val status = if (actionType.equalsIgnoreCase(ActionType.External.MLaaS.TrainModel)) {

          nuoPollingDetails =
            NuoPollingDetails(
              StartTimeMillis = timeNow + (15 * 60 * 1000),
              PollingIntervalMillis = 10000,
              EndTimeMillis = timeNow + (30 * 60 * 1000)
            )
          NuoRecognitionMetadata.NuoExecutionStatusCode.AML_MODEL_TRAINING_STARTED
        } else {
          nuoPollingDetails =
            NuoPollingDetails(
              StartTimeMillis = timeNow + (3 * 60 * 1000),
              PollingIntervalMillis = 10000,
              EndTimeMillis = timeNow + (15 * 60 * 1000)
            )
          NuoRecognitionMetadata.NuoExecutionStatusCode.AML_MODEL_PREDICTION_STARTED
        }
        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:=
          NuoRecognitionMetadata.NuoExecutionStatus(
            Status = status,
            Description = None,
            RefParams = List[String](
              newEvaluationOrPredictionId,
              minWaitTime.toString,
              maxWaitTime.toString,
              refreshIntervalMillis.toString
            )
          )
      }
    } else if (
      latestStatus.Status == NuoExecutionStatusCode.AML_MODEL_PREDICTION_STARTED
        || latestStatus.Status == NuoExecutionStatusCode.AML_MODEL_PREDICTION_COMPLETE) {

      if (latestStatus.Status == NuoExecutionStatusCode.AML_MODEL_PREDICTION_STARTED
        && isMLModelComplete(latestStatus.Status, latestStatus.RefParams)) {

        latestStatus = NuoRecognitionMetadata.NuoExecutionStatus(
          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.AML_MODEL_PREDICTION_COMPLETE,
          Description = None,
          RefParams = latestStatus.RefParams)


        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:= latestStatus
        NuoRequestHandler.incrementStepsCompleted(nuoEvaEnglishListener)

      }
      if (latestStatus.Status == NuoExecutionStatusCode.AML_MODEL_PREDICTION_COMPLETE) {

        val expectedSizeBytes = NuoStorage.transferFilesBetweenS3(
          isImportFromS3 = true,
          isExportToS3 = false,
          s3BucketName = NuoEvaEnglishListener.BucketName.MlaasDataBucketName,
          s3FilePrefix = modelOutputFilePrefix + "batch-prediction/result/",
          gcsBucketName = NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName,
          nuoFilePrefix = modelOutputFilePrefix,
          nuoEvaEnglishListener)

        val minWaitTime = System.currentTimeMillis() + math.min(math.ceil(expectedSizeBytes / 3.75 / 1024.0), 60 * 1000.0).toLong
        val maxWaitTime = System.currentTimeMillis() + 540 * 1000l

        //        val expectedSizeBytes = prevRefParams(0)
        //        val previousCount = prevRefParams(1)
        //        val minWaitTime = prevRefParams(2)
        //        val maxWaitTime = prevRefParams(3)
        //        val targetType = prevRefParams(4)
        //        val targetBucket = prevRefParams(5)
        //        val targetPath = prevRefParams(6)

        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:=
          NuoRecognitionMetadata.NuoExecutionStatus(
            Status = NuoRecognitionMetadata.NuoExecutionStatusCode.ML_IMPORT_FROM_S3_STARTED,
            Description = None,
            RefParams = List[String](
              expectedSizeBytes.toString,
              "0",
              minWaitTime.toString,
              maxWaitTime.toString,
              "GCS",
              NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName,
              modelOutputFilePrefix
            )
          )
      }
    } else if (
      latestStatus.Status == NuoExecutionStatusCode.AML_MODEL_TRAINING_STARTED
        || latestStatus.Status == NuoExecutionStatusCode.AML_MODEL_TRAINING_COMPLETE) {

      if (latestStatus.Status == NuoExecutionStatusCode.AML_MODEL_TRAINING_STARTED
        && isMLModelComplete(
        latestStatus.Status,
        refParams = latestStatus.RefParams)) {

        latestStatus = NuoRecognitionMetadata.NuoExecutionStatus(
          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.AML_MODEL_TRAINING_COMPLETE,
          Description = None,
          RefParams = latestStatus.RefParams)

        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:= latestStatus
        NuoRequestHandler.incrementStepsCompleted(nuoEvaEnglishListener)
      }
      if (latestStatus.Status == NuoExecutionStatusCode.AML_MODEL_TRAINING_COMPLETE) {


        //        NuoBqClient.writeStorageMetadataToFile()
        //        NuoRecognitionMetadata.writeCurrQuestionRecognitionData(nuoEvaEnglishListener)

        resultMessage = getMessageResponseRef(statusCode = 200,
          status = "OK",
          messageRef =
            NuoQueryResponse(
              NuoEvaMessage = NuoEvaMessage(
                AnalysisId = "",
                RuleText = "",
                CommunicationType = NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_RESULT_AVAILABLE,
                Message = s"ML Model training and evaluation has been complete for <b>$targetColumnName</b> using data from <b>$sourceTableName</b>",
                LeftEntityName = None,
                RightEntityName = None,
                NuoMappingInput = None,
                NuoPollingDetails = None),
              Result =
                Some(NuoDataGrid(
                  Metadata = List("ML Model Metric Name", "ML Model Metric Value").map(NuoField("", "", _, "String")),
                  Data = getMLPerformanceMetrics(latestStatus.RefParams.head))),
              ProfilingResult = None,
              Pattern = None
            )
        )

      }
    } else if (
      latestStatus.Status == NuoExecutionStatusCode.ML_IMPORT_FROM_S3_STARTED
        || latestStatus.Status == NuoExecutionStatusCode.ML_IMPORT_FROM_S3_COMPLETE) {

      if (latestStatus.Status == NuoExecutionStatusCode.ML_IMPORT_FROM_S3_STARTED
        && NuoStorage.isTransferFunctionComplete(latestStatus.RefParams, nuoEvaEnglishListener)) {

        latestStatus = NuoRecognitionMetadata.NuoExecutionStatus(
          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.ML_IMPORT_FROM_S3_COMPLETE,
          Description = None,
          RefParams = latestStatus.RefParams)


        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:= latestStatus
        NuoRequestHandler.incrementStepsCompleted(nuoEvaEnglishListener)

      }
      if (latestStatus.Status == NuoExecutionStatusCode.ML_IMPORT_FROM_S3_COMPLETE) {


        val tempOutputTableName = s"__OUT_$targetTableName"
        val jobId = NuoBqClient.loadDelimitedNuoFilesAutoSchema(sourceFileList = List(modelOutputFilePrefix),
          datasetName = NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
          tableName = tempOutputTableName,
          append = false,
          delimiter = ",",
          quoteCharacter = "\"",
          leadingRowsToSkip = 1)

        if (!jobId.equals("-1")) {

          nuoEvaEnglishListener
            .currAnalysisRecognitionData
            .NuoExecutionStatuses +:=
            NuoRecognitionMetadata.NuoExecutionStatus(
              Status = NuoRecognitionMetadata.NuoExecutionStatusCode.ML_BQ_TEMP_LOAD_STARTED,
              Description = None,
              RefParams = List(jobId))
        }
      }
    } else if (
      latestStatus.Status == NuoExecutionStatusCode.ML_BQ_TEMP_LOAD_STARTED
        || latestStatus.Status == NuoExecutionStatusCode.ML_BQ_TEMP_LOAD_COMPLETE) {

      if (latestStatus.Status == NuoExecutionStatusCode.ML_BQ_TEMP_LOAD_STARTED
        && NuoBqClient.isBQJobComplete(latestStatus.RefParams.head)) {

        latestStatus = NuoRecognitionMetadata.NuoExecutionStatus(
          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.ML_BQ_TEMP_LOAD_COMPLETE,
          Description = None,
          RefParams = latestStatus.RefParams)


        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:= latestStatus
        NuoRequestHandler.incrementStepsCompleted(nuoEvaEnglishListener)
      }
      if (latestStatus.Status == NuoExecutionStatusCode.ML_BQ_TEMP_LOAD_COMPLETE) {


        val tempOutputTableName = s"__OUT_$targetTableName"
        val mlModelId = getLatestMLModelIdWithPrefix(modelNameWithTenantId, null)
        if (mlModelId.isEmpty) {
          NuoRequestHandler.reportErrorToUser(new Exception(s"I could not find the trained model with name $modelName."))

          throw new Exception("Unreachable Code Statement")
        }

        val selectQuery = new mutable.StringBuilder("SELECT ")
          .append(
            NuoBqClient.getTableSchema(
              NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
              sourceTableName)
              .map(ele => s"`input`.`${ele._1}`").mkString(",")
          )

        NuoEvaEnglishListener.Client
          .amazonMachineLearningClient
          .getMLModel(new GetMLModelRequest().withMLModelId(mlModelId))
          .getMLModelType
        match {

          case x if x.equalsIgnoreCase(MLModelType.REGRESSION.toString) =>
            selectQuery
              .append(s",output.score as `$targetColumnName`, 100 as Prediction_Score")

          case x if x.equalsIgnoreCase(MLModelType.MULTICLASS.toString) =>

            val tempOutputColumns = NuoBqClient.getTableSchema(
              NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
              tempOutputTableName)
              .filterNot(_._1.equalsIgnoreCase("tag"))
              .map(ele => s"`output`.`${ele._1}` * 100 as ${ele._1}")
              .mkString(",")

            selectQuery.append(",")
            selectQuery.append(tempOutputColumns)

          case x if x.equalsIgnoreCase(MLModelType.BINARY.toString) =>
            selectQuery
              .append(s",output.bestAnswer as `$targetColumnName`,output.score as Prediction_Score")
        }

        selectQuery.append(s" FROM `${NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetName}`.`$tempInputTableName` as input" +
          s" INNER JOIN `${NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetName}`.`$tempOutputTableName` as output" +
          s" ON input.__rownum = output.tag")


        val jobId =
          NuoBqClient
            .executeDMLAndSaveResult(
              datasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
              tableName = targetTableName,
              writeIfEmpty = false,
              append = false,
              querySql = selectQuery.toString(),
              saveAsView = false
            )

        if (!jobId.equals("-1")) {
          nuoEvaEnglishListener
            .currAnalysisRecognitionData
            .NuoExecutionStatuses +:=
            NuoRecognitionMetadata.NuoExecutionStatus(
              Status = NuoRecognitionMetadata.NuoExecutionStatusCode.ML_BQ_MAIN_LOAD_STARTED,
              Description = None,
              RefParams = List(jobId))
        }
      }
    } else if (
      latestStatus.Status == NuoExecutionStatusCode.ML_BQ_MAIN_LOAD_STARTED
        || latestStatus.Status == NuoExecutionStatusCode.ML_BQ_MAIN_LOAD_COMPLETE) {

      if (latestStatus.Status == NuoExecutionStatusCode.ML_BQ_MAIN_LOAD_STARTED
        && NuoBqClient.isBQJobComplete(latestStatus.RefParams.head)) {

        latestStatus = NuoRecognitionMetadata.NuoExecutionStatus(
          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.ML_BQ_MAIN_LOAD_COMPLETE,
          Description = None,
          RefParams = latestStatus.RefParams)


        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:= latestStatus
        NuoRequestHandler.incrementStepsCompleted(nuoEvaEnglishListener)
      }
      if (latestStatus.Status == NuoExecutionStatusCode.ML_BQ_MAIN_LOAD_COMPLETE) {

        var jobId = profileResults(
          resultDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
          resultTableName = targetTableName,
          profilingTableName = profilingTableName,
          patternTableName = patternTableName,
          nuoEvaEnglishListener
        )

        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:=
          NuoRecognitionMetadata.NuoExecutionStatus(
            Status = NuoRecognitionMetadata.NuoExecutionStatusCode.ML_PROFILING_STARTED,
            Description = None,
            RefParams = List(jobId)
          )

      }

    } else if (latestStatus.Status == NuoExecutionStatusCode.ML_PROFILING_COMPLETE
      || latestStatus.Status == NuoExecutionStatusCode.ML_PROFILING_STARTED) {

      if (latestStatus.Status == NuoExecutionStatusCode.ML_PROFILING_STARTED
        && NuoBqClient.isBQJobComplete(latestStatus.RefParams.head)) {

        latestStatus = NuoRecognitionMetadata.NuoExecutionStatus(
          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.ML_PROFILING_COMPLETE,
          Description = None,
          RefParams = latestStatus.RefParams)


        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:= latestStatus

        NuoRequestHandler.incrementStepsCompleted(nuoEvaEnglishListener)

        //        NuoRecognitionMetadata.writeCurrQuestionRecognitionData(nuoEvaEnglishListener)
      }

      if (latestStatus.Status == NuoExecutionStatusCode.ML_PROFILING_COMPLETE) {

        //        NuoBqClient.writeStorageMetadataToFile()
        //        NuoRecognitionMetadata.writeCurrQuestionRecognitionData(nuoEvaEnglishListener)

        resultMessage = getMessageResponseRef(statusCode = 200,
          status = "OK",
          messageRef =
            NuoQueryResponse(
              NuoEvaMessage = NuoEvaMessage(
                AnalysisId = "",
                RuleText = "",
                CommunicationType = NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_RESULT_AVAILABLE,
                Message = "",
                LeftEntityName = None,
                RightEntityName = None,
                NuoMappingInput = None,
                NuoPollingDetails = None),
              Result =
                Some(NuoDataGrid(
                  Metadata = NuoBqClient
                    .getTableSchema(NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                      targetTableName)
                    .map(pair => NuoField("", "", pair._1, pair._2))
                    .toList,
                  Data = NuoBqClient
                    .listTableData(
                      datasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                      tableName = targetTableName,
                      pageToken = null,
                      pageSize = 100L)
                    ._2
                    .map(_.toList).toList)),
              ProfilingResult =
                Some(NuoDataGrid(
                  Metadata = NuoBqClient
                    .getTableSchema(NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                      profilingTableName)
                    .map(pair => NuoField("", "", pair._1, pair._2))
                    .toList,
                  Data = NuoBqClient
                    .listTableData(
                      datasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                      tableName = profilingTableName,
                      pageToken = null,
                      pageSize = 100L)
                    ._2
                    .map(_.toList).toList)),
              Pattern =
                Some(NuoDataGrid(
                  Metadata = NuoBqClient
                    .getTableSchema(NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                      patternTableName)
                    .map(pair => NuoField("", "", pair._1, pair._2))
                    .toList,
                  Data = NuoBqClient
                    .listTableData(
                      datasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                      tableName = patternTableName,
                      pageToken = null,
                      pageSize = 100L)
                    ._2
                    .map(_.toList).toList))
            )
        )
      } else {
        val timeNow = System.currentTimeMillis()
        sendExecutionRunningMessage(
          None,
          None,
          Some(nuoPollingDetails),
          nuoEvaEnglishListener
        )
      }

    }
    if (resultMessage != null && resultMessage.nonEmpty)
      resultMessage
    else {
      val timeNow = System.currentTimeMillis()
      sendExecutionRunningMessage(
        None,
        None,
        Some(nuoPollingDetails),
        nuoEvaEnglishListener
      )

    }
  }

  def getInputTableColAndMLDataTypes(tempInputTableName: String,
                                     //                                   stringColumns: List[String],
                                     colNameTypeAndCount: List[(String, String, Int)],
                                     totalCount: Int): List[(String, String)] = {

    colNameTypeAndCount.map(combo => (combo._1, getMLDataType(colName = combo._1,
      DataType = combo._2,
      colCount = combo._3,
      totalCount = totalCount,
      tempInputTableName = tempInputTableName)))
  }

  def getMLModelType(targetColMLDataType: String): MLModelType = {

    targetColMLDataType match {
      case x if x.equals(MLDataType.Binary) =>
        MLModelType.BINARY

      case MLDataType.Categorical =>
        MLModelType.MULTICLASS

      case MLDataType.Numeric =>
        MLModelType.REGRESSION

      case _ =>
        null
    }
  }

  def getMLDataType(colName: String,
                    DataType: String,
                    colCount: Int,
                    totalCount: Int,
                    tempInputTableName: String): String = {

    //    /*
    //    **************************************************
    //     *  [start] TEST purpose only
    //    **************************************************
    //    * */
    //    if (colCount == 2)
    //
    //      MLDataType.Numeric
    //    else
    //    /*
    //    **************************************************
    //    * [end] TEST purpose only
    //    **************************************************
    //    * */
    if (colName.equalsIgnoreCase("__rownum"))
      MLDataType.Categorical
    else if (DataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.Boolean)
      || (colCount == 2 && NuoBqClient.executeDMLAndGetResult(querySql = s"SELECT DISTINCT `$colName`" +
      s" FROM `${NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetName}`.`$tempInputTableName`",
      pollTimeMillis = 1000l).head
      .forall(ele => ele.nonEmpty && List("true", "y", "1", "t", "yes", "false", "0", "no", "f", "n").contains(ele))))

      MLDataType.Binary

    else if (List(/*NuoDataTypeHandler.NuoDataType.Int64,
      NuoDataTypeHandler.NuoDataType.Integer,
      */ NuoDataTypeHandler.NuoDataType.String,
      NuoDataTypeHandler.NuoDataType.Date,
      NuoDataTypeHandler.NuoDataType.Time,
      NuoDataTypeHandler.NuoDataType.Timestamp /*,
      NuoDataTypeHandler.NuoDataType.Float64,
      NuoDataTypeHandler.NuoDataType.Float*/)
      .map(_.toLowerCase)
      .contains(DataType.toLowerCase)
      && colCount <= maxCategoryCount
      && (colCount / totalCount * 100) <= maxCategoryPercent) {

      MLDataType.Categorical

    } else if (List(NuoDataTypeHandler.NuoDataType.Int64,
      NuoDataTypeHandler.NuoDataType.Integer,
      NuoDataTypeHandler.NuoDataType.Float64,
      NuoDataTypeHandler.NuoDataType.Float)
      .map(_.toLowerCase)
      .contains(DataType.toLowerCase))

      MLDataType.Numeric

    else MLDataType.Text


  }

  //  val awsCredentials = new BasicAWSCredentials(accessKey, secretKey)
  //  val client = new AmazonMachineLearningClient(awsCredentials)

  //  def main(args: Array[String]): Unit = {
  //    //    createDataSource(trainDataSourceId, friendlyModelPrefix + " - training data", 0, trainPercent)
  //    //    createDataSource(testDataSourceId, friendlyModelPrefix + " - testing data", trainPercent, 100)
  //    //    createModel(mlModelName, trainDataSourceId)
  //    //    createEvaluation(mlModelName)
  //    //
  //    //
  //
  //    //    waitForModel(mlModelName)
  //    //    setThreshold(mlModelName, threshold)
  //    //    createBatchPrediction(mlModelName, s3OutputUrl)
  //
  //
  //  }

  private def createDataSource(dataSourceId: String,
                               dataSourceName: String,
                               percentBegin: Int,
                               percentEnd: Int,
                               dataSchemaLocation: String,
                               dataUrl: String): Unit = {

    val dataRearrangementString =
      s"""
         |{
         |  "splitting":{
         |    "percentBegin":$percentBegin,
         |    "percentEnd":$percentEnd,
         |    "complement":"true",
         |    "strategy":"random",
         |    "strategyParams":{
         |    "randomSeed":"$dataUrl"
         |    }
         |  }
         |}
         |""".stripMargin
    val dataSpec = new S3DataSpec()
      .withDataLocationS3(dataUrl)
      .withDataRearrangement(dataRearrangementString)
      //      .withDataSchema(dataSchema)
      .withDataSchemaLocationS3(dataSchemaLocation)
    val request = new CreateDataSourceFromS3Request()
      .withDataSourceId(dataSourceId)
      .withDataSourceName(dataSourceName)
      .withComputeStatistics(true)
      .withDataSpec(dataSpec)

    NuoEvaEnglishListener.Client.amazonMachineLearningClient.createDataSourceFromS3(request)
    println(s"Created DataSource $dataSourceName with id $dataSourceId")
  }

  def getNewModelId(mlModelName: String): String = {
    val currActiveModelId = getLatestMLModelIdWithPrefix(mlModelName, null)
    if (currActiveModelId.nonEmpty) deleteMLModel(currActiveModelId)
    mlModelName + "_" + System.currentTimeMillis()
  }

  def getNewPredictionId(mlModelName: String): String = {
    val currActivePredictionId = getLatestPredictionIdWithPrefix(mlModelName, null)
    if (currActivePredictionId.nonEmpty) deletePrediction(currActivePredictionId)
    mlModelName + "_" + System.currentTimeMillis()
  }

  def getNewEvaluationId(mlModelName: String): String = {
    val currActiveEvaluationId = getLatestEvaluationIdWithPrefix(mlModelName, null)
    if (currActiveEvaluationId.nonEmpty) deleteEvaluation(currActiveEvaluationId)
    mlModelName + "_" + System.currentTimeMillis()
  }

  def getNewDataSourceId(dataSourceName: String): String = {
    deleteOldDataSourcesWithPrefix(dataSourceName, null)
    dataSourceName + "_" + System.currentTimeMillis()
  }

  /**
    * Creates an ML Model object, which begins the training process.
    * The quality of the model that the training algorithm produces depends
    * primarily on the data, but also on the hyper-parameters specified in
    * the parameters map, and the feature-processing recipe.
    *
    */
  private def createModel(mlModelId: String,
                          mlModelName: String,
                          recipe: String,
                          modelType: MLModelType,
                          trainDataSourceId: String): Unit = {


    val parameters = Map(
      "sgd.maxPasses" -> "100",
      "sgd.maxMLModelSizeInBytes" -> "104857600",
      "sgd.l2RegularizationAmount" -> "1e-4")

    var request = new CreateMLModelRequest()
      .withMLModelId(mlModelId)
      .withMLModelName(mlModelName)
      .withMLModelType(modelType)
      .withParameters(parameters.asJava)
      .withTrainingDataSourceId(trainDataSourceId)

    if (recipe.nonEmpty) request = request.withRecipe(recipe)

    NuoEvaEnglishListener.Client.amazonMachineLearningClient.createMLModel(request)
    println(s"Created ML Model with id $mlModelName")
  }

  def deleteMLModel(mlModelId: String): Unit = {
    try {
      NuoEvaEnglishListener.Client.amazonMachineLearningClient.deleteMLModel(new DeleteMLModelRequest().withMLModelId(mlModelId))
    } catch {
      case _: Exception => NuoLogger.printInfo("Got an exception for delete model request and ignoring it")
    }

  }

  def getLatestMLModelIdWithPrefix(modelName: String,
                                   pageToken: String): String = {

    val request = new DescribeMLModelsRequest().withPrefix(modelName)
    if (pageToken != null && pageToken.nonEmpty) request.withNextToken(pageToken)
    val result = NuoEvaEnglishListener.Client.amazonMachineLearningClient.describeMLModels(request)

    val nextToken = result.getNextToken
    if (result.getResults.asScala.nonEmpty) {

      var oldModels = result.getResults.asScala
        .filter(_.getMLModelId.startsWith(modelName))
        .filter(model => !List("DELETED").contains(model.getStatus.toUpperCase))
        .map(_.getMLModelId)
      if (nextToken != null && nextToken.nonEmpty) oldModels = oldModels :+ getLatestMLModelIdWithPrefix(modelName, nextToken)

      if (oldModels.nonEmpty) {
        oldModels.sortWith(_ > _).tail.foreach(deleteMLModel)
        oldModels.head
      } else ""
    } else {
      ""
    }
  }


  def deleteEvaluation(mlEvalId: String): Unit = {
    try {
      NuoEvaEnglishListener.Client.amazonMachineLearningClient.deleteEvaluation(new DeleteEvaluationRequest().withEvaluationId(mlEvalId))
    } catch {
      case _: Exception => NuoLogger.printInfo("Got an exception for delete evaluation request and ignoring it")
    }

  }

  def getLatestEvaluationIdWithPrefix(modelName: String,
                                      pageToken: String): String = {

    val request = new DescribeEvaluationsRequest().withPrefix(modelName)
    if (pageToken != null && pageToken.nonEmpty) request.withNextToken(pageToken)
    val result = NuoEvaEnglishListener.Client.amazonMachineLearningClient.describeEvaluations(request)

    val nextToken = result.getNextToken
    if (result.getResults.asScala.nonEmpty) {

      var oldEvaluations = result.getResults.asScala
        .filter(_.getEvaluationId.startsWith(modelName))
        .filter(model => !List("DELETED").contains(model.getStatus.toUpperCase))
        .map(_.getEvaluationId)
      //        .tail
      if (nextToken != null && nextToken.nonEmpty) oldEvaluations = oldEvaluations :+ getLatestEvaluationIdWithPrefix(modelName, nextToken)

      if (oldEvaluations.nonEmpty) {
        oldEvaluations.sortWith(_ > _).tail.foreach(deleteEvaluation)
        oldEvaluations.head
      } else ""
    } else {
      ""
    }
  }


  def deletePrediction(mlEvalId: String): Unit = {
    try {
      NuoEvaEnglishListener.Client.amazonMachineLearningClient.deleteBatchPrediction(new DeleteBatchPredictionRequest().withBatchPredictionId(mlEvalId))
    } catch {
      case _: Exception => NuoLogger.printInfo("Got an exception for delete prediction request and ignoring it")
    }

  }

  def getLatestPredictionIdWithPrefix(modelName: String,
                                      pageToken: String): String = {

    val request = new DescribeBatchPredictionsRequest().withPrefix(modelName)
    if (pageToken != null && pageToken.nonEmpty) request.withNextToken(pageToken)
    val result = NuoEvaEnglishListener.Client.amazonMachineLearningClient.describeBatchPredictions(request)

    val nextToken = result.getNextToken
    if (result.getResults.asScala.nonEmpty) {

      var oldPredictions = result.getResults.asScala
        .filter(_.getBatchPredictionId.startsWith(modelName))
        .filter(model => !List("DELETED").contains(model.getStatus.toUpperCase))
        .map(_.getBatchPredictionId)
      //        .tail
      if (nextToken != null && nextToken.nonEmpty) oldPredictions = oldPredictions :+ getLatestPredictionIdWithPrefix(modelName, nextToken)

      if (oldPredictions.nonEmpty) {
        oldPredictions.sortWith(_ > _).tail.foreach(deletePrediction)
        oldPredictions.head
      } else ""
    } else {
      ""
    }
  }


  def deleteDataSource(dataSourceId: String): Unit = {
    try {
      NuoEvaEnglishListener.Client.amazonMachineLearningClient.deleteDataSource(new DeleteDataSourceRequest().withDataSourceId(dataSourceId))
    } catch {
      case _: Exception => NuoLogger.printInfo("Got an exception for delete data source request and ignoring it")
    }

  }

  def deleteOldDataSourcesWithPrefix(dataSourceName: String,
                                     pageToken: String): Unit = {

    val request = new DescribeDataSourcesRequest().withPrefix(dataSourceName)
    if (pageToken != null && pageToken.nonEmpty) request.withNextToken(pageToken)
    val result = NuoEvaEnglishListener.Client.amazonMachineLearningClient.describeDataSources(request)

    val nextToken = result.getNextToken
    val oldDataSources = result.getResults.asScala
      .filter(_.getDataSourceId.startsWith(dataSourceName))
      .filter(model => !List("DELETED").contains(model.getStatus.toUpperCase))
      .map(_.getDataSourceId)
    if (nextToken != null && nextToken.nonEmpty) deleteOldDataSourcesWithPrefix(dataSourceName, nextToken)
    oldDataSources.sortWith(_ > _).foreach(deleteDataSource)
  }

  /**
    * Creates an Evaluation, which measures the quality of the ML Model
    * by seeing how many predictions it gets correct, when run on a
    * held-out sample (30%) of the original data.
    */
  private def createEvaluation(mlModelId: String,
                               evaluationId: String,
                               evaluationName: String,
                               testDataSourceId: String): Unit = {
    val request = new CreateEvaluationRequest()
      .withEvaluationDataSourceId(testDataSourceId)
      .withEvaluationId(evaluationId)
      .withEvaluationName(evaluationName)
      .withMLModelId(mlModelId)
    NuoEvaEnglishListener.Client.amazonMachineLearningClient.createEvaluation(request)
    println(s"Created Evaluation with id $evaluationId")
  }

  //  val trainDataSourceId = "trainDataSourceId"
  //  val testDataSourceId = "testDataSourceId"
  //  val mlModelName = "mlModelName"


  //  require(args.length == 3, "command-line arguments: mlModelid scoreThreshhold s3://url-where-output-should-go")
  //  val mlModelName = "mlModelName"

  //  val client = new AmazonMachineLearningClient

  /**
    * waits for the model to reach a terminal state.
    */
  private def waitForModel(mlModelId: String,
                           delay: FiniteDuration = 2.seconds): Unit = {
    var terminated = false
    while (!terminated) {
      val request = new GetMLModelRequest().withMLModelId(mlModelId)
      val model = NuoEvaEnglishListener.Client.amazonMachineLearningClient.getMLModel(request)
      println(s"Model $mlModelId is ${model.getStatus} at ${new Date()}")

      model.getStatus match {
        case "COMPLETED" | "FAILED" | "INVALID" => terminated = true
      }

      try if (!terminated) Thread.sleep(delay.toMillis)
      catch {
        case e: InterruptedException =>
          e.printStackTrace()
          terminated = true
      }
    }
  }

  def getDataSourceComputeTimeHour(dataSourceId: String): Double = {

    NuoEvaEnglishListener.Client.amazonMachineLearningClient.getDataSource(new GetDataSourceRequest().withDataSourceId(dataSourceId)).getComputeTime / 1000.0 / 3600.0
  }

  def getModelComputeTimeHour(mlModelId: String): Double = {

    NuoEvaEnglishListener.Client.amazonMachineLearningClient.getMLModel(new GetMLModelRequest().withMLModelId(mlModelId)).getComputeTime / 1000.0 / 3600.0
  }

  def getEvaluationComputeTimeHour(evaluationId: String): Double = {

    NuoEvaEnglishListener.Client.amazonMachineLearningClient.getEvaluation(new GetEvaluationRequest().withEvaluationId(evaluationId)).getComputeTime / 1000.0 / 3600.0
  }

  /**
    * Sets the score threshold on the ML Model.
    * This configures the ML Model by picking what raw prediction score
    * is the cut-off between a positive & a negative prediction.
    */
  private def setThreshold(mlModelId: String,
                           paramModelType: MLModelType,
                           threshold: Float): Unit = {

    val mlModelType = if (paramModelType == null)
      NuoEvaEnglishListener.Client.amazonMachineLearningClient.getMLModel(new GetMLModelRequest().withMLModelId(mlModelId)).getMLModelType
    else paramModelType

    if (mlModelType.equals(MLModelType.BINARY)) {


      val request = new UpdateMLModelRequest().withMLModelId(mlModelId).withScoreThreshold(threshold)
      NuoEvaEnglishListener.Client.amazonMachineLearningClient.updateMLModel(request)
    }
  }

  private def createBatchPrediction(mlModelName: String,
                                    mlModelId: String,
                                    batchPredictionDataSourceId: String,
                                    batchPredictionDataSourceName: String,
                                    batchPredictionId: String,
                                    batchPredictionName: String,
                                    dataBatchSchemaLocation: String,
                                    inputDataUrl: String,
                                    s3OutputUrl: String): Unit = {


    val dataSpec = new S3DataSpec()
      //      .withDataSchema(dataBatchSchemaLocation)
      .withDataSchemaLocationS3(dataBatchSchemaLocation)
      .withDataLocationS3(inputDataUrl)
    val dsRequest = new CreateDataSourceFromS3Request()
      .withDataSourceId(batchPredictionDataSourceId)
      .withDataSourceName(batchPredictionDataSourceName)
      .withDataSpec(dataSpec)
      .withComputeStatistics(false)
    NuoEvaEnglishListener.Client.amazonMachineLearningClient.createDataSourceFromS3(dsRequest)
    println(s"Created DataSource for batch prediction with id $batchPredictionDataSourceName")


    val batchPredictionRequest = new CreateBatchPredictionRequest()
      .withBatchPredictionId(batchPredictionId)
      .withBatchPredictionName(batchPredictionName)
      .withMLModelId(mlModelId)
      .withOutputUri(s3OutputUrl)
      .withBatchPredictionDataSourceId(batchPredictionDataSourceName)
    NuoEvaEnglishListener.Client.amazonMachineLearningClient.createBatchPrediction(batchPredictionRequest)
    println(s"Created BatchPrediction with id $batchPredictionId")
  }

  def isMLModelComplete(latestStatus: Double, refParams: List[String]): Boolean = {

    if (refParams.length >= 4) {

      val modelId = refParams(0)
      val minWaitTime = refParams(1)
      val maxWaitTime = refParams(2)
      val refreshInterval = refParams(3)

      if (System.currentTimeMillis() > minWaitTime.toLong) {

        if (System.currentTimeMillis() > maxWaitTime.toLong) {

          NuoEvaEnglishListener.nuoTenantDetails.errorEncountered = true
          NuoRequestHandler.reportErrorToUser(new Exception(s"Timeout occured while executing model $modelId because it has been running for ${maxWaitTime.toDouble / 1000.0 / 60.0 / 60.0} minutes."))
          throw new Exception("Unreachable Code Statement")
          true
        } else {
          val statusNameList = List("COMPLETED", "FAILED", "INVALID")
          if (latestStatus == NuoRecognitionMetadata.NuoExecutionStatusCode.AML_MODEL_TRAINING_STARTED) {


            val evalRequest = new GetEvaluationRequest().withEvaluationId(modelId)
            val evaluation = NuoEvaEnglishListener.Client.amazonMachineLearningClient.getEvaluation(evalRequest)

            val modelRequest = new GetMLModelRequest().withMLModelId(evaluation.getMLModelId)
            val model = NuoEvaEnglishListener.Client.amazonMachineLearningClient.getMLModel(modelRequest)

            if (statusNameList.contains(model.getStatus) && statusNameList.contains(evaluation.getStatus)) {

              var computeHours = getModelComputeTimeHour(model.getMLModelId)
              computeHours += getDataSourceComputeTimeHour(model.getTrainingDataSourceId)

              computeHours += getEvaluationComputeTimeHour(evaluation.getEvaluationId)
              computeHours += getDataSourceComputeTimeHour(evaluation.getEvaluationDataSourceId)
              NuoLogger.logUsage(NuoModifier.ActionType.External.MLaaS.TrainModel, computeHours)
              true
            } else false
          } else {
            val request = new GetBatchPredictionRequest().withBatchPredictionId(modelId)
            val prediction = NuoEvaEnglishListener.Client.amazonMachineLearningClient.getBatchPrediction(request)

            if (statusNameList.contains(prediction.getStatus)) {

              //        val computeHours =  getDataSourceComputeTimeHour(prediction.getBatchPredictionDataSourceId)
              //        NuoLogger.logUsage(NuoBiller.ServiceName.ModelPrediction, computeHours)
              true
            } else false
          }
        }
      } else false
    } else false
  }

  def getMLPerformanceMetrics(modelId: String): List[List[String]] = {

    val evalRequest = new GetEvaluationRequest().withEvaluationId(modelId)
    val evaluation = NuoEvaEnglishListener.Client.amazonMachineLearningClient.getEvaluation(evalRequest)
    evaluation.getPerformanceMetrics.getProperties.asScala
      .map(pair =>
        List(pair._1, pair._2).map(
          _.replace("RegressionRMSE", "Regression Root Mean Squared Error (Lower the better)")
            .replace("MulticlassAvgFScore", "Multiclass Average F1 Score (Best Performance at 1)")
            .replace("BinaryAUC", "Area Under the Curve (Best performance at 1)")
        )
      ).toList
  }
}
