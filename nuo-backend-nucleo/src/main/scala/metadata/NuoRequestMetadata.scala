package metadata

import java.util.{Calendar, Date, TimeZone}

import canvas.NuoDataTypeHandler
import client.NuoJdbcClient
import metadata.NuoRecognitionMetadata._
import metadata.StorageMetadata.{NuoEntity, NuoField}
import net.liftweb.json.{DefaultFormats, parse}
import nlp.grammar.NuoEvaEnglishListener

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


object NuoRequestMetadata {


  case class NuoApiRequest(Body: NuoGenericRequest,
                           Headers: Option[List[NuoApiHeader]])

  case class NuoApiHeader(Name: String,
                          Value: String)


  def getNuoApiRequest(content: String): NuoApiRequest = {

    implicit val formats = DefaultFormats

    parse(content).extract[NuoApiRequest]
  }

  case class NuoGenericRequest(RequestId: String,
                               SessionId: Option[String],
                               RequestType: String,
                               NuoCredentialsRequest: Option[NuoCredentialsRequest],
                               NuoFileContent: Option[NuoFileContent],
                               NuoFileContentList: Option[List[NuoFileContent]],
                               NuoEntities: Option[List[NuoEntity]],
                               NuoCrmUserMetadata: Option[String],
                               NuoUserMessage: Option[NuoUserMessage],
                               NuoHistoryPaginationRequest: Option[NuoHistoryPaginationRequest],
                               NuoPlatformMetricsRequest: Option[NuoPlatformMetricsRequest],
                               NuoFileLoadOptions: Option[NuoFileLoadOptions],
                               NuoAnalyzeImageOptions: Option[NuoAnalyzeImageOptions],
                               NuoGridFeatureOptions: Option[NuoGridFeatureOptions],
                               NuoSpeechOptions: Option[NuoSpeechOptions])

  case class NuoSpeechOptions(AnalysisId: String,
                              Ssml: Option[String],
                              LanguageCode: Option[String])

  case class NuoGridFeatureOptions(NuoFieldCombinations: List[NuoFieldCombination],
                                   SearchTerm: Option[String],
                                   ReplaceTerm: Option[String])

  case class NuoFieldCombination(SourceField: Option[NuoField],
                                 TargetField: Option[NuoField],
                                 ValueField: Option[NuoField])

  case class NuoFileLoadOptions(AnalysisId: String,
                                TargetTableName: String,
                                SourceFiles: List[String],
                                FileFormat: String,
                                Delimiter: Option[String],
                                QuoteCharacter: Option[String],
                                ShouldAppend: Boolean,
                                RowsToSkip: Int)

  case class NuoAnalyzeImageOptions(AnalysisId: String,
                                    SourceFiles: Option[List[String]],
                                    LanguageHints: Option[List[String]],
                                    TargetTableName: Option[String],
                                    ShouldAppend: Option[Boolean])

  case class NuoFileContent(FileName: String,
                            ContentType: Option[String],
                            SizeBytes: Option[Long],
                            Content: Option[String])

  case class NuoCredentialsRequest(UserName: String,
                                   Password: String)

  case class NuoHistoryPaginationRequest(EarliestTimestamp: Option[Long],
                                         NuoHistoryRows: List[NuoHistoryRow])

  case class NuoHistoryRow(Metadata: List[NuoField],
                           Data: List[String])

  case class NuoPlatformMetricsRequest(MetricName: String)

  case class NuoConversationRequest(NuoUserMessage: Option[NuoUserMessage])

  case class NuoQueryResponse(NuoEvaMessage: NuoEvaMessage,
                              Result: Option[NuoDataGrid],
                              ProfilingResult: Option[NuoDataGrid],
                              Pattern: Option[NuoDataGrid])

  case class NuoDataGrid(Metadata: List[NuoField],
                         Data: List[List[String]])

  case class NuoEvaMessage(AnalysisId: String,
                           RuleText: String,
                           CommunicationType: Int,
                           Message: String,
                           LeftEntityName: Option[String],
                           RightEntityName: Option[String],
                           NuoMappingInput: Option[NuoMappingInput],
                           NuoPollingDetails: Option[NuoPollingDetails])

  case class NuoPollingDetails(StartTimeMillis: Long,
                               PollingIntervalMillis: Long,
                               EndTimeMillis: Long)

  case class NuoMappingInput(NuoSourceList: List[NuoMappingElement],
                             NuoTargetList: List[NuoMappingElement])

  case class NuoMappingElement(label: String,
                               inputIndex: Int)

  case class NuoRelationshipInput(LeftEntityName: String,
                                  RightEntityName: String,
                                  NuoCommonFields: Option[List[NuoCommonField]])

  case class NuoUserMessage(AnalysisId: String,
                            Selection: Option[String],
                            Filter: Option[String],
                            ResultTableName: Option[String],
                            NuoRelationshipInput: Option[NuoRelationshipInput])

  case class NuoSessionDetails()

  case class NuoEvaFilesResponse(NuoEvaFiles: List[NuoEvaFile])

  case class NuoEvaFile(fileName: String,
                        sizeBytes: Long,
                        dateCreatedMillis: Long)

  case class NuoEvaTablesResponse(NuoEvaTables: List[NuoEntity])

  case class NuoEvaProgressMonitor(NuoQuestionMonitorList: List[NuoQuestionMonitor])

  case class NuoQuestionMonitor(QuestionAlias: String,
                                QuestionText: String,
                                StepsCompleted: Int,
                                TotalSteps: Int,
                                StartTimeMillis: Long,
                                EndTimeMillis: Long)

  def createSession(sessionId: String,
                    username: String,
                    tenantId: String,
                    sourceIp: String,
                    userAgent: String,
                    googleMapApiKey: String): Unit = {

    NuoJdbcClient
      .insertIntoMetadataTable(
        NuoEvaEnglishListener.MetadataTable.nuoSession,
        List(
          NuoField("", "", "sessionId", NuoDataTypeHandler.PostgresDataType.Text),
          NuoField("", "", "username", NuoDataTypeHandler.PostgresDataType.Text),
          NuoField("", "", "tenantId", NuoDataTypeHandler.PostgresDataType.Text),
          NuoField("", "", "createdAt", NuoDataTypeHandler.PostgresDataType.BigInt),
          NuoField("", "", "lastModifiedAt", NuoDataTypeHandler.PostgresDataType.BigInt),
          NuoField("", "", "sourceIp", NuoDataTypeHandler.PostgresDataType.Text),
          NuoField("", "", "userAgent", NuoDataTypeHandler.PostgresDataType.Text),
          NuoField("", "", "googleMapApiKey", NuoDataTypeHandler.PostgresDataType.Text),
          NuoField("", "", "isActive", NuoDataTypeHandler.PostgresDataType.Boolean)
        ),
        List(
          sessionId,
          username,
          tenantId,
          System.currentTimeMillis().toString,
          System.currentTimeMillis().toString,
          sourceIp,
          userAgent,
          googleMapApiKey,
          "true")
      )
  }

  def validateSession(sessionId: String): Boolean = {

    val queryResult = NuoJdbcClient.executeMetadataQuery(
      "SELECT LastModifiedAt, IsActive FROM " +
        NuoEvaEnglishListener.MetadataTable.nuoSession +
        s" WHERE UPPER(sessionId) = UPPER('$sessionId')"
    )

    if (queryResult == null || queryResult.isEmpty
      || queryResult.head == null || queryResult.head.isEmpty)
      false
    else
      queryResult.head(1).toBoolean /*&&
        queryResult.head.head.toLong + NuoEvaEnglishListener.SessionMaxInactiveTimeMillis > System.currentTimeMillis()*/
  }

  def getSessionDetails(sessionId: String): (String, String) = {
    updateSession(sessionId)
    val queryResult = NuoJdbcClient.executeMetadataQuery(
      "SELECT TenantId, Username FROM " +
        NuoEvaEnglishListener.MetadataTable.nuoSession +
        s" WHERE UPPER(sessionId) = UPPER('$sessionId')"
    )
    if (queryResult == null || queryResult.isEmpty
      || queryResult.head == null || queryResult.head.isEmpty)
      null
    else
      (queryResult.head.head, queryResult.head(1))
  }

  def updateSession(sessionId: String): Unit = {

    NuoJdbcClient
      .updateMetadataTable(
        NuoEvaEnglishListener.MetadataTable.nuoSession,
        List(NuoField("", "", "lastModifiedAt", NuoDataTypeHandler.PostgresDataType.BigInt)),
        List(System.currentTimeMillis().toString),
        s"UPPER(sessionId) = UPPER('$sessionId')"
      )
  }

  def endSession(sessionId: String): Unit = {

    NuoJdbcClient
      .updateMetadataTable(
        NuoEvaEnglishListener.MetadataTable.nuoSession,
        List(
          NuoField("", "", "lastModifiedAt", NuoDataTypeHandler.PostgresDataType.BigInt),
          NuoField("", "", "isActive", NuoDataTypeHandler.PostgresDataType.Boolean)
        ),
        List(
          System.currentTimeMillis().toString,
          "false"
        ),
        s"UPPER(sessionId) = UPPER('$sessionId')"
      )
  }

  def latestSessionFolderNameWithSep: String = {
    val date = new Date()
    var calendar = Calendar.getInstance()
    calendar.setTimeZone(TimeZone.getTimeZone("UTC"))
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    f"$year%04d_$month%02d_$day%02d_$hour%02d/"
  }

  def previousSessionFolderNameWithSep: String = {
    val date = new Date()
    var calendar = Calendar.getInstance()
    calendar.setTimeZone(TimeZone.getTimeZone("UTC"))
    var year = calendar.get(Calendar.YEAR)
    var month = calendar.get(Calendar.MONTH) + 1
    var day = calendar.get(Calendar.DAY_OF_MONTH)
    var hour = calendar.get(Calendar.HOUR_OF_DAY)

    hour -= 1
    if (hour < 0) {
      day -= 1
      hour = 24
    }

    if (day < 1) {
      month -= 1
      day = 1
    }
    if (month < 1) {
      year -= 1
      month = 1
    }

    f"$year%04d_$month%02d_$day%02d_$hour%02d/"
  }

  object RequestType {

    // 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107,
    // 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199
    // 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281

    object Crm {
      val GetMapApiKey = "rt2"
      val GetUserMetadata = "rt3"
      val SaveUserMetadata = "rt5"
      val CrmUploadFile = "rt7"
      val CrmDownloadFile = "rt11"
      val CrmDeleteFile = "rt13"
    }

    val AuthenticateCrmCredentials = "rt17"
    val AuthenticateCredentials = "rt19"
    val GetUsername = "rt23"
    val UploadFile = "rt29"
    val DownloadFile = "rt31"
    val SaveToHistory = "rt37"
    val ReadFromHistory = "rt41"
    val AuthenticateClientCredentials = "rt43"
    val PerformAnalysis = "rt47"
    val ExplainConclusion = "rt53"
    val UpdateConclusion = "rt59"
    val ShowPlatformMetric = "rt61"
    val ShowTableMetadata = "rt67"
    val ShowFileMetadata = "rt71"
    val LogOut = "rt73"
    val EvaUploadFile = "rt79"

    val EvaDownloadFile = "rt83"
    val EvaDeleteFile = "rt89"
    val EvaListFiles = "rt97"
    val EvaLoadFileToTable = "rt101"
    val EvaListTables = "rt103"
    val EvaGetTableList = "rt107"
    val EvaDeleteTable = "rt109"
    val EvaGetProgressReport = "rt113"
    val EvaAnalyzeImages = "rt127"

    val ProfileResults = "rt131"

    val GridSearchAndReplace = "rt137"
    val GridMerge = "rt139"
    val GridSplit = "rt149"
    val GridColumnsToRows = "rt151"
    val GridRowsToColumns = "rt157"
    val GridRenameColumn = "rt163"
    val GridDeleteColumn = "rt167"

    val PreviewTable = "rt173"
    val ProfileTable = "rt179"

    val GridTrimColumn = "rt181"
    val GridToDateColumn = "rt191"
    val GridToStringColumn = "rt193"
    val GridToIntegerColumn = "rt197"
    val GridDuplicateColumn = "rt199"

    val EvaDuplicateTable = "rt211"
    val EvaTextToSpeech = "rt223"

    val DeleteFromHistory = "rt227"
  }

}
