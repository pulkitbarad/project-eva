/*
package action

import canvas.NuoDataTypeHandler
import canvas.NuoModifier.Constant._
import dag.NuoDag._
import dag.NuoDagProcessor
import dag.NuoDagProcessor._
import logging.NuoLogger
import metadata.VariableMetadata.NuoVariable

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 09Feb2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoExpressionAndVariable {


  /*
  * This function finds the variable scope based on following logic
  * if node type of the given node is Variable_Assignment then Scope is parent of given node
  * if parent node id of given node is 0 then Scope is __businessCanvas
  * if node id is 0 of the given node then Scope is __businessCanvas
  * in all other cases the given node name is scope of variable
  * */
  def findVariableInitialScope(nodeId: String,nuoDagDetails:NuoDagDetails): String = {

    if (NuoDagProcessor.isVariableAction(nodeId,nuoDagDetails)) {
      getNodeName(nuoDagDetails.hierarchy(nodeId)._1,nuoDagDetails)
    } else {
      getNodeName(nodeId,nuoDagDetails)
    }
  }

  /*
  * This function returns
    * (Variable value,Variable type)
  * */
  def findVariable(variableName: String,
                   nodeId: String,nuoDagDetails:NuoDagDetails): Option[NuoVariable] = {
    /*
    * If variable name with current node as scope exist return it
    * else if current node is business canvas then it means variable does not exist in entire hierarchy of requested node
    * else recursively find variable in parent node scope
    * */

    val variableScope = findVariableInitialScope(nodeId,nuoDagDetails)

    if (nuoDagDetails.runDetails.variables.contains((variableScope, variableName))) {
      Some(nuoDagDetails.runDetails.variables((variableScope, variableName)))
    } else if (nodeId.equalsIgnoreCase("0")) {
      NuoLogger.printInfo(s"${variableName} does not exist",nuoDagDetails)
      None
    } else {
      NuoLogger.printInfo(s"${variableName} does not exist in scope ${getNodeName(nodeId,nuoDagDetails)}",nuoDagDetails)
      val parentNodeId = nuoDagDetails.hierarchy(nodeId)._1
      findVariable(variableName, parentNodeId,nuoDagDetails)
    }
  }

  /*
  * Following function assigns content to given variable if exists otherwise create new variable
  * */
  def assignVariable(nodeId: String,
                     scopeNodeId: String,
                     variableInfo: metadata.VariableMetadata.NuoVariable,nuoDagDetails:NuoDagDetails): Unit = {

    variableInfo.Scope = findVariableInitialScope(scopeNodeId,nuoDagDetails)

    if (nuoDagDetails.runDetails.variables.contains((variableInfo.Scope, variableInfo.Name))) {
      nuoDagDetails.runDetails.variables.remove((variableInfo.Scope, variableInfo.Name))
    }
    if (variableInfo.Type.nonEmpty && variableInfo.Value.nonEmpty) {

      variableInfo.CastValue = NuoDataTypeHandler.getExpressionCastFunction(variableInfo.Value, variableInfo.Type)
    } else if (variableInfo.Value.nonEmpty) {

      variableInfo.CastValue = NuoDataTypeHandler.getExpressionCastFunction(variableInfo.Value)
    }
    nuoDagDetails.runDetails.variables.put((variableInfo.Scope, variableInfo.Name), variableInfo)
    NuoLogger.printInfo(s"Scope=${variableInfo.Scope}",nuoDagDetails)
    NuoLogger.printInfo(s"Name=${variableInfo.Name}",nuoDagDetails)
    NuoLogger.printInfo(s"Value=${variableInfo.CastValue}",nuoDagDetails)

    metadata.VariableMetadata.updateVariables(nuoDagDetails)
  }

  /*
  * Following function assigns content to given variable if exists otherwise create new variable
  * */
  def assignVariables(nodeId: String,
                      scopeNodeId: String,
                      paramVariables: metadata.VariableMetadata.NuoVariables,nuoDagDetails:NuoDagDetails): Unit = {

    paramVariables.variables.foreach { variableInfo =>
      variableInfo.Scope = findVariableInitialScope(scopeNodeId,nuoDagDetails)

      if (nuoDagDetails.runDetails.variables.contains((variableInfo.Scope, variableInfo.Name))) {
        nuoDagDetails.runDetails.variables.remove((variableInfo.Scope, variableInfo.Name))
      }
      if (variableInfo.Type.nonEmpty && variableInfo.Value.nonEmpty) {

        variableInfo.CastValue = NuoDataTypeHandler.getExpressionCastFunction(variableInfo.Value, variableInfo.Type)
      } else if (variableInfo.Value.nonEmpty) {

        variableInfo.CastValue = NuoDataTypeHandler.getExpressionCastFunction(variableInfo.Value)
      }
      nuoDagDetails.runDetails.variables.put((variableInfo.Scope, variableInfo.Name), variableInfo)

    }
    metadata.VariableMetadata.updateVariables(nuoDagDetails)
  }

  //  def runVariableMergeQuery(nodeId:String,
  //                            varQuery: String): Unit = {
  //
  //    val columns = new mutable.ListBuffer[mutable.HashMap[String, String]]()
  //    columns += mutable.HashMap[String, String](
  //      ColumnName -> "name",
  //      ColumnSource -> s"name")
  //
  //    columns += mutable.HashMap[String, String](
  //      ColumnName -> "scope",
  //      ColumnSource -> s"scope")
  //
  //    columns += mutable.HashMap[String, String](
  //      ColumnName -> "value",
  //      ColumnSource -> s"value")
  //
  //    columns += mutable.HashMap[String, String](
  //      ColumnName -> "type",
  //      ColumnSource -> s"type")
  //
  //    /*
  //    * Since we are using MergeOverwrite, we don't need to check if variable exist or not we simply merge the variable name,value and scope to table
  //    * MergeOverwrite requires the input action name to merge the data into the target, hence we mock up the input with name "nuoTempSourceVarAssign"
  //    * */
  //
  //    /*
  //    * We do not allow more than one variable with same name and different data type within same scope.
  //    * To allow it we need to add type column in merge condition
  //    * */
  //    registerActionQuery(varQuery, NuoStorageModifier.variableViewName)
  //
  //    evalMergeIntoTable(nodeId = nodeId,
  //      actionName = NuoStorageModifier.variableTableName,
  //      targetDatasetName = NuoStorageModifier.userInternalDatasetName,
  //      targetTableName = NuoStorageModifier.variableTableName,
  //      sourceTableName = NuoStorageModifier.variableViewName,
  //      keyColumnsForTarget = List("name", "scope"),
  //      customMergeCondition = "",
  //      updateCondition = "",
  //      deleteCondition = "",
  //      isInsert = true,
  //      isUpdate = true,
  //      isDelete = false,
  //      isSynch = true,
  //      dependentNodes = Vector(NuoStorageModifier.variableViewName),
  //      columns = columns)
  //
  //  }

  /*
  * This function recursively find the expression for input column or value if variable is used
  * Returns
  *   Return code to confirm the identified column is variable or source column
  *   Evaluated expression
  *   Variable Type if input was variable
  *
  * */
  val localQualifiedColumn = "Qualified_Column"
  val localResolvedValue = "Resolved_Value"

  def findColumnExpressionSource(qualifiedColumnName: String,
                                 includeSourceName: Boolean,
                                 nodeId: String,
                                 nuoDagDetails: NuoDagDetails): (String, String) = {

    val sourceComponent = qualifiedColumnName.substring(1, qualifiedColumnName.indexOf("`", 1))
    val sourceColumn = qualifiedColumnName.substring(qualifiedColumnName.indexOf(sourceComponent) + sourceComponent.length + 3, qualifiedColumnName.length - 1)

    if (!sourceComponent.equalsIgnoreCase(_variable)) {

      (localQualifiedColumn, if (includeSourceName) {
        s"`$sourceComponent`.`$sourceColumn`"
      } else {
        s"`$sourceColumn`"
      })
    } else {

//      /*
//      * If Node Type of given node is Variable Assignment then we should start searching for variable from parent scope.
//      * Because Variable Assignment component does not have its own scope.
//      * */
//      val paramNodeId = if (NuoTenantDetails.nodes(nodeId)(NodeType).asInstanceOf[String].equalsIgnoreCase(ActionType.LoopAndVariable.CreateOrUpdateVariable)
//        || NuoTenantDetails.nodes(nodeId)(NodeType).asInstanceOf[String].equalsIgnoreCase(ActionType.LoopAndVariable.While)
//        || NuoTenantDetails.nodes(nodeId)(NodeType).asInstanceOf[String].equalsIgnoreCase(ActionType.LoopAndVariable.DoUntil)
//        || NuoTenantDetails.nodes(nodeId)(NodeType).asInstanceOf[String].equalsIgnoreCase(ActionType.LoopAndVariable.Repeat)
//      ) {
//        NuoTenantDetails.hierarchy(nodeId)._1
//      } else {
//        nodeId
//      }
      val variableDetails = findVariable(sourceColumn, nodeId,nuoDagDetails)

      if (variableDetails.isDefined) {
        (localResolvedValue, variableDetails.get.CastValue)
      } else {
        NuoLogger.printInfo(s"nodeName=${getNodeName(nodeId,nuoDagDetails)}",nuoDagDetails)
        throw new Exception(s"Variable: [$sourceColumn] does not exist in any scope for component[${getNodeName(nodeId,nuoDagDetails)}]." +
          " How am I supposed to evaluate the expression?!")
      }
    }
  }

  //
  //  def loadVariables(,nuoDagDetails:NuoDagDetails): Unit = {
  //    if (NuoTenantDetails.trackedVariables.nonEmpty) {
  //      val varList = NuoTenantDetails.trackedVariables.map(name => s"'$name'").mkString(",")
  //      val query = s"SELECT SCOPE,NAME,TYPE,VALUE FROM `${NuoStorageModifier.userInternalDatasetName}`.`${NuoStorageModifier.variableTableName}`" +
  //        s" WHERE NAME IN ($varList)"
  //      val results = NuoBQClient.executeDMLSelect(query, 100)
  //      if(results.isDefined){
  //
  //        NuoTenantDetails.variablesInScope ++= results.get.groupBy(_.head).map(variablesInScope =>
  //          variablesInScope._1 -> variablesInScope._2.map(row => metadata.VariableMetadata.Variable(Scope = row(0),Name = row(1), Type = row(2), Value = row(3)))
  //        )
  //      }
  //    }
  //  }
  //
  //  def searchVariables(): Unit = {
  //
  //    NuoTenantDetails.nodes.filter(ele => NuoTenantDetails.nodesToBeExecuted.contains(ele._1)).foreach { node =>
  //
  //      val allColSource = if(node._2.contains(ColumnMapping)) node._2(ColumnMapping)
  //        .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]]
  //        .map(colMap=>colMap(ColumnSource))
  //      else List[String]()
  //
  //      val attrMap = node._2.filter(ele => !ele._1.equalsIgnoreCase(NodeName)
  //        && !ele._1.equalsIgnoreCase(NodeType)
  //        && !ele._1.equalsIgnoreCase(ColumnMapping)
  //        && !ele._1.equalsIgnoreCase(NuoModifier.ActionPropertyName.Common.ElementTypeList))
  //        .asInstanceOf[mutable.HashMap[String, String]]
  //
  //
  //      (attrMap.values ++ allColSource)
  //        .filter { input =>
  //          val dataType = NuoDataTypeHandler.parseBQDataType(input)
  //          dataType.isEmpty ||
  //            dataType.get.equalsIgnoreCase(CanvasDataType.Date) ||
  //            dataType.get.equalsIgnoreCase(CanvasDataType.Time) ||
  //            dataType.get.equalsIgnoreCase(CanvasDataType.Timestamp)
  //        }.foreach { expressionInput =>
  //        val expBuffer = new StringBuilder()
  //        var colBuffer = ""
  //        var colDetected = false
  //
  //        for (char <- expressionInput
  //          .replace("\\\\", _backslashAsData)
  //          .replace("\\$", _dollarAsData)
  //          .toCharArray) {
  //
  //          if (colDetected && char.equals('$')) {
  //            colDetected = false
  //            if (colBuffer.isEmpty) {
  //              throw new Exception("I found two consecutive dollar signs with no escape characters, Do you really mean the qualified column name is empty?!")
  //            } else {
  //              val sourceComponent = colBuffer.substring(1, colBuffer.indexOf("`", 1))
  //              val sourceColumn = colBuffer.substring(colBuffer.indexOf(sourceComponent) + sourceComponent.length + 3, colBuffer.length - 1)
  //
  //              if (sourceComponent.equalsIgnoreCase(_variable) && !NuoTenantDetails.trackedVariables .contains(sourceColumn)) {
  //                NuoTenantDetails.trackedVariables += sourceColumn
  //              }
  //              colBuffer = ""
  //            }
  //          } else if (colDetected && !char.equals('$')) {
  //            colBuffer += char
  //          } else if (char.equals('$') && !colDetected) {
  //            colDetected = true
  //          } else {
  //            expBuffer += char
  //          }
  //        }
  //      }
  //    }
  //  }

  /*
  * This function take the expression from Compute as input and resolve all the columns of inbound compute component
  * and return expression with source column of node that is other than compute
  * Parameters:
  *   expression=> expression defined for the column
  *   targetDataType=> data type of target column
  *Returns:
  *   Evaluated expression
  *   If source table is used
  *   Data type of
  * */
  def processExpression(expressionInput: String,
                        includeSourceName: Boolean,
                        nodeId: String,nuoDagDetails:NuoDagDetails): (String, Boolean) = {

    val expBuffer = new StringBuilder()
    var colBuffer = ""
    var isSourceTableUsed = false
    var colDetected = false

    /*
    * *********************************************************************************
    * *********************************************************************************
    * *********************************************************************************
    * VERY IMPORTANT: Spark SQL query are not able to handle double quote as data in string literals as of version 1.2.1
    * So it is extremely important to replace the _doubleQuoteAsData identifier from data with double quote
    * in all the component that export data to external storage
    *
    * Update: Code upgraded to 1.5.0 and above limitation has been overcome. 12MAR2016
    * *********************************************************************************
    * *********************************************************************************
    * */
    var castFunctionUsed = false
    for (char <- expressionInput
      .replace("\\\\", _backslashAsData)
      .replace("\\$", _dollarAsData)
      .toCharArray) {

      if (colDetected && char.equals('$')) {
        colDetected = false
        if (colBuffer.isEmpty) {
          throw new Exception("I found two consecutive dollar signs with no escape characters, Do you really mean qualified column name is empty?!")
        } else {

          val expression = findColumnExpressionSource(qualifiedColumnName = colBuffer,
            includeSourceName = includeSourceName,
            nodeId = nodeId,nuoDagDetails)
          castFunctionUsed = true

          val qualifiedColumnName: String = expression._2

          if (!expression._1.equals(localResolvedValue)) {
            isSourceTableUsed = true
          }

          expBuffer.append(qualifiedColumnName)
          colBuffer = ""
        }
      } else if (colDetected && !char.equals('$')) {
        colBuffer += char
      } else if (char.equals('$') && !colDetected) {
        colDetected = true

      } else {
        expBuffer += char
      }
    }
    val evaluatedExpression = expBuffer.toString()
      .replace(_backslashAsData, "\\\\")
      .replace(_dollarAsData, "$")


    val updatedExpression = if (!castFunctionUsed) {
      NuoDataTypeHandler.getExpressionCastFunction(evaluatedExpression)
    } else {
      evaluatedExpression
    }
    (updatedExpression, isSourceTableUsed)
  }

}
*/
