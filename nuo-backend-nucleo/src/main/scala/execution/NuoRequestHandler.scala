package execution

import java.io.{InputStream, OutputStream, OutputStreamWriter}

import action.{NuoAIaaS, NuoDML, NuoMLaaS, NuoStorage}
import canvas.{NuoDataTypeHandler, NuoModifier}
import client.NuoBqClient.getNuoStorageDetails
import client._
import com.amazonaws.HttpMethod
import com.amazonaws.services.lambda.runtime.Context
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import dag.NuoTenantDetails
import logging.NuoLogger
import metadata.NuoRecognitionMetadata.{QuestionType, RecognitionCommunicationType}
import metadata.NuoRequestMetadata.{NuoPollingDetails, _}
import metadata.StorageMetadata.NuoField
import metadata.{NuoRecognitionMetadata, NuoRequestMetadata}
import net.liftweb.json.DefaultFormats
import nlp.NuoQuestionParser
import nlp.grammar.NuoEvaEnglishListener
import org.apache.commons.codec.digest.DigestUtils

import scala.collection.mutable
import scala.io.Source

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 03Mar2018
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoRequestHandler {

  var isLocalRun = false

  def main(args: Array[String]): Unit = {
    isLocalRun = true
    handleRequest(null, null, null)

    //    NuoSecurityClient.registerUser(
    //      paramTenantId = "",
    //      paramUsername = "steven.ammerlaan",
    //      paramEmail = "ronan@nuocanvas.com",
    //      isCrmUser = false
    //    )

    //    NuoEvaEnglishListener.nuoTenantDetails = new NuoTenantDetails("System_" + System.currentTimeMillis(), "tenant1", "")
    //    NuoBqClient.deleteTempTables()

    //    NuoEvaEnglishListener.nuoTenantDetails = new NuoTenantDetails("System_" + System.currentTimeMillis(), "16409d3023f", "")
    //
    //    val targetAccountDetails =
    //      AccountMetadata
    //        .getNewAccountDetails(
    //          tenantId = "16409d3023f",
    //          username = "",
    //          emailAddress = "",
    //          userName = Some(""),
    //          userDisplayName = Some("")
    //        )
    //    targetAccountDetails.GcpAccountDetails = Some(GcpDetails(Some("geometric-watch-153714"), None))
    //    NuoEvaEnglishListener.nuoTenantDetails.initTenantAccountDetails(targetAccountDetails)
    //
    //    NuoBqClient.copyDemoTables("tenant1", "16409d3023f")

  }


  /*
  ***********************************************************************************************************************
  ***********************************************************************************************************************
  ***********************************************************************************************************************
  * IMPORTANT:
  * BOdy mapping template from Api Gateway to Lambda:
  * {

      "Body" : $input.json('$'),

      "Headers": [

          #foreach($param in $input.params().header.keySet())

          {
              "Name":"$param",
              "Value": "$util.escapeJavaScript($input.params().header.get($param))"
          }

          #if($foreach.hasNext),#end

          #end

      ]
  }
  ***********************************************************************************************************************
  *  CORS Additional headers: Access-Control-Allow-Origin,Access-Control-Allow-Headers,Access-Control-Allow-Methods
  ***********************************************************************************************************************
  ***********************************************************************************************************************
  ***********************************************************************************************************************
  *
  * */
  def handleRequest(inputStream: InputStream,
                    outputStream: OutputStream,
                    lambdaContext: Context): Unit = {
    NuoLogger.printInfo(s"${NuoEvaEnglishListener.ServerDateFormat.format(System.currentTimeMillis())}: nuo-backend-nucleo: Execution started")
    NuoEvaEnglishListener.isGrammarTestMode = false

    try {
      sendResponse(
        outputStream = outputStream,
        content = performRequestOperation(
          nuoApiRequest = getRequestBody(inputStream),
          lambdaContext = lambdaContext))
    } catch {
      case e: Exception =>
        if (e.getMessage != null && e.getMessage.equalsIgnoreCase("UserInputRequested")) {
          NuoLogger.printInfo("NuoEvaMessage = " + NuoEvaEnglishListener.nuoEvaMessage)
          sendResponse(
            outputStream = outputStream,
            content = getMessageResponseRef(statusCode = 200,
              status = "OK",
              messageRef =
                NuoQueryResponse(
                  NuoEvaMessage = NuoEvaEnglishListener.nuoEvaMessage,
                  Result = None,
                  ProfilingResult = None,
                  Pattern = None)
            ))

        } else {

          val errorMessage = if (e.getMessage == null) {
            NuoLogger.printInfo(e.getStackTraceString)
            NuoLogger.printException(e)
            NuoLogger.printInfo("Error without any message.")
            "I encountered an internal error. Please contact NuoCanvas support."

          } else if (e.getMessage != null && e.getMessage.toUpperCase.startsWith("ERROR_REPORT")) {
            NuoLogger.printInfo(e.getStackTraceString)
            NuoLogger.printInfo("Error Message = " + e.getMessage)
            e.getMessage

          } else {
            NuoLogger.printInfo(e.getStackTraceString)
            NuoLogger.printInfo("Error at unexpected location = " + e.getMessage)
            "I encountered an internal error: " +
              e.getMessage
                .replaceAll(NuoEvaEnglishListener.nuoTenantDetails.tenantId, "")
                .replaceAll(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId, "") +
              ". Please contact NuoCanvas support."
          }
          sendResponse(
            outputStream = outputStream,
            content = getMessageResponseRef(statusCode = 200,
              status = "OK",
              messageRef =
                NuoQueryResponse(
                  NuoEvaMessage =
                    NuoRequestMetadata.NuoEvaMessage(
                      AnalysisId = "",
                      RuleText = "",
                      CommunicationType = RecognitionCommunicationType.COMMUNICATION_TYPE_ERROR,
                      Message = errorMessage,
                      LeftEntityName = None,
                      RightEntityName = None,
                      NuoMappingInput = None,
                      NuoPollingDetails = None
                    ),
                  Result = None,
                  ProfilingResult = None,
                  Pattern = None)
            ))
        }
    }
  }

  def getRequestBody(inputStream: InputStream): NuoApiRequest = {

    val apiRequestBody = if (inputStream != null) {
      Source.fromInputStream(inputStream).getLines().mkString
    } else {

      //     Find bank age and job
      // sessionId = "A1E58C40449F1FDA9BBCE6AA7B6BE975";
      // sessionId = "C2198BEF7066F89EC1DC652D38809405";
      """
        |{"Body":{"RequestId":"Request_1545151851292","RequestType":"rt107","SessionId":"0AD6DB904BB5849AEE4F51F1D260FFD4"}}
      """.stripMargin
      //      """
      //        |{"Body":{"RequestId":"Request_1541935809319","RequestType":"rt41","SessionId":"20DD7EC143C55C5D02B4C23130C50F2B","NuoHistoryPaginationRequest":{}}}
      //      """.stripMargin
    }
    if (!apiRequestBody.contains("Password")) {

      NuoLogger.printInfo(apiRequestBody)
    } else {
      NuoLogger.printInfo("Request for login detected. Skipping the logging for security reasons.")
    }
    metadata
      .NuoRequestMetadata
      .getNuoApiRequest(apiRequestBody)
  }

  def sendResponse(outputStream: OutputStream,
                   content: String): Unit = {

    println(s"Response = $content")
    NuoLogger.printInfo("Execution completed. Response is about to be sent.")
    //    NuoEvaEnglishListener.clearCache()
    if (outputStream != null) {
      val writer = new OutputStreamWriter(outputStream)
      writer.write(content)
      writer.close()
    }
  }

  def performRequestOperation(nuoApiRequest: NuoApiRequest,
                              lambdaContext: Context): String = {

    if (nuoApiRequest.Body.RequestType.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.AuthenticateCredentials)) {

      val reqBody = nuoApiRequest.Body.NuoCredentialsRequest.get

      authenticateCredentials(
        reqBody.UserName,
        NuoSecurityClient
          .authenticateCredentials(
            reqBody.UserName.toLowerCase,
            reqBody.Password,
            isCrmUser = false
          ),
        isCrmLogin = false
      )
    } else if (nuoApiRequest.Body.RequestType.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.AuthenticateCrmCredentials)) {

      val reqBody = nuoApiRequest.Body.NuoCredentialsRequest.get

      authenticateCredentials(
        reqBody.UserName,
        NuoSecurityClient
          .authenticateCredentials(
            reqBody.UserName.toLowerCase,
            reqBody.Password,
            isCrmUser = true
          ),
        isCrmLogin = true
      )
    } else {

      if (nuoApiRequest.Body.RequestType.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.LogOut)) {

        endSession(nuoApiRequest.Body.SessionId.get)

        getMessageResponsePlain(statusCode = 200, status = "OK", message = "You have been logged out.")

      } else if (!validateSession(nuoApiRequest.Body.SessionId.get)) {

        NuoLogger.printInfo("Session is not valid.")
        getMessageResponsePlain(statusCode = 440, status = "Login Time-out", message = "Your session is not valid. Please login again.")

      } else {

        implicit val formats = DefaultFormats
        NuoLogger.printInfo(net.liftweb.json.Serialization.write(nuoApiRequest.Body))
        val sessionDetails = getSessionDetails(nuoApiRequest.Body.SessionId.get)

        NuoEvaEnglishListener.initNuoTenantDetails(
          nuoApiRequest.Body.RequestId,
          sessionDetails._1,
          sessionDetails._2,
          lambdaContext
        )
        val nuoTenantDetails = NuoEvaEnglishListener.nuoTenantDetails

        nuoApiRequest.Body.RequestType match {

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.GetUsername) =>
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message =
                sessionDetails._2
            )

          /*
          ************************************************************************************************************
          ************************************************************************************************************
          * CRM Methods START
          ************************************************************************************************************
          ************************************************************************************************************
          * */
          //Methods for CRM
          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.Crm.GetMapApiKey) =>
            getMessageResponsePlain(statusCode = 200, status = "OK", message = NuoEvaEnglishListener.GoogleMapApiKey)

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.Crm.GetUserMetadata) =>
            implicit val formats = DefaultFormats
            val content = net.liftweb.json.Serialization.write(getCrmUserMetadata)
            getMessageResponsePlain(statusCode = 200, status = "OK", message = content.substring(1, content.length - 1))

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.Crm.SaveUserMetadata) =>
            getMessageResponsePlain(statusCode = 200, status = "OK", message = saveCrmUserMetadata(nuoApiRequest.Body.NuoCrmUserMetadata.get))

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.Crm.CrmUploadFile) =>
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = crmUploadFileUrl(
                nuoApiRequest.Body.NuoFileContent.get.FileName,
                nuoApiRequest.Body.NuoFileContent.get.ContentType.get
              )
            )

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.Crm.CrmDownloadFile) =>
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = crmDownloadFileUrl(nuoApiRequest.Body.NuoFileContent.get.FileName))

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.Crm.CrmDeleteFile) =>
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = crmDeleteFile(nuoApiRequest.Body.NuoFileContent.get.FileName))
          /*
          ************************************************************************************************************
          ************************************************************************************************************
          * CRM Methods END
          ************************************************************************************************************
          ************************************************************************************************************
          * */


          //Methods for Eva
          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.EvaTextToSpeech) =>
            val speechOptions = nuoApiRequest.Body.NuoSpeechOptions.get
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = NuoTextToSpeechClient.convertTextToSpeechPolly(speechOptions.AnalysisId, speechOptions.Ssml.get, speechOptions.LanguageCode.get)
            )
          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.SaveToHistory) =>
            saveToHistory(nuoApiRequest.Body.NuoHistoryPaginationRequest)
          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.DeleteFromHistory) =>

            NuoJdbcClient.deleteFromMetadataTable(
              NuoEvaEnglishListener.nuoTenantDetails.MetadataTable.nuoHistoryFeed,
              s"AnalysisId = '${nuoApiRequest.Body.NuoUserMessage.get.AnalysisId}'"
            )
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = s"Successfully deleted the analysis.")

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.ReadFromHistory) =>
            readFromHistory(nuoApiRequest.Body.NuoHistoryPaginationRequest)

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.PerformAnalysis) =>
            //            val start = System.currentTimeMillis()
            //            NuoLogger.printInfo("========================================================================================")
            //            val targetTableName = "x"
            //            val targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId
            //            NuoBqClient.executeQueryJob(
            //              querySql = s"SELECT vendor_id, fare_amount, tip_amount, total_amount, trip_distance FROM `nyc-tlc.yellow.trips` LIMIT ${nuoApiRequest.Body.NuoUserMessage.get.Filter}",
            //              targetDatasetName = targetDatasetName,
            //              targetTableName = targetTableName,
            //              pollingIntervalMillis = 1000
            //            )
            //            NuoLogger.printInfo(
            //              NuoDataGrid(
            //                Metadata = NuoBqClient
            //                  .getTableSchema(
            //                    targetDatasetName,
            //                    targetTableName)
            //                  .map(pair => NuoField("", targetTableName, pair._1, pair._2))
            //                  .toList,
            //                Data = NuoBqClient
            //                  .listTableData(
            //                    datasetName =targetDatasetName,
            //                    tableName = targetTableName,
            //                    pageToken = null,
            //                    pageSize = 100L)
            //                  ._2
            //                  .map(_.toList).toList)
            //            )
            //            NuoLogger.printInfo("========================================================================================")
            //            val timeElapsed = (System.currentTimeMillis() - start)/1000
            //            NuoLogger.printInfo(s"Time elapsed= $timeElapsed seconds.")
            //            timeElapsed+" Seconds."
            performAnalysis(
              nuoUserMessage = nuoApiRequest.Body.NuoUserMessage.get,
              paramNuoGrammarListener = null,
              profileResults = false,
              nuoTenantDetails = nuoTenantDetails
            )

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.ProfileResults) =>
            performAnalysis(
              nuoUserMessage = nuoApiRequest.Body.NuoUserMessage.get,
              paramNuoGrammarListener = null,
              profileResults = true,
              nuoTenantDetails = nuoTenantDetails
            )

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.PreviewTable) =>
            previewTable(
              targetTableName = nuoApiRequest.Body.NuoEntities.get.head.EntityName,
              profileResults = false
            )

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.ProfileTable) =>
            previewTable(
              targetTableName = nuoApiRequest.Body.NuoEntities.get.head.EntityName,
              profileResults = true
            )

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.EvaListFiles) =>
            getMessageResponseRef(statusCode = 200,
              status = "OK",
              messageRef =
                NuoEvaFilesResponse(listNuoEvaFiles)
            )

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.EvaUploadFile) =>
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = evaUploadFileUrl(
                nuoApiRequest.Body.NuoFileContent.get.FileName,
                nuoApiRequest.Body.NuoFileContent.get.ContentType.get
              )
            )

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.EvaLoadFileToTable) =>
            evaLoadFileToTable(nuoApiRequest.Body.NuoFileLoadOptions.get, null, nuoTenantDetails)

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.EvaDownloadFile) =>
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = evaDownloadFileUrl(nuoApiRequest.Body.NuoFileContent.get.FileName)
            )

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.EvaDeleteFile) =>
            var deleteResponse = ""

            nuoApiRequest.Body.NuoFileContentList.get.foreach { ele =>

              deleteResponse = evaDeleteFile(ele.FileName)
            }
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = deleteResponse)

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.EvaAnalyzeImages) =>
            NuoAIaaS.analyzeImages(nuoApiRequest.Body.NuoAnalyzeImageOptions.get, nuoTenantDetails)

          //
          //          case x
          //            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.EvaGetProgressReport) =>
          //
          //            getMessageResponseRef(statusCode = 200,
          //              status = "OK",
          //              messageRef =
          //                NuoEvaProgressMonitor(
          //                  NuoRecognitionMetadata.readQuestionRecognitionData
          //                    .map { ele =>
          //                      NuoQuestionMonitor(
          //                        ele.QuestionAlias,
          //                        ele.QuestionText,
          //                        ele.StepsCompleted.getOrElse(0),
          //                        ele.TotalSteps.getOrElse(0),
          //                        ele.StartTimeMillis.get,
          //                        ele.EndTimeMillis.getOrElse(System.currentTimeMillis())
          //                      )
          //                    }
          //                )
          //            )
          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.EvaGetTableList) =>

            //            NuoBqClient.writeStorageMetadataToFile()
            getMessageResponseRef(statusCode = 200,
              status = "OK",
              messageRef =
                NuoEvaTablesResponse(getNuoStorageDetails.NuoDatasets.flatMap(_.NuoEntities))
            )
          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.EvaDeleteTable) =>

            var deleteResponse = ""
            nuoApiRequest.Body.NuoEntities.get.foreach { ele =>

              deleteResponse =
                NuoBqClient
                  .deleteTable(
                    NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                    ele.EntityName
                  )
              NuoJdbcClient.deleteFromMetadataTable(
                NuoEvaEnglishListener.nuoTenantDetails.MetadataTable.nuoRelationships,
                s"LeftEntityName = '${ele.EntityName}' OR RightEntityName = '${ele.EntityName}' OR IntermediateEntities SIMILAR TO '${ele.EntityName},?'"
              )
            }

            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = deleteResponse)
          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.EvaDuplicateTable) =>

            var deleteResponse = ""
            nuoApiRequest.Body.NuoEntities.get.foreach { ele =>

              NuoLogger.printInfo(s"I am going to duplicate the following table: ${ele.EntityName}.")
              NuoBqClient.executeQueryJob(
                querySql = s"SELECT * FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele.EntityName}`",
                targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                targetTableName = ele.EntityName + "_copy",
                pollingIntervalMillis = 1000
              )
            }

            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = deleteResponse)

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.GridDeleteColumn) =>

            nuoApiRequest.Body
              .NuoGridFeatureOptions.get
              .NuoFieldCombinations
              .map(_.SourceField)
              .groupBy(_.get.EntityName)
              .foreach { ele =>
                if (ele._2.nonEmpty) {
                  val columnsToBeDeleted =
                    ele._2
                      .map { nuoField =>
                        "`" + nuoField.get.FieldName + "`"
                      }
                      .mkString(", ")
                  NuoLogger.printInfo(s"I am going to delete the following columns: $columnsToBeDeleted.")
                  NuoBqClient.executeQueryJob(
                    querySql = s"SELECT * EXCEPT($columnsToBeDeleted) FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele._1}`",
                    targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                    targetTableName = ele._1,
                    pollingIntervalMillis = 1000
                  )
                }
              }
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = s"Successfully deleted the columns.")

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.GridRenameColumn) =>

            nuoApiRequest.Body
              .NuoGridFeatureOptions.get
              .NuoFieldCombinations
              .filter(ele => ele.TargetField.isEmpty || ele.TargetField.get.EntityName.trim.isEmpty || ele.SourceField.get.EntityName.equalsIgnoreCase(ele.TargetField.get.EntityName))
              .groupBy(_.SourceField.get.EntityName)
              .foreach { ele =>
                if (ele._2.nonEmpty) {
                  val columnsToBeConverted = ele._2.map(ele => "`" + ele.SourceField.get.FieldName + "`").mkString(", ")
                  val columnExpressions =
                    ele._2
                      .map { fieldCombo =>
                        s" `${fieldCombo.SourceField.get.FieldName}` AS `${fieldCombo.TargetField.get.FieldName}`"
                      }
                      .mkString(", ")
                  NuoLogger.printInfo(s"I am going to rename the following columns: $columnsToBeConverted.")

                  var targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId
                  var targetTableName = ele._1
                  val profilingTableName = targetTableName + "_profiling"
                  val patternTableName = targetTableName + "_pattern"

                  NuoBqClient.executeQueryJob(
                    querySql = s"SELECT * EXCEPT($columnsToBeConverted), $columnExpressions FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`$targetTableName`",
                    targetDatasetName = targetDatasetName,
                    targetTableName = ele._1,
                    pollingIntervalMillis = 1000
                  )
                  NuoDML.profileResults(
                    resultDatasetName = targetDatasetName,
                    resultTableName = targetTableName,
                    profilingTableName = profilingTableName,
                    patternTableName = patternTableName,
                    nuoEvaEnglishListener = null
                  )
                }
              }
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = s"Successfully renamed the columns.")

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.GridDuplicateColumn) =>

            nuoApiRequest.Body
              .NuoGridFeatureOptions.get
              .NuoFieldCombinations
              .filter(ele => ele.TargetField.isEmpty || ele.TargetField.get.EntityName.trim.isEmpty || ele.SourceField.get.EntityName.equalsIgnoreCase(ele.TargetField.get.EntityName))
              .groupBy(_.SourceField.get.EntityName)
              .foreach { ele =>
                if (ele._2.nonEmpty) {
                  val columnsToBeConverted = ele._2.map(ele => "`" + ele.SourceField.get.FieldName + "`").mkString(", ")
                  val columnExpressions =
                    ele._2
                      .map { fieldCombo =>
                        s" `${fieldCombo.SourceField.get.FieldName}` AS `${fieldCombo.SourceField.get.FieldName}_1`"
                      }
                      .mkString(", ")
                  NuoLogger.printInfo(s"I am going to rename the following columns: $columnsToBeConverted.")
                  NuoBqClient.executeQueryJob(
                    querySql = s"SELECT *, $columnExpressions FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele._1}`",
                    targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                    targetTableName = ele._1,
                    pollingIntervalMillis = 1000
                  )
                  var targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId
                  var targetTableName = ele._1
                  val profilingTableName = targetTableName + "_profiling"
                  val patternTableName = targetTableName + "_pattern"
                  NuoDML.profileResults(
                    resultDatasetName = targetDatasetName,
                    resultTableName = targetTableName,
                    profilingTableName = profilingTableName,
                    patternTableName = patternTableName,
                    nuoEvaEnglishListener = null
                  )
                }
              }
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = s"Successfully renamed the columns.")

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.GridToIntegerColumn) =>

            nuoApiRequest.Body
              .NuoGridFeatureOptions.get
              .NuoFieldCombinations
              .filter(ele => ele.TargetField.isEmpty || ele.TargetField.get.EntityName.trim.isEmpty || ele.SourceField.get.EntityName.equalsIgnoreCase(ele.TargetField.get.EntityName))
              .groupBy(_.SourceField.get.EntityName)
              .foreach { ele =>
                if (ele._2.nonEmpty) {
                  val columnsToBeConverted = ele._2.map(ele => "`" + ele.SourceField.get.FieldName + "`").mkString(", ")
                  val columnExpressions =
                    ele._2
                      .map { fieldCombo =>
                        s"SAFE_CAST(`${fieldCombo.SourceField.get.FieldName}` AS INT64) AS `${fieldCombo.SourceField.get.FieldName}`"
                      }
                      .mkString(", ")
                  NuoLogger.printInfo(s"I am going to convert the following columns: $columnsToBeConverted to Integer.")
                  NuoBqClient.executeQueryJob(
                    querySql = s"SELECT * EXCEPT($columnsToBeConverted), $columnExpressions FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele._1}`",
                    targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                    targetTableName = ele._1,
                    pollingIntervalMillis = 1000
                  )
                  var targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId
                  var targetTableName = ele._1
                  val profilingTableName = targetTableName + "_profiling"
                  val patternTableName = targetTableName + "_pattern"
                  NuoDML.profileResults(
                    resultDatasetName = targetDatasetName,
                    resultTableName = targetTableName,
                    profilingTableName = profilingTableName,
                    patternTableName = patternTableName,
                    nuoEvaEnglishListener = null
                  )
                }
              }
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = s"Successfully renamed the columns.")

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.GridToStringColumn) =>

            nuoApiRequest.Body
              .NuoGridFeatureOptions.get
              .NuoFieldCombinations
              .filter(ele => ele.TargetField.isEmpty || ele.TargetField.get.EntityName.trim.isEmpty || ele.SourceField.get.EntityName.equalsIgnoreCase(ele.TargetField.get.EntityName))
              .groupBy(_.SourceField.get.EntityName)
              .foreach { ele =>
                if (ele._2.nonEmpty) {
                  val columnsToBeConverted = ele._2.map(ele => "`" + ele.SourceField.get.FieldName + "`").mkString(", ")
                  val columnExpressions =
                    ele._2
                      .map { fieldCombo =>
                        s"SAFE_CAST(`${fieldCombo.SourceField.get.FieldName}` AS STRING) AS `${fieldCombo.SourceField.get.FieldName}`"
                      }
                      .mkString(", ")
                  NuoLogger.printInfo(s"I am going to convert the following columns: $columnsToBeConverted to String.")
                  NuoBqClient.executeQueryJob(
                    querySql = s"SELECT * EXCEPT($columnsToBeConverted), $columnExpressions FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele._1}`",
                    targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                    targetTableName = ele._1,
                    pollingIntervalMillis = 1000
                  )
                  var targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId
                  var targetTableName = ele._1
                  val profilingTableName = targetTableName + "_profiling"
                  val patternTableName = targetTableName + "_pattern"
                  NuoDML.profileResults(
                    resultDatasetName = targetDatasetName,
                    resultTableName = targetTableName,
                    profilingTableName = profilingTableName,
                    patternTableName = patternTableName,
                    nuoEvaEnglishListener = null
                  )
                }
              }
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = s"Successfully renamed columns.")

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.GridTrimColumn) =>

            nuoApiRequest.Body
              .NuoGridFeatureOptions.get
              .NuoFieldCombinations
              .filter(ele => ele.TargetField.isEmpty || ele.TargetField.get.EntityName.trim.isEmpty || ele.SourceField.get.EntityName.equalsIgnoreCase(ele.TargetField.get.EntityName))
              .groupBy(_.SourceField.get.EntityName)
              .foreach { ele =>
                if (ele._2.nonEmpty) {
                  val columnsToBeConverted = ele._2.map(ele => "`" + ele.SourceField.get.FieldName + "`").mkString(", ")
                  val columnExpressions =
                    ele._2
                      .map { fieldCombo =>
                        s"TRIM(`${fieldCombo.SourceField.get.FieldName}`) AS `${fieldCombo.SourceField.get.FieldName}`"
                      }
                      .mkString(", ")
                  NuoLogger.printInfo(s"I am going to convert the following columns: $columnsToBeConverted to trimmed value.")
                  NuoBqClient.executeQueryJob(
                    querySql = s"SELECT * EXCEPT($columnsToBeConverted), $columnExpressions FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele._1}`",
                    targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                    targetTableName = ele._1,
                    pollingIntervalMillis = 1000
                  )
                }
              }
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = s"Successfully renamed columns.")

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.GridToDateColumn) =>

            nuoApiRequest.Body
              .NuoGridFeatureOptions.get
              .NuoFieldCombinations
              .filter(ele => ele.TargetField.isEmpty || ele.TargetField.get.EntityName.trim.isEmpty || ele.SourceField.get.EntityName.equalsIgnoreCase(ele.TargetField.get.EntityName))
              .groupBy(_.SourceField.get.EntityName)
              .foreach { ele =>
                if (ele._2.nonEmpty) {
                  val columnsToBeConverted = ele._2.map(ele => "`" + ele.SourceField.get.FieldName + "`").mkString(", ")
                  val columnExpressions =
                    ele._2
                      .map { fieldCombo =>
                        s"PARSE_DATE('%Y%m',SAFE_CAST(`${fieldCombo.SourceField.get.FieldName}` AS STRING)) AS `${fieldCombo.SourceField.get.FieldName}`"
                      }
                      .mkString(", ")
                  NuoLogger.printInfo(s"I am going to convert the following columns: $columnsToBeConverted to Date.")
                  NuoBqClient.executeQueryJob(
                    querySql = s"SELECT * EXCEPT($columnsToBeConverted), $columnExpressions FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele._1}`",
                    targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                    targetTableName = ele._1,
                    pollingIntervalMillis = 1000
                  )
                  var targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId
                  var targetTableName = ele._1
                  val profilingTableName = targetTableName + "_profiling"
                  val patternTableName = targetTableName + "_pattern"
                  NuoDML.profileResults(
                    resultDatasetName = targetDatasetName,
                    resultTableName = targetTableName,
                    profilingTableName = profilingTableName,
                    patternTableName = patternTableName,
                    nuoEvaEnglishListener = null
                  )
                }
              }
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = s"Successfully renamed columns.")

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.GridSearchAndReplace) =>

            val searchTerm =
              nuoApiRequest
                .Body
                .NuoGridFeatureOptions.get
                .SearchTerm.get

            val replaceTerm =
              nuoApiRequest
                .Body
                .NuoGridFeatureOptions.get
                .ReplaceTerm.getOrElse("")

            nuoApiRequest
              .Body
              .NuoGridFeatureOptions.get
              .NuoFieldCombinations
              .filter(ele => ele.TargetField.isEmpty || ele.TargetField.get.EntityName.trim.isEmpty || ele.SourceField.get.EntityName.equalsIgnoreCase(ele.TargetField.get.EntityName))
              .groupBy(_.SourceField.get.EntityName)
              .foreach { ele =>
                if (ele._2.nonEmpty) {
                  val columnsToBeUpdated = ele._2.map(ele => "`" + ele.SourceField.get.FieldName + "`").mkString(", ")
                  val columnExpressions =
                    ele._2
                      .map { fieldCombo =>
                        s"REPLACE(`${fieldCombo.SourceField.get.FieldName}`,'$searchTerm','$replaceTerm') AS `${fieldCombo.SourceField.get.FieldName}`"
                      }
                      .mkString(", ")
                  NuoLogger.printInfo(s"I am going to replace $searchTerm with $replaceTerm in the following columns: $columnsToBeUpdated.")
                  NuoBqClient.executeQueryJob(
                    querySql = s"SELECT * EXCEPT($columnsToBeUpdated), $columnExpressions FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele._1}`",
                    targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                    targetTableName = ele._1,
                    pollingIntervalMillis = 1000
                  )
                  val targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId
                  val targetTableName = ele._1
                  val profilingTableName = targetTableName + "_profiling"
                  val patternTableName = targetTableName + "_pattern"
                  NuoDML.profileResults(
                    resultDatasetName = targetDatasetName,
                    resultTableName = targetTableName,
                    profilingTableName = profilingTableName,
                    patternTableName = patternTableName,
                    nuoEvaEnglishListener = null
                  )
                }
              }
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = s"Successfully renamed columns.")

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.GridMerge) =>

            val searchTerm =
              nuoApiRequest
                .Body
                .NuoGridFeatureOptions.get
                .SearchTerm.get

            nuoApiRequest.Body
              .NuoGridFeatureOptions.get
              .NuoFieldCombinations
              .filter(ele => ele.TargetField.isEmpty || ele.TargetField.get.EntityName.trim.isEmpty || ele.SourceField.get.EntityName.equalsIgnoreCase(ele.TargetField.get.EntityName))
              .groupBy(_.SourceField.get.EntityName)
              .foreach { ele =>
                if (ele._2.nonEmpty) {
                  val columnsToBeMerged = ele._2.map(ele => "`" + ele.SourceField.get.FieldName + "`").mkString(", ")
                  val columnExpressions =
                    ele._2
                      .filter(_.SourceField.get.DataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.String))
                      .groupBy(_.TargetField.get)
                      .map { grpPair =>

                        val columnsToBeConcatenated =
                          grpPair._2
                            .map(childFieldCombo => "IFNULL(`" + childFieldCombo.SourceField.get.FieldName + "`,'')")
                            .mkString(s",'$searchTerm', ")
                        s"CONCAT($columnsToBeConcatenated) AS `${grpPair._1.FieldName}`"
                      }
                      .mkString(", ")

                  NuoLogger.printInfo(s"I am going to merge the following columns: $columnsToBeMerged.")

                  NuoBqClient.executeQueryJob(
                    querySql = s"SELECT * EXCEPT($columnsToBeMerged), $columnExpressions FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele._1}`",
                    targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                    targetTableName = ele._1,
                    pollingIntervalMillis = 1000
                  )
                  var targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId
                  var targetTableName = ele._1
                  val profilingTableName = targetTableName + "_profiling"
                  val patternTableName = targetTableName + "_pattern"
                  NuoDML.profileResults(
                    resultDatasetName = targetDatasetName,
                    resultTableName = targetTableName,
                    profilingTableName = profilingTableName,
                    patternTableName = patternTableName,
                    nuoEvaEnglishListener = null
                  )
                }
              }
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = s"Successfully renamed columns.")

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.GridSplit) =>

            val searchTerm =
              nuoApiRequest
                .Body
                .NuoGridFeatureOptions.get
                .SearchTerm.get

            nuoApiRequest.Body
              .NuoGridFeatureOptions.get
              .NuoFieldCombinations
              .filter(ele => ele.TargetField.isEmpty || ele.TargetField.get.EntityName.trim.isEmpty || ele.SourceField.get.EntityName.equalsIgnoreCase(ele.TargetField.get.EntityName))
              .groupBy(_.SourceField.get.EntityName)
              .foreach { ele =>
                if (ele._2.nonEmpty) {
                  val columnsToBeSplit = ele._2.map(ele => "`" + ele.SourceField.get.FieldName + "`").mkString(", ")
                  val columnExpressions =
                    ele._2
                      .filter(_.SourceField.get.DataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.String))
                      .map { fieldCombo =>

                        val maxSplitArrayLengthQuery = new mutable.StringBuilder("SELECT")
                        maxSplitArrayLengthQuery.append(s" MAX(ARRAY_LENGTH(SPLIT(`${fieldCombo.SourceField.get.FieldName}`,'$searchTerm')))")
                        maxSplitArrayLengthQuery.append(s" FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele._1}`")

                        val maxSplitArrayLengthResult = NuoBqClient.executeDMLAndGetResult(maxSplitArrayLengthQuery.toString(), 1000)

                        if (maxSplitArrayLengthResult.nonEmpty && maxSplitArrayLengthResult.head.nonEmpty) {
                          var splitExpressions = mutable.ArrayBuffer[String]()
                          for (index <- 0 until maxSplitArrayLengthResult.head.head.toInt) {

                            val colName = "`" + fieldCombo.SourceField.get.FieldName + "`"

                            splitExpressions += s"CASE WHEN ARRAY_LENGTH(SPLIT($colName,'$searchTerm')) > $index THEN SPLIT($colName,'$searchTerm')[OFFSET($index)] ELSE NULL END AS `${fieldCombo.SourceField.get.FieldName}_${index + 1}`"
                          }
                          splitExpressions.mkString(", ")
                        } else null
                      }
                      .filter(exp => exp != null && exp.trim.nonEmpty)
                      .mkString(", ")

                  NuoLogger.printInfo(s"I am going to split the following columns: $columnsToBeSplit.")

                  NuoBqClient.executeQueryJob(
                    querySql = s"SELECT *, $columnExpressions FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele._1}`",
                    targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                    targetTableName = ele._1,
                    pollingIntervalMillis = 1000
                  )
                  var targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId
                  var targetTableName = ele._1
                  val profilingTableName = targetTableName + "_profiling"
                  val patternTableName = targetTableName + "_pattern"
                  NuoDML.profileResults(
                    resultDatasetName = targetDatasetName,
                    resultTableName = targetTableName,
                    profilingTableName = profilingTableName,
                    patternTableName = patternTableName,
                    nuoEvaEnglishListener = null
                  )
                }
              }
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = s"Successfully renamed columns.")

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.GridColumnsToRows) =>

            nuoApiRequest
              .Body
              .NuoGridFeatureOptions.get
              .NuoFieldCombinations
              .filter(ele => ele.TargetField.isEmpty || ele.TargetField.get.EntityName.trim.isEmpty || ele.SourceField.get.EntityName.equalsIgnoreCase(ele.TargetField.get.EntityName))
              .groupBy(_.SourceField.get.EntityName)
              .foreach { ele =>
                if (ele._2.nonEmpty) {
                  ele._2
                    //                    .filter(_.SourceField.get.DataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.String))
                    .groupBy(_.TargetField.get.EntityName)
                    .foreach { fieldCombos =>

                      val columnsToBeMerged = ele._2.map(ele => "`" + ele.SourceField.get.FieldName + "`").mkString(", ")
                      val columnsToRowsQuery =
                        fieldCombos._2
                          .map { childFieldCombo =>
                            val valueColName = "`" + childFieldCombo.ValueField.get.FieldName + "`"
                            val sourceColName = "`" + childFieldCombo.SourceField.get.FieldName + "`"
                            val targetColName = "`" + childFieldCombo.TargetField.get.FieldName + "`"
                            s"SELECT * EXCEPT($columnsToBeMerged)," +
                              s" $sourceColName AS $valueColName, '" +
                              childFieldCombo.SourceField.get.FieldName.replaceAll("\\W+", "_") +
                              s"' AS $targetColName " +
                              s" FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele._1}`"
                          }
                          .mkString(" UNION ALL ")
                      NuoLogger.printInfo(s"I am going to convert the following columns: $columnsToBeMerged to rows.")
                      NuoBqClient.executeQueryJob(
                        querySql = columnsToRowsQuery,
                        targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                        targetTableName = ele._1,
                        pollingIntervalMillis = 1000
                      )
                    }
                }
                var targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId
                var targetTableName = ele._1
                val profilingTableName = targetTableName + "_profiling"
                val patternTableName = targetTableName + "_pattern"
                NuoDML.profileResults(
                  resultDatasetName = targetDatasetName,
                  resultTableName = targetTableName,
                  profilingTableName = profilingTableName,
                  patternTableName = patternTableName,
                  nuoEvaEnglishListener = null
                )
              }
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = s"Successfully renamed columns.")

          case x
            if x.equalsIgnoreCase(metadata.NuoRequestMetadata.RequestType.GridRowsToColumns) =>

            nuoApiRequest
              .Body
              .NuoGridFeatureOptions.get
              .NuoFieldCombinations
              .filter(ele => ele.TargetField.isEmpty || ele.TargetField.get.EntityName.trim.isEmpty || ele.SourceField.get.EntityName.equalsIgnoreCase(ele.TargetField.get.EntityName))
              .groupBy(_.SourceField.get.EntityName)
              .foreach { ele =>
                if (ele._2.nonEmpty) {
                  ele._2
                    //                    .filter(_.SourceField.get.DataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.String))
                    .groupBy(_.TargetField.get.EntityName)
                    .foreach { fieldCombos =>

                      fieldCombos._2
                        .map { childFieldCombo =>
                          val valueColName = "`" + childFieldCombo.ValueField.get.FieldName + "`"
                          val sourceColName = "`" + childFieldCombo.SourceField.get.FieldName + "`"
                          //                          val targetColName = "`" + childFieldCombo.TargetField.get.FieldName + "`"

                          val uniqueSourceValues = NuoBqClient.executeDMLAndGetResult(s"SELECT DISTINCT $sourceColName FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele._1}`", 1000)
                          if (uniqueSourceValues.nonEmpty && uniqueSourceValues.forall(_.head.nonEmpty)) {
                            val rowsToColumnsExpressions =
                              uniqueSourceValues
                                .map(_.head)
                                .map { uniqueSourceValue =>

                                  s" MAX(CASE WHEN $sourceColName = '$uniqueSourceValue' THEN $valueColName ELSE NULL END) AS " +
                                    uniqueSourceValue.replaceAll("\\W+", "_")
                                }
                                .mkString(", ")
                            NuoLogger.printInfo(s"I am going to convert the rows with values in : $sourceColName to columns.")
                            var columnCount =
                              NuoBqClient
                                .getTableSchema(NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                                  ele._1).length
                            val rowsToColumnsQuery =
                              s"SELECT * EXCEPT($sourceColName, $valueColName), " +
                                rowsToColumnsExpressions +
                                s" FROM `${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele._1}`" +
                                " GROUP BY " + (1 until columnCount - 1).mkString(", ")

                            NuoBqClient.executeQueryJob(
                              querySql = rowsToColumnsQuery,
                              targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                              targetTableName = ele._1,
                              pollingIntervalMillis = 1000
                            )
                          }
                        }
                    }
                }
                var targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId
                var targetTableName = ele._1
                val profilingTableName = targetTableName + "_profiling"
                val patternTableName = targetTableName + "_pattern"
                NuoDML.profileResults(
                  resultDatasetName = targetDatasetName,
                  resultTableName = targetTableName,
                  profilingTableName = profilingTableName,
                  patternTableName = patternTableName,
                  nuoEvaEnglishListener = null
                )
              }
            getMessageResponsePlain(
              statusCode = 200,
              status = "OK",
              message = s"Successfully renamed columns.")

          case _ =>
            getMessageResponsePlain(statusCode = 400, status = "Bad Request", message = "Sorry, I could not understand the requested operation!")
        }
      }

    }
  }

  def authenticateCredentials(username: String, authenticationResult: (Int, String, String), isCrmLogin: Boolean): String = {
    if (authenticationResult._1 == 200) {

      val tenantId = authenticationResult._2
      val username = authenticationResult._3
      val sessionId = NuoSecurityClient.generateSalt(16)

      NuoEvaEnglishListener.initNuoTenantDetails(
        "Internal_" + System.currentTimeMillis(),
        tenantId,
        username,
        null
      )

      NuoLogger.printInfo(sessionId)
      createSession(
        sessionId = sessionId,
        username = username,
        tenantId = tenantId,
        sourceIp = "",
        userAgent = "",
        googleMapApiKey = if (isCrmLogin) NuoEvaEnglishListener.GoogleMapApiKey else ""
      )
      val bufferedSource =
        if (isCrmLogin)
          if (!isLocalRun)
            Source.fromFile("crm/crm-min.js")
          else
            Source.fromFile("src/main/js/crm/crm-min.js")
        else null

      implicit val formats = DefaultFormats
      val clientScript =
        if (isCrmLogin)
          net.liftweb.json.Serialization
            .write(s"${NuoEvaEnglishListener.GoogleMapApiKey} activeUsername='${username.capitalize}'; sessionId='$sessionId'; " + bufferedSource.getLines.mkString(" "))
        else
          net.liftweb.json.Serialization
            .write(s"profileImageUrl = '${evaDownloadFileUrl("ProfileImage/Latest")}'; activeUsername = '${username.capitalize}'; sessionId='$sessionId'; ")
      if (bufferedSource != null)
        bufferedSource.close()

      getMessageResponsePlain(statusCode = 200, status = "OK", message = clientScript.substring(1, clientScript.length - 1))
    } else {
      getMessageResponsePlain(statusCode = 401, status = "Unauthorized", message = authenticationResult._2)
    }
  }

  def getMessageResponsePlain(statusCode: Int,
                              status: String,
                              message: String): String = {

    "{" +
      "\"StatusCode\":" + statusCode + "," +
      "\"Status\":\"" + status + "\"," +
      "\"Content\":\"" + message + "\"" +
      "}"
  }

  def getMessageResponseRef(statusCode: Int,
                            status: String,
                            messageRef: AnyRef): String = {

    implicit val formats = DefaultFormats

    "{" +
      "\"StatusCode\":" + statusCode + "," +
      "\"Status\":\"" + status + "\"," +
      "\"Content\":" + net.liftweb.json.Serialization.write(messageRef) +
      "}"
  }


  //  def getApiRequestHeader(nuoApiRequest: NuoApiRequest,
  //                          headerName: String): String = {
  //    nuoApiRequest.Headers
  //      .find(_.Name.equalsIgnoreCase(headerName))
  //      .getOrElse(NuoRequestMetadata.NuoApiHeader("", ""))
  //      .Value
  //  }

  def getCrmUserMetadata: String = {
    NuoS3Client
      .readFromS3(NuoEvaEnglishListener.BucketName.MasterBucketName,
        NuoEvaEnglishListener.NuoCrmUsersMetadataFileName)
  }

  def saveCrmUserMetadata(userMetadata: String): String = {
    NuoS3Client
      .writeToS3(NuoEvaEnglishListener.BucketName.MasterBucketName,
        NuoEvaEnglishListener.NuoCrmUsersMetadataFileName,
        userMetadata + " ",
        appendMode = false)
    "OK_200"
  }

  def crmUploadFileUrl(fileName: String,
                       contentType: String): String = {

    NuoS3Client.getPreSignedUrl(NuoEvaEnglishListener.BucketName.MasterBucketName, fileName, contentType, HttpMethod.PUT)
  }

  def crmDownloadFileUrl(fileName: String): String = {

    NuoS3Client.getPreSignedUrl(NuoEvaEnglishListener.BucketName.MasterBucketName, fileName, null, HttpMethod.GET)
  }

  def crmDeleteFile(fileName: String): String = {

    NuoS3Client.deleteS3File(
      NuoEvaEnglishListener.BucketName.MasterBucketName,
      fileName
    )
    s"File deleted successfully"
  }

  def listNuoEvaFiles: List[NuoEvaFile] = {

    NuoGcsClient
      .listGcsObject(
        NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName,
        NuoEvaEnglishListener.nuoTenantDetails.tenantFilePrefix,
        null
      )
      .map { gcsObj =>
        NuoEvaFile(
          fileName = gcsObj.getName.substring(NuoEvaEnglishListener.nuoTenantDetails.tenantFilePrefix.length),
          sizeBytes = gcsObj.getSize.longValueExact(),
          dateCreatedMillis = gcsObj.getTimeCreated.getValue
        )
      }
  }

  def evaUploadFileUrl(fileName: String,
                       contentType: String): String = {

    NuoGcsClient.getPreSignedUrl(NuoEvaEnglishListener.BucketName.MasterBucketName, NuoEvaEnglishListener.nuoTenantDetails.tenantFilePrefix + fileName, contentType, "PUT")
  }


  def evaLoadFileToTable(nuoFileLoadOptions: NuoFileLoadOptions,
                         paramNuoGrammarListener: NuoEvaEnglishListener,
                         nuoTenantDetails: NuoTenantDetails): String = {

    val targetTableName: String = nuoFileLoadOptions.TargetTableName
    val sourceFiles: List[String] = nuoFileLoadOptions.SourceFiles
    val fileFormat: String = nuoFileLoadOptions.FileFormat
    val delimiter: Option[String] = nuoFileLoadOptions.Delimiter
    val quoteCharacter: Option[String] = nuoFileLoadOptions.QuoteCharacter
    val rowsToSkip: Int = nuoFileLoadOptions.RowsToSkip
    val shouldAppend: Boolean = nuoFileLoadOptions.ShouldAppend

    val nuoEvaEnglishListener =
      if (paramNuoGrammarListener != null)
        paramNuoGrammarListener
      else
        new NuoEvaEnglishListener(
          analysisId = nuoFileLoadOptions.AnalysisId,
          selectionText = "",
          filterText = "",
          nuoUserMessage = null,
          nuoTenantDetails = nuoTenantDetails
        )

    var updDelimiter = ","

    val isDelimitedFile: Boolean = fileFormat match {

      case NuoModifier.FileFormat.TSV =>
        updDelimiter = "\\t"
        true

      case NuoModifier.FileFormat.CSV =>
        true

      case NuoModifier.FileFormat.DELIMITED =>
        if (delimiter.isDefined)
          updDelimiter = delimiter.get
        true

      case NuoModifier.FileFormat.NLD_JSON =>
        false

      case _ =>
        NuoRequestHandler.reportErrorToUser(new Exception(s"I cannot load the file with format $fileFormat."))
        throw new Exception("Unreachable Code Statement")
    }

    var bqJobId = if (isDelimitedFile) {

      NuoBqClient.loadDelimitedNuoFilesAutoSchema(
        sourceFileList = sourceFiles.map(NuoEvaEnglishListener.nuoTenantDetails.tenantFilePrefix + _),
        datasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
        tableName = targetTableName,
        append = shouldAppend,
        delimiter = updDelimiter,
        quoteCharacter = if (quoteCharacter.isDefined && quoteCharacter.get.trim.nonEmpty) quoteCharacter.get.trim else null,
        leadingRowsToSkip = rowsToSkip
      )
    } else {
      NuoBqClient.loadNewLineDelimJsonNuoFiles(
        sourceFileList = sourceFiles.map(NuoEvaEnglishListener.nuoTenantDetails.tenantFilePrefix + _),
        datasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
        tableName = targetTableName,
        append = shouldAppend
      )
    }

    if (!bqJobId.equals("-1")) {

      while (!NuoBqClient.isBQJobComplete(bqJobId)) {
        Thread.sleep(5000)
      }
      getMessageResponseRef(statusCode = 200,
        status = "OK",
        messageRef =
          NuoQueryResponse(
            NuoEvaMessage = NuoEvaMessage(
              AnalysisId = nuoEvaEnglishListener.currAnalysisRecognitionData.AnalysisId,
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
            ProfilingResult = None,
            Pattern = None
          )
      )

    } else {
      NuoRequestHandler.reportErrorToUser(new Exception("I encountered an error while starting the query job."))
      throw new Exception("Unreachable Code Statement")
    }
  }

  def evaDownloadFileUrl(fileName: String): String = {

    val fullyQualifiedFileName = NuoEvaEnglishListener.nuoTenantDetails.tenantFilePrefix + fileName
    try {
      NuoEvaEnglishListener.nuoTenantDetails.storageClient.objects().get(NuoEvaEnglishListener.BucketName.MasterBucketName, fullyQualifiedFileName).execute()
      NuoGcsClient.getPreSignedUrl(NuoEvaEnglishListener.BucketName.MasterBucketName, fullyQualifiedFileName, null, "GET")
    } catch {
      case e: GoogleJsonResponseException if e.getStatusCode == 404 =>
        ""
    }
  }

  def evaDeleteFile(fileName: String): String = {

    NuoGcsClient.deleteObject(
      NuoEvaEnglishListener.BucketName.MasterBucketName,
      NuoEvaEnglishListener.nuoTenantDetails.tenantFilePrefix + fileName
    )
    s"File deleted successfully"
  }

  def saveToHistory(nuoHistoryPaginationRequest: Option[NuoHistoryPaginationRequest]): String = {


    if (nuoHistoryPaginationRequest == null
      || nuoHistoryPaginationRequest.isEmpty
      || nuoHistoryPaginationRequest.get.NuoHistoryRows == null
      || nuoHistoryPaginationRequest.get.NuoHistoryRows.isEmpty) {

      getMessageResponsePlain(statusCode = 400, status = "Bad Request", message = "Bad request for history save.")
    } else {

      nuoHistoryPaginationRequest.get
        .NuoHistoryRows.foreach { row =>

        NuoJdbcClient.insertElseUpdateMetadataTable(
          tableName = NuoEvaEnglishListener.nuoTenantDetails.MetadataTable.nuoHistoryFeed,
          metadata = row.Metadata,
          values = row.Data.map(ele => if (ele != null) ele.replace("'", "''") else ele),
          condition = s" AnalysisId = '${row.Data.head}'"
        )
      }
      getMessageResponsePlain(statusCode = 200, status = "OK", message = "OK")
    }
  }

  def readFromHistory(nuoHistoryPaginationRequest: Option[NuoHistoryPaginationRequest]): String = {


    if (nuoHistoryPaginationRequest.isEmpty) {
      getMessageResponsePlain(statusCode = 400, status = "Bad Request", message = "Bad request for history read.")
    } else {

      getMessageResponseRef(statusCode = 200,
        status = "OK",
        messageRef = NuoJdbcClient
          .executeMetadataQuery(
            s"SELECT  AnalysisId, CreatedAt, LastModifiedAt, Author, Title, Selection, SelectionRaw, Filter, FilterRaw,ResultTableName, AliasMapping, Result, ProfilingResult, Pattern, Visualization" +
              s" FROM ${NuoEvaEnglishListener.nuoTenantDetails.MetadataTable.nuoHistoryFeed}" + {
              if (nuoHistoryPaginationRequest.get.EarliestTimestamp.isDefined)
                s" WHERE LastModifiedAt <= ${nuoHistoryPaginationRequest.get.EarliestTimestamp.get}"
              else
                " "
            } +
              s" ORDER BY LastModifiedAt DESC" +
              s" LIMIT ${NuoEvaEnglishListener.MaxMessageCountPerHistoryPage}"
          )
      )
    }
  }

  def performAnalysis(nuoUserMessage: NuoUserMessage,
                      paramNuoGrammarListener: NuoEvaEnglishListener,
                      profileResults: Boolean,
                      nuoTenantDetails: NuoTenantDetails): String = {

    val nuoEvaEnglishListener =
      if (paramNuoGrammarListener != null)
        paramNuoGrammarListener
      else
        new NuoEvaEnglishListener(
          analysisId = nuoUserMessage.AnalysisId,
          selectionText = nuoUserMessage.Selection.get,
          filterText = if (nuoUserMessage.Filter.isDefined && nuoUserMessage.Filter.get.trim.nonEmpty) "WITH " + nuoUserMessage.Filter.get else "",
          nuoUserMessage = nuoUserMessage,
          nuoTenantDetails = nuoTenantDetails)

    try {
      NuoQuestionParser.parseQuestion(requestTokenAndTree = true, nuoEvaEnglishListener)
    } catch {
      case e: Exception =>
        if (e.getMessage != null && e.getMessage.equalsIgnoreCase("UserInputRequested")) {
          throw e
        } else {
          e.printStackTrace()

          NuoRequestHandler.reportErrorToUser(new Exception(s"Sorry, I could not understand your question. Would you please try to rephrase it?"))
          throw new Exception("Unreachable Code Statement")
        }
    }

    if (nuoEvaEnglishListener.currAnalysisRecognitionData.AnalysisType == QuestionType.QUESTION_TYPE_SQL) {

      var sql = nuoEvaEnglishListener.currAnalysisRecognitionData.Sql
      //      var sql = s"SELECT Type, COUNT(Transaction_Number), AVG(Amount) FROM `geometric-watch-153714`.`tenant1___default`.`Transaction` GROUP BY 1"
      if (sql == null || sql.trim.isEmpty) {
        NuoRequestHandler.reportErrorToUser(new Exception("Sorry, No SQL has been generated for the given question."))
        throw new Exception("Unreachable Code Statement")
      } else {

        val (targetDatasetName, targetTableName) =
          if (nuoUserMessage.ResultTableName.isDefined
            && nuoUserMessage.ResultTableName.get.trim.nonEmpty
            && !List("null", "no result table").contains(nuoUserMessage.ResultTableName.get.trim.toLowerCase)) {
            (NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId, nuoUserMessage.ResultTableName.get)
          } else {
            sql += " LIMIT 250"
            (NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId, getMd5Hash(sql))
          }
        val profilingTableName = targetTableName + "_profiling"
        val patternTableName = targetTableName + "_pattern"
        var profilingComplete = false
        NuoLogger.printInfo(s"Result table $targetTableName")

        var cachingEnabled = false
        if (cachingEnabled && NuoStorage.doesBqTableExist(targetDatasetName, targetTableName)) {

          NuoLogger.printInfo(s"Caching enabled, target table already exists $targetTableName")

        } else {
          NuoBqClient.executeQueryJob(
            querySql = sql,
            targetDatasetName = targetDatasetName,
            targetTableName = targetTableName,
            pollingIntervalMillis = 1000
          )
        }
        if (profileResults) {
          if (NuoStorage.doesBqTableExist(NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId, profilingTableName)) {

            NuoLogger.printInfo(s"Profiling table already exists for $targetTableName")
          } else {

            NuoLogger.printInfo(s"Profiling has been requested. I am going to profile the $targetTableName")
            NuoDML.profileResults(
              resultDatasetName = targetDatasetName,
              resultTableName = targetTableName,
              profilingTableName = profilingTableName,
              patternTableName = patternTableName,
              nuoEvaEnglishListener = null
            )
          }
          profilingComplete = true
        }

        getMessageResponseRef(statusCode = 200,
          status = "OK",
          messageRef =
            NuoQueryResponse(
              NuoEvaMessage = NuoEvaMessage(
                AnalysisId = nuoEvaEnglishListener.currAnalysisRecognitionData.AnalysisId,
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
                    .getTableSchema(targetDatasetName,
                      targetTableName)
                    .map(pair => NuoField("", targetTableName, pair._1, pair._2))
                    .toList,
                  Data = NuoBqClient
                    .listTableData(
                      datasetName = targetDatasetName,
                      tableName = targetTableName,
                      pageToken = null,
                      pageSize = 250L)
                    ._2
                    .map(_.toList).toList)),
              ProfilingResult =
                if (profilingComplete) {
                  Some(NuoDataGrid(
                    Metadata = NuoBqClient
                      .getTableSchema(NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
                        profilingTableName)
                      .map(pair => NuoField("", profilingTableName, pair._1, pair._2))
                      .toList,
                    Data = NuoBqClient
                      .listTableData(
                        datasetName = NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
                        tableName = profilingTableName,
                        pageToken = null,
                        pageSize = 100L)
                      ._2
                      .map(_.toList).toList))
                } else
                  None,
              Pattern =
                if (profilingComplete) {
                  Some(NuoDataGrid(
                    Metadata = NuoBqClient
                      .getTableSchema(NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
                        patternTableName)
                      .map(pair => NuoField("", patternTableName, pair._1, pair._2))
                      .toList,
                    Data = NuoBqClient
                      .listTableData(
                        datasetName = NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
                        tableName = patternTableName,
                        pageToken = null,
                        pageSize = 1000L)
                      ._2
                      .map(_.toList).toList))
                } else
                  None
            )
        )
      }

    } else if (nuoEvaEnglishListener.currAnalysisRecognitionData.AnalysisType == QuestionType.QUESTION_TYPE_ML_LEARN
      || nuoEvaEnglishListener.currAnalysisRecognitionData.AnalysisType == QuestionType.QUESTION_TYPE_ML_PREDICT) {

      NuoMLaaS.prepareAndExecuteModel(nuoUserMessage, nuoEvaEnglishListener)
    } else {
      NuoRequestHandler.reportErrorToUser(new Exception(s"Sorry, I don't know about the question type ${nuoEvaEnglishListener.currAnalysisRecognitionData.AnalysisType}."))
      throw new Exception("Unreachable Code Statement")
    }
  }

  def previewTable(targetTableName: String,
                   profileResults: Boolean): String = {

    var targetDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId
    val profilingTableName = targetTableName + "_profiling"
    val patternTableName = targetTableName + "_pattern"

    if (profileResults) {

      if (NuoStorage.doesBqTableExist(NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId, profilingTableName)) {

        NuoLogger.printInfo(s"Profiling table already exists for $targetTableName")
      } else {

        NuoLogger.printInfo(s"Profiling has been requested. I am going to profile the $targetTableName")
        NuoDML.profileResults(
          resultDatasetName = targetDatasetName,
          resultTableName = targetTableName,
          profilingTableName = profilingTableName,
          patternTableName = patternTableName,
          nuoEvaEnglishListener = null
        )
      }
    }
    getMessageResponseRef(statusCode = 200,
      status = "OK",
      messageRef =
        NuoQueryResponse(
          NuoEvaMessage = NuoEvaMessage(
            AnalysisId = "TableExplorerRequest",
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
                .getTableSchema(targetDatasetName,
                  targetTableName)
                .map(pair => NuoField("", targetTableName, pair._1, pair._2))
                .toList,
              Data = NuoBqClient
                .listTableData(
                  datasetName = targetDatasetName,
                  tableName = targetTableName,
                  pageToken = null,
                  pageSize = 100L)
                ._2
                .map(_.toList).toList)),
          ProfilingResult =
            if (profileResults)
              Some(NuoDataGrid(
                Metadata = NuoBqClient
                  .getTableSchema(NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
                    profilingTableName)
                  .map(pair => NuoField("", profilingTableName, pair._1, pair._2))
                  .toList,
                Data = NuoBqClient
                  .listTableData(
                    datasetName = NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
                    tableName = profilingTableName,
                    pageToken = null,
                    pageSize = 100L)
                  ._2
                  .map(_.toList).toList))
            else None,
          Pattern =
            if (profileResults)
              Some(NuoDataGrid(
                Metadata = NuoBqClient
                  .getTableSchema(NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
                    patternTableName)
                  .map(pair => NuoField("", patternTableName, pair._1, pair._2))
                  .toList,
                Data = NuoBqClient
                  .listTableData(
                    datasetName = NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
                    tableName = patternTableName,
                    pageToken = null,
                    pageSize = 100L)
                  ._2
                  .map(_.toList).toList))
            else None
        )
    )
  }

  def sendExecutionRunningMessage(nuoFileLoadOptions: Option[NuoFileLoadOptions],
                                  nuoAnalyzeImageOptions: Option[NuoAnalyzeImageOptions],
                                  nuoPollingDetails: Option[NuoPollingDetails],
                                  nuoEvaEnglishListener: NuoEvaEnglishListener): String = {
    sendNuoEvaMessage(
      analysisId = nuoEvaEnglishListener.currAnalysisRecognitionData.AnalysisId,
      ruleText = "",
      communicationType = NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_EXECUTION_RUNNING,
      evaResponseMessage = "The execution is in progress.",
      leftEntityName = None,
      rightEntityName = None,
      nuoMappingInput = None,
      nuoPollingDetails = nuoPollingDetails,
      nuoGrammarListener = nuoEvaEnglishListener
    )
    "Congratulations! You have reached an unreachable statement"
  }

  def sendExecutionStartedMessage(nuoPollingDetails: Option[NuoPollingDetails],
                                  nuoEvaEnglishListener: NuoEvaEnglishListener, calculateCost: Boolean): String = {

    sendNuoEvaMessage(
      analysisId = nuoEvaEnglishListener.currAnalysisRecognitionData.AnalysisId,
      ruleText = "",
      communicationType = NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_EXECUTION_STARTED,
      evaResponseMessage = "I have started the execution to fetch the results.",
      leftEntityName = None,
      rightEntityName = None,
      nuoMappingInput = None,
      nuoPollingDetails = nuoPollingDetails,
      nuoGrammarListener = nuoEvaEnglishListener
    )
    "Congratulations! You have reached an unreachable statement"
  }

  //  def explainConclusion(nuoUserMessage: NuoUserMessage,
  //                        nuoTenantDetails: NuoTenantDetails): String = {
  //
  //    //    val nuoUserMessage = NuoRequestMetadata.getConversationRequest(nuoUserMessage)
  //
  //    val nuoGrammarListener = new NuoEvaEnglishListener(
  //      isChildQuestion = false,
  //      initConfigurations = true,
  //      questionAlias = nuoUserMessage.QuestionAlias,
  //      questionText = nuoUserMessage.QuestionText,
  //      nuoUserMessage = nuoUserMessage,
  //      nuoTenantDetails = nuoTenantDetails)
  //
  //    if (nuoGrammarListener.currAnalysisRecognitionData.IsRecognitionComplete) {
  //
  //      getMessageResponseRef(statusCode = 200,
  //        status = "OK",
  //        messageRef =
  //          NuoConclusionExplanation(
  //            nuoGrammarListener.currAnalysisRecognitionData.QuestionAlias,
  //            nuoGrammarListener.currAnalysisRecognitionData.QuestionText,
  //            nuoGrammarListener.currAnalysisRecognitionData.SelectFields ++
  //              nuoGrammarListener.currAnalysisRecognitionData.Criteria.flatMap(_.RelatedFields)
  //          )
  //      )
  //
  //    } else {
  //      NuoLogger.printInfo("The recognition is not complete yet!")
  //      getMessageResponsePlain(statusCode = 102, status = "Processing", message = "The recognition is in progress.")
  //    }
  //  }

  //
  //  def updateConclusion(nuoUserMessage: NuoUserMessage,
  //                       nuoTenantDetails: NuoTenantDetails): String = {
  //
  //    //    val nuoUserMessage = NuoRequestMetadata.getConversationRequest(requestBody)
  //
  //    val nuoGrammarListener =
  //      new NuoEvaEnglishListener(
  //        isChildQuestion = false,
  //        initConfigurations = true,
  //        questionAlias = nuoUserMessage.QuestionAlias,
  //        questionText = nuoUserMessage.QuestionText,
  //        nuoUserMessage = nuoUserMessage,
  //        nuoTenantDetails = nuoTenantDetails
  //      )
  //
  //    nuoGrammarListener.currAnalysisRecognitionData.Sql = ""
  //    nuoGrammarListener.currAnalysisRecognitionData.IsRecognitionComplete = false
  //    val userResponse = NuoEvaEnglishListener.nuoUserMessage
  //
  //    val userResponseContent = userResponse.Responses.get.filter(ele => ele != null && ele.trim.nonEmpty)
  //
  //    if (nuoGrammarListener.currAnalysisRecognitionData != null
  //      && userResponse != null
  //      && userResponse.CommunicationType.isDefined
  //      && userResponse.RuleText.isDefined
  //    ) {
  //
  //      val userResponseRuleText = userResponse.RuleText.get
  //      val userResponseInputType = userResponse.CommunicationType.get
  //      val suggestedNuoField = if ( /*userResponseInputType
  //          % NuoRecognitionMetadata
  //          .RecognitionCommunicationType
  //          .COMMUNICATION_TYPE_UPD_FIELD == 0*/
  //        userResponseContent.size == 3) {
  //
  //        Some(NuoField(
  //          DatasetName = "",
  //          EntityName = userResponseContent.head,
  //          FieldName = userResponseContent(1),
  //          DataType = userResponseContent(2)))
  //
  //      } else if ( /*userResponseInputType
  //          % NuoRecognitionMetadata
  //          .RecognitionCommunicationType
  //          .COMMUNICATION_TYPE_UPD_QUESTION == 0*/
  //        userResponseContent.size == 1) {
  //
  //        NuoQuestionRecognizer.getFirstFieldFromChildQuestion(
  //          ruleText = userResponseRuleText,
  //          childQuestionAlias = userResponseRuleText,
  //          childQuestionText = userResponseContent.head,
  //          nuoGrammarListener = nuoGrammarListener)
  //
  //      } else /*if (userResponseInputType
  //          % NuoRecognitionMetadata
  //          .RecognitionCommunicationType
  //          .COMMUNICATION_TYPE_UPD_LITERAL == 0) */ {
  //
  //        Some(NuoField(
  //          DatasetName = "",
  //          EntityName = "",
  //          FieldName = s"'$userResponseRuleText'",
  //          DataType = NuoDataTypeHandler
  //            .NuoDataType
  //            .String))
  //      }
  //
  //      if (suggestedNuoField.isDefined) {
  //
  //        NuoQuestionRecognizer
  //          .updateQuestionRecognizedField(
  //            suggestedNuoField,
  //            nuoGrammarListener)
  //
  //        NuoQuestionRecognizer.recordRecognizedField(
  //          fieldText = userResponseRuleText,
  //          recognizedNuoField = suggestedNuoField,
  //          nuoGrammarListener = nuoGrammarListener)
  //
  //        NuoRecognitionMetadata.writeFieldRecognitionData()
  //      }
  //
  //      performAnalysis(
  //        nuoUserMessage,
  //        nuoGrammarListener,
  //        nuoTenantDetails)
  //    } else
  //      NuoLogger.printInfo("Sorry, there was nothing to update.")
  //    getMessageResponsePlain(statusCode = 400, status = "Bad Request", message = "Bad request for update.")
  //  }

  def sendNuoEvaMessage(analysisId: String,
                        ruleText: String,
                        communicationType: Int,
                        evaResponseMessage: String,
                        leftEntityName: Option[String],
                        rightEntityName: Option[String],
                        nuoMappingInput: Option[NuoMappingInput],
                        nuoPollingDetails: Option[NuoPollingDetails],
                        nuoGrammarListener: NuoEvaEnglishListener): Unit = {

    NuoEvaEnglishListener.nuoEvaMessage =
      NuoRequestMetadata.NuoEvaMessage(
        AnalysisId = nuoGrammarListener.currAnalysisRecognitionData.AnalysisId,
        RuleText = ruleText,
        CommunicationType = communicationType,
        Message = evaResponseMessage + "<br><br>",
        LeftEntityName = leftEntityName,
        RightEntityName = rightEntityName,
        NuoMappingInput = nuoMappingInput,
        NuoPollingDetails = nuoPollingDetails
      )


    //    NuoRecognitionMetadata.writeRecognitionData(nuoGrammarListener)
    throw new Exception("UserInputRequested")

  }

  def reportErrorToUser(exception: Exception): Unit = {
    val errorId = "ERROR_" + System.currentTimeMillis()
    NuoLogger.printInfo(s"exception message=>${exception.getMessage}")
    NuoLogger.printInfo(s"Error Id=>$errorId")
    var updException = new Exception(s" Please contact us at support@nuocanvas.com with ERROR ID: $errorId")
    updException.setStackTrace(exception.getStackTrace)
    throw updException
  }

  def incrementStepsCompleted(nuoEvaEnglishListener: NuoEvaEnglishListener): Unit = {

    val stepsCompleted = nuoEvaEnglishListener
      .currAnalysisRecognitionData
      .StepsCompleted

    if (stepsCompleted.isEmpty) {
      nuoEvaEnglishListener
        .currAnalysisRecognitionData
        .StepsCompleted =
        Some(1)
    } else {
      nuoEvaEnglishListener
        .currAnalysisRecognitionData
        .StepsCompleted =
        Some(stepsCompleted.get + 1)
    }
    NuoLogger.printInfo("Steps completed:" + (stepsCompleted.get + 1))
    nuoEvaEnglishListener
      .currAnalysisRecognitionData
      .EndTimeMillis = Some(System.currentTimeMillis())

  }

  def assignTotalSteps(totalSteps: Int,
                       nuoEvaEnglishListener: NuoEvaEnglishListener): Unit = {

    nuoEvaEnglishListener
      .currAnalysisRecognitionData
      .AnalysisId
    nuoEvaEnglishListener
      .currAnalysisRecognitionData
      .TotalSteps =
      Some(totalSteps)
    nuoEvaEnglishListener
      .currAnalysisRecognitionData
      .EndTimeMillis = Some(System.currentTimeMillis())
  }

  def assignStepsCompleted(stepsCompleted: Int,
                           nuoEvaEnglishListener: NuoEvaEnglishListener): Unit = {

    nuoEvaEnglishListener
      .currAnalysisRecognitionData
      .StepsCompleted =
      Some(stepsCompleted)
    nuoEvaEnglishListener
      .currAnalysisRecognitionData
      .EndTimeMillis = Some(System.currentTimeMillis())
  }

  def removeConsecutiveWhiteSpace(input: String): String = {
    input.replaceAll("[ \\t\\r?\\n]+", " ")
  }

  def getMd5Hash(input: String): String = DigestUtils.md5Hex(removeConsecutiveWhiteSpace(input.trim).toLowerCase)
}
