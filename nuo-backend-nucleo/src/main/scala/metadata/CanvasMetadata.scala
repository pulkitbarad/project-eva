package metadata

import client.NuoS3Client
import logging.NuoLogger._
import net.liftweb.json._
import nlp.grammar.NuoEvaEnglishListener

/**
  * Copyright 2015 Nuocanvas Inc.
  *
  *
  * Created by Pulkit on 9/29/15.
  *
  * Content of this file is proprietry and confidential.
  * It should not be reused or disclosed without prior consent
  * of distributor
  **/
object CanvasMetadata {


  case class MetadataRoot(Nodes: List[Node],
                          Graphics: Option[JValue])


  case class Node(NodeName: String,
                  NodeType: String,
                  NodeId: String,
                  FunctionalInformation: FunctionalInfo,
                  IntegrityInformation: IntegrityInfo,
                  GraphicalInformation: Option[JValue])

  case class FunctionalInfo(Attributes: List[Attribute],
                            ColumnMapping: List[ColumnMapping])

  case class Attribute(Name: String,
                       Value: Option[String])


  case class ColumnMapping(Target: String,
                           DataType: Option[String],
                           Precision: Option[String],
                           Scale: Option[String],
                           IsMandatory: Option[String],
                           Source: Option[String])

  case class IntegrityInfo(IsReference: String,
                           InputPorts: List[Port],
                           OutputPorts: List[Port],
                           ParentId: String,
                           ChildrenIds: List[String],
                           ChildConnections: List[ChildConnection],
                           InputConnections: List[InputConnection])

  case class Port(Position: String,
                  PortName: String,
                  PortId: String)

  case class ChildConnection(ChildPort: String,
                             ParentPortId: String)

  case class InputConnection(SourcePort: String,
                             TargetPortId: String,
                             IsMandatory: String)

  /*
  * This function parses the Tenant Preference metadata file and returns the case class structure as AST
  * */
  def getCanvasMetadata(): MetadataRoot = {

    /*
    Following statement is important to apply default formats.
    */

    try {
      implicit val formats = DefaultFormats


      val jsonContent = NuoS3Client.readFromS3(NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName,
        NuoEvaEnglishListener.nuoTenantDetails.CanvasMetadataFileName)
      val jsonAST = parse(jsonContent)

      jsonAST.extract[MetadataRoot]

    } catch {
      case e: Exception => printException(e)
        null
    }
  }
}

