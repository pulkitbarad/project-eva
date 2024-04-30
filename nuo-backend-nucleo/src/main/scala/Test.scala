
import java.io.File
import java.util.concurrent.Executors

import logging.NuoLogger
import metadata.NuoRecognitionMetadata
import org.fluttercode.datafactory.impl.DataFactory

import scala.util.Random


//import client.{NuoDataflowClient, NuoS3Client}
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model._
import com.amazonaws.services.s3.transfer.TransferManager
import com.amazonaws.{AmazonClientException, AmazonServiceException}

import scala.collection.JavaConverters._
import scala.collection.mutable

/*Spotify scio imports*/
//import com.spotify.scio._
//import com.spotify.scio.accumulators._

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 06Jan2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object Test {


  val MAX_QUERY_SIZE_BYTES: Int = 256 * 1024
  var JOB_POLL_INTERVAL_MILLIS: Int = 1 * 1000

  def main(args: Array[String]): Unit = {

    generateTestData()
    //

  }


  //
  //  def dataflowTest(): Unit = {
  //
  //    /*
  //SBT
  //runMain
  //  com.spotify.scio.examples.MinimalWordCount
  //  --project=[PROJECT] --runner=DataflowRunner --zone=[ZONE]
  //  --input=gs://dataflow-samples/shakespeare/kinglear.txt
  //  --output=gs://[BUCKET]/[PATH]/minimal_wordcount
  //*/
  //
  //        val sc = ScioContext()
  //
  //        val input = "gs://dataflow-samples/shakespeare/kinglear.txt"
  //        val output = "gs://tenant1-backend-nuocanvas-com/out.txt"
  //
  //        // initialize accumulators
  //        val max = sc.maxAccumulator[Int]("maxLineLength")
  //        val min = sc.minAccumulator[Int]("minLineLength")
  //        val sumNonEmpty = sc.sumAccumulator[Long]("nonEmptyLines")
  //        val sumEmpty = sc.sumAccumulator[Long]("emptyLines")
  //
  //        sc.textFile(input)
  //          .map(_.trim)
  //          .accumulateBy(max, min)(_.length)
  //          .accumulateCountFilter(sumNonEmpty, sumEmpty)(_.nonEmpty)
  //          .flatMap(_.split("[^a-zA-Z']+").filter(_.nonEmpty))
  //          .countByValue
  //          .map(t => t._1 + ": " + t._2)
  //          .saveAsTextFile(output)
  //
  //        val result = sc.close().waitUntilFinish()
  //
  //        // scalastyle:off regex
  //        // retrieve accumulator values
  //        println("Max: " + result.accumulatorTotalValue(max))
  //        println("Min: " + result.accumulatorTotalValue(min))
  //        println("Sum non-empty: " + result.accumulatorTotalValue(sumNonEmpty))
  //        println("Sum empty: " + result.accumulatorTotalValue(sumEmpty))
  //
  //  }
  //

  def uploadTest(): Unit = {


    uploadToS3HighLevel("E:\\NuoCanvas\\Source_Code\\Idea_Projects\\nuo-backend-nucleo\\out\\artifacts\\nuo-backend-nucleo.jar",
      "Avellino/nuo-backend-nucleo/nuo-backend-nucleo.jar")

    uploadToS3HighLevel("E:\\NuoCanvas\\Source_Code\\Idea_Projects\\nuo-backend-controllore\\out\\artifacts\\nuo-backend-controllore.jar",
      "Avellino/nuo-backend-controllore/nuo-backend-controllore.jar")

  }

  //
  //  def dryRunTest(): Unit = {
  //    val projectId = "geometric-watch-153714"
  //
  //    val job = new Job()
  //    val jobConfig = new JobConfiguration()
  //    val queryConfig = new JobConfigurationQuery()
  //      .setQuery("select * from Comments_text")
  //      .setUseLegacySql(false)
  //      .setAllowLargeResults(false)
  //      .setDefaultDataset(new DatasetReference().setProjectId(projectId).setDatasetId("default"))
  //      .setUseQueryCache(false)
  //
  //    jobConfig.setQuery(queryConfig)
  //    job.setConfiguration(jobConfig)
  //
  //    val jobObject = bigqueryClient.jobs().insert(projectId, new Job().setConfiguration(jobConfig.setQuery(queryConfig))).execute()
  //
  //    println(s"usage=${jobObject.getStatistics.getTotalBytesProcessed}")
  //    println(s"id1=${jobObject.getJobReference.getJobId}")
  //    println(s"id2=${jobObject.getId}")
  //  }
  //
  //  def storageBillingTest(): Unit = {
  //    val projectId = "geometric-watch-153714"
  //    //    val datasetId = "my_new_dataset"
  //    val bucketName = "tenant1-data-in-backend-nuocanvas-com"
  //
  //    var totalBytes = 0l
  //    bigqueryClient.datasets().list(projectId).execute().getDatasets.asScala.map(_.getDatasetReference.getDatasetId).foreach { datasetId =>
  //
  //      var bqResponse = bigqueryClient.tables().list(projectId, datasetId).execute()
  //      bqResponse.getTables.asScala.map(_.getTableReference.getTableId).foreach { tableId =>
  //        totalBytes += bigqueryClient.tables().get(projectId, datasetId, tableId).execute().getNumBytes
  //      }
  //      if (bqResponse.getTables.size() < bqResponse.getTotalItems) {
  //
  //        var nextToken = bqResponse.getNextPageToken
  //        while (nextToken != null && nextToken.nonEmpty) {
  //
  //          bqResponse = bigqueryClient.tables().list(projectId, datasetId).setPageToken(nextToken).execute()
  //          bqResponse.getTables.asScala.map(_.getTableReference.getTableId).foreach { tableId =>
  //            totalBytes += bigqueryClient.tables().get(projectId, datasetId, tableId).execute().getNumBytes
  //          }
  //          nextToken = bqResponse.getNextPageToken
  //        }
  //      }
  //    }
  //
  //    var storageResponse = storageClient.objects().list(bucketName).execute()
  //    totalBytes += storageResponse.getItems.asScala.map(obj => obj.getSize.longValue()).sum
  //    var nextToken = storageResponse.getNextPageToken
  //
  //    while (nextToken != null && nextToken.nonEmpty) {
  //
  //      storageResponse = storageClient.objects().list(bucketName).setPageToken(nextToken).execute()
  //      totalBytes += storageResponse.getItems.asScala.map(obj => obj.getSize.longValue()).sum
  //      nextToken = storageResponse.getNextPageToken
  //    }
  //    println(s"total size = ${totalBytes / 1024 / 1024}")
  //
  //  }
  //
  ////  def createGCSClients(): Unit = {
  ////
  ////
  ////    val transport = new NetHttpTransport
  ////    val jsonFactory = new JacksonFactory
  ////    var credential = GoogleCredential.fromStream(NuoS3Client.getS3FileInputStream("master-backend-nuocanvas-com", "Credentials/Gcp.json"))
  ////    if (credential.createScopedRequired) credential = credential.createScoped(BigqueryScopes.all)
  ////    bigqueryClient = new Bigquery.Builder(transport, jsonFactory, credential).setApplicationName("Bigquery Test Server App").build
  ////    transferClient = new Storagetransfer.Builder(transport, jsonFactory, credential).setApplicationName("Transfer Test Server App").build
  ////    storageClient = new Storage.Builder(transport, jsonFactory, credential).setApplicationName("Storage Server App").build
  ////
  ////  }
  //
  //  def test(): Unit = {
  //    val startDate = new Date()
  //      .setYear(Calendar.getInstance().get(Calendar.YEAR))
  //      .setMonth(Calendar.getInstance().get(Calendar.MONTH))
  //      .setDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
  //
  //
  //    var objectConditions = new ObjectConditions()
  //    objectConditions = objectConditions.setIncludePrefixes(List[String]("SampleCSVData/SampleData").asJava)
  //    try {
  //      val transferJob =
  //        new TransferJob()
  //          .setDescription("test")
  //          .setProjectId("geometric-watch-153714")
  //          .setTransferSpec(new TransferSpec()
  //            .setAwsS3DataSource(new AwsS3Data()
  //              .setBucketName("nuocluster.nuocanvas.com")
  //              .setAwsAccessKey(new AwsAccessKey()
  //                .setAccessKeyId("")
  //                .setSecretAccessKey("")))
  //            .setObjectConditions(objectConditions)
  //            .setTransferOptions(new TransferOptions()
  //              .setOverwriteObjectsAlreadyExistingInSink(false)
  //              .setDeleteObjectsFromSourceAfterTransfer(false))
  //            .setGcsDataSink(new GcsData()
  //              .setBucketName("internal-nuocanvas-com")))
  //          .setSchedule(new Schedule()
  //            .setScheduleStartDate(startDate)
  //            .setScheduleEndDate(startDate))
  //          .setStatus("ENABLED")
  //
  //      transferClient.transferJobs().create(transferJob).execute()
  //    } catch {
  //      case e: Exception => printException(e)
  //    }
  //  }
  //
  //  def getSchemaFromQuery(): Unit = {
  //    val projectId = "geometric-watch-153714"
  //    val DatasetName = "my_new_dataset"
  //    val querySql = s"SELECT * FROM `${projectId}.${DatasetName}.Comments` LIMIT 1"
  //    val EntityName = "Comments"
  //
  //
  //    val content = new Table()
  //    val tableReference = new TableReference()
  //    tableReference.setTableId("y1")
  //    tableReference.setDatasetId(DatasetName)
  //    tableReference.setProjectId(projectId)
  //    content.setTableReference(tableReference)
  //
  //    val view = new ViewDefinition()
  //    view.setQuery(querySql).setUseLegacySql(false)
  //    content.setView(view)
  //    bigqueryClient.tables().insert(projectId, DatasetName, content).execute()
  //
  //    val viewRef = bigqueryClient.tables().get(projectId, DatasetName, "y1").execute()
  //
  //    println(viewRef.getFields.getFields.asScala.map(_.getType).mkString("\n"))
  //  }
  //
  //  def asyncBqJob(): Unit = {
  //
  //    val projectId = "geometric-watch-153714"
  //    val DatasetName = "my_new_dataset"
  //    val EntityName = "table2"
  //
  //    val result = bigqueryClient.tabledata().list(projectId, DatasetName, EntityName)
  //      .setMaxResults(10l)
  //      .setStartIndex(BigInteger.valueOf(10))
  //      .execute()
  //
  //    result.getRows.asScala.map(_.getF.asScala.map(_.getV))
  //  }

  def uploadToS3HighLevel(sourceFileName: String, s3FileName: String): Unit = {

    val accessKey = "xxx"
    val secretKey = "xxx"
    val bucketName = "deployment-nuocanvas-com"

    val sourceFile = new File(sourceFileName)
    val awsS3Credentials = new BasicAWSCredentials(accessKey, secretKey)
    val amazonS3Client = new AmazonS3Client(awsS3Credentials)
    val contentLength = sourceFile.length()

    val tm = new TransferManager(amazonS3Client)
    // TransferManager processes all transfers asynchronously,
    // so this call will return immediately.
    NuoLogger.printInfo("Upload started")
    val startTime = System.currentTimeMillis()
    val upload = tm.upload(
      bucketName, s3FileName, sourceFile)


    try {
      // Or you can block and wait for the upload to finish
      upload.waitForCompletion()
      val timeElapsedSec = (System.currentTimeMillis() - startTime) / 1000
      NuoLogger.printInfo(s"Uploaded ${s3FileName} of size ${contentLength / 1024 / 1024} MB " +
        s"in ${timeElapsedSec / 60.0} minute." +
        s"at the throughput of ${contentLength / 1024 / timeElapsedSec} KB/s .")
    } catch {
      case e: Exception =>
        NuoLogger.printInfo(s"Unable to upload file ${s3FileName}, upload was aborted.")
        e.printStackTrace()
    }
  }

  def uploadToS3LowLevel(): Unit = {

    val accessKey = "xxx"
    val secretKey = "xxx"
    val bucketName = "nuoanalyzer.nuocanvas.com"
    val s3FileName = "nuo-api-handler/artifacts/nuo-api-handler.jar"
    val sourceFileName = "E:\\NuoCanvas\\Source_Code\\Idea_Projects\\nuo-api-handler\\out\\artifacts\\nuo-api-handler.jar"
    val sourceFile = new File(sourceFileName)
    val awsS3Credentials = new BasicAWSCredentials(accessKey, secretKey)
    val amazonS3Client = new AmazonS3Client(awsS3Credentials)

    val threadCount = 10
    val execServicePool = Executors.newFixedThreadPool(threadCount)

    val contentLength = sourceFile.length()

    var filePosition: Long = 0l
    var i = 1
    // Create a list of UploadPartResponse objects. You get one of these for
    // each part upload.
    val partETags = new mutable.ArrayBuffer[PartETag]()

    // Step 1: Initialize.
    val initRequest = new InitiateMultipartUploadRequest(bucketName, s3FileName)
    val initResponse = amazonS3Client.initiateMultipartUpload(initRequest)

    var partSize = 5 * 1024 * 1024L // Set part size to 5 MB.
    try {

      // Step 2: Upload parts.
      while (filePosition < contentLength) {
        // Last part can be less than 5 MB. Adjust part size.
        partSize = math.min(partSize, contentLength - filePosition)

        // Create request to upload a part.
        val uploadRequest = new UploadPartRequest()
          .withBucketName(bucketName).withKey(s3FileName)
          .withUploadId(initResponse.getUploadId).withPartNumber(i)
          .withFileOffset(filePosition)
          .withFile(sourceFile)
          .withPartSize(partSize)

        // Upload part and add response to our list.
        partETags += amazonS3Client.uploadPart(uploadRequest).getPartETag

        filePosition += partSize
        i += 1
      }

      // Step 3: Complete.
      val compRequest = new CompleteMultipartUploadRequest(bucketName,
        s3FileName,
        initResponse.getUploadId,
        partETags.asJava)

      amazonS3Client.completeMultipartUpload(compRequest);
    } catch {
      case e: Exception =>
        System.err.println(e)
        println("Unable to put object as multipart to Amazon S3 for file " + sourceFile.getName)
        amazonS3Client.abortMultipartUpload(
          new AbortMultipartUploadRequest(
            bucketName, sourceFile.getName, initResponse.getUploadId))
      case ase: AmazonServiceException =>
      case ace: AmazonClientException =>
    }
  }

  def generateTestData(): Unit = {

    val dir = "/NuoCanvas/Data/Eva_Demo_Data/"



    //    generateBankData(dir + "Bank")
    //    generateAccountData(dir + "Account/Account", 5 * 1000 * 1000)
    //    generateCreditCardData(dir + "Credit_Card/Credit_Card", 2 * 1000 * 1000)
    //    generateTransactionData(
    //      fileName = dir + "Transaction/Transaction",
    //      maxCount = 10 * 1000 * 1000,
    //      accountMaxCount = 5 * 1000 * 1000,
    //      creditCardMaxCount = 2 * 100 * 1000
    //    )
    generateAddressData(dir + "Address/Address", 5 * 1000 * 1000)
  }

  def generateBankData(fileName: String) {

    val bankHeader =
      List(
        "Bank_Code",
        "Bank_Name",
        "Address_Code",
        "Telephone"
      )

    var bankData = mutable.ArrayBuffer[List[String]]()
    var random = new Random()

    for (i <- 1 to 120) {

      var bankRow = mutable.ArrayBuffer[String]()
      bankRow += "Bank_" + i
      bankRow += bankNames(i - 1)
      bankRow += "Address_" + i
      var phone = "0"
      for (i <- 1 to 8) {
        phone += random.nextInt(10) + 1
      }
      bankRow += phone
      bankData += bankRow.toList
    }
    NuoRecognitionMetadata.writeFileLocal(fileName + ".txt", (bankHeader +: bankData).map(_.mkString(";")).mkString("\n"))

  }

  def generateAccountData(fileName: String, maxCount: Long) {

    val customerHeader =
      List(
        "Account_Number",
        "Account_Holder_Name",
        "Bank_Code",
        "Address_Code",
        "Opened_At"
      )

    val dataFactory = new DataFactory()
    var random = new Random()

    var customerData = mutable.ArrayBuffer[List[String]]()
    var bankCounter = 1
    var addressCounter = 122
    val accountNumber = 5213030160121608L
    var i = 1
    while (i <= maxCount) {

      bankCounter = random.nextInt(120) + 1

      var customerRow = mutable.ArrayBuffer[String]()
      customerRow += (accountNumber + i).toString
      customerRow += {
        if (i % 3 == 0)
          dataFactory.getBusinessName
        else dataFactory.getName
      }
      customerRow += "Bank_" + bankCounter
      customerRow += "Address_" + addressCounter
      customerRow += f"20${random.nextInt(18)}%02d-${random.nextInt(12) + 1}%02d-${random.nextInt(27) + 1}%02d"
      customerData += customerRow.toList

      if (i % (100 * 1000) == 0) {

        NuoRecognitionMetadata.writeFileLocal(fileName + "_" + i + ".txt", (customerHeader +: customerData).map(_.mkString(";")).mkString("\n"))
        customerData = mutable.ArrayBuffer[List[String]]()
      }
      i += 1
      addressCounter += 1
    }
  }

  def generateCreditCardData(fileName: String, maxCount: Long) {


    val creditCardHeader =
      List(
        "Credit_Card_Number",
        "Credit_Card_Holder_Name",
        "Bank_Code",
        "Address_Code",
        "Card_Type",
        "Valid_From",
        "Valid_Until"
      )

    val cardType =
      List(
        "Visa Platinum",
        "Visa Gold",
        "Visa World Card",
        "MasterCard Flying Blue Silver",
        "MasterCard Flying Blue Gold",
        "MasterCard Black",
        "Capital One",
        "American Express Gold",
        "American Express Platinum"
      )

    val dataFactory = new DataFactory()
    var random = new Random()

    var creditCardData = mutable.ArrayBuffer[List[String]]()
    var bankCounter = 1
    var addressCounter = 122
    val creditCardNumber = 4303036010121608L
    var i = 1
    while (i <= maxCount) {

      bankCounter = random.nextInt(120) + 1

      var creditCardRow = mutable.ArrayBuffer[String]()
      creditCardRow += (creditCardNumber + i).toString
      creditCardRow += {
        if (i % 3 == 0)
          dataFactory.getBusinessName
        else dataFactory.getName
      }
      creditCardRow += "Bank_" + bankCounter
      creditCardRow += "Address_" + addressCounter
      creditCardRow += cardType(random.nextInt(8))
      var validFromYear = random.nextInt(18)
      creditCardRow += f"20$validFromYear%02d-${random.nextInt(12) + 1}%02d-${random.nextInt(27) + 1}%02d"
      creditCardRow += f"20${validFromYear + 5}%02d-${random.nextInt(12) + 1}%02d-${random.nextInt(27) + 1}%02d"
      creditCardData += creditCardRow.toList

      if (i % (100 * 1000) == 0) {

        NuoRecognitionMetadata.writeFileLocal(fileName + "_" + i + ".txt", (creditCardHeader +: creditCardData).map(_.mkString(";")).mkString("\n"))
        creditCardData = mutable.ArrayBuffer[List[String]]()
      }
      i += 1
      addressCounter += 1
    }
  }

  def generateTransactionData(fileName: String,
                              maxCount: Long,
                              accountMaxCount: Int,
                              creditCardMaxCount: Int) {


    val transactionHeader =
      List(
        "Transaction_Number",
        "Reference_Number",
        "Is_Debit",
        "Amount",
        "Transaction_Date",
        "Type"
      )

    val transactionType =
      List(
        "ACCOUNT",
        "CREDIT_CARD"
      )

    val random = new Random()

    var transactionData = mutable.ArrayBuffer[List[String]]()
    val transactionNumber = 6010143030321608L
    var i = 1
    while (i <= maxCount) {

      var transactionTypeRandom = random.nextInt(2)

      var transactionRow = mutable.ArrayBuffer[String]()
      transactionRow += (transactionNumber + i).toString
      transactionRow += {
        if (transactionTypeRandom == 0)
          (random.nextInt(accountMaxCount + 1) + 5213030160121608L).toString
        else (random.nextInt(creditCardMaxCount + 1) + 4303036010121608L).toString
      }
      transactionRow += List("true", "false")(random.nextInt(2))
      transactionRow += (random.nextInt(10 * 1000) + (random.nextInt(100) / 100.0)).toString
      var date = f"20${random.nextInt(18)}%02d-${random.nextInt(12) + 1}%02d-${random.nextInt(27) + 1}%02d"
      var time = f"${random.nextInt(24)}%02d:${random.nextInt(60)}%02d:${random.nextInt(60)}%02d"
      transactionRow += s"$date $time"
      transactionRow += transactionType(transactionTypeRandom)
      transactionData += transactionRow.toList

      if (i % (100 * 1000) == 0) {

        NuoRecognitionMetadata.writeFileLocal(fileName + "_" + i + ".txt", (transactionHeader +: transactionData).map(_.mkString(";")).mkString("\n"))
        transactionData = mutable.ArrayBuffer[List[String]]()
      }
      i += 1
    }
  }

  def generateAddressData(fileName: String,
                          maxCount: Long) {


    val addressHeader =
      List(
        "AddressCode",
        "Street",
        "HouseNum",
        "City",
        "Country",
        "PostalCode"
      )


    val countryList =
      List(
        "Austria",
        "Belgium",
        "Bulgaria",
        "Cyprus",
        "Czech Republic",
        "Denmark",
        "Estonia",
        "Finland",
        "France",
        "Germany",
        "Greece",
        "Hungary",
        "Ireland",
        "Italy",
        "Luxembourg",
        "Netherlands",
        "Portugal",
        "Spain",
        "Sweden"

      )

    val dataFactory = new DataFactory()
    val random = new Random()
    var addressData = mutable.ArrayBuffer[List[String]]()
    var i = 1
    while (i <= maxCount) {

      var addressRow = mutable.ArrayBuffer[String]()
      addressRow += "Address_" + i
      addressRow += dataFactory.getStreetName + " Street"
      val houseNum = dataFactory.getAddressLine2
      addressRow += houseNum.substring(houseNum.indexOf("#") + 1)
      addressRow += dataFactory.getCity
      addressRow += countryList(random.nextInt(19))
      addressRow += f"${random.nextInt(100)}%02d${random.nextInt(100)}%02d" + (random.nextInt(26) + 65).toChar + (random.nextInt(26) + 65).toChar

      addressData += addressRow.toList

      if (i % (100 * 1000) == 0) {

        NuoRecognitionMetadata.writeFileLocal(fileName + "_" + i + ".txt", (addressHeader +: addressData).map(_.mkString(";")).mkString("\n"))
        addressData = mutable.ArrayBuffer[List[String]]()
      }
      i += 1
    }
  }

  val bankNames =
    List(
      "Achmea Bank Holding N.V.",
      "AlpInvest Partners",
      "Amsterdam Trade Bank N.V.",
      "Bank Insinger de Beaufort N.V.",
      "Bank Mendes Gans N.V.",
      "Bank Nederlandse Gemeenten",
      "Bank of New York Mellon",
      "Bank Ten Cate & Cie. N.V.",
      "Binck N.V.",
      "Demir Halk Bank N.V.",
      "Effectenbank Stroeve N.V.",
      "F. van Lanschot Bankiers N.V.",
      "Friesland Bank N.V.",
      "Garantibank International N.V.",
      "GWK Bank N.V.",
      "KAS BANK N.V.",
      "KBC Bank N.V. Nederland",
      "Netherlands Development Finance Company (FMO)",
      "Petercam Bank N.V.",
      "SNS Bank N.V.",
      "Staalbankiers N.V.",
      "Theodoor Gilissen Bankiers N.V.",
      "Triodos Bank",
      "Antwerpse Diamantbank",
      "Argenta Spaarbank",
      "Banca Monte Paschi Belgio",
      "Bank Degroof",
      "Bank Delen & de Schaetzen Bank Delen of Bank de Schaetzen",
      "DDS Bank",
      "Banque Eni",
      "Banque Transatlantique Belgium",
      "Beobank",
      "Bpost Bank",
      "Byblos Bank Europe",
      "CBC Banque",
      "Commerzbank Belgium",
      "Delta Lloyd Bank",
      "Ethias Bank",
      "Euroclear Bank",
      "Europabank",
      "Goffin Bank",
      "Keytrade Bank",
      "MeDirect Bank Belgium",
      "Petercam",
      "Puilaetco Dewaay Private Bankers",
      "Puilaetco Private Bankers",
      "Santander Benelux",
      "SG Private Banking",
      "Shizuoka Bank (Europe)",
      "Triodos Bank",
      "UBS Belgium",
      "United Taiwan Bank",
      "Van Lanschot Bankiers België",
      "B&N Bank, Greek Branch",
      "Bank of America",
      "Bank of Cyprus",
      "Bank Saderat Iran",
      "BMW Austria Bank (de)",
      "BNP Paribas Securities Services",
      "Citibank Europe",
      "Credit Suisse (Luxembourg)",
      "DVB Bank",
      "FCA Bank",
      "FCE Bank (de)",
      "Fimbank",
      "HSH Nordbank",
      "Opel Bank",
      "ProCredit Bank (Bulgaria)",
      "The Royal Bank of Scotland",
      "UniCredit Bank",
      "Volkswagen Bank (de)",
      "Ziraat Bankası",
      "BayernLB, Munich",
      "Comdirect, Quickborn",
      "Commerzbank, Frankfurt",
      "Consorsbank, Nuremberg",
      "DAB BNP Paribas, Munich",
      "DekaBank Deutsche Girozentrale, Frankfurt",
      "Deutsche Bank, Frankfurt",
      "Deutsche Pfandbriefbank, Unterschleißheim (next to Munich)",
      "DZ Bank, Frankfurt",
      "GLS Bank, Bochum",
      "HSH Nordbank, Hamburg/Kiel (dual seat)",
      "KfW, Frankfurt",
      "Landesbank Baden-Württemberg, Stuttgart",
      "Landesbank Berlin Holding, Berlin",
      "Landesbank Hessen-Thüringen, Frankfurt",
      "Landwirtschaftliche Rentenbank",
      "N26, Berlin",
      "Nord/LB, Hanover",
      "NRW.Bank, Düsseldorf",
      "Portigon Financial Services, Duesseldorf",
      "solarisBank, Berlin",
      "Wirecard Bank",
      "HSBC France",
      "La Poste",
      "Kanz Bank",
      "La Roche & Co Banquiers",
      "Lombard, Odier & Cie",
      "Maerki, Baumann and Co. AG",
      "MigrosBank",
      "Mirabaud",
      "Morval & Cie SA Banque",
      "Pictet",
      "Rabo Robeco Bank",
      "Rahn & Bodmer Banquiers",
      "Rosbank (Switzerland)",
      "S&B Investment Bank",
      "SG Ruegg Bank",
      "Skandia Bank (Switzerland) AG",
      "Solothurner Bank (Soba)",
      "Swiss National Bank",
      "Swissnetbank.com AG",
      "UBS AG",
      "Union Bancaire Privée",
      "Union Suisse des Banques Raiffeisen (USBR)",
      "Valiant Bank",
      "Volksbank Bodensee AG",
      "VP Bank (Schweiz) AG",
      "Wegelin & Co"
    )


}