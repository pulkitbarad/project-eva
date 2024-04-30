package client

import java.io.{ByteArrayInputStream, File}

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model._
import com.amazonaws.{AmazonClientException, AmazonServiceException, HttpMethod}
import logging.NuoLogger
import logging.NuoLogger.{printException, printInfo}
import nlp.grammar.NuoEvaEnglishListener

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 09Feb2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoS3Client {


  def createCustomAwsS3Client(accessKey: String,
                              secretKey: String): AmazonS3Client = {

    val awsCredentials = new BasicAWSCredentials(accessKey, secretKey)
    new AmazonS3Client(awsCredentials)
  }

  def createS3Bucket(bucketName: String,
                     deleteExisting: Boolean): Unit = {
    try {
      if (deleteExisting) {
        NuoEvaEnglishListener.Client.amazonS3Client.deleteBucket(new DeleteBucketRequest(bucketName))
      }

    } catch {
      case ase: AmazonServiceException =>
        if (ase.getStatusCode == 404) {
          //do nothing
        } else {
          throw new Exception(ase)
        }
      case x: Exception =>
        throw new Exception(x)
    }
    NuoEvaEnglishListener.Client.amazonS3Client.createBucket(new CreateBucketRequest(bucketName))
  }

  def getS3FileInputStream(bucketName: String,
                           filePath: String): S3ObjectInputStream = {
    try {

      //      printInfo(requestId,tenantId, s"Inside readFromS3:2427 I am reading the file $filePath")

      val s3Object = NuoEvaEnglishListener.Client.amazonS3Client.getObject(new GetObjectRequest(bucketName, filePath))

      s3Object.getObjectContent

    } catch {

      case ase: AmazonServiceException =>
        if (ase.getStatusCode == 404) {
          printInfo(s"I could not find file  $filePath")
        } else {
          printInfo(s"I got an exception: ${ase.getMessage} at ${ase.getStackTraceString}")
        }
        null
      case ex: Exception =>
        printInfo(s"I got an exception: ${ex.getMessage} at ${ex.getStackTraceString}")
        null
    }
  }


  def encryptS3File(encryptionUser: String,
                    bucketName: String,
                    sourceFilePath: String,
                    targetFilePath: String): Unit = {

    val content = readFromS3(bucketName, sourceFilePath)
    val encryptedContent = NuoSecurityClient.encryptTextForUser(encryptionUser, content)
    writeToS3(bucketName,
      if (targetFilePath == null || targetFilePath.isEmpty) sourceFilePath else targetFilePath,
      encryptedContent.getBytes)
  }

  def decryptS3File(encryptionUser: String,
                    bucketName: String,
                    sourceFilePath: String,
                    targetFilePath: String): Unit = {

    writeToS3(bucketName,
      if (targetFilePath == null || targetFilePath.isEmpty) sourceFilePath else targetFilePath,
      readAndDecryptS3File(encryptionUser, bucketName, sourceFilePath).getBytes)
  }

  def readAndDecryptS3File(encryptionUser: String,
                           bucketName: String,
                           filePath: String): String = {

    val content = readFromS3(bucketName, filePath)
    if (content.isEmpty) ""
    else NuoSecurityClient.decryptTextForUser(encryptionUser, content)
  }

  def readFromS3(bucketName: String,
                 fileName: String): String = {
    try {

      val before = System.currentTimeMillis()
      val s3Object = NuoEvaEnglishListener.Client.amazonS3Client.getObject(new GetObjectRequest(bucketName, fileName))
      NuoLogger.printInfo(s"${System.currentTimeMillis() - before} milliseconds spent in s3 get object $fileName")
      new String(Stream.continually(s3Object.getObjectContent.read).takeWhile(_ != -1).map(_.toByte).toArray)
    } catch {

      case ase: AmazonServiceException =>
        if (ase.getStatusCode == 404) {
          NuoLogger.printInfo(s"I could not find file  $fileName")
        } else {
          NuoLogger.printInfo(s"I got an exception: ${ase.getMessage} at ${ase.getStackTraceString}")
        }
        ""
      case ex: Exception =>
        NuoLogger.printInfo(s"I got an exception: ${ex.getMessage} at ${ex.getStackTraceString}")
        ""
    }
  }

  def readByteArrayFromS3(bucketName: String,
                          filePath: String): Array[Byte] = {
    try {

      val s3Object = NuoEvaEnglishListener.Client.amazonS3Client.getObject(new GetObjectRequest(bucketName, filePath))
      //      printInfo(s"I have the s3 file content===>$filePath")

      Stream.continually(s3Object.getObjectContent.read).takeWhile(_ != -1).map(_.toByte).toArray


    } catch {

      case ase: AmazonServiceException =>
        if (ase.getStatusCode == 404) {
          printInfo(s"I could not find file  $filePath")
        } else {
          printInfo(s"I got an exception: ${ase.getMessage} at ${ase.getStackTraceString}")
        }
        null
      case ex: Exception =>
        printInfo(s"I got an exception: ${ex.getMessage} at ${ex.getStackTraceString}")
        null
    }
  }


  def uploadToS3(bucketName: String,
                 s3FileName: String,
                 sourceFileName: String): Unit = {

    /*
     * Put the object in S3
     */
    try {
      NuoEvaEnglishListener.Client.amazonS3Client.putObject(new PutObjectRequest(bucketName, s3FileName, new File(sourceFileName)))
    } catch {
      case ase: AmazonServiceException =>
        printException(ase)
        printInfo(s"Bucket Name:    $bucketName")
        printInfo(s"S3 File Name:    $s3FileName")
        printInfo(s"SourceFile Name:    $sourceFileName")
        printInfo("Error Message:    " + ase.getMessage)
        printInfo("HTTP Status Code: " + ase.getStatusCode)
        printInfo("AWS Error Code:   " + ase.getErrorCode)
        printInfo("Error Type:       " + ase.getErrorType)
        printInfo("Request ID:       " + ase.getRequestId)
      case ace: AmazonClientException =>
        printException(ace)
        printInfo("Error Message: " + ace.getMessage);
    }
  }

  def encryptAndWriteToS3File(encryptionUser: String,
                              bucketName: String,
                              filePath: String,
                              content: String): Unit = {

    writeToS3(bucketName, filePath, NuoSecurityClient.encryptTextForUser(encryptionUser, content).getBytes)
  }

  def writeToS3(bucketName: String,
                fileName: String,
                contentBytes: Array[Byte]): Unit = {

    /*
    * If append serverLoggingLevel is true, read the file from S3 and append source content to it.
    * */


    printInfo(s"I am writing $fileName")
    val metadata = new ObjectMetadata()

    val contentLength = contentBytes.length
    metadata.setContentLength(contentLength)

    if (contentLength > 0) {

      /*
       * Put the object in S3
       */
      val inputStream = new ByteArrayInputStream(contentBytes)

      try {
        NuoEvaEnglishListener.Client.amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata))
      } catch {
        case ase: AmazonServiceException =>
          printException(ase)
          printInfo(s"Bucket Name:     $bucketName")
          printInfo(s"S3 File Name:    $fileName")
          printInfo(s"Error Message:    ${ase.getMessage}")
          printInfo(s"HTTP Status Code: ${ase.getStatusCode}")
          printInfo(s"AWS Error Code:   ${ase.getErrorCode}")
          printInfo(s"Error Type:       ${ase.getErrorType}")
          printInfo(s"Request ID:       ${ase.getRequestId}")
        case ace: AmazonClientException =>
          printException(ace)
          printInfo(s"Error Message: ${ace.getMessage}")
      } finally {
        if (inputStream != null) {
          inputStream.close()
        }
      }
    }
  }

  def writeToS3(bucketName: String,
                fileName: String,
                contentBytes: Array[Byte],
                amazonS3Client: AmazonS3Client): Unit = {

    /*
    * If append serverLoggingLevel is true, read the file from S3 and append source content to it.
    * */


    println(s"I am writing $fileName")
    val metadata = new ObjectMetadata()

    val contentLength = contentBytes.length
    metadata.setContentLength(contentLength)

    if (contentLength > 0) {

      /*
       * Put the object in S3
       */
      val inputStream = new ByteArrayInputStream(contentBytes)

      try {
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata))
      } catch {
        case ase: AmazonServiceException =>
          System.err.println(ase)
          println(s"Bucket Name:     $bucketName")
          println(s"S3 File Name:    $fileName")
          println(s"Error Message:    ${ase.getMessage}")
          println(s"HTTP Status Code: ${ase.getStatusCode}")
          println(s"AWS Error Code:   ${ase.getErrorCode}")
          println(s"Error Type:       ${ase.getErrorType}")
          println(s"Request ID:       ${ase.getRequestId}")
        case ace: AmazonClientException =>
          System.err.println(ace)
      } finally {
        if (inputStream != null) {
          inputStream.close()
        }
      }
    }
  }

  def getPreSignedUrl(bucketName: String,
                      fileName: String,
                      contentType: String,
                      method: HttpMethod): String = {

    val expiration = new java.util.Date()
    var milliSeconds = expiration.getTime
    milliSeconds += 1000 * 60 * 60; // Add 1 hour.
    expiration.setTime(milliSeconds)

    val generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileName)
    generatePresignedUrlRequest.setMethod(method)
    if (contentType != null && contentType.trim.nonEmpty)
      generatePresignedUrlRequest.setContentType(contentType)
    generatePresignedUrlRequest.setExpiration(expiration)
    NuoEvaEnglishListener
      .Client
      .amazonS3Client
      .generatePresignedUrl(generatePresignedUrlRequest)
      .toString
  }

  def writeToS3(bucketName: String,
                fileName: String,
                sourceContent: String,
                appendMode: Boolean): Unit = {
    try {

      /*
      * If append serverLoggingLevel is true, read the file from S3 and append source content to it.
      * */

      val contentBytes = if (appendMode && doesFileExist(bucketName, fileName)) {
        val content = readFromS3(bucketName, fileName)
        if (content == null) {
          printException(new Exception(s"File $fileName does not exist"))
        }
        (content.mkString + sourceContent).getBytes
      } else {
        sourceContent.getBytes
      }

      writeToS3(bucketName, fileName, contentBytes)
      printInfo(s"I completed writing $fileName")

    } catch {
      case e: Exception => printException(e)
    }
  }

  def doesFileExist(bucketName: String,
                    filePath: String): Boolean = {

    try {
      val iteratorS3 = getS3ObjectListIterator(bucketName, filePath)

      while (iteratorS3.hasNext) {
        val s3Obj = iteratorS3.next()
        if (s3Obj.getKey.equalsIgnoreCase(filePath)) {
          printInfo(s"The $filePath exists")
          return true
        }
      }
      printInfo(s"The file $filePath  does not exist inside $bucketName.")
      false
    } catch {
      case e: Exception => printException(e)
        printInfo(s"The file $filePath  does not exist inside $bucketName.")
        false
    }
  }

  def copyS3File(sourceBucketName: String,
                 destBucketName: String,
                 sourceFileName: String,
                 destFileName: String): Unit = {

    /*
     * Put the object in S3
     */
    try {
      if (sourceBucketName.equalsIgnoreCase(destBucketName) && sourceFileName.equalsIgnoreCase(destFileName)) {
        val tempFile = destFileName + ".temp"
        NuoEvaEnglishListener.Client.amazonS3Client.copyObject(new CopyObjectRequest(sourceBucketName, sourceFileName, destBucketName, tempFile))
        NuoEvaEnglishListener.Client.amazonS3Client.deleteObject(new DeleteObjectRequest(sourceBucketName, sourceFileName))
        NuoEvaEnglishListener.Client.amazonS3Client.copyObject(new CopyObjectRequest(sourceBucketName, tempFile, destBucketName, destFileName))
        NuoEvaEnglishListener.Client.amazonS3Client.deleteObject(new DeleteObjectRequest(sourceBucketName, tempFile))

      } else
        NuoEvaEnglishListener.Client.amazonS3Client.copyObject(new CopyObjectRequest(sourceBucketName, sourceFileName, destBucketName, destFileName))
    } catch {
      case ase: AmazonServiceException =>
        printInfo(s"Source Bucket Name:    $sourceBucketName")
        printInfo(s"Destination Bucket Name:    $destBucketName")
        printInfo(s"SourceFile Name:    $sourceFileName")
        printInfo(s"DestFile Name:    $destFileName")
        printInfo("Error Message:    " + ase.getMessage)
        printInfo("HTTP Status Code: " + ase.getStatusCode)
        printInfo("AWS Error Code:   " + ase.getErrorCode)
        printInfo("Error Type:       " + ase.getErrorType)
        printInfo("Request ID:       " + ase.getRequestId)
      case ace: AmazonClientException =>
        printInfo("Error Message: " + ace.getMessage)
    }
  }

  def moveS3File(sourceBucketName: String,
                 destBucketName: String,
                 sourceFileName: String,
                 destFileName: String): Unit = {

    /*
     * Put the object in S3
     */
    try {
      if (sourceBucketName.equalsIgnoreCase(destBucketName) && sourceFileName.equalsIgnoreCase(destFileName)) {
        val tempFile = destFileName + ".temp"
        NuoEvaEnglishListener.Client.amazonS3Client.copyObject(new CopyObjectRequest(sourceBucketName, sourceFileName, destBucketName, tempFile))
        NuoEvaEnglishListener.Client.amazonS3Client.deleteObject(new DeleteObjectRequest(sourceBucketName, sourceFileName))
        NuoEvaEnglishListener.Client.amazonS3Client.copyObject(new CopyObjectRequest(sourceBucketName, tempFile, destBucketName, destFileName))
        NuoEvaEnglishListener.Client.amazonS3Client.deleteObject(new DeleteObjectRequest(sourceBucketName, tempFile))

      } else {
        NuoEvaEnglishListener.Client.amazonS3Client.copyObject(new CopyObjectRequest(sourceBucketName, sourceFileName, destBucketName, destFileName))
      }
      NuoEvaEnglishListener.Client.amazonS3Client.deleteObject(sourceBucketName, sourceFileName)
    } catch {
      case ase: AmazonServiceException =>
        printInfo(s"Source Bucket Name:    $sourceBucketName")
        printInfo(s"Destination Bucket Name:    $destBucketName")
        printInfo(s"SourceFile Name:    $sourceFileName")
        printInfo(s"DestFile Name:    $destFileName")
        printInfo("Error Message:    " + ase.getMessage)
        printInfo("HTTP Status Code: " + ase.getStatusCode)
        printInfo("AWS Error Code:   " + ase.getErrorCode)
        printInfo("Error Type:       " + ase.getErrorType)
        printInfo("Request ID:       " + ase.getRequestId)
      case ace: AmazonClientException =>
        printInfo("Error Message: " + ace.getMessage)
    }
  }

  def deleteS3File(bucketName: String,
                   s3FileName: String): Unit = {

    printInfo(s"I am deleting $s3FileName")
    try {
      NuoEvaEnglishListener.Client.amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, s3FileName))

    } catch {
      case ase: AmazonServiceException =>
        printException(ase)
        printInfo(s"Bucket Name:     $bucketName")
        printInfo(s"S3 File Name:    $s3FileName")
        printInfo(s"Error Message:    ${ase.getMessage}")
        printInfo(s"HTTP Status Code: ${ase.getStatusCode}")
        printInfo(s"AWS Error Code:   ${ase.getErrorCode}")
        printInfo(s"Error Type:       ${ase.getErrorType}")
        printInfo(s"Request ID:       ${ase.getRequestId}")
      case ace: AmazonClientException =>
        printException(ace)
        printInfo(s"Error Message: ${ace.getMessage}")
    }
  }

  def getS3FileList(bucketName: String,
                    prefix: String): List[String] = {
    val s3Iterator = getS3ObjectListIterator(bucketName, prefix)
    if (s3Iterator.nonEmpty) {

      val s3Files = ArrayBuffer[String]()
      while (s3Iterator.hasNext) {
        s3Files += s3Iterator.next().getKey
      }
      s3Files.toList

    } else {
      List[String]()
    }
  }


  def getS3ObjectListIterator(bucketName: String,
                              prefix: String): Iterator[S3ObjectSummary] = {

    val objectSummaries = mutable.ArrayBuffer[S3ObjectSummary]()
    try {


      var listRequest = new ListObjectsRequest().withBucketName(bucketName)
      listRequest = if (prefix.nonEmpty) {
        listRequest.withPrefix(prefix).withMaxKeys(Integer.MAX_VALUE)
      } else {
        listRequest.withMaxKeys(Integer.MAX_VALUE)
      }
      var objects = NuoEvaEnglishListener.Client.amazonS3Client.listObjects(listRequest)
      objectSummaries ++= objects.getObjectSummaries.asScala

      while (objects.isTruncated) {
        objects = NuoEvaEnglishListener.Client.amazonS3Client.listNextBatchOfObjects(objects)
        objectSummaries ++= objects.getObjectSummaries.asScala
      }
    } catch {
      case e: Exception =>
        println(s"Exception: ${e.getMessage} at \n ${e.getStackTraceString}")
    }
    objectSummaries.filterNot(_.getKey.endsWith("/")).iterator
  }

  def getObjectNuoRequestId(bucketName: String, fileName: String, amazonS3Client: AmazonS3Client): Option[String] = {
    val tag = amazonS3Client.getObjectTagging(new GetObjectTaggingRequest(bucketName, fileName)).getTagSet.asScala.find(_.getKey.equalsIgnoreCase("NuoRequestId"))

    if (tag.isDefined) Some(tag.get.getValue)
    else None
  }

  def getS3ObjectListIterator(bucketName: String,
                              prefix: String,
                              amazonS3Client: AmazonS3Client): Iterator[S3ObjectSummary] = {

    val objectSummaries = mutable.ArrayBuffer[S3ObjectSummary]()

    try {
      var listRequest = new ListObjectsRequest().withBucketName(bucketName)
      listRequest = if (prefix.nonEmpty) {
        listRequest.withPrefix(prefix /*.replace(" ","%20")*/).withMaxKeys(Integer.MAX_VALUE)
      } else {
        listRequest.withMaxKeys(Integer.MAX_VALUE)
      }
      var objects = amazonS3Client.listObjects(listRequest)
      objectSummaries ++= objects.getObjectSummaries.asScala

      while (objects.isTruncated) {
        objects = amazonS3Client.listNextBatchOfObjects(objects)
        objectSummaries ++= objects.getObjectSummaries.asScala
      }
    } catch {
      case e: Exception =>
        println(s"Exception: ${e.getMessage} at \n ${e.getStackTraceString}")
    }
    objectSummaries.filterNot(_.getKey.endsWith("/")).iterator
  }
}
