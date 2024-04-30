/*
package dag

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 28Jan2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

import canvas.NuoModifier.ActionAttrName._
import canvas.NuoModifier.ActionType
import canvas.NuoModifier.ActionType.UserInput
import client.NuoBqClient
import dag.NuoActionPatternMatcher.invokeActionFunction
import logging.NuoLogger
import logging.NuoLogger._
//import server.NuoCoreServer.TenantDetails

import scala.collection.mutable

object NuoDagProcessor {

  import dag.NuoDag._


  def getNodeName(nodeId: String,
                  nuoDagDetails: NuoDagDetails): String = {
    if (nodeId.trim.nonEmpty)
      nuoDagDetails.nodes.find(_._1.contentEquals(nodeId)).get._2(NodeName).asInstanceOf[String]
    else
      ""
  }


  def getNodeType(nodeId: String,
                  nuoDagDetails: NuoDagDetails): String = {
    nuoDagDetails.nodes(nodeId)(NodeType).asInstanceOf[String]
  }

  def isLoopAction(nodeId: String,
                   nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    nodeType.equalsIgnoreCase(ActionType.LoopAndVariable.While) ||
      nodeType.equalsIgnoreCase(ActionType.LoopAndVariable.DoUntil) ||
      nodeType.equalsIgnoreCase(ActionType.LoopAndVariable.Repeat)
  }

  def isProcess(nodeId: String,
                nuoDagDetails: NuoDagDetails): Boolean = {

    nuoDagDetails.nodes(nodeId)(NodeType).asInstanceOf[String].equalsIgnoreCase(ActionType.Process)
  }

  def isVariableAction(nodeId: String,
                       nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    //    nodeType.equalsIgnoreCase(ActionType.LoopAndVariable.CreateVariable) ||
    nodeType.equalsIgnoreCase(ActionType.LoopAndVariable.CreateOrUpdateVariable)
  }

  def isExternalAction(nodeId: String,
                       nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    isImageAction(nodeId, nuoDagDetails) ||
      isNlpAction(nodeId, nuoDagDetails) ||
      isTranslateAction(nodeId, nuoDagDetails) ||
      isSpeechAction(nodeId, nuoDagDetails) ||
      isVideoAction(nodeId, nuoDagDetails) ||
      nodeType.equalsIgnoreCase(ActionType.External.InHouse.AnalyzeSentiment)

  }

  def isImageAction(nodeId: String,
                    nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    List[String](ActionType.External.ImageIntelligence.DetectCropHint,
      ActionType.External.ImageIntelligence.DetectExplicitContent,
      ActionType.External.ImageIntelligence.DetectFace,
      ActionType.External.ImageIntelligence.DetectLabel,
      ActionType.External.ImageIntelligence.DetectLandmark,
      ActionType.External.ImageIntelligence.DetectLogo,
      ActionType.External.ImageIntelligence.DetectProperties,
      ActionType.External.ImageIntelligence.DetectTextInDocument,
      ActionType.External.ImageIntelligence.DetectTextInImage,
      ActionType.External.ImageIntelligence.FindSimilarImage).map(_.toLowerCase).contains(nodeType.toLowerCase)
  }

  def isSpeechAction(nodeId: String,
                     nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    List[String](ActionType.External.SpeechIntelligence.ConvertSpeechToText,
      ActionType.External.SpeechIntelligence.ConvertTextToSpeech).map(_.toLowerCase).contains(nodeType.toLowerCase)
  }

  def isNlpAction(nodeId: String,
                  nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    List[String](ActionType.External.NlpIntelligence.AnalyzeTextEntities,
      ActionType.External.NlpIntelligence.AnalyzeTextSentiment,
      ActionType.External.NlpIntelligence.AnalyzeTextSyntax,
      ActionType.External.NlpIntelligence.ClassifyText).map(_.toLowerCase).contains(nodeType.toLowerCase)
  }

  def isTranslateAction(nodeId: String,
                        nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    List[String](ActionType.External.TranslateIntelligence.TranslateText).map(_.toLowerCase).contains(nodeType.toLowerCase)
  }

  def isVideoAction(nodeId: String,
                    nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    List[String](ActionType.External.VideoIntelligence.DetectExplicitContentInVideo,
      ActionType.External.VideoIntelligence.DetectFaceInVideo,
      ActionType.External.VideoIntelligence.DetectLabelInVideo,
      ActionType.External.VideoIntelligence.DetectShotChange).map(_.toLowerCase).contains(nodeType.toLowerCase)
  }

  def isUserInputAction(nodeId: String,
                        nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    nodeType.equalsIgnoreCase(UserInput.ChoiceInput) ||
      nodeType.equalsIgnoreCase(UserInput.TextInput)
  }

  def isTwoStageLoadAction(nodeId: String,
                           nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    nodeType.equalsIgnoreCase(ActionType.Storage.LoadDelimitedFilesFromS3) ||
      nodeType.equalsIgnoreCase(ActionType.Storage.LoadNLDJsonFilesFromS3)
  }

  def isTransformAction(nodeId: String,
                        nuoDagDetails: NuoDagDetails): Boolean = {
    val nodeType = getNodeType(nodeId, nuoDagDetails)

    nodeType.equalsIgnoreCase(ActionType.Dml.Join) ||
      nodeType.equalsIgnoreCase(ActionType.Dml.Filter) ||
      nodeType.equalsIgnoreCase(ActionType.Dml.RemoveDuplicate) ||
      nodeType.equalsIgnoreCase(ActionType.Dml.Sort) ||
      nodeType.equalsIgnoreCase(ActionType.Dml.Aggregate) ||
      nodeType.equalsIgnoreCase(ActionType.Dml.Compute) ||
      nodeType.equalsIgnoreCase(ActionType.Dml.Union) ||
      nodeType.equalsIgnoreCase(ActionType.Dml.Intersect) ||
      nodeType.equalsIgnoreCase(ActionType.Dml.Minus) ||
      nodeType.equalsIgnoreCase(ActionType.Dml.Merge) ||
      nodeType.equalsIgnoreCase(ActionType.Dml.Match) ||
      nodeType.equalsIgnoreCase(ActionType.Dml.Case) ||
      nodeType.equalsIgnoreCase(ActionType.Dml.DefaultCase)
  }

  def isWriterAction(nodeId: String,
                     nuoDagDetails: NuoDagDetails): Boolean = {
    val nodeType = getNodeType(nodeId, nuoDagDetails)

    nodeType.equalsIgnoreCase(ActionType.Storage.SaveAsTable) ||
      nodeType.equalsIgnoreCase(ActionType.Storage.ExportTableToFile) ||
      nodeType.equalsIgnoreCase(ActionType.Storage.MergeIntoTable) ||
      nodeType.equalsIgnoreCase(ActionType.Storage.LoadDelimitedFiles) ||
      //      nodeType.equalsIgnoreCase(ActionType.Storage.LoadDelimitedFilesFromS3) ||
      nodeType.equalsIgnoreCase(ActionType.Storage.LoadNLDJsonFiles) ||
      //      nodeType.equalsIgnoreCase(ActionType.Storage.LoadNLDJsonFilesFromS3) ||
      nodeType.equalsIgnoreCase(ActionType.Storage.ImportFilesFromS3) ||
      nodeType.equalsIgnoreCase("Writer") //Test value
  }

  def isReaderAction(nodeId: String,
                     nuoDagDetails: NuoDagDetails): Boolean = {
    val nodeType = getNodeType(nodeId, nuoDagDetails)
    nodeType.equalsIgnoreCase(ActionType.Storage.ReadNuoTable) ||
      nodeType.equalsIgnoreCase("Reader") //Test value

  }

  def isProfilingAction(nodeId: String,
                        nuoDagDetails: NuoDagDetails): Boolean = {
    val nodeType = getNodeType(nodeId, nuoDagDetails)
    nodeType.equalsIgnoreCase(ActionType.DataProfiling.ProfileResult)
  }


  def isDDLAction(nodeId: String,
                  nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    nodeType.equalsIgnoreCase(ActionType.Storage.CreateTable) ||
      nodeType.equalsIgnoreCase(ActionType.Storage.UpdateTableStructure) ||
      nodeType.equalsIgnoreCase(ActionType.Storage.CheckTableExist) ||
      nodeType.equalsIgnoreCase(ActionType.Storage.DeleteTable) ||
      nodeType.equalsIgnoreCase(ActionType.Storage.CreateDataset) ||
      nodeType.equalsIgnoreCase(ActionType.Storage.DeleteDataset)
  }

  def isChartAction(nodeId: String,
                    nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    nodeType.equalsIgnoreCase(ActionType.Chart.DataGrid) ||
      nodeType.equalsIgnoreCase(ActionType.Chart.VariedHeightPieChart) ||
      nodeType.equalsIgnoreCase(ActionType.Chart.PolarAreaChart) ||
      nodeType.equalsIgnoreCase(ActionType.Chart.PieChart) ||
      nodeType.equalsIgnoreCase(ActionType.Chart.DoughnutChart) ||
      nodeType.equalsIgnoreCase(ActionType.Chart.RadialChart) ||
      nodeType.equalsIgnoreCase(ActionType.Chart.LiquidGaugeChart) ||
      nodeType.equalsIgnoreCase(ActionType.Chart.LineChart) ||
      nodeType.equalsIgnoreCase(ActionType.Chart.ScatterPlot) ||
      nodeType.equalsIgnoreCase(ActionType.Chart.StackedAreaChart) ||
      nodeType.equalsIgnoreCase(ActionType.Chart.BarChart) ||
      nodeType.equalsIgnoreCase(ActionType.Chart.RadarChart) ||
      nodeType.equalsIgnoreCase(ActionType.Chart.GeoLocationMap) ||
      nodeType.equalsIgnoreCase(ActionType.Chart.GeoCountryMap)
  }

  def isTransferAction(nodeId: String,
                       nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    nodeType.equalsIgnoreCase(ActionType.Storage.ImportFilesFromS3) ||
      nodeType.equalsIgnoreCase(ActionType.Storage.ExportFilesToS3)
  }

  def isTrainModelAction(nodeId: String,
                         nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    nodeType.equalsIgnoreCase(ActionType.External.MLaaS.TrainModel)
  }

  def isPredictAction(nodeId: String,
                      nuoDagDetails: NuoDagDetails): Boolean = {

    val nodeType = getNodeType(nodeId, nuoDagDetails)

    nodeType.equalsIgnoreCase(ActionType.External.MLaaS.PredictValue)
  }

  //
  //  def isFinalStateAction(nodeId: String): Boolean = {
  //    isChartAction(nodeId) ||
  //      isDDLAction(nodeId) ||
  //      isVariableAction(nodeId) ||
  //      //      isExternalAction(nodeId) ||
  //      isWriterAction(nodeId) ||
  //    isTwoStageLoadAction(nodeId)
  //  }

  //
  //  def isFinalStateObject(nodeId: String): Boolean = {
  //    isChartAction(nodeId) ||
  //      isDDLAction(nodeId) ||
  //      isVariableAction(nodeId) ||
  //      isLoopAction(nodeId) ||
  //      isProcess(nodeId,nuoDagDetails) ||
  //            isTwoStageLoadAction(nodeId) ||
  //      isWriterAction(nodeId)
  //  }


  def findAndRunAllChildren(parentNode: String,
                            nuoDagDetails: NuoDagDetails): Unit = {

    /*
    * We will reuse the logic for Start_Here run mode with all the extreme reader nodes as start points
    * */
    val startPointList = findExtremeLeftChildren(parentNode, nuoDagDetails)

    printInfo(s"Extreme left children of ${getNodeName(parentNode, nuoDagDetails)} = ${startPointList.toString}", nuoDagDetails)


    if (startPointList.nonEmpty) {

      createExecutionFlow(nodeId = startPointList.head, nuoDagDetails)
    }
    if (startPointList.size > 1) {

      startPointList.tail.foreach { startPoint =>

        nuoDagDetails.runDetails.inProgressBranchCount += 1
        nuoDagDetails.runDetails.executorService.execute(new Runnable {
          override def run(): Unit = {

            createExecutionFlow(nodeId = startPoint, nuoDagDetails)
            nuoDagDetails.runDetails.inProgressBranchCount -= 1
          }
        })
      }
    }
  }


  def findExtremeLeftChildren(nodeId: String,
                              nuoDagDetails: NuoDagDetails): Vector[String] = {

    val children = nuoDagDetails.hierarchy(nodeId)._2

    children.filter(childNodeId => !(nuoDagDetails.inboundMandatoryNodes(childNodeId) ++ nuoDagDetails.inboundOptionalNodes(childNodeId)).toSet
      .subsetOf(children.toSet) || (nuoDagDetails.inboundMandatoryNodes(childNodeId).isEmpty && nuoDagDetails.inboundOptionalNodes(childNodeId).isEmpty))
  }

  def findExtremeWriterChildren(nodeId: String,
                                nuoDagDetails: NuoDagDetails): Vector[String] = {

    val children = nuoDagDetails.hierarchy(nodeId)._2

    children.filter(childNodeId => (!(nuoDagDetails.outboundMandatoryNodes(childNodeId) ++ nuoDagDetails.outboundOptionalNodes(childNodeId)).toSet
      .subsetOf(children.toSet) || (nuoDagDetails.outboundMandatoryNodes(childNodeId).isEmpty && nuoDagDetails.outboundOptionalNodes(childNodeId).isEmpty)))
  }

  //  /*
  //  * This function verifies that the entire dependency chain of a given node is executed or not.
  //  * */
  //  def isDependencyEligible(nodeId: String,
  //                           nuoDagDetails: NuoDagDetails): Boolean = {
  //
  //    /*
  //    * When this function is called on the external action, condition check for an external action is irrelevant.
  //    * However, an inbound node of the external action can be of any type and hence the external action condition check.
  //    * */
  //
  //    isObjectExecutionComplete(nodeId, nuoDagDetails)
  //    /* &&
  //          (nuoDagDetails.inboundMandatoryNodes(nodeId).isEmpty || nuoDagDetails.inboundMandatoryNodes(nodeId)
  //            .foldLeft(true)((left, right) => left && isDependencyEligible(right, nuoDagDetails))) &&
  //          (nuoDagDetails.inboundOptionalNodes(nodeId).isEmpty || nuoDagDetails.inboundOptionalNodes(nodeId)
  //            .foldLeft(false)((left, right) => left || isDependencyEligible(right, nuoDagDetails)))*/
  //  }

  /*
  * This function finds the execute dependencies for an inbound node of the given node.
  * Call this function for each inbound node of the node to be executed.
  * Result dependency list shall be considered for the execution flow.
  * */
  def findWithClauseDependencies(nodeId: String,
                                 isRecursiveCall: Boolean,
                                 nuoDagDetails: NuoDagDetails /*,
                                      inSearchOfStartPoints: Boolean ,
                                      forExecution: Boolean*/): mutable.ArrayBuffer[String] = {

    if (!isRecursiveCall) {
      (nuoDagDetails.inboundMandatoryNodes(nodeId) ++ nuoDagDetails.inboundOptionalNodes(nodeId))
        .flatMap(inboundNodeId => findWithClauseDependencies(nodeId = inboundNodeId,
          isRecursiveCall = true, nuoDagDetails)).filter(nuoDagDetails.runDetails.nodesToBeExecuted.contains)
    } else if (isTransformAction(nodeId, nuoDagDetails)) {

      /*
      * If the node is a transform action then the it needs to be considered as an execute dependency
      * However, inbound nodes of a transform action can be of any type hence the recursive call for all inbound nodes.
      * */
      (nuoDagDetails.inboundMandatoryNodes(nodeId) ++ nuoDagDetails.inboundOptionalNodes(nodeId))
        .flatMap(inboundNodeId => findWithClauseDependencies(nodeId = inboundNodeId,
          isRecursiveCall = true, nuoDagDetails)) ++ mutable.ArrayBuffer[String](nodeId)

    } else if (isReaderAction(nodeId, nuoDagDetails) || isProfilingAction(nodeId, nuoDagDetails) || isExternalAction(nodeId, nuoDagDetails) || isUserInputAction(nodeId, nuoDagDetails)
      || isPredictAction(nodeId, nuoDagDetails)) {

      /*
      * The Reader action can always be added without eligibility check.
      * However, there is no need to find dependencies for a reader action. Because that is source data provider.
      * */
      mutable.ArrayBuffer(nodeId)
    } else {
      /*
      * All other types of nodes, e.g., ddl, Process, variable, writer are not required to be traced back further.
      * */
      mutable.ArrayBuffer.empty[String]
    }
  }

  def isObjectExecutionComplete(nodeId: String,
                                nuoDagDetails: NuoDagDetails): Boolean = {

    if (nuoDagDetails.runDetails.inProgressActions.contains(nodeId)) {
      NuoDagExecutionManager.refreshExecutionStatus(nuoDagDetails)
      isObjectExecutionCompleteQuick(nodeId, updateStorageMetadata = true, nuoDagDetails)
    } else {

      isObjectExecutionCompleteQuick(nodeId, updateStorageMetadata = false, nuoDagDetails)
    }
  }

  def isObjectExecutionCompleteQuick(nodeId: String, updateStorageMetadata: Boolean,
                                     nuoDagDetails: NuoDagDetails): Boolean = {

    if (isProcess(nodeId, nuoDagDetails)
      && findExtremeWriterChildren(nodeId, nuoDagDetails)
      .forall(ele => isObjectExecutionCompleteQuick(ele, updateStorageMetadata, nuoDagDetails))) {

      NuoDagExecutionManager.addExecutionStatus(nodeId = nodeId, code = "NA", status = 2.0, refParam = "NA", nuoDagDetails = nuoDagDetails)
      true
    } else if ((isWriterAction(nodeId, nuoDagDetails)
      || isChartAction(nodeId, nuoDagDetails)
      || isPredictAction(nodeId, nuoDagDetails)
      /*|| isTrainModelAction(nodeId, nuoDagDetails)*/)
      && (nuoDagDetails.runDetails.executionStatusOf.contains(nodeId)
      && nuoDagDetails.runDetails.executionStatusOf(nodeId) >= getTotalStagesForAction(nodeId, nuoDagDetails))) {

      if (nuoDagDetails.runDetails.inProgressActions.contains(nodeId)) {
        NuoBqClient.writeStorageMetadataToFile(nuoDagDetails)
      }
      true
    } else if (isTwoStageLoadAction(nodeId, nuoDagDetails)
      && nuoDagDetails.runDetails.executionStatusOf.contains(nodeId)
      && nuoDagDetails.runDetails.executionStatusOf(nodeId) >= getTotalStagesForAction(nodeId, nuoDagDetails)) {

      if (nuoDagDetails.runDetails.inProgressActions.contains(nodeId)) {
        NuoBqClient.writeStorageMetadataToFile(nuoDagDetails)
      }
      true
    } else
      (nuoDagDetails.runDetails.executionStatusOf.contains(nodeId)
        && nuoDagDetails.runDetails.executionStatusOf(nodeId) >= getTotalStagesForAction(nodeId, nuoDagDetails))
  }

  def getTotalStagesForAction(nodeId: String,
                              nuoDagDetails: NuoDagDetails): Double = {

    if ((isTransformAction(nodeId, nuoDagDetails)
      || isReaderAction(nodeId, nuoDagDetails)
      || isDDLAction(nodeId, nuoDagDetails)
      || isVariableAction(nodeId, nuoDagDetails))) 1.0
    else if (isLoopAction(nodeId, nuoDagDetails)
      || isWriterAction(nodeId, nuoDagDetails)
      || isChartAction(nodeId, nuoDagDetails)
      || isProcess(nodeId, nuoDagDetails)
      || isTransferAction(nodeId, nuoDagDetails)) 2.0
    else if (isTwoStageLoadAction(nodeId, nuoDagDetails)) 4.0
    else if (isProfilingAction(nodeId, nuoDagDetails)) 3.0
    else if (isExternalAction(nodeId, nuoDagDetails)) 7.0
    else if (isTrainModelAction(nodeId, nuoDagDetails)) 8.0
    else if (isPredictAction(nodeId, nuoDagDetails)) 15.0
    else if (isUserInputAction(nodeId, nuoDagDetails)) 3.0
    else 1.0
  }

  def shouldObjectBeExecuted(nodeId: String,
                             nuoDagDetails: NuoDagDetails): Boolean = {

    //    NuoLogger.printInfo(s"nuoDagDetails.runDetails.executionStatusOf = ${nuoDagDetails.runDetails.executionStatusOf.toString()}.", nuoDagDetails)

    val result = if (!nuoDagDetails.runDetails.executionStatusOf.contains(nodeId)
      || nuoDagDetails.runDetails.executionStatusOf(nodeId) == 0.0
      || isTransformAction(nodeId, nuoDagDetails)
      || isReaderAction(nodeId, nuoDagDetails)) {
      true
    } /*else if (isTransformAction(nodeId, nuoDagDetails) || isReaderAction(nodeId, nuoDagDetails)) {
      !nuoDagDetails.subQueryForNodeId.contains(nodeId)
    }*/ else if (isTwoStageLoadAction(nodeId, nuoDagDetails)) {

      /*
      * 21AUG17T1816Z:Pulkit: Order of TwoStageLoadAction test and WriteAction test is important because former is subset of later.
      * */
      nuoDagDetails.runDetails.executionStatusOf(nodeId) < 1.0 || nuoDagDetails.runDetails.executionStatusOf(nodeId) == 2.0
    } else if (isVariableAction(nodeId, nuoDagDetails)
      || isDDLAction(nodeId, nuoDagDetails)
      || isWriterAction(nodeId, nuoDagDetails)
      //      || isTwoStageLoadAction(nodeId, nuoDagDetails)
      //      || isMLaaSAction(nodeId, nuoDagDetails)
      || isChartAction(nodeId, nuoDagDetails)
      || isTransferAction(nodeId, nuoDagDetails)) {

      /*
      * 21AUG17T1816Z:Pulkit: We don't want write action or chart action to be processed again if they have status >= 1.0. Because it will trigger duplicate bq job.
      * */
      nuoDagDetails.runDetails.executionStatusOf(nodeId) < 1.0
      //      nuoDagDetails.runDetails.executionStatusOf(nodeId) < getTotalStagesForAction(nodeId, nuoDagDetails)
    } else if (isLoopAction(nodeId, nuoDagDetails)) {

      nuoDagDetails.runDetails.executionStatusOf(nodeId) < 2.0
    } else if (isExternalAction(nodeId, nuoDagDetails)) {

      //      nuoDagDetails.runDetails.executionStatusOf(nodeId) % 2 == 0 || nuoDagDetails.runDetails.executionStatusOf(nodeId) >= getTotalStagesForAction(nodeId, nuoDagDetails)
      true
    } else if (isTrainModelAction(nodeId, nuoDagDetails)) {

      nuoDagDetails.runDetails.executionStatusOf(nodeId) % 2 == 0 && nuoDagDetails.runDetails.executionStatusOf(nodeId) < getTotalStagesForAction(nodeId, nuoDagDetails)

    } else if (isPredictAction(nodeId, nuoDagDetails) || isProfilingAction(nodeId, nuoDagDetails)) {

      nuoDagDetails.runDetails.executionStatusOf(nodeId) % 2 == 0 || nuoDagDetails.runDetails.executionStatusOf(nodeId) >= getTotalStagesForAction(nodeId, nuoDagDetails)

    } else if (isUserInputAction(nodeId, nuoDagDetails)) {

      nuoDagDetails.runDetails.executionStatusOf(nodeId) < 1.0 || nuoDagDetails.runDetails.executionStatusOf(nodeId) == 2.0
    } else if (isProcess(nodeId, nuoDagDetails)) {

      !isObjectExecutionComplete(nodeId, nuoDagDetails)
    } else {
      printException(new Exception("I could not classify the object type!"), nuoDagDetails)
      false
    }
    //    printInfo(s"shouldObjectBeExecuted for ${getNodeName(nodeId)} = $result")

    result
  }

  def findNodesToBeExecuted(nodeId: String,
                            endPoints: List[String],
                            nuoDagDetails: NuoDagDetails): List[String] = {


    if (endPoints.contains(nodeId)) {
      List(nodeId) ++
        nuoDagDetails.hierarchy(nodeId)._2.flatMap(ele => findNodesToBeExecuted(ele, endPoints, nuoDagDetails))
    }
    else {
      val outbounds = nuoDagDetails.outboundNodes(nodeId)

      /*
      * 09JUL2017:Pulkit: We need to check if a particular branch should be processed or not only if the given node is junction i.e., has more than one outbound.
      * in single path end-point is sufficient to allow or block execution. Allowed-Path/Blocked-Path are useful when there are more than one execution paths.
      * */
      if (outbounds.size > 1) {

        List(nodeId) ++
          nuoDagDetails.hierarchy(nodeId)._2.flatMap(ele => findNodesToBeExecuted(ele, endPoints, nuoDagDetails)) ++
          nuoDagDetails.outboundNodes(nodeId).filter(shouldPathBeProcessed(_, nuoDagDetails)).flatMap(ele => findNodesToBeExecuted(ele, endPoints, nuoDagDetails))
      } else {

        List(nodeId) ++
          nuoDagDetails.hierarchy(nodeId)._2.flatMap(ele => findNodesToBeExecuted(ele, endPoints, nuoDagDetails)) ++
          nuoDagDetails.outboundNodes(nodeId).flatMap(ele => findNodesToBeExecuted(ele, endPoints, nuoDagDetails))
      }
    }
  }

  def haveInboundsExecuted(nodeId: String,
                           nuoDagDetails: NuoDagDetails): Boolean = {

    //    printInfo(s"haveInboundsExecuted for ${getNodeName(nodeId)}")
    val optionalInbounds = nuoDagDetails.inboundOptionalNodes(nodeId).filter(nuoDagDetails.runDetails.nodesToBeExecuted.contains)
    val mandatoryInbounds = nuoDagDetails.inboundMandatoryNodes(nodeId).filter(nuoDagDetails.runDetails.nodesToBeExecuted.contains)

    mandatoryInbounds.isEmpty || mandatoryInbounds.forall(haveInboundsExecuted(_, nuoDagDetails)) &&
      (optionalInbounds.isEmpty ||
        ((!nuoDagDetails.runDetails.isExecutionLenient && optionalInbounds.forall(haveInboundsExecuted(_, nuoDagDetails)))
          && (nuoDagDetails.runDetails.isExecutionLenient && optionalInbounds.foldLeft(false)((left, right) => left || haveInboundsExecuted(right, nuoDagDetails)))))
  }

  /*
  * This function finds all the outbound nodes of the given node.
  * Call this function for each start point to be executed.
  * */
  def createExecutionFlow(nodeId: String,
                          nuoDagDetails: NuoDagDetails): Unit = {


    try {

      val nodeExecutionStatus = nuoDagDetails.runDetails.executionStatusOf.getOrElse(nodeId, 0.0)

      NuoLogger.printInfo(s"I am creating execution flow for action = ${getNodeName(nodeId, nuoDagDetails)}.", nuoDagDetails)

      if (haveInboundsExecuted(nodeId, nuoDagDetails)) {

        NuoLogger.printInfo(s"Path should be processed and inbounds have been executed for action = ${getNodeName(nodeId, nuoDagDetails)}.", nuoDagDetails)
        if (shouldObjectBeExecuted(nodeId, nuoDagDetails)) {

          NuoLogger.printInfo(s"I am processing action = ${getNodeName(nodeId, nuoDagDetails)}.", nuoDagDetails)
          if (!nuoDagDetails.runDetails.inProgressActions.contains(nodeId)) nuoDagDetails.runDetails.inProgressActions += nodeId
          processNode(nodeId = nodeId,
            dependencyNodes = findWithClauseDependencies(nodeId,
              isRecursiveCall = false
              , nuoDagDetails /*,
              inSearchOfStartPoints = false ,
              forExecution = false*/).distinct.toVector, nuoDagDetails)
        }

        if (isObjectExecutionComplete(nodeId, nuoDagDetails)) {
          nuoDagDetails.runDetails.inProgressActions -= nodeId

          if (nuoDagDetails.runDetails.executionStatusOf(nodeId) > nodeExecutionStatus) {
            NuoLogger.logInfo(s"I have completed execution of action = ${getNodeName(nodeId, nuoDagDetails)}.", nuoDagDetails)
          }


          val outboundNodes = nuoDagDetails.outboundNodes(nodeId).filter(nuoDagDetails.runDetails.nodesToBeExecuted.contains(_))

          if (outboundNodes.nonEmpty) {

            createExecutionFlow(nodeId = outboundNodes.head, nuoDagDetails)
          }
          if (outboundNodes.size > 1) {

            outboundNodes.tail.foreach { outboundNodeId =>

              nuoDagDetails.runDetails.inProgressBranchCount += 1
              nuoDagDetails.runDetails.executorService.execute(new Runnable {
                override def run(): Unit = {

                  createExecutionFlow(nodeId = outboundNodeId, nuoDagDetails)
                  nuoDagDetails.runDetails.inProgressBranchCount -= 1
                }
              })
            }
          }
        }
      }
    } catch {
      case e: Exception => printException(e, nuoDagDetails)
    }
  }


  def findStartPoints(nodeId: String,
                      isInitialCall: Boolean,
                      nuoDagDetails: NuoDagDetails): mutable.ArrayBuffer[String] = {

    /*
    * 09JUL2017:Pulkit:
    *  While finding start points we need to mock up allowed paths when start point and/or allowed/blocked paths are empty.
    *  Because by choosing the end point only, user implies that he is only interested in that particular object execution (hence branch containing the end point).
    *  So, we need to make sure other branches are blocked from execution (in other words only given branch is allowed to be executed)
    * */
    val inbounds = nuoDagDetails.inboundMandatoryNodes(nodeId) ++ nuoDagDetails.inboundOptionalNodes(nodeId)
    if (nuoDagDetails.runDetails.blockedPaths.isEmpty
      && nuoDagDetails.runDetails.allowedPaths.isEmpty
      && nuoDagDetails.runDetails.startPoints.isEmpty
      && inbounds.map(nuoDagDetails.outboundNodes).foldLeft(false)((left, right) => left || right.size > 1)) {

      if (!nuoDagDetails.runDetails.allowedPaths.contains(nodeId)) {
        nuoDagDetails.runDetails.allowedPaths += nodeId
      }
    }

    if (isInitialCall || isTransformAction(nodeId, nuoDagDetails)) {


      /*
      * If the node is a transform action then the it needs to be considered as an execute dependency
      * However, inbound nodes of a transform action can be of any type hence the recursive call for all inbound nodes.
      * */
      /*
      * If inbound list is empty for initial call, that means user wants to run the single object in current path and hence we will return endpoint as the start point
      * */
      if (isInitialCall && inbounds.isEmpty) mutable.ArrayBuffer(nodeId)
      else inbounds.flatMap(findStartPoints(_, isInitialCall = false, nuoDagDetails)).distinct

    } else if (isReaderAction(nodeId, nuoDagDetails)
      || isProfilingAction(nodeId, nuoDagDetails)
      || isExternalAction(nodeId, nuoDagDetails)
      || isUserInputAction(nodeId, nuoDagDetails)
      || isPredictAction(nodeId, nuoDagDetails)) {

      /*
      * The Reader action can always be added without eligibility check.
      * However, there is no need to find dependencies for a reader action. Because that is source data provider.
      * */
      mutable.ArrayBuffer(nodeId)
    } else {

      /*
      * All other types of nodes, e.g., ddl, Process, variable, writer are not required to be traced back further.
      * */
      mutable.ArrayBuffer.empty[String]
    }
  }

  def shouldPathBeProcessed(nodeId: String,
                            nuoDagDetails: NuoDagDetails): Boolean = {

    if (nuoDagDetails.runDetails.blockedPaths.nonEmpty) {
      !nuoDagDetails.runDetails.blockedPaths.contains(nodeId)
    } else if (nuoDagDetails.runDetails.allowedPaths.nonEmpty) {
      nuoDagDetails.runDetails.allowedPaths.contains(nodeId)
    } else {
      true
    }
  }

  def processNode(nodeId: String,
                  //                  nextExecutionStage: Double,
                  dependencyNodes: Vector[String],
                  nuoDagDetails: NuoDagDetails): Unit = {

    if (System.currentTimeMillis() < nuoDagDetails.runDetails.endTime) {

      if (isProcess(nodeId, nuoDagDetails)) {

        /*
        * Execution status of Process is not being used to check whether the execution is complete or not, but their extreme right children's statuses.
        * However, Plan closure checks for the completion of nodes available in executionStatusOf only.
        * Hence, following entry is to force plan closure to verify completion of all the processes as well.
        * */
        NuoDagExecutionManager.addExecutionStatus(nodeId = nodeId, code = "NA", status = 1.0, refParam = "NA", nuoDagDetails = nuoDagDetails)
        findAndRunAllChildren(nodeId, nuoDagDetails)
      } else {
        NuoLogger.logInfo(s"I am going to execute action = ${getNodeName(nodeId, nuoDagDetails)}.", nuoDagDetails)
        invokeActionFunction(nodeId = nodeId,
          dependencyNodes = dependencyNodes, nuoDagDetails)
      }
    } else {
      nuoDagDetails.runDetails.recursiveCallRequired = true
    }
  }

  def executePlan(startPoints: List[String],
                  endPoints: List[String],
                  isRecursiveCall: Boolean,
                  nuoDagDetails: NuoDagDetails): Unit = {

    /** 05JUL2017CET1727:Pulkit
      * The best way to understand execution algorithm is to think of following theory:
      *
      * "Run forward as far as you can, trace back until only if necessary"
      *
      * Explanation: Based on given Start points, End points, Allowed paths and Blocked paths;
      * Keep finding nodes to be executed for a given node until you come across an end point.
      * While finding dependency of a given node, trace back only if the given node could fail without it.
      *
      * We will find above theory applied in findNodesToBeExecuted, findStartPoints and FindWithClauseDependencies functions of NuoDagProcessor
      * */

    nuoDagDetails.runDetails.nodesToBeExecuted ++= startPoints
      .flatMap(findNodesToBeExecuted(_, endPoints, nuoDagDetails))
      .distinct
      .filterNot(nuoDagDetails.runDetails.nodesToBeExecuted.contains)

    if (!isRecursiveCall) {

      NuoLogger.logInfo("I am going to execute the following objects of your Canvas=>" +
        nuoDagDetails.runDetails.nodesToBeExecuted
          .map(getNodeName(_, nuoDagDetails))
          .mkString(", "), nuoDagDetails)
    }
    startPoints.foreach { startPoint =>

      nuoDagDetails.runDetails.inProgressBranchCount += 1
      nuoDagDetails.runDetails.executorService.execute(new Runnable {
        override def run(): Unit = {
          createExecutionFlow(
            nodeId = startPoint, nuoDagDetails)
          nuoDagDetails.runDetails.inProgressBranchCount -= 1
        }
      })
    }

    //    executorService.shutdown()
    //    executorService.awaitTermination(5,TimeUnit.MINUTES)
  }
}*/
