package action


import canvas.NuoModifier
import client.NuoJdbcClient.createMetadataTable
import client.{NuoGcsClient, NuoHttpClient, NuoS3Client}
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.bigquery.model._
import logging.NuoLogger
import logging.NuoLogger.printInfo
import metadata.StorageMetadata.NuoField
import net.liftweb.json.JsonDSL._
import net.liftweb.json.compactRender
import nlp.grammar.NuoEvaEnglishListener

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object NuoStorage {

  object Modifiers {
    val ColumnName: String = "Target"
    val DataType: String = "Data_Type"
    val Precision: String = "Precision"
    val Scale: String = "Scale"
    val ColumnSource: String = "Source"
  }

  object Metadata {

    def createTenantMetadataTables(tenantId: String): Unit = {
      createNuoRelationshipsTable(tenantId)
      createNuoHistoryTable(tenantId)
      createNuoExecutionMonitorTable(tenantId)
    }

    def createNuoHistoryTable(tenantId: String): Unit = {

      val columns = List(
        NuoField(DatasetName = "", EntityName = "", FieldName = "AnalysisId", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "CreatedAt", DataType = "bigint"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "LastModifiedAt", DataType = "bigint"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Author", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Title", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Selection", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "SelectionRaw", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Filter", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "FilterRaw", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "ResultTableName", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "AliasMapping", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Result", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "ProfilingResult", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Pattern", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Visualization", DataType = "text")
      )
      createMetadataTable(NuoEvaEnglishListener.nuoTenantDetails.MetadataTable.nuoHistoryFeed, columns)
    }

    def createNuoRelationshipsTable(tenantId: String): Unit = {

      val columns = List(
        NuoField(DatasetName = "", EntityName = "", FieldName = "LeftEntityName", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "RightEntityName", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "IntermediateEntities", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "CommonFields", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "JoinClause", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "IsVerified", DataType = "Boolean")
      )
      createMetadataTable(NuoEvaEnglishListener.nuoTenantDetails.MetadataTable.nuoRelationships, columns)
    }

    def createNuoExecutionMonitorTable(tenantId: String): Unit = {

      val columns = List(
        NuoField(DatasetName = "", EntityName = "", FieldName = "Timestamp", DataType = "bigint"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "RequestId", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "AnalysisId", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "AnalysisHash", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "CompletedSteps", DataType = "int"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "TotalSteps", DataType = "int"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "ExecutionStartedAt", DataType = "bigint"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "ExecutionCompletedAt", DataType = "bigint"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "RefParams", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "isSucceeded", DataType = "Boolean")
      )
      createMetadataTable(NuoEvaEnglishListener.nuoTenantDetails.MetadataTable.nuoExecutionMonitor, columns)
    }

    def createNuoUsageTable(tenantId: String): Unit = {

      val columns = List(
        NuoField(DatasetName = "", EntityName = "", FieldName = "Timestamp", DataType = "bigint"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "RequestId", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "AnalysisId", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "AnalysisHash", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "UnitOfMeasure", DataType = "int"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "UsageCount", DataType = "float8")
      )
      createMetadataTable(NuoEvaEnglishListener.nuoTenantDetails.MetadataTable.nuoUsage, columns)
    }

    def createNuoCredentialsTable(): Unit = {


      val columns = List(

        NuoField(DatasetName = "", EntityName = "", FieldName = "UserId", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Username", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Email", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Password", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "TenantId", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "IsActive ", DataType = "Boolean")
      )
      createMetadataTable(NuoEvaEnglishListener.MetadataTable.nuoCredentials, columns)
    }

    def createNuoSessionTable(): Unit = {


      val columns = List(

        NuoField(DatasetName = "", EntityName = "", FieldName = "SessionId", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "CreatedAt", DataType = "bigint"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "LastModifiedAt", DataType = "bigint"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Username", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "TenantId", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "IsActive ", DataType = "Boolean")
      )
      createMetadataTable(NuoEvaEnglishListener.MetadataTable.nuoSession, columns)
    }

    def createNuoTenantDetailsTable(): Unit = {


      val columns = List(

        NuoField(DatasetName = "", EntityName = "", FieldName = "TenantId", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "TenantName", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Email", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "GcpCredentials", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "GcpProjectId", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "GcpRegion", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "AwsAccessKey", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "AwsSecretKey", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "AwsRegion", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "PricingPlanCode", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "AddressId", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "CompanyId", DataType = "text")
      )

      createMetadataTable(NuoEvaEnglishListener.MetadataTable.nuoTenantDetails, columns)
    }

    def createNuoAddressTable(): Unit = {


      val columns = List(

        NuoField(DatasetName = "", EntityName = "", FieldName = "AddressId", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Address", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "City", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "StateOrProvince", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Country", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "PostalCode", DataType = "text")
      )

      createMetadataTable(NuoEvaEnglishListener.MetadataTable.nuoAddress, columns)
    }

    def createNuoCompanyDetailsTable(): Unit = {


      val columns = List(
        NuoField(DatasetName = "", EntityName = "", FieldName = "CompanyId", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "CompanyName", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "RegistrationNumber", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "TaxNumber", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Website", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "PrimaryEmail", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "SecondaryEmail", DataType = "text"),
        NuoField(DatasetName = "", EntityName = "", FieldName = "Telephone", DataType = "text")
      )
      createMetadataTable(NuoEvaEnglishListener.MetadataTable.nuoCompanyDetails, columns)
    }

  }

  def transferFilesBetweenS3(isImportFromS3: Boolean,
                             isExportToS3: Boolean,
                             s3BucketName: String,
                             s3FilePrefix: String,
                             gcsBucketName: String,
                             nuoFilePrefix: String,
                             nuoEvaEnglishListener: NuoEvaEnglishListener): Long = {


    callTransferFunction(
      actionType = if (isImportFromS3) NuoModifier.ActionType.Storage.ImportFilesFromS3 else if (isExportToS3) NuoModifier.ActionType.Storage.ExportFilesToS3 else "NA",
      s3BucketName = s3BucketName,
      s3FilePrefix = s3FilePrefix,
      gcsBucketName = gcsBucketName,
      gcsFilePrefix = nuoFilePrefix,
      transferBatchSizeBytes = 500 * 1024 * 1024l,
      functionTimeoutMilli = 540 * 1000l,
      maxRetryCount = 1,
      nuoEvaEnglishListener)

    if (isImportFromS3)
      NuoS3Client.getS3ObjectListIterator(s3BucketName,
        s3FilePrefix,
        NuoS3Client.createCustomAwsS3Client(NuoEvaEnglishListener.AwsDetails.AccessKey, NuoEvaEnglishListener.AwsDetails.SecretKey))
        .map(_.getSize)
        .sum
    else if (isExportToS3)
      NuoGcsClient.listGcsFileNameAndSize(gcsBucketName, nuoFilePrefix, null)
        .map(_._2)
        .sum
    else -1l
  }

  def callTransferFunction(actionType: String,
                           s3BucketName: String,
                           s3FilePrefix: String,
                           gcsBucketName: String,
                           gcsFilePrefix: String,
                           transferBatchSizeBytes: Long,
                           functionTimeoutMilli: Long,
                           maxRetryCount: Int,
                           nuoEvaEnglishListener: NuoEvaEnglishListener): Unit = {


    val requestBody = ("RequestId" -> nuoEvaEnglishListener.currAnalysisRecognitionData.AnalysisId) ~
      ("TenantId" -> NuoEvaEnglishListener.nuoTenantDetails.tenantId) ~
      ("Region" -> "us-central1") ~
      //      ("Region" -> NuoEvaEnglishListener.nuoTenantDetails.gcpRegion) ~
      ("RunPlanName" -> "NA") ~
      ("ProjectId" -> NuoEvaEnglishListener.nuoTenantDetails.bqProjectId) ~
      ("FunctionName" -> "nuo-function-transfer") ~
      ("ActionName" -> "NA") ~
      ("ActionType" -> actionType) ~
      ("AwsAccessKeyId" -> NuoEvaEnglishListener.AwsDetails.AccessKey) ~
      ("AwsSecretAccessKey" -> NuoEvaEnglishListener.AwsDetails.SecretKey) ~
      ("S3BucketName" -> s3BucketName) ~
      ("S3FilePrefix" -> s3FilePrefix) ~
      ("GcsBucketName" -> gcsBucketName) ~
      ("GcsFilePrefix" -> gcsFilePrefix) ~
      ("StartIndex" -> 0) ~
      ("MaxResults" -> 1) ~
      ("TotalCount" -> 1) ~
      //      ("TransferBatchSizeBytes" -> 500 * 1024 * 1024) ~
      ("TransferBatchSizeBytes" -> transferBatchSizeBytes) ~
      ("FunctionTimeoutMilli" -> functionTimeoutMilli) ~
      ("MaxRetryCount" -> maxRetryCount)


    NuoHttpClient.sendHttpsPostRequestPlainText(url = s"us-central1-${NuoEvaEnglishListener.nuoTenantDetails.bqProjectId}.cloudfunctions.net",
      port = "443",
      uri = "nuo-function-controllore",
      timeoutMillis = 10,
      requestBody = compactRender(requestBody))

    //    NuoHttpClient.sendHttpsPostRequestPlainText(url = s"${NuoEvaEnglishListener.nuoTenantDetails.gcpRegion}-${NuoEvaEnglishListener.nuoTenantDetails.bqProjectId}.cloudfunctions.net",
    //      port = "443",
    //      uri = "nuo-function-controllore",
    //      timeoutMillis = 10,
    //      requestBody = compactRender(requestBody))
    //
    NuoLogger.printInfo(s"I have invoked the function with body $requestBody")
  }

  def isTransferFunctionComplete(prevRefParams: List[String],
                                 nuoEvaEnglishListener: NuoEvaEnglishListener): Boolean = {

    if (prevRefParams.length < 6 || System.currentTimeMillis() <= prevRefParams(2).toLong) false
    else {


      val expectedSizeBytes = prevRefParams(0)
      val previousCount = prevRefParams(1)
      val minWaitTime = prevRefParams(2)
      val maxWaitTime = prevRefParams(3)
      val targetType = prevRefParams(4)
      val targetBucket = prevRefParams(5)
      val targetPath = prevRefParams(6)

      val currentCount = if (targetType.equalsIgnoreCase("GCS")) {

        NuoGcsClient.listGcsObject(targetBucket, targetPath, null)
          .filter { gcsObject =>
            val requestId = gcsObject.getMetadata.asScala.getOrElse("NuoRequestId", "")
            requestId.nonEmpty && requestId.equals(nuoEvaEnglishListener.currAnalysisRecognitionData.AnalysisId)
          }
          .map(_.getSize.longValueExact())
          .sum
      } else {
        NuoS3Client.getS3ObjectListIterator(targetBucket, targetPath, NuoEvaEnglishListener.Client.amazonS3Client)
          .filter { s3File =>
            val requestId = NuoS3Client.getObjectNuoRequestId(targetBucket, s3File.getKey, NuoEvaEnglishListener.Client.amazonS3Client)
            requestId.isDefined && requestId.get.equals(nuoEvaEnglishListener.currAnalysisRecognitionData.AnalysisId)
          }
          .map(_.getSize)
          .sum
      }
      if (System.currentTimeMillis() > maxWaitTime.toLong) {
        System.err.println(s"Timeout occurred while executing transfer function for request-id  ${NuoEvaEnglishListener.nuoTenantDetails.requestId} because it has been running for ${maxWaitTime.toDouble / 1000.0 / 60.0 / 60.0} minutes." +
          s" Please contact NuoCanvas support.")
        NuoEvaEnglishListener.nuoTenantDetails.errorEncountered = true
        true
      } else if (previousCount.toLong < currentCount) {
        currentCount.toDouble >= expectedSizeBytes.toDouble
      } else
        false
    }
  }


  /*
  * Following function creates tables and database required internally.
  * */
  def createBqStorageComponents(): Unit = {


    /*
    * Create internal database for tenant
    * */
    createBqDataset(NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId)
    createBqDataset(NuoEvaEnglishListener.nuoTenantDetails.userMetadataDatasetNameWOTenantId)
    createBqDataset(NuoEvaEnglishListener.nuoTenantDetails.userCacheDatasetNameWOTenantId)
    createBqDataset(NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId)

    /*
    * Create default database for tenant
    * */
    //    createDatasetInternal(projectId, tenantId, getTenantDefaultDatasetName(tenantId))

    createFunctionStatusTable()
    printInfo(s"I have checked all the internal storage structures for tenant ${NuoEvaEnglishListener.nuoTenantDetails.tenantId}")

  }

  def createFunctionStatusTable(): Unit = {

    val columns = new mutable.ListBuffer[NuoField]()

    columns += NuoField(
      "",
      "",
      "RequestId",
      "String"
    )

    columns += NuoField(
      "",
      "",
      "ActionName",
      "String"
    )

    columns += NuoField(
      "",
      "",
      "StartIndex",
      "Int64"
    )

    columns += NuoField(
      "",
      "",
      "MaxResults",
      "Int64"
    )
    createBqTable(NuoEvaEnglishListener.nuoTenantDetails.userMetadataDatasetNameWOTenantId, NuoEvaEnglishListener.nuoTenantDetails.functionStatusTable, columns.toList)
  }

  def createBqTable(datasetName: String,
                    tableName: String,
                    columns: List[NuoField]): Int = {
    if (!doesBqTableExist(datasetName, tableName)) {

      val qualifiedDatasetName = NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName
      try {
        val table: Table = createBqTableObject(qualifiedDatasetName,
          tableName,
          columns)

        NuoEvaEnglishListener.nuoTenantDetails.bigqueryClient.tables().insert(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
          qualifiedDatasetName,
          table)
          .execute()
        printInfo(s"Successfully created table $tableName.")
        200
      } catch {
        case e: Exception =>
          NuoLogger.printException(e)
          500
      }

    } else {
      NuoLogger.logInfo(s"Table $tableName already exists in dataset $datasetName")
      409
    }
  }

  def createBqTableObject(datasetName: String,
                          tableName: String,
                          columns: List[NuoField]): Table = {

    val schema = new TableSchema()
    val tableFieldSchema = ArrayBuffer[TableFieldSchema]()

    columns.foreach { column =>

      val schemaEntry: TableFieldSchema = new TableFieldSchema()
      schemaEntry.setName(column.FieldName)
      schemaEntry.setType(column.DataType.toUpperCase())
      tableFieldSchema += schemaEntry
    }
    schema.setFields(tableFieldSchema.asJava)

    val tableRef: TableReference = new TableReference()
    tableRef.setDatasetId(datasetName)
    tableRef.setProjectId(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId)
    tableRef.setTableId(tableName)
    if ("".nonEmpty) {
      new Table().setSchema(schema).setTableReference(tableRef).setDescription("")
    } else {
      new Table().setSchema(schema).setTableReference(tableRef)

    }
  }

  def doesBqTableExist(datasetName: String,
                       tableName: String): Boolean = {

    try {
      NuoEvaEnglishListener.nuoTenantDetails.bigqueryClient.tables().get(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId, NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName, tableName).execute()
    } catch {
      case e: GoogleJsonResponseException =>
        if (e.getDetails.getCode == 404 || e.getDetails.getCode == 400) {
          return false
        }
    }
    true
  }

  def createBqDataset(datasetName: String): Int = {
    if (doesBqDatasetExist(datasetName)) {

      printInfo(s"Dataset $datasetName already exists.")
      409
    } else {
      val dataset = new Dataset()
      val datasetRef = new DatasetReference()

      datasetRef.setProjectId(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId)
      datasetRef.setDatasetId(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName)
      dataset.setDatasetReference(datasetRef)
      dataset.setLocation("EU")
      try {

        NuoEvaEnglishListener.nuoTenantDetails.bigqueryClient.datasets().insert(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId, dataset).execute()
        printInfo(s"Successfully created dataset $datasetName.")
        200
      } catch {
        case e: Exception =>
          NuoLogger.printException(e)
          500
      }
    }
  }

  def doesBqDatasetExist(datasetId: String): Boolean = {

    try {
      NuoEvaEnglishListener.nuoTenantDetails.bigqueryClient.datasets().get(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId, NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetId).execute()
    } catch {
      case e: GoogleJsonResponseException =>
        if (e.getDetails.getCode == 404 || e.getDetails.getCode == 400) {
          return false
        }
    }
    true
  }


}

/*

import action.NuoDML._
import action.NuoExpressionAndVariable.processExpression
import canvas.NuoModifier.ActionAttrName._
import canvas.NuoModifier.NuoStorageModifier
import canvas.NuoModifier.NuoStorageModifier._
import canvas.{NuoDataTypeHandler, NuoModifier}
import client.{NuoBqClient, NuoGcsClient, NuoS3Client}
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.bigquery.model._
import dag.{NuoDagProcessor, NuoTenantDetails}
import dag.NuoDagProcessor._
import logging.NuoLogger._
import logging.{NuoBiller, NuoLogger}

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Copyright 2016 Nuocanvas Inc.
  *
  *
  * Created by Pulkit on 28JUL2016.
  *
  * Content of this file is proprietry and confidential.
  * It should not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoStorage {


  object ProfilingMetric {
    val Timestamp = "Timestamp"
    val ProcessName = "Process_Name"
    val ColumnName = "Column_Name"
    val ColumnType = "Column_Type"
    val ColumnPosition = "Column_Position"
    val TotalCount = "Total_Count"
    val NonEmptyCount = "NotNull_Count"
    val UniqueCount = "Unique_Count"
    val UniquenessPercent = "Uniqueness_Percent"
    val EmptyCount = "Null_Count"
    val Min = "Min"
    val Max = "Max"
    val Median = "Median_Approximate"
    val Average = "Average"
    val Range = "Range"
    val StandardDeviation = "Standard_Deviation"
    val BlankCount = "Blank_Count"
    val MinLength = "Min_Length"
    val MaxLength = "Max_Length"
    val AvgLength = "Avg_Length"
    val MedianLengthApproximate = "Median_Length_Approximate"
  }

  /*
  * * This function performs the Create Table operation based on parameters provided in metadata.
  * * */
 def evalCreateTable(userDatasetName: String,
                      userTableName: String,
                      columns: mutable.ListBuffer[mutable.HashMap[String, String]],
                      turnOnLogging: Boolean,
                      nuoTenantDetails: NuoTenantDetails): Unit = {

    val datasetName = if (userDatasetName.isEmpty) {
      if (turnOnLogging)
        NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.", nuoTenantDetails)
      userDefaultDatasetName
    } else userDatasetName

    if (!doesBqTableExist(datasetName, userTableName, nuoTenantDetails)) {
      val projectId = nuoTenantDetails.bqProjectId

      printInfo(s"Creating table $userTableName.", nuoTenantDetails)

      try {
        val table: Table = createBqTableObject(datasetName,
          userTableName,
          columns, nuoTenantDetails)

        nuoTenantDetails.bigqueryClient.tables().insert(projectId,
          nuoTenantDetails.tenantId + "_" + datasetName,
          table)
          .execute()
      } catch {
        case e: Exception =>
          printException(e, nuoTenantDetails)
      }

      printInfo(s"Successfully created table $userTableName.", nuoTenantDetails)
    } else {
      if (turnOnLogging)
        NuoLogger.logWarning(s"The table $userTableName already exists.", nuoTenantDetails)
    }
  }

  /*
  * This function performs the Create Table operation based on parameters provided in metadata.
  * */
  def deleteTable(userDatasetName: String,
                      userTableName: String,
                      nuoTenantDetails: NuoTenantDetails): Unit = {


    val datasetName = if (userDatasetName.isEmpty) {
      NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.", nuoTenantDetails)
      userDefaultDatasetName
    } else userDatasetName

    if (!isInternalTable(datasetName, userTableName, nuoTenantDetails)) {
      if (doesBqTableExist(datasetName, userTableName, nuoTenantDetails)) {
        printInfo(s"Deleting table $userTableName.", nuoTenantDetails)

        try {
          nuoTenantDetails.bigqueryClient.tables().delete(nuoTenantDetails.bqProjectId, nuoTenantDetails.tenantId + "_" + datasetName, userTableName).execute()
        } catch {
          case e: Exception =>
            printException(e, nuoTenantDetails)
        }
        printInfo(s"Successfully deleted table $userTableName.", nuoTenantDetails)
      } else {
        NuoLogger.logWarning(s"Table $userTableName does not exist in database $datasetName.", nuoTenantDetails)
      }
    }
  }

  /*
  * This function performs the Create Index operation based on parameters provided in metadata.
  * */
  def evalDoesTableExist(userDatasetName: String,
                         userTableName: String,
                         nuoTenantDetails: NuoTenantDetails): Unit = {


    val datasetName = if (userDatasetName.isEmpty) {
      //      NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.")
      userDefaultDatasetName
    } else userDatasetName

    printInfo(s"Checking if table $userTableName exists  in dataset $datasetName", nuoTenantDetails)

    val tableExists = doesBqTableExist(datasetName, userTableName, nuoTenantDetails)

    if (tableExists) {

      printInfo(s"Yes,table $userTableName exists  in dataset $datasetName", nuoTenantDetails)
    } else {
      printInfo(s"No, table $userTableName does not exist  in dataset $datasetName", nuoTenantDetails)
    }
  }

  /*
  * Following functions checks whether given table exist in given dataset or not.
  * */
  def doesBqTableExist(datasetName: String,
                     tableName: String,
                     nuoTenantDetails: NuoTenantDetails): Boolean = {

    if (doesBqDatasetExist(datasetName, nuoTenantDetails)) {

      try {
        nuoTenantDetails.bigqueryClient.tables().get(nuoTenantDetails.bqProjectId, nuoTenantDetails.tenantId + "_" + datasetName, tableName).execute()
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

  def doesBqDatasetExist(datasetName: String,
                       nuoTenantDetails: NuoTenantDetails): Boolean = {

    try {
      nuoTenantDetails.bigqueryClient.datasets().get(nuoTenantDetails.bqProjectId, nuoTenantDetails.tenantId + "_" + datasetName).execute()
    } catch {
      case e: GoogleJsonResponseException =>
        if (e.getDetails.getCode == 404 || e.getDetails.getCode == 400) {
          return false
        }
    }
    true
  }

  /*
  * This function performs the Create Index operation based on parameters provided in metadata.
  * */
  def evalCreateDataset(userDatasetName: String, turnOnLogging: Boolean,
                        nuoTenantDetails: NuoTenantDetails): Unit = {

    if (doesBqDatasetExist(userDatasetName, nuoTenantDetails)) {
      if (turnOnLogging)
        logWarning(s"Dataset $userDatasetName already exists.", nuoTenantDetails)

    } else {
      val dataset = new Dataset()
      val datasetRef = new DatasetReference()
      val projectId = nuoTenantDetails.bqProjectId

      datasetRef.setProjectId(projectId)
      datasetRef.setDatasetId(nuoTenantDetails.tenantId + "_" + userDatasetName)
      dataset.setDatasetReference(datasetRef)
      dataset.setLocation("EU")
      try {
        nuoTenantDetails.bigqueryClient.datasets().insert(projectId, dataset).execute()
      } catch {
        case e: Exception =>
          printException(e, nuoTenantDetails)
      }
    }

  }

  /*
  * This function performs the Create Index operation based on parameters provided in metadata.
  * */
  def evalDeleteDataset(actionName: String,
                        userDatasetName: String,
                        nuoTenantDetails: NuoTenantDetails): Unit = {

    if (!List(userDefaultDatasetName, userCacheDatasetName, userMetadataDatasetName, userTempDatasetName).contains(userDatasetName.toLowerCase)) {
      if (doesBqDatasetExist(userDatasetName, nuoTenantDetails)) {
        logWarning(s"Deleting dataset $userDatasetName.", nuoTenantDetails)

        try {
          nuoTenantDetails.bigqueryClient.datasets().delete(nuoTenantDetails.bqProjectId, nuoTenantDetails.tenantId + "_" + userDatasetName).execute()
        } catch {
          case e: Exception =>
            printException(e, nuoTenantDetails)
        }
        printInfo(s"Successfully deleted dataset $userDatasetName.", nuoTenantDetails)
      } else {
        logWarning(s"Dataset $userDatasetName does not exist.", nuoTenantDetails)

      }
    }
  }

  /*
  * This function performs the Change Column operation based on parameters provided in metadata.
  * */
  def evalUpdateTableStructure(userDatasetName: String,
                               userTableName: String,
                               columns: mutable.ListBuffer[mutable.HashMap[String, String]],
                               nuoTenantDetails: NuoTenantDetails): Unit = {


    val datasetName = if (userDatasetName.isEmpty) {
      NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.", nuoTenantDetails)
      userDefaultDatasetName
    } else userDatasetName
    val projectId = nuoTenantDetails.bqProjectId

    if (doesBqTableExist(datasetName, userTableName, nuoTenantDetails)) {

      if (!isInternalTable(userDatasetName, userTableName, nuoTenantDetails)) {

        printInfo(s"Updating table structure for $userTableName.", nuoTenantDetails)

        try {
          val table: Table = createBqTableObject(datasetName,
            userTableName,
            columns, nuoTenantDetails)

          nuoTenantDetails.bigqueryClient.tables().patch(projectId,
            nuoTenantDetails.tenantId + "_" + datasetName,
            userTableName,
            table).execute()
        } catch {
          case e: Exception =>
            printException(e, nuoTenantDetails)
        }
        printInfo(s"Successfully updated table structure of $userTableName.", nuoTenantDetails)
      }
    } else {
      NuoLogger.logWarning(s"The table $userTableName does not exist in dataset $datasetName. I am not goign to update the structure.", nuoTenantDetails)

    }
  }

  def convertNuoColumnsToBqSchema(columns: mutable.ListBuffer[mutable.HashMap[String, String]],
                                  nuoTenantDetails: NuoTenantDetails): TableSchema = {
    val schema = new TableSchema()
    val tableFieldSchema = mutable.ArrayBuffer[TableFieldSchema]()

    columns.foreach { column =>

      val schemaEntry: TableFieldSchema = new TableFieldSchema()
      schemaEntry.setName(column(ColumnName))
      schemaEntry.setType(column(NuoModifier.ActionAttrName.DataType).toUpperCase)
      tableFieldSchema += schemaEntry
    }
    schema.setFields(tableFieldSchema.asJava)
  }

  def createBqTableObject(userDatasetName: String,
                        userTableName: String,
                        columns: mutable.ListBuffer[mutable.HashMap[String, String]],
                        nuoTenantDetails: NuoTenantDetails): Table = {
    val datasetName = if (userDatasetName.isEmpty) {
      NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.", nuoTenantDetails)
      userDefaultDatasetName
    } else userDatasetName

    val projectId = nuoTenantDetails.bqProjectId

    val schema = convertNuoColumnsToBqSchema(columns, nuoTenantDetails)

    val tableRef: TableReference = new TableReference()
    tableRef.setDatasetId(nuoTenantDetails.tenantId + "_" + datasetName)
    tableRef.setProjectId(projectId)
    tableRef.setTableId(userTableName)

    new Table().setSchema(schema).setTableReference(tableRef)
  }

  /*
  * This function reads the data from the NuoStorage table.
  * */
  def evalSaveAsNuoTable(actionName: String,
                         userDatasetName: String,
                         sourceTableName: String,
                         targetTableName: String,
                         writeIfEmpty: Boolean,
                         appendOutput: Boolean,
                         dependencyNodes: Vector[String],
                         columns: mutable.ListBuffer[mutable.HashMap[String, String]],
                         nuoTenantDetails: NuoTenantDetails): Unit = {

    val datasetName = if (userDatasetName.isEmpty) {
      NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.", nuoTenantDetails)
      userDefaultDatasetName
    } else userDatasetName

    if (!isInternalTable(datasetName, targetTableName, nuoTenantDetails)) {

      val selectQuery = getGenericSelectClauseForSource(nuoTenantDetails.nodeNameToIdMap(actionName), columns, nuoTenantDetails)
      selectQuery.append(s" FROM `$sourceTableName`")

      val jobId = NuoBqClient.executeDMLAndSaveResult(datasetName,
        targetTableName,
        writeIfEmpty,
        appendOutput,
        selectQuery.toString(),
        dependencyNodes, nuoTenantDetails)
      if (!jobId.equals("-1"))
        dag.NuoDagExecutionManager.addExecutionStatus(nuoTenantDetails.nodeNameToIdMap(actionName), "BQ", 1.0, jobId, nuoTenantDetails)
    }
  }


  /*
  * This function profiles the results from input action.
  * */
  def getSqlQuestionResults(actionName: String,
                        sourceTable: String,
                        nodeId: String,
                        dependencyNodes: Vector[String],
                        columns: mutable.ListBuffer[mutable.HashMap[String, String]],
                       nuoTenantDetails: NuoTenantDetails): Unit = {

    val actionTableName = actionName.replaceAll("\\s", "_").replaceAll("\\W", "")
    val tempInputViewName = s"__IN_$actionTableName"

    if (nuoTenantDetails.executionStatusOf.getOrElse(nodeId, 0.0) < 1.0) {

      createView(datasetName = NuoStorageModifier.userTempDatasetName,
        viewName = tempInputViewName,
        selectQuery = s"SELECT * FROM `$sourceTable`",
        dependencyNodes = dependencyNodes,
        nuoTenantDetails = nuoTenantDetails)

      val schema = NuoBqClient.getTableSchema(NuoStorageModifier.userTempDatasetName, tempInputViewName, nuoTenantDetails)
      val timestamp = System.currentTimeMillis()

      var columnPosition = 1
      val profilingExpressionList = schema
        .map { colNameAndType =>
          val colName = colNameAndType._1
          val colNameWithTicks = s"`$colName`"
          val colType = colNameAndType._2

          val queryExpressions = mutable.ArrayBuffer[String](
            s"$timestamp",
            s"'${nuoTenantDetails.runPlanName}'",
            s"'$colName'",
            s"'$colType'",
            s"$columnPosition",
            s"COUNT(*)",
            s"COUNT($colNameWithTicks)",
            s"COUNT(DISTINCT $colNameWithTicks)",
            s"COUNT(DISTINCT $colNameWithTicks)/COUNT($colNameWithTicks) * 100.0",
            s"COUNT(*) - COUNT($colNameWithTicks)",
            s"CAST(MIN($colNameWithTicks) as STRING)",
            s"CAST(MAX($colNameWithTicks) as STRING)",
            s"CAST(APPROX_QUANTILES($colNameWithTicks,1000)[OFFSET(500)] as STRING)")

          colType match {
            case NuoDataTypeHandler.CanvasDataType.Integer
                 | NuoDataTypeHandler.CanvasDataType.Int64
                 | NuoDataTypeHandler.CanvasDataType.Float
                 | NuoDataTypeHandler.CanvasDataType.Float64 =>
              queryExpressions += s"AVG($colNameWithTicks)"
              queryExpressions += s"MAX($colNameWithTicks)-MIN($colNameWithTicks)"
              queryExpressions += s"STDDEV_POP($colNameWithTicks)"
            case _ =>
              queryExpressions += "NULL"
              queryExpressions += "NULL"
              queryExpressions += "NULL"
          }
          colType match {

            case NuoDataTypeHandler.CanvasDataType.String =>
              queryExpressions += s"COUNTIF(LENGTH($colNameWithTicks) <= 0)"
              queryExpressions += s"MIN(LENGTH($colNameWithTicks))"
              queryExpressions += s"MAX(LENGTH($colNameWithTicks))"
              queryExpressions += s"AVG(LENGTH($colNameWithTicks))"
              queryExpressions += s"APPROX_QUANTILES(LENGTH($colNameWithTicks),1000)[OFFSET(500)]"

            case _ =>
              queryExpressions += "NULL"
              queryExpressions += "NULL"
              queryExpressions += "NULL"
              queryExpressions += "NULL"
              queryExpressions += "NULL"
          }
          columnPosition += 1
          queryExpressions.toList
        }

      val query = (List(profilingExpressionList.head.zip(List(ProfilingMetric.Timestamp,
        ProfilingMetric.ProcessName,
        ProfilingMetric.ColumnName,
        ProfilingMetric.ColumnType,
        ProfilingMetric.ColumnPosition,
        ProfilingMetric.TotalCount,
        ProfilingMetric.NonEmptyCount,
        ProfilingMetric.UniqueCount,
        ProfilingMetric.UniquenessPercent,
        ProfilingMetric.EmptyCount,
        ProfilingMetric.Min,
        ProfilingMetric.Max,
        ProfilingMetric.Median,
        ProfilingMetric.Average,
        "`" + ProfilingMetric.Range + "`",
        ProfilingMetric.StandardDeviation,
        ProfilingMetric.BlankCount,
        ProfilingMetric.MinLength,
        ProfilingMetric.MaxLength,
        ProfilingMetric.AvgLength,
        ProfilingMetric.MedianLengthApproximate))
        .map(expAndName => expAndName._1 + " " + expAndName._2)) ++ profilingExpressionList.tail.toList)
        .map(expList =>
          expList.mkString(start = "SELECT ",
            sep = ",",
            end = s" FROM ${NuoBqClient.getFullyQualifiedTableName(NuoStorageModifier.userTempDatasetName, tempInputViewName, nuoTenantDetails)}"))
        .mkString(" UNION ALL ")

      val jobId = NuoBqClient.executeDMLAndSaveResult(datasetName = NuoStorageModifier.userTempDatasetName,
        tableName = actionTableName,
        writeIfEmpty = false,
        append = false,
        selectQuery = query,
        dependencyNodes = Vector[String](),
        nuoTenantDetails = nuoTenantDetails)

      if (!jobId.equals("-1"))
        dag.NuoDagExecutionManager.addExecutionStatus(nuoTenantDetails.nodeNameToIdMap(actionName), "BQ", 1.0, jobId, nuoTenantDetails)

    } else if (nuoTenantDetails.executionStatusOf(nodeId) >= 2.0) {

      NuoStorage.evalReadNuoTableWOStatusUpd(nodeId = nodeId,
        actionName = actionName,
        userDatasetName = userTempDatasetName,
        userTableName = s"$actionTableName",
        filterCondition = "",
        columns = columns, nuoTenantDetails)

      if (nuoTenantDetails.executionStatusOf(nodeId) < 3.0)
        dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "SQL", 3.0, "NA", nuoTenantDetails)
    }
  }

  /*
  * This function reads the data from the NuoStorage table.
  * */
  def evalExportTableToFile(actionName: String,
                            userDatasetName: String,
                            tableName: String,
                            outputFilePrefix: String,
                            destinationFormat: String,
                            printHeader: String,
                            enableCompression: String,
                           nuoTenantDetails: NuoTenantDetails): Unit = {

    val datasetName = if (userDatasetName.isEmpty) {
      NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.", nuoTenantDetails)
      userDefaultDatasetName
    } else userDatasetName

    if (!isInternalTable(datasetName, tableName, nuoTenantDetails)) {

      val jobId = NuoBqClient.exportTableToGcs(datasetName, tableName, outputFilePrefix, destinationFormat, printHeader, enableCompression.toBoolean, nuoTenantDetails)
      dag.NuoDagExecutionManager.addExecutionStatus(nuoTenantDetails.nodeNameToIdMap(actionName), "BQ", 1.0, jobId, nuoTenantDetails)
    }
  }

  /*
  * This function reads the data from the NuoStorage table.
  * */
  def evalTruncateNuoTable(actionName: String,
                           userDatasetName: String,
                           targetTableName: String,
                          nuoTenantDetails: NuoTenantDetails): Unit = {


    val datasetName = if (userDatasetName.isEmpty) {
      NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.", nuoTenantDetails)
      userDefaultDatasetName
    } else userDatasetName

    if (!isInternalTable(userDatasetName, targetTableName, nuoTenantDetails)) {

      val jobId = NuoBqClient.executeDMLToTruncateTable(datasetName = datasetName,
        tableName = targetTableName,
        nuoTenantDetails = nuoTenantDetails)
    }
  }

  def createWithClause(explicitLimit: Int,
                       dependencyNodes: Vector[String],
                       nuoTenantDetails: NuoTenantDetails): String = {

    if (dependencyNodes.nonEmpty) {

      s"WITH " +
        dependencyNodes.filter(nuoTenantDetails.subQueryForNodeId.contains).map { nodeId =>
          if (explicitLimit > 0) s" ${getNodeName(nodeId, nuoTenantDetails)} AS (${nuoTenantDetails.subQueryForNodeId(nodeId)} LIMIT $explicitLimit)"
          else {
            val nodeNameOpt = nuoTenantDetails.nodes.find(_._1.contentEquals(nodeId))
            s" `${if (nodeNameOpt.isDefined) nodeNameOpt.get._2(NodeName).asInstanceOf[String] else nodeId}` AS (${nuoTenantDetails.subQueryForNodeId(nodeId)})"
          }
        }.mkString(",")
    } else {
      " "
    }
  }

  /*
  * This function saves the action result as data from the NuoStorage table.
  * */
  def evalReadNuoTable(nodeId: String,
                       actionName: String,
                       userDatasetName: String,
                       userTableName: String,
                       filterCondition: String,
                       columns: mutable.ListBuffer[mutable.HashMap[String, String]],
                       nuoTenantDetails: NuoTenantDetails): Unit = {

    val datasetName = if (userDatasetName.isEmpty) {
      NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.", nuoTenantDetails)
      userDefaultDatasetName
    } else userDatasetName

    if (!isInternalTable(userDatasetName, userTableName, nuoTenantDetails)) {

      val query = getGenericSelectClauseForTarget(columns, nuoTenantDetails)
      query.append(s" FROM `${nuoTenantDetails.bqProjectId}`.`${nuoTenantDetails.tenantId}_$datasetName`.`$userTableName`")
      if (filterCondition.nonEmpty) {
        query.append(s" WHERE $filterCondition")
      }
      registerActionQuery(query.toString(), actionName, nuoTenantDetails)
    }
  }

  /*
  * This function saves the action result as data from the NuoStorage table.
  * */
  def evalReadNuoTableWOStatusUpd(nodeId: String,
                                  actionName: String,
                                  userDatasetName: String,
                                  userTableName: String,
                                  filterCondition: String,
                                  columns: mutable.ListBuffer[mutable.HashMap[String, String]],
                                  nuoTenantDetails: NuoTenantDetails): Unit = {

    val datasetName = if (userDatasetName.isEmpty) {
      NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.", nuoTenantDetails)
      userDefaultDatasetName
    } else userDatasetName

    val query = getGenericSelectClauseForTarget(columns, nuoTenantDetails)
    query.append(s" FROM `${nuoTenantDetails.bqProjectId}`.`${nuoTenantDetails.tenantId}_$datasetName`.`$userTableName`")
    if (filterCondition.nonEmpty) {
      query.append(s" WHERE $filterCondition")
    }
    registerActionQueryWOStatus(query.toString(), actionName, nuoTenantDetails)
  }

  /*
  * This function performs the Merge (Insert else Update/Delete) into "Insert_Only" Table based on parameters provided in metadata.
  * */
  def evalMergeIntoTable(nodeId: String,
                         actionName: String,
                         userTgtDatasetName: String,
                         targetTableName: String,
                         sourceTableName: String,
                         keyColumnsForTarget: List[String],
                         customMergeCondition: String,
                         updateCondition: String,
                         deleteCondition: String,
                         isInsert: Boolean,
                         isUpdate: Boolean,
                         isDelete: Boolean,
                         isSynch: Boolean,
                         dependencyNodes: Vector[String],
                         columns: mutable.ListBuffer[mutable.HashMap[String, String]],
                         nuoTenantDetails: NuoTenantDetails): Unit = {

    val targetDatasetName = if (userTgtDatasetName.isEmpty) {
      NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.", nuoTenantDetails)
      userDefaultDatasetName
    } else userTgtDatasetName

    if (!isInternalTable(targetDatasetName, targetTableName, nuoTenantDetails)) {
      if (!doesBqTableExist(targetDatasetName, targetTableName, nuoTenantDetails)) {
        throw new Exception(s"Table: $targetTableName does not exist in dataset: $targetDatasetName")
      }
      val tempViewName = s"${targetTableName}_IN"
      createView(datasetName = userTempDatasetName,
        viewName = tempViewName,
        dependencyNodes = dependencyNodes,
        selectQuery = nuoTenantDetails.subQueryForNodeId(nuoTenantDetails.nodeNameToIdMap.getOrElse(sourceTableName, sourceTableName)),
        nuoTenantDetails = nuoTenantDetails)

      val jobIds = mutable.ArrayBuffer[String]()
      if (isDelete) {

        val joinCondition = if (keyColumnsForTarget.isEmpty && customMergeCondition.nonEmpty) {
          customMergeCondition
        } else if (keyColumnsForTarget.nonEmpty) {
          columns.filter(colMap => keyColumnsForTarget.contains(colMap(ColumnName))).map { colMap =>
            val colSource = processExpression(expressionInput = colMap(ColumnSource),
              includeSourceName = false,
              nodeId = nuoTenantDetails.nodeNameToIdMap(actionName), nuoTenantDetails)._1
            s"SRC.$colSource= TGT.`${colMap(ColumnName)}`"
          }.mkString(" AND ")
        } else {
          ""
        }

        val whereClause = if (deleteCondition.nonEmpty) {
          s"($joinCondition) AND ($deleteCondition)"
        } else {
          joinCondition
        }

        val deleteQuery = s"DELETE `$targetDatasetName`.`$targetTableName` TGT" +
          s" WHERE  EXISTS (SELECT * FROM ${NuoBqClient.getFullyQualifiedTableName(NuoStorageModifier.userTempDatasetName, tempViewName, nuoTenantDetails)} SRC WHERE $whereClause)"

        if (isSynch) {

          NuoBqClient.executeDMLSynch(deleteQuery,
            datasetName = userTempDatasetName, nuoTenantDetails)
        } else {
          val jobId = NuoBqClient.executeDMLAsynch(deleteQuery,
            datasetName = userTempDatasetName, allowLargeResults = false, nuoTenantDetails)
          if (!jobId.equalsIgnoreCase("-1"))
            jobIds += jobId
        }
      }

      if (isUpdate) {

        val joinCondition = if (keyColumnsForTarget.isEmpty && customMergeCondition.nonEmpty) {
          customMergeCondition
        } else if (keyColumnsForTarget.nonEmpty) {
          columns.filter(colMap => keyColumnsForTarget.contains(colMap(ColumnName)))
            .map { colMap =>
              val colSource = processExpression(expressionInput = colMap(ColumnSource),
                includeSourceName = false,
                nodeId = nuoTenantDetails.nodeNameToIdMap(actionName), nuoTenantDetails)._1
              s"SRC.$colSource= TGT.`${colMap(ColumnName)}`"
            }
            .mkString(" AND ")
        } else {
          printInfo(s"No custom merge condition or key columns specified for $actionName", nuoTenantDetails)
          ""
        }

        val whereClause = if (updateCondition.nonEmpty) {
          s"($joinCondition) AND ($updateCondition)"
        } else {
          joinCondition
        }

        val updateQuery = s"UPDATE `$targetDatasetName`.`$targetTableName` TGT" +
          s" SET " + columns.filter(colMap => !keyColumnsForTarget.contains(colMap(ColumnName)))
          .map { colMap =>
            val colSource = processExpression(expressionInput = colMap(ColumnSource),
              includeSourceName = false,
              nodeId = nuoTenantDetails.nodeNameToIdMap(actionName), nuoTenantDetails)._1
            s"TGT.`${colMap(ColumnName)}` = SRC.$colSource"
          }
          .mkString(" , ") +
          s" FROM ${NuoBqClient.getFullyQualifiedTableName(NuoStorageModifier.userTempDatasetName, tempViewName, nuoTenantDetails)} SRC" +
          s" WHERE $whereClause"

        if (isSynch) {

          NuoBqClient.executeDMLSynch(updateQuery,
            datasetName = userTempDatasetName, nuoTenantDetails)
        } else {
          val jobId = NuoBqClient.executeDMLAsynch(updateQuery,
            datasetName = userTempDatasetName, allowLargeResults = false, nuoTenantDetails)
          if (!jobId.equalsIgnoreCase("-1"))
            jobIds += jobId

        }
      }

      if (isInsert) {

        val insertQuery = new StringBuilder(s"INSERT `$targetDatasetName`.`$targetTableName` (" +
          columns.map(colMap => colMap(ColumnName)).mkString(" , ")
          + s") SELECT " + columns.map { colMap =>
          s"SRC.${
            processExpression(expressionInput = colMap(ColumnSource),
              includeSourceName = false,
              nodeId = nuoTenantDetails.nodeNameToIdMap(actionName), nuoTenantDetails)._1
          }"
        }.mkString(" , ")
          + s" FROM ${NuoBqClient.getFullyQualifiedTableName(NuoStorageModifier.userTempDatasetName, tempViewName, nuoTenantDetails)} SRC LEFT JOIN `$targetDatasetName`.`$targetTableName` TGT ")

        if (keyColumnsForTarget.nonEmpty) {
          insertQuery.append("USING (" + keyColumnsForTarget.mkString(" , ") + ")")
        } else if (customMergeCondition.nonEmpty) {
          insertQuery.append("ON " + customMergeCondition)
        } else {
          printInfo(s"No custom merge condition or key columns specified for $actionName", nuoTenantDetails)
          ""
        }
        insertQuery.append(" WHERE " + columns
          .map(colMap => s" TGT.`${colMap(ColumnName)}` IS NULL")
          .mkString(" AND "))

        if (isSynch) {

          NuoBqClient.executeDMLSynch(insertQuery.toString(),
            datasetName = userTempDatasetName, nuoTenantDetails)
        } else {
          val jobId = NuoBqClient.executeDMLAsynch(insertQuery.toString(),
            datasetName = userTempDatasetName,
            allowLargeResults = false, nuoTenantDetails)
          if (!jobId.equalsIgnoreCase("-1"))
            jobIds += jobId

        }
      }
      dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "BQ", 1.0, jobIds.mkString("|+|"), nuoTenantDetails)
    }
  }

  def createView(datasetName: String,
                 viewName: String,
                 selectQuery: String,
                 dependencyNodes: Vector[String],
                nuoTenantDetails: NuoTenantDetails): Unit = {

    val querySql = NuoStorage.createWithClause(0, dependencyNodes, nuoTenantDetails) + " " + selectQuery

    if (!isInternalTable(datasetName, viewName, nuoTenantDetails)) {
      if (doesBqTableExist(datasetName, viewName, nuoTenantDetails)) {
        deleteTable(datasetName, viewName, nuoTenantDetails)
      }
      val projectId = nuoTenantDetails.bqProjectId

      val content = new Table()
      val tableReference = new TableReference()
      tableReference.setTableId(viewName)
      tableReference.setDatasetId(nuoTenantDetails.tenantId + "_" + datasetName)
      tableReference.setProjectId(projectId)
      content.setTableReference(tableReference)

      val view = new ViewDefinition()
      view.setQuery(querySql).setUseLegacySql(false)
      content.setView(view)
      nuoTenantDetails.bigqueryClient.tables().insert(projectId, nuoTenantDetails.tenantId + "_" + datasetName, content).execute()
    }
  }

  //  def evalTransferS3FilesToGcs(nodeId: String,
  //                               s3BucketName: String,
  //                               s3PrefixIncludeList: List[String],
  //                               s3PrefixExcludeList: List[String],
  //                               awsAccesKey: String,
  //                               awsSecretKey: String,
  //                               deleteSourceAfterTransfer: Boolean,
//  nuoTenantDetails: NuoTenantDetails
//  ): Unit = {
    //
    //    val jobId = NuoGcsClient.transferS3FilesToGcs(s3BucketName = s3BucketName,
    //      awsPrefixIncludeList = s3PrefixIncludeList,
    //      awsPrefixExcludeList = s3PrefixExcludeList,
    //      awsAccessKey = awsAccesKey,
    //      awsSecretKey = awsSecretKey,
    //      deleteSourceAfterTransfer = deleteSourceAfterTransfer,
    //      overwriteTarget = true,
    //      transferJobDescription = s"${nuoTenantDetails.tenantId}_$s3BucketName", nuoTenantDetails)
    //
    //    dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "TRANSFER", 1.0, jobId, nuoTenantDetails)
    //  }

    def evalImportExportFilesBetweenS3(nodeId: String,
                                       awsAccessKey: String,
                                       awsSecretKey: String,
                                       s3BucketName: String,
                                       s3FilePrefix: String,
                                       nuoFilePrefix: String,
                                       nuoTenantDetails: NuoTenantDetails): Unit = {

      val actionType = nuoTenantDetails.nodes(nodeId)(NuoModifier.ActionAttrName.NodeType).asInstanceOf[String]

      val expectedSizeBytes = transferFilesBetweenS3(nodeId = nodeId,
        isImportFromS3 = actionType.equalsIgnoreCase(NuoModifier.ActionType.Storage.ImportFilesFromS3),
        isExportToS3 = actionType.equalsIgnoreCase(NuoModifier.ActionType.Storage.ExportFilesToS3),
        awsAccessKey = awsAccessKey,
        awsSecretKey = awsSecretKey,
        s3BucketName = s3BucketName,
        s3FilePrefix = s3FilePrefix,
        nuoFilePrefix = nuoFilePrefix,
        nuoTenantDetails = nuoTenantDetails)

      if (nuoTenantDetails.executionStatusOf.getOrElse(nodeId, 0.0) < 1.0) {

        val minWaitTime = System.currentTimeMillis() + math.min(math.ceil(expectedSizeBytes / 3.75 / 1024.0), 60 * 1000.0).toLong
        val maxWaitTime = System.currentTimeMillis() + 540 * 1000l
        val targetType = if (actionType.equalsIgnoreCase(NuoModifier.ActionType.Storage.ImportFilesFromS3)) "GCS" else "S3"
        val targetBucket = if (actionType.equalsIgnoreCase(NuoModifier.ActionType.Storage.ImportFilesFromS3)) nuoTenantDetails.tenantBucketName else s3BucketName
        val targetPath = if (actionType.equalsIgnoreCase(NuoModifier.ActionType.Storage.ImportFilesFromS3)) nuoFilePrefix else s3FilePrefix
        dag.NuoDagExecutionManager.addExecutionStatus(nodeId = nodeId,
          code = "TRANSFER-FUNCTION",
          status = 1.0,
          refParam = s"$expectedSizeBytes|+|0|+|$minWaitTime|+|$maxWaitTime|+|$targetType|+|$targetBucket|+|$targetPath",
          nuoTenantDetails = nuoTenantDetails)
      }
    }

    def transferFilesBetweenS3(nodeId: String,
                               isImportFromS3: Boolean,
                               isExportToS3: Boolean,
                               awsAccessKey: String,
                               awsSecretKey: String,
                               s3BucketName: String,
                               s3FilePrefix: String,
                               nuoFilePrefix: String,
                               nuoTenantDetails: NuoTenantDetails): Long = {


      NuoAIaaS.callTransferFunction(nodeId = nodeId,
        actionName = NuoDagProcessor.getNodeName(nodeId, nuoTenantDetails),
        actionType = if (isImportFromS3) NuoModifier.ActionType.Storage.ImportFilesFromS3 else if (isExportToS3) NuoModifier.ActionType.Storage.ExportFilesToS3 else "NA",
        awsAccessKeyId = awsAccessKey,
        awsSecretAccessKey = awsSecretKey,
        s3BucketName = s3BucketName,
        s3FilePrefix = s3FilePrefix,
        gcsBucketName = nuoTenantDetails.tenantBucketName,
        gcsFilePrefix = nuoFilePrefix,
        transferBatchSizeBytes = 500 * 1024 * 1024l,
        functionTimeoutMilli = 540 * 1000l,
        maxRetryCount = 1,
        nuoTenantDetails = nuoTenantDetails)

      if (isImportFromS3)
        NuoS3Client.getS3ObjectListIterator(s3BucketName,
          s3FilePrefix,
          NuoS3Client.createCustomAwsS3Client(awsAccessKey, awsSecretKey))
          .map(_.getSize)
          .sum
      else if (isExportToS3)
        NuoGcsClient.listGcsFileNameAndSize(nuoTenantDetails.tenantBucketName, nuoFilePrefix, null, nuoTenantDetails)
          .map(_._2)
          .sum
      else -1l
    }

    def evalLoadFilesFromS3(nodeId: String,
                            s3BucketName: String,
                            s3FilePrefix: String,
                            awsAccessKey: String,
                            awsSecretKey: String,
                            userTgtDatasetName: String,
                            targetTableName: String,
                            writeIfEmpty: Boolean,
                            appendOutput: Boolean,
                            delimiter: String,
                            quoteCharacter: String,
                            leadingRowsToSkip: String,
                           nuoTenantDetails: NuoTenantDetails): Unit = {

      val targetDatasetName = if (userTgtDatasetName.isEmpty) {
        NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.", nuoTenantDetails)
        userDefaultDatasetName
      } else userTgtDatasetName

      val actionName = if (nodeId.nonEmpty) getNodeName(nodeId, nuoTenantDetails) else ""
      val nuoFilePrefix = s3FilePrefix

      if (NuoBiller.isServiceAllowed(NuoBiller.ServiceName.StorageInFiles, math.ceil(NuoGcsClient.calcGCSBytes(nuoTenantDetails) / 1024.0 / 1024.0 / 1024.0), nuoTenantDetails)) {

        if (!isInternalTable(targetDatasetName, targetTableName, nuoTenantDetails)) {
          if (!nuoTenantDetails.executionStatusOf.contains(nodeId) || nuoTenantDetails.executionStatusOf(nodeId) < 1.0) {

            val expectedSizeBytes = transferFilesBetweenS3(nodeId = nodeId,
              isImportFromS3 = true,
              isExportToS3 = false,
              awsAccessKey = awsAccessKey,
              awsSecretKey = awsSecretKey,
              s3BucketName = s3BucketName,
              s3FilePrefix = s3FilePrefix,
              nuoFilePrefix = nuoFilePrefix,
              nuoTenantDetails = nuoTenantDetails)

            if (nuoTenantDetails.executionStatusOf.getOrElse(nodeId, 0.0) < 1.0) {

              val minWaitTime = System.currentTimeMillis() + math.min(math.ceil(expectedSizeBytes / 3.75 / 1024.0), 60 * 1000.0).toLong
              val maxWaitTime = System.currentTimeMillis() + 540 * 1000l
              dag.NuoDagExecutionManager.addExecutionStatus(nodeId = nodeId,
                code = "TRANSFER-FUNCTION",
                status = 1.0,
                refParam = s"$expectedSizeBytes|+|0|+|$minWaitTime|+|$maxWaitTime|+|GCS|+|${nuoTenantDetails.tenantBucketName}|+|$nuoFilePrefix",
                null)
            }


          } else if (nuoTenantDetails.executionStatusOf(nodeId) == 2.0) {

            val sourceFilesList = NuoGcsClient.listGcsObject(nuoTenantDetails.tenantBucketName, nuoFilePrefix, null, nuoTenantDetails)
              .filter { gcsObject =>
                val requestId = gcsObject.getMetadata.asScala.getOrElse("NuoRequestId", "")
                requestId.nonEmpty && requestId.equals(nuoTenantDetails.requestId)
              }.map(_.getName)

            val actionType = nuoTenantDetails.nodes(nodeId)(NuoModifier.ActionAttrName.NodeType).asInstanceOf[String]
            val jobId = actionType match {
              case NuoModifier.ActionType.Storage.LoadDelimitedFilesFromS3 =>

                NuoBqClient.loadDelimitedNuoFilesAutoSchema(sourceFileList = sourceFilesList,
                  datasetName = targetDatasetName,
                  tableName = targetTableName,
                  writeIfEmpty = writeIfEmpty,
                  append = appendOutput,
                  delimiter = delimiter,
                  quoteCharacter = quoteCharacter,
                  leadingRowsToSkip = leadingRowsToSkip, nuoTenantDetails)

              case NuoModifier.ActionType.Storage.LoadNLDJsonFilesFromS3 =>

                NuoBqClient.loadNewLineDelimJsonNuoFiles(sourceFileList = sourceFilesList,
                  datasetName = targetDatasetName,
                  tableName = targetTableName,
                  writeIfEmpty = writeIfEmpty,
                  append = appendOutput, nuoTenantDetails)
            }
            dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "BQ-LOAD", 3.0, jobId, nuoTenantDetails)
          }
        }
      }
    }

    def evalLoadNLDJsonFilesFromS3(nodeId: String,
                                   s3BucketName: String,
                                   s3FilePrefix: String,
                                   awsAccessKey: String,
                                   awsSecretKey: String,
                                   userTgtDatasetName: String,
                                   targetTableName: String,
                                   writeIfEmpty: Boolean,
                                   appendOutput: Boolean,
                                   deleteSourceAfterTransfer: Boolean,
                                   nuoTenantDetails: NuoTenantDetails): Unit = {


      val targetDatasetName = if (userTgtDatasetName.isEmpty) {
        NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.", nuoTenantDetails)
        userDefaultDatasetName
      } else userTgtDatasetName


      val actionName = if (nodeId.nonEmpty) getNodeName(nodeId, nuoTenantDetails) else ""
      val nuoFilePrefix = s3FilePrefix

      if (!isInternalTable(targetDatasetName, targetTableName, nuoTenantDetails)) {
        if (!nuoTenantDetails.executionStatusOf.contains(nodeId) || nuoTenantDetails.executionStatusOf(nodeId) < 1.0) {


          val expectedSizeBytes = transferFilesBetweenS3(nodeId = nodeId,
            isImportFromS3 = true,
            isExportToS3 = false,
            awsAccessKey = awsAccessKey,
            awsSecretKey = awsSecretKey,
            s3BucketName = s3BucketName,
            s3FilePrefix = s3FilePrefix,
            nuoFilePrefix = nuoFilePrefix,
            nuoTenantDetails = nuoTenantDetails)

          if (nuoTenantDetails.executionStatusOf.getOrElse(nodeId, 0.0) < 1.0) {

            val minWaitTime = System.currentTimeMillis() + math.min(math.ceil(expectedSizeBytes / 3.75 / 1024.0), 60 * 1000.0).toLong
            val maxWaitTime = System.currentTimeMillis() + 540 * 1000l
            dag.NuoDagExecutionManager.addExecutionStatus(nodeId = nodeId,
              code = "TRANSFER-FUNCTION",
              status = 1.0,
              refParam = s"$expectedSizeBytes|+|0|+|$minWaitTime|+|$maxWaitTime|+|GCS|+|${nuoTenantDetails.tenantBucketName}|+|$nuoFilePrefix",
              nuoTenantDetails = nuoTenantDetails)
          }

        } else if (nuoTenantDetails.executionStatusOf(nodeId) == 2.0) {

          val sourceFilesList = NuoGcsClient.listGcsObject(nuoTenantDetails.tenantBucketName, nuoFilePrefix, null, nuoTenantDetails)
            .filter { gcsObject =>
              val requestId = gcsObject.getMetadata.asScala.getOrElse("NuoRequestId", "")
              requestId.nonEmpty && requestId.equals(nuoTenantDetails.requestId)
            }.map(_.getName)

          val jobId = NuoBqClient.loadNewLineDelimJsonNuoFiles(sourceFileList = sourceFilesList,
            datasetName = targetDatasetName,
            tableName = targetTableName,
            writeIfEmpty = writeIfEmpty,
            append = appendOutput, nuoTenantDetails)
          dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "BQ-LOAD", 3.0, jobId, nuoTenantDetails)
        }
      }
    }

    def evalLoadDelimitedGCSFiles(nodeId: String,
                                  sourceFileList: List[String],
                                  userTgtDatasetName: String,
                                  targetTableName: String,
                                  writeIfEmpty: Boolean,
                                  appendOutput: Boolean,
                                  delimiter: String,
                                  quoteCharacter: String,
                                  leadingRowsToSkip: String,
                                  nuoTenantDetails: NuoTenantDetails): Unit = {


      val targetDatasetName = if (userTgtDatasetName.isEmpty) {
        NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.", nuoTenantDetails)
        userDefaultDatasetName
      } else userTgtDatasetName

      if (!isInternalTable(targetDatasetName, targetTableName, nuoTenantDetails)) {
        val jobId = NuoBqClient.loadDelimitedNuoFilesAutoSchema(sourceFileList = sourceFileList,
          datasetName = targetDatasetName,
          tableName = targetTableName,
          writeIfEmpty = writeIfEmpty,
          append = appendOutput,
          delimiter = delimiter,
          quoteCharacter = quoteCharacter,
          leadingRowsToSkip = leadingRowsToSkip, nuoTenantDetails)
        dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "BQ-LOAD", 1.0, jobId, nuoTenantDetails)
      }
    }

    def evalLoadNLDJsonGCSFiles(nodeId: String,
                                sourceFileList: List[String],
                                userTgtDatasetName: String,
                                targetTableName: String,
                                writeIfEmpty: Boolean,
                                appendOutput: Boolean,
                                nuoTenantDetails: NuoTenantDetails): Unit = {


      val targetDatasetName = if (userTgtDatasetName.isEmpty) {
        NuoLogger.logWarning(s"The DatasetName property is empty. Hence I am going to use User Default Dataset.", nuoTenantDetails)
        userDefaultDatasetName
      } else userTgtDatasetName

      if (!isInternalTable(targetDatasetName, targetTableName, nuoTenantDetails)) {
        val jobId = NuoBqClient.loadNewLineDelimJsonNuoFiles(sourceFileList = sourceFileList,
          datasetName = targetDatasetName,
          tableName = targetTableName,
          writeIfEmpty = writeIfEmpty,
          append = appendOutput, nuoTenantDetails)
        dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "BQ-LOAD", 1.0, jobId, nuoTenantDetails)
      }
    }

    def isInternalTable(targetDatasetName: String,
                        targetTableName: String,
                        nuoTenantDetails: NuoTenantDetails): Boolean = {
      if (List(userCacheDatasetName, userTempDatasetName, userMetadataDatasetName).contains(targetDatasetName.substring(targetDatasetName.indexOf("_") + 1)) &&
        (targetTableName.equalsIgnoreCase(loggingTable)
          || targetTableName.equalsIgnoreCase(functionStatusTable)
          || targetTableName.equalsIgnoreCase(usageTable))) {

        logError(s"$targetTableName is an internal table and I cannot allow to modify it.", nuoTenantDetails)
        true
      } else false
    }

  }
*/
