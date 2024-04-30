/*
package dag

import action.NuoDML._
import action.NuoLoop._
import action.NuoStorage._
import action.{NuoExpressionAndVariable, NuoMLaaS}
import canvas.NuoDataTypeHandler.CanvasDataType
import canvas.NuoModifier.ActionAttrName._
import canvas.NuoModifier.ActionPropertyName._
import canvas.NuoModifier._
import canvas.{NuoDataTypeHandler, NuoModifier}
import client.NuoBqClient
import dag.NuoDagProcessor._
import logging.NuoLogger
import logging.NuoLogger._
import dag.NuoDag.NuoDagDetails
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Copyright 2016 NuoCanvas.
  *
  *
  * Created by Pulkit on 26SEP2016
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/


object NuoActionPatternMatcher {


  def getActionAttribute(nodeId: String,
                         processedAttrMap: mutable.HashMap[String, String],
                         attrName: String, nuoDagDetails: NuoDagDetails): String = {

    if (processedAttrMap.contains(attrName)) {

      val attrValue = processedAttrMap(attrName)

      if (attrValue.startsWith("'") && attrValue.endsWith("'")) attrValue.substring(1, attrValue.length - 1)
      else attrValue
    } else ""
  }

  def processAttributeMap(nodeId: String,
                          nuoDagDetails: NuoDagDetails): mutable.HashMap[String, String] = {

    val attrMap = nuoDagDetails.nodes(nodeId).filter(ele => !ele._1.equalsIgnoreCase(NodeName)
      && !ele._1.equalsIgnoreCase(NodeType)
      && !ele._1.equalsIgnoreCase(ColumnMapping)
      && !ele._1.equalsIgnoreCase(NuoModifier.ActionPropertyName.Common.ElementTypeList))
      .asInstanceOf[mutable.HashMap[String, String]]

    val expressionList = attrMap.filter { ele =>
      val dataType = NuoDataTypeHandler.parseBQDataType(ele._2)
      dataType.isEmpty ||
        dataType.get.equalsIgnoreCase(CanvasDataType.Date) ||
        dataType.get.equalsIgnoreCase(CanvasDataType.Time) ||
        dataType.get.equalsIgnoreCase(CanvasDataType.Timestamp)
    }
      .map(ele =>
        s"${
          NuoExpressionAndVariable.processExpression(expressionInput = ele._2,
            includeSourceName = false,
            nodeId = nodeId, nuoDagDetails)._1
        } AS ${ele._1}"
      ).mkString(",")

    if (expressionList.nonEmpty) {

      val resultsAndSchema = NuoBqClient.executeDMLSelectFixedPolling(s"SELECT $expressionList", nuoDagDetails)
      if (resultsAndSchema._1.isDefined) {

        attrMap.filterNot { ele =>
          val dataType = NuoDataTypeHandler.parseBQDataType(ele._2)
          dataType.isEmpty ||
            dataType.get.equalsIgnoreCase(CanvasDataType.Date) ||
            dataType.get.equalsIgnoreCase(CanvasDataType.Time) ||
            dataType.get.equalsIgnoreCase(CanvasDataType.Timestamp)
        } ++= (resultsAndSchema._2.map(_._1) zip resultsAndSchema._1.get.head).toMap
      } else {
        printException(new Exception(s"I could not process attribute map for node = ${getNodeName(nodeId, nuoDagDetails)}"), nuoDagDetails)
        attrMap
      }
    } else {
      attrMap
    }
  }

  def getSourceTablesFor(nodeId: String,
                         nuoDagDetails: NuoDagDetails): List[String] = {

    nuoDagDetails.inboundActualNodes(nodeId)
      .filter(ele => isTransformAction(ele, nuoDagDetails)
        || isReaderAction(ele, nuoDagDetails)
        || isProfilingAction(ele, nuoDagDetails)
        || isExternalAction(ele, nuoDagDetails)
        || isUserInputAction(ele, nuoDagDetails))
      .map(ele => getNodeName(ele, nuoDagDetails)).toList
  }

  /* This function identifies the type of the action and invokes associated function
  */
  def invokeActionFunction(nodeId: String,
                           dependencyNodes: Vector[String],
                           nuoDagDetails: NuoDagDetails): Unit = {

    try {

      val actionName = getNodeName(nodeId, nuoDagDetails)
      val actionType = nuoDagDetails.nodes(nodeId)(NodeType).asInstanceOf[String]

      val tempMap = nuoDagDetails.nodes(nodeId)
      val processedAttrMap = processAttributeMap(nodeId, nuoDagDetails)
      val sourceTables = getSourceTablesFor(nodeId, nuoDagDetails)


      printInfo(s"I am going to execute $actionName of type $actionType", nuoDagDetails)

      actionType match {

        /*
        * *********************************************************************************************************
        * This block is for testing
        * *********************************************************************************************************
        * */
        case "amplifier" | "Amplifier" => printInfo(s"I have executed $actionName of type amplifier", nuoDagDetails)
        case "Fail" | "fail" => throw new Exception(s"I am failing the process run at $actionName")
        /*
        * *********************************************************************************************************
        * *********************************************************************************************************
        * */

        /*
        * *********************************************************************************************************
        * This block is for testing
        * *********************************************************************************************************
        * */
        case "reader" | "Reader" => printInfo(s"I have executed $actionName of type reader", nuoDagDetails)
        /*
        * *********************************************************************************************************
        * *********************************************************************************************************
        * */

        /*
        * *********************************************************************************************************
        * This block is for testing
        * *********************************************************************************************************
        * */
        case "writer" | "Writer" => printInfo(s"I have executed $actionName of type writer", nuoDagDetails)
        /*
        * *********************************************************************************************************
        * *********************************************************************************************************
        * */

        //
        //          /*
        //          * Pattern for Delimited File Reader action
        //          * */
        //          case x if x.equalsIgnoreCase(ReadDelimitedFileServer) =>
        //            val numPartition = globalAttributes(NumPartitionFile)
        //
        //            evalDelimitedFileReader(
        //              //              nodeId,
        //
        //              numPartition.toInt,
        //              actionName,
        //              getActionAttribute(nodeId,FileName),
        //              getActionAttribute(nodeId,ContainsHeader),
        //              "\n",
        //              getActionAttribute(nodeId,Delimiter),
        //              "\"",
        //              tempMap(ColumnMapping)
        //                .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]],nuoDagDetails
        //            )

        //
        //          /*
        //          * Pattern for Delimited File Reader action
        //          * */
        //          case x if x.equalsIgnoreCase(ReadDelimitedFileS3) =>
        //
        //            val numPartition = globalAttributes(NumPartitionFile)
        //
        //            evalDelimitedFileReaderS3(
        //
        //               //              nodeId = nodeId,
        //
        //
        //              numPartition = numPartition.toInt,
        //              actionName = actionName,
        //              bucketName = getActionAttribute(nodeId,BucketName),
        //              accessKey = getActionAttribute(nodeId,AccessKey),
        //              secretKey = getActionAttribute(nodeId,SecretKey),
        //              fileName = getActionAttribute(nodeId,FileName),
        //              containsHeader = getActionAttribute(nodeId,ContainsHeader),
        //              delimiter = getActionAttribute(nodeId,Delimiter),
        //              quoteChar = getActionAttribute(nodeId,QuoteCharacter),
        //              escapeChar = getActionAttribute(nodeId,EscapeCharacter),
        //              parsingMode = getActionAttribute(nodeId,ParsingMode),
        //              charset = getActionAttribute(nodeId,CharacterSet),
        //              commentChar = getActionAttribute(nodeId,CommentChar),
        //              nullValue = getActionAttribute(nodeId,NullValue),
        //              columns = tempMap(ColumnMapping)
        //                .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]],nuoDagDetails
        //            )
        //
        //
        //          /*
        //          * Pattern for Delimited File Writer action
        //          * */
        //          case x if x.equalsIgnoreCase(WriteDelimitedFileServer) =>
        //

        //            evalDelimitedFileWriter(//
        //              actionName,
        //              getActionAttribute(nodeId,FileName),
        //              getActionAttribute(nodeId,ContainsHeader),
        //              "\n",
        //              getActionAttribute(nodeId,Delimiter),
        //              "\"",
        //              "false",
        //              sourceTables.head,
        //              nodeId,
        //              tempMap(ColumnMapping)
        //                .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]],nuoDagDetails
        //            )

        //
        //          /*
        //          * Pattern for Delimited File Writer action
        //          * */
        //          case x if x.equalsIgnoreCase(WriteDelimitedFileS3) =>
        //

        //            evalDelimitedFileWriterS3( //
        //              actionName = actionName,
        //              bucketName = getActionAttribute(nodeId,BucketName),
        //              accessKey = getActionAttribute(nodeId,AccessKey),
        //              secretKey = getActionAttribute(nodeId,SecretKey),
        //              fileName = getActionAttribute(nodeId,FileName),
        //              containsHeader = getActionAttribute(nodeId,ContainsHeader),
        //              delimiter = getActionAttribute(nodeId,Delimiter),
        //              quoteChar = getActionAttribute(nodeId,QuoteCharacter),
        //              escapeChar = getActionAttribute(nodeId,EscapeCharacter),
        //              nullValue = getActionAttribute(nodeId,NullValue),
        //              quoteMode = getActionAttribute(nodeId,QuoteMode),
        //              compressionCodec = getActionAttribute(nodeId,CompressionCodec),
        //              saveMode = getActionAttribute(nodeId,SaveMode),
        //              sourceTable = sourceTables.head,
        //              tenantDir = tenantDir,
        //              nodeId = nodeId,
        //              columns = tempMap(ColumnMapping)
        //                .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]],nuoDagDetails
        //            )

        /*
        * Pattern for Join action
        * */
        case x if x.equalsIgnoreCase(ActionType.Dml.Join) =>


          evalJoin(
            nodeId = nodeId,
            joinerName = actionName,
            joinType = getActionAttribute(nodeId, processedAttrMap, Dml.Join.JoinType, nuoDagDetails),
            joinCondition = getActionAttribute(nodeId, processedAttrMap, Dml.Join.JoinCondition, nuoDagDetails),
            joinColumns = getActionAttribute(nodeId, processedAttrMap, Dml.Join.JoinColumns, nuoDagDetails).split(",").map(_.trim).filterNot(_.isEmpty).toList,
            joinLeftSource = getActionAttribute(nodeId, processedAttrMap, Dml.Join.JoinOrder, nuoDagDetails).split(",").map(_.trim).filterNot(_.isEmpty).head,
            joinRightSource = getActionAttribute(nodeId, processedAttrMap, Dml.Join.JoinOrder, nuoDagDetails).split(",").map(_.trim).filterNot(_.isEmpty).reverse.head,
            columns = tempMap(ColumnMapping)
              .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]]
            , nuoDagDetails)

        /*
        * Pattern for Filter action
        * */
        case x if x.equalsIgnoreCase(ActionType.Dml.Filter) =>



          evalFilter(
            actionName,
            getActionAttribute(nodeId, processedAttrMap, Dml.Filter.FilterCondition, nuoDagDetails),
            sourceTables.head,
            nodeId,
            tempMap(ColumnMapping)
              .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]], nuoDagDetails
          )

        /*
        * Pattern for RemoveDuplicates action
        * */
        case x if x.equalsIgnoreCase(ActionType.Dml.RemoveDuplicate) =>
          //          val tempMap = NuoDAG.NuoDAG.nodes(nodeId)


          evalRemoveDuplicates(
            actionName,
            sourceTables.head,
            nodeId,
            tempMap(ColumnMapping)
              .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]], nuoDagDetails
          )

        /*
        * Pattern for Sort action
        * */
        case x if x.equalsIgnoreCase(ActionType.Dml.Sort) =>
          //          val sortByClause = Vector[String]() ++ getActionAttribute(nodeId,SortByClause)
          val sortByClause = Vector[String]() ++ getActionAttribute(nodeId, processedAttrMap, Dml.Sort.SortByClause, nuoDagDetails)
            .split(",").map(_.trim).filterNot(_.isEmpty)


          evalSort(
            actionName,
            sortByClause,
            sourceTables.head,
            nodeId,
            tempMap(ColumnMapping)
              .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]], nuoDagDetails
          )

        /*
        * Pattern for Aggregate action
        * */
        case x if x.equalsIgnoreCase(ActionType.Dml.Aggregate) =>
          //          val groupByClause = Vector[String]() ++ getActionAttribute(nodeId,processedAttrMap,GroupByClause)

          val groupByClause = Vector[String]() ++ getActionAttribute(nodeId, processedAttrMap, Dml.Aggregate.GroupByClause, nuoDagDetails)
            .split(",").map(_.trim).filterNot(_.isEmpty)

          evalAggregate(
            actionName,
            groupByClause,
            sourceTables.head,
            nodeId,
            tempMap(ColumnMapping)
              .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]], nuoDagDetails
          )

        /*
        * Pattern for Compute action
        * */
        case x if x.equalsIgnoreCase(ActionType.Dml.Compute) =>
          //          val tempMap = NuoDAG.NuoDAG.nodes(nodeId)

          evalCompute(
            actionName,
            nodeId,
            tempMap(ColumnMapping)
              .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]], nuoDagDetails
          )

        /*
        * Pattern for Union action
        * */
        case x if x.equalsIgnoreCase(ActionType.Dml.Union) =>


          val distinctResult = getActionAttribute(nodeId, processedAttrMap, Dml.Common.DistinctResult, nuoDagDetails)
          evalSetOperation(
            nodeId = nodeId,
            actionName = actionName,
            actionType = actionType,
            unionAll = if (distinctResult.isEmpty) false else distinctResult.toBoolean,
            inputOrder = nuoDagDetails.inboundActualMandatoryNodes(nodeId) /*++NuoDAG.inboundActualOptionalNodes(nodeId)*/
            , nuoDagDetails)

        /*
        * Pattern for Intersect action
        * */
        case x if x.equalsIgnoreCase(ActionType.Dml.Intersect) =>

          //          val distinctResult = getActionAttribute(nodeId,processedAttrMap, Dml.Common.DistinctResult)

          evalSetOperation(
            nodeId = nodeId,
            actionName = actionName,
            actionType = actionType,
            unionAll = false,
            inputOrder = nuoDagDetails.inboundActualMandatoryNodes(nodeId) /*++ NuoDAG.inboundActualOptionalNodes(nodeId)*/
            , nuoDagDetails)

        /*
        * Pattern for Minus action
        * */
        case x if x.equalsIgnoreCase(ActionType.Dml.Minus) =>


          //          val distinctResult = getActionAttribute(nodeId,processedAttrMap, Dml.Common.DistinctResult)

          evalSetOperation(
            nodeId,
            actionName,
            actionType,
            unionAll = false,
            ArrayBuffer[String]() ++ getActionAttribute(nodeId, processedAttrMap, Dml.Minus.InputOrder, nuoDagDetails).split(",").map(ele => nuoDagDetails.nodeNameToIdMap(ele.trim))
            , nuoDagDetails)

        /*
        * Pattern for While  action
        * */
        case x if x.equalsIgnoreCase(ActionType.LoopAndVariable.While) =>


          evalWhileLoop(actionName,
            nodeId,
            getActionAttribute(nodeId, processedAttrMap, ActionPropertyName.LoopAndVariable.Common.Condition, nuoDagDetails), nuoDagDetails)


        /*
        * Pattern for Do_Until  action
        * */
        case x if x.equalsIgnoreCase(ActionType.LoopAndVariable.DoUntil) =>


          evalDoUntilLoop(actionName,
            nodeId,
            getActionAttribute(nodeId, processedAttrMap, ActionPropertyName.LoopAndVariable.Common.Condition, nuoDagDetails), nuoDagDetails)

        /*
        * Pattern for Repeat action
        * If input type is Repeat then execute the loop body for counter specified in column Toal_Iteration.
        * Value of each item can be accessed through column "Current_Item"
        * */
        case x if x.equalsIgnoreCase(ActionType.LoopAndVariable.Repeat) =>

          evalRepeatLoop(actionName,
            nodeId,
            getActionAttribute(nodeId, processedAttrMap, ActionPropertyName.LoopAndVariable.Repeat.TotalIterations, nuoDagDetails), nuoDagDetails)

        /*
        * Pattern for Variable Assignment
        * */
        case x if x.equalsIgnoreCase(ActionType.LoopAndVariable.CreateOrUpdateVariable) =>

          evalAssignVariable(actionName = actionName,
            nodeId = nodeId,
            columns = tempMap(ColumnMapping)
              .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]], nuoDagDetails)

        /*
        * Pattern for Merge
        * */
        case x if x.equalsIgnoreCase(ActionType.Dml.Merge) =>

          val isInsert = getActionAttribute(nodeId, processedAttrMap, Dml.Merge.IsInsert, nuoDagDetails)
          val isUpdate = getActionAttribute(nodeId, processedAttrMap, Dml.Merge.IsUpdate, nuoDagDetails)
          val isDelete = getActionAttribute(nodeId, processedAttrMap, Dml.Merge.IsDelete, nuoDagDetails)
          evalMerge(
            nodeId = nodeId,
            actionName = actionName,
            keyColumns = Vector[String]()
              ++ getActionAttribute(nodeId, processedAttrMap, Dml.Merge.KeyColumns, nuoDagDetails).split(",").map(_.trim).filterNot(_.isEmpty),
            sourceTable = getActionAttribute(nodeId, processedAttrMap, Dml.Merge.MergeSource, nuoDagDetails),
            targetTable = getActionAttribute(nodeId, processedAttrMap, Dml.Merge.MergeTarget, nuoDagDetails),
            isInsert = if (isInsert.nonEmpty) isInsert.toBoolean else false,
            isUpdate = if (isUpdate.nonEmpty) isUpdate.toBoolean else false,
            isDelete = if (isDelete.nonEmpty) isDelete.toBoolean else false,
            updateCondition = getActionAttribute(nodeId, processedAttrMap, Dml.Merge.UpdateCondition, nuoDagDetails),
            deleteCondition = getActionAttribute(nodeId, processedAttrMap, Dml.Merge.DeleteCondition, nuoDagDetails),
            columns = tempMap(ColumnMapping)
              .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]], nuoDagDetails = nuoDagDetails)

        /*
        * Pattern for Match-case
        * */
        case x if x.equalsIgnoreCase(ActionType.Dml.Match) =>

          val inputPatternColumn = getActionAttribute(nodeId, processedAttrMap, NuoModifier.ActionPropertyName.Dml.Match.InputPatternColumn, nuoDagDetails)

          evalMatchCase(
            sourceTables.head,
            nodeId,
            actionName,
            actionType,
            inputPatternColumn,
            tempMap(ColumnMapping)
              .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]], nuoDagDetails)

        case x if x.equalsIgnoreCase(ActionType.Dml.Case) =>
        /*
        * Invocation is already taken care of as a part of Match action
        * */

        case x if x.equalsIgnoreCase(ActionType.Dml.DefaultCase) =>
        /*
        * Invocation is already taken care of as a part of Match action
        * */

        /*
        * Pattern for process invocation
        * */
        case x if x.equalsIgnoreCase(ActionType.Process) =>

          printInfo("Something is wrong for updated DAG shall not try to execute node with type process.", nuoDagDetails)

        /*
        * If none of the above function pattern matches then match to the storage action pattern
        * */
        case _ =>
          invokeStorageFunction(//            isRerun: Boolean,
            nodeId: String,
            processedAttrMap,
            dependencyNodes, nuoDagDetails)
      }
    } catch {
      case e: Exception => printException(e, nuoDagDetails)

    }
  }

  /*
  * Storage action functions
  * */
  def invokeStorageFunction(nodeId: String,
                            processedAttrMap: mutable.HashMap[String, String],
                            dependencyNodes: Vector[String],
                            nuoDagDetails: NuoDagDetails): Unit = {

    /*
    * Register all the UDF
    * */

    val actionName = getNodeName(nodeId, nuoDagDetails)
    val actionType = nuoDagDetails.nodes(nodeId)(NodeType).asInstanceOf[String]

    val tempMap = nuoDagDetails.nodes(nodeId)
    val sourceTables = getSourceTablesFor(nodeId, nuoDagDetails)

    /*
    * If any of the outbound node of current node is Merge then persist this node
    * */

    actionType match {

      /*
      * Pattern for Create Table action
      * */
      case x if x.equalsIgnoreCase(ActionType.Storage.CreateTable) =>
        val userDBName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.DatasetName, nuoDagDetails)
        val userTableName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.TableName, nuoDagDetails)
        if (userTableName.isEmpty)
          NuoLogger.logError(s"TableName property of the Create Table action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I cannot execute it.", nuoDagDetails)

        evalCreateTable(userDatasetName = userDBName,
          userTableName = userTableName,
          columns = tempMap(ColumnMapping)
            .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]],
          turnOnLogging = true
          , nuoDagDetails)

      /*
      * Pattern for "Does Table Exist" action
      * */
      case x if x.equalsIgnoreCase(ActionType.Storage.CheckTableExist) =>
        val userDBName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.DatasetName, nuoDagDetails)
        val userTableName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.TableName, nuoDagDetails)
        if (userTableName.isEmpty)
          NuoLogger.logError(s"TableName property of the Check Table Exist action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I cannot execute it.", nuoDagDetails)

        evalDoesTableExist(userDatasetName = userDBName,
          userTableName = userTableName, nuoDagDetails)

      /*
      * Pattern for Drop Table action
      * */
      case x if x.equalsIgnoreCase(ActionType.Storage.DeleteTable) =>
        val userDatasetName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.DatasetName, nuoDagDetails)
        val userTableName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.TableName, nuoDagDetails)
        if (userTableName.isEmpty)
          NuoLogger.logError(s"TableName property of the Delete Table action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I cannot execute it.", nuoDagDetails)

        evalDeleteTable(userDatasetName = userDatasetName,
          userTableName = userTableName, nuoDagDetails)

      /*
      * Pattern for Create Database action
      * */
      case x if x.equalsIgnoreCase(ActionType.Storage.CreateDataset) =>
        val userDatasetName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.DatasetName, nuoDagDetails)
        if (userDatasetName.isEmpty)
          NuoLogger.logError(s"DatasetName property of the Create Dataset action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I cannot execute it.", nuoDagDetails)

        evalCreateDataset(userDatasetName = userDatasetName, turnOnLogging = true, nuoDagDetails)

      /*
      * Pattern for Drop Database action
      * */
      case x if x.equalsIgnoreCase(ActionType.Storage.DeleteDataset) =>
        val userDatasetName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.DatasetName, nuoDagDetails)
        if (userDatasetName.isEmpty)
          NuoLogger.logError(s"DatasetName property of the Delete Dataset action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I cannot execute it.", nuoDagDetails)

        evalDeleteDataset(actionName = actionName,
          userDatasetName = userDatasetName, nuoDagDetails)


      /*
      * Pattern for Change Column action
      * */
      case x if x.equalsIgnoreCase(ActionType.Storage.UpdateTableStructure) =>
        val userDatasetName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.DatasetName, nuoDagDetails)
        val userTableName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.TableName, nuoDagDetails)
        if (userDatasetName.isEmpty)
          NuoLogger.logError(s"TableName property of the Update Table Structure action = ${getNodeName(nodeId, nuoDagDetails)} is empty. I cannot execute it.", nuoDagDetails)


        evalUpdateTableStructure(userDatasetName,
          userTableName,
          tempMap(ColumnMapping)
            .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]], nuoDagDetails
        )

      /*
      * Pattern for Merge_overwrite
      * */

      case x if x.equalsIgnoreCase(ActionType.Storage.MergeIntoTable) =>
        val tgtDatasetName = getActionAttribute(nodeId, processedAttrMap, Storage.MergeIntoTable.TgtDatasetName, nuoDagDetails)
        val tgtTableName = getActionAttribute(nodeId, processedAttrMap, Storage.MergeIntoTable.TgtTableName, nuoDagDetails)
        val keyColumns = getActionAttribute(nodeId, processedAttrMap, Storage.MergeIntoTable.KeyColumns, nuoDagDetails).split(",").map(_.trim).filterNot(_.isEmpty).toList
        val mergeCondition = getActionAttribute(nodeId, processedAttrMap, Storage.MergeIntoTable.MergeCondition, nuoDagDetails)
        val updateCondition = getActionAttribute(nodeId, processedAttrMap, Storage.MergeIntoTable.UpdateCondition, nuoDagDetails)
        val deleteCondition = getActionAttribute(nodeId, processedAttrMap, Storage.MergeIntoTable.DeleteCondition, nuoDagDetails)
        val isInsert = getActionAttribute(nodeId, processedAttrMap, Storage.MergeIntoTable.IsInsert, nuoDagDetails)
        val isUpdate = getActionAttribute(nodeId, processedAttrMap, Storage.MergeIntoTable.IsUpdate, nuoDagDetails)
        val isDelete = getActionAttribute(nodeId, processedAttrMap, Storage.MergeIntoTable.IsDelete, nuoDagDetails)


        evalMergeIntoTable(nodeId = nodeId,
          actionName = actionName,
          userTgtDatasetName = tgtDatasetName,
          targetTableName = tgtTableName,
          sourceTableName = sourceTables.head,
          keyColumnsForTarget = keyColumns,
          customMergeCondition = mergeCondition,
          updateCondition = updateCondition,
          deleteCondition = deleteCondition,
          isInsert = if (isInsert.nonEmpty) isInsert.toBoolean else false,
          isUpdate = if (isUpdate.nonEmpty) isUpdate.toBoolean else false,
          isDelete = if (isDelete.nonEmpty) isDelete.toBoolean else false,
          isSynch = false,
          dependencyNodes = dependencyNodes,
          columns = tempMap(ColumnMapping)
            .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]], nuoDagDetails
        )

      /*
      * Pattern for SaveAsNuoTable
      * */

      case x if x.equalsIgnoreCase(ActionType.Storage.SaveAsTable) =>
        val userDatasetName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.DatasetName, nuoDagDetails)
        val userTableName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.TableName, nuoDagDetails)
        val writeIfEmpty = getActionAttribute(nodeId, processedAttrMap, Storage.Common.WriteIntoTable.WriteIfEmpty, nuoDagDetails)
        val appendOutput = getActionAttribute(nodeId, processedAttrMap, Storage.Common.WriteIntoTable.AppendOutput, nuoDagDetails)


        evalSaveAsNuoTable(actionName = actionName,
          userDatasetName = userDatasetName,
          sourceTableName = sourceTables.head,
          targetTableName = userTableName,
          writeIfEmpty = if (writeIfEmpty.nonEmpty) writeIfEmpty.toBoolean else false,
          appendOutput = if (appendOutput.nonEmpty) appendOutput.toBoolean else false,
          dependencyNodes = dependencyNodes,
          columns = tempMap(ColumnMapping)
            .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]], nuoDagDetails
        )

      /*
      * Pattern for Profile Results action
      * */
      case x if x.equalsIgnoreCase(ActionType.DataProfiling.ProfileResult) =>
        //          val groupByClause = Vector[String]() ++ getActionAttribute(nodeId,processedAttrMap,GroupByClause)

        evalProfileResult(
          actionName,
          sourceTables.head,
          nodeId,
          dependencyNodes,
          columns = tempMap(ColumnMapping)
            .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]],
          nuoDagDetails)

      /*
      * Pattern for ExportTableToFile
      * */

      case x if x.equalsIgnoreCase(ActionType.Storage.ExportTableToFile) =>
        val userDatasetName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.DatasetName, nuoDagDetails)
        val userTableName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.TableName, nuoDagDetails)
        val outputFilePrefix = getActionAttribute(nodeId, processedAttrMap, Storage.Common.ExportTableToFile.OutputFilePrefix, nuoDagDetails)
        val destinationFormat = getActionAttribute(nodeId, processedAttrMap, Storage.Common.ExportTableToFile.OutputFormat, nuoDagDetails)
        val printHeader = getActionAttribute(nodeId, processedAttrMap, Storage.Common.ExportTableToFile.PrintHeader, nuoDagDetails)
        val enableCompression = getActionAttribute(nodeId, processedAttrMap, Storage.Common.ExportTableToFile.EnableCompression, nuoDagDetails)


        evalExportTableToFile(actionName = actionName,
          userDatasetName = userDatasetName,
          tableName = userTableName,
          outputFilePrefix = outputFilePrefix,
          destinationFormat = destinationFormat,
          printHeader = printHeader,
          enableCompression = enableCompression,
          nuoDagDetails
        )


      /*
      * Pattern for ReadNuoTable
      * */

      case x if x.equalsIgnoreCase(ActionType.Storage.ReadNuoTable) =>
        val userDatasetName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.DatasetName, nuoDagDetails)
        val userTableName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.TableName, nuoDagDetails)
        val filterCondition = getActionAttribute(nodeId, processedAttrMap, Storage.ReadNuoTable.WhereClause, nuoDagDetails)


        evalReadNuoTable(nodeId = nodeId,
          actionName = actionName,
          userDatasetName = userDatasetName,
          userTableName = userTableName,
          filterCondition = filterCondition,
          columns = tempMap(ColumnMapping)
            .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]], nuoDagDetails
        )

      case x if isTransferAction(nodeId, nuoDagDetails) =>

        val awsAccessKey = NuoActionPatternMatcher.getActionAttribute(nodeId, processedAttrMap, NuoModifier.ActionPropertyName.Storage.Common.AwsAccessKey, nuoDagDetails)
        val awsSecretKey = NuoActionPatternMatcher.getActionAttribute(nodeId, processedAttrMap, NuoModifier.ActionPropertyName.Storage.Common.AwsSecretKey, nuoDagDetails)
        val s3BucketName = NuoActionPatternMatcher.getActionAttribute(nodeId, processedAttrMap, NuoModifier.ActionPropertyName.Storage.Common.S3BucketName, nuoDagDetails)
        val s3FilePrefix = NuoActionPatternMatcher.getActionAttribute(nodeId, processedAttrMap, NuoModifier.ActionPropertyName.Storage.Common.S3FilePrefix, nuoDagDetails)
        val nuoFilePrefix = NuoActionPatternMatcher.getActionAttribute(nodeId, processedAttrMap, NuoModifier.ActionPropertyName.Storage.Common.NuoFilePrefix, nuoDagDetails)

        evalImportExportFilesBetweenS3(nodeId = nodeId,
          awsAccessKey = awsAccessKey,
          awsSecretKey = awsSecretKey,
          s3BucketName = s3BucketName,
          s3FilePrefix = s3FilePrefix,
          nuoFilePrefix = nuoFilePrefix,
          nuoDagDetails = nuoDagDetails)

      case x if List(ActionType.Storage.LoadDelimitedFilesFromS3, ActionType.Storage.LoadNLDJsonFilesFromS3).contains(x) =>
        val writeIfEmpty = getActionAttribute(nodeId, processedAttrMap, Storage.Common.WriteIntoTable.WriteIfEmpty, nuoDagDetails)
        val appendOutput = getActionAttribute(nodeId, processedAttrMap, Storage.Common.WriteIntoTable.AppendOutput, nuoDagDetails)
        val s3BucketName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.S3BucketName, nuoDagDetails)
        val s3FilePrefix = NuoActionPatternMatcher.getActionAttribute(nodeId, processedAttrMap, NuoModifier.ActionPropertyName.Storage.Common.S3FilePrefix, nuoDagDetails)
        val awsAccessKey = getActionAttribute(nodeId, processedAttrMap, Storage.Common.AwsAccessKey, nuoDagDetails)
        val awsSecretKey = getActionAttribute(nodeId, processedAttrMap, Storage.Common.AwsSecretKey, nuoDagDetails)
        val targetDatasetName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.DatasetName, nuoDagDetails)
        val targetTableName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.TableName, nuoDagDetails)
        val delimiter = getActionAttribute(nodeId, processedAttrMap, Storage.Common.LoadDelimitedFiles.Delimiter, nuoDagDetails)
        val quoteCharacter = getActionAttribute(nodeId, processedAttrMap, Storage.Common.LoadDelimitedFiles.QuoteCharacter, nuoDagDetails)
        val leadingRowsToSkip = getActionAttribute(nodeId, processedAttrMap, Storage.Common.LoadDelimitedFiles.LeadingRowsToSkip, nuoDagDetails)

        evalLoadFilesFromS3(nodeId,
          s3BucketName = s3BucketName,
          s3FilePrefix = s3FilePrefix,
          awsAccessKey = awsAccessKey,
          awsSecretKey = awsSecretKey,
          userTgtDatasetName = targetDatasetName,
          targetTableName = targetTableName,
          writeIfEmpty = if (writeIfEmpty.nonEmpty) writeIfEmpty.toBoolean else false,
          appendOutput = if (appendOutput.nonEmpty) appendOutput.toBoolean else false,
          delimiter = delimiter,
          quoteCharacter = quoteCharacter,
          leadingRowsToSkip = leadingRowsToSkip,
          nuoDagDetails)
      //
      //      case x if x.equalsIgnoreCase(ActionType.Storage.LoadNLDJsonFilesFromS3) =>
      //        val writeIfEmpty = getActionAttribute(nodeId, processedAttrMap, Storage.Common.WriteIntoTable.WriteIfEmpty, nuoDagDetails)
      //        val appendOutput = getActionAttribute(nodeId, processedAttrMap, Storage.Common.WriteIntoTable.AppendOutput, nuoDagDetails)
      //        val s3BucketName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.S3FileReader.S3BucketName, nuoDagDetails)
      //
      //        val s3PrefixIncludeList = getActionAttribute(nodeId, processedAttrMap, Storage.Common.S3FileReader.S3PrefixIncludeList, nuoDagDetails).split(",").map(_.trim).filterNot(_.isEmpty)
      //        val s3PrefixExcludeList = getActionAttribute(nodeId, processedAttrMap, Storage.Common.S3FileReader.S3PrefixExcludeList, nuoDagDetails).split(",").map(_.trim).filterNot(_.isEmpty)
      //        val awsAccessKey = getActionAttribute(nodeId, processedAttrMap, Storage.Common.S3FileReader.AwsAccessKey, nuoDagDetails)
      //        val awsSecretKey = getActionAttribute(nodeId, processedAttrMap, Storage.Common.S3FileReader.AwsSecretKey, nuoDagDetails)
      //        val targetDatasetName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.DatasetName, nuoDagDetails)
      //        val targetTableName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.TableName, nuoDagDetails)
      //        val deleteSourceAfterTransfer = getActionAttribute(nodeId, processedAttrMap, Storage.Common.S3FileReader.DeleteSourceAfterTransfer, nuoDagDetails)
      //
      //        evalLoadNLDJsonFilesFromS3(nodeId,
      //          s3BucketName = s3BucketName,
      //          s3FilePrefix = "",
      //          awsAccessKey = awsAccessKey,
      //          awsSecretKey = awsSecretKey,
      //          userTgtDatasetName = targetDatasetName,
      //          targetTableName = targetTableName,
      //          writeIfEmpty = if (writeIfEmpty.nonEmpty) writeIfEmpty.toBoolean else false,
      //          appendOutput = if (appendOutput.nonEmpty) appendOutput.toBoolean else false,
      //          deleteSourceAfterTransfer = if (deleteSourceAfterTransfer.nonEmpty) deleteSourceAfterTransfer.toBoolean else false,
      //          nuoDagDetails)

      case x if x.equalsIgnoreCase(ActionType.Storage.LoadDelimitedFiles) =>
        val writeIfEmpty = getActionAttribute(nodeId, processedAttrMap, Storage.Common.WriteIntoTable.WriteIfEmpty, nuoDagDetails)
        val appendOutput = getActionAttribute(nodeId, processedAttrMap, Storage.Common.WriteIntoTable.AppendOutput, nuoDagDetails)

        val sourceFileList = getActionAttribute(nodeId, processedAttrMap, Storage.Common.GcsFileDetails.SourceFileList, nuoDagDetails).split(",").map(_.trim).filterNot(_.isEmpty)
        val targetDatasetName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.DatasetName, nuoDagDetails)
        val targetTableName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.TableName, nuoDagDetails)
        val delimiter = getActionAttribute(nodeId, processedAttrMap, Storage.Common.LoadDelimitedFiles.Delimiter, nuoDagDetails)
        val quoteCharacter = getActionAttribute(nodeId, processedAttrMap, Storage.Common.LoadDelimitedFiles.QuoteCharacter, nuoDagDetails)
        val leadingRowsToSkip = getActionAttribute(nodeId, processedAttrMap, Storage.Common.LoadDelimitedFiles.LeadingRowsToSkip, nuoDagDetails)


        evalLoadDelimitedGCSFiles(nodeId,
          sourceFileList = sourceFileList.toList,
          userTgtDatasetName = targetDatasetName,
          targetTableName = targetTableName,
          writeIfEmpty = if (writeIfEmpty.nonEmpty) writeIfEmpty.toBoolean else false,
          appendOutput = if (appendOutput.nonEmpty) appendOutput.toBoolean else false,
          delimiter = delimiter,
          quoteCharacter = quoteCharacter,
          leadingRowsToSkip = leadingRowsToSkip, nuoDagDetails)

      case x if x.equalsIgnoreCase(ActionType.Storage.LoadNLDJsonFiles) =>
        val writeIfEmpty = getActionAttribute(nodeId, processedAttrMap, Storage.Common.WriteIntoTable.WriteIfEmpty, nuoDagDetails)
        val appendOutput = getActionAttribute(nodeId, processedAttrMap, Storage.Common.WriteIntoTable.AppendOutput, nuoDagDetails)

        val sourceFileList = getActionAttribute(nodeId, processedAttrMap, Storage.Common.GcsFileDetails.SourceFileList, nuoDagDetails).split(",").map(_.trim).filterNot(_.isEmpty)
        val targetDatasetName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.DatasetName, nuoDagDetails)
        val targetTableName = getActionAttribute(nodeId, processedAttrMap, Storage.Common.TableName, nuoDagDetails)

        evalLoadNLDJsonGCSFiles(nodeId,
          sourceFileList = sourceFileList.toList,
          userTgtDatasetName = targetDatasetName,
          targetTableName = targetTableName,
          writeIfEmpty = if (writeIfEmpty.nonEmpty) writeIfEmpty.toBoolean else false,
          appendOutput = if (appendOutput.nonEmpty) appendOutput.toBoolean else false,
          nuoDagDetails)

      case _ =>
        invokeChartFunction(nodeId: String,
          processedAttrMap,
          dependencyNodes, nuoDagDetails)
    }

    if (isDDLAction(nodeId, nuoDagDetails))
      NuoDagExecutionManager.addExecutionStatus(nodeId, "DDL", 1.0, "NA", nuoDagDetails)
    //    if (isDDLAction(nodeId, nuoDagDetails) || isWriterAction(nodeId, nuoDagDetails) || isMLaaSAction(nodeId, nuoDagDetails))
    //      NuoBqClient.writeStorageMetadataToFile(nuoDagDetails)
  }

  /*
  * Chart action functions
  * */
  def invokeChartFunction(nodeId: String,
                          processedAttrMap: mutable.HashMap[String, String],
                          dependencyNodes: Vector[String],
                          nuoDagDetails: NuoDagDetails): Unit = {


    /*
    * Register all of the UDFs
    * */
    //      NuoFunction.registerUDFs(hiveContext)

    val actionName = getNodeName(nodeId, nuoDagDetails)
    val actionType = nuoDagDetails.nodes(nodeId)(NodeType).asInstanceOf[String]

    val tempMap = nuoDagDetails.nodes(nodeId)

    val sourceTables = getSourceTablesFor(nodeId, nuoDagDetails)

    nodeId match {

      /*
      * Pattern for Create Table action
      * */
      case x if NuoDagProcessor.isChartAction(nodeId, nuoDagDetails) =>

        evalSaveAsNuoTable(actionName = actionName,
          userDatasetName = NuoStorageModifier.userCacheDatasetName,
          sourceTableName = sourceTables.head,
          targetTableName = nodeId,
          writeIfEmpty = false,
          appendOutput = false,
          dependencyNodes = dependencyNodes,
          columns = tempMap(ColumnMapping)
            .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]], nuoDagDetails)

      /*
      * Pattern for Machine Learning as a Service actions
      * */
      case x if NuoDagProcessor.isTrainModelAction(nodeId, nuoDagDetails) || NuoDagProcessor.isPredictAction(nodeId, nuoDagDetails) =>
        NuoMLaaS.evalTrainOrPredictMlModel(nodeId, dependencyNodes, processedAttrMap, nuoDagDetails)
      case _ =>
        invokeAIaaSFunction(nodeId: String,
          processedAttrMap,
          dependencyNodes, nuoDagDetails)

    }
  }

  /*
  * External action functions
  * */
  def invokeAIaaSFunction(nodeId: String,
                          processedAttrMap: mutable.HashMap[String, String],
                          dependencyNodes: Vector[String],
                          nuoDagDetails: NuoDagDetails): Unit = {

    val tempMap = nuoDagDetails.nodes(nodeId)
    val actionType = tempMap(NuoModifier.ActionAttrName.NodeType).asInstanceOf[String]

    actionType match {

      case x if isImageAction(nodeId, nuoDagDetails)
        || isNlpAction(nodeId, nuoDagDetails)
        || isTranslateAction(nodeId, nuoDagDetails)
        || isVideoAction(nodeId, nuoDagDetails)
        || isSpeechAction(nodeId, nuoDagDetails) =>

        action.NuoAIaaS.evalAIaaSFunction(nodeId = nodeId,null)

      /*
      * Default pattern
      * */
      case _ =>
        invokeExtApiFunction(nodeId: String,
          processedAttrMap,
          dependencyNodes, nuoDagDetails)

    }
  }

  /*
  * External action functions
  * */
  def invokeExtApiFunction(nodeId: String,
                           processedAttrMap: mutable.HashMap[String, String],
                           dependencyNodes: Vector[String],
                           nuoDagDetails: NuoDagDetails): Unit = {

    val actionName = getNodeName(nodeId, nuoDagDetails)
    val actionType = nuoDagDetails.nodes(nodeId)(NodeType).asInstanceOf[String]

    val tempMap = nuoDagDetails.nodes(nodeId)


    actionType match {

      case x if x.equalsIgnoreCase(ActionType.External.InHouse.AnalyzeSentiment) =>

        action.NuoAIaaS.evalAnalyzeSentiment(nodeId, dependencyNodes, nuoDagDetails)

      //      case x if x.equalsIgnoreCase(ActionType.UserInput.ChoiceInput) =>
      //
      //        action.NuoAIaaS.evalChoiceInput(nodeId, dependencyNodes, nuoDagDetails)

      /*
      * Default pattern
      * */
      case _ =>
        printInfo("I don't know about this action\nAction Name: " + actionName + " Action Type:" + actionType, nuoDagDetails)

    }
  }
}
*/
