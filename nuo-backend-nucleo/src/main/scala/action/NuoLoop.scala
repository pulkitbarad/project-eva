//package action
//
//import action.NuoExpressionAndVariable._
//import canvas.NuoModifier
//import dag.NuoDag.NuoDagDetails
//import dag.{NuoActionPatternMatcher, NuoTenantDetails, NuoDagExecutionManager, NuoDagProcessor}
//import metadata.VariableMetadata.NuoVariable
////import client.NuoBQClient.executeDML
//import dag.NuoDagProcessor._
//
///**
//  * Copyright 2015-2018 NuoCanvas.
//  *
//  *
//  * Created by Pulkit on 09Feb2017
//  *
//  * Content of this file is proprietary and confidential.
//  * It shall not be reused or disclosed without prior consent
//  * of distributor
//  **/
//
//object NuoLoop {
//
//  //
//  //  /*
//  //  * This function verify and run that while loop
//  //  * */
//  //  def verifyAndRunWhileLoop(conditionExp: String,
//  //                            actionName: String,
//  //                            nodeId: String): Unit = {
//  //
//  //
//  //    val writerChildren = findExtremeWriterChildren(nodeId)
//  //
//  //    if (conditionExp.toBoolean) {
//  //      if (writerChildren.forall(isObjectExecutionComplete)) {
//  //        dag.NuoDAGExecutionManager.updateExecutionStatus(nodeId, "LOOP", NuoDAGProcessor.nuoTenantDetails.runDetails.executionStatusOf(nodeId) + 0.1, "NA")
//  //        NuoDAG.hierarchy(nodeId)._2.foreach(NuoDAGExecutionManager.removeExecutionStatus)
//  //      }
//  //
//  //
//  //      findAndRunAllChildren(nodeId)
//  //      processNode(nodeId, Vector.empty[String])
//  //      //      verifyAndRunWhileLoop(conditionExp, actionName, nodeId)
//  //    } else {
//  //      dag.NuoDAGExecutionManager.updateExecutionStatus(nodeId, "LOOP", 2.0, "NA")
//  //    }
//  //  }
//  //
//  //
//  //  /*
//  //  * This function verify and run that Do until loop
//  //  * */
//  //  def verifyAndRunDoUntilLoop(conditionExp: String,
//  //                              actionName: String,
//  //                              nodeId: String): Unit = {
//  //
//  //    val writerChildren = findExtremeWriterChildren(nodeId)
//  //
//  //    if (writerChildren.forall(isObjectExecutionComplete)) {
//  //      NuoDAG.hierarchy(nodeId)._2.foreach(NuoDAGExecutionManager.removeExecutionStatus)
//  //      dag.NuoDAGExecutionManager.updateExecutionStatus(nodeId, "LOOP", NuoDAGProcessor.nuoTenantDetails.runDetails.executionStatusOf(nodeId) + 0.1, "NA")
//  //    }
//  //
//  //    findAndRunAllChildren(nodeId)
//  //
//  //    if (conditionExp.toBoolean) {
//  //      processNode(nodeId, Vector.empty[String])
//  //      //      verifyAndRunDoUntilLoop(conditionExp, actionName, nodeId)
//  //    } else {
//  //      dag.NuoDAGExecutionManager.updateExecutionStatus(nodeId, "LOOP", 2.0, "NA")
//  //    }
//  //  }
//
//
//  /*
//  * This function execute the body of While loop
//  * */
//  def evalWhileLoop(actionName: String,
//                    nodeId: String,
//                    conditionExpression: String,
//                    nuoTenantDetails: NuoDagDetails): Unit = {
//
//    if (!nuoTenantDetails.runDetails.executionStatusOf.contains(nodeId) || nuoTenantDetails.runDetails.executionStatusOf(nodeId)< 1.0) {
//      dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "LOOP", 1.0, "NA",nuoTenantDetails)
//    }
//
//    val writerChildren = findExtremeWriterChildren(nodeId,nuoTenantDetails)
//
//    if (conditionExpression.toBoolean) {
//      if (writerChildren.forall(isObjectExecutionComplete(_,nuoTenantDetails))) {
//        dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "LOOP", nuoTenantDetails.runDetails.executionStatusOf(nodeId) + 0.1, "NA",nuoTenantDetails)
//        nuoTenantDetails.hierarchy(nodeId)._2.foreach(NuoDagExecutionManager.removeExecutionStatus(_,nuoTenantDetails))
//      }
//      findAndRunAllChildren(nodeId,nuoTenantDetails)
//      processNode(nodeId, Vector.empty[String],nuoTenantDetails)
//      //      verifyAndRunWhileLoop(conditionExp, actionName, nodeId)
//    } else {
//      dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "LOOP", 2.0, "NA",nuoTenantDetails)
//    }
//  }
//
//  /*
//  * This function execute the body of While loop
//  * */
//  def evalDoUntilLoop(actionName: String,
//                      nodeId: String,
//                      conditionExpression: String,nuoTenantDetails:NuoDagDetails): Unit = {
//
//    if (!nuoTenantDetails.runDetails.executionStatusOf.contains(nodeId) || nuoTenantDetails.runDetails.executionStatusOf(nodeId)< 1.0) {
//      dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "LOOP", 1.0, "NA",nuoTenantDetails)
//    }
//
//    val writerChildren = findExtremeWriterChildren(nodeId,nuoTenantDetails)
//
//    if (writerChildren.forall(isObjectExecutionComplete(_,nuoTenantDetails))) {
//      nuoTenantDetails.hierarchy(nodeId)._2.foreach(NuoDagExecutionManager.removeExecutionStatus(_,nuoTenantDetails))
//      dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "LOOP", nuoTenantDetails.runDetails.executionStatusOf(nodeId) + 0.1, "NA",nuoTenantDetails)
//    }
//    findAndRunAllChildren(nodeId,nuoTenantDetails)
//
//    if (conditionExpression.toBoolean) {
//      processNode(nodeId, Vector.empty[String],nuoTenantDetails)
//      //      verifyAndRunDoUntilLoop(conditionExp, actionName, nodeId)
//    } else {
//      dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "LOOP", 2.0, "NA",nuoTenantDetails)
//    }
//  }
//
//
//  /*
//  * This function execute the body of While loop
//  * */
//  def evalRepeatLoop(actionName: String,
//                     nodeId: String,
//                     totalIterationsExp: String,nuoTenantDetails:NuoDagDetails): Unit = {
//
//    dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "LOOP", 1.0, "NA",nuoTenantDetails)
//
//    val writerChildren = findExtremeWriterChildren(nodeId,nuoTenantDetails)
//
//
//    val sourceTables = NuoActionPatternMatcher.getSourceTablesFor(nodeId,nuoTenantDetails)
//
//    val sourceTable = if (sourceTables.nonEmpty) {
//      sourceTables.head
//    } else {
//      ""
//    }
//
//    var loopCounter = totalIterationsExp.toInt
//    assignVariable(nodeId = nodeId,
//      scopeNodeId = nodeId,
//      variableInfo = NuoVariable(Scope = "",
//        Name = s"${NuoModifier.ActionPropertyName.LoopAndVariable.Common.CurrentItem}",
//        Type = "",
//        Value = loopCounter.toString,
//        CastValue = ""),nuoTenantDetails)
//
//    var continueLoop = true
//    while (continueLoop && loopCounter > 0) {
//
//      if (writerChildren.forall(isObjectExecutionComplete(_,nuoTenantDetails))) {
//        nuoTenantDetails.hierarchy(nodeId)._2.foreach(NuoDagExecutionManager.removeExecutionStatus(_,nuoTenantDetails))
//        dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "LOOP", nuoTenantDetails.runDetails.executionStatusOf(nodeId) + 0.1, "NA",nuoTenantDetails)
//
//        /*
//        * The assumption is that Total_Iterations column expression of Loop action will always evaluate to the integer result.
//        * */
//        loopCounter -= 1
//        assignVariable(nodeId = nodeId,
//          scopeNodeId = nodeId,
//          variableInfo = NuoVariable(Scope = "",
//            Name = s"${NuoModifier.ActionPropertyName.LoopAndVariable.Common.CurrentItem}",
//            Type = "",
//            Value = loopCounter.toString,
//            CastValue = ""),nuoTenantDetails)
//      }
//      if(loopCounter>0){
//        findAndRunAllChildren(nodeId,nuoTenantDetails)
//      }else{
//        dag.NuoDagExecutionManager.addExecutionStatus(nodeId, "LOOP", 2.0, "NA",nuoTenantDetails)
//      }
//      if(System.currentTimeMillis()>= nuoTenantDetails.runDetails.endTime){
//        continueLoop=false
//        nuoTenantDetails.runDetails.recursiveCallRequired = true
//      }
//    }
//  }
//}
