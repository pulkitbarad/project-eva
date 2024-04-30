package client

import java.io.FileInputStream
import java.security.SecureRandom
import javax.crypto.spec.{PBEKeySpec, SecretKeySpec}
import javax.crypto.{Cipher, KeyGenerator, SecretKey, SecretKeyFactory}
import javax.xml.bind.DatatypeConverter

import action.NuoStorage
import canvas.NuoDataTypeHandler
import client.NuoS3Client.readFromS3
import com.amazonaws.util.CRC32ChecksumCalculatingInputStream
import dag.NuoTenantDetails
import logging.NuoLogger
import metadata.StorageMetadata.NuoField
import nlp.grammar.NuoEvaEnglishListener
import org.apache.commons.codec.DecoderException

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 15Jun2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoSecurityClient {

  private val masterSecretKeyHex = "xxx"
  private val masterSaltHex = "xxx"
  private val securityFactor = 65536
  private val saltLengthForUid = 30 / 2
  private val saltLength = 256 / 2
  private val hashLength = 256 * 4
  val newPasswordEmailSubject = "NuoCanvas Account Password"
  val newPasswordEmailBody = "You can access your NuoCanvas account with the Username provided at the time of registration and password:\n"
  val resetPasswordEmailSubject = "NuoCanvas Account Password Reset"
  val resetPasswordEmailBody = "Your NuoCanvas account password has been reset to:\n"
  val copyPasswordEmailSubject = "Attention: NuoCanvas Account Password Copy"
  val copyPasswordEmailBody = "A NuoCanvas account password has been created with"
  val copyPasswordEmailAddresses = "pulkit@nuocanvas.com"

  /**
    * Please note that secret key and encrypted text is unreadable binary and hence
    * in the following program we display it in hexadecimal format of the underlying bytes.
    *
    */


  def main(args: Array[String]) {

  }

  /**
    * Get the AES encryption key.
    *
    */
  private def generateEncryptionKey(secretKeyHex: String): SecretKey = {

    if (secretKeyHex != null) {

      var encoded = Array[Byte]()
      try {
        encoded = hexToBytes(secretKeyHex)
      } catch {
        case e: DecoderException => e.printStackTrace()
          return null
      }
      new SecretKeySpec(encoded, 0, encoded.length, "AES")
    } else {

      NuoLogger.printInfo("I am generating new key")
      val generator = KeyGenerator.getInstance("AES")
      generator.init(256) // The AES key size in number of bits
      generator.generateKey()
    }
  }

  private def saveEncryptionKeyForTenant(tenantId: String,
                                         key: SecretKey): Unit = {

    NuoS3Client.writeToS3(NuoEvaEnglishListener.BucketName.MasterBucketName,
      NuoEvaEnglishListener.NuoEncryptionKeysDir + tenantId,
      bytesToHex(key.getEncoded).getBytes)
  }

  private def loadEncryptionKeyForTenant(tenantId: String): SecretKey = {


    if (NuoS3Client
      .doesFileExist(
        bucketName = NuoEvaEnglishListener.BucketName.MasterBucketName,
        filePath = NuoEvaEnglishListener.NuoEncryptionKeysDir + tenantId)) {

      var encoded = Array[Byte]()
      try {
        encoded = hexToBytes(readFromS3(NuoEvaEnglishListener.BucketName.MasterBucketName, "Credentials/" + tenantId))
      } catch {
        case e: DecoderException => e.printStackTrace()
          return null
      }
      new SecretKeySpec(encoded, 0, encoded.length, "AES")
    } else {
      val key = generateEncryptionKey(null)
      saveEncryptionKeyForTenant(tenantId, key)
      key
    }
  }

  /**
    * Encrypt plainText in AES using the secret key
    */
  def encryptTextForUser(username: String,
                         plainText: String): String = {

    if (username != null && username.nonEmpty) {

      // AES defaults to AES/ECB/PKCS5Padding in Java 7
      val aesCipher = Cipher.getInstance("AES")
      aesCipher.init(Cipher.ENCRYPT_MODE, loadEncryptionKeyForTenant(username))
      val byteCipherText = aesCipher.doFinal(plainText.getBytes())
      bytesToHex(byteCipherText)
    } else {
      encryptTextForMaster(plainText)
    }
  }

  /**
    * Decrypt encrypted byte array using the key used for encryption.
    */
  def decryptTextForUser(username: String,
                         readableCipher: String): String = {

    if (username != null && username.nonEmpty) {

      val byteCipherText = hexToBytes(readableCipher)

      // AES defaults to AES/ECB/PKCS5Padding in Java 7
      val aesCipher = Cipher.getInstance("AES")
      aesCipher.init(Cipher.DECRYPT_MODE, loadEncryptionKeyForTenant(username))
      val bytePlainText = aesCipher.doFinal(byteCipherText)
      new String(bytePlainText)
    } else {
      decryptTextForMaster(readableCipher)
    }
  }

  /**
    * Encrypt plainText in AES using the secret key
    */
  private def encryptTextForMaster(plainText: String): String = {

    // AES defaults to AES/ECB/PKCS5Padding in Java 7
    val aesCipher = Cipher.getInstance("AES")
    aesCipher.init(Cipher.ENCRYPT_MODE, generateEncryptionKey(masterSecretKeyHex))
    val byteCipherText = aesCipher.doFinal(plainText.getBytes())
    bytesToHex(byteCipherText)
  }

  /**
    * Decrypt encrypted byte array using the key used for encryption.
    */
  private def decryptTextForMaster(readableCipher: String): String = {

    val byteCipherText = hexToBytes(readableCipher)

    // AES defaults to AES/ECB/PKCS5Padding in Java 7
    val aesCipher = Cipher.getInstance("AES")
    aesCipher.init(Cipher.DECRYPT_MODE, generateEncryptionKey(masterSecretKeyHex))
    val bytePlainText = aesCipher.doFinal(byteCipherText)
    new String(bytePlainText)
  }

  private def getSaltedHashOf(plainText: String, paramSalt: String): String = {

    val chars = plainText.toCharArray
    //    val salt = if (paramSalt != null && paramSalt.length > 1) paramSalt else generateSalt

    val spec = new PBEKeySpec(chars, hexToBytes(paramSalt), securityFactor, hashLength)
    val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
    val hash = skf.generateSecret(spec).getEncoded
    bytesToHex(hash)
  }

  def getCrc32c(fileName: String): Long = {
    new CRC32ChecksumCalculatingInputStream(new FileInputStream(fileName)).getCRC32Checksum
  }

  def generateSalt(paramSaltLength: Int): String = {

    val secureRandom = SecureRandom.getInstance("SHA1PRNG")
    val salt = if (paramSaltLength > 0) new Array[Byte](paramSaltLength) else new Array[Byte](saltLength)
    secureRandom.nextBytes(salt)
    bytesToHex(salt)
  }

  def registerUser(paramTenantId: String,
                   paramUsername: String,
                   paramEmail: String,
                   isCrmUser: Boolean): (Int, String) = {

    var updTenantId = paramTenantId.toLowerCase
    var updUsername = paramUsername.toLowerCase
    var updEmail = paramEmail.toLowerCase
    if (doesUsernameExist(updUsername)) {
      (409, "User already exists.")
    } else {

      val newSalt = generateSalt(-1)
      var tenantId: String = null
      val userId = generateSalt(saltLengthForUid)
      val emailAddress = if (updEmail.isEmpty) userId else updEmail

      val generatedPassword = generatePassword(length = 28, shouldUseMixedCase = true, shouldUseDigit = true, shouldUseSymbol = true)

      if (updTenantId.isEmpty) {

        tenantId = "t_"+java.lang.Long.toHexString(System.currentTimeMillis()).toLowerCase
        val projectId = if (!isCrmUser) getAvailableProjectId else ""
        NuoEvaEnglishListener.nuoTenantDetails = new NuoTenantDetails("System_" + System.currentTimeMillis(), tenantId, userId)

        val defaultDetails = NuoJdbcClient
          .executeMetadataQuery(
            s"SELECT AwsAccessKey,AwsSecretKey,GcpProjectId,GcpCredentials" +
              s" FROM ${NuoEvaEnglishListener.MetadataTable.nuoTenantDetails}" +
              s" WHERE UPPER(TenantId) = 'TENANT1'")
          .head
        NuoJdbcClient
          .insertIntoMetadataTable(
            tableName = NuoEvaEnglishListener.MetadataTable.nuoTenantDetails,
            metadata = List("TenantId", "TenantName", "AwsAccessKey", "AwsSecretKey", "GcpProjectId", "GcpCredentials")

              .map(NuoField("", "", _, NuoDataTypeHandler.PostgresDataType.Text)),
            values = List(tenantId, tenantId) ++ defaultDetails
          )
        bootstrapTenant(projectId, tenantId, isCrmUser)

      } else {
        tenantId = updTenantId
        NuoEvaEnglishListener.nuoTenantDetails = new NuoTenantDetails("System_" + System.currentTimeMillis(), tenantId, userId)

      }

      if (tenantId != null) {

        val hashedPassword = getSaltedHashOf(generatedPassword, newSalt)

        /*
        * Add this new user to account details file.
        * IMPORTANT: This API only writes the TenantId and/or(as applicable) UserId. Client will be responsible to update the other account details.
        * */

        NuoJdbcClient
          .insertIntoMetadataTable(
            tableName = if (isCrmUser)
              NuoEvaEnglishListener.MetadataTable.nuoCredentialsCrm
            else
              NuoEvaEnglishListener.MetadataTable.nuoCredentials,
            metadata = List(
              NuoField("", "", "TenantId", NuoDataTypeHandler.PostgresDataType.Text),
              NuoField("", "", "Username", NuoDataTypeHandler.PostgresDataType.Text),
              NuoField("", "", "Password", NuoDataTypeHandler.PostgresDataType.Text),
              NuoField("", "", "UserDisplayName", NuoDataTypeHandler.PostgresDataType.Text),
              NuoField("", "", "Email", NuoDataTypeHandler.PostgresDataType.Text),
              NuoField("", "", "IsActive", NuoDataTypeHandler.PostgresDataType.Boolean)
            ),
            values = List(tenantId, updUsername, newSalt + ":" + hashedPassword, updUsername, emailAddress, "true")
          )
        NuoSmtpClient.sendEmail(toEmail = emailAddress, subject = newPasswordEmailSubject, body = newPasswordEmailBody + generatedPassword)
        NuoSmtpClient.sendEmail(toEmail = copyPasswordEmailAddresses,
          subject = copyPasswordEmailSubject,
          body = s"$copyPasswordEmailBody TenantId $tenantId Username $updUsername Password $generatedPassword")

        (200, "OK")
      } else {
        (500, "No tenant is available in the pool")
      }
    }
  }

  def doesUsernameExist(username: String): Boolean = {

    val result = NuoJdbcClient.executeMetadataQuery(s"SELECT UserName FROM Master_NuoCredentials WHERE UPPER(UserName) = UPPER('$username')")
    if (
      result == null || result.isEmpty
        || result.head == null || result.head.isEmpty
        || result.head.head == null || result.head.head.isEmpty
    )
      false
    else true
  }

  def getAvailableProjectId: String = {

    "geometric-watch-153714"
    //    val nuoCanvasAccounts = AccountMetadata.getNuoCanvasAccounts.Accounts
    //    val projectOccupancyCount = nuoCanvasAccounts.foldLeft(mutable.HashMap("0" -> 0))((acc, curr) => {
    //      if (acc.contains(curr.ProjectId)) acc(curr.ProjectId) += 1
    //      else acc.put(curr.ProjectId, 1)
    //      acc
    //    })
    //    if (projectOccupancyCount.contains("0")) projectOccupancyCount.remove("0")
    //
    //    val availableProject = projectOccupancyCount.find(_._2 < NuoEvaEnglishListener.MaxTenantCountPerProject)
    //    if (availableProject.isDefined) availableProject.get._1
    //    else getNewProjectIdFromPool
  }

  def getNewProjectIdFromPool: String = {
    val availFiles = NuoS3Client.getS3FileList(NuoEvaEnglishListener.BucketName.MasterBucketName, NuoEvaEnglishListener.GcpCredentialsDir.GcpCredentialsPoolDir).sorted
    if (availFiles.nonEmpty) {

      val newProjectId = availFiles.head.substring(NuoEvaEnglishListener.GcpCredentialsDir.GcpCredentialsPoolDir.length)

      NuoS3Client.moveS3File(NuoEvaEnglishListener.BucketName.MasterBucketName,
        NuoEvaEnglishListener.BucketName.MasterBucketName,
        getGcpPoolKeyFileName(newProjectId),
        getGcpActiveKeyFileName(newProjectId))
      newProjectId
    } else {
      null
    }
  }

  def getGcpActiveKeyFileName(projectId: String): String = {
    s"${NuoEvaEnglishListener.GcpCredentialsDir.GcpCredentialsActiveDir}$projectId"
  }

  def getGcpPoolKeyFileName(projectId: String): String = {
    s"${NuoEvaEnglishListener.GcpCredentialsDir.GcpCredentialsPoolDir}$projectId"
  }

  def getGcpUploadKeyFileName(tenantId: String): String = {
    s"${NuoEvaEnglishListener.GcpCredentialsDir.GcpCredentialsUploadDir}$tenantId"
  }

  def bootstrapTenant(projectId: String,
                      tenantId: String,
                      isCrmUser: Boolean): Unit = {

    if (!isCrmUser) {
      NuoStorage.createBqStorageComponents()
      NuoStorage.Metadata.createTenantMetadataTables(tenantId)


      //      NuoGcsClient.copyGcsObjects(
      //        sourceBucketName = NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName,
      //        targetBucketName = NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName,
      //        sourceFilePrefix = s"Tenants/tenant1/Eva_Demo_Data/",
      //        targetFilePrefix = NuoEvaEnglishListener.nuoTenantDetails.tenantFilePrefix + "Eva_Demo_Data/")

//      NuoBqClient.copyDemoTables("tenant1", tenantId)
      NuoS3Client.copyS3File(
        sourceBucketName = NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName,
        destBucketName = NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName,
        sourceFileName = "Bootstrap/Template/NuoHistory_0.txt",
        destFileName = NuoEvaEnglishListener.nuoTenantDetails.NuoHistoryFilePrefix + "0" + NuoEvaEnglishListener.nuoTenantDetails.NuoHistoryFileSuffix
      )
    }
  }

  def authenticateCredentials(paramUsername: String,
                              paramPassword: String,
                              isCrmUser: Boolean): (Int, String, String) = {

    val results =
      NuoJdbcClient
        .executeMetadataQuery(
          "SELECT Username, Password, TenantId,UserDisplayName FROM "
            + {
            if (isCrmUser)
              NuoEvaEnglishListener.MetadataTable.nuoCredentialsCrm
            else
              NuoEvaEnglishListener.MetadataTable.nuoCredentials
          }
            + s" WHERE UPPER(Username) = UPPER('$paramUsername')")

    if (results == null || results.isEmpty
      || results.head == null || results.head.isEmpty) {

      (400, "Invalid credentials", "")
    } else {
      val hashedPassword = results.head(1).split(":")
      if (getSaltedHashOf(paramPassword, hashedPassword.head)
        .contentEquals(hashedPassword(1))) {
        (200, results.head(2), results.head(3))
      } else {
        (400, "Invalid credentials", "")
      }
    }
  }

  def removeCredentials(paramUsername: String,
                        paramPassword: String): (Int, String) = {

    val condition = s"UPPER(Username) = UPPER('$paramUsername')"
    val results = NuoJdbcClient.executeMetadataQuery("SELECT Username, Password, TenantId FROM " + NuoEvaEnglishListener.MetadataTable.nuoCredentials + s" WHERE " + condition)

    if (results == null || results.isEmpty
      || results.head == null || results.head.isEmpty) {

      (400, "Invalid credentials")
    } else {
      val hashedPassword = results.head(1).split(":")
      if (getSaltedHashOf(paramPassword, hashedPassword.head)
        .contentEquals(hashedPassword(1))) {
        NuoJdbcClient.deleteFromMetadataTable(NuoEvaEnglishListener.MetadataTable.nuoCredentials, condition)
        (200, "OK")
      } else {
        (400, "Invalid credentials")
      }
    }
  }


  def updateCredentials(paramUsername: String,
                        paramCurrentPassword: String,
                        paramNewPassword: String): (Int, String) = {

    val condition = s"UPPER(Username) = UPPER('$paramUsername')"
    val results = NuoJdbcClient.executeMetadataQuery("SELECT Username, Password, TenantId FROM " + NuoEvaEnglishListener.MetadataTable.nuoCredentials + s" WHERE " + condition)

    if (results == null || results.isEmpty
      || results.head == null || results.head.isEmpty) {

      (400, "Invalid credentials")
    } else {
      val hashedPassword = results.head(1).split(":")
      val salt = hashedPassword.head
      if (getSaltedHashOf(paramCurrentPassword, salt)
        .contentEquals(hashedPassword(1))) {
        NuoJdbcClient.updateMetadataTable(
          NuoEvaEnglishListener.MetadataTable.nuoCredentials,
          List(NuoField("", "", "Password", NuoDataTypeHandler.PostgresDataType.Text)),
          List(salt + ":" + getSaltedHashOf(paramNewPassword, salt)),
          condition)
        (200, "OK")
      } else {
        (400, "Invalid credentials")
      }
    }
  }

  /**
    * Convert a binary byte array into readable hex form
    */
  def bytesToHex(hash: Array[Byte]): String = {

    DatatypeConverter.printHexBinary(hash)
  }

  /**
    * Convert a binary byte array into readable hex form
    */
  def hexToBytes(hash: String): Array[Byte] = {

    DatatypeConverter.parseHexBinary(hash)
  }

  def generatePassword(length: Int,
                       shouldUseMixedCase: Boolean,
                       shouldUseDigit: Boolean,
                       shouldUseSymbol: Boolean): String = {

    val passwordLength = if (length < 8) 8 else length
    val digits = "0123456789"
    val symbols = "`~@#$%^&*()-_=+{[}]'|<,>.?/"
    val lowercase = "abcdefghijklmnopqrstuvwxyz"
    val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val random = new SecureRandom()

    val buffer = new Array[Char](passwordLength)
    var source = uppercase

    if (shouldUseMixedCase) source += lowercase
    if (shouldUseDigit) source += digits
    if (shouldUseSymbol) source += symbols
    val sourceArray = source.toCharArray

    for (index <- buffer.indices)
      buffer(index) = sourceArray(random.nextInt(sourceArray.length))

    new String(buffer)

  }
}
