package action

import canvas.NuoDataTypeHandler
import client.NuoBqClient
import client.NuoBqClient.isBQJobComplete
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.bigquery.model.{Table, TableReference, ViewDefinition}
import execution.NuoRequestHandler
import logging.NuoLogger
import nlp.grammar.NuoEvaEnglishListener

import scala.collection.mutable

/**
  * Copyright 2015 NuoCanvas Inc.
  *
  *
  * Created by Pulkit on 29SEP2015.
  *
  * Content of this file is proprietry and confidential.
  * It should not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoDML {

  object ProfilingMetric {
    val ColumnName = "Column_Name"
    val ColumnType = "Column_Type"
    val TotalCount = "Total_Count"
    val UniqueCount = "Unique_Count"
    val EmptyCount = "Empty_Count"
    val Min = "Min"
    val Max = "Max"
    val Average = "Average"
    val Median = "Median"
    val StandardDeviation = "Standard_Deviation"
    val Pattern = "Pattern"
  }

  /*
  * This function profiles the results from input action.
  * */
  //  def getSqlQuestionResults(nuoUserMessage: NuoUserMessage,
  //                            nuoEvaEnglishListener: NuoEvaEnglishListener): String = {
  //
  //    val userResponse = NuoEvaEnglishListener.nuoUserMessage
  //
  //
  //    var isInternalResultTable = true
  //    var resultTableName =
  //
  //      if (nuoEvaEnglishListener
  //        .currAnalysisRecognitionData
  //        .ResultTableName.isDefined) {
  //        isInternalResultTable = false
  //        nuoEvaEnglishListener
  //          .currAnalysisRecognitionData
  //          .ResultTableName.get
  //      } else {
  //        nuoEvaEnglishListener
  //          .currAnalysisRecognitionData
  //          .QuestionAlias
  //      }
  //
  //    var profilingTableName = resultTableName + "_profiling_result"
  //
  //    var latestStatus = nuoEvaEnglishListener.currAnalysisRecognitionData.NuoExecutionStatuses.maxBy(_.Status)
  //
  //    var resultMessage = ""
  //
  //    if (latestStatus.Status == NuoExecutionStatusCode.RECOGNITION_COMPLETE) {
  //
  //      if (userResponse.CommunicationType.get %
  //        NuoRecognitionMetadata
  //          .RecognitionCommunicationType
  //          .COMMUNICATION_TYPE_GET_RESULT == 0) {
  //
  //        if (userResponse.ResultTableName.isDefined && userResponse.ResultTableName.get.trim.nonEmpty) {
  //          resultTableName = userResponse.ResultTableName.get.trim
  //          isInternalResultTable = false
  //          nuoEvaEnglishListener
  //            .currAnalysisRecognitionData
  //            .ResultTableName = Some(resultTableName)
  //        }
  //
  //        val bqJobId = NuoBqClient.executeDMLAndSaveResult(
  //          datasetName =
  //            if (isInternalResultTable) NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId
  //            else NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
  //          tableName = resultTableName,
  //          writeIfEmpty = false,
  //          append = false,
  //          querySql = nuoEvaEnglishListener.currAnalysisRecognitionData.Sql,
  //          saveAsView = true)
  //
  //        if (!bqJobId.equals("-1")) {
  //
  //          nuoEvaEnglishListener
  //            .currAnalysisRecognitionData
  //            .NuoExecutionStatuses +:=
  //            NuoRecognitionMetadata.NuoExecutionStatus(
  //              Status = NuoRecognitionMetadata.NuoExecutionStatusCode.BQ_JOB_STARTED,
  //              Description = None,
  //              RefParams = List(bqJobId))
  //
  //          NuoRecognitionMetadata.writeCurrQuestionRecognitionData(nuoEvaEnglishListener)
  //
  //          val timeNow = System.currentTimeMillis()
  //          sendExecutionRunningMessage(
  //            nuoFileLoadOptions = None,
  //            nuoAnalyzeImageOptions = None,
  //            nuoPollingDetails = Some(
  //              NuoPollingDetails(
  //                StartTimeMillis = timeNow + 5000,
  //                PollingIntervalMillis = 2000,
  //                EndTimeMillis = timeNow + (30 * 60 * 1000)
  //              )
  //            ),
  //            nuoEvaEnglishListener = nuoEvaEnglishListener
  //          )
  //        } else {
  //          NuoRequestHandler.reportErrorToUser(new Exception(s"I encountered an error while starting your query job."))
  //          throw new Exception("Unreachable Code statement")
  //        }
  //      } else {
  //        NuoRecognitionMetadata.writeCurrQuestionRecognitionData(nuoEvaEnglishListener)
  //
  //        val timeNow = System.currentTimeMillis()
  //        sendExecutionStartedMessage(Some(
  //          NuoPollingDetails(
  //            StartTimeMillis = timeNow + 5000,
  //            PollingIntervalMillis = 2000,
  //            EndTimeMillis = timeNow + (30 * 60 * 1000)
  //          )
  //        ),
  //          nuoEvaEnglishListener, calculateCost = true)
  //      }
  //
  //    } else if (latestStatus.Status == NuoExecutionStatusCode.BQ_JOB_COMPLETE
  //      || latestStatus.Status == NuoExecutionStatusCode.BQ_JOB_STARTED) {
  //
  //      if (latestStatus.Status == NuoExecutionStatusCode.BQ_JOB_STARTED
  //        && NuoBqClient.isBQJobComplete(latestStatus.RefParams.head)) {
  //
  //        latestStatus = NuoRecognitionMetadata.NuoExecutionStatus(
  //          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.BQ_JOB_COMPLETE,
  //          Description = None,
  //          RefParams = latestStatus.RefParams)
  //
  //
  //        nuoEvaEnglishListener
  //          .currAnalysisRecognitionData
  //          .NuoExecutionStatuses +:= latestStatus
  //        NuoRequestHandler.incrementStepsCompleted(nuoEvaEnglishListener)
  //      }
  //
  //
  //      if (latestStatus.Status == NuoExecutionStatusCode.BQ_JOB_COMPLETE) {
  //
  //        var jobId = profileResults(
  //          if (isInternalResultTable)
  //            NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId
  //          else
  //            NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
  //          resultTableName,
  //          profilingTableName,
  //          nuoEvaEnglishListener)
  //
  //        nuoEvaEnglishListener
  //          .currAnalysisRecognitionData
  //          .NuoExecutionStatuses +:=
  //          NuoRecognitionMetadata.NuoExecutionStatus(
  //            Status = NuoRecognitionMetadata.NuoExecutionStatusCode.PROFILING_JOB_STARTED,
  //            Description = None,
  //            RefParams = List(jobId))
  //      }
  //
  //    } else if (latestStatus.Status == NuoExecutionStatusCode.PROFILING_JOB_COMPLETE
  //      || latestStatus.Status == NuoExecutionStatusCode.PROFILING_JOB_STARTED) {
  //
  //      if (latestStatus.Status == NuoExecutionStatusCode.PROFILING_JOB_STARTED
  //        && NuoBqClient.isBQJobComplete(latestStatus.RefParams.head)) {
  //
  //        latestStatus =
  //          NuoRecognitionMetadata
  //            .NuoExecutionStatus(
  //              Status = NuoRecognitionMetadata.NuoExecutionStatusCode.PROFILING_JOB_COMPLETE,
  //              Description = None,
  //              RefParams = latestStatus.RefParams)
  //
  //        nuoEvaEnglishListener
  //          .currAnalysisRecognitionData
  //          .NuoExecutionStatuses +:= latestStatus
  //
  //        nuoEvaEnglishListener
  //          .currAnalysisRecognitionData
  //          .IsExecutionComplete = true
  //        NuoRequestHandler.incrementStepsCompleted(nuoEvaEnglishListener)
  //
  //        NuoRecognitionMetadata.writeCurrQuestionRecognitionData(nuoEvaEnglishListener)
  //      }
  //
  //      if (latestStatus.Status == NuoExecutionStatusCode.PROFILING_JOB_COMPLETE) {
  //
  //        NuoBqClient.writeStorageMetadataToFile()
  //
  //        resultMessage = getMessageResponseRef(statusCode = 200,
  //          status = "OK",
  //          messageRef =
  //            NuoQueryResponse(
  //              NuoEvaMessage = NuoEvaMessage(nuoUserMessage.QuestionAlias,
  //                nuoUserMessage.QuestionText,
  //                AnalysisId = "",
  //                RuleText = "",
  //                CommunicationType = NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_RESULT_AVAILABLE,
  //                Message = "",
  //                LeftEntityName = "",
  //                RightEntityName = "",
  //                NuoEntities = List(),
  //                NuoUserChoices = List(),
  //                NuoMappingInput = None,
  //                NuoFileLoadOptions = None,
  //                NuoAnalyzeImageOptions = None,
  //                NuoPollingDetails = None),
  //              NuoDataGrid =
  //                Some(NuoDataGrid(
  //                  Metadata = NuoBqClient
  //                    .getTableSchema(
  //                      if (isInternalResultTable)
  //                        NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId
  //                      else
  //                        NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
  //                      resultTableName)
  //                    .map(pair => NuoField("", "", pair._1, pair._2))
  //                    .toList,
  //                  Data = NuoBqClient
  //                    .listTableData(
  //                      datasetName = if (isInternalResultTable)
  //                        NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId
  //                      else
  //                        NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
  //                      tableName = resultTableName,
  //                      pageToken = null,
  //                      pageSize = 100L)
  //                    ._2
  //                    .map(_.toList).toList)),
  //              ProfilingResult =
  //                Some(NuoDataGrid(
  //                  Metadata = NuoBqClient
  //                    .getTableSchema(if (isInternalResultTable)
  //                      NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId
  //                    else
  //                      NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
  //                      profilingTableName)
  //                    .map(pair => NuoField("", "", pair._1, pair._2))
  //                    .toList,
  //                  Data = NuoBqClient
  //                    .listTableData(
  //                      datasetName =
  //                        if (isInternalResultTable)
  //                          NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId
  //                        else
  //                          NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
  //                      tableName = profilingTableName,
  //                      pageToken = null,
  //                      pageSize = 100L)
  //                    ._2
  //                    .map(_.toList).toList))
  //            )
  //        )
  //      }
  //
  //    }
  //    if (resultMessage != null && resultMessage.nonEmpty)
  //      resultMessage
  //    else {
  //
  //      val timeNow = System.currentTimeMillis()
  //      sendExecutionRunningMessage(
  //        nuoFileLoadOptions = None,
  //        nuoAnalyzeImageOptions = None,
  //        nuoPollingDetails = Some(
  //          NuoPollingDetails(
  //            StartTimeMillis = timeNow + 5000,
  //            PollingIntervalMillis = 2000,
  //            EndTimeMillis = timeNow + (30 * 60 * 1000)
  //          )
  //        ),
  //        nuoEvaEnglishListener = nuoEvaEnglishListener
  //      )
  //    }
  //
  //  }

  def profileResults(resultDatasetName: String,
                     resultTableName: String,
                     profilingTableName: String,
                     patternTableName: String,
                     nuoEvaEnglishListener: NuoEvaEnglishListener): String = {

    val schema = NuoBqClient.getTableSchema(resultDatasetName, resultTableName)

    val profilingExpressionList = schema
      .map { colNameAndType =>
        val colName = colNameAndType._1
        val fullyQualifiedColName = s"`$resultTableName`.`$colName`"
        val colType = colNameAndType._2

        val queryExpressions = mutable.ArrayBuffer[String](
          s"'$colName'",
          s"'$colType'",
          s"COUNT($fullyQualifiedColName)",
          s"COUNT(DISTINCT $fullyQualifiedColName)")

        colType.toUpperCase match {
          case x if List(NuoDataTypeHandler.NuoDataType.Integer,
            NuoDataTypeHandler.NuoDataType.Int64,
             NuoDataTypeHandler.NuoDataType.Float,
             NuoDataTypeHandler.NuoDataType.Float64).map(_.toUpperCase).contains(x.toUpperCase) =>

            queryExpressions += s"COUNT(*) - COUNT($fullyQualifiedColName)"
            queryExpressions += s"MIN($fullyQualifiedColName)"
            queryExpressions += s"MAX($fullyQualifiedColName)"
            queryExpressions += s"AVG($fullyQualifiedColName)"
            queryExpressions += s"APPROX_QUANTILES($fullyQualifiedColName,1000)[OFFSET(500)]"
            queryExpressions += s"STDDEV_POP($fullyQualifiedColName)"

          case x if x.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.String) =>
            queryExpressions += s"COUNT(*) - COUNT($fullyQualifiedColName) + COUNTIF(LENGTH($fullyQualifiedColName) <= 0)"
            queryExpressions += s"MIN(LENGTH($fullyQualifiedColName))"
            queryExpressions += s"MAX(LENGTH($fullyQualifiedColName))"
            queryExpressions += s"AVG(LENGTH($fullyQualifiedColName))"
            queryExpressions += s"APPROX_QUANTILES(LENGTH($fullyQualifiedColName),1000)[OFFSET(500)]"
            queryExpressions += "NULL"

          case _ =>
            queryExpressions += s"COUNT(*) - COUNT($fullyQualifiedColName)"
            queryExpressions += s"NULL"
            queryExpressions += s"NULL"
            queryExpressions += s"NULL"
            queryExpressions += s"NULL"
            queryExpressions += "NULL"
        }
        queryExpressions.toList
      }

    val profilingQuery = (List(profilingExpressionList.head.zip(List(
      ProfilingMetric.ColumnName,
      ProfilingMetric.ColumnType,
      ProfilingMetric.TotalCount,
      ProfilingMetric.UniqueCount,
      ProfilingMetric.EmptyCount,
      ProfilingMetric.Min,
      ProfilingMetric.Max,
      ProfilingMetric.Average,
      ProfilingMetric.Median,
      ProfilingMetric.StandardDeviation))
      .map(expAndName => expAndName._1 + " " + expAndName._2)) ++ profilingExpressionList.tail.toList)
      .map(expList =>
        expList.mkString(start = "SELECT ",
          sep = ",",
          end = s" FROM ${NuoBqClient.getFullyQualifiedTableName(resultDatasetName, resultTableName)}"))
      .mkString(" UNION ALL ")

    val patternExpressionList =
      schema
        .filter(_._2.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.String))
        .map { colNameAndType =>
          val colName = colNameAndType._1
          val fullyQualifiedColName = s"`$resultTableName`.`$colName`"
          val colType = colNameAndType._2

          mutable.ArrayBuffer[String](
            s"'$colName'",
            s"'$colType'",
            s"REGEXP_REPLACE(" +
              s"REGEXP_REPLACE(" +
              s"REGEXP_REPLACE(" +
              s"REGEXP_REPLACE(" +
              s"REGEXP_REPLACE($fullyQualifiedColName,'[A-Z]+','A')" +
              s",'[a-z]+','a')" +
              s",'\\\\d+','9')" +
              s",'\\\\s+',' ')" +
              s",'_+','_')",
            s"COUNT(*)"
          )
        }

    val patternQuery = (List(patternExpressionList.head.zip(List(
      ProfilingMetric.ColumnName,
      ProfilingMetric.ColumnType,
      ProfilingMetric.Pattern,
      ProfilingMetric.TotalCount))
      .map(expAndName => expAndName._1 + " " + expAndName._2)) ++ patternExpressionList.tail.toList)
      .map(expList =>
        expList.mkString(start = "SELECT ",
          sep = ",",
          end = s" FROM ${NuoBqClient.getFullyQualifiedTableName(resultDatasetName, resultTableName)} GROUP BY 1,2,3"))
      .mkString(" UNION ALL ")


    val profilingJobId = NuoBqClient.startQueryJob(
      datasetName = NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
      querySql = profilingQuery,
      allowLargeResults = true,
      writeDisposition = "WRITE_TRUNCATE",
      destinationTable = profilingTableName,
      saveAsView = false)


    val patternJobId = NuoBqClient.startQueryJob(
      datasetName = NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
      querySql = patternQuery,
      allowLargeResults = true,
      writeDisposition = "WRITE_TRUNCATE",
      destinationTable = patternTableName,
      saveAsView = false)

    while (!isBQJobComplete(profilingJobId) || !isBQJobComplete(patternJobId)) {
      Thread.sleep(1000)
    }
    ""
  }

  def isInternalTable(targetDatasetName: String,
                      targetTableName: String): Boolean = {
    if (
      List(
        NuoEvaEnglishListener.nuoTenantDetails.userCacheDatasetNameWOTenantId,
        NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
        NuoEvaEnglishListener.nuoTenantDetails.userMetadataDatasetNameWOTenantId)
        .contains(targetDatasetName.substring(targetDatasetName.indexOf("_") + 1)) &&
        (targetTableName.equalsIgnoreCase(NuoEvaEnglishListener.nuoTenantDetails.loggingTable)
          || targetTableName.equalsIgnoreCase(NuoEvaEnglishListener.nuoTenantDetails.functionStatusTable)
          || targetTableName.equalsIgnoreCase(NuoEvaEnglishListener.nuoTenantDetails.usageTable))) {

      NuoLogger.printInfo(s"$targetTableName is an internal table and I cannot allow to modify it.")
      true
    } else false
  }

  def createView(datasetName: String,
                 viewName: String,
                 selectQuery: String): Unit = {

    val querySql = selectQuery

    if (!isInternalTable(datasetName, viewName)) {
      if (doesTableExist(datasetName, viewName)) {
        deleteTable(datasetName, viewName)
      }
      val projectId = NuoEvaEnglishListener.nuoTenantDetails.bqProjectId

      val content = new Table()
      val tableReference = new TableReference()
      tableReference.setTableId(viewName)
      tableReference.setDatasetId(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName)
      tableReference.setProjectId(projectId)
      content.setTableReference(tableReference)

      val view = new ViewDefinition()
      view.setQuery(querySql).setUseLegacySql(false)
      content.setView(view)
      NuoEvaEnglishListener.nuoTenantDetails.bigqueryClient.tables().insert(projectId, NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName, content).execute()
    }
  }

  def doesTableExist(datasetName: String,
                     tableName: String): Boolean = {

    if (doesDatasetExist(datasetName)) {

      try {
        NuoEvaEnglishListener.nuoTenantDetails.bigqueryClient.tables().get(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId, NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName, tableName).execute()
      } catch {
        case e: GoogleJsonResponseException =>
          if (e.getDetails.getCode == 404 || e.getDetails.getCode == 400) {
            return false
          }
      }
      true
    } else
      false
  }

  def doesDatasetExist(datasetName: String): Boolean = {

    try {
      NuoEvaEnglishListener.nuoTenantDetails.bigqueryClient.datasets().get(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId, NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName).execute()
    } catch {
      case e: GoogleJsonResponseException =>
        if (e.getDetails.getCode == 404 || e.getDetails.getCode == 400) {
          return false
        }
    }
    true
  }

  def deleteTable(userDatasetName: String,
                  userTableName: String): Unit = {


    val datasetName = if (userDatasetName.isEmpty) {
      NuoLogger.printInfo(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.")
      NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId
    } else userDatasetName

    if (!isInternalTable(datasetName, userTableName)) {
      if (doesTableExist(datasetName, userTableName)) {
        NuoLogger.printInfo(s"Deleting table $userTableName.")

        try {
          NuoEvaEnglishListener.nuoTenantDetails.bigqueryClient.tables().delete(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId, NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName, userTableName).execute()
        } catch {
          case e: Exception =>
            e.printStackTrace()
            NuoRequestHandler.reportErrorToUser(new Exception(s"I encountered an error while deleting the table $userTableName."))
            throw new Exception("Unreachable Code statement")
        }
        NuoLogger.printInfo(s"Successfully deleted table $userTableName.")
      } else {
        NuoLogger.printInfo(s"Table $userTableName does not exist in database $datasetName.")
      }
    }
  }
}

/*

import action.NuoExpressionAndVariable._
import canvas.NuoModifier
import canvas.NuoModifier.ActionAttrName._
import canvas.NuoModifier.ActionType
import client.NuoBqClient._
import dag.NuoActionPatternMatcher
import dag.NuoDag.NuoDagDetails
import dag.NuoDagProcessor._
import logging.NuoLogger
import logging.NuoLogger._
import metadata.VariableMetadata.{NuoVariable, NuoVariables}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Copyright 2015 NuoCanvas Inc.
  *
  *
  * Created by Pulkit on 29SEP2015.
  *
  * Content of this file is proprietry and confidential.
  * It should not be reused or disclosed without prior consent
  * of distributor
  **/
object NuoDML {

  /*
  * This function returns the list of inbound node that are not compute component
  * */

  def validateComputeChain(nodeId: String, sourceList: mutable.ArrayBuffer[String], nuoDagDetails: NuoDagDetails): Unit = {


    val nodeName = getNodeName(nodeId, nuoDagDetails)
    val nodeType = nuoDagDetails.nodes(nodeId)(NodeType).asInstanceOf[String]

    if (!nodeType.equalsIgnoreCase(ActionType.Dml.Compute)) {
      if (!sourceList.contains(nodeName)) {
        sourceList += nodeName
      }
    } else {
      /*
      * Recursively validate the compute chain for all inbound components
      * */
      for (inboundNodeId <- NuoActionPatternMatcher.getSourceTablesFor(nodeId, nuoDagDetails).map(nuoDagDetails.nodeNameToIdMap(_))) {
        validateComputeChain(inboundNodeId, sourceList, nuoDagDetails)
      }
    }
  }


  def getGenericSelectListForSource(nodeId: String,
                                    columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): mutable.StringBuilder = {

    new StringBuilder(
      columns.map { column =>

        val colSource = column(ColumnSource)
        val FieldName = column(ColumnName)

        val source = processExpression(expressionInput = colSource,
          includeSourceName = false,
          nodeId = nodeId, nuoDagDetails)._1

        s"$source AS `$FieldName`"
      }.toList.mkString(","))
  }

  def getGenericSelectClauseForSource(nodeId: String,
                                      columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): mutable.StringBuilder = {

    new StringBuilder(" SELECT ").append(
      columns.map { column =>

        val colSource = column(ColumnSource)
        val FieldName = column(ColumnName)

        val source = processExpression(expressionInput = colSource,
          includeSourceName = false,
          nodeId = nodeId,
          nuoDagDetails)._1

        if (source.trim.nonEmpty) s"$source AS `$FieldName`"
        else s"'$source' AS `$FieldName`"
      }.toList.mkString(","))
  }

  def getGenericSelectListForTarget(columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): mutable.StringBuilder = {

    val query: mutable.StringBuilder = new mutable.StringBuilder("")
    var firstElementFlag = true

    for (column <- columns) {
      val FieldName = column(ColumnName)
      if (!firstElementFlag) {
        query.append(", ")
      }
      firstElementFlag = false
      query.append(s"`$FieldName`")
    }
    query
  }

  def getGenericSelectClauseForTarget(columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): mutable.StringBuilder = {

    val query: mutable.StringBuilder = new mutable.StringBuilder(" SELECT ")
    var firstElementFlag = true

    for (column <- columns) {
      val FieldName = column(ColumnName)
      if (!firstElementFlag) {
        query.append(", ")
      }
      firstElementFlag = false
      query.append(s"`$FieldName`")
    }
    query
  }

  def getGenericSelectClauseForMerge(sourceName: String,
                                     targetName: String,
                                     columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): mutable.StringBuilder = {

    val sourceList: mutable.StringBuilder = new mutable.StringBuilder("")
    val targetList: mutable.StringBuilder = new mutable.StringBuilder("")

    var firstElementFlag = true

    for (column <- columns) {
      val FieldName = column(ColumnName)
      if (firstElementFlag) {
        firstElementFlag = false
      } else {
        sourceList.append(", ")
        targetList.append(", ")
      }
      sourceList.append(" `" + sourceName + "`").append(".").append(FieldName)
      targetList.append(" `" + targetName + "`").append(".").append(FieldName)
    }
    new mutable.StringBuilder(" SELECT ").append(sourceList).append(targetList)
  }


  /*
  * This function constructs the select clause for compute component.
  * It recursively backtrack expression for all column names until input node is not compute node
  * */
  def getSelectClauseAndSourceNameForCompute(actionName: String,
                                             nodeId: String,
                                             columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): (mutable.StringBuilder, Option[String], Boolean) = {

    val query: mutable.StringBuilder = new mutable.StringBuilder(" SELECT ")
    var firstElementFlag = true
    val sourceList = new ArrayBuffer[String]()
    validateComputeChain(nodeId, sourceList, nuoDagDetails)

    if (sourceList.length > 1) {
      throw new Exception(actionName + " contains more than one data source as input. How can I know the relationships between them?!:" + sourceList)
    }

    var isSourceTableUsed = false
    for (column <- columns) {

      /*
      * If the inbound component is compute
      * then evaluate the expression for each column until the inbound component is found such that is not compute
      * */


      val source = column(ColumnSource)
      val FieldName = column(ColumnName)

      val expResult = processExpression(expressionInput = source,
        includeSourceName = true,
        nodeId = nodeId, nuoDagDetails)

      val sourceExpression = expResult._1
      if (!isSourceTableUsed) {
        isSourceTableUsed = expResult._2
      }
      if (firstElementFlag) {
        firstElementFlag = false
      } else {
        query.append(", ")
      }
      query.append(" ").append(s" $sourceExpression AS `$FieldName`")
    }
    if (sourceList.isEmpty) {
      (query, None, isSourceTableUsed)
    } else {
      (query, Some(sourceList.head), isSourceTableUsed)
    }
  }

  /*
  * This function adds the query to WITH Clause.
  * */
  def registerActionQuery(query: String,
                          actionName: String, nuoDagDetails: NuoDagDetails): Unit = {
    val nodeId = nuoDagDetails.nodeNameToIdMap.getOrElse(actionName, actionName)
    if (nuoDagDetails.runDetails.isSampleExecution) {

      nuoDagDetails.subQueryForNodeId.put(nodeId, query + s" LIMIT ${nuoDagDetails.runDetails.sampleSize}")
    } else {
      nuoDagDetails.subQueryForNodeId.put(nodeId, query)
    }
    dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "SQL", 1.0, "NA", nuoDagDetails)

  }

  /*
  * This function adds the query to WITH Clause.
  * */
  def registerActionQueryWOStatus(query: String,
                                  actionName: String, nuoDagDetails: NuoDagDetails): Unit = {
    val nodeId = nuoDagDetails.nodeNameToIdMap.getOrElse(actionName, actionName)
    if (nuoDagDetails.runDetails.isSampleExecution) {

      nuoDagDetails.subQueryForNodeId.put(nodeId, query + s" LIMIT ${nuoDagDetails.runDetails.sampleSize}")
    } else {
      nuoDagDetails.subQueryForNodeId.put(nodeId, query)
    }
  }


  /*
  * This function performs the join operation on the inputs and register the resultant data set as temporary table
  * */
  def evalJoin(nodeId: String,
               joinerName: String,
               joinType: String,
               joinCondition: String,
               joinColumns: List[String],
               joinLeftSource: String,
               joinRightSource: String,
               columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): Unit = {

    val query = new StringBuilder("SELECT ").append(columns.map { column =>

      val colSource = column(ColumnSource)
      val FieldName = column(ColumnName)

      val source = processExpression(expressionInput = colSource,
        includeSourceName = true,
        nodeId = nodeId, nuoDagDetails)._1

      s"$source AS `$FieldName`"
    }.toList.mkString(","))

    if (joinLeftSource.isEmpty) NuoLogger.logError(s"The left input of the join action = ${getNodeName(nodeId, nuoDagDetails)} is empty.", nuoDagDetails)
    else if (joinRightSource.isEmpty) NuoLogger.logError(s"The right input of the join action = ${getNodeName(nodeId, nuoDagDetails)} is empty.", nuoDagDetails)

    query.append(s" FROM $joinLeftSource $joinType $joinRightSource")

    if (joinColumns.nonEmpty) {
      query.append(s" USING (" + joinColumns.mkString(",") + ")")
    } else if (joinCondition.nonEmpty) {
      query.append(s" ON $joinCondition")
    } else {
      NuoLogger.logError(s"The properties Join Condition and Join Columns  are empty of the join action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I need atleast one of them.", nuoDagDetails)
      //      printException(throw new Exception("Join condition and Join columns are empty. At least one of them is mandatory!"))
    }
    registerActionQuery(query.toString(), joinerName, nuoDagDetails)
  }

  def evalFilter(filterName: String,
                 filterCondition: String,
                 sourceTable: String,
                 nodeId: String,
                 columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): Unit = {

    val selectClause = getGenericSelectClauseForSource(nodeId, columns, nuoDagDetails)

    val query = selectClause
    if (sourceTable.nonEmpty) {
      query.append(" FROM `" + sourceTable + "`")
    } else {
      //      query.append(" FROM `" + _dummyTable + "` ")
      NuoLogger.logError(s"The filter action = ${getNodeName(nodeId, nuoDagDetails)} does not have an input action. I cannot execute it.", nuoDagDetails)
      printInfo(s"Action: [${getNodeName(nodeId, nuoDagDetails)}] does not have source table defined!", nuoDagDetails)
    }
    query.append(s" WHERE $filterCondition")

    registerActionQuery(query.toString(), filterName, nuoDagDetails)
  }

  def evalRemoveDuplicates(actionName: String,
                           sourceTable: String,
                           nodeId: String,
                           columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): Unit = {

    val selectClause = getGenericSelectListForSource(nodeId, columns, nuoDagDetails)

    val query = new StringBuilder("SELECT DISTINCT ")
    query.append(selectClause)
    if (sourceTable.nonEmpty) {
      query.append(" FROM `" + sourceTable + "` ")
    } else {
      printInfo(s"Action: [${getNodeName(nodeId, nuoDagDetails)}] does not have source table defined!", nuoDagDetails)
      NuoLogger.logError(s"The action = ${getNodeName(nodeId, nuoDagDetails)} does not have an input action. I cannot execute it.", nuoDagDetails)


    }
    registerActionQuery(query.toString(), actionName, nuoDagDetails)
  }

  def evalSort(actionName: String,
               sortByColumnList: Vector[String],
               sourceTable: String,
               nodeId: String,
               columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): Unit = {

    val selectClause = getGenericSelectClauseForSource(nodeId, columns, nuoDagDetails)

    val query = selectClause
    //      val sourceTable = selectClause._2
    if (sourceTable.nonEmpty) {
      query.append(" FROM `" + sourceTable + "`")
    } else {
      printInfo(s"Action: [${getNodeName(nodeId, nuoDagDetails)}] does not have source table defined!", nuoDagDetails)
      NuoLogger.logError(s"The filter action = ${getNodeName(nodeId, nuoDagDetails)} does not have an input action. I cannot execute it.", nuoDagDetails)

    }
    query.append(s" ORDER BY " + sortByColumnList.map(ele => ele).mkString(", "))
    if (sortByColumnList.isEmpty)
      NuoLogger.logError(s"The Sort By property of the sort action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I cannot execute it.", nuoDagDetails)

    registerActionQuery(query.toString(), actionName, nuoDagDetails)

  }


  def evalAggregate(actionName: String,
                    groupByColumnList: Vector[String],
                    sourceTable: String,
                    nodeId: String,
                    columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): Unit = {

    val selectClause = getGenericSelectClauseForSource(nodeId, columns, nuoDagDetails)

    val query = selectClause
    if (sourceTable.nonEmpty) {
      query.append(" FROM `" + sourceTable + "`")
    } else {
      printInfo(s"Action: [${getNodeName(nodeId, nuoDagDetails)}] does not have source table defined!", nuoDagDetails)
      NuoLogger.logError(s"The filter action = ${getNodeName(nodeId, nuoDagDetails)} does not have an input action. I cannot execute it.", nuoDagDetails)

    }
    query.append(s" GROUP BY " + groupByColumnList.map(ele => ele).mkString(", "))
    if (groupByColumnList.isEmpty)
      NuoLogger.logError(s"The Group By property of the aggregate action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I cannot execute it.", nuoDagDetails)

    //    println(s"Aggregate query==> ${query.toString()}")
    registerActionQuery(query.toString(), actionName, nuoDagDetails)
  }

  /*
  * This function register the table for compute component
  * */
  def evalCompute(actionName: String,
                  nodeId: String,
                  columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): Unit = {

    val selectClause = getSelectClauseAndSourceNameForCompute(actionName, nodeId, columns, nuoDagDetails)

    val query = selectClause._1

    if (selectClause._2.isDefined && selectClause._3) {
      val sourceTable = selectClause._2.get
      query.append(" FROM `" + sourceTable + "` ")
    }
    /* else {
          printInfo(s"Action: [${getNodeName(nodeId,nuoDagDetails)}] does not have source table defined!")

        }*/
    registerActionQuery(query.toString(), actionName, nuoDagDetails)
  }


  /*
  * This function performs the set operations (union/intersect)on the input and register the result data set
  * This component does not have column Mapping, because it takes two or more identical table structure as input and union all input data sets.
  * Output data structure is same as any of the input data structure.
  * Furthermore, union component does not remove duplicates from the result.
  * */
  def evalSetOperation(nodeId: String,
                       actionName: String,
                       actionType: String,
                       unionAll: Boolean,
                       inputOrder: ArrayBuffer[String], nuoDagDetails: NuoDagDetails): Unit = {

    printInfo(s"inputOrder = " + inputOrder.mkString(","), nuoDagDetails)
    if (inputOrder.isEmpty)
      NuoLogger.logError(s"The Input Order property of the set operator action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I cannot execute it.", nuoDagDetails)

    val firstNodeId = inputOrder.head
    val firstColumnMap = nuoDagDetails.nodes(firstNodeId)(ColumnMapping)
      .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]]
      .sortBy(colMap => colMap(ColumnName))

    val firstCompString = firstColumnMap
      .map { colMap =>
        (colMap(ColumnName)
          + colMap(NuoModifier.ActionAttrName.DataType)
          + colMap(Scale)
          + colMap(Precision))
      }

    val actionQuery = new mutable.StringBuilder(" SELECT DISTINCT ").append(getGenericSelectListForTarget(firstColumnMap, nuoDagDetails))
    val whereClause = new StringBuilder(" WHERE ")
    actionQuery.append(s" FROM `${getNodeName(firstNodeId, nuoDagDetails)}` ")

    for (inNodeId <- inputOrder.tail) {

      /*
      * To compare the structure of the input nodes we need to sort the column mapping of each input node,
      * So two inputs with same column mapping but in different order pass the test
      * */
      val colMapList = nuoDagDetails.nodes(inNodeId)(ColumnMapping)
        .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]]
        .sortBy(colMap => colMap(ColumnName))

      val compString = colMapList
        .map { colMap =>
          (colMap(ColumnName)
            + colMap(NuoModifier.ActionAttrName.DataType)
            + colMap(Scale)
            + colMap(Precision))
        }

      var firstFlag = true
      if (!firstCompString.equals(compString)) {
        throw new Exception("This component: " + actionName + " contains at least one input that is not identical to "
          + "structure of other inputs"
          + " Comparison failed for Components: [" + getNodeName(firstNodeId, nuoDagDetails) + "] AND [" + getNodeName(inNodeId, nuoDagDetails) + "]")
      } else {

        /*
        * If column Mapping of current input is same as earlier inputs then perform operation the current data set with union result obtained so far
        * */
        actionType match {

          case ActionType.Dml.Union =>
            actionQuery.append(" UNION ")
            if (unionAll)
              actionQuery.append("ALL ")
            else
              actionQuery.append("DISTINCT ")

            actionQuery.append(getGenericSelectClauseForTarget(colMapList, nuoDagDetails) + s" FROM `${getNodeName(inNodeId, nuoDagDetails)}` ")

          case ActionType.Dml.Minus =>

            actionQuery.append(" LEFT JOIN ")
              .append(s"`${getNodeName(inNodeId, nuoDagDetails)}` USING (")
              .append(firstColumnMap
                .map(colMap => s"`${colMap(ColumnName)}`")
                .mkString(","))
              .append(")")
            //
            if (!firstFlag) {
              whereClause.append(" AND ")
            }
            firstFlag = false
            //
            whereClause.append(firstColumnMap
              .map(colMap => s"`${getNodeName(inNodeId, nuoDagDetails)}`.`${colMap(ColumnName)}` IS NULL")
              .mkString(" AND "))

          case ActionType.Dml.Intersect =>
            actionQuery.append(" INNER JOIN ")
              .append(s"`${getNodeName(inNodeId, nuoDagDetails)}` USING (")
              .append(firstColumnMap
                .map(colMap => s"`${colMap(ColumnName)}`")
                .mkString(","))
              .append(")")
        }
      }
    }

    if (actionType.contentEquals(ActionType.Dml.Minus)) {
      actionQuery.append(whereClause)
    }
    /*Register result of the union as temp table
    * */
    registerActionQuery(actionQuery.toString(), actionName, nuoDagDetails)
  }

  /*
  * This function performs the variable assignment.
  * It evaluate the expression defined for each column and creates the broadcast instance for it
  * */
  def evalAssignVariable(actionName: String,
                         nodeId: String,
                         columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): Unit = {

    val selectClause = getSelectClauseAndSourceNameForCompute(actionName, nodeId, columns, nuoDagDetails)

    val query = selectClause._1

    if (selectClause._2.isDefined && selectClause._3) {
      val sourceTable = selectClause._2.get
      query.append(" FROM `" + sourceTable + "` LIMIT 1")
    }

    val queryResponse = executeDMLSelectFixedPolling(query.toString(), nuoDagDetails)
    //      resultDF.printSchema()
    val fields = queryResponse._2

    val allVariables = queryResponse._1.get.head.zip(fields).map { ele =>
      val varName = ele._2._1
      val varType = ele._2._2

      val varContent = ele._1.toString


      /*
      * ****************************************************************************************************
      * IMPORTANT: Instead of adding/updating broadcast variables, use NuoStorage for variable implementation.
      * UPDATE: Updated the variable read/write mechanism -14MAR2016
      * UPDATE: Now, can handle ephemeral variables too. - 19MAR2016
      * UPDATE:Pulkit:14APR2017: All variables are global
      * ****************************************************************************************************
      * */
      NuoVariable("", varName, varType, varContent, "")
    }
    assignVariables(nodeId, nuoDagDetails.hierarchy(nodeId)._1, NuoVariables(allVariables), nuoDagDetails)
    dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "VAR", 1.0, "NA", nuoDagDetails)

  }

  /*
  * This function construct the condition to chekck if none of the column in given table is null
  * */
  def getNullCheckCondition(table: String,
                            keyColumns: Vector[String], nuoDagDetails: NuoDagDetails): String = {

    keyColumns.map(col => s"`$table`.$col IS NULL").mkString(" AND ")
  }


  /*
  * Construct a condition to check whether any of the key column from  source side is NULL
  * */
  def getSourceColNotNullCheckForMerge(keyColumns: Vector[String],
                                       sourceTable: String, nuoDagDetails: NuoDagDetails): String = {


    var firstElementFlag = true
    val sourceColNotNullCheck = new StringBuilder()

    for (column <- keyColumns) {
      if (firstElementFlag) {
        firstElementFlag = false
      } else {
        sourceColNotNullCheck.append(" AND ")

      }
      sourceColNotNullCheck.append(s" SRC.`$column` IS NOT NULL")
    }
    sourceColNotNullCheck.toString()
  }


  /*
  * While finding no change records in update section,
   * we need to pick columns from target side from right outer join in case there are no matching keys
  * and if there are matching keys then we pick columns from source side
  * */
  def getCaseStatementForMerge(keyColumns: Vector[String],
                               sourceTable: String,
                               targetTable: String,
                               FieldName: String, nuoDagDetails: NuoDagDetails): String = {

    val columnSelection = new StringBuilder("")

    columnSelection.append(s" CASE WHEN (${getSourceColNotNullCheckForMerge(keyColumns, sourceTable, nuoDagDetails)}) THEN SRC.`$FieldName`")
      .append(s" ELSE TGT.`$FieldName` END AS `$FieldName`")
    //    printInfo("This is case statement==>"+columnSelection.toString())
    columnSelection.toString()
  }

  def getMergeMinusQuery(leftQuery: String,
                         rightQuery: String,
                         actionName: String,
                         columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): String = {

    val actionQuery = new StringBuilder()

    registerActionQuery(leftQuery, s"${actionName}_1", nuoDagDetails)

    val whereClause = new StringBuilder("WHERE ")
    actionQuery.append(s" SELECT * FROM ${actionName}_1 LEFT JOIN ")
      .append(s" ($rightQuery) ${actionName}_2 USING (")
      .append(columns
        .map(colMap => s"`${colMap(ColumnSource)}`")
        .mkString(","))
      .append(")")

    whereClause.append(columns
      .map(colMap => s"${actionName}_2.`${colMap(ColumnSource)}` IS NULL")
      .mkString(" AND "))
    actionQuery.append(whereClause).toString()
  }

  /*
  * This function constructs the outer join query for given operation in merge (insert,update,delete)
  * */
  def getMergeChildQuery(flag: String,
                         sourceTable: String,
                         targetTable: String,
                         //                         joinCondition: String,
                         updateCondition: String,
                         deleteCondition: String,
                         keyColumns: Vector[String],
                         columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): String = {

    val query = new StringBuilder("SELECT ")


    var firstElementFlag = true
    for (column <- columns) {

      val FieldName = column(ColumnName)
      if (firstElementFlag) {
        firstElementFlag = false
      } else {
        query.append(",")

      }

      /*
      * If constructing select clause for insert query then pick source side value from join
      * else if it is for update query then apply further logic
      * and in case of delete query pick target side values from target side of join these records are no-change record
      * */
      if (flag.equalsIgnoreCase(NuoModifier.ActionPropertyName.Dml.Merge.IsInsert)) {
        query.append(s" SRC.`$FieldName` AS `$FieldName`")
      } else if (flag.equalsIgnoreCase(NuoModifier.ActionPropertyName.Dml.Merge.IsDelete)) {
        query.append(s" TGT.`$FieldName` AS `$FieldName`")
      } else if (flag.equalsIgnoreCase(NuoModifier.ActionPropertyName.Dml.Merge.IsUpdate)) {

        //        printInfo("Inside update select clause column definition")
        query.append(getCaseStatementForMerge(keyColumns, sourceTable, targetTable, FieldName, nuoDagDetails))
      }
    }

    query.append(s" FROM `$sourceTable` SRC")

    /*
    * Construct join clause based on query type
    * */
    if (flag.equalsIgnoreCase(NuoModifier.ActionPropertyName.Dml.Merge.IsInsert)) {

      /*
      * Records that have target side values NULL in LEFT OUTER JOIN are the one that exist in source side but not on target side
      * These records need to be inserted in target data set
      * */
      query.append(s" LEFT OUTER JOIN `$targetTable` TGT USING (${keyColumns.mkString(",")})")
      query.append(" WHERE " + keyColumns.map(col => s"TGT.$col IS NULL").mkString(" AND "))


    } else if (flag.equalsIgnoreCase(NuoModifier.ActionPropertyName.Dml.Merge.IsUpdate)) {


      /*
      * RIGHT OUTER JOIN gives the records that are either not available in source side (These are no change record and only target side values should be picked up)
      * or has matching keys and update condition satisfied are the update records
      * */
      query.append(" RIGHT OUTER JOIN `" + targetTable + s"` TGT USING (${keyColumns.mkString(",")}) ")
      if (updateCondition.nonEmpty) {
        query.append(s" WHERE $updateCondition")
      }

    } else if (flag.equalsIgnoreCase(NuoModifier.ActionPropertyName.Dml.Merge.IsDelete)) {

      /*
      * If delete query is provided then include it in join condition
      * */
      query.append(" INNER JOIN `" + targetTable + s"` TGT USING (${keyColumns.mkString(",")}) ")

      if (deleteCondition.nonEmpty) {
        /*
        * If update query is provided then include it in join condition
        * */
        query.append(s" WHERE $deleteCondition")
      }
    }
    query.toString()
  }

  /*
  * This function performs the join operation on the inputs and register the resultant data set as temporary table
  * */
  def evalMerge(nodeId: String,
                actionName: String,
                keyColumns: Vector[String],
                sourceTable: String,
                targetTable: String,
                isInsert: Boolean,
                isUpdate: Boolean,
                isDelete: Boolean,
                updateCondition: String,
                deleteCondition: String,
                columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): Unit = {

    if (!isInsert && !isUpdate && !isDelete) {
      NuoLogger.logError(s"None of the operations [Insert,Update,Delete] is selected for the Merge action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I cannot execute it.", nuoDagDetails)

      //      throw new Exception("No operation is selected for Merge component. How am I supposed to merge data?! Component Name:[" + actionName + "]")
    } else {
      if (isInsert && isUpdate && isDelete) {

        /*
        * Insert else Delete/Update section
        * */

        /*
        * Union of records from insert, update and delete queries may have duplicate records.
        * Because update query and delete query both provide the no change records,
        * these are removed by distinct function
        * */
        if (updateCondition.isEmpty)
          NuoLogger.logWarning(s"Update condition for the Merge action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I will update all the matching records.", nuoDagDetails)
        if (deleteCondition.isEmpty)
          NuoLogger.logWarning(s"Delete condition for the Merge action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I will delete all the matching records.", nuoDagDetails)

        val insertQuery = getMergeChildQuery(NuoModifier.ActionPropertyName.Dml.Merge.IsInsert, sourceTable, targetTable, "", "", keyColumns, columns, nuoDagDetails)
        val updateQuery = getMergeChildQuery(NuoModifier.ActionPropertyName.Dml.Merge.IsUpdate, sourceTable, targetTable, updateCondition, deleteCondition, keyColumns, columns, nuoDagDetails)
        val deleteQuery = getMergeChildQuery(NuoModifier.ActionPropertyName.Dml.Merge.IsDelete, sourceTable, targetTable, "", deleteCondition, keyColumns, columns, nuoDagDetails)

        registerActionQuery(query = getMergeMinusQuery(leftQuery = insertQuery + " UNION DISTINCT " + updateQuery,
          rightQuery = deleteQuery,
          actionName = actionName,
          columns = columns, nuoDagDetails),
          actionName = actionName, nuoDagDetails)

      } else if (isInsert && !isUpdate && !isDelete) {

        /*
        * Insert only section
        * */


        val insertQuery = getMergeChildQuery(NuoModifier.ActionPropertyName.Dml.Merge.IsInsert, sourceTable, targetTable, "", "", keyColumns, columns, nuoDagDetails)

        /*
        * Since insert query does not provide no change records. The result should be union of target data set and insert records.
        * */
        val targetQuery = new StringBuilder(getGenericSelectClauseForTarget(columns, nuoDagDetails).toString())
        targetQuery.append(" FROM `" + targetTable + "` TGT")

        val actionQuery = insertQuery + " UNION DISTINCT" + targetQuery
        registerActionQuery(actionQuery, actionName, nuoDagDetails)

      } else if (isDelete && !isInsert && !isUpdate) {

        /*
        * Delete only section
        * */
        if (deleteCondition.isEmpty)
          NuoLogger.logWarning(s"Delete condition for the Merge action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I will delete all the matching records.", nuoDagDetails)

        val targetQuery = new StringBuilder(getGenericSelectClauseForTarget(columns, nuoDagDetails).toString())
        targetQuery.append(" FROM `" + targetTable + "` TGT")

        val deleteQuery = getMergeChildQuery(NuoModifier.ActionPropertyName.Dml.Merge.IsDelete, sourceTable, targetTable, "", deleteCondition, keyColumns, columns, nuoDagDetails)

        //      val resultDF = hiveContext.sql(targetQuery.toString()).except(hiveContext.sql(deleteQuery)).distinct()
        registerActionQuery(query = getMergeMinusQuery(leftQuery = targetQuery.toString(),
          rightQuery = deleteQuery,
          actionName = actionName,
          columns = columns, nuoDagDetails),
          actionName = actionName, nuoDagDetails)

      } else if (isUpdate && !isInsert && !isDelete) {

        /*
        * Update only section
        * */
        if (updateCondition.isEmpty)
          NuoLogger.logWarning(s"Update condition for the Merge action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I will update all the matching records.", nuoDagDetails)
        if (deleteCondition.isEmpty)
          NuoLogger.logWarning(s"Delete condition for the Merge action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I will delete all the matching records.", nuoDagDetails)
        val updateQuery = getMergeChildQuery(NuoModifier.ActionPropertyName.Dml.Merge.IsUpdate, sourceTable, targetTable, updateCondition, deleteCondition, keyColumns, columns, nuoDagDetails)

        registerActionQuery(updateQuery, actionName, nuoDagDetails)


      } else if (!isDelete) {

        /*
        * Insert else Update section
        * */
        if (updateCondition.isEmpty)
          NuoLogger.logWarning(s"Update condition for the Merge action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I will update all the matching records.", nuoDagDetails)
        if (deleteCondition.isEmpty)
          NuoLogger.logWarning(s"Delete condition for the Merge action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I will delete all the matching records.", nuoDagDetails)
        val insertQuery = getMergeChildQuery(NuoModifier.ActionPropertyName.Dml.Merge.IsInsert, sourceTable, targetTable, "", "", keyColumns, columns, nuoDagDetails)
        val updateQuery = getMergeChildQuery(NuoModifier.ActionPropertyName.Dml.Merge.IsUpdate, sourceTable, targetTable, updateCondition, deleteCondition, keyColumns, columns, nuoDagDetails)


        //      val resultDF = hiveContext.sql(insertQuery).unionAll(hiveContext.sql(updateQuery)).distinct()
        registerActionQuery(s"$insertQuery UNION DISTINCT $updateQuery", actionName, nuoDagDetails)

      } else if (!isInsert) {

        /*
        *  Update else Delete section
        * */
        if (updateCondition.isEmpty)
          NuoLogger.logWarning(s"Update condition for the Merge action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I will update all the matching records.", nuoDagDetails)
        if (deleteCondition.isEmpty)
          NuoLogger.logWarning(s"Delete condition for the Merge action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I will delete all the matching records.", nuoDagDetails)
        val updateQuery = getMergeChildQuery(NuoModifier.ActionPropertyName.Dml.Merge.IsUpdate, sourceTable, targetTable, updateCondition, deleteCondition, keyColumns, columns, nuoDagDetails)
        val deleteQuery = getMergeChildQuery(NuoModifier.ActionPropertyName.Dml.Merge.IsDelete, sourceTable, targetTable, "", deleteCondition, keyColumns, columns, nuoDagDetails)


        //      val resultDF = hiveContext.sql(updateQuery).except(hiveContext.sql(deleteQuery)).distinct()
        registerActionQuery(query = getMergeMinusQuery(leftQuery = updateQuery,
          rightQuery = deleteQuery,
          actionName = actionName,
          columns = columns, nuoDagDetails),
          actionName = actionName, nuoDagDetails)

      } else {

        /*
        * Insert else Delete section
        * */
        if (deleteCondition.isEmpty)
          NuoLogger.logWarning(s"Delete condition for the Merge action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I will delete all the matching records.", nuoDagDetails)

        val insertQuery = getMergeChildQuery(NuoModifier.ActionPropertyName.Dml.Merge.IsInsert, sourceTable, targetTable, "", "", keyColumns, columns, nuoDagDetails)
        val deleteQuery = getMergeChildQuery(NuoModifier.ActionPropertyName.Dml.Merge.IsDelete, sourceTable, targetTable, "", deleteCondition, keyColumns, columns, nuoDagDetails)

        val targetQuery = new StringBuilder(getGenericSelectClauseForTarget(columns, nuoDagDetails).toString())
        targetQuery.append(" FROM `" + targetTable + "`")

        registerActionQuery(query = getMergeMinusQuery(leftQuery = insertQuery + " UNION DISTINCT " + targetQuery,
          rightQuery = deleteQuery,
          actionName = actionName,
          columns = columns, nuoDagDetails),
          actionName = actionName, nuoDagDetails)
      }
    }
    //    val joinCondition = getMergeCondition(sourceTable, targetTable, keyColumns)
  }


  /*
  * This function evaluate the match operation on the inputs and register the resultant data set as temporary table
  * */
  def evalMatchCase(sourceTable: String,
                    nodeId: String,
                    actionName: String,
                    actionType: String,
                    inputPatternColumn: String,
                    columns: mutable.ListBuffer[mutable.HashMap[String, String]], nuoDagDetails: NuoDagDetails): Unit = {

    val selectClause = getGenericSelectClauseForSource(nodeId, columns, nuoDagDetails)


    val query = selectClause
    if (sourceTable.nonEmpty) {
      query.append(" FROM `" + sourceTable + "`")
    } else {
      //      query.append(" FROM `" + _dummyTable + "`")

    }

    registerActionQuery(query.toString(), actionName, nuoDagDetails)

    /*
    * If Match node contains only one column Input_Pattern then only the Case nodes that matches input pattern needs to be allowed for execution
    * If multiple inputs are given to the single column Input_Pattern only first input will be considered
    * */
    if (columns.size <= 1) {
      NuoLogger.logWarning(s"There are no column mapping defined in the action = ${getNodeName(nodeId, nuoDagDetails)}. I cannot execute it.", nuoDagDetails)
    } else if (columns.size > 1) {


      /*
      * All direct outbound nodes of Match are nodes with type Case.
      * For each Case we will get the records from Match where pattern case is matching input pattern.
      * */
      val defaultPattern = new ArrayBuffer[String]()
      var firstEleflag = true
      var defaultNodeId = ""
      val attrMap = nuoDagDetails.nodes(nodeId).asInstanceOf[mutable.HashMap[String, String]]
      for (outboundNodeId <- nuoDagDetails.outboundNodes(nodeId)) {

        val tempMap = nuoDagDetails.nodes(outboundNodeId)
        val attrMap = tempMap.asInstanceOf[mutable.HashMap[String, String]]

        /*
        * In case of default case save node id of Default Case and use custom filter outside of this loop
        * */
        if (!tempMap(NodeType).asInstanceOf[String].equalsIgnoreCase(ActionType.Dml.DefaultCase)) {

          val casePattern = attrMap(NuoModifier.ActionPropertyName.Dml.Case.CasePatternValue)

          val caseQuery = getGenericSelectClauseForTarget(columns, nuoDagDetails)
          caseQuery.append(s" FROM `$actionName` WHERE $inputPatternColumn = $casePattern")
          registerActionQuery(caseQuery.toString(), s"${attrMap(NodeName)}", nuoDagDetails)

          defaultPattern += casePattern
        } else {
          defaultNodeId = outboundNodeId
        }
      }

      /*
      * Register temptable for default case node
      * */
      if (defaultNodeId.nonEmpty) {

        val tempMap = nuoDagDetails.nodes(defaultNodeId)
        val attrMap = tempMap.asInstanceOf[mutable.HashMap[String, String]]

        val defaultCaseQuery = getGenericSelectClauseForTarget(columns, nuoDagDetails)
        defaultCaseQuery.append(s" FROM `$actionName` WHERE $inputPatternColumn NOT IN (" + defaultPattern.mkString(",") + ")")
        registerActionQuery(defaultCaseQuery.toString(), s"${attrMap(NodeName)}", nuoDagDetails)
      }
    }
  }
}*/
