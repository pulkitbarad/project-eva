package dag

import java.io.ByteArrayInputStream

import client.NuoJdbcClient
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.polly.AmazonPollyClient
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.bigquery.{Bigquery, BigqueryScopes}
import com.google.api.services.storage.{Storage, StorageScopes}
import com.google.api.services.storagetransfer.v1.{Storagetransfer, StoragetransferScopes}
import com.google.api.services.texttospeech.v1.{Texttospeech, TexttospeechScopes}
import com.google.auth.oauth2.ServiceAccountCredentials
import nlp.grammar.NuoEvaEnglishListener

import scala.collection.mutable

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 21Apr2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

class NuoTenantDetails {


  //
  var tenantId: String = _
  var username: String = _
  var requestId: String = _
  var processName: String = _
  var errorEncountered: Boolean = _
  val executionStatusOf = mutable.HashMap[String, Double]()
  val executionStatusRefParamOf = mutable.HashMap[String, Array[String]]()

  val tenantBucketName = s"master-backend-nuocanvas-com"

  def tenantFilePrefix = s"Tenants/${NuoEvaEnglishListener.nuoTenantDetails.tenantId}/"

  def tenantInternalDirPrefix = s"Tenants/$tenantId/Internal/"

  def tableExportDirPrefix = s"${tenantInternalDirPrefix}TableExport/"

  //Default Datasets
  val userCacheDatasetNameWOTenantId = "__cache"
  val userDefaultDatasetNameWOTenantId = "__default"
  val userMetadataDatasetNameWOTenantId = "__metadata"
  val userTempDatasetNameWOTenantId = "__temp"

  def userCacheDatasetName = s"${tenantId}_$userCacheDatasetNameWOTenantId"

  def userDefaultDatasetName = s"${tenantId}_$userDefaultDatasetNameWOTenantId"

  def userMetadataDatasetName = s"${tenantId}_$userMetadataDatasetNameWOTenantId"

  def userTempDatasetName = s"${tenantId}_$userTempDatasetNameWOTenantId"

  //Internal Table Names
  object MetadataTable {
    def nuoHistoryFeed = s"${tenantId}_NuoHistoryFeed"

    def nuoRelationships = s"${tenantId}_NuoRelationships"

    def nuoExecutionMonitor = s"${tenantId}_NuoExecutionMonitor"

    def nuoUsage = s"${tenantId}_NuoUsage"
  }

  def functionStatusTable = "nuo_function_status"

  def usageTable = "nuo_usage"

  def loggingTable = "nuo_logging"

  //Metadata file names
  def NuoFieldRecognitionDataFileName = s"Tenants/$tenantId/RecognitionData/NuoFieldRecognitionData.txt"

  def NuoQuestionRecognitionDataFileName = s"Tenants/$tenantId/RecognitionData/NuoQuestionRecognitionData.txt"

  def NuoRecogStorageDetailsFileName = s"Tenants/$tenantId/RecognitionData/NuoRecogStorageDetails.json"

  def NuoRelationshipsFileName = s"Tenants/$tenantId/RecognitionData/NuoRelationships.txt"

  def NuoAccountDetailsFileName = s"Tenants/$tenantId/Preference/Account_Details.json"

  def CanvasMetadataFileName = s"Tenants/$tenantId/Metadata/Canvas/Canvas_Metadata.json"

  def NuoStorageMetadataFileName: String = s"Tenants/$tenantId/Metadata/NuoStorage/NuoStorage_Metadata.json"

  def NuoHistoryFilePrefix: String = s"Tenants/$tenantId/Metadata/NuoHistory/NuoHistory_"

  val NuoHistoryFileSuffix: String = ".txt"


  private var bqProjectIdRef: String = _

  def bqProjectId: String =
    if (bqProjectIdRef != null)
      bqProjectIdRef
    else {
      bqProjectIdRef = "geometric-watch-153714"
      bqProjectIdRef
    }

  var transport = new NetHttpTransport
  var jsonFactory = new JacksonFactory

  private var googleCredentialRef: GoogleCredential = _

  def gcpCredentials: GoogleCredential =
    if (googleCredentialRef != null)
      googleCredentialRef
    else {
      googleCredentialRef =
        GoogleCredential
          .fromStream(
            new ByteArrayInputStream(
              NuoJdbcClient
                .executeMetadataQuery(
                  s"SELECT GcpCredentials" +
                    s" FROM ${NuoEvaEnglishListener.MetadataTable.nuoTenantDetails}" +
                    s" WHERE UPPER(TenantId) = UPPER('${NuoEvaEnglishListener.nuoTenantDetails.tenantId}')")
                .head.head
                .getBytes))
      googleCredentialRef
    }

  private var googleServiceAccountCredentialRef: ServiceAccountCredentials = _

  def gcpServiceAccountCredentials: ServiceAccountCredentials =
    if (googleServiceAccountCredentialRef != null)
      googleServiceAccountCredentialRef
    else {
      googleServiceAccountCredentialRef =
        ServiceAccountCredentials
          .fromStream(
            new ByteArrayInputStream(
              NuoJdbcClient
                .executeMetadataQuery(
                  s"SELECT GcpCredentials" +
                    s" FROM ${NuoEvaEnglishListener.MetadataTable.nuoTenantDetails}" +
                    s" WHERE UPPER(TenantId) = UPPER('${NuoEvaEnglishListener.nuoTenantDetails.tenantId}')")
                .head.head
                .getBytes))
      googleServiceAccountCredentialRef
    }

  private var amazonPollyClientRef: AmazonPollyClient = _

  def amazonPollyClient =
    if (amazonPollyClientRef != null) amazonPollyClientRef
    else {
      val awsCredentials = new BasicAWSCredentials(NuoEvaEnglishListener.AwsDetails.AccessKey, NuoEvaEnglishListener.AwsDetails.SecretKey)

      amazonPollyClientRef =
        new AmazonPollyClient(awsCredentials)
      amazonPollyClientRef
    }

  private var textToSpeechClientRefGcp: Texttospeech = _

  def textToSpeechClientGcp =
    if (textToSpeechClientRefGcp != null) textToSpeechClientRefGcp
    else {
      textToSpeechClientRefGcp =
        new Texttospeech.Builder(transport,
          jsonFactory,
          if (gcpCredentials.createScopedRequired)
            gcpCredentials.createScoped(BigqueryScopes.all)
          else gcpCredentials)
          .setApplicationName(tenantId)
          .build
      textToSpeechClientRefGcp
    }

  private var bigqueryClientRef: Bigquery = _

  def bigqueryClient =
    if (bigqueryClientRef != null) bigqueryClientRef
    else {


      bigqueryClientRef =
        new Bigquery
        .Builder(transport,
          jsonFactory,
          if (gcpCredentials.createScopedRequired)
            gcpCredentials.createScoped(BigqueryScopes.all)
          else gcpCredentials)
          .setApplicationName(tenantId)
          .build
      bigqueryClientRef
    }

  private var transferClientRef: Storagetransfer = _

  def transferClient =
    if (transferClientRef != null) transferClientRef
    else {

      transferClientRef =
        new Storagetransfer
        .Builder(transport,
          jsonFactory,
          if (gcpCredentials.createScopedRequired)
            gcpCredentials.createScoped(StoragetransferScopes.all)
          else gcpCredentials)
          .setApplicationName(tenantId)
          .build
      transferClientRef
    }

  private var storageClientRef: Storage = _

  def storageClient =
    if (storageClientRef != null) storageClientRef
    else {

      storageClientRef =
        new Storage.Builder(transport,
          jsonFactory,
          if (gcpCredentials.createScopedRequired)
            gcpCredentials.createScoped(StorageScopes.all)
          else gcpCredentials
        ).setApplicationName(tenantId)
          .build
      storageClientRef
    }

  def getTableExportGcsUriPrefix(datasetName: String, tableName: String, prefix: String): String = {
    if (prefix.isEmpty || prefix.trim.isEmpty) {
      s"gs://$tenantBucketName/$tableExportDirPrefix$datasetName/$tableName/*"
    } else {
      s"gs://$tenantBucketName/$prefix/*"
    }
  }

  def this(paramRequestId: String,
           paramTenantId: String,
           paramUsername: String) {

    this()

    this.requestId = paramRequestId
    this.tenantId = paramTenantId
    this.username = paramUsername

  }
}
