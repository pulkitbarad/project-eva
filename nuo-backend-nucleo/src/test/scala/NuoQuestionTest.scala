import java.io.{File, FileFilter}

import dag.NuoTenantDetails
import metadata.NuoRecognitionMetadata
import nlp.grammar.NuoEvaEnglishListener
//import org.fluttercode.datafactory.impl.DataFactory

import scala.io.Source

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 08Dec2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoQuestionTest {

  val testDataDirectory = "src/test/data/"
  //  val SqlTestStorageDetails = testDataDirectory + "SqlTestStorageDetails.json"
  //  val NuoRelationshipsFile = testDataDirectory + "SqlTestNuoRelationships.txt"

  //  val PRETTY_PRINT_NONE = 0
  val PRETTY_PRINT_SQL = 2
  val PRETTY_PRINT_TOKENS = 3
  val PRETTY_PRINT_TREE = 5
  val WRITE_NOTHING = 7
  val WRITE_TESTS = 11
  val WRITE_RESULTS = 13
  val WRITE_FULL_RESULTS = 17

  val nuoTenantDetails = new NuoTenantDetails(
    paramRequestId = "Test",
    paramTenantId = "tenant1",
    paramUsername = "Test")


  def main(args: Array[String]): Unit = {

    NuoEvaEnglishListener.isGrammarTestMode = true
//    testAllQuestions(testDataDirectory + "test_cases/", writeFlag = WRITE_TESTS)
    testAllQuestions(testDataDirectory + "test_cases/", writeFlag = WRITE_RESULTS)
    //    testAllQuestions(testDataDirectory + "test_cases/", writeFlag = PRETTY_PRINT_SQL)
  }

  def testAllQuestions(fileName: String,
                       writeFlag: Int): Unit = {

    val inputFile = new File(fileName)

    if (inputFile.isDirectory) {
      inputFile.listFiles(new FileFilter {
        //        override def accept(pathname: File) = !(pathname.getAbsolutePath.toLowerCase.endsWith("_result") || pathname.getAbsolutePath.toLowerCase.endsWith("_test"))
        override def accept(pathname: File) = !pathname.getAbsolutePath.toLowerCase.endsWith("_test") && !pathname.getAbsolutePath.toLowerCase.endsWith("_result") && !pathname.getAbsolutePath.toLowerCase.endsWith("_failed")
      }) foreach (file => testQuestionFile(file.getAbsolutePath, writeFlag))
    } else testQuestionFile(inputFile.getAbsolutePath, writeFlag)
  }

  def testQuestionFile(filePath: String,
                       writeFlag: Int): Unit = {

    var counter = 0
    // 1 based index java when the test is run for the first time.
    val testFile = filePath + "_Test"
    val resultFile = filePath + "_Result"
    val startTime = System.currentTimeMillis()
    if (List(PRETTY_PRINT_SQL, PRETTY_PRINT_TOKENS, PRETTY_PRINT_TREE).map(writeFlag % _).contains(0)) {
      val formattedResultFile = filePath + "_Formatted_Result"
      val formattedTree = new StringBuilder(s"File: $filePath \n")
      Source.fromFile(filePath).getLines.foreach { questionText =>

        if (questionText.nonEmpty) {

          val parsedQuestion = nlp.NuoQuestionParser.parseQuestion(requestTokenAndTree = true,
            new NuoEvaEnglishListener(
              analysisId = "TestAnalysis",
              selectionText = questionText,
              filterText = "",
              nuoUserMessage = null,
              nuoTenantDetails = nuoTenantDetails))
          counter += 1
          formattedTree.append(s"\nQuestion $counter: $questionText\n")
          if (writeFlag % PRETTY_PRINT_SQL == 0) formattedTree.append(parsedQuestion.sql)
          if (writeFlag % PRETTY_PRINT_TOKENS == 0) formattedTree.append(parsedQuestion.tokens)
          if (writeFlag % PRETTY_PRINT_TREE == 0) formattedTree.append(parsedQuestion.tree)
          val delim = "================================================================================================"
          formattedTree.append(delim)
        }
      }
      NuoRecognitionMetadata.writeFileLocal(formattedResultFile, formattedTree.toString())

      System.out.println(s"Completed writing formatted result with option $writeFlag for File = $filePath in ${(System.currentTimeMillis() - startTime) / 1000.0} second.")

    }


    if (writeFlag % WRITE_TESTS == 0) {

      if (new File(filePath).getParentFile.listFiles().exists(_.getAbsolutePath.equalsIgnoreCase(testFile))) {

        System.out.println(s"Test file already exist for $filePath. Please backup or rename the test files before writing another test.")

      } else {

        counter = 0
        val testCases = Source.fromFile(filePath).getLines.toList.filter(_.nonEmpty).map { questionText =>

          val parsedQuestion = nlp.NuoQuestionParser.parseQuestion(requestTokenAndTree = false,
            new NuoEvaEnglishListener(
              analysisId = "TestAnalysis",
              selectionText = questionText,
              filterText = "",
              nuoUserMessage = null,
              nuoTenantDetails = nuoTenantDetails))

          counter += 1
          NuoRecognitionMetadata.NuoQuestionTestInfo(Id = Some(counter.toString),
            ParentId = None,
            QuestionText = Some(questionText),
            ExpectedSql = Some(removeConsecutiveWhiteSpace(parsedQuestion.sql)),
            GeneratedSql = None,
            IsTestSuccessful = None)
        }
        NuoRecognitionMetadata.writeTestDataLocal(testFile, testCases)
        System.out.println(s"Test case $testFile has been created successfully in ${(System.currentTimeMillis() - startTime) / 1000.0} second.")
      }
    }

    if (writeFlag % WRITE_RESULTS == 0) {
      if (!new File(filePath).getParentFile.listFiles().exists(_.getAbsolutePath.equalsIgnoreCase(testFile))) {

        System.out.println(s"Test file does not exist for test case in $filePath. Please write test files before running the test.")

      } else {

        val formattedFailedTree = new StringBuilder(s"File: $filePath \n")

        counter = 0
        val testResults = NuoRecognitionMetadata.readTestDataLocal(testFile).map { question =>

          counter += 1
          if (question.QuestionText.nonEmpty) {

            val parsedQuestion = nlp.NuoQuestionParser.parseQuestion(requestTokenAndTree = true,
              new NuoEvaEnglishListener(
                analysisId = "TestAnalysis",
                selectionText = question.QuestionText.get,
                filterText = "",
                nuoUserMessage = null,
                nuoTenantDetails = nuoTenantDetails))
            val generatedSql = parsedQuestion.sql

            question.GeneratedSql = Some(removeConsecutiveWhiteSpace(generatedSql))
            question.IsTestSuccessful =
              Some((question.ExpectedSql.nonEmpty &&
                question.GeneratedSql.get.equalsIgnoreCase(question.ExpectedSql.get)).toString)
            val result = if (question.IsTestSuccessful.get.toBoolean) "PASSED" else "FAILED"
            println(s"Question ${question.Id.getOrElse("-1")} test has $result")

            if (!question.IsTestSuccessful.get.toBoolean) {

              formattedFailedTree.append(s"\nQuestion $counter: ${question.QuestionText.get}\n")
              formattedFailedTree.append(s"\n[Expected SQL]    :${question.ExpectedSql.get}\n")
              formattedFailedTree.append(s"[Generated SQL]   :${generatedSql}\n")
              formattedFailedTree.append(parsedQuestion.tokens)
              formattedFailedTree.append(parsedQuestion.tree)
              val delim = "================================================================================================"
              formattedFailedTree.append(delim)
            }
            question

          } else {
            question.IsTestSuccessful = Some("false")
            println(s"I could not find the QuestionText for the question with id${question.Id.getOrElse("-1")}")
            question
          }
        }

        if (writeFlag % WRITE_FULL_RESULTS == 0) {

          NuoRecognitionMetadata.writeTestDataLocal(resultFile, testResults)
        }
        //        NuoRecognitionMetadata.writeTestDataLocal(failedTestFile, testResults.filter(!_.IsTestSuccessful.get.toBoolean))

        val formattedFailedFile = filePath + "_Formatted_Failed"
        NuoRecognitionMetadata.writeFileLocal(formattedFailedFile, formattedFailedTree.toString())
        val successCount = testResults.count(_.IsTestSuccessful.getOrElse("false").equalsIgnoreCase("true"))
        System.out.println(s"File = $filePath has failed ${testResults.length - successCount} tests with success rate of ${successCount / testResults.length.toDouble * 100.0} % in ${(System.currentTimeMillis() - startTime) / 1000.0} second.")
      }
    }

  }

  def removeConsecutiveWhiteSpace(input: String): String = {
    input.replaceAll("[ \\t\\r?\\n]+", " ")
  }
}
