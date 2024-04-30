package nlp

import java.io.File
import java.text.SimpleDateFormat

import logging.NuoLogger

import scala.sys.process._

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 03Dec2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoParserCodeGenerator {


  val AntlrLibPath = new File("resources/tools/antlr-4.7-complete.jar").getAbsolutePath
  val AntlrCodeOutputPath = new File("src/main/scala/nlp/parser/").getAbsolutePath
  val Listener = "-listener"
  val Visitor = "-visitor"
  val PackageName = "nlp.parser"
  val GrammarFilePath = new File("src/main/scala/nlp/grammar/").getAbsolutePath

  def main(args: Array[String]): Unit = {

    generateAntlrCode("evaEnglish")
    //    generateAntlrCode("adpLocationCriteriaV0")
  }

  def generateAntlrCode(grammarName: String): Unit = {
    var command = Seq("java", "-jar", AntlrLibPath, "-o", AntlrCodeOutputPath, Listener, Visitor, "-package", PackageName, s"$GrammarFilePath/$grammarName.g4")
    var response = command !!

    NuoLogger.printInfo(response)
    val serverDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    NuoLogger.printInfo(s"The code for grammar $grammarName has been generated at ${serverDateFormat.format(System.currentTimeMillis())}")

  }

}
