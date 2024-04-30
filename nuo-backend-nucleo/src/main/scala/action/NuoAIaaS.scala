package action

import action.NuoDML._
import client.{NuoBqClient, NuoGcsClient, NuoHttpClient}
import dag.NuoTenantDetails
import execution.NuoRequestHandler
import execution.NuoRequestHandler.{getMessageResponseRef, sendExecutionRunningMessage}
import logging.NuoLogger
import metadata.NuoRecognitionMetadata
import metadata.NuoRecognitionMetadata.NuoExecutionStatusCode
import metadata.NuoRequestMetadata._
import metadata.StorageMetadata.NuoField
import net.liftweb.json.JsonDSL._
import net.liftweb.json.compactRender
import nlp.grammar.NuoEvaEnglishListener

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 21Mar2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoAIaaS {

  def analyzeImages(nuoAnalyzeImageOptions: NuoAnalyzeImageOptions,
                    nuoTenantDetails: NuoTenantDetails): String = {

    val nuoEvaEnglishListener = new NuoEvaEnglishListener(
      analysisId = nuoAnalyzeImageOptions.AnalysisId,
      selectionText = "",
      filterText = "",
      nuoUserMessage = null,
      nuoTenantDetails = nuoTenantDetails
    )

    if (
      nuoAnalyzeImageOptions == null
        || nuoAnalyzeImageOptions.TargetTableName.isEmpty
        || nuoAnalyzeImageOptions.ShouldAppend.isEmpty
        || nuoAnalyzeImageOptions.SourceFiles.isEmpty
        || nuoAnalyzeImageOptions.SourceFiles.get.isEmpty
    ) {
      NuoRequestHandler.reportErrorToUser(new Exception("I could not find the image details from recognized data."))
      throw new Exception("Unreachable Code Statement")
    }

    val targetTableName = nuoAnalyzeImageOptions.TargetTableName.get
    val languageHints =
      if (nuoAnalyzeImageOptions.LanguageHints.isDefined)
        nuoAnalyzeImageOptions.LanguageHints.get
      else null
    val sourceFiles =
      nuoAnalyzeImageOptions
        .SourceFiles.get
        .map(NuoEvaEnglishListener.nuoTenantDetails.tenantFilePrefix + _)
        .flatMap { ele =>
          if (ele.endsWith("/*")) {
            NuoGcsClient
              .listGcsObject(NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName, ele, null)
              .map(_.getName)
          } else List(ele)
        }
        .filter(isSupportedImageFile)


    val outputFilePrefix = s"Tenants/${NuoEvaEnglishListener.nuoTenantDetails.tenantId}/Internal/${nuoEvaEnglishListener.currAnalysisRecognitionData.AnalysisId}/output/"
    val statuses = nuoEvaEnglishListener.currAnalysisRecognitionData.NuoExecutionStatuses

    var latestStatus = statuses.maxBy(_.Status)
    var resultMessage = ""
    val timeNow = System.currentTimeMillis()
    val profilingTableName = targetTableName + "_profiling"
    val patternTableName = targetTableName + "_pattern"


    var nuoPollingDetails = NuoPollingDetails(
      StartTimeMillis = timeNow + 10000,
      PollingIntervalMillis = 2000,
      EndTimeMillis = timeNow + (30 * 60 * 1000)
    )

    if (statuses.isEmpty
      || statuses.head.Status == NuoRecognitionMetadata.NuoExecutionStatusCode.RECOGNIZING
      || statuses.head.Status == NuoRecognitionMetadata.NuoExecutionStatusCode.RECOGNITION_COMPLETE) {

      callImageFunction(
        startIndex = 0,
        maxResultsPerFunctionCall = sourceFiles.length,
        totalCount = sourceFiles.length,
        functionTimeoutMilli = 540 * 1000L,
        maxRetryCount = 1,
        inputFiles = sourceFiles,
        languageHints = if (languageHints != null && languageHints.nonEmpty) languageHints else List[String](),
        outputDataPath = outputFilePrefix,
        nuoEvaEnglishListener = nuoEvaEnglishListener
      )

      val minWaitTime = System.currentTimeMillis() + sourceFiles.length / 10 * 1000L
      val maxWaitTime = math.max(minWaitTime + 300 * 1000, System.currentTimeMillis() + 540 * 1000)
      var nuoPollingDetails = NuoPollingDetails(
        StartTimeMillis = timeNow + minWaitTime,
        PollingIntervalMillis = 2000,
        EndTimeMillis = maxWaitTime
      )

      nuoEvaEnglishListener
        .currAnalysisRecognitionData
        .NuoExecutionStatuses +:=
        NuoRecognitionMetadata.NuoExecutionStatus(
          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.IMAGE_FUNCTION_STARTED,
          Description = None,
          RefParams = List(sourceFiles.length.toString)
        )
      NuoRequestHandler.assignStepsCompleted(0, nuoEvaEnglishListener)
      NuoRequestHandler.assignTotalSteps(3, nuoEvaEnglishListener)
    } else if (
      latestStatus.Status == NuoExecutionStatusCode.IMAGE_FUNCTION_STARTED
        || latestStatus.Status == NuoExecutionStatusCode.IMAGE_FUNCTION_COMPLETE) {

      if (latestStatus.Status == NuoExecutionStatusCode.IMAGE_FUNCTION_STARTED
        && isImageFunctionComplete(NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName, outputFilePrefix, sourceFiles.length)) {

        latestStatus = NuoRecognitionMetadata.NuoExecutionStatus(
          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.IMAGE_FUNCTION_COMPLETE,
          Description = None,
          RefParams = latestStatus.RefParams)


        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:= latestStatus
        NuoRequestHandler.incrementStepsCompleted(nuoEvaEnglishListener)

      }
      if (latestStatus.Status == NuoExecutionStatusCode.IMAGE_FUNCTION_COMPLETE) {

        val jobId =
          NuoBqClient
            .loadNewLineDelimJsonNuoFiles(
              sourceFileList = List(outputFilePrefix),
              datasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
              tableName = targetTableName,
              append =
                if (
                  nuoAnalyzeImageOptions.ShouldAppend.isEmpty
                    || !nuoAnalyzeImageOptions.ShouldAppend.get)
                  false
                else true
            )

        if (!jobId.equals("-1")) {

          nuoEvaEnglishListener
            .currAnalysisRecognitionData
            .NuoExecutionStatuses +:=
            NuoRecognitionMetadata.NuoExecutionStatus(
              Status = NuoRecognitionMetadata.NuoExecutionStatusCode.IMAGE_BQ_LOAD_STARTED,
              Description = None,
              RefParams = List(jobId))
        }
      }
    } else if (
      latestStatus.Status == NuoExecutionStatusCode.IMAGE_BQ_LOAD_STARTED
        || latestStatus.Status == NuoExecutionStatusCode.IMAGE_BQ_LOAD_COMPLETE) {

      if (latestStatus.Status == NuoExecutionStatusCode.IMAGE_BQ_LOAD_STARTED
        && NuoBqClient.isBQJobComplete(latestStatus.RefParams.head)) {

        latestStatus = NuoRecognitionMetadata.NuoExecutionStatus(
          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.IMAGE_BQ_LOAD_COMPLETE,
          Description = None,
          RefParams = latestStatus.RefParams)


        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:= latestStatus
        NuoRequestHandler.incrementStepsCompleted(nuoEvaEnglishListener)
      }
      if (latestStatus.Status == NuoExecutionStatusCode.IMAGE_BQ_LOAD_COMPLETE) {

        var jobId = profileResults(
          resultDatasetName = NuoEvaEnglishListener.nuoTenantDetails.userTempDatasetNameWOTenantId,
          resultTableName = targetTableName,
          profilingTableName = profilingTableName,
          patternTableName = patternTableName,
          nuoEvaEnglishListener = nuoEvaEnglishListener
        )

        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:=
          NuoRecognitionMetadata.NuoExecutionStatus(
            Status = NuoRecognitionMetadata.NuoExecutionStatusCode.IMAGE_PROFILING_STARTED,
            Description = None,
            RefParams = List(jobId)
          )

      }

    } else if (latestStatus.Status == NuoExecutionStatusCode.IMAGE_PROFILING_STARTED
      || latestStatus.Status == NuoExecutionStatusCode.IMAGE_PROFILING_COMPLETE) {

      if (latestStatus.Status == NuoExecutionStatusCode.IMAGE_PROFILING_STARTED
        && NuoBqClient.isBQJobComplete(latestStatus.RefParams.head)) {

        latestStatus = NuoRecognitionMetadata.NuoExecutionStatus(
          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.IMAGE_PROFILING_COMPLETE,
          Description = None,
          RefParams = latestStatus.RefParams)


        nuoEvaEnglishListener
          .currAnalysisRecognitionData
          .NuoExecutionStatuses +:= latestStatus

        NuoRequestHandler.incrementStepsCompleted(nuoEvaEnglishListener)

        //        NuoRecognitionMetadata.writeCurrQuestionRecognitionData(nuoEvaEnglishListener)
      }

      if (latestStatus.Status == NuoExecutionStatusCode.IMAGE_PROFILING_COMPLETE) {

        //        NuoBqClient.writeStorageMetadataToFile()

        resultMessage = getMessageResponseRef(statusCode = 200,
          status = "OK",
          messageRef =
            NuoQueryResponse(
              NuoEvaMessage = NuoEvaMessage(
                AnalysisId = "",
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
              ProfilingResult =
                Some(NuoDataGrid(
                  Metadata = NuoBqClient
                    .getTableSchema(NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                      profilingTableName)
                    .map(pair => NuoField("", "", pair._1, pair._2))
                    .toList,
                  Data = NuoBqClient
                    .listTableData(
                      datasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                      tableName = profilingTableName,
                      pageToken = null,
                      pageSize = 100L)
                    ._2
                    .map(_.toList).toList)),
              Pattern =
                Some(NuoDataGrid(
                  Metadata = NuoBqClient
                    .getTableSchema(NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                      patternTableName)
                    .map(pair => NuoField("", "", pair._1, pair._2))
                    .toList,
                  Data = NuoBqClient
                    .listTableData(
                      datasetName = NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
                      tableName = patternTableName,
                      pageToken = null,
                      pageSize = 100L)
                    ._2
                    .map(_.toList).toList))
            )
        )
      } else {
        val timeNow = System.currentTimeMillis()
        sendExecutionRunningMessage(
          nuoFileLoadOptions = None,
          nuoAnalyzeImageOptions = Some(nuoAnalyzeImageOptions),
          nuoPollingDetails = Some(nuoPollingDetails),
          nuoEvaEnglishListener = nuoEvaEnglishListener
        )
      }

    }
    if (resultMessage != null && resultMessage.nonEmpty)
      resultMessage
    else {
      val timeNow = System.currentTimeMillis()
      sendExecutionRunningMessage(
        None,
        Some(nuoAnalyzeImageOptions),
        Some(nuoPollingDetails),
        nuoEvaEnglishListener
      )
    }
  }


  def callImageFunction(startIndex: Long,
                        maxResultsPerFunctionCall: Long,
                        totalCount: Long,
                        functionTimeoutMilli: Long,
                        maxRetryCount: Int,
                        inputFiles: List[String],
                        languageHints: List[String],
                        outputDataPath: String,
                        nuoEvaEnglishListener: NuoEvaEnglishListener): Unit = {


    val requestBody = ("RequestId" -> nuoEvaEnglishListener.currAnalysisRecognitionData.AnalysisId) ~
      ("TenantId" -> NuoEvaEnglishListener.nuoTenantDetails.tenantId) ~
      ("ProjectId" -> NuoEvaEnglishListener.nuoTenantDetails.bqProjectId) ~
      ("Region" -> "us-central1") ~
      //      ("Region" -> NuoEvaEnglishListener.nuoTenantDetails.gcpRegion) ~
      ("FunctionName" -> "nuo-function-image") ~
      ("InputFiles" -> inputFiles) ~
      ("LanguageHints" -> languageHints) ~
      ("OutputDataPath" -> outputDataPath) ~
      ("StartIndex" -> startIndex) ~
      ("MaxResults" -> maxResultsPerFunctionCall) ~
      ("TotalCount" -> totalCount) ~
      ("FunctionTimeoutMilli" -> functionTimeoutMilli) ~
      ("MaxRetryCount" -> maxRetryCount)


    NuoHttpClient
      .sendHttpsPostRequestPlainText(
        //        url = s"${NuoEvaEnglishListener.nuoTenantDetails.gcpRegion}-${NuoEvaEnglishListener.nuoTenantDetails.bqProjectId}.cloudfunctions.net",
        url = s"us-central1-${NuoEvaEnglishListener.nuoTenantDetails.bqProjectId}.cloudfunctions.net",
        port = "443",
        uri = "nuo-function-controllore",
        timeoutMillis = 10,
        requestBody = compactRender(requestBody))

    NuoLogger.printInfo(s"I have invoked the function with body $requestBody")
  }


  //  case class RateLimit(MinWaitTimeMilli: Long,
  //                       FunctionTimeoutMilli: Long,
  //                       MaxResultsPerFunctionCall: Int,
  //                       ExpectedCount: Int)
  //
  //  def getRateLimits(nodeId: String,
  //                    actionType: String,
  //                    startIndex: Int,
  //                    sourceFileNameAndSize: List[(String, Long)],
  //                    nuoDagDetails: NuoDagDetails): RateLimit = {
  //
  //    if (NuoDagProcessor.isImageAction(nodeId, nuoDagDetails)) {
  //
  //      val preferredBatchSize = 1000
  //      if (startIndex == 0 && sourceFileNameAndSize.length <= preferredBatchSize) {
  //        RateLimit(MinWaitTimeMilli = sourceFileNameAndSize.length / 10 * 1000L,
  //          FunctionTimeoutMilli = 540 * 1000L,
  //          MaxResultsPerFunctionCall = sourceFileNameAndSize.length,
  //          ExpectedCount = sourceFileNameAndSize.length)
  //      } else {
  //        RateLimit(MinWaitTimeMilli = 100 * 1000L,
  //          FunctionTimeoutMilli = 540 * 1000L,
  //          MaxResultsPerFunctionCall = 100,
  //          ExpectedCount = preferredBatchSize)
  //      }
  //    } else if (NuoDagProcessor.isVideoAction(nodeId, nuoDagDetails)) {
  //
  //      val fileAndDuration = sourceFileNameAndSize
  //        .map(gcsFileAndSize =>
  //          (gcsFileAndSize._1, NuoGcsClient.getVideoDuration(projectId = nuoDagDetails.tenantDetails.bqProjectId,
  //            bucketName = nuoDagDetails.tenantDetails.tenantBucketName,
  //            gcsFileName = gcsFileAndSize._1,
  //            nuoDagDetails = nuoDagDetails)))
  //      var acc = 0.0
  //      var index = startIndex
  //      var preferredBatchSize = 0
  //
  //      while (index < fileAndDuration.length && acc <= 600.0) {
  //        acc += fileAndDuration(index)._2
  //        index += 1
  //        preferredBatchSize += 1
  //      }
  //
  //      if (startIndex == 0 && fileAndDuration.length <= preferredBatchSize) {
  //        RateLimit(MinWaitTimeMilli = math.round(acc) / 6 * 1000L,
  //          FunctionTimeoutMilli = 540 * 1000L,
  //          MaxResultsPerFunctionCall = 10,
  //          ExpectedCount = sourceFileNameAndSize.length)
  //      } else {
  //        RateLimit(MinWaitTimeMilli = 100 * 1000L,
  //          FunctionTimeoutMilli = 540 * 1000L,
  //          MaxResultsPerFunctionCall = 1,
  //          ExpectedCount = preferredBatchSize)
  //      }
  //    } else if (NuoDagProcessor.isNlpAction(nodeId, nuoDagDetails)) {
  //
  //      val preferredBatchSize = 1000
  //
  //      //      while (index < sourceFileNameAndSize.length && acc <= 1000.0) {
  //      //        acc += 1
  //      //        index += 1
  //      //        preferredBatchSize += 1
  //      //      }
  //
  //      if (startIndex == 0 && sourceFileNameAndSize.length <= preferredBatchSize) {
  //        RateLimit(MinWaitTimeMilli = 10 * 1000L,
  //          FunctionTimeoutMilli = 540 * 1000L,
  //          MaxResultsPerFunctionCall = sourceFileNameAndSize.length,
  //          ExpectedCount = sourceFileNameAndSize.length)
  //      } else {
  //        RateLimit(MinWaitTimeMilli = 100 * 1000L,
  //          FunctionTimeoutMilli = 540 * 1000L,
  //          MaxResultsPerFunctionCall = preferredBatchSize,
  //          ExpectedCount = preferredBatchSize)
  //      }
  //    } else if (NuoDagProcessor.isTranslateAction(nodeId, nuoDagDetails)) {
  //
  //      var acc = 0.0
  //      var index = startIndex
  //      var preferredBatchSize = 0
  //
  //      while (index < sourceFileNameAndSize.length && acc <= 100 * 1000.0) {
  //        acc += sourceFileNameAndSize(index)._2
  //        index += 1
  //        preferredBatchSize += 1
  //      }
  //
  //      if (startIndex == 0 && sourceFileNameAndSize.length <= preferredBatchSize) {
  //        RateLimit(MinWaitTimeMilli = 10 * 1000L,
  //          FunctionTimeoutMilli = 540 * 1000L,
  //          MaxResultsPerFunctionCall = sourceFileNameAndSize.length,
  //          ExpectedCount = sourceFileNameAndSize.length)
  //      } else {
  //        RateLimit(MinWaitTimeMilli = 100 * 1000L,
  //          FunctionTimeoutMilli = 540 * 1000L,
  //          MaxResultsPerFunctionCall = preferredBatchSize,
  //          ExpectedCount = preferredBatchSize)
  //      }
  //    } else if (actionType.equalsIgnoreCase(ActionType.External.SpeechIntelligence.ConvertSpeechToText)) {
  //
  //      //      var acc = 0.0
  //      var index = startIndex
  //      val preferredBatchSize = 500
  //
  //      //      while (index < sourceFileNameAndSize.length && acc <= 500) {
  //      //        acc += sourceFileNameAndSize(index)._2
  //      //        index += 1
  //      //        preferredBatchSize += 1
  //      //      }
  //
  //      if (startIndex == 0 && sourceFileNameAndSize.length <= preferredBatchSize) {
  //        RateLimit(MinWaitTimeMilli = sourceFileNameAndSize.length / 5 * 1000L,
  //          FunctionTimeoutMilli = 540 * 1000L,
  //          MaxResultsPerFunctionCall = sourceFileNameAndSize.length,
  //          ExpectedCount = sourceFileNameAndSize.length)
  //      } else {
  //        RateLimit(MinWaitTimeMilli = 100 * 1000L,
  //          FunctionTimeoutMilli = 540 * 1000L,
  //          MaxResultsPerFunctionCall = 1,
  //          ExpectedCount = preferredBatchSize)
  //      }
  //    } else if (actionType.equalsIgnoreCase(ActionType.External.SpeechIntelligence.ConvertTextToSpeech)) {
  //
  //      var acc = 0.0
  //      var index = startIndex
  //      var preferredBatchSize = 0
  //
  //      while (index < sourceFileNameAndSize.length && acc <= 5 * 1024.0) {
  //        acc += sourceFileNameAndSize(index)._2
  //        index += 1
  //        preferredBatchSize += 1
  //      }
  //
  //      if (startIndex == 0 && sourceFileNameAndSize.length <= preferredBatchSize) {
  //        RateLimit(MinWaitTimeMilli = 10 * 1000L,
  //          FunctionTimeoutMilli = 540 * 1000L,
  //          MaxResultsPerFunctionCall = sourceFileNameAndSize.length,
  //          ExpectedCount = sourceFileNameAndSize.length)
  //      } else {
  //        RateLimit(MinWaitTimeMilli = 100 * 1000L,
  //          FunctionTimeoutMilli = 540 * 1000L,
  //          MaxResultsPerFunctionCall = preferredBatchSize,
  //          ExpectedCount = preferredBatchSize)
  //      }
  //    } else {
  //      RateLimit(MinWaitTimeMilli = 0L,
  //        FunctionTimeoutMilli = 540 * 1000L,
  //        MaxResultsPerFunctionCall = 0,
  //        ExpectedCount = 0)
  //    }
  //  }

  def isImageFunctionComplete(bucketName: String,
                              nuoFilePrefix: String,
                              expectedCount: Int): Boolean = {

    NuoGcsClient.listGcsObject(bucketName, nuoFilePrefix, null)
      .map(fileName => fileName.getName.substring(fileName.getName.lastIndexOf("-") + 1).toInt)
      .sum >= expectedCount
  }

  def isSupportedAudioFile(fileName: String): Boolean = {
    "^.+\\.(AMR|AMR\\-WB|FLAC|L16|ULAW|OGG|WAV)$".r.findFirstIn(fileName.toUpperCase).nonEmpty
  }

  def isSupportedImageFile(fileName: String): Boolean = {
    "^.+\\.(JPG|JPEG|PNG|BMP|WEBP|GIF|RAW|ICO)$".r.findFirstIn(fileName.toUpperCase).nonEmpty
  }

  def isSupportedVideoFile(fileName: String): Boolean = {
    "^.+\\.(MOV|MPEG4|MP4|AVI)$".r.findFirstIn(fileName.toUpperCase).nonEmpty
  }

}
