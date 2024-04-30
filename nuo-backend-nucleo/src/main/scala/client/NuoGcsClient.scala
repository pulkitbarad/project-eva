package client

import java.io._
import java.net.URLEncoder
import java.util.Base64

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.{ByteArrayContent, InputStreamContent}
import com.google.api.services.storage.model.Bucket.Cors
import com.google.api.services.storage.model._
import dag.NuoTenantDetails
import logging.NuoLogger
import logging.NuoLogger._
import metadata.AccountMetadata
import nlp.grammar.NuoEvaEnglishListener

import scala.collection.JavaConverters._

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 22Apr2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoGcsClient {


  //  def createCustomStorageClient(credentials:String): com.google.api.services.storage.Storage = {
  //    val transport = new NetHttpTransport
  //    val jsonFactory = new JacksonFactory
  //    var credential = GoogleCredential.fromStream(new ByteArrayInputStream(credentials.getBytes()))
  //    if (credential.createScopedRequired) credential = credential.createScoped(BigqueryScopes.all)
  //    new com.google.api.services.storage.Storage.Builder(transport, jsonFactory, credential).setApplicationName("Storage Server App").build
  //  }
  //
  //  def createCustomTransferClient(credentials:String): Storagetransfer = {
  //    val transport = new NetHttpTransport
  //    val jsonFactory = new JacksonFactory
  //    var credential = GoogleCredential.fromStream(new ByteArrayInputStream(credentials.getBytes()))
  //    if (credential.createScopedRequired) credential = credential.createScoped(BigqueryScopes.all)
  //    new Storagetransfer.Builder(transport, jsonFactory, credential).setApplicationName("Storage Transfer Server App").build
  //  }
  //
  //  def transferS3FilesToGcs(s3BucketName: String,
  //                           awsPrefixIncludeList: List[String],
  //                           awsPrefixExcludeList: List[String],
  //                           awsAccessKey: String,
  //                           awsSecretKey: String,
  //                           deleteSourceAfterTransfer: Boolean,
  //                           overwriteTarget: Boolean,
  //                           transferJobDescription: String,
  //                           nuoTenantDetails: NuoTenantDetails): String = {
  //
  //
  //    val startDate = new Date()
  //      .setYear(Calendar.getInstance().get(Calendar.YEAR))
  //      .setMonth(Calendar.getInstance().get(Calendar.MONTH))
  //      .setDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
  //
  //
  //    var objectConditions = new ObjectConditions()
  //    if (awsPrefixIncludeList.filterNot(_.isEmpty).nonEmpty) {
  //      objectConditions = objectConditions.setIncludePrefixes(awsPrefixIncludeList.asJava)
  //    }
  //    if (awsPrefixExcludeList.filterNot(_.isEmpty).nonEmpty) {
  //      objectConditions = objectConditions.setExcludePrefixes(awsPrefixExcludeList.asJava)
  //    }
  //    try {
  //      val transferJob =
  //        new TransferJob()
  //          .setDescription(transferJobDescription)
  //          .setProjectId(nuoTenantDetails.bqProjectId)
  //          .setTransferSpec(new TransferSpec()
  //            .setAwsS3DataSource(new AwsS3Data()
  //              .setBucketName(s3BucketName)
  //              .setAwsAccessKey(new AwsAccessKey()
  //                .setAccessKeyId(awsAccessKey)
  //                .setSecretAccessKey(awsSecretKey)))
  //            .setObjectConditions(objectConditions)
  //            .setTransferOptions(new TransferOptions()
  //              .setOverwriteObjectsAlreadyExistingInSink(overwriteTarget)
  //              .setDeleteObjectsFromSourceAfterTransfer(deleteSourceAfterTransfer))
  //            .setGcsDataSink(new GcsData()
  //              .setBucketName(nuoTenantDetails.tenantBucketName)))
  //          .setSchedule(new Schedule()
  //            .setScheduleStartDate(startDate)
  //            .setScheduleEndDate(startDate))
  //          .setStatus("ENABLED")
  //
  //      nuoTenantDetails.transferClient.transferJobs().create(transferJob).execute().getName
  //    } catch {
  //      case e: Exception =>
  //        printException(e, nuoTenantDetails)
  //        ""
  //    }
  //  }


  def setCorsToBucket(): Unit = {

    if (NuoEvaEnglishListener.nuoTenantDetails == null) {

      NuoEvaEnglishListener.nuoTenantDetails = new NuoTenantDetails("System_" + System.currentTimeMillis(), "16409d3023f", "")

      val targetAccountDetails =
        AccountMetadata
          .getNewAccountDetails(
            tenantId = "16409d3023f",
            userId = "",
            emailAddress = "",
            userName = Some(""),
            userDisplayName = Some("")
          )
    }

    val bucket = new Bucket()
    bucket.setName(NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName)

    var cors = new Cors()
    cors.setMethod(List("*").asJava)
    cors.setOrigin(List("*").asJava)
    cors.setMaxAgeSeconds(3 * 3600)
    cors.setResponseHeader(List("*").asJava)
    bucket.setCors(List(cors).asJava)

    NuoEvaEnglishListener
      .nuoTenantDetails
      .storageClient
      .buckets()
      .update(bucket.getName, bucket)
      .execute()
  }

  def makeFilePublic(fileName: String): Unit = {

    val gcsObject =
      NuoEvaEnglishListener
        .nuoTenantDetails
        .storageClient
        .objects()
        .get(
          NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName,
          NuoEvaEnglishListener.nuoTenantDetails.tenantFilePrefix + fileName
        )
        .execute()
    gcsObject.setAcl(List(new ObjectAccessControl().setEntity("allUsers").setRole("READER")).asJava)

    NuoEvaEnglishListener
      .nuoTenantDetails
      .storageClient
      .objects()
      .update(NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName,
        NuoEvaEnglishListener.nuoTenantDetails.tenantFilePrefix + fileName,
        gcsObject)
  }

  def createGcsBucket(bucketName: String,
                      deleteExisting: Boolean): Unit = {


    val bucket = new Bucket()
    //    bucket.setId(bucketName)
    bucket.setName(bucketName)
    bucket.setLocation("EU")
    if (deleteExisting) {

      try {

        NuoEvaEnglishListener.nuoTenantDetails.storageClient.buckets().delete(bucketName).execute()
      } catch {
        case e: GoogleJsonResponseException =>
          if (e.getStatusCode == 404) {
            //do nothing
          } else {
            throw new Exception(e)
          }
        case x: Exception =>
          throw new Exception(x)

      }
    }
    //    var cors = new Cors()
    //    cors.setMethod(List("*").asJava)
    //    cors.setOrigin(List("*").asJava)
    //    cors.setMaxAgeSeconds(3600)
    //    cors.setResponseHeader(List("*").asJava)
    //    bucket.setCors(List(cors).asJava)

    NuoEvaEnglishListener.nuoTenantDetails.storageClient.buckets().insert(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId, bucket).execute()
  }


  def createBucketAcl(projectId: String,
                      bucketName: String,
                      serviceAccountEmail: String): Unit = {

    try {
      val orgPolicy = NuoEvaEnglishListener.nuoTenantDetails.storageClient.buckets().getIamPolicy(bucketName).execute()
      val bindings = orgPolicy.getBindings
      bindings.add(new Policy.Bindings().setMembers(List(s"serviceAccount:$serviceAccountEmail").asJava).setRole("roles/storage.objectAdmin"))
      NuoEvaEnglishListener.nuoTenantDetails.storageClient.buckets().setIamPolicy(bucketName, new Policy().setBindings(bindings)).execute()
    } catch {
      case e: Exception => printException(e)
    }
  }

  def listGcsFileNames(bucketName: String,
                       nuoFilePrefix: String,
                       pageToken: String): List[String] = {

    var listRequest = NuoEvaEnglishListener.nuoTenantDetails.storageClient.objects().list(bucketName)
    listRequest = if (nuoFilePrefix.trim.nonEmpty) {

      listRequest.setPrefix(nuoFilePrefix)
    } else {
      listRequest
    }
    listRequest = if (pageToken != null && pageToken.nonEmpty) {
      listRequest.setPageToken(pageToken)
    } else {
      listRequest
    }

    val response = listRequest.execute()
    val nextPageToken = response.getNextPageToken
    if (nextPageToken != null && nextPageToken.nonEmpty) {
      response.getItems.asScala.filterNot(_.getName.endsWith("/")).map(_.getName).toList ++ listGcsFileNames(bucketName, nuoFilePrefix, nextPageToken)
    }
    else {
      if (response.size() > 1) response.getItems.asScala.filterNot(_.getName.endsWith("/")).map(_.getName).toList
      else List()
    }
  }

  def listGcsFileNameAndSize(bucketName: String,
                             nuoFilePrefix: String,
                             pageToken: String): List[(String, Long)] = {

    var listRequest = NuoEvaEnglishListener.nuoTenantDetails.storageClient.objects().list(bucketName)
    listRequest = if (nuoFilePrefix.trim.nonEmpty) {

      listRequest.setPrefix(nuoFilePrefix)
    } else {
      listRequest
    }
    listRequest = if (pageToken != null && pageToken.nonEmpty) {
      listRequest.setPageToken(pageToken)
    } else {
      listRequest
    }

    val response = listRequest.execute()
    val nextPageToken = response.getNextPageToken
    if (nextPageToken != null && nextPageToken.nonEmpty) {
      response.getItems.asScala.filterNot(_.getName.endsWith("/")).map(file => (file.getName, file.getSize.longValueExact())).toList ++ listGcsFileNameAndSize(bucketName, nuoFilePrefix, nextPageToken)
    }
    else {
      if (response.size() > 1) response.getItems.asScala.filterNot(_.getName.endsWith("/")).map(file => (file.getName, file.getSize.longValueExact())).toList
      else List()
    }
  }


  def listGcsObject(bucketName: String,
                    nuoFilePrefix: String,
                    pageToken: String): List[StorageObject] = {

    var listRequest = NuoEvaEnglishListener.nuoTenantDetails.storageClient.objects().list(bucketName)
    listRequest = if (nuoFilePrefix.trim.nonEmpty) {

      listRequest.setPrefix(nuoFilePrefix)
    } else {
      listRequest
    }
    listRequest = if (pageToken != null && pageToken.nonEmpty) {
      listRequest.setPageToken(pageToken)
    } else {
      listRequest
    }

    val response = listRequest.execute()
    val nextPageToken = response.getNextPageToken
    if (nextPageToken != null && nextPageToken.nonEmpty) {
      response.getItems.asScala
        .filterNot(_.getName.endsWith("/"))
        //        .map(file => (file.getName, file.getSize.longValueExact()))
        .toList ++ listGcsObject(bucketName, nuoFilePrefix, nextPageToken)
    }
    else {
      if (response.size() > 1) response.getItems.asScala.filterNot(_.getName.endsWith("/")) /*.map(file => (file.getName, file.getSize.longValueExact()))*/ .toList
      else List()
    }
  }


  def copyGcsObjects(sourceBucketName: String,
                     targetBucketName: String,
                     sourceFilePrefix: String,
                     targetFilePrefix: String): Unit = {

    listGcsObject(sourceBucketName, sourceFilePrefix, null).foreach { obj =>
      NuoEvaEnglishListener
        .nuoTenantDetails
        .storageClient
        .objects()
        .copy(
          sourceBucketName,
          obj.getName,
          targetBucketName,
          targetFilePrefix + obj.getName.substring(obj.getName.indexOf(sourceFilePrefix) + sourceFilePrefix.length),
          obj
        ).execute()
    }
  }


  def writeToGcs(gcsFileName: String,
                 content: String): Unit = {
    printInfo(s"I am writing gcs file: $gcsFileName")

    NuoEvaEnglishListener.nuoTenantDetails.storageClient.objects().insert(NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName,
      new StorageObject().setContentType("text/csv").setContentEncoding("UTF8").setId(gcsFileName).setName(gcsFileName),
      ByteArrayContent.fromString("text/csv", content)).execute()
  }

  def deleteObject(bucketName: String,
                   gcsFileName: String): Unit = {
    printInfo(s"I am deleting gcs file: $gcsFileName")
    NuoEvaEnglishListener.nuoTenantDetails.storageClient.objects().delete(bucketName, gcsFileName).execute()

  }


  def uploadLocalFileToGcs(projectId: String,
                           bucketName: String,
                           gcsFileName: String,
                           localFileName: String): Unit = {

    NuoEvaEnglishListener.nuoTenantDetails.storageClient.objects().insert(bucketName, new StorageObject().setId(gcsFileName).setName(gcsFileName),
      new InputStreamContent(
        null, new BufferedInputStream(new FileInputStream(
          localFileName)))).execute()

    val localFileCrc32c = NuoSecurityClient.getCrc32c(localFileName)
    var gcsFileCrc32c = 0l
    while (gcsFileCrc32c != localFileCrc32c) {
      var gcsObject = new StorageObject()
      Thread.sleep(1000)
      try {

        gcsObject = NuoEvaEnglishListener.nuoTenantDetails.storageClient.objects().get(bucketName, gcsFileName).execute()
      } catch {
        case e: Exception =>
          printException(e)
      }
      gcsFileCrc32c = gcsObject.getCrc32c.toLong
    }

  }

  def downloadGcsFile(projectId: String,
                      bucketName: String,
                      gcsFileName: String,
                      localFileName: String): Unit = {


    val gcsObject = NuoEvaEnglishListener.nuoTenantDetails.storageClient.objects().get(bucketName, gcsFileName)
    // Downloading data.
    val out = new ByteArrayOutputStream()
    gcsObject.getMediaHttpDownloader.setDirectDownloadEnabled(true)
    gcsObject.executeMediaAndDownloadTo(out)

    val outputStream = new FileOutputStream(localFileName)
    outputStream.write(out.toByteArray)
    outputStream.close()

  }

  def getPreSignedUrl(bucketName: String,
                      fileName: String,
                      contentType: String,
                      method: String): String = {

    val expirationTimeMillis = System.currentTimeMillis() + 1 * 60 * 60 * 1000 // 1 hour time out

    val updFileName = fileName.split("/").map(URLEncoder.encode(_, "UTF-8")).mkString("/")
    var updContentType = if (contentType != null && contentType.trim.nonEmpty) contentType else ""
    val stringToSign =
      method + "\n" +
        "\n" +
        updContentType + "\n" +
        expirationTimeMillis + "\n" +
        "/" + bucketName + "/" + updFileName

    val credentials = NuoEvaEnglishListener.nuoTenantDetails.gcpServiceAccountCredentials

    NuoLogger.printInfo("stringToSign = " + stringToSign)
    //    NuoLogger.printInfo("credentials = "+credentials)
    //    NuoLogger.printInfo("signedString = "+credentials.sign(stringToSign.getBytes))
    var signedString = Base64.getEncoder.encodeToString(credentials.sign(stringToSign.getBytes))

    // URL encode the signed string so that we can add this URL
    signedString = URLEncoder.encode(signedString, "UTF-8")

    "https://storage.googleapis.com/" + bucketName + "/" + updFileName +
      "?GoogleAccessId=" + credentials.getClientEmail +
      "&Expires=" + expirationTimeMillis +
      "&Signature=" + signedString
  }

  def getVideoDuration(projectId: String,
                       bucketName: String,
                       gcsFileName: String): Double = {
    NuoEvaEnglishListener.nuoTenantDetails.storageClient.objects()
      .get(bucketName, gcsFileName)
      .execute()
      .getMetadata
      .get("Duration").toDouble
  }

  def isTransferJobComplete(projectId: String,
                            jobId: String): Boolean = {
    var transferOps = NuoEvaEnglishListener.nuoTenantDetails.transferClient.transferOperations()
      .list("transferOperations")
      .setFilter("{\"project_id\": \"" + projectId + "\", \"job_names\": [\"" + jobId + "\"] }")
      .execute()

    while (transferOps.isEmpty) {
      printInfo("I am waiting for Storage Transfer operation to be started!")
      Thread.sleep(1000)
      transferOps = NuoEvaEnglishListener.nuoTenantDetails.transferClient.transferOperations()
        .list("transferOperations")
        .setFilter("{\"project_id\": \"" + projectId + "\", \"job_names\": [\"" + jobId + "\"] }")
        .execute()
    }
    transferOps.getOperations.asScala.forall(_.getMetadata.get("status").toString.equalsIgnoreCase("SUCCESS"))
  }

  def calcGCSBytes(): Long = {
    var totalBytes = 0L
    var storageResponse = NuoEvaEnglishListener.nuoTenantDetails.storageClient.objects().list(NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName).execute()
    var files = storageResponse.getItems
    if (files != null && files.size() > 0) {
      totalBytes += storageResponse.getItems.asScala.map(obj => obj.getSize.longValue()).sum
    }
    var nextToken = storageResponse.getNextPageToken

    while (nextToken != null && nextToken.nonEmpty) {

      storageResponse = NuoEvaEnglishListener.nuoTenantDetails.storageClient.objects().list(NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName).setPageToken(nextToken).execute()
      files = storageResponse.getItems
      if (files != null && files.size() > 0) {
        totalBytes += storageResponse.getItems.asScala.map(obj => obj.getSize.longValue()).sum
      }
      nextToken = storageResponse.getNextPageToken
    }
    totalBytes

  }
}

