//
//package dag
//
//import java.io.ByteArrayInputStream
//import java.text.SimpleDateFormat
//import java.util.concurrent.{ExecutorService, Executors}
//
//import canvas.NuoModifier.ActionAttrName._
//import canvas.NuoModifier.ActionType
//import canvas.NuoModifier.Constant.{_businessCanvas, _nullValue}
//import client.NuoS3Client
//import client.NuoS3Client.readFromS3
//import com.amazonaws.auth.{BasicAWSCredentials, DefaultAWSCredentialsProviderChain}
//import com.amazonaws.services.lambda.runtime.Context
//import com.amazonaws.services.machinelearning.AmazonMachineLearningClient
//import com.amazonaws.services.s3.AmazonS3Client
//import com.amazonaws.services.s3.event.S3EventNotification
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
//import com.google.api.client.http.javanet.NetHttpTransport
//import com.google.api.client.json.jackson2.JacksonFactory
//import com.google.api.services.bigquery.{Bigquery, BigqueryScopes}
//import com.google.api.services.storage.{Storage, StorageScopes}
//import com.google.api.services.storagetransfer.v1.{Storagetransfer, StoragetransferScopes}
//import logging.NuoLogger.{logError, printException, printInfo}
//import metadata.VariableMetadata.NuoVariable
//import metadata.{AccountMetadata, CanvasMetadata, VariableMetadata}
//import net.liftweb.json.DefaultFormats
//
//import scala.collection.JavaConverters._
//import scala.collection.mutable
//import scala.collection.mutable.ArrayBuffer
//
//object NuoDag {
//
//  case class NuoDagDetails(paramS3EventRef: S3EventNotification, paramLambdaContext: Context) {
//
//    /*Each element in Nodes map represent a node in graph.
//  * Key : Node id assigned to each node in graph
//  * Value : HashMap contains Node attributes. It contains the _attribute_name as key
//  *         and _attribute_value as value in map.
//  */
//    val nodes = mutable.HashMap[String, mutable.HashMap[String, Any]](
//      "0" -> mutable.HashMap[String, Any](
//        NodeName -> _businessCanvas,
//        NodeType -> _businessCanvas
//      )
//    )
//    /*
//  * Mandatory and Optional Outbound Nodes
//  * Each element of inEdges represent the neighbor nodes connected to
//  * particular node in incoming direction.
//  *   Key: Node id of node (it must exist in DAG.nodes)
//  *   value: List of Node id of nodes connected in incoming direction (They must exist in DAG.nodes)
//  */
//    val inboundMandatoryNodes = mutable.HashMap[String, ArrayBuffer[String]]()
//    val inboundOptionalNodes = mutable.HashMap[String, ArrayBuffer[String]]()
//    val inboundActualMandatoryNodes = mutable.HashMap[String, ArrayBuffer[String]]()
//    @deprecated
//    val inboundActualOptionalNodes = mutable.HashMap[String, ArrayBuffer[String]]()
//    val inboundActualNodes = mutable.HashMap[String, ArrayBuffer[String]]()
//    /*
//  * Mandatory and Optional Outbound Nodes
//  * Each element of inEdges represent the neighbor nodes connected to
//  * particular node in outgoing direction.
//  *   Key: Node id of node (it must exist in DAG.nodes)
//  *   value: List of Node id of nodes connected in outgoing direction (They must exist in DAG.nodes)
//  */
//    val outboundMandatoryNodes = mutable.HashMap[String, ArrayBuffer[String]]()
//    val outboundOptionalNodes = mutable.HashMap[String, ArrayBuffer[String]]()
//    val outboundNodes = mutable.HashMap[String, ArrayBuffer[String]]()
//    /*
//  * Hierarchy of all process in Business Canvas.Root is always 0L (Business Canvas)
//  * Key: Node id of particular node
//  * Value: First element in Tuple represents parent Node id
//  *        Second element in Tuple represents list of child Node id
//  *
//  * Note: All Node id must exist in DAG.nodes
//  *  */
//    val hierarchy = mutable.HashMap[String, (String, Vector[String])]("0" -> ("-1", Vector[String]()))
//    val nodeNameToIdMap = mutable.HashMap[String, String](_businessCanvas -> "0")
//    val runPlanDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS")
//    val subQueryForNodeId = mutable.HashMap[String, String]()
//    var tenantDetails: NuoTenantDetails = new NuoTenantDetails(paramS3EventRef, paramLambdaContext, this)
//    var runDetails: NuoRunDetails = new NuoRunDetails(paramS3EventRef, this)
//
//    /*
//  * This function initialize the above defined data structures from the json metadata
//  * */
//    def loadNuoDag(): Unit = {
//
//      try {
//
//        printInfo("I am going to load NuoDag", this)
//        /*
//      * 20JUN2017:Pulkit:
//      * nuo-backend-controllore creates the storage components at the time of user registration
//      * */
//        VariableMetadata.loadVariables(this)
//
//
//        /*
//      Following statement is important to apply default formats.
//       */
//        implicit val formats = DefaultFormats
//        val canvasMetadata = CanvasMetadata.getCanvasMetadata(null)
//
//        processNodeInformation(canvasMetadata)
//        for {node <- canvasMetadata.Nodes} {
//          if (node.NodeType.equalsIgnoreCase(ActionType.Process)) {
//            populateProcessConnections(node, canvasMetadata)
//          } else {
//            //          printInfo("populating action connection")
//            populateActionConnections(node, canvasMetadata)
//          }
//        }
//        inboundActualNodes.keys.foreach { nodeId =>
//
//          inboundActualNodes(nodeId) ++= (inboundActualMandatoryNodes(nodeId) ++ inboundActualOptionalNodes(nodeId))
//        }
//        outboundNodes.keys.foreach { nodeId =>
//          outboundNodes(nodeId) ++= (outboundMandatoryNodes(nodeId) ++ outboundOptionalNodes(nodeId))
//        }
//
//        //      printInfo(s"inbound actual nodes ==> ${inboundActualNodes.map(ele => (ele._1, ele._2.map(x => getNodeName(x))))}")
//        //      printInfo(s"outbound nodes ==> ${outboundNodes.map(ele => (ele._1, ele._2.map(x => getNodeName(x))))}")
//
//        /*
//      * After populating all inbound and outbound connections, remove duplicate entries.
//      * */
//        //      printInfo("removing duplicate connections")
//        removeDuplicateConnections()
//      } catch {
//        case e: Exception => printException(e, this)
//          logError(s"Canvas Metadata is invalid. I cannot execute process = ${runDetails.runPlanName}.", this)
//      }
//    }
//
//
//    /*
//  * Following function removes duplicate in inbound and outbound nodes map
//  * */
//    def removeDuplicateConnections(): Unit = {
//
//      var distinctMap = inboundMandatoryNodes.map(ele => (ele._1, ele._2.distinct))
//      inboundMandatoryNodes ++= distinctMap
//
//      distinctMap = inboundOptionalNodes.map(ele => (ele._1, ele._2.distinct))
//      inboundOptionalNodes ++= distinctMap
//
//      distinctMap = inboundActualMandatoryNodes.map(ele => (ele._1, ele._2.distinct))
//      inboundActualMandatoryNodes ++= distinctMap
//
//      distinctMap = inboundActualOptionalNodes.map(ele => (ele._1, ele._2.distinct))
//      inboundActualOptionalNodes ++= distinctMap
//
//      distinctMap = outboundMandatoryNodes.map(ele => (ele._1, ele._2.distinct))
//      outboundMandatoryNodes ++= distinctMap
//
//      distinctMap = outboundOptionalNodes.map(ele => (ele._1, ele._2.distinct))
//      outboundOptionalNodes ++= distinctMap
//
//    }
//
//
//    /*
//  *
//  * */
//    def processNodeInformation(canvasMetadata: CanvasMetadata.MetadataRoot): Unit = {
//
//      //      printInfo(s"canvasMetadata.Nodes.size = ${canvasMetadata.Nodes.size}")
//
//      for (node <- canvasMetadata.Nodes) {
//        val nodeId = node.NodeId
//        /*
//      * Add node name and node type
//      * */
//        nodes.put(nodeId, mutable.HashMap(
//          NodeName -> node.NodeName,
//          NodeType -> node.NodeType))
//
//        nodeNameToIdMap.put(node.NodeName, nodeId)
//        populateActionAttributes(node)
//        populateActionColumns(node)
//        initOutbounds(nodeId)
//        initInbounds(nodeId)
//        populateHierarchy(node)
//      }
//
//    }
//
//    def initOutbounds(nodeId: String): Unit = {
//
//      outboundNodes.put(nodeId, ArrayBuffer[String]())
//      outboundMandatoryNodes.put(nodeId, ArrayBuffer[String]())
//      outboundOptionalNodes.put(nodeId, ArrayBuffer[String]())
//    }
//
//    def initInbounds(nodeId: String): Unit = {
//
//      inboundMandatoryNodes.put(nodeId, ArrayBuffer[String]())
//      inboundOptionalNodes.put(nodeId, ArrayBuffer[String]())
//      inboundActualNodes.put(nodeId, ArrayBuffer[String]())
//      inboundActualMandatoryNodes.put(nodeId, ArrayBuffer[String]())
//      inboundActualOptionalNodes.put(nodeId, ArrayBuffer[String]())
//    }
//
//    def populateActionAttributes(node: CanvasMetadata.Node): Unit = {
//
//      val nodeId = node.NodeId
//      for (attr <- node.FunctionalInformation.Attributes) {
//        if (nodes.contains(nodeId)) {
//          if (!attr.Name.equalsIgnoreCase("Element_Type_List")) {
//
//            nodes(nodeId).put(attr.Name, attr.Value.get)
//          }
//        } else {
//          throw new Exception("I found a node without Node_name and Node_type attributes, How am I supposed to identify it?!:" + node.NodeName)
//        }
//      }
//    }
//
//    def populateActionColumns(node: CanvasMetadata.Node): Unit = {
//
//      val nodeId = node.NodeId
//      //                printInfo("Attributes----" + node.Functional_Information.ColumnMapping)
//      for (colMapping <- node.FunctionalInformation.ColumnMapping) {
//        if (nodes.contains(nodeId)) {
//          if (nodes(nodeId).contains(ColumnMapping)) {
//
//            nodes(nodeId)(ColumnMapping)
//              .asInstanceOf[mutable.ListBuffer[mutable.HashMap[String, String]]] += mutable.HashMap(
//              ColumnName -> colMapping.Target,
//              DataType -> colMapping.DataType.getOrElse(""),
//              Precision -> colMapping.Precision.getOrElse(""),
//              Scale -> colMapping.Scale.getOrElse(""),
//              ColumnSource -> colMapping.Source.getOrElse(""),
//              IsMandatory -> colMapping.IsMandatory.get)
//          } else {
//            val tempList = new mutable.ListBuffer[mutable.HashMap[String, String]]()
//            tempList += mutable.HashMap[String, String](
//              ColumnName -> colMapping.Target,
//              DataType -> colMapping.DataType.getOrElse(""),
//              Precision -> colMapping.Precision.getOrElse(""),
//              Scale -> colMapping.Scale.getOrElse(""),
//              ColumnSource -> colMapping.Source.getOrElse(""),
//              IsMandatory -> colMapping.IsMandatory.get)
//            nodes(nodeId).put(ColumnMapping, tempList)
//          }
//        } else
//          throw new Exception("I found a node without Node_name and Node_type attributes, How am I supposed to identify it?!:" + node.NodeName)
//      }
//    }
//
//    /*Collect hierarchy from AST*/
//    def populateHierarchy(node: CanvasMetadata.Node): Unit = {
//
//      hierarchy.put(node.NodeId,
//        (node.IntegrityInformation.ParentId,
//          Vector[String]() ++ node.IntegrityInformation.ChildrenIds.withFilter(!_.equals(_nullValue)).
//            map(_.toString)))
//
//    }
//
//    /*
//  * Following function populates
//  * */
//    def populateActionConnections(node: CanvasMetadata.Node,
//                                  metadataAstObject: CanvasMetadata.MetadataRoot): Unit = {
//      /*Collect inbound nodes from AST*/
//
//      val nodeId = node.NodeId
//
//      inboundMandatoryNodes(nodeId) ++= node.IntegrityInformation.InputConnections
//        .withFilter(_.IsMandatory.toBoolean == true)
//        .map { input =>
//          val tempPort = input.SourcePort
//
//          val tempSourceNodeId = tempPort.substring(0, tempPort.indexOf('.'))
//          val tempSourcePortId = tempPort.substring(tempPort.indexOf('.') + 1)
//          val sourceNodeId = findLowestChild(tempSourceNodeId, tempSourcePortId, metadataAstObject)
//          sourceNodeId
//        }
//
//      inboundActualMandatoryNodes(nodeId) ++= node.IntegrityInformation.InputConnections
//        .withFilter(_.IsMandatory.toBoolean == true)
//        .map { input =>
//          val tempPort = input.SourcePort
//
//          val tempSourceNodeId = tempPort.substring(0, tempPort.indexOf('.'))
//          val tempSourcePortId = tempPort.substring(tempPort.indexOf('.') + 1)
//          val sourceNodeId = findLowestChild(tempSourceNodeId, tempSourcePortId, metadataAstObject)
//          sourceNodeId
//        }
//
//      inboundOptionalNodes(nodeId) ++= node.IntegrityInformation.InputConnections
//        .withFilter(_.IsMandatory.toBoolean == false)
//        .map { input =>
//          val tempPort = input.SourcePort
//
//          val tempSourceNodeId = tempPort.substring(0, tempPort.indexOf('.'))
//          val tempSourcePortId = tempPort.substring(tempPort.indexOf('.') + 1)
//          val sourceNodeId = findLowestChild(tempSourceNodeId, tempSourcePortId, metadataAstObject)
//          sourceNodeId
//        }
//
//      inboundActualOptionalNodes(nodeId) ++= node.IntegrityInformation.InputConnections
//        .withFilter(_.IsMandatory.toBoolean == false)
//        .map { input =>
//          val tempPort = input.SourcePort
//
//          val tempSourceNodeId = tempPort.substring(0, tempPort.indexOf('.'))
//          val tempSourcePortId = tempPort.substring(tempPort.indexOf('.') + 1)
//          val sourceNodeId = findLowestChild(tempSourceNodeId, tempSourcePortId, metadataAstObject)
//          sourceNodeId
//        }
//
//      /*Populate outbound nodes from AST*/
//
//      /*
//    * According to new logic in updated metadata, All nodes contains only input connections.
//    * So we need to update outbound connection of source node of input connection.
//    * */
//      node.IntegrityInformation.InputConnections
//        .foreach { input =>
//          val tempPort = input.SourcePort
//
//          val tempSourceNodeId = tempPort.substring(0, tempPort.indexOf('.'))
//          val tempSourcePortId = tempPort.substring(tempPort.indexOf('.') + 1)
//          val sourceNodeId = findLowestChild(tempSourceNodeId, tempSourcePortId, metadataAstObject)
//
//          if (input.IsMandatory.toBoolean) {
//
//            //          printInfo(s"outbound nodes before :${outboundMandatoryNodes.mkString}")
//            outboundMandatoryNodes(sourceNodeId) += nodeId
//            //          printInfo(s"outbound nodes after :${outboundMandatoryNodes.mkString}")
//          } else {
//            outboundOptionalNodes(sourceNodeId) += nodeId
//          }
//        }
//
//    }
//
//    /*
//* At execution level all actions are connected to each other directly, Process hierarchy is for front-end simplification only.
//* Following function translates the process connections to lowest level actions.
//* */
//    def populateProcessConnections(node: CanvasMetadata.Node,
//                                   metadataAstObject: CanvasMetadata.MetadataRoot): Unit = {
//
//      val processNodeId = node.NodeId
//
//      node.IntegrityInformation.InputConnections
//        .foreach { input =>
//
//          //        printInfo(s"Processing input connection of $nodeId")
//
//          val tempPort = input.SourcePort
//
//          val tempSourceNodeId = tempPort.substring(0, tempPort.indexOf('.'))
//          val tempSourcePortId = tempPort.substring(tempPort.indexOf('.') + 1)
//          val sourceNodeId = findLowestChild(tempSourceNodeId, tempSourcePortId, metadataAstObject)
//
//          val targetPortId = input.TargetPortId
//
//          val childConnections = node.IntegrityInformation.ChildConnections
//            .withFilter(_.ParentPortId.contentEquals(targetPortId))
//
//          if (childConnections.map(_.ChildPort).nonEmpty) {
//            childConnections.foreach { childCon =>
//
//
//              val tempPort = childCon.ChildPort
//              val childNodeId = tempPort.substring(0, tempPort.indexOf('.'))
//              val childPortId = tempPort.substring(tempPort.indexOf('.') + 1)
//
//              val targetNodeId = findLowestChild(childNodeId, childPortId, metadataAstObject)
//
//              if (input.IsMandatory.toBoolean) {
//
//                outboundMandatoryNodes(sourceNodeId) += targetNodeId
//                inboundMandatoryNodes(targetNodeId) += sourceNodeId
//                inboundActualMandatoryNodes(targetNodeId) += sourceNodeId
//              } else {
//
//                outboundOptionalNodes(sourceNodeId) += targetNodeId
//                inboundOptionalNodes(targetNodeId) += sourceNodeId
//                inboundActualOptionalNodes(targetNodeId) += sourceNodeId
//              }
//            }
//          } else if (input.IsMandatory.toBoolean) {
//
//            outboundMandatoryNodes(sourceNodeId) += processNodeId
//            inboundMandatoryNodes(processNodeId) += sourceNodeId
//          } else {
//
//            outboundOptionalNodes(sourceNodeId) += processNodeId
//            inboundOptionalNodes(processNodeId) += sourceNodeId
//          }
//        }
//    }
//
//
//    /*
//  * Following function finds the lowest level action for given process connection
//  * */
//    def findLowestChild(nodeId: String,
//                        portId: String,
//                        canvasMetadata: CanvasMetadata.MetadataRoot): String = {
//
//
//      val node = canvasMetadata.Nodes
//        .find(_.NodeId.equalsIgnoreCase(nodeId)).get
//
//      if (node.NodeType.equalsIgnoreCase(ActionType.Process)) {
//
//        val childConnection = node
//          .IntegrityInformation.ChildConnections.find(_.ParentPortId.equalsIgnoreCase(portId))
//
//        if (childConnection.isDefined) {
//          val childPort = childConnection.get.ChildPort
//          val childNodeId = childPort.substring(0, childPort.indexOf('.'))
//
//          val childNode = canvasMetadata.Nodes
//            .find(_.NodeId.equalsIgnoreCase(childNodeId)).get
//
//          if (childNode.NodeType.equalsIgnoreCase(ActionType.Process)) {
//
//            val childPortId = childPort.substring(childPort.indexOf('.') + 1)
//            findLowestChild(childNodeId, childPortId, canvasMetadata)
//          } else {
//            childNode.NodeId
//          }
//        } else {
//          nodeId
//        }
//      } else {
//        nodeId
//      }
//    }
//
//  }
//
//
//  class NuoRunDetails(paramS3EventRef: S3EventNotification, nuoDagDetails: NuoDagDetails) {
//
//    implicit val formats = DefaultFormats
//
//    nuoDagDetails.runDetails = this
//
//    val incomingBucketName: String = "incoming-backend-nuocanvas-com"
//    val runMetadataFileName = if (paramS3EventRef != null) {
//      paramS3EventRef.getRecords.asScala.head.getS3.getObject.getKey
//    } else {
//      "15e89b2f792/1514324545705.json"
//    }
//
//    val executionStatusOf = mutable.HashMap[String, Double]()
//    val executionStatusRefParamOf = mutable.HashMap[String, Array[String]]()
//    val nodesToBeExecuted = mutable.ArrayBuffer[String]()
//    val variables = mutable.HashMap[(String, String), NuoVariable]()
//    val inProgressActions = mutable.ArrayBuffer[String]()
//    val startTime =
//      if (paramS3EventRef != null) {
//        paramS3EventRef.getRecords.asScala.head.getEventTime.toDate.getTime
//      } else {
//        System.currentTimeMillis()
//      }
//    val endTime = startTime + 250 * 1000
//    val runMetadata = metadata.RunMetadata.getRunMetadata(readFromS3(incomingBucketName, runMetadataFileName, nuoDagDetails), nuoDagDetails)
//    val requestId = runMetadata.RequestId
//    val runPlanId = runMetadata.RunPlanId.get
//    val runPlanName = runMetadata.RunPlanName.get
//    val runPlanType = runMetadata.RunPlanType.get
//    val isActive = runMetadata.IsActive.get.toBoolean
//    val isExecutionLenient = runMetadata.IsExecutionLenient.get.toBoolean
//    val logLevel = runMetadata.LogLevel.get
//    val isSampleExecution = runMetadata.IsSampleExecution.get.toBoolean
//    val sampleSize = runMetadata.SampleSize.get.toInt
//    val rerunStrategy = runMetadata.RerunStrategy.get
//    val numOfRetries = runMetadata.NumOfRetries.get.toInt
//    val startPoints = mutable.ArrayBuffer[String]() ++ runMetadata.StartPoints
//    val endPoints = mutable.ArrayBuffer[String]() ++ runMetadata.EndPoints
//    val allowedPaths = mutable.ArrayBuffer[String]() ++ runMetadata.AllowedPaths
//    val blockedPaths = mutable.ArrayBuffer[String]() ++ runMetadata.BlockedPaths
//    //
//    var executorService: ExecutorService = Executors.newFixedThreadPool(1000)
//    var inProgressBranchCount = 0
//    var recursiveCallRequired: Boolean = _
//    var isExecutionComplete: Boolean = _
//    var errorEncountered: Boolean = _
//
//
//  }
//
//  class NuoTenantDetails(paramS3EventRef: S3EventNotification, paramLambdaContext: Context, nuoDagDetails: NuoDagDetails) {
//
//    nuoDagDetails.tenantDetails = this
//
//    val ServerDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
//    val lambdaContextRef: Context = paramLambdaContext
//    val s3EventRef: S3EventNotification = paramS3EventRef
//    //    val httpClient: CloseableHttpClient = _
//    val inProgressDir: String = s"Execution/InProgress/"
//    val completedDir: String = s"Execution/Completed/"
//    val failedDir: String = s"Execution/Failed/"
//    val variableFile: String = s"Data/Variables.json"
//    val nuoStorageMetadataFileName: String = "Metadata/NuoStorage/NuoStorage_Metadata.json"
//    val nuoRelationshipFileName: String = "Metadata/NuoStorage/NuoRelationship.txt"
//    val canvasMetadataFileName = s"Metadata/Canvas/Canvas_Metadata.json"
//    val accountDetailsFileName = s"Preference/Account_Details.json"
//    //
//    val MasterBucketName: String = "master-backend-nuocanvas-com"
//    val MlaasDataBucketName: String = "mlaas-data-backend-nuocanvas-com"
//    val amazonS3Client: AmazonS3Client = if (paramS3EventRef != null) {
//      new AmazonS3Client(new DefaultAWSCredentialsProviderChain())
//    } else {
//
//      NuoS3Client.createCustomAwsS3Client(accessKey, secretKey)
//    }
//    val amazonMachineLearningClient: AmazonMachineLearningClient = if (paramS3EventRef != null) {
//      new AmazonMachineLearningClient(new DefaultAWSCredentialsProviderChain())
//    } else {
//
//      new AmazonMachineLearningClient(new BasicAWSCredentials(accessKey, secretKey))
//    }
//    val incomingBucketName: String = "incoming-backend-nuocanvas-com"
//
//    val runMetadataFileName = if (paramS3EventRef != null) {
//      paramS3EventRef.getRecords.asScala.head.getS3.getObject.getKey
//    } else {
//      "15e89b2f792/1514324545705.json"
//    }
//    val tenantId = runMetadataFileName.split("\\/")(0)
//
//    val tenantBucketName = s"${tenantId.toLowerCase}-backend-nuocanvas-com"
//    val tenantInternalDirPrefix: String = s"Internal/"
//    val tableExportDirPrefix: String = s"${tenantInternalDirPrefix}TableExport/"
//
//    nuoDagDetails.tenantDetails = this
//
//    val tenantAccountDetails = AccountMetadata.getAccountDetails(null)
//    val bqProjectId = tenantAccountDetails.GcpAccountDetails.get.ProjectId.get
//    val gcpCredentialsFile = s"Credentials/GcpActive/$bqProjectId"
//    val gcpRegion = tenantAccountDetails.GcpAccountDetails.get.Location.getOrElse("us-central1")
//    val transport = new NetHttpTransport
//    val jsonFactory = new JacksonFactory
//    var credential = {
//      GoogleCredential.fromStream(new ByteArrayInputStream(NuoS3Client.readAndDecryptS3File(null, MasterBucketName, gcpCredentialsFile, nuoDagDetails).getBytes))
//    }
//
//    if (credential.createScopedRequired) credential = credential.createScoped(BigqueryScopes.all)
//    val bigqueryClient = new Bigquery.Builder(transport, jsonFactory, credential).setApplicationName(tenantId).build
//
//    if (credential.createScopedRequired) credential = credential.createScoped(StoragetransferScopes.all)
//    val transferClient = new Storagetransfer.Builder(transport, jsonFactory, credential).setApplicationName(tenantId).build
//
//    if (credential.createScopedRequired) credential = credential.createScoped(StorageScopes.all)
//    val storageClient = new Storage.Builder(transport, jsonFactory, credential).setApplicationName(tenantId).build
//    //
//
//    def getTableExportGcsUriPrefix(DatasetName: String, EntityName: String, prefix: String): String = {
//      if (prefix.isEmpty || prefix.trim.isEmpty) {
//        s"gs://$tenantBucketName/$tableExportDirPrefix$DatasetName/$EntityName/*"
//      } else {
//        s"gs://$tenantBucketName/$prefix/*"
//
//      }
//    }
//  }
//
//}*/
