/*
package metadata

import client.NuoS3Client
import dag.NuoTenantDetails
import logging.NuoLogger.printException
import net.liftweb.json.Serialization.write
import net.liftweb.json.{DefaultFormats, parse}

/**
  * Created by Pulkit on Apr 14, 17.
  */
object VariableMetadata {

  case class NuoVariables(variables:List[NuoVariable])
  case class NuoVariable(var Scope:String,
                         var Name:String,
                         var Type:String,
                         var Value:String,
                         var CastValue:String)

  /*
* This function parses the Tenant Preference metadata file and returns the case class structure as AST
* */
  def loadVariables(nuoTenantDetails: NuoTenantDetails) : Unit = {

    /*
    Following statement is important to apply default formats.
    */

    try {
      if(NuoS3Client.doesS3FileExist(nuoTenantDetails.tenantBucketName,nuoTenantDetails.variableFile,nuoTenantDetails)){

        implicit val formats = DefaultFormats


        val jsonContent = NuoS3Client.readFromS3(nuoTenantDetails.tenantBucketName,
          nuoTenantDetails.variableFile,nuoTenantDetails)
        val jsonAST = parse(jsonContent)

        nuoTenantDetails.runDetails.variables.clear()
        nuoTenantDetails.runDetails.variables ++= jsonAST.extract[NuoVariables].variables.map(ele=>(ele.Scope,ele.Name)->ele)
      }
    } catch {
      case e: Exception => printException( e,nuoTenantDetails)
    }
  }

  def updateVariables(nuoTenantDetails: NuoTenantDetails): Unit ={
    implicit val formats = DefaultFormats

    NuoS3Client.writeToS3(nuoTenantDetails.tenantBucketName,
      nuoTenantDetails.variableFile,
      write(NuoVariables(nuoTenantDetails.runDetails.variables.values.toList)).getBytes(),nuoTenantDetails)
  }
}
*/
