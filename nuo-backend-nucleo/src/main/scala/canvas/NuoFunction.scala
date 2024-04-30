package canvas

import java.security.MessageDigest

/**
  * Copyright 2015 Nuocanvas Inc.
  *
  *
  * Created by Pulkit on 29SEP2015.
  *
  * Content of this file is proprietry and confidential.
  * It should not be reused or disclosed without prior consent
  * of distributor
  **/
object NuoFunction {

  /*
  * This function register all custom UDF to Spark SQlContext
  * */
//
//  def registerUDFs(hiveContext: HiveContext): Unit = {
//
//    /*
//    * Name: Concat
//    * Arguments:
//    * arg1:Any,
//    * arg2:Any
//    * Return: String
//    * */
//    hiveContext.udf.register(NuoModifier.UDFNames.UDFConcat, (arg1: Any, arg2: Any) => {
//      concat(arg1, arg2)
//    })
//
//    /*
//    * Name: Length
//    * Arguments:
//    * arg1:String,
//    * Return: Int
//    * */
//    hiveContext.udf.register(NuoModifier.UDFNames.UDFLength, (arg1: String) => {
//      length(arg1)
//    })
//
//    /*
//    * Name: Md5
//    * Arguments:
//    * arg1:String,
//    * Return: String
//    * */
//    hiveContext.udf.register(NuoModifier.UDFNames.UDFMd5, (arg1: String) => {
//      md5(arg1)
//    })
//
//    /*
//    * Name: Sha256
//    * Arguments:
//    * arg1:String,
//    * Return: String
//    * */
//    hiveContext.udf.register(NuoModifier.UDFNames.UDFSha256, (arg1: String) => {
//      sha256(arg1)
//    })
//
//  }
//

  def md5(input: Any): String = {

    val digest = MessageDigest.getInstance("MD5")

    digest.digest(input.toString.getBytes).map("%02x".format(_)).mkString
  }

  def sha256(input: Any): String = {

    val digest = MessageDigest.getInstance("SHA-256")

    digest.digest(input.toString.getBytes).map("%02x".format(_)).mkString
  }

  def concat(arg1: Any, arg2: Any): String = (arg1, arg2) match {
    case _ => arg1.toString + arg2.toString
  }

  //    def concat(arg1: String, arg2: String*): String = (arg1, arg2) match {
  //      case _ => arg1 + arg2.mkString
  //    }

  def length(arg1: String): Int = arg1 match {
    case _ => arg1.length
  }

}

