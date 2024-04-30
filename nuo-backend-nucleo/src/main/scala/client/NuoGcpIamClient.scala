package client

import com.google.api.services.iam.v1.Iam
import nlp.grammar.NuoEvaEnglishListener

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 07Sep2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoGcpIamClient {
  var gcpIamClient: Iam = null


  def getGcpActiveKeyFileName(projectId: String): String = {
    s"${NuoEvaEnglishListener.GcpCredentialsDir.GcpCredentialsActiveDir}$projectId"
  }

  def getGcpPoolKeyFileName(projectId: String): String = {
    s"${NuoEvaEnglishListener.GcpCredentialsDir.GcpCredentialsPoolDir}$projectId"
  }

  def main(args: Array[String]): Unit = {

    //    encryptGcsCredentials()
    val masterProjectId = "geometric-watch-153714"
    //    createIamClient(masterProjectId)
    //    createFileUploadServiceAccount(masterProjectId = masterProjectId, tenantId = "tenant1", tenantBucketName = "tenant1-backend-nuocanvas-com")


  }

  //  def createIamClient(projectId: String): Unit = {
  //
  //    if (gcpIamClient == null) {
  //
  //      val transport = new NetHttpTransport
  //      val jsonFactory = new JacksonFactory
  //      var credential: GoogleCredential = null
  //      credential = GoogleCredential.fromStream(
  //        new ByteArrayInputStream(NuoS3Client.readAndDecryptS3File(null,
  //          NuoEvaEnglishListener.BucketName.MasterBucketName,
  //          getGcpActiveKeyFileName(projectId)).getBytes))
  //      if (credential.createScopedRequired) credential = credential.createScoped(IamScopes.all)
  //      gcpIamClient = new Iam.Builder(transport, jsonFactory, credential).setApplicationName(projectId).build()
  //    }
  //  }
  //
  //  def createFileUploadServiceAccount(masterProjectId: String,
  //                                     tenantId: String,
  //                                     tenantBucketName: String): Unit = {
  //
  //    createIamClient(masterProjectId)
  //    try {
  //
  //
  //      val serviceAccount = gcpIamClient.projects().serviceAccounts().create(s"projects/$masterProjectId", new CreateServiceAccountRequest()
  //        .setServiceAccount(new ServiceAccount().setDisplayName(s"svc-upload-${tenantId.toLowerCase}"))
  //        .setAccountId(s"svc-upload-${tenantId.toLowerCase}")).execute()
  //
  //      val serviceAccountKey = gcpIamClient.projects()
  //        .serviceAccounts()
  //        .keys()
  //        .create(serviceAccount.getName,
  //          new CreateServiceAccountKeyRequest()).execute()
  //
  //      NuoS3Client.encryptAndWriteToS3File(encryptionUser = "",
  //        bucketName = NuoEvaEnglishListener.BucketName.MasterBucketName,
  //        filePath = NuoEvaEnglishListener.GcpCredentialsDir.GcpCredentialsUploadDir + tenantId,
  //        content = new String(Base64.getDecoder.decode(serviceAccountKey.getPrivateKeyData)))
  //      NuoGcsClient.createBucketAcl(masterProjectId, tenantBucketName, serviceAccount.getEmail)
  //    } catch {
  //      case e: Exception => System.err.println(e)
  //    }
  //
  //  }

  def encryptGcsCredentials(): Unit = {
    encryptGcsActiveCredentials()
    encryptGcsPoolCredentials()
    encryptGcsUploadCredentials()
  }

  def encryptGcsUploadCredentials(): Unit = {

    NuoS3Client.getS3FileList(bucketName = NuoEvaEnglishListener.BucketName.MasterBucketName,
      prefix = NuoEvaEnglishListener.GcpCredentialsDir.GcpCredentialsUploadDir)
      .filter(_.toLowerCase.endsWith("_raw")).foreach { filePath =>
      NuoS3Client.encryptS3File(null,
        bucketName = NuoEvaEnglishListener.BucketName.MasterBucketName,
        sourceFilePath = filePath,
        targetFilePath = filePath.substring(0, filePath.length - 4)) /*Because we have _raw prefix for unencrypted files*/
    }
  }

  def encryptGcsActiveCredentials(): Unit = {

    NuoS3Client.getS3FileList(bucketName = NuoEvaEnglishListener.BucketName.MasterBucketName,
      prefix = NuoEvaEnglishListener.GcpCredentialsDir.GcpCredentialsActiveDir)
      .filter(_.toLowerCase.endsWith("_raw")).foreach { filePath =>
      NuoS3Client.encryptS3File(null,
        bucketName = NuoEvaEnglishListener.BucketName.MasterBucketName,
        sourceFilePath = filePath,
        targetFilePath = filePath.substring(0, filePath.length - 4)) /*Because we have _raw prefix for unencrypted files*/
    }
  }

  def encryptGcsPoolCredentials(): Unit = {

    NuoS3Client.getS3FileList(bucketName = NuoEvaEnglishListener.BucketName.MasterBucketName,
      prefix = NuoEvaEnglishListener.GcpCredentialsDir.GcpCredentialsPoolDir)
      .filter(_.toLowerCase.endsWith("_raw")).foreach { filePath =>
      NuoS3Client.encryptS3File(null,
        bucketName = NuoEvaEnglishListener.BucketName.MasterBucketName,
        sourceFilePath = filePath,
        targetFilePath = filePath.substring(0, filePath.length - 4)) /*Because we have _raw prefix for unencrypted files*/
    }
  }
}
