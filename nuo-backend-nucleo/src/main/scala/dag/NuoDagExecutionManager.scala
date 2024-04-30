//
//package dag
//
////Java imports
//import java.io._
//import java.util
//
//import action.NuoMLaaS
//import canvas.NuoModifier
//import canvas.NuoModifier.NuoStorageModifier._
//import client.{NuoBqClient, NuoGcsClient}
//import com.amazonaws.services.s3.event.S3EventNotification
//import dag.NuoDag.NuoDagDetails
//import logging.{NuoBiller, NuoLogger}
//
//import scala.io.Source
//
////Internal Imports
//
//import canvas.NuoModifier.RunPlanName._
//import client.NuoS3Client
//import client.NuoS3Client._
//import dag.NuoDagProcessor._
//import logging.NuoLogger._
////Third party imports
////Scala imports
//import scala.collection.JavaConverters._
//import scala.collection.mutable
//import scala.language.postfixOps
////AWS imports
//import com.amazonaws.services.lambda.runtime.Context
//
///**
//  * Copyright 2016 NuoCanvas.
//  *
//  *
//  * Created by Pulkit on 26SEP2016
//  *
//  * Content of this file is proprietary and confidential.
//  * It shall not be reused or disclosed without prior consent
//  * of distributor
//  **/
//
//
//object NuoDagExecutionManager {
//
//
//  def handleApiGatewayRequest(inputStream: InputStream, outputStream: OutputStream, context: Context): Unit = {
//
//    val req = Source.fromInputStream(inputStream).getLines().mkString
//    context.getLogger.log("This is request content=" + req)
//    val writer = new OutputStreamWriter(outputStream)
//    writer.write("{\"Hello Back\":\"From scala code\"}")
//    writer.close()
//  }
//  def handleRequest(s3Event: S3EventNotification, context: Context): String = {
//
//    val nuoDagDetails = NuoDagDetails(s3Event, context)
//    if (isExecutionLocked(nuoDagDetails)) {
//      context.getLogger.log(s"I have already started execution for file ${s3Event.getRecords.asScala.head.getS3.getObject.getKey}. Please don't make me repeat!")
//    } else {
//      context.getLogger.log(s"I am starting execution for file ${s3Event.getRecords.asScala.head.getS3.getObject.getKey}.")
//      try {
//        initDagExecutionManager(nuoDagDetails)
//        evaluatePlan(nuoDagDetails, isRecursiveCall = false)
//      } catch {
//        case default: Exception =>
//          printException(default, nuoDagDetails)
//      }
//    }
//    ""
//  }
//
//  def isBigqueryJobComplete(jobId: String, nuoDagDetails: NuoDagDetails): Boolean = {
//
//    nuoDagDetails.tenantDetails.bigqueryClient.jobs()
//      .get(nuoDagDetails.tenantDetails.bqProjectId, jobId).execute()
//      .getStatus.getState.equalsIgnoreCase("DONE")
//  }
//
//  def getLockFileName(nuoDagDetails: NuoDagDetails): String = {
//    s"${nuoDagDetails.tenantDetails.inProgressDir}${nuoDagDetails.runDetails.requestId}/Execution.lock"
//  }
//
//  def getTerminationFileName(nuoDagDetails: NuoDagDetails): String = {
//    s"${nuoDagDetails.tenantDetails.inProgressDir}${nuoDagDetails.runDetails.requestId}/Execution.terminate"
//  }
//
//  def isExecutionLocked(nuoDagDetails: NuoDagDetails): Boolean = {
//    NuoS3Client.doesFileExist(bucketName = nuoDagDetails.tenantDetails.tenantBucketName,
//      getLockFileName(nuoDagDetails), nuoDagDetails)
//  }
//
//  def isExecutionTerminated(nuoDagDetails: NuoDagDetails): Boolean = {
//    NuoS3Client.doesFileExist(bucketName = nuoDagDetails.tenantDetails.tenantBucketName,
//      getTerminationFileName(nuoDagDetails), nuoDagDetails)
//  }
//
//  def initDagExecutionManager(nuoDagDetails: NuoDagDetails): Unit = {
//
//    NuoLogger.logInfo(s"I have started execution of process = ${nuoDagDetails.runDetails.runPlanName}.", nuoDagDetails)
//
//    NuoS3Client.writeToS3(bucketName = nuoDagDetails.tenantDetails.tenantBucketName,
//      fileName = getLockFileName(nuoDagDetails),
//      contentBytes = "NA".getBytes(), nuoDagDetails)
//
//    /*
//     * Initialize data structure from metadata files
//     * */
//    nuoDagDetails.loadNuoDag()
//    refreshExecutionStatus(nuoDagDetails)
//  }
//
//  def evaluatePlan(nuoDagDetails: NuoDagDetails, isRecursiveCall: Boolean): Unit = {
//
//    printInfo("Inside evaluate Plan", nuoDagDetails)
//    printInfo(s"startTime = ${nuoDagDetails.runDetails.startTime}", nuoDagDetails)
//    nuoDagDetails.runDetails.inProgressBranchCount = 0
//    nuoDagDetails.runDetails.recursiveCallRequired = false
//
//    if (!isRecursiveCall || (/*refreshExecutionStatus(nuoDagDetails) &&*/ !nuoDagDetails.runDetails.errorEncountered)) {
//      try {
//
//        nuoDagDetails.runDetails.inProgressBranchCount += 1
//        nuoDagDetails.runDetails.executorService.execute(new Runnable {
//          override def run(): Unit = {
//            nuoDagDetails.runDetails.runPlanType match {
//
//              case x if x.equalsIgnoreCase(RunFromHereStrict)
//                || x.equalsIgnoreCase(RunFromHereLenient)
//                || x.equalsIgnoreCase(RunProcessStrict)
//                || x.equalsIgnoreCase(RunProcessLenient) =>
//
//                NuoDagProcessor.executePlan(nuoDagDetails.runDetails.startPoints.toList, List[String](), isRecursiveCall, nuoDagDetails)
//
//              case x if x.equalsIgnoreCase(RunUntilHereStrict)
//                || x.equalsIgnoreCase(RunUntilHereLenient) =>
//
//                NuoDagProcessor.executePlan(startPoints = nuoDagDetails.runDetails.endPoints.flatMap(findStartPoints(_, isInitialCall = true, nuoDagDetails)).toList,
//                  endPoints = nuoDagDetails.runDetails.endPoints.toList,
//                  isRecursiveCall = isRecursiveCall,
//                  nuoDagDetails = nuoDagDetails)
//
//              case x if x.equalsIgnoreCase(RunCustomStrict)
//                || x.equalsIgnoreCase(RunCustomLenient) =>
//
//                NuoDagProcessor.executePlan(nuoDagDetails.runDetails.startPoints.toList, nuoDagDetails.runDetails.endPoints.toList, isRecursiveCall, nuoDagDetails)
//
//              case default => printInfo(s"I don't know about this Run serverLoggingLevel [$default] in runPlan:[ ${nuoDagDetails.runDetails.runPlanName}]", nuoDagDetails)
//            }
//            nuoDagDetails.runDetails.inProgressBranchCount -= 1
//          }
//        })
//      } catch {
//        case default: Exception =>
//          printException(default, nuoDagDetails)
//      }
//    }
//
//    try {
//
//      if (!isRecursiveCall) Thread.sleep(10 * 1000)
//      while (nuoDagDetails.runDetails.inProgressBranchCount > 0) Thread.sleep(1000)
//
//      refreshExecutionStatus(nuoDagDetails)
//      printInfo(s"Current time = ${System.currentTimeMillis()}", nuoDagDetails)
//      printInfo(s"Run-end time = ${nuoDagDetails.runDetails.endTime}", nuoDagDetails)
//      if (!isExecutionTerminated(nuoDagDetails)
//        && !nuoDagDetails.runDetails.errorEncountered
//        && !nuoDagDetails.runDetails.nodesToBeExecuted.forall(ele => NuoDagProcessor.isObjectExecutionCompleteQuick(ele, updateStorageMetadata = false, nuoDagDetails))
//        && System.currentTimeMillis() < nuoDagDetails.runDetails.endTime) {
//
//        Thread.sleep(1000) //Because we don't want to send too many query requests to Bigquery.
//        evaluatePlan(nuoDagDetails, isRecursiveCall = true)
//      } else {
//        if (System.currentTimeMillis() >= nuoDagDetails.runDetails.endTime) {
//          nuoDagDetails.runDetails.recursiveCallRequired = true
//        }
//        executionPlanClosure(nuoDagDetails)
//        nuoDagDetails.runDetails.executorService.shutdown()
//      }
//    } catch {
//      case default: Exception =>
//        printException(default, nuoDagDetails)
//    }
//
//  }
//
//  def addExecutionStatus(nodeId: String,
//                         code: String,
//                         status: Double,
//                         refParam: String,
//                         nuoDagDetails: NuoDagDetails): Unit = {
//
//    val updStatus = BigDecimal(status).setScale(6, BigDecimal.RoundingMode.HALF_DOWN)
//
//    printInfo(s"I am updating execution status of ${getNodeName(nodeId, nuoDagDetails)} to $updStatus", nuoDagDetails)
//    if (!nuoDagDetails.runDetails.executionStatusOf.contains(nodeId)
//      || updStatus == 0.0 || updStatus > BigDecimal(nuoDagDetails.runDetails.executionStatusOf(nodeId)).setScale(6, BigDecimal.RoundingMode.HALF_DOWN)) {
//
//      //      printInfo(s"nodeId = ${nodeId}")
//      val entry = new util.HashMap[String, String]()
//      entry.put("Timestamp", System.currentTimeMillis().toString)
//      entry.put("RequestId", nuoDagDetails.runDetails.requestId)
//      entry.put("ProcessName", nuoDagDetails.runDetails.runPlanName)
//      entry.put("NodeId", nodeId)
//      entry.put("ObjectName", getNodeName(nodeId, nuoDagDetails))
//      entry.put("StageCode", code)
//      entry.put("CurrentStage", updStatus.toString)
//      entry.put("TotalStages", getTotalStagesForAction(nodeId, nuoDagDetails).toString)
//      entry.put("RefParam", refParam)
//
//      NuoBqClient.streamRecordIntoTable(userMetadataDatasetName, executionStatusTable, entry, nuoDagDetails)
//      nuoDagDetails.runDetails.executionStatusOf.put(nodeId, updStatus.doubleValue())
//    }
//  }
//
//
//  def closeAllExecutionStatuses(nodeIds: List[String],
//                                nuoDagDetails: NuoDagDetails): Unit = {
//
//    val records = nodeIds.map { nodeId =>
//
//      val entry = new util.HashMap[String, String]()
//      entry.put("Timestamp", System.currentTimeMillis().toString)
//      entry.put("RequestId", nuoDagDetails.runDetails.requestId)
//      entry.put("ProcessName", nuoDagDetails.runDetails.runPlanName)
//      entry.put("NodeId", nodeId)
//      entry.put("ObjectName", getNodeName(nodeId, nuoDagDetails))
//      entry.put("StageCode", "NA")
//      entry.put("CurrentStage", "0.0")
//      entry.put("TotalStages", "0.0")
//      entry.put("RefParam", "NA")
//
//      entry
//    }
//    NuoLogger.printInfo(s"Closing executionStatusOf for requestId= ${nuoDagDetails.runDetails.requestId}.", nuoDagDetails)
//    NuoBqClient.streamRecordsIntoTable(userMetadataDatasetName, executionStatusTable, records, nuoDagDetails)
//  }
//
//
//  def removeExecutionStatus(nodeId: String, nuoDagDetails: NuoDagDetails): Unit = {
//
//    addExecutionStatus(nodeId, "NA", 0.0, "NA", nuoDagDetails)
//    nuoDagDetails.runDetails.executionStatusOf.remove(nodeId)
//  }
//
//  def refreshExecutionStatus(nuoDagDetails: NuoDagDetails): Boolean = {
//
//    NuoLogger.printInfo(s"Refreshing executionStatusOf for requestId= ${nuoDagDetails.runDetails.requestId}", nuoDagDetails)
//
//
//    val statusLists = NuoBqClient.executeDMLAndGetResult(s"SELECT Timestamp,RequestId,ProcessName,NodeId,ObjectName,StageCode,CurrentStage,TotalStages,RefParam" +
//      s" FROM `${nuoDagDetails.tenantDetails.tenantId}_$userMetadataDatasetName.$executionStatusTable` src" +
//      s" WHERE src.RequestId = '${nuoDagDetails.runDetails.requestId}' AND CurrentStage =" +
//      s" (SELECT MAX(CurrentStage) FROM `${nuoDagDetails.tenantDetails.tenantId}_$userMetadataDatasetName.$executionStatusTable` maxStageSrc" +
//      s" WHERE maxStageSrc.RequestId = '${nuoDagDetails.runDetails.requestId}' AND maxStageSrc.ProcessName = src.ProcessName AND maxStageSrc.NodeId = src.NodeId)" +
//      s" AND Timestamp = (SELECT MAX(Timestamp) FROM `${nuoDagDetails.tenantDetails.tenantId}_$userMetadataDatasetName.$executionStatusTable` maxTimeSrc" +
//      s" WHERE maxTimeSrc.RequestId = '${nuoDagDetails.runDetails.requestId}' AND maxTimeSrc.ProcessName = src.ProcessName AND maxTimeSrc.NodeId = src.NodeId)", 100, nuoDagDetails)
//
//
//    //Populate the status to executionStatus object.
//    nuoDagDetails.runDetails.executionStatusOf ++= statusLists
//      .map(statusRecord => statusRecord(3) -> statusRecord(6).toDouble)
//
//    //Populate the non-empty reference parameters to executionStatusRefParam object.
//    nuoDagDetails.runDetails.executionStatusRefParamOf ++= statusLists
//      .filter(statusRecord => statusRecord(8) != null && statusRecord(8).nonEmpty && !List("NULL", "NA").contains(statusRecord(8).toUpperCase))
//      .map(statusRecord => statusRecord(3) -> statusRecord(8).split("\\|\\+\\|"))
//
//
//    val statusChangeFlag = refreshJobStatus(statusLists, nuoDagDetails)
//    if (statusChangeFlag)
//      nuoDagDetails.runDetails.recursiveCallRequired = true
//
//    NuoLogger.printInfo(s"nuoDagDetails.runDetails.executionStatusOf = ${nuoDagDetails.runDetails.executionStatusOf.map(x => getNodeName(x._1, nuoDagDetails) -> x._2).toString()}.", nuoDagDetails)
//
//    statusChangeFlag
//  }
//
//  def refreshJobStatus(latestStatusLists: List[List[String]],
//                       nuoDagDetails: NuoDagDetails): Boolean = {
//    var changeFlag = false
//
//    //    printInfo(s"latestStatusLists = ${latestStatusLists.mkString}")
//    latestStatusLists.foreach { status =>
//
//      if ((status(5).equalsIgnoreCase("BQ") && List(1.0, 3.0, 13.0).contains(status(6).toDouble))
//        || (status(5).equalsIgnoreCase("BQ-LOAD") &&
//        List(1.0, 5.0, 11.0).contains(status(6).toDouble))) {
//
//        /*Check if all BQ job from status have completed*/
//        val jobIds = status(8).split("\\|\\+\\|")
//
//        if (jobIds.forall(id => NuoBqClient.isBQJobComplete(id, nuoDagDetails))) {
//          val oldCode = status(6)
//          val newCode = oldCode.toDouble + 1.0
//          //          printInfo(s"status = ${status.mkString}")
//          addExecutionStatus(nodeId = status(3),
//            code = status(5),
//            status = newCode,
//            refParam = status(8),
//            nuoDagDetails = nuoDagDetails)
//          changeFlag = true
//        }
//      } else if (status(5).equalsIgnoreCase("TRANSFER") && status(6).toDouble == 1.0) {
//
//        //        /*Check if all BQ job from status have completed*/
//        //        val jobIds = status(8).split("\\|\\+\\|")
//        //
//        //        if (jobIds.forall(NuoGcsClient.isTransferJobComplete(nuoDagDetails.tenantDetails.bqProjectId, _, nuoDagDetails))) {
//        //          val oldCode = status(6)
//        //          val newCode = oldCode.toDouble + 1.0
//        //          addExecutionStatus(nodeId = status(3),
//        //            code = status(5),
//        //            status = newCode,
//        //            refParam = status(8),
//        //            nuoDagDetails = nuoDagDetails)
//        //          changeFlag = true
//        //        }
//
//        val oldCode = status(6)
//        val newCode = oldCode.toDouble + 1.0
//        addExecutionStatus(nodeId = status(3),
//          code = status(5),
//          status = newCode,
//          refParam = status(8),
//          nuoDagDetails = nuoDagDetails)
//        changeFlag = true
//      } else if (status(5).equalsIgnoreCase("FUNCTION") && status(6).toDouble >= 3.0 && status(6).toDouble < 4.0) {
//
//        val refParams = status(8).split("\\|\\+\\|")
//        val outputDataFilePath = refParams(0)
//        val expectedCount = refParams(1)
//        val totalCount = refParams(2)
//
//        if (hasExternalActionStatusChanged(nodeName = status(4), refParams = refParams, nuoDagDetails = nuoDagDetails)) {
//          val oldCode = status(6).toDouble
//          //          val newCode = if (expectedCount >= totalCount) 4.0 else 3.0 + math.round(expectedCount.toDouble / totalCount.toDouble * 10000.0) / 10000.0
//          if (expectedCount >= totalCount) {
//
//            addExecutionStatus(nodeId = status(3),
//              code = status(5),
//              status = 4.0,
//              refParam = status(8),
//              nuoDagDetails = nuoDagDetails)
//          }
//          changeFlag = true
//        }
//      } else if (status(5).equalsIgnoreCase("TRANSFER-FUNCTION") && List(1.0, 5.0, 9.0).contains(status(6).toDouble)) {
//
//        changeFlag = isTransferFunctionComplete(status(3), nuoDagDetails)
//      } else if ((status(5).equalsIgnoreCase("AMZ-ML-TRAIN") || status(5).equalsIgnoreCase("AMZ-ML-PREDICT"))
//        && status(6).toDouble == 7.0) {
//
//        val nodeId = status(3)
//        val refParams = status(8).split("\\|\\+\\|")
//        if (isMLModelComplete(status(5), refParams, nuoDagDetails)) {
//          val oldCode = status(6).toDouble
//          addExecutionStatus(nodeId = status(3),
//            code = status(5),
//            status = oldCode + 1.0,
//            refParam = status(8),
//            nuoDagDetails = nuoDagDetails)
//          changeFlag = true
//        }
//      }
//    }
//    changeFlag
//  }
//
//  def hasExternalActionStatusChanged(nodeName: String, refParams: Array[String], nuoDagDetails: NuoDagDetails): Boolean = {
//
//    if (refParams.length >= 5) {
//
//      val outputDataFilePath = refParams(0)
//      val expectedCount = refParams(1)
//      //      val totalCount = refParams(2)
//      val minWaitTime = refParams(3)
//      val maxWaitTime = refParams(4)
//
//      if (System.currentTimeMillis() > minWaitTime.toLong) {
//        if (System.currentTimeMillis() > maxWaitTime.toLong) {
//          printException(new Exception(s"Timeout occurred while executing action $nodeName because it has been running for ${maxWaitTime.toDouble / 1000.0 / 60.0 / 60.0} minutes." +
//            s" Please contact the NuoCanvas support."), nuoDagDetails)
//          nuoDagDetails.runDetails.errorEncountered = System.currentTimeMillis() > maxWaitTime.toLong
//          true
//        } else
//          NuoGcsClient.listGcsFileNames(nuoDagDetails.tenantDetails.tenantBucketName, outputDataFilePath, "", nuoDagDetails)
//            .map(fileName => fileName.substring(fileName.lastIndexOf("-") + 1).toInt).sum >= expectedCount.toInt
//
//        //        hasExpectedCount || System.currentTimeMillis() > maxWaitTime.toLong
//      } else false
//    } else false
//  }
//
//  def isMLModelComplete(statusCode: String, refParams: Array[String], nuoDagDetails: NuoDagDetails): Boolean = {
//
//    if (refParams.length >= 4) {
//
//      val modelId = refParams(0)
//      val minWaitTime = refParams(1)
//      val maxWaitTime = refParams(2)
//      val refreshInterval = refParams(3)
//
//      if (System.currentTimeMillis() > minWaitTime.toLong) {
//
//        if (System.currentTimeMillis() > maxWaitTime.toLong) {
//
//          printException(new Exception(s"Timeout occured while executing model $modelId because it has been running for ${maxWaitTime.toDouble / 1000.0 / 60.0 / 60.0} minutes." +
//            s" Please contact the NuoCanvas support."), nuoDagDetails)
//          nuoDagDetails.runDetails.errorEncountered = System.currentTimeMillis() > maxWaitTime.toLong
//          true
//        } else NuoMLaaS.hasModelCompleted(modelId, statusCode.equalsIgnoreCase("AMZ-ML-TRAIN"), nuoDagDetails)
//      } else false
//    } else false
//  }
//
//  def isTransferFunctionComplete(nodeId: String,
//                                       nuoDagDetails: NuoDagDetails): Boolean = {
//
//    val actionType = nuoDagDetails.nodes(nodeId)(NuoModifier.ActionAttrName.NodeType).asInstanceOf[String]
//    val prevRefParams = nuoDagDetails.runDetails.executionStatusRefParamOf.getOrElse(nodeId, Array[String](""))
//    //
//
//    val expectedSizeBytes = prevRefParams(0)
//    val previousCount = prevRefParams(1)
//    val minWaitTime = prevRefParams(2)
//    val maxWaitTime = prevRefParams(3)
//    val targetType = prevRefParams(4)
//    val targetBucket = prevRefParams(5)
//    val targetPath = prevRefParams(6)
//
//    if (prevRefParams.length < 6 || System.currentTimeMillis() <= prevRefParams(2).toLong) false
//    else {
//
//      val currentCount = actionType match {
//
//        case NuoModifier.ActionType.Storage.ExportFilesToS3 =>
//
//          val processedAttrMap = NuoActionPatternMatcher.processAttributeMap(nodeId, nuoDagDetails)
//
//          val awsAccessKey = NuoActionPatternMatcher.getActionAttribute(nodeId, processedAttrMap, NuoModifier.ActionPropertyName.Storage.Common.AwsAccessKey, nuoDagDetails)
//          val awsSecretKey = NuoActionPatternMatcher.getActionAttribute(nodeId, processedAttrMap, NuoModifier.ActionPropertyName.Storage.Common.AwsSecretKey, nuoDagDetails)
//          val s3BucketName = NuoActionPatternMatcher.getActionAttribute(nodeId, processedAttrMap, NuoModifier.ActionPropertyName.Storage.Common.S3BucketName, nuoDagDetails)
//          val s3FilePrefix = NuoActionPatternMatcher.getActionAttribute(nodeId, processedAttrMap, NuoModifier.ActionPropertyName.Storage.Common.S3FilePrefix, nuoDagDetails)
//
//          val customAmazonClient = if (awsAccessKey.nonEmpty) NuoS3Client.createCustomAwsS3Client(awsAccessKey, awsSecretKey) else nuoDagDetails.tenantDetails.amazonS3Client
//          NuoS3Client.getS3ObjectListIterator(s3BucketName, s3FilePrefix, customAmazonClient)
//            .filter {
//              s3File =>
//                val requestId = NuoS3Client.getObjectNuoRequestId(s3BucketName, s3File.getKey, customAmazonClient)
//                requestId.isDefined && requestId.get.equals(nuoDagDetails.runDetails.requestId)
//            }.map(_.getSize)
//            .sum
//
//        case _ =>
//
//          if (targetType.equalsIgnoreCase("GCS")) {
//
//            NuoGcsClient.listGcsObject(targetBucket, targetPath, null, nuoDagDetails)
//              .filter { gcsObject =>
//                val requestId = gcsObject.getMetadata.asScala.getOrElse("NuoRequestId", "")
//                requestId.nonEmpty && requestId.equals(nuoDagDetails.runDetails.requestId)
//              }
//              .map(_.getSize.longValueExact())
//              .sum
//          } else {
//            NuoS3Client.getS3ObjectListIterator(targetBucket, targetPath, nuoDagDetails.tenantDetails.amazonS3Client)
//              .filter { s3File =>
//                val requestId = NuoS3Client.getObjectNuoRequestId(targetBucket, s3File.getKey, nuoDagDetails.tenantDetails.amazonS3Client)
//                requestId.isDefined && requestId.get.equals(nuoDagDetails.runDetails.requestId)
//              }.map(_.getSize)
//              .sum
//          }
//      }
//      if (System.currentTimeMillis() > maxWaitTime.toLong) {
//        printException(new Exception(s"Timeout occurred while executing action ${getNodeName(nodeId, nuoDagDetails)} because it has been running for ${maxWaitTime.toDouble / 1000.0 / 60.0 / 60.0} minutes." +
//          s" Please contact the NuoCanvas support."), nuoDagDetails)
//        nuoDagDetails.runDetails.errorEncountered = System.currentTimeMillis() > maxWaitTime.toLong
//        true
//      } else if (previousCount.toLong < currentCount) {
//
//
//        dag.NuoDagExecutionManager.addExecutionStatus(nodeId = nodeId,
//          code = "TRANSFER-FUNCTION",
//          status = if (System.currentTimeMillis() > maxWaitTime.toLong || currentCount.toDouble >= expectedSizeBytes.toDouble)
//            nuoDagDetails.runDetails.executionStatusOf(nodeId).toInt + 1.0
//          else
//            nuoDagDetails.runDetails.executionStatusOf(nodeId).toInt + math.round(currentCount / expectedSizeBytes.toDouble * 10000.0) / 10000.0,
//          refParam = s"$expectedSizeBytes|+|$currentCount|+|$minWaitTime|+|$maxWaitTime|+|$targetType|+|$targetBucket|+|$targetPath",
//          nuoDagDetails = nuoDagDetails)
//        true
//      } else
//        false
//    }
//  }
//
//  def reWriteTriggerFile(nuoDagDetails: NuoDagDetails): Unit = {
//
//    NuoS3Client.copyS3File(nuoDagDetails.tenantDetails.incomingBucketName,
//      nuoDagDetails.tenantDetails.incomingBucketName,
//      s"${nuoDagDetails.tenantDetails.tenantId}/${nuoDagDetails.runDetails.requestId}.json", s"${nuoDagDetails.tenantDetails.tenantId}/${nuoDagDetails.runDetails.requestId}.json", nuoDagDetails)
//  }
//
//
//  def executionPlanClosure(nuoDagDetails: NuoDagDetails) = {
//
//
//    printInfo("Before logging usage", nuoDagDetails)
//    logUsage(NuoBiller.ServiceName.StorageInTables, NuoBqClient.calcBQStorageBytes(nuoDagDetails) / 1024.0 / 1024.0 / 1024.0, nuoDagDetails)
//    logUsage(NuoBiller.ServiceName.StorageInFiles, NuoGcsClient.calcGCSBytes(nuoDagDetails) / 1024.0 / 1024.0 / 1024.0, nuoDagDetails)
//
//    printInfo("I am going to execute plan closure now", nuoDagDetails)
//
//    NuoS3Client.deleteS3File(bucketName = nuoDagDetails.tenantDetails.tenantBucketName,
//      fileName = getLockFileName(nuoDagDetails), nuoDagDetails)
//    printInfo("I have released execution lock! backend controllore should take the control now", nuoDagDetails)
//
//
//    if (isExecutionTerminated(nuoDagDetails)) {
//      logError(s"The execution has been forcefully terminated for process = ${nuoDagDetails.runDetails.runPlanName}. Please contact NuoCanvas support if it wasn't you.", nuoDagDetails)
//
//    } else if (nuoDagDetails.runDetails.nodesToBeExecuted.forall(ele => NuoDagProcessor.isObjectExecutionCompleteQuick(ele, updateStorageMetadata = false, nuoDagDetails))) {
//
//      if (nuoDagDetails.runDetails.errorEncountered) {
//
//        logInfo(s"I have encountered an error while executing process = ${nuoDagDetails.runDetails.runPlanName}. Please resolve it and retry.", nuoDagDetails)
//      } else {
//
//        val dirName = s"${nuoDagDetails.tenantDetails.inProgressDir}${nuoDagDetails.runDetails.requestId}/"
//        val iterator = NuoS3Client.getS3ObjectListIterator(bucketName = nuoDagDetails.tenantDetails.tenantBucketName,
//          prefix = dirName, nuoDagDetails)
//
//        while (iterator.hasNext) {
//          val oldFileName = iterator.next().getKey
//          val newFileName = oldFileName.replace(nuoDagDetails.tenantDetails.inProgressDir, nuoDagDetails.tenantDetails.completedDir)
//          copyS3File(nuoDagDetails.tenantDetails.tenantBucketName, nuoDagDetails.tenantDetails.tenantBucketName, oldFileName, newFileName, nuoDagDetails)
//          NuoS3Client.deleteS3File(nuoDagDetails.tenantDetails.tenantBucketName, oldFileName, nuoDagDetails)
//        }
//      }
//      NuoS3Client.writeToS3(bucketName = nuoDagDetails.tenantDetails.tenantBucketName,
//        fileName = s"${nuoDagDetails.tenantDetails.inProgressDir}${nuoDagDetails.runDetails.requestId}/Done",
//        contentBytes = "NA".getBytes(), nuoDagDetails)
//
//      logInfo(s"I have completed execution of process = ${nuoDagDetails.runDetails.runPlanName}.", nuoDagDetails)
//
//    } else if (nuoDagDetails.runDetails.errorEncountered) {
//
//      NuoS3Client.writeToS3(bucketName = nuoDagDetails.tenantDetails.tenantBucketName,
//        fileName = s"${nuoDagDetails.tenantDetails.inProgressDir}${nuoDagDetails.runDetails.requestId}/Done",
//        contentBytes = "NA".getBytes(), nuoDagDetails)
//
//      logInfo(s"I have encountered an error and hence aborted execution of process = ${nuoDagDetails.runDetails.runPlanName}.", nuoDagDetails)
//    } else if (nuoDagDetails.runDetails.recursiveCallRequired) {
//
//      /*
//      * Whenever there is a status change for any of the actions to be executed, recursiveCall is requested.
//      * But we only need to invoke recursively if there is no error and execution is not finished.
//      * */
//
//      printInfo("I am not finished hence I will rewrite trigger file and born again!", nuoDagDetails)
//      reWriteTriggerFile(nuoDagDetails)
//    }
//
//  }
//
//  /*
//  * This function writes the delimited file
//  * */
//  def writeDelimitedFile(fileName: String,
//                         containsHeader: Boolean,
//                         lineDelimiter: String,
//                         colDelimiter: String,
//                         textQualifier: String,
//                         appendOutput: Boolean,
//                         header: Vector[String],
//                         data: mutable.ArrayBuffer[mutable.ArrayBuffer[String]]): Unit = {
//
//    val file: File = new File(fileName)
//
//    /*
//    * Create file and/or directory structure if they do not exist
//    * */
//
//
//    if (!file.getParentFile.exists()) file.getParentFile.mkdirs()
//
//    if (!file.exists()) file.createNewFile()
//
//    val out: FileOutputStream = new FileOutputStream(file.getCanonicalPath, appendOutput)
//
//    try {
//      if (containsHeader) {
//        out.write((header.mkString(colDelimiter) + lineDelimiter).getBytes)
//      }
//      var firstColFlag = true
//      data.foreach { row =>
//        row.foreach { element =>
//
//          if (firstColFlag) {
//            firstColFlag = false
//          } else {
//            out.write(colDelimiter.getBytes())
//          }
//
//          /*
//          if column value contains the colDelimiter then surround the value with text qualifier
//          * */
//          if (element.contains(colDelimiter) || element.contains(lineDelimiter)) {
//            out.write((textQualifier + element + textQualifier).getBytes())
//          } else if (element.contains(textQualifier)) {
//
//            /*
//            *if column value contains the text qualifier then escape the text qualifier with additional text qualifier
//            *and surround the entire value with text qualifiers
//            * */
//            out.write((textQualifier + element.replaceAll(textQualifier, textQualifier + textQualifier) + textQualifier).getBytes())
//
//          } else {
//            out.write(element.getBytes())
//          }
//        }
//        out.write(lineDelimiter.getBytes())
//        firstColFlag = true
//      }
//
//    } finally {
//      out.close()
//    }
//  }
//}
