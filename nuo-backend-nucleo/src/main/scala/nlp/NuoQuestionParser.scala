package nlp

import dag.NuoTenantDetails
import logging.NuoLogger
import metadata.NuoRequestMetadata
import nlp.grammar._
import nlp.parser.{evaEnglishLexer, evaEnglishParser}
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}

import scala.collection.JavaConverters._

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 05Dec2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoQuestionParser {


  /*
  * Returns,
  *   List of TokenTypeAndName separated by comma.
  *     TokenTypeAndName is Token type and name separated by space
  *   Parse tree string
  * */
  case class ParsedQuestion(sql: String,
                            tokens: String,
                            tree: String)

  def parseQuestion(requestTokenAndTree: Boolean,
                    nuoGrammarListener: NuoEvaEnglishListener): ParsedQuestion = {

    val questionText = nuoGrammarListener.currAnalysisRecognitionData.Selection.trim + " " + nuoGrammarListener.currAnalysisRecognitionData.Filter.trim

    //    val alreadyParsedQuestion = if (questionText == null || questionText.trim.isEmpty) Some(ParsedQuestion(null, null, null))
    //    else if (NuoEvaEnglishListener.isGrammarTestMode) None
    //    else Some(ParsedQuestion(nuoGrammarListener.currAnalysisRecognitionData.Sql, null, null))

    //    if (alreadyParsedQuestion.isDefined) alreadyParsedQuestion.get
    //    else {

    val updQuestionText = if (questionText.endsWith(".") || questionText.endsWith("?") || questionText.endsWith("!"))
      " " + questionText.replaceAll("([!?\\.]+$)", "") + " "
    else " " + questionText + " "
    val charStream = CharStreams.fromString(updQuestionText)

    val lexer = new evaEnglishLexer(charStream)
    val tokens = new CommonTokenStream(lexer)
    val parser = new evaEnglishParser(tokens)
    val tree = parser.question()

    new ParseTreeWalker().walk(nuoGrammarListener, tree)
    val parsedSql = tree.sql

    if (requestTokenAndTree)
      ParsedQuestion(parsedSql,
        getFormattedTokens(tokens),
        getFormattedParseTree(tree.toStringTree))
    else ParsedQuestion(parsedSql, null, null)
    //    }
  }

  def main(args: Array[String]): Unit = {

    NuoEvaEnglishListener.isGrammarTestMode = true

    List(
      "Find name of companies with country as France, Ireland, Netherlands or UK or city starts with Ams or Par or Ber or Transaction date is outside 12th apr 2016 and 2nd march "
    ).foreach { question =>

      try {
        val nuoGrammarListener = new NuoEvaEnglishListener(
          analysisId = "SampleAnalysis",
          selectionText = question,
          filterText = "",
          nuoUserMessage = NuoRequestMetadata.NuoUserMessage(
            AnalysisId = "SampleAnalysis",
            Selection = Some(question),
            Filter = None,
            ResultTableName = None,
            NuoRelationshipInput = None),
          nuoTenantDetails = new NuoTenantDetails(
            paramRequestId = "Test",
            paramTenantId = "tenant1",
            paramUsername = "Test"))

        val parsedQuestion = parseQuestion(requestTokenAndTree = true, nuoGrammarListener)

        NuoLogger.printInfo(question)
        NuoLogger.printInfo(parsedQuestion.sql)
        NuoLogger.printInfo(parsedQuestion.tokens)
        NuoLogger.printInfo(parsedQuestion.tree)
      } catch {
        case e: Exception =>
          if (e.getMessage != null && e.getMessage.equalsIgnoreCase("UserInputRequested")) {
            NuoLogger.printInfo(NuoEvaEnglishListener.nuoEvaMessage)
          } else {
            System.err.println(e.getStackTraceString)
          }
      }
    }
  }

  def getTokenTypeAndNames(tokens: CommonTokenStream): List[(String, String)] = {
    tokens.getTokens.asScala.toList.map { t =>

      val symbolicName = evaEnglishLexer.VOCABULARY.getSymbolicName(t.getType)
      val literalName = evaEnglishLexer.VOCABULARY.getLiteralName(t.getType)
      val tokenName = if (symbolicName == null) literalName else symbolicName
      (tokenName, t.getText.replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t"))
    }
  }

  def getFormattedTokens(tokens: CommonTokenStream): String = {

    val formattedTokens = new StringBuilder("\n[TOKENS]\n\n")
    formattedTokens.append(getTokenTypeAndNames(tokens).filterNot(_._1.equalsIgnoreCase("WS")).map(pair => pair._1 + " " + pair._2).mkString("\n") + "\n")
    formattedTokens.toString()
  }

  def getFormattedParseTree(tree: String): String = {
    val formattedTree = new StringBuilder("\n[PARSE-TREE]")
    var indentation = 1
    var buffer = ""
    var bufferStarted = false
    tree.toCharArray.foreach { c =>

      if (c == '(') indentation += 1
      if (c == '(' || c == ')') {
        if (indentation > 1) {
          formattedTree.append("\n")
        }
        for (i <- 0 until indentation) {

          formattedTree.append("  ")
        }
      }
      if (c == ')') indentation -= 1
      //
      //      if (bufferStarted && c == ']' && buffer.nonEmpty) {
      //        bufferStarted = false
      //        val symbolicName = v0Lexer.VOCABULARY.getSymbolicName(buffer.toInt)
      //        val literalName = v0Lexer.VOCABULARY.getLiteralName(buffer.toInt)
      //        val tokenName = if (symbolicName == null) literalName else symbolicName
      //        formattedTree.append(" -> "+buffer)
      //        buffer = ""
      //      }
      //      if (bufferStarted) buffer += c
      //      if (c == '[') bufferStarted = true
      formattedTree.append(c)
    }
    formattedTree.append("\n")
    formattedTree.toString()
  }
}
