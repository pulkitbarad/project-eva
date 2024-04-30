package metadata

import java.io.{BufferedWriter, FileWriter}

import client.NuoJdbcClient
import logging.NuoLogger
import metadata.StorageMetadata.NuoField
import net.liftweb.json.Serialization.write
import net.liftweb.json.{DefaultFormats, parse}
import nlp.grammar.NuoEvaEnglishListener

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

object NuoRecognitionMetadata {


  object AliasType {
    val ALIAS_TYPE_FIELD = 2
    val ALIAS_TYPE_QUESTION = 3
    val ALIAS_TYPE_UNARY_EXP = 5
  }

  object QuestionType {
    val QUESTION_TYPE_SQL = 2
    val QUESTION_TYPE_ML_LEARN = 3
    val QUESTION_TYPE_ML_PREDICT = 5
    val QUESTION_TYPE_FILE_LOAD = 7
  }

  object RecognitionCommunicationType {
    val COMMUNICATION_TYPE_ERROR = -1
    val COMMUNICATION_TYPE_RELATIONSHIPS = 2
    val COMMUNICATION_TYPE_DEFAULT = 3
    val COMMUNICATION_TYPE_EXECUTION_STARTED = 5
    val COMMUNICATION_TYPE_RESULT_AVAILABLE = 7
    val COMMUNICATION_TYPE_EXECUTION_RUNNING = 11
    val COMMUNICATION_TYPE_EXECUTION_FAILED = 13
    val COMMUNICATION_TYPE_MAPPING = 17
  }

  object NuoExecutionStatusCode {
    val UNKNOWN = 2.0
    val FAILED = 3.0
    val RECOGNIZING = 5.0
    val WAITING_FOR_USER_INPUT = 7.0
    val RECOGNITION_COMPLETE = 11.0
    val BQ_JOB_STARTED = 13.0
    val BQ_JOB_COMPLETE = 17.0
    val PROFILING_JOB_STARTED = 19.0
    val PROFILING_JOB_COMPLETE = 23.0
    val TABLE_EXPORT_JOB_STARTED = 29.0
    val TABLE_EXPORT_JOB_COMPLETE = 31.0
    val ML_EXPORT_TO_S3_STARTED = 37.0
    val ML_EXPORT_TO_S3_COMPLETE = 41.0
    val AML_MODEL_TRAINING_STARTED = 43.0
    val AML_MODEL_TRAINING_COMPLETE = 47.0
    val AML_MODEL_PREDICTION_STARTED = 53.0
    val AML_MODEL_PREDICTION_COMPLETE = 59.0
    val ML_IMPORT_FROM_S3_STARTED = 61.0
    val ML_IMPORT_FROM_S3_COMPLETE = 67.0
    val ML_BQ_TEMP_LOAD_STARTED = 71.0
    val ML_BQ_TEMP_LOAD_COMPLETE = 73.0
    val ML_BQ_MAIN_LOAD_STARTED = 79.0
    val ML_BQ_MAIN_LOAD_COMPLETE = 83.0
    val ML_PROFILING_STARTED = 89.0
    val ML_PROFILING_COMPLETE = 97.0
    val IMAGE_FUNCTION_STARTED = 101.0
    val IMAGE_FUNCTION_COMPLETE = 103.0
    val IMAGE_BQ_LOAD_STARTED = 107.0
    val IMAGE_BQ_LOAD_COMPLETE = 109.0
    val IMAGE_PROFILING_STARTED = 113.0
    val IMAGE_PROFILING_COMPLETE = 127.0
  }

  case class NuoAnalysisRecognitionData(var ResultTableName: Option[String],
                                        var AnalysisType: Int,
                                        var AnalysisId: String,
                                        var Selection: String,
                                        var Filter: String,
                                        var Sql: String,
                                        var BqUdf: Option[String],
                                        var NonAggFieldIndexBase1: List[Int],
                                        var DoesOrCriteriaExist: Option[Boolean],
                                        var WhereClause: String,
                                        var HavingClause: String,
                                        var RankSelectClause: List[String],
                                        var RankWhereClause: Option[String],
                                        var ComparisionWhereClauses: List[ComparisionWhereClause],
                                        var NuoMLDetails: Option[NuoMLDetails],
                                        var StepsCompleted: Option[Int],
                                        var TotalSteps: Option[Int],
                                        var StartTimeMillis: Option[Long],
                                        var EndTimeMillis: Option[Long],
                                        var NuoExecutionStatuses: List[NuoExecutionStatus])

  case class ComparisionWhereClause(var Expression: String,
                                    var Suffix: String,
                                    var IsAggregated: Boolean,
                                    var GroupName:String)

  case class NuoImageDetails(var SourceFiles: Option[List[String]],
                             var LanguageHints: Option[List[String]],
                             var TargetTableName: Option[String],
                             var AppendToTable: Option[Boolean])

  case class NuoMLDetails(var sourceTableName: Option[String],
                          var targetColumnName: Option[String],
                          var modelName: Option[String],
                          var actionType: Option[String],
                          var targetNuoField: Option[NuoField],
                          var orderedSourceColumns: List[String])

  case class NuoCriteria(RuleText: String,
                         FieldType: String,
                         CriterionText: String,
                         RelatedFields: List[NuoQuestionField])

  case class NuoFieldRecognitionData(AliasName: String,
                                     AliasType: Int,
                                     Fields: List[NuoField],
                                     UnaryExpressions: List[NuoUnaryExpression])

  case class NuoTimeFrameExpression(RuleText: String,
                                    var Fields: List[NuoQuestionField])

  case class NuoUnaryExpression(var LeftField: NuoField,
                                var RightExpText: String)

  case class NuoQuestionField(FieldText: String,
                              Field: NuoField)

  case class NuoQuestionTestInfo(var Id: Option[String],
                                 var ParentId: Option[String],
                                 var QuestionText: Option[String],
                                 var ExpectedSql: Option[String],
                                 var GeneratedSql: Option[String],
                                 var IsTestSuccessful: Option[String])

  case class NuoRelationship(leftEntityName: String,
                             rightEntityName: String,
                             intermediateEntities: List[String],
                             var commonFields: List[NuoCommonField],
                             var joinClause: String,
                             var isVerified: Boolean)

  case class NuoCommonField(leftField: NuoField,
                            rightField: NuoField)

  case class NuoExecutionStatus(var Status: Double,
                                var Description: Option[String],
                                var RefParams: List[String])

  /*
  * 04MAR2018:Pulkit: NuoGrammarField is identical to QuestionFields.
  * It may be removed in future after performing impact analysis.
  * */
  case class NuoGrammarField(var FieldText: String,
                             var FieldType: Int,
                             var FieldValue: String,
                             var DateParts: NuoDateParts,
                             var Datatype: String,
                             var EntityNames: java.util.List[String])

  case class NuoDateParts(var dd: Int,
                          var mm: Int,
                          var yyyy: Int,
                          var hh: Int,
                          var MM: Int,
                          var ss: Int,
                          var SSS: Int,
                          var tz: String)


  def readNuoRelationships(nuoGrammarListener: NuoEvaEnglishListener): List[NuoRelationship] = {

    /*
    Following statement is important to apply default formats.
    */
    implicit val formats = DefaultFormats


    val queryResults = NuoJdbcClient.executeMetadataQuery(
      "SELECT LeftEntityName,RightEntityName, IntermediateEntities, CommonFields, JoinClause, IsVerified" +
        " FROM " + NuoEvaEnglishListener.nuoTenantDetails.MetadataTable.nuoRelationships
    )

    queryResults
      .map { row =>
        NuoRelationship(
          leftEntityName = row(0),
          rightEntityName = row(1),
          intermediateEntities = row(2).split(",").toList.filter(ele=> ele !=null && ele.trim.nonEmpty),
          commonFields =
            if(row(3).trim.length > 0 && row(3).contains("|")){

              row(3).split(",")
                .map { ele =>
                  val fields =
                    ele.split("\\+")
                      .map { fieldText =>
                        val params = fieldText.split("\\|")
                        NuoField("", params(0), params(1), params(2))
                      }
                  NuoCommonField(fields(0), fields(1))
                }.toList
            }else List(),
          joinClause = row(4),
          isVerified = row(5).toBoolean
        )
      }
  }


  def readTestDataLocal(fileName: String): List[NuoQuestionTestInfo] = {


    implicit val formats = DefaultFormats

    Source.fromFile(fileName).getLines().map { line =>
      val questionMetadataAST = parse(line)

      questionMetadataAST.extract[NuoQuestionTestInfo]

    }.toList
  }

  def writeTestDataLocal(fileName: String, questions: List[NuoQuestionTestInfo]): Unit = {

    val bufferedWriter = new BufferedWriter(new FileWriter(fileName))
    try {
      implicit val formats = DefaultFormats

      val x = questions.map(write(_)).mkString("\n")
      val content = x

      bufferedWriter.write(content)
      bufferedWriter.close()
    } catch {
      case e: Exception => NuoLogger.printInfo(e.getStackTraceString)
        bufferedWriter.close()
    }
  }

  def writeFileLocal(fileName: String, content: String): Unit = {

    val bufferedWriter = new BufferedWriter(new FileWriter(fileName))
    try {
      bufferedWriter.write(content)
      bufferedWriter.close()
    } catch {
      case e: Exception => NuoLogger.printInfo(e.getStackTraceString)
        bufferedWriter.close()
    }
  }
}
