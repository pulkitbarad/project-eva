package client

import java.io.ByteArrayInputStream
import java.text.SimpleDateFormat
import java.util
import java.util.Date

import canvas.NuoModifier
import canvas.NuoModifier.ActionAttrName.ColumnName
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.Data
import com.google.api.services.bigquery.model._
import com.google.api.services.bigquery.{Bigquery, BigqueryScopes}
import execution.NuoRequestHandler
import logging.NuoLogger._
import logging.{NuoBiller, NuoLogger}
import metadata.StorageMetadata._
import nlp.grammar.NuoEvaEnglishListener

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 03Feb2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoBqClient {

  var BQRetryCount = 10

  def createCustomBigqueryClient(credentials: String): Bigquery = {
    val transport = new NetHttpTransport
    val jsonFactory = new JacksonFactory
    var credential = GoogleCredential.fromStream(new ByteArrayInputStream(credentials.getBytes()))
    if (credential.createScopedRequired) credential = credential.createScoped(BigqueryScopes.all)
    new Bigquery.Builder(transport, jsonFactory, credential).setApplicationName("Storage Server App").build
  }

  //  /*
  //  * This function is executed synchronously.
  //  * It should be used only for the small queries.
  //  * The underlying bq job does not need to be tracked for run-plan termination
  //  * */
  //  def executeDMLSelect(querySql: String,
  //                       pollTimeMillis: Long): Option[List[List[String]]] = {
  //
  //    val queryReq = new QueryRequest()
  //      .setQuery(querySql)
  //      .setUseLegacySql(false)
  //      .setUseQueryCache(false)
  //
  //    val queryResponse =
  // NuoEvaEnglishListener.nuoTenantDetails.bigqueryClient.jobs.query(bqProjectId, queryReq).execute
  //
  //    while (!queryResponse.getJobComplete) {
  //      Thread.sleep(pollTimeMillis)
  //    }
  //    if (queryResponse.getTotalRows.intValueExact() > 0) {
  //
  //      Some(transformBQRowToScalaList(queryResponse.getRows))
  //    } else {
  //      None
  //    }
  //  }

  def executeDMLSelectFixedPolling(querySql: String): (Option[List[List[String]]], List[(String, String)]) = {

    val queryReq = new QueryRequest()
      .setQuery(querySql)
      .setUseLegacySql(false)
      .setUseQueryCache(false)
    //      .setTimeoutMs(TenantInternal.lambdaContextRef.getRemainingTimeInMillis - 10 * 1000L)

    val queryResponse =
      NuoEvaEnglishListener
        .nuoTenantDetails
        .bigqueryClient
        .jobs
        .query(
          NuoEvaEnglishListener
            .nuoTenantDetails
            .bqProjectId,
          queryReq
        )
        .execute

    while (!queryResponse.getJobComplete) {
      Thread.sleep(100l)
    }
    if (queryResponse.getTotalRows.intValueExact() > 0) {

      (
        Some(
          queryResponse
            .getRows.asScala.toList
            .map(row =>
              row
                .getF.asScala.toList
                .map(cell =>
                  cell.getV.asInstanceOf[String]
                )
            )
        ),
        queryResponse
          .getSchema
          .getFields.asScala
          .map(field => (field.getName, field.getType))
          .toList
      )
    } else {

      (
        None,
        queryResponse
          .getSchema
          .getFields.asScala
          .map(field => (field.getName, field.getType))
          .toList
      )
    }
  }

  def executeDMLSynch(querySql: String,
                      datasetName: String): Unit = {
    val jobId =
      startQueryJob(
        datasetName = datasetName,
        querySql = querySql,
        allowLargeResults = false,
        writeDisposition = "",
        destinationTable = "",
        saveAsView = false
      )

    while (!isBQJobComplete(jobId)) {
      Thread.sleep(100l)
    }
  }

  def isBQJobComplete(jobId: String): Boolean = {
    var result = if (jobId.equalsIgnoreCase("-1")) true
    else
      NuoEvaEnglishListener
        .nuoTenantDetails
        .bigqueryClient
        .jobs()
        .get(
          NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
          jobId
        )
        .execute()
        .getStatus
        .getState.equalsIgnoreCase("DONE")

    //    if (result) {
    //      writeStorageMetadataToFile()
    //    }
    result
  }

  def executeDMLAsynch(querySql: String,
                       datasetName: String,
                       allowLargeResults: Boolean): String = {

    startQueryJob(
      datasetName = datasetName,
      querySql = querySql,
      allowLargeResults = false,
      writeDisposition = "",
      destinationTable = "",
      saveAsView = false
    )
  }

  def getBigQueryUsageCount(datasetName: String,
                            querySql: String): Long = {

    val queryConfig = new JobConfigurationQuery()
      .setQuery(querySql)
      .setUseLegacySql(false)
      .setDefaultDataset(
        new DatasetReference()
          .setProjectId(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId)
          .setDatasetId(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName))
      .setUseQueryCache(false)

    NuoEvaEnglishListener.nuoTenantDetails
      .bigqueryClient
      .jobs()
      .insert(
        NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
        new Job()
          .setConfiguration(new JobConfiguration()
            .setQuery(queryConfig)
            .setDryRun(true)))
      .execute()
      .getStatistics
      .getTotalBytesProcessed
      .longValue()

  }

  def startQueryJob(datasetName: String,
                    querySql: String,
                    allowLargeResults: Boolean,
                    writeDisposition: String,
                    destinationTable: String,
                    saveAsView: Boolean): String = {

    val queryConfig = new JobConfigurationQuery()
      .setQuery(querySql)
      .setUseLegacySql(false)
      .setDefaultDataset(
        new DatasetReference()
          .setProjectId(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId)
          .setDatasetId(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName))
      .setUseQueryCache(false)

    if (allowLargeResults) {
      //      queryConfig.setAllowLargeResults(true)
      queryConfig.setAllowLargeResults(true)
      queryConfig
        .setDestinationTable(
          new TableReference()
            .setProjectId(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId)
            .setDatasetId(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName)
            .setTableId(destinationTable)
        )
      queryConfig.setCreateDisposition("CREATE_IF_NEEDED")
      queryConfig.setWriteDisposition(writeDisposition)
    }
    printInfo(s"I am going to execute  query=> $querySql")
    val usageCount = getQueryResultSizeMB(datasetName, querySql)

    if (NuoBiller.isServiceAllowed(NuoBiller.ServiceName.DataProcessed, usageCount / 1024.0 / 1024.0)) {

      val jobObject =
        NuoEvaEnglishListener
          .nuoTenantDetails
          .bigqueryClient
          .jobs()
          .insert(
            NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
            new Job()
              .setConfiguration(new JobConfiguration().setQuery(queryConfig))
          )
          .execute()
      if (jobObject.getStatus.getState.equalsIgnoreCase("DONE")) {
        printInfo("Location=" + jobObject.getStatus.getErrorResult.getLocation)
        printInfo("Message = " + jobObject.getStatus.getErrorResult.getMessage)
        printInfo("Reason=" + jobObject.getStatus.getErrorResult.getReason)
        printInfo(s"Query = $querySql")
        NuoRequestHandler
          .reportErrorToUser(
            new Exception(s"I encountered an error while loading the table $destinationTable.")
          )
        throw new Exception("Unreachable Code Statement")

      }
      jobObject.getJobReference.getJobId
    } else {
      NuoEvaEnglishListener.nuoTenantDetails.errorEncountered = true
      "-1"
    }
  }

  def executeQueryJob(querySql: String,
                      targetDatasetName: String,
                      targetTableName: String,
                      pollingIntervalMillis: Long): Boolean = {


    val queryConfig = new JobConfigurationQuery()
      .setQuery(querySql)
      .setUseLegacySql(false)
      .setDefaultDataset(
        new DatasetReference()
          .setProjectId(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId)
          .setDatasetId(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + targetDatasetName))
      .setUseQueryCache(false)


    //      queryConfig.setAllowLargeResults(true)
    queryConfig.setAllowLargeResults(true)
    queryConfig
      .setDestinationTable(
        new TableReference()
          .setProjectId(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId)
          .setDatasetId(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + targetDatasetName)
          .setTableId(targetTableName)
      )
    queryConfig.setCreateDisposition("CREATE_IF_NEEDED")
    queryConfig.setWriteDisposition("WRITE_TRUNCATE")
    printInfo(s"I am going to execute  query=> $querySql")

    var jobObject =
      NuoEvaEnglishListener
        .nuoTenantDetails
        .bigqueryClient
        .jobs()
        .insert(
          NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
          new Job()
            .setConfiguration(new JobConfiguration().setQuery(queryConfig))
        )
        .execute()
    if (jobObject.getStatus.getState.equalsIgnoreCase("DONE")) {
      printInfo("Location=" + jobObject.getStatus.getErrorResult.getLocation)
      printInfo("Message = " + jobObject.getStatus.getErrorResult.getMessage)
      printInfo("Reason=" + jobObject.getStatus.getErrorResult.getReason)
      printInfo(s"Query = $querySql")
      NuoRequestHandler
        .reportErrorToUser(
          new Exception(s"I encountered an error while loading the table $targetTableName.")
        )
      throw new Exception("Unreachable Code Statement")

    }
    while (!isBQJobComplete(jobObject.getJobReference.getJobId)) {
      Thread.sleep(pollingIntervalMillis)
    }

    true
  }

  def getQueryResultSizeMB(datasetName: String,
                           querySql: String): Double = {

    val queryConfig = new JobConfigurationQuery()
      .setQuery(querySql)
      .setUseLegacySql(false)
      .setDefaultDataset(new DatasetReference()
        .setProjectId(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId)
        .setDatasetId(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName))
      .setUseQueryCache(false)

    NuoEvaEnglishListener.nuoTenantDetails.bigqueryClient
      .jobs().insert(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
      new Job().setConfiguration(new JobConfiguration()
        .setQuery(queryConfig)
        .setDryRun(true)))
      .execute()
      .getStatistics
      .getTotalBytesProcessed / 1024.0 / 1024.0
  }

  def getTableSchema(datasetName: String,
                     tableName: String): mutable.Buffer[(String, String)] = {


    NuoEvaEnglishListener
      .nuoTenantDetails
      .bigqueryClient
      .tables()
      .get(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
        NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName,
        tableName)
      .execute().getSchema.getFields.asScala
      .map(field => (field.getName, field.getType))
  }

  def executeDMLAndSaveResult(datasetName: String,
                              tableName: String,
                              writeIfEmpty: Boolean,
                              append: Boolean,
                              querySql: String,
                              saveAsView: Boolean): String = {

    val writeDisposition = if (writeIfEmpty) {
      "WRITE_EMPTY"
    } else if (append) {
      "WRITE_APPEND"
    } else {
      "WRITE_TRUNCATE"
    }
    startQueryJob(datasetName, querySql, allowLargeResults = true, writeDisposition, tableName, saveAsView = true)
  }

  def executeDMLToTruncateTable(datasetName: String,
                                tableName: String): Unit = {

    val querySql = s"SELECT * FROM `$datasetName`.`$tableName` WHERE 1=2"

    val writeDisposition = "WRITE_TRUNCATE"
    val jobId =
      startQueryJob(
        datasetName,
        querySql,
        allowLargeResults = true,
        writeDisposition,
        tableName,
        saveAsView = false
      )

    while (!isBQJobComplete(jobId)) {
      Thread.sleep(100l)
    }
  }

  def executeDMLAndGetResult(querySql: String,
                             pollTimeMillis: Long): List[List[String]] = {

    val projectId = NuoEvaEnglishListener.nuoTenantDetails.bqProjectId

    val queryReq = new QueryRequest()
      .setQuery(querySql)
      .setUseLegacySql(false)
      .setUseQueryCache(false)

    try {

      val queryResponse =
        NuoEvaEnglishListener
          .nuoTenantDetails
          .bigqueryClient
          .jobs
          .query(projectId, queryReq)
          .execute

      //      NuoLogger.printInfo(s"Waiting for the query to complete:$querySql")
      while (!queryResponse.getJobComplete) {
        //        NuoLogger.printInfo(s"Sleeping. ${System.currentTimeMillis()}")
        Thread.sleep(pollTimeMillis)
      }
      transformBQRowsToScalaList(queryResponse.getRows)
    } catch {
      case ex: Exception =>
        NuoLogger.printInfo(s"I got an exception while running the query :$querySql")
        ex match {
          case exception: GoogleJsonResponseException if exception.getStatusCode == 503 =>
            if (BQRetryCount > 0) {

              NuoLogger.printInfo(s"The API response code was 503 and hence I will try again after 3 seconds")
              Thread.sleep(3000)
              executeDMLAndGetResult(querySql, pollTimeMillis)
            } else {
              ex.printStackTrace()
              NuoRequestHandler
                .reportErrorToUser(
                  new Exception(s"I encountered an error. Your query execution failed because of rate limit."
                  )
                )
              throw new Exception("Unreachable Code statement")
            }
          case _ =>
            ex.printStackTrace()
            NuoRequestHandler
              .reportErrorToUser(
                new Exception(s"I encountered an error. Your query execution failed.")
              )
            throw new Exception("Unreachable Code statement")

        }
    }
  }


  def transformBQRowsToScalaList(tableRows: util.List[TableRow]): List[List[String]] = {

    if (tableRows == null || tableRows.isEmpty) {
      List[List[String]]()
    } else {

      tableRows.asScala
        .map(row => row.getF.asScala
          .map(cell => if (Data.isNull(cell.getV)) "null" else cell.getV.toString).toList).toList
    }
  }

  def loadDelimitedNuoFilesAutoSchema(sourceFileList: List[String],
                                      datasetName: String,
                                      tableName: String,
                                      append: Boolean,
                                      delimiter: String,
                                      quoteCharacter: String,
                                      leadingRowsToSkip: Int): String = {

    val sourceURIs = sourceFileList.map { fileName =>
      if (fileName.endsWith("/"))
        s"gs://${NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName}/$fileName*"
      else
        s"gs://${NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName}/$fileName"
    }

    loadDelimGcsFilesAutoSchema(sourceURIs.asJava,
      datasetName,
      tableName,
      append,
      delimiter,
      quoteCharacter,
      leadingRowsToSkip)
  }

  def loadDelimitedNuoFilesWithSchema(sourceFileList: List[String],
                                      datasetName: String,
                                      tableName: String,
                                      writeIfEmpty: Boolean,
                                      append: Boolean,
                                      delimiter: String,
                                      quoteCharacter: String,
                                      leadingRowsToSkip: Int,
                                      columns: mutable.ListBuffer[mutable.HashMap[String, String]]): String = {

    val sourceURIs = sourceFileList.map { fileName =>
      if (fileName.endsWith("/"))
        s"gs://${NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName}/$fileName*"
      else
        s"gs://${NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName}/$fileName"
    }

    loadDelimGcsFilesWithSchema(sourceURIs.asJava,
      datasetName,
      tableName,
      writeIfEmpty,
      append,
      delimiter,
      quoteCharacter,
      leadingRowsToSkip,
      columns)
  }

  def loadDelimGcsFilesAutoSchema(sourceUris: util.List[String],
                                  datasetName: String,
                                  tableName: String,
                                  append: Boolean,
                                  delimiter: String,
                                  quoteCharacter: String,
                                  leadingRowsToSkip: Int): String = {


    val schemaUpdateOptions = ArrayBuffer[String]()

    val writeDisposition = if (append) {
      schemaUpdateOptions += "ALLOW_FIELD_ADDITION"
      "WRITE_APPEND"
    } else {
      "WRITE_TRUNCATE"
    }

    val job = new Job()
    val jobConfig = new JobConfiguration()
    var loadConfig = new JobConfigurationLoad()
      .setAllowQuotedNewlines(true)
      .setAutodetect(true)
      .setCreateDisposition("CREATE_IF_NEEDED")
      .setDestinationTable(
        new TableReference()
          .setProjectId(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId)
          .setDatasetId(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName)
          .setTableId(tableName)
      )
      .setFieldDelimiter(delimiter)
      .setIgnoreUnknownValues(true)
      .setMaxBadRecords(Integer.MAX_VALUE - 10)
      //      .setQuote(quoteCharacter)
      .setSchemaUpdateOptions(schemaUpdateOptions.toList.asJava)
      .setSkipLeadingRows(leadingRowsToSkip)
      .setSourceFormat("CSV")
      .setSourceUris(sourceUris)
      .setWriteDisposition(writeDisposition)

    if (quoteCharacter != null && quoteCharacter.trim.nonEmpty)
      loadConfig = loadConfig.setQuote(quoteCharacter)

    jobConfig.setLoad(loadConfig)
    job.setConfiguration(jobConfig)

    val jobObject =
      NuoEvaEnglishListener
        .nuoTenantDetails
        .bigqueryClient
        .jobs()
        .insert(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId, job).execute()
    if (jobObject.getStatus.getState.equalsIgnoreCase("DONE")) {

      printInfo("Location=" + jobObject.getStatus.getErrorResult.getLocation)
      printInfo("Message = " + jobObject.getStatus.getErrorResult.getMessage)
      printInfo("Reason=" + jobObject.getStatus.getErrorResult.getReason)
      printInfo(s"Query = $jobObject")
      NuoRequestHandler
        .reportErrorToUser(
          new Exception(s"I encountered an error while loading the table $tableName.")
        )
      throw new Exception("Unreachable Code Statement")
    }
    jobObject.getJobReference.getJobId
  }

  def loadDelimGcsFilesWithSchema(sourceUris: util.List[String],
                                  datasetName: String,
                                  tableName: String,
                                  writeIfEmpty: Boolean,
                                  append: Boolean,
                                  delimiter: String,
                                  quoteCharacter: String,
                                  leadingRowsToSkip: Int,
                                  columns: mutable.ListBuffer[mutable.HashMap[String, String]]): String = {


    val schemaUpdateOptions = ArrayBuffer[String]()

    val writeDisposition = if (writeIfEmpty) {
      "WRITE_EMPTY"
    } else if (append) {
      schemaUpdateOptions += "ALLOW_FIELD_ADDITION"
      "WRITE_APPEND"
    } else {
      "WRITE_TRUNCATE"
    }

    val job = new Job()
    val jobConfig = new JobConfiguration()
    var loadConfig = new JobConfigurationLoad()
      .setAllowQuotedNewlines(true)
      //      .setAutodetect(true)
      .setCreateDisposition("CREATE_IF_NEEDED")
      .setDestinationTable(
        new TableReference()
          .setProjectId(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId)
          .setDatasetId(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName)
          .setTableId(tableName)
      )
      .setFieldDelimiter(delimiter)
      .setIgnoreUnknownValues(true)
      .setMaxBadRecords(Integer.MAX_VALUE - 10)
      //      .setQuote(quoteCharacter)
      .setSchemaUpdateOptions(schemaUpdateOptions.toList.asJava)
      .setSkipLeadingRows(leadingRowsToSkip)
      .setSourceFormat("CSV")
      .setSourceUris(sourceUris)
      .setWriteDisposition(writeDisposition)
      .setSchema(convertNuoFieldsToBqSchema(columns))

    if (quoteCharacter != null && quoteCharacter.trim.nonEmpty)
      loadConfig = loadConfig.setQuote(quoteCharacter)

    jobConfig.setLoad(loadConfig)
    job.setConfiguration(jobConfig)

    val jobObject =
      NuoEvaEnglishListener
        .nuoTenantDetails
        .bigqueryClient
        .jobs()
        .insert(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId, job)
        .execute()
    if (jobObject.getStatus.getState.equalsIgnoreCase("DONE")) {

      printInfo("Location=" + jobObject.getStatus.getErrorResult.getLocation)
      printInfo("Message = " + jobObject.getStatus.getErrorResult.getMessage)
      printInfo("Reason=" + jobObject.getStatus.getErrorResult.getReason)
      printInfo(s"Query = $jobObject")

      NuoRequestHandler
        .reportErrorToUser(
          new Exception(s"I encountered an error while loading the table $tableName.")
        )
      throw new Exception("Unreachable Code Statement")
    }
    jobObject.getJobReference.getJobId
  }

  def loadNewLineDelimJsonNuoFiles(sourceFileList: List[String],
                                   datasetName: String,
                                   tableName: String,
                                   append: Boolean): String = {

    val sourceURIs = sourceFileList.map { fileName =>
      if (fileName.endsWith("/"))
        s"gs://${NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName}/$fileName*"
      else
        s"gs://${NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName}/$fileName"
    }

    val schemaUpdateOptions = ArrayBuffer[String]()

    val writeDisposition = if (append) {
      schemaUpdateOptions += "ALLOW_FIELD_ADDITION"
      "WRITE_APPEND"
    } else {
      "WRITE_TRUNCATE"
    }

    val job = new Job()
    val jobConfig = new JobConfiguration()
    val loadConfig = new JobConfigurationLoad()
      .setAllowQuotedNewlines(true)
      .setAutodetect(true)
      .setCreateDisposition("CREATE_IF_NEEDED")
      .setDestinationTable(
        new TableReference()
          .setProjectId(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId)
          .setDatasetId(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName)
          .setTableId(tableName)
      )
      .setIgnoreUnknownValues(true)
      .setMaxBadRecords(1)
      .setSchemaUpdateOptions(schemaUpdateOptions.toList.asJava)
      .setSourceFormat("NEWLINE_DELIMITED_JSON")
      .setSourceUris(sourceURIs.asJava)
      .setWriteDisposition(writeDisposition)

    jobConfig.setLoad(loadConfig)
    job.setConfiguration(jobConfig)

    val jobObject =
      NuoEvaEnglishListener
        .nuoTenantDetails
        .bigqueryClient
        .jobs()
        .insert(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId, job)
        .execute()
    if (jobObject.getStatus.getState.equalsIgnoreCase("DONE")) {

      printInfo("Location=" + jobObject.getStatus.getErrorResult.getLocation)
      printInfo("Message = " + jobObject.getStatus.getErrorResult.getMessage)
      printInfo("Reason=" + jobObject.getStatus.getErrorResult.getReason)
      printInfo(s"Query = $jobObject")
      NuoRequestHandler
        .reportErrorToUser(
          new Exception(s"I encountered an error while loading the table $tableName.")
        )
      throw new Exception("Unreachable Code Statement")
    }
    jobObject.getJobReference.getJobId

  }

  def streamRecordIntoTable(datasetName: String,
                            tableName: String,
                            data: util.Map[String, String]): Unit = {

    streamRecordsIntoTable(datasetName, tableName, List(data))
  }

  def streamRecordsIntoTable(datasetName: String,
                             tableName: String,
                             data: List[util.Map[String, String]]): Unit = {

    var counter = 0
    val rows = mutable.ArrayBuffer[TableDataInsertAllRequest.Rows]()

    data.foreach { rowMap =>
      rows += new TableDataInsertAllRequest.Rows()
        .setInsertId(counter.toString + System.currentTimeMillis())
        .setJson(rowMap.asInstanceOf[util.Map[String, AnyRef]])
      counter += 1
    }

    val insertRequest = new TableDataInsertAllRequest()
    insertRequest.setIgnoreUnknownValues(true)
    insertRequest.setRows(rows.asJava)

    val response =
      NuoEvaEnglishListener
        .nuoTenantDetails
        .bigqueryClient
        .tabledata()
        .insertAll(
          NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
          NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName,
          tableName,
          insertRequest)
        .execute()
    if (response.containsKey("insertErrors") && !response.getInsertErrors.isEmpty) {
      NuoRequestHandler
        .reportErrorToUser(
          new Exception(
            s"I encountered an error while streaming records into the table $tableName."
          )
        )
      throw new Exception("Unreachable Code statement")
    }
  }

  def listTableData(datasetName: String,
                    tableName: String,
                    pageToken: String,
                    pageSize: Long): (String, Iterable[Iterable[String]]) = {

    val tableDataList = NuoEvaEnglishListener.nuoTenantDetails.bigqueryClient.tabledata()
      .list(
        NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
        NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName,
        tableName
      )
      .setPageToken(pageToken)
      .setMaxResults(pageSize)
      .execute()

    (tableDataList.getPageToken,
      transformBQRowsToScalaList(tableDataList.getRows))

  }

  def getTables(datasetName: String,
                tablePrefix: String): List[String] = {

    NuoEvaEnglishListener
      .nuoTenantDetails
      .bigqueryClient
      .tables()
      .list(
        NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
        NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName
      )
      .execute().getTables.asScala
      .filter(_.getTableReference.getTableId.startsWith(tablePrefix))
      .map(_.getTableReference.getTableId).toList
  }

  def calcBQStorageBytes(): Long = {
    var totalBytes = 0L

    listDatasets()
      .filter(_.startsWith(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_"))
      .foreach { datasetId =>

        var bqResponse =
          NuoEvaEnglishListener
            .nuoTenantDetails
            .bigqueryClient
            .tables()
            .list(
              NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
              datasetId
            )
            .execute()
        if (bqResponse.get("totalItems").asInstanceOf[Int] > 0) {

          bqResponse.getTables.asScala.map(_.getTableReference.getTableId).foreach { tableId =>
            totalBytes +=
              NuoEvaEnglishListener
                .nuoTenantDetails
                .bigqueryClient
                .tables()
                .get(
                  NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
                  datasetId,
                  tableId
                )
                .execute()
                .getNumBytes
          }
          if (bqResponse.getTables.size() < bqResponse.getTotalItems) {

            var nextToken = bqResponse.getNextPageToken
            while (nextToken != null && nextToken.nonEmpty) {

              bqResponse =
                NuoEvaEnglishListener
                  .nuoTenantDetails
                  .bigqueryClient
                  .tables()
                  .list(
                    NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
                    datasetId
                  )
                  .setPageToken(nextToken)
                  .execute()
              bqResponse.getTables.asScala.map(_.getTableReference.getTableId).foreach { tableId =>
                totalBytes +=
                  NuoEvaEnglishListener
                    .nuoTenantDetails
                    .bigqueryClient
                    .tables()
                    .get(
                      NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
                      datasetId,
                      tableId
                    )
                    .execute()
                    .getNumBytes
              }
              nextToken = bqResponse.getNextPageToken
            }
          }
        }
      }
    totalBytes
  }

  //  def writeStorageMetadataToFile(): Boolean = {
  //    try {
  //
  //      implicit val formats = DefaultFormats
  //
  //      val content = write(getNuoStorageDetails)
  //
  //      NuoS3Client.writeToS3(
  //        NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName,
  //        NuoEvaEnglishListener.nuoTenantDetails.NuoStorageMetadataFileName,
  //        content.getBytes
  //      )
  //      content != null && content.trim.nonEmpty
  //    } catch {
  //      case e: Exception =>
  //        e.printStackTrace()
  //        NuoRequestHandler.reportErrorToUser(new Exception(s"I encountered an error while writing storage metadata."))
  //        throw new Exception("Unreachable Code statement")
  //    }
  //  }

  def getNuoStorageDetails: NuoStorageDetails = {

    NuoStorageDetails(System.currentTimeMillis().toString,
      listDatasets()
        .filter(datasetId =>
          datasetId.toLowerCase.startsWith(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_")
        )
        .filterNot(datasetId =>
          List(
            NuoEvaEnglishListener.nuoTenantDetails.userMetadataDatasetName,
            NuoEvaEnglishListener.nuoTenantDetails.userCacheDatasetName,
            NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetName
          )
            .contains(datasetId.toLowerCase))
        .map { datasetId =>

          NuoDataset(datasetId.substring(datasetId.indexOf("_") + 1), getNuoEntities(datasetId))
        })
  }

  def getNuoEntities(datasetId: String): List[NuoEntity] = {

    val tableList = ArrayBuffer[NuoEntity]()
    var bqResponse =
      NuoEvaEnglishListener
        .nuoTenantDetails
        .bigqueryClient
        .tables()
        .list(
          NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
          datasetId
        )
        .execute()
    if (bqResponse.get("totalItems").asInstanceOf[Int] > 0) {

      bqResponse.getTables.asScala.map(_.getTableReference.getTableId).foreach { tableId =>
        val tableObj =
          NuoEvaEnglishListener
            .nuoTenantDetails
            .bigqueryClient
            .tables()
            .get(
              NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
              datasetId,
              tableId
            )
            .execute()

        tableList +=
          NuoEntity(
            datasetId,
            tableId,
            Some(tableObj.getDescription),
            Some(tableObj.getCreationTime.toString),
            Some(tableObj.getLastModifiedTime.longValue().toString),
            Some(tableObj.getNumBytes.toString),
            Some(tableObj.getNumRows.longValue().toString),
            tableObj
              .getSchema
              .getFields.asScala
              .map(col =>
                NuoField(datasetId, tableId, col.getName, col.getType)
              ).toList
          )
      }
      if (bqResponse.getTables.size() < bqResponse.getTotalItems) {

        var nextToken = bqResponse.getNextPageToken
        while (nextToken != null && nextToken.nonEmpty) {

          bqResponse =
            NuoEvaEnglishListener
              .nuoTenantDetails
              .bigqueryClient
              .tables()
              .list(
                NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
                datasetId
              )
              .setPageToken(nextToken)
              .execute()
          bqResponse.getTables.asScala.map(_.getTableReference.getTableId).foreach { tableId =>
            val tableObj =
              NuoEvaEnglishListener
                .nuoTenantDetails
                .bigqueryClient
                .tables()
                .get(
                  NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
                  datasetId,
                  tableId
                )
                .execute()

            tableList += NuoEntity(
              datasetId,
              tableId,
              Some(tableObj.getDescription),
              Some(tableObj.getCreationTime.toString),
              Some(tableObj.getLastModifiedTime.longValue().toString),
              Some(tableObj.getNumBytes.toString),
              Some(tableObj.getNumRows.longValue().toString),
              tableObj
                .getSchema
                .getFields.asScala
                .map(col =>
                  NuoField(datasetId, tableId, col.getName, col.getType)
                )
                .toList
            )
          }
          nextToken = bqResponse.getNextPageToken
        }
      }
    }

    tableList.toList
  }

  def listDatasets(): List[String] = {

    var datasetList = mutable.ArrayBuffer[String]()
    var listDatasetResponse =
      NuoEvaEnglishListener
        .nuoTenantDetails
        .bigqueryClient
        .datasets()
        .list(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId)
        .execute()

    listDatasetResponse
      .getDatasets.asScala

      .foreach { datasetObj =>
        datasetList += datasetObj.getDatasetReference.getDatasetId
      }
    var nextToken = listDatasetResponse.getNextPageToken

    while (nextToken != null && nextToken.nonEmpty) {

      listDatasetResponse =
        NuoEvaEnglishListener
          .nuoTenantDetails
          .bigqueryClient
          .datasets()
          .list(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId)
          .setPageToken(nextToken)
          .execute()
      listDatasetResponse
        .getDatasets.asScala
        .foreach { datasetObj =>
          datasetList += datasetObj.getDatasetReference.getDatasetId
        }
      nextToken = listDatasetResponse.getNextPageToken
    }
    datasetList.toList
  }

  def deleteTempTables(): Unit = {

    listDatasets
      .filter(datasetId =>
        datasetId.toLowerCase.endsWith("__temp")
      )
      .foreach { datasetId =>

        var bqResponse =
          NuoEvaEnglishListener
            .nuoTenantDetails
            .bigqueryClient
            .tables()
            .list(
              NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
              datasetId
            )
            .execute()
        if (bqResponse.get("totalItems").asInstanceOf[Int] > 0) {

          bqResponse.getTables.asScala.map(_.getTableReference.getTableId).foreach { tableId =>

            try {
              NuoEvaEnglishListener
                .nuoTenantDetails
                .bigqueryClient
                .tables()
                .delete(
                  NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
                  datasetId,
                  tableId)
                .execute()
            }
            catch {
              case e: Exception =>
                printInfo(s"I am ignoring the error I received while deleting table $tableId within dataset $datasetId")
            }
            printInfo(s"I have deleted table $tableId within dataset $datasetId")
          }
          if (bqResponse.getTables.size() < bqResponse.getTotalItems) {

            var nextToken = bqResponse.getNextPageToken
            while (nextToken != null && nextToken.nonEmpty) {

              bqResponse =
                NuoEvaEnglishListener
                  .nuoTenantDetails
                  .bigqueryClient
                  .tables()
                  .list(
                    NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
                    datasetId
                  )
                  .setPageToken(nextToken)
                  .execute()
              bqResponse.getTables.asScala.map(_.getTableReference.getTableId).foreach { tableId =>
                try {
                  NuoEvaEnglishListener
                    .nuoTenantDetails
                    .bigqueryClient
                    .tables()
                    .delete(
                      NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
                      datasetId,
                      tableId)
                    .execute()
                }
                catch {
                  case e: Exception =>
                    printInfo(s"I am ignoring the error I received while deleting table $tableId within dataset $datasetId")
                }
                printInfo(s"I have deleted table $tableId within dataset $datasetId")
              }
              nextToken = bqResponse.getNextPageToken
            }
          }
        }
      }
  }

  def getFullyQualifiedTableName(datasetName: String,
                                 tableName: String): String = {
    s"`${NuoEvaEnglishListener.nuoTenantDetails.bqProjectId}`" +
      s".`${NuoEvaEnglishListener.nuoTenantDetails.tenantId}_$datasetName`" +
      s".`$tableName`"
  }

  //  def getFullyQualifiedDatasetName(DatasetName: String,
  //                                 NuoEvaEnglishListener.nuoTenantDetails: NuoTenantDetails): String = {
  //    s"`${NuoEvaEnglishListener.nuoTenantDetails.bqProjectId}`.`${NuoEvaEnglishListener.nuoTenantDetails.tenantId}_$DatasetName`"
  //  }

  //  def getDatasetNameWithPrefix(DatasetName: String,
  //                                 NuoEvaEnglishListener.nuoTenantDetails: NuoTenantDetails): String = {
  //    s"${NuoEvaEnglishListener.nuoTenantDetails.tenantId}_$DatasetName"
  //  }

  def transformBQRowToList(tableRow: TableRow): List[String] = {

    if (tableRow == null || tableRow.isEmpty) {
      List[String]()
    } else {

      tableRow.getF.asScala
        .map(cell => if (Data.isNull(cell.getV)) "null" else cell.getV.toString).toList
    }
  }

  def exportTableToGcs(datasetName: String,
                       tableName: String,
                       filePrefix: String,
                       destinationFormat: String,
                       printHeader: String,
                       enableCompression: Boolean): String = {

    val timestamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm").format(new Date())
    val extractConfig = new JobConfigurationExtract()
      //      .setDestinationFormat("CSV")
      .setDestinationFormat(destinationFormat)
      .setPrintHeader(if (printHeader.nonEmpty) printHeader.toBoolean else true)
      //      .setCompression("GZIP")
      .setSourceTable(
      new TableReference()
        .setProjectId(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId)
        .setDatasetId(NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName)
        .setTableId(tableName)
    )
      .setDestinationUri(
        NuoEvaEnglishListener
          .nuoTenantDetails
          .getTableExportGcsUriPrefix(
            datasetName,
            tableName,
            filePrefix
          )
      )

    if (enableCompression) extractConfig.setCompression("GZIP")

    val jobObject =
      NuoEvaEnglishListener
        .nuoTenantDetails
        .bigqueryClient
        .jobs()
        .insert(
          NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
          new Job()
            .setConfiguration(
              new JobConfiguration()
                .setExtract(extractConfig)
            )
        )
        .execute()
    if (jobObject.getStatus.getState.equalsIgnoreCase("DONE")) {

      printInfo("Message = " + jobObject.getStatus.getErrorResult.getMessage)
      printInfo("Reason=" + jobObject.getStatus.getErrorResult.getReason)
      printInfo(s"DatasetName = $datasetName")
      printInfo(s"EntityName = $tableName")

      NuoRequestHandler.reportErrorToUser(new Exception(s"I encountered an error while loading the table $tableName."))
      throw new Exception("Unreachable Code Statement")
    }
    jobObject.getJobReference.getJobId
  }

  def convertNuoFieldsToBqSchema(columns: mutable.ListBuffer[mutable.HashMap[String, String]]): TableSchema = {
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

  /*
* This function performs the Create Table operation based on parameters provided in metadata.
* */
  def deleteTable(datasetName: String,
                  tableName: String): String = {

    try {
      NuoEvaEnglishListener
        .nuoTenantDetails
        .bigqueryClient
        .tables()
        .delete(
          NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
          NuoEvaEnglishListener.nuoTenantDetails.tenantId + "_" + datasetName,
          tableName)
        .execute()
    } catch {
      case e: Exception =>
        e.printStackTrace()
        NuoRequestHandler
          .reportErrorToUser(
            new Exception(
              s"I encountered an error while deleting the table $tableName."
            )
          )
        throw new Exception("Unreachable Code statement")
    }
    "Table deleted successfully."
  }

  def copyDemoTables(sourceTenantId: String,
                     targetTenantId: String): Unit = {

    val sourceDatasetName =
      s"${sourceTenantId}_${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId}"

    val targetDatasetName =
      NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName

    var jobId = NuoBqClient.copyTable(
      sourceDatasetName,
      targetDatasetName,
      "Account"
    )
    NuoLogger.printInfo(s"The bq job id for copying table Account is $jobId")

    NuoBqClient.copyTable(
      sourceDatasetName,
      targetDatasetName,
      "Address"
    )
    NuoLogger.printInfo(s"The bq job id for copying table Address is $jobId")

    jobId = NuoBqClient.copyTable(
      sourceDatasetName,
      targetDatasetName,
      "Bank"
    )
    NuoLogger.printInfo(s"The bq job id for copying table Bank is $jobId")

    jobId = NuoBqClient.copyTable(
      sourceDatasetName,
      targetDatasetName,
      "Credit_Card"
    )
    NuoLogger.printInfo(s"The bq job id for copying table Credit_Card is $jobId")

    jobId = NuoBqClient.copyTable(
      sourceDatasetName,
      targetDatasetName,
      "Credit_Card_Known"
    )
    NuoLogger.printInfo(s"The bq job id for copying table Credit_Card_Known is $jobId")

    jobId = NuoBqClient.copyTable(
      sourceDatasetName,
      targetDatasetName,
      "Credit_Card_Unknown"
    )
    NuoLogger.printInfo(s"The bq job id for copying table Credit_Card_Unknown is $jobId")

    jobId = NuoBqClient.copyTable(
      sourceDatasetName,
      targetDatasetName,
      "Transaction"
    )
    NuoLogger.printInfo(s"The bq job id for copying table Transaction is $jobId")
  }

  def copyTable(sourceDatasetName: String,
                targetDatasetName: String,
                tableName: String): String = {

    try {

      val sourceTable =
        NuoEvaEnglishListener
          .nuoTenantDetails
          .bigqueryClient
          .tables()
          .get(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId, sourceDatasetName, tableName)
          .execute()
          .getTableReference

      val targetTable = sourceTable.clone().setDatasetId(targetDatasetName)

      val jobConfig =
        new JobConfigurationTableCopy()
          .setSourceTable(sourceTable)
          .setDestinationTable(targetTable)
          .setCreateDisposition("WRITE_TRUNCATE")
          .setCreateDisposition("CREATE_IF_NEEDED")

      val jobObject =
        NuoEvaEnglishListener
          .nuoTenantDetails
          .bigqueryClient
          .jobs()
          .insert(
            NuoEvaEnglishListener.nuoTenantDetails.bqProjectId,
            new Job().setConfiguration(new JobConfiguration().setCopy(jobConfig))
          ).execute()
      if (jobObject.getStatus.getState.equalsIgnoreCase("DONE")) {
        printInfo("Location=" + jobObject.getStatus.getErrorResult.getLocation)
        printInfo("Message = " + jobObject.getStatus.getErrorResult.getMessage)
        printInfo("Reason=" + jobObject.getStatus.getErrorResult.getReason)
        NuoRequestHandler
          .reportErrorToUser(
            new Exception(s"I encountered an error while copying the table $tableName."
            )
          )
        throw new Exception("Unreachable Code Statement")
      }
      jobObject.getJobReference.getJobId

    } catch {
      case e: Exception =>
        e.printStackTrace()

        NuoRequestHandler
          .reportErrorToUser(
            new Exception(s"I encountered an error while copying the table $tableName."
            )
          )
        throw new Exception("Unreachable Code statement")
    }
  }
}
