package nlp.grammar

import java.text.SimpleDateFormat
import java.util.Calendar

import canvas.NuoDataTypeHandler
import canvas.NuoDataTypeHandler.NuoDataType
import client.{NuoDynamoDBClient, NuoS3Client}
import com.amazonaws.auth.{BasicAWSCredentials, DefaultAWSCredentialsProviderChain}
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.machinelearning.AmazonMachineLearningClient
import com.amazonaws.services.s3.AmazonS3Client
import dag.NuoTenantDetails
import execution.NuoRequestHandler
import metadata.NuoRecognitionMetadata._
import metadata.StorageMetadata.{NuoEntity, NuoField, NuoStorageDetails}
import metadata.{NuoRecognitionMetadata, NuoRequestMetadata}
import nlp.NuoQuestionRecognizer
import nlp.parser._
import nlp.parser.evaEnglishParser._

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 07Dec2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

class NuoEvaEnglishListener extends evaEnglishBaseListener {

  var recognizedFields = mutable.ArrayBuffer[NuoField]()
  var recognizedRankPartFields = mutable.ArrayBuffer[List[String]]()
  var recognizedRankOrdFields = mutable.ArrayBuffer[List[String]]()
  var recordedFieldAliases = mutable.HashMap[String, String]()

  var currAnalysisRecognitionData: NuoAnalysisRecognitionData = _

  /*
  *
  * 25NOV2018T2058Z:Pulkit: Bigquery specific features
  * SELECT EXCEPT
  * Safe_cast
  * STRPOS, SQLServer=> CHRINDEX, Oracle=> INSTR
  *
  *
  * */
  def this(analysisId: String,
           selectionText: String,
           filterText: String,
           nuoUserMessage: metadata.NuoRequestMetadata.NuoUserMessage,
           nuoTenantDetails: NuoTenantDetails) {

    this()

    NuoEvaEnglishListener.nuoTenantDetails = nuoTenantDetails
    NuoEvaEnglishListener.nuoUserMessage = nuoUserMessage

    //    NuoEvaEnglishListener.nuoStorageDetails = StorageMetadata.readNuoStorageDetails()

    NuoEvaEnglishListener.nuoRelationships = NuoRecognitionMetadata.readNuoRelationships(this)

    //    NuoEvaEnglishListener.nuoFieldRecognitionData = NuoRecognitionMetadata.readFieldRecognitionData

    //    NuoEvaEnglishListener.nuoQuestionRecognitionData = NuoRecognitionMetadata.readQuestionRecognitionData


    val questionTextHash = NuoRequestHandler.getMd5Hash(selectionText + filterText)

    this.currAnalysisRecognitionData =

      NuoAnalysisRecognitionData(
        ResultTableName = None,
        AnalysisType = NuoRecognitionMetadata.QuestionType.QUESTION_TYPE_SQL,
        AnalysisId = analysisId,
        Selection = selectionText,
        Filter = filterText,
        Sql = "",
        BqUdf = None,
        NonAggFieldIndexBase1 = List(),
        DoesOrCriteriaExist = None,
        WhereClause = "",
        HavingClause = "",
        RankSelectClause = List(),
        RankWhereClause = None,
        ComparisionWhereClauses = List(),
        NuoMLDetails = None,
        StepsCompleted = Some(0),
        TotalSteps = Some(3),
        StartTimeMillis = Some(System.currentTimeMillis()),
        EndTimeMillis = Some(System.currentTimeMillis()),
        NuoExecutionStatuses = List(NuoExecutionStatus(NuoRecognitionMetadata.NuoExecutionStatusCode.RECOGNIZING, None, List()))
      )
  }

  //
  //  override def exitCommandLearn(ctx: CommandLearnContext): Unit = {
  //
  //    if (NuoEvaEnglishListener.isGrammarTestMode)
  //      ctx.sql = "Learn " + ctx.fieldContent().fieldValue
  //    else {
  //
  //      this.currAnalysisRecognitionData.AnalysisType = QuestionType.QUESTION_TYPE_ML_LEARN
  //      NuoRequestHandler.assignTotalSteps(5, this)
  //
  //
  //      val fieldText = ctx.fieldContent().fieldText
  //      val columnName = fieldText.substring(1, fieldText.indexOf("]"))
  //      val tableName = fieldText.substring(1, fieldText.indexOf("]"))
  //      val datatype = fieldText.substring(1, fieldText.indexOf("]"))
  //
  //      this.currAnalysisRecognitionData
  //        .NuoMLDetails =
  //        Some(
  //          NuoMLDetails(
  //            sourceTableName = Some(tableName),
  //            targetColumnName = Some(columnName),
  //            modelName = Some(tableName + "_" + columnName),
  //            actionType = Some(ActionType.External.MLaaS.TrainModel),
  //            targetNuoField = Some(NuoField("", tableName, columnName, datatype)),
  //            orderedSourceColumns = List()
  //          )
  //        )
  //      currAnalysisRecognitionData
  //        .NuoExecutionStatuses +:=
  //        NuoRecognitionMetadata.NuoExecutionStatus(
  //          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.RECOGNITION_COMPLETE,
  //          Description = None,
  //          RefParams = List())
  //    }
  //    if (!NuoEvaEnglishListener.isGrammarTestMode) {
  //      //      NuoRecognitionMetadata.writeRecognitionData(this)
  //    }
  //  }
  //
  //  override def exitCommandPredict(ctx: CommandPredictContext): Unit = {
  //
  //    if (NuoEvaEnglishListener.isGrammarTestMode)
  //      ctx.sql = "Predict " + ctx.fieldContent(0).fieldValue + " for " + ctx.fieldContent(1).fieldValue
  //    else {
  //
  //      this.currAnalysisRecognitionData.AnalysisType = QuestionType.QUESTION_TYPE_ML_PREDICT
  //      NuoRequestHandler.assignTotalSteps(9, this)
  //
  //      var allModelTargetColumns =
  //        NuoEvaEnglishListener
  //          .nuoQuestionRecognitionData
  //          .filter { questionData =>
  //            questionData.NuoMLDetails.isDefined &&
  //              questionData.NuoMLDetails.get.actionType.get.equalsIgnoreCase(ActionType.External.MLaaS.TrainModel)
  //          }.map {
  //          _.NuoMLDetails.get.targetNuoField.get
  //        }
  //
  //
  //      var sourceTableName = "TO_DO"
  //      var targetColumnName = "TO_DO"
  //      var trainingTableName = "TO_DO"
  //      var orderedSourceColumns = List[String]()
  //      var trainingTableRuleText = targetColumnName + "_training"
  //      var mappingRuleText = targetColumnName + "_mapping"
  //
  //      val userResponse = NuoEvaEnglishListener.nuoUserMessage
  //
  //      var mappingSourceColumns =
  //        NuoBqClient
  //          .getTableSchema(
  //            NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
  //            sourceTableName
  //          )
  //          .map(_._1)
  //          .zipWithIndex
  //          .toList
  //
  //      var index = 0
  //      var mappingTargetColumns =
  //        NuoBqClient
  //          .getTableSchema(
  //            NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetNameWOTenantId,
  //            trainingTableName
  //          )
  //          .map(_._1)
  //          .filterNot(_.equalsIgnoreCase(targetColumnName))
  //          .map { col =>
  //            index += 1
  //            var sourceCol = mappingSourceColumns.find(_._1.equalsIgnoreCase(col))
  //            if (sourceCol.isDefined)
  //              NuoMappingElement(col, sourceCol.get._2)
  //            else
  //              NuoMappingElement(col, index)
  //          }
  //          .toList
  //
  //      if (true /*userResponse != null
  //        && userResponse.AnalysisId.isDefined
  //        && userResponse.RuleText.isDefined
  //        && userResponse.RuleText.get.equalsIgnoreCase(mappingRuleText)*/ ) {
  //
  //
  //        /*
  //        *
  //        * 24AUG2018:Pulkit: To be updated.
  //        *
  //        *
  //        *
  //        *
  //        *
  //        if (userResponse.CommunicationType.get %
  //          NuoRecognitionMetadata
  //            .RecognitionCommunicationType
  //            .COMMUNICATION_TYPE_MAPPING == 0) {
  //
  //          var mapping = userResponse.Responses.get
  //            .map { ele =>
  //              var arr = ele.split("\\|~\\|")
  //              (arr(0), arr(1))
  //            }
  //          orderedSourceColumns =
  //            mappingTargetColumns
  //              .map { ele =>
  //                var src = mapping.find(mEle => mEle._2.equalsIgnoreCase(ele.label))
  //                if (src.isEmpty) throw new Exception("I could not map the columns between training and prediction data.")
  //                else src.get._1
  //              }
  //
  //        } else {
  //          NuoRequestHandler.reportErrorToUser(new Exception(s"I could not understand the user input during recognition of mapping between training and prediction data."))
  //          throw new Exception("Unreachable Code statement")
  //        }
  //        *
  //        *
  //        *
  //        *
  //        *
  //        *
  //        *
  //        */
  //      } else {
  //        sendNuoEvaMessage(
  //          analysisId = currAnalysisRecognitionData.AnalysisId,
  //          ruleText = mappingRuleText,
  //          communicationType = NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_MAPPING,
  //          evaResponseMessage = s"Please help me understand the mapping of columns between training data and prediction data.",
  //          leftEntityName = None,
  //          rightEntityName = None,
  //          nuoMappingInput =
  //            Some(
  //              NuoMappingInput(
  //                mappingSourceColumns.map(ele => NuoMappingElement(ele._1, 0)),
  //                mappingTargetColumns.toList
  //              )
  //            ),
  //          nuoPollingDetails = None,
  //          nuoGrammarListener = this)
  //        None
  //      }
  //
  //      this.currAnalysisRecognitionData
  //        .NuoMLDetails =
  //        Some(
  //          NuoMLDetails(
  //            sourceTableName = Some(sourceTableName),
  //            targetColumnName = Some(targetColumnName),
  //            modelName = Some(trainingTableName + "_" + targetColumnName),
  //            actionType = Some(ActionType.External.MLaaS.PredictValue),
  //            Some(NuoField("TO_DO", "TO_DO", "TO_DO", "TO_DO")),
  //            orderedSourceColumns = orderedSourceColumns
  //          )
  //        )
  //      currAnalysisRecognitionData
  //        .NuoExecutionStatuses +:=
  //        NuoRecognitionMetadata.NuoExecutionStatus(
  //          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.RECOGNITION_COMPLETE,
  //          Description = None,
  //          RefParams = List())
  //
  //    }
  //    if (!NuoEvaEnglishListener.isGrammarTestMode) {
  //      //      NuoRecognitionMetadata.writeRecognitionData(this)
  //    }
  //  }

  override def exitCommandSelect(ctx: CommandSelectContext): Unit = {

    val selectClauseFields = ctx.selectClause()
      .fieldList()
      .values.asScala
      .map(_.asScala.toList)
      .map(attrList => (attrList.head, attrList(1).toInt % NuoEvaEnglishListener.FIELD_TYPE_AGG == 0 && attrList(1).toInt > 0))
    val fieldList = selectClauseFields.map(_._1.trim)
    var selectClause = fieldList.mkString(",\n\t")
    ctx.fields = fieldList.asJava

    val tableName = "X"
    var havingClause = ""
    var groupByClause = ""
    var whereClause = ""

    val criteriaClauseObj = ctx.criteriaClause()

    var isGroupByRequired = selectClauseFields.exists(_._2)
    if (criteriaClauseObj != null) {
      if (criteriaClauseObj.fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG == 0) {
        if (this.currAnalysisRecognitionData.DoesOrCriteriaExist.isDefined && this.currAnalysisRecognitionData.DoesOrCriteriaExist.get) {
          havingClause = criteriaClauseObj.sumCaseValue
        } else {
          havingClause = criteriaClauseObj.sumCaseValue
        }
        isGroupByRequired = true
      } else {
        whereClause = criteriaClauseObj.defaultValue
      }
    }

    ctx.entities = this.recognizedFields.filter(field => field != null && field.EntityName != null && field.EntityName.trim.nonEmpty).map(_.EntityName).distinct.asJava

    if (whereClause.nonEmpty) {
      this.currAnalysisRecognitionData.WhereClause = whereClause
      ctx.whereClause = this.currAnalysisRecognitionData.WhereClause
    }

    if (isGroupByRequired) {
      var index = 1

      selectClauseFields.foreach { fieldAndAggType =>
        if (!fieldAndAggType._2 && !this.currAnalysisRecognitionData.NonAggFieldIndexBase1.contains(index))
          this.currAnalysisRecognitionData.NonAggFieldIndexBase1 :+= index
        index += 1
      }
      ctx.nonAggFields = this.currAnalysisRecognitionData.NonAggFieldIndexBase1.map(_.toString).asJava
    }

    if (havingClause.nonEmpty) {
      this.currAnalysisRecognitionData.HavingClause = havingClause
      ctx.havingClause = this.currAnalysisRecognitionData.HavingClause
    }

    //      if (this.currAnalysisRecognitionData.ComparisionWhereClauses.nonEmpty) {
    //
    //        var subqueries = new mutable.ArrayBuffer[String]()
    //        var finalQuery = new mutable.StringBuilder("SELECT DISTINCT ")
    //
    //
    //        var selectFields =
    //          (this.recognizedRankFields
    //            .distinct
    //            .filter(ele =>
    //              ele.EntityName != null
    //                && ele.EntityName.trim.nonEmpty
    //                && ele.FieldName != null
    //                && ele.FieldName.trim.nonEmpty
    //                && !ctx.fields.asScala.toList.distinct.exists(_.contains(s"`${ele.EntityName}`.`${ele.FieldName}`"))
    //            )
    //            .map { ele =>
    //              var exp = recordedFieldAliases.getOrElse(s"`${ele.EntityName}`.`${ele.FieldName}`", ele.FieldName)
    //              List(exp, "2", ele.DataType)
    //            }) ++
    //            ctx.selectClause().fieldList().values.asScala.map(_.asScala.toList).toList
    //        var numberFields =
    //          selectFields
    //            .filterNot { ele =>
    //              ele.head.toLowerCase.contains("`lat`") ||
    //                ele.head.toLowerCase.contains("`long`") ||
    //                ele.head.toLowerCase.contains("`latitude`") ||
    //                ele.head.toLowerCase.contains("`longitude`")
    //            }
    //            .filter(ele => NuoDataTypeHandler.isNumberType(ele(2)))
    //
    //        var nonNumberFields =
    //          selectFields
    //            .filterNot { ele =>
    //
    //              NuoDataTypeHandler.isNumberType(ele(2)) && !(
    //                ele.head.toLowerCase.contains("`lat`") ||
    //                  ele.head.toLowerCase.contains("`long`") ||
    //                  ele.head.toLowerCase.contains("`latitude`") ||
    //                  ele.head.toLowerCase.contains("`longitude`")
    //                )
    //            }
    //
    //        var groupIndex = 0
    //        this.currAnalysisRecognitionData
    //          .ComparisionWhereClauses
    //          .groupBy(_.GroupName)
    //          .foreach { comparisionGroup =>
    //
    //
    //            var index = 0
    //            comparisionGroup._2
    //              .foreach { compWhereClause =>
    //
    //                var compWhereClauseSuffix = compWhereClause.Suffix
    //
    //                subqueries += constructSql(
    //                  selectFieldList = ctx.fields.asScala.toList.distinct,
    //                  entities = ctx.entities.asScala.toList,
    //                  whereClause = ctx.whereClause,
    //                  nonAggFields = if (ctx.nonAggFields != null) ctx.nonAggFields.asScala.toList else List(),
    //                  havingClause = ctx.havingClause,
    //                  comparisionWhereClause = compWhereClause,
    //                  includeRankSubquery = groupIndex == 0 && index == 0)
    //
    //                if (nonNumberFields.nonEmpty) {
    //
    //                  if (groupIndex == 0 && index == 0) {
    //                    finalQuery.append(
    //                      nonNumberFields
    //                        .map { fieldInfo =>
    //                          var colAlias = recordedFieldAliases.getOrElse(fieldInfo.head, fieldInfo.head)
    //                          comparisionGroup._2
    //                            .map { compWhereClauseInner =>
    //
    //                              s" WHEN `Final_SRC${compWhereClauseInner.Suffix}`.$colAlias IS NOT NULL THEN `Final_SRC${compWhereClauseInner.Suffix}`.$colAlias"
    //                            }.mkString("CASE ", " ", s" END AS $colAlias")
    //                        }
    //                        .mkString(", ")
    //                    )
    //                  }
    //                  finalQuery.append(", ")
    //                }
    //
    //
    //                finalQuery.append(
    //                  numberFields
    //                    .map { fieldInfo =>
    //                      var colAlias = recordedFieldAliases.getOrElse(fieldInfo.head, fieldInfo.head)
    //                      var exp = s"`Final_SRC${compWhereClauseSuffix}`.$colAlias AS $colAlias${compWhereClauseSuffix}"
    //                      if (comparisionGroup._2.length > 1 && index == comparisionGroup._2.length - 1) {
    //
    //                        var headSuffix = comparisionGroup._2.head.Suffix
    //                        if (colAlias.startsWith("Rank_Of_") || colAlias.startsWith("Leading_") || colAlias.startsWith("Following_"))
    //
    //                          exp += s", (`Final_SRC${headSuffix}`.$colAlias - `Final_SRC${compWhereClauseSuffix}`.$colAlias) AS ${colAlias}_${comparisionGroup._1}_Change"
    //                        else
    //                          exp += s", (SAFE_DIVIDE(`Final_SRC${headSuffix}`.$colAlias, `Final_SRC${compWhereClauseSuffix}`.$colAlias) - 1)  * 100.0 AS Percentage_Change_In_${colAlias}_${comparisionGroup._1}"
    //                      }
    //                      exp
    //                    }
    //                    .mkString(", ")
    //                )
    //                index += 1
    //              }
    //            groupIndex += 1
    //          }
    //        finalQuery.append(" FROM ")
    //
    //        if (this.currAnalysisRecognitionData.ComparisionWhereClauses.length > 1) {
    //
    //
    //          if (nonNumberFields.nonEmpty) {
    //
    //            var index = 0
    //            this.currAnalysisRecognitionData
    //              .ComparisionWhereClauses
    //              .foreach { compWhereClause =>
    //                var currCompSuffix = compWhereClause.Suffix
    //                if (index > 0) {
    //                  finalQuery.append(s" FULL JOIN `Final_SRC${currCompSuffix}`")
    //                  finalQuery.append(s" USING ")
    //                  finalQuery.append(
    //                    nonNumberFields
    //                      .map { fieldInfo =>
    //
    //                        recordedFieldAliases.getOrElse(fieldInfo.head, fieldInfo.head)
    //                      }
    //                      .mkString("(", ", ", ")")
    //                  )
    //                } else {
    //                  finalQuery.append(s"`Final_SRC${currCompSuffix}`")
    //                }
    //                index += 1
    //              }
    //          } else {
    //            this.currAnalysisRecognitionData
    //              .ComparisionWhereClauses
    //              .map { compWhereClause =>
    //                finalQuery.append(s"`Final_SRC${compWhereClause.Suffix}`")
    //
    //              }.mkString(", ")
    //          }
    //        } else {
    //          s"`Final_SRC${this.currAnalysisRecognitionData.ComparisionWhereClauses.head.Suffix}`"
    //        }
    //
    //        subqueries.mkString("WITH ", ",", " ") + finalQuery.toString()
    //      } else {
    //        "WITH " +
    //          constructSql(
    //            selectFieldList = ctx.fields.asScala.toList.distinct,
    //            entities = ctx.entities.asScala.toList,
    //            whereClause = ctx.whereClause,
    //            nonAggFields = if (ctx.nonAggFields != null) ctx.nonAggFields.asScala.toList else List(),
    //            havingClause = ctx.havingClause,
    //            comparisionWhereClause = ComparisionWhereClause("", "", false, ""),
    //            includeRankSubquery = true) +
    //          " SELECT * FROM Final_SRC"
    //      }
    this.currAnalysisRecognitionData.Sql =
      constructSql(
        selectFieldList = ctx.fields.asScala.toList.distinct,
        entities = ctx.entities.asScala.toList,
        whereClause = ctx.whereClause,
        nonAggFields = if (ctx.nonAggFields != null) ctx.nonAggFields.asScala.toList else List(),
        havingClause = ctx.havingClause)


    if (this.currAnalysisRecognitionData.BqUdf.isDefined && this.currAnalysisRecognitionData.BqUdf.get.nonEmpty)
      this.currAnalysisRecognitionData.Sql = this.currAnalysisRecognitionData.BqUdf + " " + this.currAnalysisRecognitionData.Sql
    ctx.sql = this.currAnalysisRecognitionData.Sql

    currAnalysisRecognitionData
      .NuoExecutionStatuses +:=
      NuoRecognitionMetadata
        .NuoExecutionStatus(
          Status = NuoRecognitionMetadata.NuoExecutionStatusCode.RECOGNITION_COMPLETE,
          Description = None,
          RefParams = List())
    if (!NuoEvaEnglishListener.isGrammarTestMode) {
      //      NuoRecognitionMetadata.writeRecognitionData(this)
    }
  }

  def getCompWhereClauseSuffix(compWhereClause: String): String = {
    "_" +
      compWhereClause
        .substring(
          if (compWhereClause.trim.indexOf("=") >= 0) {
            compWhereClause.trim.indexOf("=") + 1
          } else 0
        )
  }

  def constructSql(selectFieldList: List[String],
                   entities: List[String],
                   whereClause: String,
                   nonAggFields: List[String],
                   havingClause: String /*,
                   comparisionWhereClause: ComparisionWhereClause,
                   includeRankSubquery: Boolean*/): String = {

    val selectQuery = new StringBuilder("")
    var additionaldNonAggFields = List[String]()

    var mainSelectClause = new StringBuilder("")

    mainSelectClause.append(s" SELECT DISTINCT " +
      selectFieldList
        .map { fullColName =>
          if (recordedFieldAliases.contains(fullColName))
            s"$fullColName AS ${recordedFieldAliases.get(fullColName).get}"
          else fullColName
        }.mkString(",")
    )
    if (NuoEvaEnglishListener.isGrammarTestMode) mainSelectClause.append(s" FROM X")
    else mainSelectClause.append(s" FROM ${constructFromClause(entities)}")

    if (whereClause != null && whereClause.nonEmpty) {
      mainSelectClause.append(s" WHERE $whereClause")
    }


    if (nonAggFields != null && nonAggFields.nonEmpty) {
      mainSelectClause.append(s" GROUP BY ${(nonAggFields ++ additionaldNonAggFields).mkString(",")}")
    }

    if (havingClause != null && havingClause.nonEmpty) {
      mainSelectClause.append(s" HAVING $havingClause")
    }

    if (this.currAnalysisRecognitionData.RankSelectClause.nonEmpty) {
      selectQuery.append("WITH SRC AS (")
      selectQuery.append(mainSelectClause)
      selectQuery.append(s"), Rank_SRC AS (SELECT SRC.*, ")
      selectQuery.append(
        this.currAnalysisRecognitionData
          .RankSelectClause
          .mkString(",")
      )
      selectQuery.append(s" FROM SRC)")
      if (this.currAnalysisRecognitionData.RankWhereClause.isDefined
        && this.currAnalysisRecognitionData.RankWhereClause.get.trim.nonEmpty
      ) {
        selectQuery.append(s" SELECT * FROM RANK_SRC WHERE ${this.currAnalysisRecognitionData.RankWhereClause.get.trim}")
      } else {
        selectQuery.append(s" SELECT * FROM RANK_SRC")
      }
    } else {
      selectQuery.append(mainSelectClause)
    }
    selectQuery.toString()
  }

  def constructFromClause(recognizedEntities: List[String]): String = {
    NuoQuestionRecognizer.getNuoRelsForRecognizedEntities(recognizedEntities, this)
      .map { ele =>
        NuoRelationship(
          leftEntityName =
            if (ele.leftEntityName != null && ele.leftEntityName.trim.nonEmpty)
              s"`${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele.leftEntityName}`"
            else null,
          rightEntityName =
            if (ele.rightEntityName != null && ele.rightEntityName.trim.nonEmpty)
              s"`${NuoEvaEnglishListener.nuoTenantDetails.userDefaultDatasetName}`.`${ele.rightEntityName}`"
            else null,
          intermediateEntities = ele.intermediateEntities,
          commonFields = ele.commonFields,
          joinClause = ele.joinClause,
          isVerified = ele.isVerified
        )
      }
      .map(ele => s"${if (ele.leftEntityName != null && ele.leftEntityName.trim.nonEmpty) " LEFT JOIN " else " "}${ele.rightEntityName}" +
        s" ${
          if (ele.joinClause != null && ele.joinClause.trim.nonEmpty) "ON " + ele.joinClause
          else ""
        }")
      .mkString(" ")
  }

  override def exitFieldList(ctx: FieldListContext): Unit = {

    val fieldList = ctx.field().asScala.toList

    ctx.values =
      (
        ctx.field().asScala
          .map { field =>
            List(
              field.fieldValue,
              field.fieldType.toString,
              field.datatype,
              field.fieldAlias
            ).asJava
          }
          ++ ctx.rankField().asScala
          .map { field =>
            List(
              field.fieldValue,
              field.fieldType.toString,
              field.datatype,
              field.fieldAlias
            ).asJava
          }
          ++ this.recognizedRankOrdFields.map(_.asJava)
          ++ this.recognizedRankPartFields.map(_.asJava)
        ).distinct.asJava
  }

  override def exitCriteriaParen(ctx: CriteriaParenContext): Unit = {

    val criteriaClauseObj = ctx.criteriaClause()

    ctx.defaultValue = "(" + criteriaClauseObj.defaultValue + ")"
    ctx.sumCaseValue = "(" + criteriaClauseObj.sumCaseValue + ")"
    ctx.fieldType = criteriaClauseObj.fieldType
  }

  override def exitCriteriaAnd(ctx: CriteriaAndContext): Unit = {

    val criteriaClauseObj = ctx.criteriaClause().asScala


    ctx.defaultValue = criteriaClauseObj.map(_.defaultValue).filter(ele => ele != null && ele.trim.nonEmpty).mkString(s" AND ")
    ctx.sumCaseValue = criteriaClauseObj.map(_.sumCaseValue).filter(ele => ele != null && ele.trim.nonEmpty).mkString(s" AND ")
    ctx.fieldType = criteriaClauseObj.map(_.fieldType).distinct.product
  }

  override def exitCriteriaOr(ctx: CriteriaOrContext): Unit = {

    val criteriaClauseObj = ctx.criteriaClause().asScala


    ctx.defaultValue = criteriaClauseObj.map(_.defaultValue).filter(ele => ele != null && ele.trim.nonEmpty).mkString(s" OR ")
    ctx.sumCaseValue = criteriaClauseObj.map(_.sumCaseValue).filter(ele => ele != null && ele.trim.nonEmpty).mkString(s" OR ")
    ctx.fieldType = criteriaClauseObj.map(_.fieldType).distinct.product

    this.currAnalysisRecognitionData.DoesOrCriteriaExist = Some(true)
  }

  def getConditionalCountExp(condition: String): String = {
    if (condition != null && condition.trim.nonEmpty)
      s"SUM( CASE WHEN $condition THEN 1 ELSE 0 END) > 0"
    else ""
  }

  override def exitCriteriaNegative(ctx: CriteriaNegativeContext): Unit = {

    val criteriaClauseObj = ctx.criteriaClause()

    ctx.fieldType = criteriaClauseObj.fieldType

    ctx.defaultValue = s"NOT(${criteriaClauseObj.defaultValue})"
    ctx.sumCaseValue = s"NOT(${criteriaClauseObj.sumCaseValue})"
  }

  override def exitCriteriaPositive(ctx: CriteriaPositiveContext): Unit = {

    val criteriaClauseObj = ctx.criteriaClause()

    ctx.fieldType = criteriaClauseObj.fieldType

    ctx.defaultValue = criteriaClauseObj.defaultValue
    ctx.sumCaseValue = criteriaClauseObj.sumCaseValue
  }

  //
  //  override def exitCriteriaWhereNegative(ctx: CriteriaWhereNegativeContext): Unit = {
  //
  //    ctx.fieldType = ctx.whereClauseContent().fieldType
  //
  //    val criteriaText = if (ctx.whereClauseContent() != null) s"NOT(${ctx.whereClauseContent().value})"
  //    else "?????"
  //
  //    ctx.defaultValue = criteriaText
  //    ctx.sumCaseValue = if (ctx.fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG != 0) getConditionalCountExp(condition = criteriaText) else criteriaText
  //
  //  }

  override def exitCriteriaWhereOrphanNegative(ctx: CriteriaWhereOrphanNegativeContext): Unit = {

    ctx.fieldType = ctx.whereClauseOrphanContent().fieldType

    val criteriaText = if (ctx.whereClauseOrphanContent() != null) s"NOT(${ctx.whereClauseOrphanContent().value})"
    else "?????"

    ctx.defaultValue = criteriaText
    ctx.sumCaseValue = if (ctx.fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG != 0) getConditionalCountExp(condition = criteriaText) else criteriaText

  }

  //
  //
  //  override def exitCriteriaWhereComparision(ctx: CriteriaWhereComparisionContext): Unit = {
  //
  //    ctx.fieldType = ctx.whereClauseComparisionContent().fieldType
  //
  //    val whereClauseText = if (ctx.whereClauseComparisionContent() != null) ctx.whereClauseComparisionContent().value
  //    else "?????"
  //
  //    ctx.defaultValue = whereClauseText
  //    ctx.sumCaseValue = if (ctx.fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG != 0) getConditionalCountExp(condition = whereClauseText) else whereClauseText
  //
  //  }

  override def exitCriteriaWhereDefault(ctx: CriteriaWhereDefaultContext): Unit = {

    ctx.fieldType = ctx.whereClauseContent().fieldType

    val whereClauseText = if (ctx.whereClauseContent() != null) ctx.whereClauseContent().value
    else "?????"

    ctx.defaultValue = whereClauseText
    ctx.sumCaseValue = if (ctx.fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG != 0) getConditionalCountExp(condition = whereClauseText) else whereClauseText

  }

  override def exitCriteriaWhereOrphanDefault(ctx: CriteriaWhereOrphanDefaultContext): Unit = {

    ctx.fieldType = ctx.whereClauseOrphanContent().fieldType

    val whereClauseText = if (ctx.whereClauseOrphanContent() != null) ctx.whereClauseOrphanContent().value
    else "?????"

    ctx.defaultValue = whereClauseText
    ctx.sumCaseValue = if (ctx.fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG != 0) getConditionalCountExp(condition = whereClauseText) else whereClauseText

  }


  //  override def exitTimeFrameSince(ctx: TimeFrameSinceContext): Unit = {
  //    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_DURATION_SINCE
  //
  //    ctx.fieldType *= ctx.dateField().fieldType
  //    ctx.availableDateParts = ctx.dateField().availableDateParts
  //    ctx.value = ctx.dateField().value
  //  }
  //
  //  override def exitTimeFrameUntil(ctx: TimeFrameUntilContext): Unit = {
  //    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_DURATION_UNTIL
  //    ctx.fieldType *= ctx.dateField().fieldType
  //    ctx.availableDateParts = ctx.dateField().availableDateParts
  //    ctx.value = ctx.dateField().value
  //  }

  //  override def exitTimeFrameFor(ctx: TimeFrameForContext): Unit = {
  //    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_DURATION_FOR
  //    ctx.durationSeconds = ctx.durationField().durationSeconds
  //    if (NuoEvaEnglishListener.isGrammarTestMode) ctx.value = ctx.durationField().getText
  //  }


  case class RecognitionResult(ExpressionText: String,
                               QuestionFields: List[NuoQuestionField])

  def recognizeNumberField(field: FieldContext,
                           isFloatingPoint: Boolean): String = {

    if (NuoEvaEnglishListener.isGrammarTestMode) {
      field.fieldValue
    } else {
      if (NuoDataType.String.equalsIgnoreCase(field.datatype.toUpperCase)) {
        s"SAFE_CAST(${field.fieldValue} AS FLOAT64)"
      } else if (isFloatingPoint &&
        List(NuoDataType.Int64,
          NuoDataType.Integer)
          .map(_.toUpperCase)
          .contains(field.datatype.toUpperCase)) {
        s"SAFE_CAST(${field.fieldValue} AS FLOAT64)"
      } else {
        s"${field.fieldValue}"
      }
    }
  }

  override def exitWhereOrphanBetween(ctx: WhereOrphanBetweenContext): Unit = {

    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
    //    }

    val numbers = ctx.numberField().asScala.toList

    val recognitionResult = recognizeNumberField(ctx.field(), numbers.exists(_.value.contains(".")))

    ctx.fieldType = ctx.field().fieldType
    ctx.value =
      if (ctx.KWD_OUTSIDE() != null)
        s"($recognitionResult < ${numbers.head.value} OR $recognitionResult > ${numbers(1).value})"
      else
        s"$recognitionResult BETWEEN ${numbers.head.value} AND ${numbers(1).value}"
  }


  override def exitWhereOrphanGtEq(ctx: WhereOrphanGtEqContext): Unit = {

    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
    //    }
    val aggFunction = ctx.numberField().aggFunction

    val recognitionResult = recognizeNumberField(ctx.field(), ctx.numberField().value.contains("."))
    if (aggFunction != null && aggFunction.nonEmpty) {
      ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_AGG
      ctx.value = s"$aggFunction($recognitionResult) >= ${ctx.numberField().value}"
    } else {
      ctx.fieldType = ctx.field().fieldType
      ctx.value = s"$recognitionResult >= ${ctx.numberField().value}"
    }

  }

  override def exitWhereOrphanLtEq(ctx: WhereOrphanLtEqContext): Unit = {
    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
    //    }

    val aggFunction = ctx.numberField().aggFunction

    val recognitionResult = recognizeNumberField(ctx.field(), ctx.numberField().value.contains("."))

    if (aggFunction != null && aggFunction.nonEmpty) {
      ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_AGG
      ctx.value = s"$aggFunction($recognitionResult) <= ${ctx.numberField().value}"
    } else {
      ctx.fieldType = ctx.field().fieldType
      ctx.value = s"$recognitionResult <= ${ctx.numberField().value}"
    }
  }

  override def exitWhereOrphanGt(ctx: WhereOrphanGtContext): Unit = {

    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
    //    }

    val aggFunction = ctx.numberField().aggFunction

    val recognitionResult = recognizeNumberField(ctx.field(), ctx.numberField().value.contains("."))

    if (aggFunction != null && aggFunction.nonEmpty) {
      ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_AGG
      ctx.value = s"$aggFunction($recognitionResult) > ${ctx.numberField().value}"
    } else {
      ctx.fieldType = ctx.field().fieldType
      ctx.value = s"$recognitionResult > ${ctx.numberField().value}"
    }
  }

  override def exitWhereOrphanLt(ctx: WhereOrphanLtContext): Unit = {
    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
    //    }

    val aggFunction = ctx.numberField().aggFunction

    val recognitionResult = recognizeNumberField(ctx.field(), ctx.numberField().value.contains("."))

    if (aggFunction != null && aggFunction.nonEmpty) {
      ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_AGG
      ctx.value = s"$aggFunction($recognitionResult) < ${ctx.numberField().value}"
    } else {
      ctx.fieldType = ctx.field().fieldType
      ctx.value = s"$recognitionResult < ${ctx.numberField().value}"
    }
  }


  override def exitWhereOrphanEq(ctx: WhereOrphanEqContext): Unit = {
    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //
    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
    //
    //    }

    val aggFunction = ctx.numberField().aggFunction

    val recognitionResult = recognizeNumberField(ctx.field(), ctx.numberField().value.contains("."))

    if (aggFunction != null && aggFunction.nonEmpty) {
      ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_AGG
      ctx.value = s"$aggFunction($recognitionResult) = ${ctx.numberField().value}"
    } else {
      ctx.fieldType = ctx.field().fieldType
      val op = if (ctx.negativeVerb() != null) "<>" else "="
      ctx.value = s"$recognitionResult $op ${ctx.numberField().value}"
    }
  }


  /*
  *
  * 04FEB2018:Pulkit: Following function is not likely to execute and only serves the purpose of future reference.
  * Because Negative Where Orphan will have lower precedence than Negative NuoCriteria Orphan
  * Therefore it is not required to be handled in the current version of grammar
  * */
  //  override def exitWhereOrphanNotEq(ctx: WhereOrphanNotEqContext): Unit = {
  //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
  //    if (knownCriterion.isDefined) {
  //
  //      ctx.fieldType = knownCriterion.get.FieldType.toInt
  //      ctx.value = knownCriterion.get.CriterionText
  //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
  //    } else {
  //
  //      val aggFunction = ctx.numberField().aggFunction
  //
  //      val recognitionResult = recognizeNumberField(ctx.field(), ctx.numberField().value.contains("."))
  //
  //      if (aggFunction != null && aggFunction.nonEmpty) {
  //        ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_AGG
  //        ctx.value = s"$aggFunction($recognitionResult) <> ${ctx.numberField().value}"
  //      } else {
  //        ctx.fieldType = ctx.field().fieldType
  //        ctx.value = s"$recognitionResult <> ${ctx.numberField().value}"
  //      }
  //    }
  //  }

  //  def recognizeWhereField(fieldContent: FieldContentContext): NuoField = {
  //
  //    val fieldType = fieldContent.fieldType
  //    val fieldValue = fieldContent.fieldValue
  //
  //    val recognizedField = if (fieldType % NuoEvaEnglishListener.FIELD_TYPE_INT == 0) NuoField("", "", fieldValue, NuoDataTypeHandler.NuoDataType.Int64)
  //    else if (fieldType % NuoEvaEnglishListener.FIELD_TYPE_FLOAT == 0) NuoField("", "", fieldValue, NuoDataTypeHandler.NuoDataType.Float64)
  //    else if (fieldType % NuoEvaEnglishListener.FIELD_TYPE_DATE == 0) NuoField("", "", fieldValue, NuoDataTypeHandler.NuoDataType.Date)
  //    else if (fieldType % NuoEvaEnglishListener.FIELD_TYPE_TIME == 0) NuoField("", "", fieldValue, NuoDataTypeHandler.NuoDataType.Time)
  //    else if (fieldType % NuoEvaEnglishListener.FIELD_TYPE_TIMESTAMP == 0) NuoField("", "", fieldValue, NuoDataTypeHandler.NuoDataType.Timestamp)
  //    else if (fieldType % NuoEvaEnglishListener.FIELD_TYPE_DEFAULT == 0
  //      || fieldType % NuoEvaEnglishListener.FIELD_TYPE_QUOTED == 0
  //      || fieldType % NuoEvaEnglishListener.FIELD_TYPE_RANK_OVER == 0) {
  //      if (NuoEvaEnglishListener.isGrammarTestMode) {
  //        if (fieldType % NuoEvaEnglishListener.FIELD_TYPE_QUOTED == 0)
  //          NuoField("", "", fieldValue, NuoDataTypeHandler.NuoDataType.String)
  //        else NuoField("", "X", fieldValue, NuoDataTypeHandler.NuoDataType.String)
  //      } else {
  //        if (fieldType % NuoEvaEnglishListener.FIELD_TYPE_QUOTED == 0)
  //          NuoField("", "", s"'$fieldValue'", NuoDataTypeHandler.NuoDataType.String)
  //        else NuoField("", fieldContent.tableName, fieldValue, NuoDataTypeHandler.NuoDataType.String)
  //
  //      }
  //    } else NuoField("", "", fieldValue, NuoDataTypeHandler.NuoDataType.String)
  //
  //    recognizedFields.put(fieldContent.fieldText, List(recognizedField))
  //    recognizedField
  //  }

  val RECOGNIZED_WHERE_TYPE_OTHER = 2
  val RECOGNIZED_WHERE_TYPE_LEFT_STRING_FIELD = 3
  val RECOGNIZED_WHERE_TYPE_RIGHT_STRING_FIELD = 5
  val RECOGNIZED_WHERE_TYPE_LEFT_LITERAL = 7
  val RECOGNIZED_WHERE_TYPE_RIGHT_LITERAL = 11

  case class RecognizedWhereClause(RecognizedExps: List[String],
                                   RecognizedQuestionFields: List[NuoQuestionField],
                                   RecognizedWhereType: Int)

  def recognizeWhereClause(ruleText: String,
                           leftGrammarField: NuoGrammarField,
                           rightGrammarField1: NuoGrammarField,
                           rightGrammarField2: NuoGrammarField,
                           dateGrammarField1: NuoGrammarField,
                           dateGrammarField2: NuoGrammarField): RecognizedWhereClause = {

    if (NuoEvaEnglishListener.isGrammarTestMode) {
      RecognizedWhereClause(
        RecognizedExps = List(
          leftGrammarField,
          rightGrammarField1,
          rightGrammarField2,
          dateGrammarField1,
          dateGrammarField2
        )
          .filterNot(_ == null)
          .map(_.FieldValue),
        RecognizedQuestionFields = List(),
        RecognizedWhereType = RECOGNIZED_WHERE_TYPE_OTHER)
    } else {

      //      val valueAndDataType = mutable.ArrayBuffer[(String, String)]()
      val recognizedFields = mutable.ArrayBuffer[NuoField]()

      val expGrammarFields = List(leftGrammarField, rightGrammarField1, rightGrammarField2, dateGrammarField1, dateGrammarField2)
      val castedValues = NuoDataTypeHandler.castDataTypes(grammarFields = expGrammarFields, expectedDataType = "", nuoGrammarListener = this)

      RecognizedWhereClause(RecognizedExps = castedValues._1,
        List(NuoQuestionField(leftGrammarField.FieldText, NuoField("", "", leftGrammarField.FieldValue, leftGrammarField.Datatype)),
          if (rightGrammarField1 != null)
            NuoQuestionField(
              rightGrammarField1.FieldText,
              NuoField("", "", rightGrammarField1.FieldValue, rightGrammarField1.Datatype)
            )
          else null,
          if (rightGrammarField2 != null)
            NuoQuestionField(
              rightGrammarField2.FieldText,
              NuoField("", "", rightGrammarField2.FieldValue, rightGrammarField2.Datatype)
            )
          else null,
          if (dateGrammarField1 != null)
            NuoQuestionField(
              dateGrammarField1.FieldText,
              NuoField("", "", dateGrammarField1.FieldValue, dateGrammarField1.Datatype)
            )
          else null,
          if (dateGrammarField2 != null)
            NuoQuestionField(
              dateGrammarField2.FieldText,
              NuoField("", "", dateGrammarField2.FieldValue, dateGrammarField2.Datatype)
            )
          else null)
          .filter(_ != null),
        getRecognizedWhereType(castedValues._2,
          leftGrammarField,
          rightGrammarField1)
      )
    }
  }

  def getRecognizedWhereType(highestOrderDataType: String,
                             leftGrammarField: NuoGrammarField,
                             rightGrammarField: NuoGrammarField): Int = {

    val isLeftStringField = isStringFieldComparision(highestOrderDataType, leftGrammarField)
    val isRightStringField = isStringFieldComparision(highestOrderDataType, rightGrammarField)

    val isLeftLiteral = isLeftStringField && isStringLiteral(leftGrammarField)
    val isRightLiteral = isRightStringField && isStringLiteral(rightGrammarField)

    if (isLeftLiteral) RECOGNIZED_WHERE_TYPE_LEFT_LITERAL
    else if (isRightLiteral) RECOGNIZED_WHERE_TYPE_RIGHT_LITERAL
    else if (isLeftStringField) RECOGNIZED_WHERE_TYPE_LEFT_STRING_FIELD
    else if (isRightStringField) RECOGNIZED_WHERE_TYPE_RIGHT_STRING_FIELD
    else RECOGNIZED_WHERE_TYPE_OTHER
  }

  def isStringFieldComparision(highestOrderDataType: String,
                               expField: NuoGrammarField): Boolean = {

    highestOrderDataType.equalsIgnoreCase(NuoDataType.String) &&
      (expField != null &&
        (expField.FieldType % NuoEvaEnglishListener.FIELD_TYPE_DEFAULT == 0))
  }

  def isStringLiteral(nuoGrammarField: NuoGrammarField): Boolean = {

    nuoGrammarField.Datatype.equalsIgnoreCase(NuoDataType.String) && nuoGrammarField != null &&
      (nuoGrammarField.EntityNames == null || nuoGrammarField.EntityNames.size() == 0)
  }

  //
  //  override def exitWhereNegativeTime(ctx: WhereNegativeTimeContext): Unit = {
  //
  //    ctx.fieldType = ctx.field().fieldType
  //
  //    val timeFrameClause = ctx.timeFrameClause()
  //
  //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(timeFrameClause.getText.trim))
  //    val timeFrameClauseText = if (NuoEvaEnglishListener.isGrammarTestMode) ctx.field().fieldValue + " AND " + timeFrameClause.value
  //    else if (knownCriterion.isDefined) {
  //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
  //      knownCriterion.get.CriterionText
  //    } else {
  //
  //      var dateField = NuoQuestionRecognizer.recognizeField(
  //        fieldText = ctx.field().fieldValue,
  //        expectedDataTypes = null,
  //        expectedEntity = null,
  //        expectedFields = List(),
  //        allowLiteralChoice = false,
  //        allowQuestionChoice = true,
  //        nuoGrammarListener = this
  //      )
  //      if (dateField.isEmpty) {
  //        NuoRequestHandler.reportErrorToUser(new Exception(s"I could not recognize column for [${ctx.field().fieldValue}]."))
  //        throw new Exception("Unreachable Code statement")
  //      }
  //      val timeFrameTextLocal = recognizeTimeFrameDurationExp(timeFrameClause.getText,
  //        timeFrameClause.value,
  //        timeFrameClause.fieldType,
  //        timeFrameClause.durationSeconds,
  //        timeFrameClause.availableDateParts.asScala.toList,
  //        dateField.get,
  //        ctx.field().aggFunction)
  //
  //      this.currAnalysisRecognitionData.Criteria +:= NuoCriteria(ctx.getText,
  //        ctx.fieldType.toString,
  //        s"NOT($timeFrameTextLocal)",
  //        List(NuoQuestionField(ctx.field().fieldValue, dateField.get)))
  //      s"NOT($timeFrameTextLocal)"
  //    }
  //    ctx.value = timeFrameClauseText
  //  }
  //
  //  override def exitWhereDefaultTime(ctx: WhereDefaultTimeContext): Unit = {
  //
  //
  //    ctx.fieldType = ctx.field().fieldType
  //
  //    val timeFrameClause = ctx.timeFrameClause()
  //
  //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(timeFrameClause.getText.trim))
  //    val timeFrameClauseText = if (NuoEvaEnglishListener.isGrammarTestMode) ctx.field().fieldValue + " AND " + timeFrameClause.value
  //    else if (knownCriterion.isDefined) {
  //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
  //      knownCriterion.get.CriterionText
  //    } else {
  //
  //      var dateField = NuoQuestionRecognizer.recognizeField(
  //        fieldText = ctx.field().fieldValue,
  //        expectedDataTypes = null,
  //        expectedEntity = null,
  //        expectedFields = List(),
  //        allowLiteralChoice = false,
  //        allowQuestionChoice = true,
  //        nuoGrammarListener = this
  //      )
  //      if (dateField.isEmpty) {
  //        NuoRequestHandler.reportErrorToUser(new Exception(s"I could not recognize column for [${ctx.field().fieldValue}]."))
  //        throw new Exception("Unreachable Code statement")
  //      }
  //      val timeFrameTextLocal = recognizeTimeFrameDurationExp(timeFrameClause.getText,
  //        timeFrameClause.value,
  //        timeFrameClause.fieldType,
  //        timeFrameClause.durationSeconds,
  //        timeFrameClause.availableDateParts.asScala.toList,
  //        dateField.get,
  //        ctx.field().aggFunction)
  //
  //      this.currAnalysisRecognitionData.Criteria +:= NuoCriteria(ctx.getText,
  //        ctx.fieldType.toString,
  //        timeFrameTextLocal,
  //        List(NuoQuestionField(ctx.field().fieldValue, dateField.get)))
  //      timeFrameTextLocal
  //    }
  //    ctx.value = timeFrameClauseText
  //
  //  }

  def recognizeTimeFrameDurationExp(ruleText: String,
                                    tfValue: String,
                                    tfFieldType: Int,
                                    tfDurationSeconds: Int,
                                    referenceField: FieldContext): String = {

    if (NuoEvaEnglishListener.isGrammarTestMode) {
      if (tfFieldType % NuoEvaEnglishListener.FIELD_TYPE_DURATION_PAST == 0) {
        s"TIMESTAMP_DIFF(CURRENT_TIMESTAMP(),$ruleText,SECOND) = $tfDurationSeconds"
      } else {
        s"TIMESTAMP_DIFF($ruleText,CURRENT_TIMESTAMP(),SECOND) = $tfDurationSeconds"
      }
    } else {
      val dateGrammarField =
        NuoGrammarField(
          FieldText = referenceField.fieldText,
          FieldType = referenceField.fieldType,
          FieldValue = referenceField.fieldValue,
          DateParts = NuoDateParts(
            referenceField.dd,
            referenceField.mm,
            referenceField.yyyy,
            referenceField.hh,
            referenceField.MM,
            referenceField.ss,
            referenceField.SSS,
            referenceField.tz
          ),
          Datatype = referenceField.datatype,
          EntityNames = referenceField.entityNames
        )

      val dateFieldType = referenceField.datatype

      var expValues = NuoDataTypeHandler.castDataTypes(List(dateGrammarField),
        expectedDataType = if (NuoDataTypeHandler.isDateType(referenceField.datatype))
          null else NuoDataType.Timestamp,
        this)._1


      var dateExp = if (dateFieldType.nonEmpty && NuoDataTypeHandler.isDateType(dateFieldType)) {
        expValues.head
      } else {
        NuoRequestHandler.reportErrorToUser(new Exception(s"I could not understand the column ${referenceField.fieldValue}."))
        throw new Exception("Unreachable Code statement")
      }

      val expressionText =

        if (tfFieldType % NuoEvaEnglishListener.FIELD_TYPE_DURATION_PAST == 0) {

          if (dateFieldType.equalsIgnoreCase(NuoDataType.Timestamp))
            s"TIMESTAMP_DIFF(CURRENT_TIMESTAMP(),$dateExp,SECOND) = $tfDurationSeconds"
          else if (dateFieldType.equalsIgnoreCase(NuoDataType.Time))
            s"TIME_DIFF(CURRENT_TIME(),$dateExp,SECOND) = $tfDurationSeconds"
          else if (dateFieldType.equalsIgnoreCase(NuoDataType.Date))
            s"DATE_DIFF(CURRENT_DATE(),$dateExp,DAY) = ${tfDurationSeconds / 3600.0 / 24.0}"
          else {
            NuoRequestHandler.reportErrorToUser(new Exception(s"I could not create the TimeFrame criteria with data type as ${dateFieldType} for expression $ruleText."))
            throw new Exception("Unreachable Code statement")
          }


        } else /* if (tfFieldType % NuoEvaEnglishListener.FIELD_TYPE_DURATION_NEXT == 0)*/ {

          if (dateFieldType.equalsIgnoreCase(NuoDataType.Timestamp))
            s"TIMESTAMP_DIFF($dateExp,CURRENT_TIMESTAMP(),SECOND) = $tfDurationSeconds"
          else if (dateFieldType.equalsIgnoreCase(NuoDataType.Time))
            s"TIME_DIFF($dateExp,CURRENT_TIME(),SECOND) = $tfDurationSeconds"
          else if (dateFieldType.equalsIgnoreCase(NuoDataType.Date))
            s"DATE_DIFF($dateExp,CURRENT_DATE(),DAY) = ${tfDurationSeconds / 3600.0 / 24.0}"
          else {
            NuoRequestHandler.reportErrorToUser(new Exception(s"I could not create the TimeFrame criteria with data type as ${dateFieldType} for expression $ruleText."))
            throw new Exception("Unreachable Code statement")
          }
        }

      expressionText
    }
  }

  override def exitWhereBetween(ctx: WhereBetweenContext): Unit = {
    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
    //    }

    val fields = ctx.field().asScala.toList
    ctx.fieldType = fields.map(_.fieldType).distinct.product

    val recognitionResult = recognizeWhereClause(ruleText = ctx.getText,
      leftGrammarField = NuoGrammarField(
        FieldText = fields(0).fieldText,
        FieldType = fields(0).fieldType,
        FieldValue = fields(0).fieldValue,
        DateParts = NuoDateParts(
          fields(0).dd,
          fields(0).mm,
          fields(0).yyyy,
          fields(0).hh,
          fields(0).MM,
          fields(0).ss,
          fields(0).SSS,
          fields(0).tz
        ),
        Datatype = fields(0).datatype,
        EntityNames = fields(0).entityNames),
      rightGrammarField1 = NuoGrammarField(
        FieldText = fields(1).fieldText,
        FieldType = fields(1).fieldType,
        FieldValue = fields(1).fieldValue,
        DateParts = NuoDateParts(
          fields(1).dd,
          fields(1).mm,
          fields(1).yyyy,
          fields(1).hh,
          fields(1).MM,
          fields(1).ss,
          fields(1).SSS,
          fields(1).tz
        ),
        Datatype = fields(1).datatype,
        EntityNames = fields(1).entityNames),
      rightGrammarField2 = NuoGrammarField(
        FieldText = fields(2).fieldText,
        FieldType = fields(2).fieldType,
        FieldValue = fields(2).fieldValue,
        DateParts = NuoDateParts(
          fields(2).dd,
          fields(2).mm,
          fields(2).yyyy,
          fields(2).hh,
          fields(2).MM,
          fields(2).ss,
          fields(2).SSS,
          fields(2).tz
        ),
        Datatype = fields(2).datatype,
        EntityNames = fields(2).entityNames),
      dateGrammarField1 = null,
      dateGrammarField2 = null)

    val fieldValues = recognitionResult.RecognizedExps

    val field1 = fieldValues.head

    val field2 = fieldValues(1)

    val field3 = fieldValues(2)

    ctx.value =
      if (ctx.KWD_OUTSIDE() != null)
        s"($field1 < $field2 OR $field1 > $field3)"
      else
        s"$field1 BETWEEN $field2 AND $field3"
    if (ctx.KWD_WITHOUT() != null || ctx.prefixNegVerb != null) {
      ctx.value = s"NOT(${ctx.value})"
    }
  }

  override def exitWhereGtEq(ctx: WhereGtEqContext): Unit = {
    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
    //    } else {

    ctx.fieldType = ctx.field().asScala.map(_.fieldType).distinct.product

    val fields = ctx.field().asScala
    val leftGrammarField = NuoGrammarField(
      FieldText = fields(0).fieldText,
      FieldType = fields(0).fieldType,
      FieldValue = fields(0).fieldValue,
      DateParts = NuoDateParts(
        fields(0).dd,
        fields(0).mm,
        fields(0).yyyy,
        fields(0).hh,
        fields(0).MM,
        fields(0).ss,
        fields(0).SSS,
        fields(0).tz
      ),
      Datatype = fields(0).datatype,
      EntityNames = fields(0).entityNames)

    var conditions =
      fields
        .tail
        .map { rightField =>
          var rightGrammarField1 = NuoGrammarField(
            FieldText = rightField.fieldText,
            FieldType = rightField.fieldType,
            FieldValue = rightField.fieldValue,
            DateParts = NuoDateParts(
              rightField.dd,
              rightField.mm,
              rightField.yyyy,
              rightField.hh,
              rightField.MM,
              rightField.ss,
              rightField.SSS,
              rightField.tz
            ),
            Datatype = fields(1).datatype,
            EntityNames = fields(1).entityNames)

          val recognizedClause = recognizeWhereClause(ruleText = ctx.getText,
            leftGrammarField = leftGrammarField,
            rightGrammarField1 = rightGrammarField1,
            rightGrammarField2 = null,
            dateGrammarField1 = null,
            dateGrammarField2 = null)

          val fieldValues = recognizedClause.RecognizedExps
          val field1 = fieldValues.head
          val field2 = fieldValues(1)

          s"$field1 >= $field2"
        }
    ctx.value =
      if (conditions.length > 1)
        conditions.mkString("(", " OR ", ")")
      else conditions.head
    if (ctx.KWD_WITHOUT() != null || ctx.prefixNegVerb != null) {
      ctx.value = s"NOT(${ctx.value})"
    }
    if (ctx.criteriaClause() != null && ctx.criteriaClause().asScala.length > 0) {

      val conditionExpResult = getConditionalWhereValueAndType(ctx.value, ctx.fieldType, ctx.criteriaClause())
      ctx.value = conditionExpResult._1
      ctx.fieldType = conditionExpResult._2

    }
  }

  def getConditionalWhereValueAndType(whereClauseValue: String,
                                      whereClauseFieldType: Int,
                                      criteriaClauseList: java.util.List[CriteriaClauseContext]): (String, Int) = {

    var finalFieldType = criteriaClauseList.asScala.map(_.fieldType).distinct.product * whereClauseFieldType

    var updWhereClauseValue = whereClauseValue
    var updCriteraClause = criteriaClauseList.asScala.map(_.defaultValue).mkString(" OR ")
    if (
      finalFieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG == 0

    ) {
      if (whereClauseFieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG != 0) {
        updWhereClauseValue = getConditionalCountExp(whereClauseValue)
      }
      updCriteraClause = criteriaClauseList.asScala.map(_.sumCaseValue).mkString(" OR ")
    }
    (s"$updWhereClauseValue OR " + updCriteraClause, finalFieldType)
  }

  override def exitWhereLtEq(ctx: WhereLtEqContext): Unit = {
    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
    //    }

    ctx.fieldType = ctx.field().asScala.map(_.fieldType).distinct.product

    val fields = ctx.field().asScala

    val leftGrammarField = NuoGrammarField(
      FieldText = fields(0).fieldText,
      FieldType = fields(0).fieldType,
      FieldValue = fields(0).fieldValue,
      DateParts = NuoDateParts(
        fields(0).dd,
        fields(0).mm,
        fields(0).yyyy,
        fields(0).hh,
        fields(0).MM,
        fields(0).ss,
        fields(0).SSS,
        fields(0).tz
      ),
      Datatype = fields(0).datatype,
      EntityNames = fields(0).entityNames)

    val conditions =
      fields
        .tail
        .map { rightField =>
          var rightGrammarField1 = NuoGrammarField(
            FieldText = rightField.fieldText,
            FieldType = rightField.fieldType,
            FieldValue = rightField.fieldValue,
            DateParts = NuoDateParts(
              rightField.dd,
              rightField.mm,
              rightField.yyyy,
              rightField.hh,
              rightField.MM,
              rightField.ss,
              rightField.SSS,
              rightField.tz
            ),
            Datatype = fields(1).datatype,
            EntityNames = fields(1).entityNames)

          val recognizedClause = recognizeWhereClause(ruleText = ctx.getText,
            leftGrammarField = leftGrammarField,
            rightGrammarField1 = rightGrammarField1,
            rightGrammarField2 = null,
            dateGrammarField1 = null,
            dateGrammarField2 = null)

          val fieldValues = recognizedClause.RecognizedExps
          val field1 = fieldValues.head
          val field2 = fieldValues(1)

          s"$field1 <= $field2"
        }
    ctx.value =
      if (conditions.length > 1)
        conditions.mkString("(", " OR ", ")")
      else conditions.head
    if (ctx.KWD_WITHOUT() != null || ctx.prefixNegVerb != null) {
      ctx.value = s"NOT(${ctx.value})"
    }
    if (ctx.criteriaClause() != null && ctx.criteriaClause().asScala.length > 0) {

      val conditionExpResult = getConditionalWhereValueAndType(ctx.value, ctx.fieldType, ctx.criteriaClause())
      ctx.value = conditionExpResult._1
      ctx.fieldType = conditionExpResult._2

    }
  }

  override def exitWhereLt(ctx: WhereLtContext): Unit = {
    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
    //    }

    ctx.fieldType = ctx.field().asScala.map(_.fieldType).distinct.product

    val fields = ctx.field().asScala
    val leftGrammarField = NuoGrammarField(
      FieldText = fields(0).fieldText,
      FieldType = fields(0).fieldType,
      FieldValue = fields(0).fieldValue,
      DateParts = NuoDateParts(
        fields(0).dd,
        fields(0).mm,
        fields(0).yyyy,
        fields(0).hh,
        fields(0).MM,
        fields(0).ss,
        fields(0).SSS,
        fields(0).tz
      ),
      Datatype = fields(0).datatype,
      EntityNames = fields(0).entityNames)

    val conditions =
      fields
        .tail
        .map { rightField =>
          var rightGrammarField1 = NuoGrammarField(
            FieldText = rightField.fieldText,
            FieldType = rightField.fieldType,
            FieldValue = rightField.fieldValue,
            DateParts = NuoDateParts(
              rightField.dd,
              rightField.mm,
              rightField.yyyy,
              rightField.hh,
              rightField.MM,
              rightField.ss,
              rightField.SSS,
              rightField.tz
            ),
            Datatype = fields(1).datatype,
            EntityNames = fields(1).entityNames)

          val recognizedClause = recognizeWhereClause(ruleText = ctx.getText,
            leftGrammarField = leftGrammarField,
            rightGrammarField1 = rightGrammarField1,
            rightGrammarField2 = null,
            dateGrammarField1 = null,
            dateGrammarField2 = null)

          val fieldValues = recognizedClause.RecognizedExps
          val field1 = fieldValues.head
          val field2 = fieldValues(1)

          s"$field1 < $field2"
        }
    ctx.value =
      if (conditions.length > 1)
        conditions.mkString("(", " OR ", ")")
      else conditions.head
    if (ctx.KWD_WITHOUT() != null || ctx.prefixNegVerb != null) {
      ctx.value = s"NOT(${ctx.value})"
    }
    if (ctx.criteriaClause() != null && ctx.criteriaClause().asScala.length > 0) {

      val conditionExpResult = getConditionalWhereValueAndType(ctx.value, ctx.fieldType, ctx.criteriaClause())
      ctx.value = conditionExpResult._1
      ctx.fieldType = conditionExpResult._2

    }
  }

  override def exitWhereGt(ctx: WhereGtContext): Unit = {
    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
    //    }

    val fields = ctx.field().asScala
    ctx.fieldType = fields.map(_.fieldType).distinct.product

    val leftGrammarField = NuoGrammarField(
      FieldText = fields(0).fieldText,
      FieldType = fields(0).fieldType,
      FieldValue = fields(0).fieldValue,
      DateParts = NuoDateParts(
        fields(0).dd,
        fields(0).mm,
        fields(0).yyyy,
        fields(0).hh,
        fields(0).MM,
        fields(0).ss,
        fields(0).SSS,
        fields(0).tz
      ),
      Datatype = fields(0).datatype,
      EntityNames = fields(0).entityNames)

    val conditions =
      fields
        .tail
        .map { rightField =>
          var rightGrammarField1 = NuoGrammarField(
            FieldText = rightField.fieldText,
            FieldType = rightField.fieldType,
            FieldValue = rightField.fieldValue,
            DateParts = NuoDateParts(
              rightField.dd,
              rightField.mm,
              rightField.yyyy,
              rightField.hh,
              rightField.MM,
              rightField.ss,
              rightField.SSS,
              rightField.tz
            ),
            Datatype = fields(1).datatype,
            EntityNames = fields(1).entityNames)

          val recognizedClause = recognizeWhereClause(ruleText = ctx.getText,
            leftGrammarField = leftGrammarField,
            rightGrammarField1 = rightGrammarField1,
            rightGrammarField2 = null,
            dateGrammarField1 = null,
            dateGrammarField2 = null)

          val fieldValues = recognizedClause.RecognizedExps
          val field1 = fieldValues.head
          val field2 = fieldValues(1)

          s"$field1 > $field2"
        }
    ctx.value =
      if (conditions.length > 1)
        conditions.mkString("(", " OR ", ")")
      else conditions.head
    if (ctx.KWD_WITHOUT() != null || ctx.prefixNegVerb != null) {
      ctx.value = s"NOT(${ctx.value})"
    }
    if (ctx.criteriaClause() != null && ctx.criteriaClause().asScala.length > 0) {

      val conditionExpResult = getConditionalWhereValueAndType(ctx.value, ctx.fieldType, ctx.criteriaClause())
      ctx.value = conditionExpResult._1
      ctx.fieldType = conditionExpResult._2

    }
  }

  //  override def exitWhereDateNotEq(ctx: WhereDateNotEqContext): Unit = {
  //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
  //    if (knownCriterion.isDefined) {
  //
  //      ctx.fieldType = knownCriterion.get.FieldType.toInt
  //      ctx.value = knownCriterion.get.CriterionText
  //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
  //    } else {
  //
  //      ctx.fieldType = ctx.field().fieldType * ctx.dateField().fieldType
  //
  //      val recognizedClause = recognizeWhereClause(ruleText = ctx.getText,
  //        leftGrammarField = NuoGrammarField(
  //          ctx.field.fieldText,
  //          ctx.field.fieldType,
  //          ctx.field.fieldValue,
  //          ctx.field.availableDateParts,
  //          ctx.field.datatype,
  //          ctx.field.entityNames),
  //        rightGrammarField1 = null,
  //        rightGrammarField2 = null,
  //        dateGrammarField1 = NuoGrammarField(
  //          ctx.dateField().fieldText,
  //          ctx.dateField().fieldType,
  //          ctx.dateField().fieldValue,
  //          ctx.dateField().availableDateParts,
  //          ctx.dateField().datatype,
  //          null
  //        ),
  //        dateGrammarField2 = null,
  //        shouldTruncateDateTime = true)
  //      val fieldValues = recognizedClause.RecognizedExps
  //
  //      ctx.value = s"${fieldValues.head} <> ${fieldValues(1)}"
  //      this.currAnalysisRecognitionData.Criteria +:= NuoCriteria(ctx.getText, ctx.fieldType.toString, ctx.value, recognizedClause.RecognizedQuestionFields)
  //    }
  //  }

  override def exitWhereDateEq(ctx: WhereDateEqContext): Unit = {
    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
    //    }

    var leftExp = ""
    var rightExpValues =
      ctx.field().asScala
        .map { field =>

          ctx.fieldType = field.fieldType * ctx.dateField().fieldType
          val recognizedClause = recognizeWhereClause(ruleText = ctx.getText,
            leftGrammarField = NuoGrammarField(
              FieldText = field.fieldText,
              FieldType = field.fieldType,
              FieldValue = field.fieldValue,
              DateParts = NuoDateParts(
                field.dd,
                field.mm,
                field.yyyy,
                field.hh,
                field.MM,
                field.ss,
                field.SSS,
                field.tz
              ),
              Datatype = field.datatype,
              EntityNames = field.entityNames),
            rightGrammarField1 = null,
            rightGrammarField2 = null,
            dateGrammarField1 = NuoGrammarField(
              FieldText = ctx.dateField().fieldText,
              FieldType = ctx.dateField().fieldType,
              FieldValue = ctx.dateField().fieldValue,
              DateParts = NuoDateParts(
                ctx.dateField().dd,
                ctx.dateField().mm,
                ctx.dateField().yyyy,
                ctx.dateField().hh,
                ctx.dateField().MM,
                ctx.dateField().ss,
                ctx.dateField().SSS,
                ctx.dateField().tz
              ),
              Datatype = ctx.dateField().datatype,
              EntityNames = null
            ),
            dateGrammarField2 = null)
          val fieldValues = recognizedClause.RecognizedExps

          leftExp = fieldValues.head
          fieldValues(1)
        }
    if (rightExpValues.length > 1) {
      val op = if (ctx.subNegVerb != null) "NOT IN" else "IN"
      ctx.value = s"${leftExp} $op " + rightExpValues.mkString("(", ",", ")")

    } else {
      val op = if (ctx.subNegVerb != null) "<>" else "="
      ctx.value = s"${leftExp} $op ${rightExpValues.head}"
    }
    if (ctx.KWD_WITHOUT() != null || ctx.prefixNegVerb != null) {
      ctx.value = s"NOT(${ctx.value})"
    }
    if (ctx.criteriaClause() != null && ctx.criteriaClause().asScala.length > 0) {

      val conditionExpResult = getConditionalWhereValueAndType(ctx.value, ctx.fieldType, ctx.criteriaClause())
      ctx.value = conditionExpResult._1
      ctx.fieldType = conditionExpResult._2

    }
  }

  //
  //  override def exitWhereComparisionDateEq(ctx: WhereComparisionDateEqContext): Unit = {
  //    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
  //    //    if (knownCriterion.isDefined) {
  //    //
  //    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
  //    //      ctx.value = knownCriterion.get.CriterionText
  //    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
  //    //    }
  //
  //
  //    ctx.dateField().asScala.foreach { dateField =>
  //
  //      ctx.fieldType = ctx.field().fieldType * dateField.fieldType
  //
  //      val recognizedClause = recognizeWhereClause(ruleText = ctx.getText,
  //        leftGrammarField = NuoGrammarField(
  //          FieldText = ctx.field.fieldText,
  //          FieldType = ctx.field.fieldType,
  //          FieldValue = ctx.field.fieldValue,
  //          DateParts = NuoDateParts(
  //            ctx.field.dd,
  //            ctx.field.mm,
  //            ctx.field.yyyy,
  //            ctx.field.hh,
  //            ctx.field.MM,
  //            ctx.field.ss,
  //            ctx.field.SSS,
  //            ctx.field.tz
  //          ),
  //          Datatype = ctx.field.datatype,
  //          EntityNames = ctx.field.entityNames),
  //        rightGrammarField1 = null,
  //        rightGrammarField2 = null,
  //        dateGrammarField1 = NuoGrammarField(
  //          FieldText = dateField.fieldText,
  //          FieldType = dateField.fieldType,
  //          FieldValue = dateField.fieldValue,
  //          DateParts = NuoDateParts(
  //            dateField.dd,
  //            dateField.mm,
  //            dateField.yyyy,
  //            dateField.hh,
  //            dateField.MM,
  //            dateField.ss,
  //            dateField.SSS,
  //            dateField.tz
  //          ),
  //          Datatype = dateField.datatype,
  //          EntityNames = null
  //        ),
  //        dateGrammarField2 = null)
  //      val fieldValues = recognizedClause.RecognizedExps
  //
  //      var op =
  //        if (ctx.KWD_WITHOUT() != null ||
  //          (ctx.negativeVerb() != null && ctx.negativeVerb().asScala.nonEmpty)) {
  //          "<>"
  //        } else "="
  //      this.currAnalysisRecognitionData.ComparisionWhereClauses ::= ComparisionWhereClause(
  //        Expression = s"${fieldValues.head} $op ${fieldValues(1)}",
  //        Suffix = "_" + dateField.fieldText.trim.replaceAll("[^\\w]+", "_"),
  //        IsAggregated = ctx.fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG == 0,
  //        GroupName = fieldValues.head.trim.replaceAll("[^\\w]+", "_")
  //      )
  //    }
  //
  //    ctx.value = ""
  //
  //  }

  override def exitWhereDuration(ctx: WhereDurationContext): Unit = {
    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
    //    } else {

    ctx.fieldType = ctx.field().fieldType * ctx.durationField().fieldType

    val durationField = ctx.durationField()

    ctx.value = recognizeTimeFrameDurationExp(
      ruleText = ctx.field().getText,
      tfValue = durationField.value,
      tfFieldType = durationField.fieldType,
      tfDurationSeconds = durationField.durationSeconds,
      referenceField = ctx.field()
    )
    if (ctx.KWD_WITHOUT() != null || ctx.prefixNegVerb != null) {
      ctx.value = s"NOT(${ctx.value})"
    }


  }

  //
  //  override def exitWhereComparisionDuration(ctx: WhereComparisionDurationContext): Unit = {
  //    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
  //    //    if (knownCriterion.isDefined) {
  //    //
  //    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
  //    //      ctx.value = knownCriterion.get.CriterionText
  //    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
  //    //    } else {
  //
  //    ctx.durationField().asScala.foreach { durationField =>
  //
  //      ctx.fieldType = ctx.field().fieldType * durationField.fieldType
  //
  //      var expression = recognizeTimeFrameDurationExp(
  //        ruleText = ctx.field().getText,
  //        tfValue = durationField.value,
  //        tfFieldType = durationField.fieldType,
  //        tfDurationSeconds = durationField.durationSeconds,
  //        referenceField = ctx.field()
  //      )
  //      var updExpression =
  //        if (ctx.KWD_WITHOUT() != null ||
  //          (ctx.negativeVerb() != null && ctx.negativeVerb().asScala.nonEmpty)) {
  //          s"NOT(${expression})"
  //        } else expression
  //
  //      this.currAnalysisRecognitionData.ComparisionWhereClauses ::= ComparisionWhereClause(
  //        Expression = updExpression,
  //        Suffix = "_" + durationField.getText.trim.replaceAll("[^\\w]+", "_"),
  //        IsAggregated = ctx.fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG == 0,
  //        GroupName = ctx.field().fieldValue.trim.replaceAll("[^\\w]+", "_")
  //      )
  //    }
  //    ctx.value = ""
  //  }

  override def exitWhereEq(ctx: WhereEqContext): Unit = {
    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
    //    }

    ctx.fieldType = ctx.field().asScala.map(_.fieldType).distinct.product


    val fields = ctx.field().asScala
    val leftGrammarField = NuoGrammarField(
      FieldText = fields(0).fieldText,
      FieldType = fields(0).fieldType,
      FieldValue = fields(0).fieldValue,
      DateParts = NuoDateParts(
        fields(0).dd,
        fields(0).mm,
        fields(0).yyyy,
        fields(0).hh,
        fields(0).MM,
        fields(0).ss,
        fields(0).SSS,
        fields(0).tz
      ),
      Datatype = fields(0).datatype,
      EntityNames = fields(0).entityNames)

    val conditions =
      fields
        .tail
        .map { rightField =>
          var rightGrammarField1 = NuoGrammarField(
            FieldText = rightField.fieldText,
            FieldType = rightField.fieldType,
            FieldValue = rightField.fieldValue,
            DateParts = NuoDateParts(
              rightField.dd,
              rightField.mm,
              rightField.yyyy,
              rightField.hh,
              rightField.MM,
              rightField.ss,
              rightField.SSS,
              rightField.tz
            ),
            Datatype = fields(1).datatype,
            EntityNames = fields(1).entityNames)

          val recognizedClause = recognizeWhereClause(ruleText = ctx.getText,
            leftGrammarField = leftGrammarField,
            rightGrammarField1 = rightGrammarField1,
            rightGrammarField2 = null,
            dateGrammarField1 = null,
            dateGrammarField2 = null)
          val fieldValues = recognizedClause.RecognizedExps

          val field1 = fieldValues.head

          val field2 = fieldValues(1)

          val likeOp = if (ctx.subNegVerb != null) "NOT LIKE" else "LIKE"
          val eqOp = if (ctx.subNegVerb != null) "<>" else "="

          if (recognizedClause.RecognizedWhereType % RECOGNIZED_WHERE_TYPE_RIGHT_LITERAL == 0)
            s"UPPER(${fieldValues.head}) $likeOp '%${fieldValues(1).trim.toUpperCase.replaceAll("'", "")}%'"
          else if (recognizedClause.RecognizedWhereType % RECOGNIZED_WHERE_TYPE_RIGHT_STRING_FIELD == 0)
            s"UPPER(TRIM(${fieldValues.head})) $eqOp UPPER(TRIM(${fieldValues(1)}))"
          else if (recognizedClause.RecognizedWhereType % RECOGNIZED_WHERE_TYPE_LEFT_LITERAL == 0)
            s"UPPER(${fieldValues(1)}) $likeOp '%${fieldValues.head.trim.toUpperCase.replaceAll("'", "")}%'"
          else if (recognizedClause.RecognizedWhereType % RECOGNIZED_WHERE_TYPE_LEFT_STRING_FIELD == 0)
            s"UPPER(TRIM(${fieldValues.head.trim})) $eqOp UPPER(TRIM(${fieldValues(1).trim}))"
          else s"$field1 $eqOp $field2"
        }
    ctx.value =
      if (conditions.length > 1)
        conditions.mkString("(", " OR ", ")")
      else conditions.head
    if (ctx.KWD_WITHOUT() != null || ctx.prefixNegVerb != null) {
      ctx.value = s"NOT(${ctx.value})"
    }
    if (ctx.criteriaClause() != null && ctx.criteriaClause().asScala.length > 0) {

      val conditionExpResult = getConditionalWhereValueAndType(ctx.value, ctx.fieldType, ctx.criteriaClause())
      ctx.value = conditionExpResult._1
      ctx.fieldType = conditionExpResult._2


    }
  }

  //  override def exitWhereComparisionEq(ctx: WhereComparisionEqContext): Unit = {
  //    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
  //    //    if (knownCriterion.isDefined) {
  //    //
  //    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
  //    //      ctx.value = knownCriterion.get.CriterionText
  //    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
  //    //    }
  //
  //    ctx.field().asScala.tail.foreach { rightField =>
  //
  //
  //      ctx.fieldType = ctx.field().asScala.map(_.fieldType).distinct.product
  //
  //
  //      val fields = ctx.field().asScala
  //      val recognizedClause = recognizeWhereClause(ruleText = ctx.getText,
  //        leftGrammarField = NuoGrammarField(
  //          FieldText = fields(0).fieldText,
  //          FieldType = fields(0).fieldType,
  //          FieldValue = fields(0).fieldValue,
  //          DateParts = NuoDateParts(
  //            fields(0).dd,
  //            fields(0).mm,
  //            fields(0).yyyy,
  //            fields(0).hh,
  //            fields(0).MM,
  //            fields(0).ss,
  //            fields(0).SSS,
  //            fields(0).tz
  //          ),
  //          Datatype = fields(0).datatype,
  //          EntityNames = fields(0).entityNames),
  //        rightGrammarField1 = NuoGrammarField(
  //          FieldText = rightField.fieldText,
  //          FieldType = rightField.fieldType,
  //          FieldValue = rightField.fieldValue,
  //          DateParts = NuoDateParts(
  //            rightField.dd,
  //            rightField.mm,
  //            rightField.yyyy,
  //            rightField.hh,
  //            rightField.MM,
  //            rightField.ss,
  //            rightField.SSS,
  //            rightField.tz
  //          ),
  //          Datatype = rightField.datatype,
  //          EntityNames = rightField.entityNames),
  //        rightGrammarField2 = null,
  //        dateGrammarField1 = null,
  //        dateGrammarField2 = null)
  //      val fieldValues = recognizedClause.RecognizedExps
  //
  //      val field1 = fieldValues.head
  //
  //      val field2 = fieldValues(1)
  //
  //      val likeOp = if (ctx.KWD_WITHOUT() != null || (ctx.negativeVerb() != null && ctx.negativeVerb().asScala.nonEmpty)) "NOT LIKE" else "LIKE"
  //      val eqOp = if (ctx.KWD_WITHOUT() != null || (ctx.negativeVerb() != null && ctx.negativeVerb().asScala.nonEmpty)) "<>" else "="
  //      var expression =
  //        if (recognizedClause.RecognizedWhereType % RECOGNIZED_WHERE_TYPE_RIGHT_LITERAL == 0)
  //          s"UPPER(${fieldValues.head}) $likeOp '%${fieldValues(1).trim.toUpperCase.replaceAll("'", "")}%'"
  //        else if (recognizedClause.RecognizedWhereType % RECOGNIZED_WHERE_TYPE_RIGHT_STRING_FIELD == 0)
  //          s"UPPER(TRIM(${fieldValues.head})) $eqOp UPPER(TRIM(${fieldValues(1)}))"
  //        else if (recognizedClause.RecognizedWhereType % RECOGNIZED_WHERE_TYPE_LEFT_LITERAL == 0)
  //          s"UPPER(${fieldValues(1)}) $likeOp '%${fieldValues.head.trim.toUpperCase.replaceAll("'", "")}%'"
  //        else if (recognizedClause.RecognizedWhereType % RECOGNIZED_WHERE_TYPE_LEFT_STRING_FIELD == 0)
  //          s"UPPER(TRIM(${fieldValues.head.trim})) $eqOp UPPER(TRIM(${fieldValues(1).trim}))"
  //        else s"$field1 $eqOp $field2"
  //      this.currAnalysisRecognitionData.ComparisionWhereClauses ::= ComparisionWhereClause(
  //        Expression = expression,
  //        Suffix = "_" + rightField.fieldText.trim.replaceAll("[^\\w]+", "_"),
  //        IsAggregated = ctx.fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG == 0,
  //        GroupName = field1.trim.replaceAll("[^\\w]+", "_")
  //      )
  //
  //
  //      val nuoFields = recognizedClause.RecognizedQuestionFields.map(questionField =>
  //        List(questionField.Field.DatasetName,
  //          questionField.Field.EntityName,
  //          questionField.Field.FieldName,
  //          questionField.Field.DataType).asJava)
  //    }
  //    ctx.value = ""
  //
  //    //    ctx.nuoFields = nuoFields.asJava
  //  }
  //
  //  override def exitWhereComparisionStringFunctions(ctx: WhereComparisionStringFunctionsContext): Unit = {
  //
  //    ctx.field().asScala.tail.foreach { rightField =>
  //
  //
  //      ctx.fieldType = ctx.field().asScala.map(_.fieldType).distinct.product
  //
  //
  //      val fields = ctx.field().asScala
  //
  //
  //      val leftField = fields(0)
  //
  //      val leftExp =
  //        if (leftField.datatype.equalsIgnoreCase(NuoDataType.String)) {
  //          leftField.fieldValue
  //        } else {
  //          s"SAFE_CAST(${leftField.fieldValue} AS STRING)"
  //        }
  //
  //      val rightExp =
  //        if (rightField.datatype.equalsIgnoreCase(NuoDataType.String)) {
  //          rightField.fieldValue
  //        } else {
  //          s"SAFE_CAST(${rightField.fieldValue} AS STRING)"
  //        }
  //
  //      var expression =
  //        if (ctx.KWD_STARTS_WITH() != null) {
  //          s"STARTS_WITH(UPPER($leftExp), UPPER($rightExp))"
  //        } else if (ctx.KWD_ENDS_WITH() != null) {
  //          s"ENDS_WITH(UPPER($leftExp), UPPER($rightExp))"
  //        } else if (ctx.KWD_CONTAINS() != null) {
  //          s"STRPOS(UPPER(TRIM($leftExp)), UPPER(TRIM($rightExp))) > 0"
  //        } else {
  //          NuoRequestHandler.reportErrorToUser(new Exception(s"I could not recognize the filter [${ctx.getText}]."))
  //          throw new Exception("Unreachable Code statement")
  //        }
  //      if (ctx.KWD_WITHOUT() != null || (ctx.negativeVerb() != null && ctx.negativeVerb().asScala.nonEmpty)) {
  //        expression = s"NOT($expression)"
  //      }
  //      this.currAnalysisRecognitionData.ComparisionWhereClauses ::= ComparisionWhereClause(
  //        Expression = expression,
  //        Suffix = "_" + rightField.fieldText.trim.replaceAll("[^\\w]+", "_"),
  //        IsAggregated = ctx.fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG == 0,
  //        GroupName = rightField.fieldValue.trim.replaceAll("[^\\w]+", "_")
  //      )
  //
  //
  //      //      val nuoFields = recognizedClause.RecognizedQuestionFields.map(questionField =>
  //      //        List(questionField.Field.DatasetName,
  //      //          questionField.Field.EntityName,
  //      //          questionField.Field.FieldName,
  //      //          questionField.Field.DataType).asJava)
  //    }
  //    ctx.value = ""
  //
  //
  //  }
  //
  //  override def exitWhereComparisionGt(ctx: WhereComparisionGtContext): Unit = {
  //    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
  //    //    if (knownCriterion.isDefined) {
  //    //
  //    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
  //    //      ctx.value = knownCriterion.get.CriterionText
  //    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
  //    //    }
  //
  //    ctx.field().asScala.tail.foreach { rightField =>
  //
  //
  //      ctx.fieldType = ctx.field().asScala.map(_.fieldType).distinct.product
  //
  //
  //      val fields = ctx.field().asScala
  //      val recognizedClause = recognizeWhereClause(ruleText = ctx.getText,
  //        leftGrammarField = NuoGrammarField(
  //          FieldText = fields(0).fieldText,
  //          FieldType = fields(0).fieldType,
  //          FieldValue = fields(0).fieldValue,
  //          DateParts = NuoDateParts(
  //            fields(0).dd,
  //            fields(0).mm,
  //            fields(0).yyyy,
  //            fields(0).hh,
  //            fields(0).MM,
  //            fields(0).ss,
  //            fields(0).SSS,
  //            fields(0).tz
  //          ),
  //          Datatype = fields(0).datatype,
  //          EntityNames = fields(0).entityNames),
  //        rightGrammarField1 = NuoGrammarField(
  //          FieldText = rightField.fieldText,
  //          FieldType = rightField.fieldType,
  //          FieldValue = rightField.fieldValue,
  //          DateParts = NuoDateParts(
  //            rightField.dd,
  //            rightField.mm,
  //            rightField.yyyy,
  //            rightField.hh,
  //            rightField.MM,
  //            rightField.ss,
  //            rightField.SSS,
  //            rightField.tz
  //          ),
  //          Datatype = rightField.datatype,
  //          EntityNames = rightField.entityNames),
  //        rightGrammarField2 = null,
  //        dateGrammarField1 = null,
  //        dateGrammarField2 = null)
  //      val fieldValues = recognizedClause.RecognizedExps
  //
  //      val field1 = fieldValues.head
  //
  //      val field2 = fieldValues(1)
  //
  //      var op =
  //        if (ctx.KWD_WITHOUT() != null || (ctx.negativeVerb() != null && ctx.negativeVerb().asScala.nonEmpty))
  //          "<="
  //        else ">"
  //      this.currAnalysisRecognitionData.ComparisionWhereClauses ::= ComparisionWhereClause(
  //        Expression = s"$field1 $op $field2",
  //        Suffix = "_" + rightField.fieldText.trim.replaceAll("[^\\w]+", "_"),
  //        IsAggregated = ctx.fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG == 0,
  //        GroupName = field1.trim.replaceAll("[^\\w]+", "_")
  //      )
  //
  //
  //      val nuoFields = recognizedClause.RecognizedQuestionFields.map(questionField =>
  //        List(questionField.Field.DatasetName,
  //          questionField.Field.EntityName,
  //          questionField.Field.FieldName,
  //          questionField.Field.DataType).asJava)
  //    }
  //    ctx.value = ""
  //
  //    //    ctx.nuoFields = nuoFields.asJava
  //  }
  //
  //  override def exitWhereComparisionLt(ctx: WhereComparisionLtContext): Unit = {
  //    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
  //    //    if (knownCriterion.isDefined) {
  //    //
  //    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
  //    //      ctx.value = knownCriterion.get.CriterionText
  //    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
  //    //    }
  //
  //    ctx.field().asScala.tail.foreach { rightField =>
  //
  //
  //      ctx.fieldType = ctx.field().asScala.map(_.fieldType).distinct.product
  //
  //
  //      val fields = ctx.field().asScala
  //      val recognizedClause = recognizeWhereClause(ruleText = ctx.getText,
  //        leftGrammarField = NuoGrammarField(
  //          FieldText = fields(0).fieldText,
  //          FieldType = fields(0).fieldType,
  //          FieldValue = fields(0).fieldValue,
  //          DateParts = NuoDateParts(
  //            fields(0).dd,
  //            fields(0).mm,
  //            fields(0).yyyy,
  //            fields(0).hh,
  //            fields(0).MM,
  //            fields(0).ss,
  //            fields(0).SSS,
  //            fields(0).tz
  //          ),
  //          Datatype = fields(0).datatype,
  //          EntityNames = fields(0).entityNames),
  //        rightGrammarField1 = NuoGrammarField(
  //          FieldText = rightField.fieldText,
  //          FieldType = rightField.fieldType,
  //          FieldValue = rightField.fieldValue,
  //          DateParts = NuoDateParts(
  //            rightField.dd,
  //            rightField.mm,
  //            rightField.yyyy,
  //            rightField.hh,
  //            rightField.MM,
  //            rightField.ss,
  //            rightField.SSS,
  //            rightField.tz
  //          ),
  //          Datatype = rightField.datatype,
  //          EntityNames = rightField.entityNames),
  //        rightGrammarField2 = null,
  //        dateGrammarField1 = null,
  //        dateGrammarField2 = null)
  //      val fieldValues = recognizedClause.RecognizedExps
  //
  //      val field1 = fieldValues.head
  //
  //      val field2 = fieldValues(1)
  //
  //
  //      var op =
  //        if (ctx.KWD_WITHOUT() != null || (ctx.negativeVerb() != null && ctx.negativeVerb().asScala.nonEmpty))
  //          ">="
  //        else "<"
  //      this.currAnalysisRecognitionData.ComparisionWhereClauses ::= ComparisionWhereClause(
  //        Expression = s"$field1 $op $field2",
  //        Suffix = "_" + rightField.fieldText.trim.replaceAll("[^\\w]+", "_"),
  //        IsAggregated = ctx.fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG == 0,
  //        GroupName = field1.trim.replaceAll("[^\\w]+", "_")
  //      )
  //
  //
  //      val nuoFields = recognizedClause.RecognizedQuestionFields.map(questionField =>
  //        List(questionField.Field.DatasetName,
  //          questionField.Field.EntityName,
  //          questionField.Field.FieldName,
  //          questionField.Field.DataType).asJava)
  //    }
  //    ctx.value = ""
  //
  //    //    ctx.nuoFields = nuoFields.asJava
  //  }
  //
  //  override def exitWhereComparisionGtEq(ctx: WhereComparisionGtEqContext): Unit = {
  //    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
  //    //    if (knownCriterion.isDefined) {
  //    //
  //    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
  //    //      ctx.value = knownCriterion.get.CriterionText
  //    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
  //    //    }
  //
  //    ctx.field().asScala.tail.foreach { rightField =>
  //
  //
  //      ctx.fieldType = ctx.field().asScala.map(_.fieldType).distinct.product
  //
  //
  //      val fields = ctx.field().asScala
  //      val recognizedClause = recognizeWhereClause(ruleText = ctx.getText,
  //        leftGrammarField = NuoGrammarField(
  //          FieldText = fields(0).fieldText,
  //          FieldType = fields(0).fieldType,
  //          FieldValue = fields(0).fieldValue,
  //          DateParts = NuoDateParts(
  //            fields(0).dd,
  //            fields(0).mm,
  //            fields(0).yyyy,
  //            fields(0).hh,
  //            fields(0).MM,
  //            fields(0).ss,
  //            fields(0).SSS,
  //            fields(0).tz
  //          ),
  //          Datatype = fields(0).datatype,
  //          EntityNames = fields(0).entityNames),
  //        rightGrammarField1 = NuoGrammarField(
  //          FieldText = rightField.fieldText,
  //          FieldType = rightField.fieldType,
  //          FieldValue = rightField.fieldValue,
  //          DateParts = NuoDateParts(
  //            rightField.dd,
  //            rightField.mm,
  //            rightField.yyyy,
  //            rightField.hh,
  //            rightField.MM,
  //            rightField.ss,
  //            rightField.SSS,
  //            rightField.tz
  //          ),
  //          Datatype = rightField.datatype,
  //          EntityNames = rightField.entityNames),
  //        rightGrammarField2 = null,
  //        dateGrammarField1 = null,
  //        dateGrammarField2 = null)
  //      val fieldValues = recognizedClause.RecognizedExps
  //
  //      val field1 = fieldValues.head
  //
  //      val field2 = fieldValues(1)
  //
  //      var op =
  //        if (ctx.KWD_WITHOUT() != null || (ctx.negativeVerb() != null && ctx.negativeVerb().asScala.nonEmpty))
  //          "<"
  //        else ">="
  //      this.currAnalysisRecognitionData.ComparisionWhereClauses ::= ComparisionWhereClause(
  //        Expression = s"$field1 >= $field2",
  //        Suffix = "_" + rightField.fieldText.trim.replaceAll("[^\\w]+", "_"),
  //        IsAggregated = ctx.fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG == 0,
  //        GroupName = field1.trim.replaceAll("[^\\w]+", "_")
  //      )
  //
  //
  //      val nuoFields = recognizedClause.RecognizedQuestionFields.map(questionField =>
  //        List(questionField.Field.DatasetName,
  //          questionField.Field.EntityName,
  //          questionField.Field.FieldName,
  //          questionField.Field.DataType).asJava)
  //    }
  //    ctx.value = ""
  //
  //    //    ctx.nuoFields = nuoFields.asJava
  //  }
  //
  //  override def exitWhereComparisionLtEq(ctx: WhereComparisionLtEqContext): Unit = {
  //    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
  //    //    if (knownCriterion.isDefined) {
  //    //
  //    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
  //    //      ctx.value = knownCriterion.get.CriterionText
  //    //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
  //    //    }
  //
  //    ctx.field().asScala.tail.foreach { rightField =>
  //
  //
  //      ctx.fieldType = ctx.field().asScala.map(_.fieldType).distinct.product
  //
  //
  //      val fields = ctx.field().asScala
  //      val recognizedClause = recognizeWhereClause(ruleText = ctx.getText,
  //        leftGrammarField = NuoGrammarField(
  //          FieldText = fields(0).fieldText,
  //          FieldType = fields(0).fieldType,
  //          FieldValue = fields(0).fieldValue,
  //          DateParts = NuoDateParts(
  //            fields(0).dd,
  //            fields(0).mm,
  //            fields(0).yyyy,
  //            fields(0).hh,
  //            fields(0).MM,
  //            fields(0).ss,
  //            fields(0).SSS,
  //            fields(0).tz
  //          ),
  //          Datatype = fields(0).datatype,
  //          EntityNames = fields(0).entityNames),
  //        rightGrammarField1 = NuoGrammarField(
  //          FieldText = rightField.fieldText,
  //          FieldType = rightField.fieldType,
  //          FieldValue = rightField.fieldValue,
  //          DateParts = NuoDateParts(
  //            rightField.dd,
  //            rightField.mm,
  //            rightField.yyyy,
  //            rightField.hh,
  //            rightField.MM,
  //            rightField.ss,
  //            rightField.SSS,
  //            rightField.tz
  //          ),
  //          Datatype = rightField.datatype,
  //          EntityNames = rightField.entityNames),
  //        rightGrammarField2 = null,
  //        dateGrammarField1 = null,
  //        dateGrammarField2 = null)
  //      val fieldValues = recognizedClause.RecognizedExps
  //
  //      val field1 = fieldValues.head
  //
  //      val field2 = fieldValues(1)
  //
  //      var op =
  //        if (ctx.KWD_WITHOUT() != null || (ctx.negativeVerb() != null && ctx.negativeVerb().asScala.nonEmpty))
  //          ">"
  //        else "<="
  //      this.currAnalysisRecognitionData.ComparisionWhereClauses ::= ComparisionWhereClause(
  //        Expression = s"$field1 $op $field2",
  //        Suffix = "_" + rightField.fieldText.trim.replaceAll("[^\\w]+", "_"),
  //        IsAggregated = ctx.fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG == 0,
  //        GroupName = field1.trim.replaceAll("[^\\w]+", "_")
  //      )
  //
  //
  //      val nuoFields = recognizedClause.RecognizedQuestionFields.map(questionField =>
  //        List(questionField.Field.DatasetName,
  //          questionField.Field.EntityName,
  //          questionField.Field.FieldName,
  //          questionField.Field.DataType).asJava)
  //    }
  //    ctx.value = ""
  //
  //    //    ctx.nuoFields = nuoFields.asJava
  //  }

  //
  //  override def exitWhereNotEq(ctx: WhereNotEqContext): Unit = {
  //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
  //    if (knownCriterion.isDefined) {
  //
  //      ctx.fieldType = knownCriterion.get.FieldType.toInt
  //      ctx.value = knownCriterion.get.CriterionText
  //      recognizedFields.put(ctx.getText, knownCriterion.get.RelatedFields.map(_.Field))
  //    } else {
  //
  //      ctx.fieldType = ctx.field().asScala.map(_.fieldType).distinct.product
  //
  //      val fields = ctx.field().asScala
  //      val recognizedClause = recognizeWhereClause(ruleText = ctx.getText,
  //        leftGrammarField = NuoGrammarField(
  //          fields(0).fieldText,
  //          fields(0).fieldType,
  //          fields(0).fieldValue,
  //          fields(0).availableDateParts,
  //          fields(0).datatype,
  //          fields(0).entityNames),
  //        rightGrammarField1 = NuoGrammarField(
  //          fields(1).fieldText,
  //          fields(1).fieldType,
  //          fields(1).fieldValue,
  //          fields(1).availableDateParts,
  //          fields(1).datatype,
  //          fields(1).entityNames),
  //        rightGrammarField2 = null,
  //        dateGrammarField1 = null,
  //        dateGrammarField2 = null,
  //        shouldTruncateDateTime = true)
  //      val fieldValues = recognizedClause.RecognizedExps
  //
  //
  //      val field1 = fieldValues.head
  //
  //      val field2 = fieldValues(1)
  //
  //      ctx.value = if (recognizedClause.RecognizedWhereType % RECOGNIZED_WHERE_TYPE_RIGHT_LITERAL == 0)
  //        s"UPPER(${fieldValues.head}) NOT LIKE '%${fieldValues(1).trim.toUpperCase}%'"
  //      else if (recognizedClause.RecognizedWhereType % RECOGNIZED_WHERE_TYPE_RIGHT_STRING_FIELD == 0)
  //        s"UPPER(${fieldValues.head}) <> UPPER(${fieldValues(1)})"
  //      else if (recognizedClause.RecognizedWhereType % RECOGNIZED_WHERE_TYPE_LEFT_LITERAL == 0)
  //        s"UPPER(${fieldValues(1)}) NOT LIKE '%${fieldValues.head.trim.toUpperCase}%'"
  //      else if (recognizedClause.RecognizedWhereType % RECOGNIZED_WHERE_TYPE_LEFT_STRING_FIELD == 0)
  //        s"UPPER(${fieldValues(1)}) <> UPPER(${fieldValues.head})"
  //      else s"$field1 <> $field2"
  //
  //      val nuoFields = recognizedClause.RecognizedQuestionFields.map(questionField =>
  //        List(questionField.Field.DatasetName,
  //          questionField.Field.EntityName,
  //          questionField.Field.FieldName,
  //          questionField.Field.DataType).asJava)
  //      ctx.nuoFields = nuoFields.asJava
  //      this.currAnalysisRecognitionData.Criteria +:= NuoCriteria(ctx.getText,
  //        ctx.fieldType.toString,
  //        ctx.value,
  //        recognizedClause.RecognizedQuestionFields)
  //    }
  //
  //  }

  override def exitWhereStringFunctions(ctx: WhereStringFunctionsContext): Unit = {
    val ruleText = ctx.getText
    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ruleText, knownCriterion.get.RelatedFields.map(_.Field))
    //    }

    ctx.fieldType = ctx.field().asScala.map(_.fieldType).distinct.product


    val leftField = ctx.field(0)

    val leftExp =
      if (leftField.datatype.equalsIgnoreCase(NuoDataType.String)) {
        leftField.fieldValue
      } else {
        s"SAFE_CAST(${leftField.fieldValue} AS STRING)"
      }

    val conditions =
      ctx.field().asScala
        .tail
        .map { rightField =>

          val rightExp =
            if (rightField.datatype.equalsIgnoreCase(NuoDataType.String)) {
              rightField.fieldValue
            } else {
              s"SAFE_CAST(${rightField.fieldValue} AS STRING)"
            }
          val fieldValue = if (ctx.KWD_STARTS_WITH() != null) {
            s"STARTS_WITH(UPPER($leftExp), UPPER($rightExp))"
          } else if (ctx.KWD_ENDS_WITH() != null) {
            s"ENDS_WITH(UPPER($leftExp), UPPER($rightExp))"
          } else if (ctx.KWD_CONTAINS() != null) {
            s"STRPOS(UPPER(TRIM($leftExp)), UPPER(TRIM($rightExp))) > 0"
          } else {
            NuoRequestHandler.reportErrorToUser(new Exception(s"I could not recognize the filter [${ruleText}]."))
            throw new Exception("Unreachable Code statement")
          }
          if (ctx.subNegVerb != null)
            s"NOT(${fieldValue})"
          else fieldValue
        }
    ctx.value =
      if (conditions.length > 1)
        conditions.mkString("(", " OR ", ")")
      else conditions.head

    if (ctx.KWD_WITHOUT() != null || ctx.prefixNegVerb != null) {
      ctx.value = s"NOT(${ctx.value})"
    }

    if (ctx.criteriaClause() != null && ctx.criteriaClause().asScala.length > 0) {

      val conditionExpResult = getConditionalWhereValueAndType(ctx.value, ctx.fieldType, ctx.criteriaClause())
      ctx.value = conditionExpResult._1
      ctx.fieldType = conditionExpResult._2
    }
  }

  override def exitWhereDefault(ctx: WhereDefaultContext): Unit = {

    val ruleText = ctx.getText
    val fieldContent = ctx.fieldContent()

    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ruleText, knownCriterion.get.RelatedFields.map(_.Field))
    //    }

    ctx.fieldType = fieldContent.fieldType

    //    if (NuoEvaEnglishListener.isGrammarTestMode) {
    //      ctx.value = fieldContent.fieldValue
    //    } else {

    val notNulExp = s"${fieldContent.fieldValue} IS NOT NULL"
    ctx.value = if (fieldContent.datatype.equalsIgnoreCase(NuoDataType.Boolean))
      s"$notNulExp AND ${fieldContent.fieldValue}"
    else if (fieldContent.datatype.equalsIgnoreCase(NuoDataType.String))
      s"$notNulExp AND LENGTH(TRIM(${fieldContent.fieldValue})) > 0 AND UPPER(TRIM(${fieldContent.fieldValue})) <> 'NULL' "
    else if (NuoDataTypeHandler.isNumberType(fieldContent.datatype))
      s"$notNulExp AND ${fieldContent.fieldValue} <> 0 "
    else notNulExp

//    if (ctx.KWD_WITHOUT() != null || ctx.prefixNegVerb != null) {
//      ctx.value = s"NOT(${ctx.value})"
//    }

    //    }
  }

  override def exitWhereDefaultNegative(ctx: WhereDefaultNegativeContext): Unit = {


    val ruleText = ctx.getText
    val fieldContent = ctx.fieldContent()

    //    val knownCriterion = this.currAnalysisRecognitionData.Criteria.find(_.RuleText.trim.equalsIgnoreCase(ctx.getText.trim))
    //    if (knownCriterion.isDefined) {
    //
    //      ctx.fieldType = knownCriterion.get.FieldType.toInt
    //      ctx.value = knownCriterion.get.CriterionText
    //      recognizedFields.put(ruleText, knownCriterion.get.RelatedFields.map(_.Field))
    //    }

    ctx.fieldType = fieldContent.fieldType

    //    if (NuoEvaEnglishListener.isGrammarTestMode) {
    //      ctx.value = fieldContent.fieldValue
    //    } else {

    val notNulExp = s"${fieldContent.fieldValue} IS NOT NULL"
    ctx.value = if (fieldContent.datatype.equalsIgnoreCase(NuoDataType.Boolean))
      s"$notNulExp AND ${fieldContent.fieldValue}"
    else if (fieldContent.datatype.equalsIgnoreCase(NuoDataType.String))
      s"$notNulExp AND LENGTH(TRIM(${fieldContent.fieldValue})) > 0"
    else if (NuoDataTypeHandler.isNumberType(fieldContent.datatype))
      s"$notNulExp AND ${fieldContent.fieldValue} <> 0 "
    else notNulExp

    ctx.value = s"NOT(${ctx.value})"
  }


  def convertToNumber(input: String, multiplier: Long): String = {

    /*
    * Following the international standard for decimal places
    * */
    val output = input.toDouble * multiplier
    if ((output * 10) % 10 > 0) output.toFloat.toString
    else output.toLong.toString
  }

  override def exitNumberAgg(ctx: NumberAggContext): Unit = {
    ctx.value = ctx.aggregateNumber().value
    ctx.aggFunction = ctx.aggregateNumber().aggFunction

  }

  override def exitNumberT(ctx: NumberTContext): Unit = {
    val plainText = ctx.KWD_NUMBER_T().getText.toUpperCase
    ctx.value = convertToNumber(plainText.substring(0, plainText.indexOf('T')).trim, 1000 * 1000 * 1000 * 1000L)
  }

  override def exitNumberB(ctx: NumberBContext): Unit = {
    val plainText = ctx.KWD_NUMBER_B().getText.toUpperCase
    ctx.value = convertToNumber(plainText.substring(0, plainText.indexOf('B')).trim, 1000 * 1000 * 1000)

  }

  override def exitNumberM(ctx: NumberMContext): Unit = {
    val plainText = ctx.KWD_NUMBER_M().getText.toUpperCase
    ctx.value = convertToNumber(plainText.substring(0, plainText.indexOf('M')).trim, 1000 * 1000)

  }

  override def exitNumberK(ctx: NumberKContext): Unit = {
    val plainText = ctx.KWD_NUMBER_K().getText.toUpperCase
    ctx.value = if (plainText.contains("THOUSAND")) convertToNumber(plainText.substring(0, plainText.indexOf('T')).trim, 1000)
    else if (plainText.contains("G")) convertToNumber(plainText.substring(0, plainText.indexOf('G')).trim, 1000)
    else convertToNumber(plainText.substring(0, plainText.indexOf('K')).trim, 1000)
  }

  override def exitNumberDefault(ctx: NumberDefaultContext): Unit = {
    ctx.value = ctx.NUMBER().getText.trim

  }

  def recordAlias(fieldValue: String,
                  fieldAlias: String): Unit = {

    if (fieldValue != null && fieldValue.trim.nonEmpty) {


      var updFieldAlias = if (fieldAlias != null && fieldAlias.trim.nonEmpty) fieldAlias else "Expression"
      if (recordedFieldAliases.values.toList.contains(fieldAlias) && recordedFieldAliases.get(fieldValue).isDefined && !recordedFieldAliases.get(fieldValue).get.equalsIgnoreCase(fieldAlias)) {
        updFieldAlias += "_1"
      }

      //      var duplicateCount = recordedFieldAliases.values.toList.distinct.count(_.equalsIgnoreCase(fieldAlias))
      //      if (duplicateCount > 0)
      //        recordedFieldAliases.put(fieldValue, updFieldAlias + "_" + duplicateCount)
      //      else
      recordedFieldAliases.put(fieldValue, updFieldAlias)
    }

  }

  override def exitAggFieldAvg(ctx: AggFieldAvgContext): Unit = {

    ctx.fieldType = ctx.fieldContent().fieldType * NuoEvaEnglishListener.FIELD_TYPE_AGG
    ctx.fieldValue = s"AVG(${ctx.fieldContent().fieldValue})"
    ctx.datatype = ctx.fieldContent().datatype
    ctx.fieldAlias = "Average_" + ctx.fieldContent().fieldAlias
    ctx.isAggregated = true
    ctx.entityNames = List(ctx.fieldContent().tableName).asJava
    recordAlias(fieldValue = ctx.fieldValue, fieldAlias = ctx.fieldAlias)
    ctx.fieldText = ctx.getText
  }

  override def exitAggFieldCount(ctx: AggFieldCountContext): Unit = {

    ctx.fieldType = ctx.fieldContent().fieldType * NuoEvaEnglishListener.FIELD_TYPE_AGG
    ctx.fieldValue = s"COUNT(DISTINCT ${ctx.fieldContent().fieldValue})"
    ctx.fieldAlias = ctx.fieldContent().fieldAlias + "_Count"
    ctx.datatype = NuoDataType.Int64
    ctx.isAggregated = true
    ctx.entityNames = List(ctx.fieldContent().tableName).asJava
    ctx.fieldText = ctx.getText

    recordAlias(ctx.fieldValue, ctx.fieldAlias)
  }

  override def exitAggFieldMax(ctx: AggFieldMaxContext): Unit = {

    ctx.fieldType = ctx.fieldContent().fieldType * NuoEvaEnglishListener.FIELD_TYPE_AGG
    ctx.fieldValue = s"MAX(${ctx.fieldContent().fieldValue})"
    ctx.fieldAlias = "Maximum_" + ctx.fieldContent().fieldAlias
    ctx.datatype = ctx.fieldContent().datatype
    ctx.isAggregated = true
    ctx.entityNames = List(ctx.fieldContent().tableName).asJava
    ctx.fieldText = ctx.getText

    recordAlias(ctx.fieldValue, ctx.fieldAlias)


  }

  override def exitAggFieldMin(ctx: AggFieldMinContext): Unit = {

    ctx.fieldType = ctx.fieldContent().fieldType * NuoEvaEnglishListener.FIELD_TYPE_AGG
    ctx.fieldValue = s"MIN(${ctx.fieldContent().fieldValue})"
    ctx.fieldAlias = "Minimum_" + ctx.fieldContent().fieldAlias
    ctx.datatype = ctx.fieldContent().datatype
    ctx.isAggregated = true
    ctx.entityNames = List(ctx.fieldContent().tableName).asJava
    ctx.fieldText = ctx.getText

    recordAlias(ctx.fieldValue, ctx.fieldAlias)
  }

  override def exitAggFieldSum(ctx: AggFieldSumContext): Unit = {

    ctx.fieldType = ctx.fieldContent().fieldType * NuoEvaEnglishListener.FIELD_TYPE_AGG
    ctx.fieldValue = s"SUM(${ctx.fieldContent().fieldValue})"
    ctx.fieldAlias = "Total_" + ctx.fieldContent().fieldAlias
    ctx.datatype = ctx.fieldContent().datatype
    ctx.isAggregated = true
    ctx.entityNames = List(ctx.fieldContent().tableName).asJava

    ctx.fieldText = ctx.getText

    recordAlias(ctx.fieldValue, ctx.fieldAlias)
  }

  override def exitAggNumberAvg(ctx: AggNumberAvgContext): Unit = {
    ctx.value = ctx.numberField().value
    ctx.aggFunction = "AVG"
  }

  override def exitAggNumberCount(ctx: AggNumberCountContext): Unit = {
    ctx.value = ctx.numberField().value
    ctx.aggFunction = "COUNT"
  }

  override def exitAggNumberMax(ctx: AggNumberMaxContext): Unit = {
    ctx.value = ctx.numberField().value
    ctx.aggFunction = "MAX"
  }

  override def exitAggNumberMin(ctx: AggNumberMinContext): Unit = {
    ctx.value = ctx.numberField().value
    ctx.aggFunction = "MIN"
  }

  override def exitAggNumberSum(ctx: AggNumberSumContext): Unit = {
    ctx.value = ctx.numberField().value
    ctx.aggFunction = "SUM"
  }


  //  override def exitConvertOF(ctx: ConvertOFContext): Unit = {
  //    ctx.value = ctx.fieldContent(1).value + " " + ctx.fieldContent(0).value
  //  }

  //
  //  override def exitFieldWithQuotes(ctx: FieldWithQuotesContext): Unit = {
  //
  //    ctx.value = ctx.getText.replace("'", "").replace("\"", "").trim
  //  }

  override def exitDurationFieldNext(ctx: DurationFieldNextContext): Unit = {
    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_DURATION_NEXT * NuoEvaEnglishListener.FIELD_TYPE_TIMESTAMP
    ctx.value = s"TIMESTAMP_ADD(CURRENT_TIMESTAMP(), INTERVAL ${ctx.duration().durationSeconds} SECOND)"
    val calendar = Calendar.getInstance()
    ctx.timeInMillis = calendar.getTimeInMillis + (ctx.duration().durationSeconds * 1000)
    ctx.durationSeconds = ctx.duration().durationSeconds

  }

  override def exitDurationFieldPast(ctx: DurationFieldPastContext): Unit = {
    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_DURATION_PAST * NuoEvaEnglishListener.FIELD_TYPE_INT
    ctx.value = s"TIMESTAMP_SUB(CURRENT_TIMESTAMP(), INTERVAL ${ctx.duration().durationSeconds} SECOND)"
    val calendar = Calendar.getInstance()
    ctx.timeInMillis = calendar.getTimeInMillis + (ctx.duration().durationSeconds * 1000)
    ctx.durationSeconds = ctx.duration().durationSeconds
  }

  override def exitDuration(ctx: DurationContext): Unit = {
    var durationSeconds = 0
    if (ctx.duration() != null) {
      durationSeconds += ctx.duration().asScala.map(_.durationSeconds).sum
    }
    if (ctx.KWD_YEAR() != null) {
      val num = if (ctx.years != null) ctx.years.getText.trim.toInt else 1
      durationSeconds += num * 365 * 24 * 3600
      ctx.numOfYears = num.toString
    }
    if (ctx.KWD_MONTH() != null) {
      val num = if (ctx.months != null) ctx.months.getText.trim.toInt else 1
      durationSeconds += num * 30 * 24 * 3600
      ctx.numOfMonths = num.toString
    }
    if (ctx.KWD_WEEK() != null) {
      val num = if (ctx.weeks != null) ctx.weeks.getText.trim.toInt else 1
      durationSeconds += num * 7 * 24 * 3600
      ctx.numOfDays = num.toString
    }
    if (ctx.KWD_DAY() != null) {
      val num = if (ctx.days != null) ctx.days.getText.trim.toInt else 1
      durationSeconds += num * 24 * 3600
      ctx.numOfDays = num.toString
    }
    if (ctx.KWD_HOUR() != null) {
      val num = if (ctx.hours != null) ctx.hours.getText.trim.toInt else 1
      durationSeconds += num * 3600
      ctx.numOfHours = num.toString
    }
    if (ctx.KWD_MINUTE() != null) {
      val num = if (ctx.minutes != null) ctx.minutes.getText.trim.toInt else 1
      durationSeconds += num * 60
      ctx.numOfMinutes = num.toString
    }
    if (ctx.KWD_SECOND() != null) {
      val num = if (ctx.seconds != null) ctx.seconds.getText.trim.toInt else 1
      durationSeconds += num
      ctx.numOfSeconds = num.toString
    }
    if (durationSeconds > 0) ctx.durationSeconds = durationSeconds
  }

  def getTotalDaysInMonth(monthNum: Int, yearNum: Int): Int = {

    if (List(1, 3, 5, 7, 8, 10, 12).contains(monthNum)) 31
    else if (monthNum == 2) {
      if (isLeapYear(yearNum)) 29 else 28
    } else 30
  }

  override def exitDateFieldDateTime(ctx: DateFieldDateTimeContext): Unit = {

    val dateObj = ctx.date()
    val timeObj = ctx.time()


    ctx.fieldText = ctx.getText
    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_TIMESTAMP
    ctx.fieldValue = s"TIMESTAMP '" +
      s"${dateObj.yyyy}" +
      s"-${dateObj.mm}" +
      s"-${dateObj.dd}" +
      s" ${timeObj.hh}" +
      s":${timeObj.MM}" +
      s":${timeObj.ss}" +
      s".${timeObj.SSS}" +
      s"'"
    ctx.datatype = NuoDataType.Timestamp

    ctx.dd = dateObj.dd
    ctx.mm = dateObj.mm
    ctx.yyyy = dateObj.yyyy

    ctx.hh = timeObj.hh
    ctx.MM = timeObj.MM
    ctx.ss = timeObj.ss
    ctx.SSS = timeObj.SSS

    val calendar = Calendar.getInstance()
    calendar.set(dateObj.yyyy, dateObj.dd - 1, dateObj.dd, timeObj.hh, timeObj.MM, timeObj.ss)
    ctx.timeInMillis = System.currentTimeMillis() + calendar.getTimeInMillis + timeObj.SSS
  }

  override def exitDateFieldDateOnly(ctx: DateFieldDateOnlyContext): Unit = {
    val dateObj = ctx.date()
    ctx.fieldText = ctx.getText
    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_DATE
    ctx.fieldValue = s"DATE '" +
      s"${dateObj.yyyy}" +
      s"-${dateObj.mm}" +
      s"-${dateObj.dd}" +
      s"'"
    ctx.datatype = NuoDataType.Date
    ctx.dd = dateObj.dd
    ctx.mm = dateObj.mm
    ctx.yyyy = dateObj.yyyy
  }

  override def exitDateFieldTimeOnly(ctx: DateFieldTimeOnlyContext): Unit = {
    val timeObj = ctx.time()
    val availableDateParts = mutable.ArrayBuffer[String]()


    ctx.fieldText = ctx.getText
    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_TIME
    ctx.fieldValue = s"TIME '" +
      s"${timeObj.hh}" +
      s":${timeObj.MM}" +
      s":${timeObj.ss}" +
      s".${timeObj.SSS}" +
      s"'"
    ctx.datatype = NuoDataType.Time

    ctx.hh = timeObj.hh
    ctx.MM = timeObj.MM
    ctx.ss = timeObj.ss
    ctx.SSS = timeObj.SSS

    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, timeObj.hh)
    calendar.set(Calendar.MINUTE, timeObj.MM)
    calendar.set(Calendar.SECOND, timeObj.ss)
    ctx.timeInMillis = System.currentTimeMillis() + calendar.getTimeInMillis + timeObj.SSS
  }

  def convertMonthLiteralToNum(inputMonth: String): Int = {

    inputMonth.trim match {
      //      case x if x.toUpperCase.startsWith("JAN") => "1"
      case x if x.toUpperCase.startsWith("FEB") => 2
      case x if x.toUpperCase.startsWith("MAR") => 3
      case x if x.toUpperCase.startsWith("APR") => 4
      case x if x.toUpperCase.startsWith("MAY") => 5
      case x if x.toUpperCase.startsWith("JUN") => 6
      case x if x.toUpperCase.startsWith("JUL") => 7
      case x if x.toUpperCase.startsWith("AUG") => 8
      case x if x.toUpperCase.startsWith("SEP") => 9
      case x if x.toUpperCase.startsWith("OCT") => 10
      case x if x.toUpperCase.startsWith("NOV") => 11
      case x if x.toUpperCase.startsWith("DEC") => 12
      case _ => 1
    }
  }


  def convertYyToYyyy(yy: Int): Int = {
    if (yy.toString.length == 2) {
      if (yy > Calendar.getInstance().get(Calendar.YEAR) % 100) {
        yy + 1900
      } else {
        yy + 2000
      }
    } else yy
  }

  override def exitDateAdvDay(ctx: DateAdvDayContext): Unit = {

    val availableDateParts = mutable.ArrayBuffer[String]()

    val now = Calendar.getInstance()
    val currDay = now.get(Calendar.DAY_OF_MONTH)
    ctx.yyyy = (now.get(Calendar.YEAR))
    ctx.mm = (now.get(Calendar.MONTH) + 1)
    ctx.dd =
      if (ctx.KWD_TOMORROW() != null) {
        (now.get(Calendar.DAY_OF_MONTH) + 1)
      } else if (ctx.KWD_YESTERDAY() != null) {
        (now.get(Calendar.DAY_OF_MONTH) - 1)
      } else {
        now.get(Calendar.DAY_OF_MONTH)
      }
    if (ctx.dd == 0) {
      ctx.mm = (ctx.mm - 1)
      ctx.dd = 1
    } else if (ctx.dd == getTotalDaysInMonth(ctx.mm, ctx.yyyy) + 1) {
      ctx.mm = (ctx.mm + 1)
      ctx.dd = 1
    }
    if (ctx.mm == 0) {
      ctx.yyyy = (ctx.yyyy - 1)
      ctx.mm = 1
    } else if (ctx.mm == 13) {
      ctx.yyyy = (ctx.yyyy + 1)
      ctx.mm = 1
    }
  }

  def isLeapYear(year: Int): Boolean = {
    year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
  }

  override def exitDateAdjNum(ctx: DateAdjNumContext): Unit = {


    val now = Calendar.getInstance()
    val currYear = now.get(Calendar.YEAR)
    val currMonth = now.get(Calendar.MONTH) + 1
    val currDay = now.get(Calendar.DAY_OF_MONTH)
    ctx.mm = if (ctx.KWD_MONTH_OF_YEAR() != null) {
      convertMonthLiteralToNum(ctx.KWD_MONTH_OF_YEAR().getText)
    } else 1
    ctx.dd = if (ctx.dayOfMonth() != null) {
      ctx.dayOfMonth.value.trim.toInt
    } else 1

    if (ctx.year != null && ctx.dayOfMonth == null) {

      ctx.yyyy = convertYyToYyyy(ctx.year.getText.trim.toInt)
    } else if (ctx.year == null && ctx.dayOfMonth != null) {

      val ddInt = ctx.dayOfMonth.value.trim.toInt
      if (ddInt > getTotalDaysInMonth(ctx.mm, currYear)) {

        ctx.dd = 1
        ctx.yyyy = convertYyToYyyy(ddInt)
      } else {
        ctx.dd = ddInt
        ctx.yyyy =
          if (
            ctx.mm > currMonth
              || (ctx.mm == currMonth && ctx.dd > currDay)
          )
            (currYear - 1)
          else currYear
      }

    } else if (ctx.year != null && ctx.dayOfMonth != null) {

      //Both year and date are present
      ctx.dd = ctx.dayOfMonth.value.trim.toInt
      ctx.yyyy = convertYyToYyyy(ctx.year.getText.trim.toInt)
    } else {

      //Both year and date are absent
      ctx.dd = 1
      ctx.yyyy = if (ctx.mm.toInt > currMonth || (ctx.mm.toInt == currMonth && ctx.dd.toInt > currDay)) (currYear - 1) else currYear

    }
  }

  override def exitDateDefault(ctx: DateDefaultContext): Unit = {
    val availableDateParts = mutable.ArrayBuffer[String]()
    val now = Calendar.getInstance()
    val currYear = now.get(Calendar.YEAR)
    val currMonth = now.get(Calendar.MONTH) + 1
    val currDay = now.get(Calendar.DAY_OF_MONTH)

    ctx.mm = if (ctx.KWD_MONTH_OF_YEAR() != null) {
      convertMonthLiteralToNum(ctx.KWD_MONTH_OF_YEAR().getText)
    } else if (ctx.monthOfYear != null) {
      ctx.monthOfYear.getText.toInt
    } else 1

    ctx.dd = if (ctx.dayOfMonth != null) {
      ctx.dayOfMonth.value.trim.toInt
    } else 1

    ctx.yyyy = if (ctx.year != null) {
      convertYyToYyyy(ctx.year.getText.trim.toInt)
    } else {
      if (ctx.mm.toInt > currMonth || (ctx.mm.toInt == currMonth && ctx.dd.toInt > currDay)) (currYear - 1) else currYear

    }
  }

  override def exitDayOfMonth(ctx: DayOfMonthContext): Unit = {
    ctx.value = ctx.NUMBER().getText.trim
  }

  //
  //  override def exitYear(ctx: YearContext): Unit = {
  //    ctx.value = ctx.getText.trim
  //  }

  override def exitTimeAmPmDefault(ctx: TimeAmPmDefaultContext): Unit = {

    val timeObj = ctx.time()
    ctx.hh = timeObj.hh
    ctx.MM = timeObj.MM
    ctx.ss = timeObj.ss
    ctx.SSS = timeObj.SSS
    if (ctx.KWD_PM() != null && ctx.hh.toInt < 12) {
      ctx.hh = (ctx.hh.toInt + 12)
    }
  }

  override def exitTimeAmPmHour(ctx: TimeAmPmHourContext): Unit = {

    ctx.hh = ctx.NUMBER().getText.trim.toInt
    ctx.MM = 0
    ctx.ss = 0
    ctx.SSS = 0
    if (ctx.KWD_PM() != null && ctx.hh < 12) {
      ctx.hh = (ctx.hh + 12)
    }
  }

  override def exitTimeTz(ctx: TimeTzContext): Unit = {

    val timeObj = ctx.time()
    ctx.hh = timeObj.hh
    ctx.MM = timeObj.MM
    ctx.ss = timeObj.ss
    ctx.SSS = timeObj.SSS
    if (ctx.timeZone() != null) {

      val hhMM = ctx.timeZone().value.replaceAll("(?i)(GMT|UTC)", "").trim.split("(\\.|:)").map(_.trim.toInt)

      ctx.hh = (ctx.hh - hhMM(0))
      ctx.MM = if (hhMM(0) < 0) (ctx.MM + hhMM(1)) else (ctx.MM - hhMM(1))
    }
  }

  override def exitTimeComplete(ctx: TimeCompleteContext): Unit = {
    val availableDateParts = mutable.ArrayBuffer[String]()
    val timeNumArray = ctx.NUMBER().asScala
    ctx.hh = timeNumArray(0).getText.trim.toInt
    ctx.MM = timeNumArray(1).getText.trim.toInt
    var secondsText = timeNumArray(2).getText.trim
    if (secondsText.contains(".")) {
      ctx.SSS = secondsText.substring(secondsText.indexOf(".") + 1).toInt
      ctx.ss = secondsText.substring(0, secondsText.indexOf(".")).toInt
    } else if (secondsText.nonEmpty) {
      ctx.ss = secondsText.toInt
      ctx.SSS = 0
    }

    //    else throw new Exception(s"I cannot parse the time literal ${ctx.getText}")
  }

  override def exitTimePartial(ctx: TimePartialContext): Unit = {
    val timeNumArray = ctx.NUMBER().asScala
    val availableDateParts = mutable.ArrayBuffer[String]()

    var hoursText = timeNumArray(0).getText.trim
    var minutesText = timeNumArray(1).getText.trim

    ctx.hh = hoursText.toInt
    ctx.ss = 0
    ctx.SSS = 0

    if (minutesText.contains(".")) {
      ctx.ss = minutesText.substring(0, minutesText.indexOf(".")).toInt
      ctx.SSS = minutesText.substring(minutesText.indexOf(".") + 1).toInt
      ctx.MM = hoursText.toInt
    } else {
      ctx.MM = minutesText.toInt
    }
    if (ctx.hh > 23) {

      ctx.ss = ctx.MM
      ctx.MM = ctx.hh
    }
  }

  override def exitTimeZone(ctx: TimeZoneContext): Unit = {
    ctx.value = ctx.getText.toUpperCase.trim
  }


  override def exitFieldParen(ctx: FieldParenContext): Unit = {
    ctx.fieldText = ctx.getText
    ctx.fieldType = ctx.field().fieldType
    ctx.fieldValue = "(" + ctx.field().fieldValue + ")"
    ctx.fieldAlias = ctx.field().fieldAlias
    ctx.datatype = ctx.field().datatype
    if (ctx.field().entityNames != null)
      ctx.entityNames = ctx.field().entityNames.asScala.filter(ele => ele != null && ele.trim.nonEmpty).asJava
    ctx.dd = ctx.field().dd
    ctx.mm = ctx.field().mm
    ctx.yyyy = ctx.field().yyyy
    ctx.hh = ctx.field().hh
    ctx.MM = ctx.field().MM
    ctx.ss = ctx.field().ss
    ctx.SSS = ctx.field().SSS
    ctx.tz = ctx.field().tz
  }

  override def exitFieldArticle(ctx: FieldArticleContext): Unit = {
    ctx.fieldText = ctx.getText
    ctx.fieldType = ctx.field().fieldType
    ctx.fieldValue = ctx.field().fieldValue
    ctx.fieldAlias = ctx.field().fieldAlias
    ctx.datatype = ctx.field().datatype
    if (ctx.field().entityNames != null)
      ctx.entityNames = ctx.field().entityNames.asScala.filter(ele => ele != null && ele.trim.nonEmpty).asJava
    ctx.dd = ctx.field().dd
    ctx.mm = ctx.field().mm
    ctx.yyyy = ctx.field().yyyy
    ctx.hh = ctx.field().hh
    ctx.MM = ctx.field().MM
    ctx.ss = ctx.field().ss
    ctx.SSS = ctx.field().SSS
    ctx.tz = ctx.field().tz
  }

  override def exitFieldOperationDuration(ctx: FieldOperationDurationContext): Unit = {

    val field = ctx.field()
    val duration = ctx.duration()
    ctx.fieldText = ctx.getText
    ctx.fieldType = field.fieldType

    val fieldValue = if (ctx.SYM_PLUS() != null) {

      field.datatype match {

        case NuoDataType.Timestamp =>
          s"TIMESTAMP_ADD(${field.fieldValue}, INTERVAL ${duration.durationSeconds} SECOND)"

        case NuoDataType.Time =>
          s"TIME_ADD(${field.fieldValue}, INTERVAL ${duration.durationSeconds} SECOND)"

        case NuoDataType.Date =>
          s"DATE_ADD(${field.fieldValue}, INTERVAL ${duration.durationSeconds / (3600 * 24)} DAY)"

        case NuoDataType.String =>
          s"CONCAT(${field.fieldValue},${duration.getText})"

        case _ =>
          s"CONCAT(SAFE_CAST(${field.fieldValue} AS STRING),${duration.getText})"
      }
    } else {
      field.datatype match {

        case NuoDataType.Timestamp =>
          s"TIMESTAMP_SUB(${field.fieldValue}, INTERVAL ${duration.durationSeconds} SECOND)"

        case NuoDataType.Time =>
          s"TIME_SUBD(${field.fieldValue}, INTERVAL ${duration.durationSeconds} SECOND)"

        case NuoDataType.Date =>
          s"DATE_SUB(${field.fieldValue}, INTERVAL ${duration.durationSeconds / (3600 * 24)} DAY)"

        case NuoDataType.String =>
          s"REPLACE(${field.fieldValue},${duration.getText},'')"

        case _ =>
          s"REPLACE(SAFE_CAST(${field.fieldValue} AS STRING),${duration.getText},'')"
      }
    }

    ctx.fieldAlias = field.fieldAlias
    ctx.datatype = if (NuoDataTypeHandler.isDateType(field.datatype)) field.datatype else NuoDataType.String
    ctx.fieldValue = fieldValue
    ctx.isAggregated = field.isAggregated
    if (ctx.field().entityNames != null)
      ctx.entityNames = field.entityNames.asScala.filter(ele => ele != null && ele.trim.nonEmpty).asJava
    //    recordAlias(ctx.fieldValue, ctx.fieldAlias)
  }


  override def exitFieldOperationPlus(ctx: FieldOperationPlusContext): Unit = {

    val fields = ctx.field().asScala
    ctx.fieldText = ctx.getText
    ctx.fieldType = fields.map(_.fieldType).product

    val datatypes = fields.map(_.datatype)
    val (fieldValue, targetDatatype) =
      if (ctx.duration() != null) {

        val field = ctx.field(0)
        val duration = ctx.duration()

        (
          field.datatype match {

            case NuoDataType.Timestamp =>
              s"TIMESTAMP_ADD(${field.fieldValue}, INTERVAL ${duration.durationSeconds} SECOND)"

            case NuoDataType.Time =>
              s"TIME_ADD(${field.fieldValue}, INTERVAL ${duration.durationSeconds} SECOND)"

            case NuoDataType.Date =>
              s"DATE_ADD(${field.fieldValue}, INTERVAL ${duration.durationSeconds / (3600 * 24)} DAY)"

            case NuoDataType.String =>
              s"CONCAT(${field.fieldValue},${duration.getText})"

            case _ =>
              s"CONCAT(SAFE_CAST(${field.fieldValue} AS STRING),${duration.getText})"
          },
          if (NuoDataTypeHandler.isDateType(ctx.field(0).datatype))
            ctx.field(0).datatype
          else
            NuoDataType.String
        )
      } else if (datatypes.forall(NuoDataTypeHandler.isNumberType)) {
        (
          fields.map(_.fieldValue).mkString(" + "),
          if (datatypes
            .forall(ele =>
              List(
                NuoDataType.Int64,
                NuoDataType.Integer
              )
                .map(_.toUpperCase)
                .contains(ele.toUpperCase)
            )
          )
            NuoDataType.Int64
          else
            NuoDataType.Float64
        )
      } else {
        val field1 = fields(0)
        val field2 = fields(1)
        val exp1 = if (!field1.datatype.equalsIgnoreCase(NuoDataType.String)) {
          s"SAFE_CAST(${field1.fieldValue} AS STRING)"
        } else
          field1.fieldValue
        val exp2 = if (!field2.datatype.equalsIgnoreCase(NuoDataType.String)) {
          s"SAFE_CAST(${field2.fieldValue} AS STRING)"
        } else field2.fieldValue
        (s"CONCAT($exp1, $exp2)", NuoDataType.String)
      }
    ctx.fieldAlias = fields.filter(ele => ele.fieldAlias != null && ele.fieldAlias.trim.nonEmpty).map(_.fieldAlias).mkString("_")
    ctx.datatype = targetDatatype
    ctx.fieldValue = fieldValue
    ctx.isAggregated = !fields.forall(!_.isAggregated)
    ctx.entityNames =
      fields
        .filter(ele =>
          ele.entityNames != null
            && ele.entityNames.asScala
            .filter(ele => ele != null && ele.trim.nonEmpty)
            .nonEmpty
        )
        .flatMap(_.entityNames.asScala).asJava
    //    recordAlias(ctx.fieldValue, ctx.fieldAlias)

  }

  override def exitFieldOperationMinus(ctx: FieldOperationMinusContext): Unit = {

    val fields = ctx.field().asScala
    ctx.fieldText = ctx.getText
    ctx.fieldType = fields.map(_.fieldType).product


    val castedValsAndHighestDatatype =
      NuoDataTypeHandler
        .castDataTypes(
          grammarFields =
            fields
              .map(field =>
                NuoGrammarField(
                  FieldText = field.fieldText,
                  FieldType = field.fieldType,
                  FieldValue = field.fieldValue,
                  DateParts = NuoDateParts(
                    field.dd,
                    field.mm,
                    field.yyyy,
                    field.hh,
                    field.MM,
                    field.ss,
                    field.SSS,
                    field.tz
                  ),
                  Datatype = field.datatype,
                  EntityNames = field.entityNames
                )
              ).toList,
          expectedDataType = null,
          nuoGrammarListener = this
        )
    val highestOrderDatatype = castedValsAndHighestDatatype._2
    val castedValues = castedValsAndHighestDatatype._1

    val fieldValue =
      if (ctx.duration() != null) {
        val field = ctx.field(0)
        val duration = ctx.duration()
        field.datatype match {

          case NuoDataType.Timestamp =>
            s"TIMESTAMP_SUB(${field.fieldValue}, INTERVAL ${duration.durationSeconds} SECOND)"

          case NuoDataType.Time =>
            s"TIME_SUBD(${field.fieldValue}, INTERVAL ${duration.durationSeconds} SECOND)"

          case NuoDataType.Date =>
            s"DATE_SUB(${field.fieldValue}, INTERVAL ${duration.durationSeconds / (3600 * 24)} DAY)"

          case NuoDataType.String =>
            s"REPLACE(${field.fieldValue},${duration.getText},'')"

          case _ =>
            s"REPLACE(SAFE_CAST(${field.fieldValue} AS STRING),${duration.getText},'')"
        }
      } else if (NuoDataTypeHandler.isNumberType(highestOrderDatatype)) {
        castedValues.mkString(" - ")
      } else if (highestOrderDatatype.equalsIgnoreCase(NuoDataType.Timestamp)) {

        s"TIMESTAMP_DIFF(${castedValues.head},${castedValues(1)},SECOND)"

      } else if (highestOrderDatatype.equalsIgnoreCase(NuoDataType.Time)) {

        s"TIME_DIFF(${castedValues.head},${castedValues(1)},SECOND)"

      } else if (highestOrderDatatype.equalsIgnoreCase(NuoDataType.Date)) {

        s"DATE_DIFF(${castedValues.head},${castedValues(1)},DAY)"

      } else {
        val field1 = fields(0)
        val field2 = fields(1)

        val exp1 =
          if (!field1.datatype.equalsIgnoreCase(NuoDataType.String)) {
            s"SAFE_CAST(${field1.fieldValue} AS STRING)"
          } else
            field1.fieldValue

        val exp2 =
          if (!field2.datatype.equalsIgnoreCase(NuoDataType.String)) {
            s"SAFE_CAST(${field2.fieldValue} AS STRING)"
          } else field2.fieldValue
        s"REPLACE($exp1, $exp2, '')"
      }
    ctx.fieldAlias = fields.filter(ele => ele.fieldAlias != null && ele.fieldAlias.trim.nonEmpty).map(_.fieldAlias).mkString("_")
    ctx.datatype =
      if (ctx.duration() != null) {
        if (NuoDataTypeHandler.isDateType(ctx.field(0).datatype)) ctx.field(0).datatype else NuoDataType.String
      } else
        highestOrderDatatype
    ctx.fieldValue = fieldValue
    ctx.isAggregated = !fields.forall(!_.isAggregated)
    ctx.entityNames = fields.filter(ele => ele.entityNames != null && ele.entityNames.asScala.filter(ele => ele != null && ele.trim.nonEmpty).nonEmpty).flatMap(_.entityNames.asScala).asJava
    //    recordAlias(ctx.fieldValue, ctx.fieldAlias)
  }

  override def exitFieldOperationOther(ctx: FieldOperationOtherContext): Unit = {

    val fields = ctx.field().asScala
    ctx.fieldText = ctx.getText
    ctx.fieldType = fields.map(_.fieldType).product

    val datatypes = fields.map(_.datatype)
    val fieldValue =
      if (datatypes.forall(NuoDataTypeHandler.isNumberType)) {

        if (ctx.SYM_STAR() != null) {
          s"${fields(0).fieldValue} * ${fields(1).fieldValue}"
        } else if (ctx.SYM_F_SLASH() != null) {
          s"SAFE_DIVIDE(${fields(0).fieldValue},${fields(1).fieldValue})"
        } else if (ctx.SYM_CARET() != null) {
          s"POWER(${fields(0).fieldValue},${fields(1).fieldValue})"
        } else if (ctx.SYM_PERCENT() != null) {
          s"IF(${fields(1).fieldValue} = 0, NULL, MOD(${fields(0).fieldValue},${fields(1).fieldValue}))"
        } else {
          NuoRequestHandler.reportErrorToUser(new Exception(s"I could not recognize the operator in ${ctx.getText}."))
          throw new Exception("Unreachable Code statement")
        }
      } else {
        NuoRequestHandler.reportErrorToUser(new Exception(s"I cannot perform the division operation on datatype other than number."))
        throw new Exception("Unreachable Code statement")
      }
    ctx.fieldAlias = fields.filter(ele => ele.fieldAlias != null && ele.fieldAlias.trim.nonEmpty).map(_.fieldAlias).mkString("_")
    ctx.datatype = NuoDataType.Float64
    ctx.fieldValue = fieldValue
    ctx.isAggregated = !fields.forall(!_.isAggregated)
    ctx.entityNames =
      fields
        .filter(ele =>
          ele.entityNames != null &&
            ele.entityNames.asScala
              .filter(ele => ele != null && ele.trim.nonEmpty)
              .nonEmpty
        )
        .flatMap(_.entityNames.asScala).asJava
    recordAlias(ctx.fieldValue, ctx.fieldAlias)
  }

  override def exitFieldOneArgFunction(ctx: FieldOneArgFunctionContext): Unit = {

    ctx.fieldText = ctx.getText
    ctx.fieldType = ctx.field().fieldType

    var functionName = ctx.functionOneArg().value

    val field = ctx.field()
    ctx.fieldValue = s"$functionName(${field.fieldValue})"

    ctx.fieldAlias = field.fieldAlias

    ctx.datatype = ctx.functionOneArg().targetDatatype
    ctx.isAggregated = field.isAggregated || ctx.functionOneArg().isAggregated
    if (ctx.isAggregated) {
      ctx.fieldType *= NuoEvaEnglishListener.FIELD_TYPE_AGG
    }
    if (field.entityNames != null)
      ctx.entityNames = field.entityNames.asScala.filter(ele => ele != null && ele.trim.nonEmpty).asJava
    //    recordAlias(ctx.fieldValue, ctx.fieldAlias)
  }

  override def exitFieldSubstrFunction(ctx: FieldSubstrFunctionContext): Unit = {

    ctx.fieldText = ctx.getText
    val fields = ctx.field().asScala
    ctx.fieldType = fields.map(_.fieldType).product

    if (fields.length < 2) {
      NuoRequestHandler.reportErrorToUser(new Exception(s"I cannot perform the substring operation without start index for ${ctx.getText}"))
      throw new Exception("Unreachable Code statement")

    } else if (fields.length > 3) {
      NuoRequestHandler.reportErrorToUser(new Exception(s"I cannot perform the substring operation with more than three arguments for ${ctx.getText}"))
      throw new Exception("Unreachable Code statement")

    } else if (fields.length == 2) {

      val field1 = fields(0).fieldValue

      val field2 = fields(1).fieldValue

      ctx.fieldValue = s"SUBSTR($field1,$field2)"
    } else {

      val field1 = fields(0).fieldValue

      val field2 = fields(1).fieldValue

      val field3 = fields(2).fieldValue

      ctx.fieldValue = s"SUBSTR($field1,$field2,$field3)"
    }

    ctx.fieldAlias = fields.filter(ele => ele.fieldAlias != null && ele.fieldAlias.trim.nonEmpty).map(_.fieldAlias).mkString("_")
    ctx.datatype = NuoDataType.String
    ctx.isAggregated = fields.map(_.isAggregated).foldLeft(false)((l, r) => l || r)
    ctx.entityNames = ctx.field().asScala.filter(ele => ele.entityNames != null && ele.entityNames.asScala.filter(ele => ele != null && ele.trim.nonEmpty).nonEmpty).flatMap(_.entityNames.asScala).asJava
    //    ctx.availableDateParts = fields.flatMap(_.availableDateParts.asScala).toList.asJava
    //    recordAlias(ctx.fieldValue, ctx.fieldAlias)
  }

  override def exitFieldTwoArgFunction(ctx: FieldTwoArgFunctionContext): Unit = {

    ctx.fieldText = ctx.getText
    val fields = ctx.field().asScala
    ctx.fieldType = fields.map(_.fieldType).product


    var functionName = ctx.functionTwoArgs().value
    var targetDatatype = NuoDataType.Float64

    if (fields.length != 2) {
      NuoRequestHandler.reportErrorToUser(new Exception(s"I cannot perform the ${ctx.functionTwoArgs().getText} operation wiht ${fields.length} arguments for ${ctx.getText}"))
      throw new Exception("Unreachable Code statement")
    } else {

      val field1 = fields(0).fieldValue

      val field2 = fields(1).fieldValue

      ctx.fieldValue = s"$functionName($field1, $field2)"
    }

    ctx.fieldAlias = fields.filter(ele => ele.fieldAlias != null && ele.fieldAlias.trim.nonEmpty).map(_.fieldAlias).mkString("_")
    ctx.datatype = targetDatatype
    ctx.isAggregated = fields.map(_.isAggregated).foldLeft(false)((l, r) => l || r)
    ctx.entityNames =
      ctx.field().asScala
        .filter(ele =>
          ele.entityNames != null
            && ele.entityNames.asScala
            .filter(ele => ele != null
              && ele.trim.nonEmpty)
            .nonEmpty)
        .flatMap(_.entityNames.asScala)
        .asJava
    //    recordAlias(ctx.fieldValue, ctx.fieldAlias)
  }

  override def exitFieldDivFunction(ctx: FieldDivFunctionContext): Unit = {

    ctx.fieldText = ctx.getText
    val fields = ctx.field().asScala
    ctx.fieldType = fields.map(_.fieldType).product


    var functionName = "DIV"
    var targetDatatype = NuoDataType.Int64

    val datatypes = fields.map(_.datatype)
    if (!datatypes.forall(NuoDataTypeHandler.isNumberType)) {
      //      NuoRequestHandler.reportErrorToUser(new Exception(s"I cannot perform the division operation on datatypes in ${ctx.getText}"))
      //      throw new Exception("Unreachable Code statement")
    } else if (fields.length != 2) {
      NuoRequestHandler.reportErrorToUser(new Exception(s"I cannot perform the division operation with ${fields.length} arguments for ${ctx.getText}"))
      throw new Exception("Unreachable Code statement")
    } else {
      val field1 =
        if (!fields(0).datatype.equalsIgnoreCase(NuoDataType.Int64))
          s"SAFE_CAST(${fields(0).fieldValue} AS INT64)"
        else
          fields(0).fieldValue

      val field2 =
        if (!fields(1).datatype.equalsIgnoreCase(NuoDataType.Int64))
          s"SAFE_CAST(${fields(1).fieldValue} AS INT64)"
        else
          fields(1).fieldValue

      ctx.fieldValue = s"$functionName($field1, $field2)"
    }

    ctx.fieldAlias = fields.filter(ele => ele.fieldAlias != null && ele.fieldAlias.trim.nonEmpty).map(_.fieldAlias).mkString("_")
    ctx.datatype = targetDatatype
    ctx.isAggregated = fields.map(_.isAggregated).foldLeft(false)((l, r) => l || r)
    ctx.entityNames = ctx.field().asScala.filter(ele => ele.entityNames != null && ele.entityNames.asScala.filter(ele => ele != null && ele.trim.nonEmpty).nonEmpty).flatMap(_.entityNames.asScala).asJava
    //    recordAlias(ctx.fieldValue, ctx.fieldAlias)
  }

  override def exitFunctionOneArg(ctx: FunctionOneArgContext): Unit = {
    var targetDatatype = NuoDataType.Float64
    ctx.value =
      if (ctx.FUNC_STD_DEV() != null) {
        ctx.isAggregated = true
        "STDDEV"
      }
      else if (ctx.FUNC_VARIANCE() != null) {
        ctx.isAggregated = true
        "VARIANCE"
      }
      else if (ctx.FUNC_ABSOLUTE() != null)
        "ABS"
      else if (ctx.FUNC_SIGN() != null)
        "SIGN"
      else if (ctx.FUNC_SQRT() != null)
        "SQRT"
      else if (ctx.FUNC_EXPONENTIAL() != null)
        "EXP"
      else if (ctx.FUNC_LOG() != null)
        "LOG"
      else if (ctx.FUNC_ROUND() != null) {
        targetDatatype = NuoDataType.Int64
        "ROUND"
      }
      else if (ctx.FUNC_COS() != null)
        "COS"
      else if (ctx.FUNC_SIN() != null)
        "SIN"
      else if (ctx.FUNC_TAN() != null)
        "TAN"
      else if (ctx.FUNC_MD5() != null) {
        targetDatatype = NuoDataType.String
        "MD5"
      }
      else if (ctx.FUNC_SHA1() != null) {
        targetDatatype = NuoDataType.String
        "SHA1"
      }
      else if (ctx.FUNC_SHA256() != null) {
        targetDatatype = NuoDataType.String
        "SHA256"
      }
      else if (ctx.FUNC_SHA512() != null) {
        targetDatatype = NuoDataType.String
        "SHA512"
      } else ctx.getText
    ctx.targetDatatype = targetDatatype
  }

  override def exitFunctionTwoArgs(ctx: FunctionTwoArgsContext): Unit = {
    var targetDatatype = NuoDataType.Float64
    ctx.value =
      if (ctx.FUNC_CORRELATION() != null)
        "CORR"
      else ctx.getText
    ctx.targetDatatype = targetDatatype
  }


  override def exitFieldNumber(ctx: FieldNumberContext): Unit = {
    val aggFunction = ctx.numberField().aggFunction
    var fieldType = 1
    val fieldValue = ctx.numberField().value
    if (aggFunction != null && aggFunction.nonEmpty) {
      fieldType *= NuoEvaEnglishListener.FIELD_TYPE_AGG
      ctx.isAggregated = true
    }


    ctx.fieldText = ctx.getText
    fieldType *= {
      if (ctx.numberField().value.contains(".") && !(aggFunction != null && aggFunction.equalsIgnoreCase("COUNT")))
        NuoEvaEnglishListener.FIELD_TYPE_FLOAT
      else NuoEvaEnglishListener.FIELD_TYPE_INT
    }
    ctx.fieldType = fieldType
    ctx.fieldValue = fieldValue
    ctx.datatype = NuoEvaEnglishListener.convertFieldTypeToDataType(fieldType)
    if (ctx.numberField().value.trim.nonEmpty)
      ctx.entityNames = List(ctx.numberField().value).asJava

  }

  override def exitFieldDefault(ctx: FieldDefaultContext): Unit = {

    ctx.fieldType = ctx.fieldContent().fieldType

    ctx.fieldText = ctx.getText
    ctx.fieldValue = ctx.fieldContent().fieldValue
    ctx.fieldAlias = ctx.fieldContent().fieldAlias
    ctx.datatype = ctx.fieldContent().datatype
    ctx.entityNames = List(ctx.fieldContent().tableName).asJava
  }

  override def exitFieldAgg(ctx: FieldAggContext): Unit = {
    ctx.fieldText = ctx.getText
    ctx.fieldValue = ctx.aggregateField().fieldValue
    ctx.fieldAlias = ctx.aggregateField().fieldAlias

    ctx.datatype = ctx.aggregateField().datatype
    ctx.isAggregated = true
    ctx.fieldType = ctx.aggregateField().fieldType
  }

  override def exitFieldDate(ctx: FieldDateContext): Unit = {

    ctx.dd = ctx.dateField().dd
    ctx.mm = ctx.dateField().mm
    ctx.yyyy = ctx.dateField().yyyy

    ctx.fieldText = ctx.getText
    ctx.fieldType = ctx.dateField().fieldType

    ctx.fieldValue = ctx.dateField().fieldValue

    ctx.datatype = NuoEvaEnglishListener.convertFieldTypeToDataType(ctx.fieldType)

  }

  override def exitFieldDuration(ctx: FieldDurationContext): Unit = {

    ctx.fieldType = ctx.durationField().fieldType
    ctx.fieldText = ctx.getText
    ctx.fieldValue = ctx.durationField().value

    ctx.datatype = NuoEvaEnglishListener.convertFieldTypeToDataType(ctx.durationField().fieldType)
  }


  val RANK_FUNCTION_TYPE_DEFAULT = 2
  val RANK_FUNCTION_TYPE_TOP = 13
  val RANK_FUNCTION_TYPE_LAST = 3
  val RANK_FUNCTION_TYPE_LEADING = 5
  val RANK_FUNCTION_TYPE_FOLLOWING = 7
  val RANK_FUNCTION_TYPE_NTH = 11

  case class RecognizedRankField(RankColumnAlias: String,
                                 SelectFieldDatatype: String)

  def recognizeRankField(fieldContent: FieldContentContext,
                         numberValue: String,
                         overClause: OverClauseContext,
                         rankFunctionType: Int): RecognizedRankField = {

    var rankColAlias: String = s"Rank_Of_${fieldContent.fieldAlias}"


    var targetDatatype = fieldContent.datatype
    val rankFunction =
      if (rankFunctionType % RANK_FUNCTION_TYPE_LEADING == 0) {
        rankColAlias = s"LEADING_${fieldContent.fieldAlias}"
        new mutable.StringBuilder(s"LEAD(${fieldContent.fieldValue}) OVER(")
      }
      else if (rankFunctionType % RANK_FUNCTION_TYPE_FOLLOWING == 0) {
        rankColAlias = s"FOLLOWING_${fieldContent.fieldAlias}"
        new mutable.StringBuilder(s"LAG(${fieldContent.fieldValue}) OVER(")
      }
      //      else if (rankFunctionType % RANK_FUNCTION_TYPE_NTH == 0 && numberValue != null)
      //        new mutable.StringBuilder(s"NTH_VALUE(${fieldContent.fieldValue},${numberValue} IGNORE NULLS) OVER(")
      else {
        targetDatatype = NuoDataType.Int64
        new mutable.StringBuilder("RANK() OVER(")
      }

    if (overClause.partitionFields != null && overClause.partitionFields.size() > 0) {

      val partitionFields = overClause.partitionFields.asScala
      rankFunction.append(s"PARTITION BY ${partitionFields.mkString(",")}")
      rankColAlias += "_" + partitionFields.map(ele => ele.substring(ele.indexOf(".") + 1)).mkString("_")
    }

    if (overClause.orderFields != null && overClause.orderFields.size() > 0) {

      val orderFields = overClause.orderFields.asScala
      var ordExp =
        if (rankFunctionType % RANK_FUNCTION_TYPE_LAST == 0) "ASC"
        else "DESC"
      rankFunction
        .append(s" ORDER BY" +
          s" ${
            orderFields
              .map(ele => ele + s" $ordExp")
              .mkString(",")
          }"
        )
      //      rankColAlias += "_" + orderFields.map(ele => ele.substring(ele.indexOf(".") + 1, ele.length - 1)).mkString("_")
      rankColAlias = rankColAlias.replace("`", "")
    }

    rankFunction.append(s") AS $rankColAlias")

    if (!this.currAnalysisRecognitionData.RankSelectClause.map(_.toLowerCase.trim).contains(rankFunction.toString()))
      this.currAnalysisRecognitionData.RankSelectClause :+= rankFunction.toString()

    if (numberValue != null
      && (
      rankFunctionType % RANK_FUNCTION_TYPE_NTH == 0
        || rankFunctionType % RANK_FUNCTION_TYPE_TOP == 0
        || rankFunctionType % RANK_FUNCTION_TYPE_LAST == 0
      )
    ) {
      var rankCluase = ""
      if (this.currAnalysisRecognitionData.RankWhereClause.isDefined && this.currAnalysisRecognitionData.RankWhereClause.get.trim.nonEmpty) {
        rankCluase = this.currAnalysisRecognitionData.RankWhereClause.get
        rankCluase += " AND "
      }
      val operator =
        if (rankFunctionType % RANK_FUNCTION_TYPE_NTH == 0) "="
        else "<="
      val currRankCondition = s" $rankColAlias $operator $numberValue"
      if (!rankCluase.contains(currRankCondition))
        this.currAnalysisRecognitionData.RankWhereClause = Some(rankCluase + currRankCondition)
    }

    RecognizedRankField(rankColAlias, targetDatatype)
  }

  override def exitRankFieldDefault(ctx: RankFieldDefaultContext): Unit = {

    val recognizedRankField = recognizeRankField(
      ctx.fieldContent(),
      numberValue = null,
      overClause = ctx.overClause(),
      rankFunctionType = RANK_FUNCTION_TYPE_DEFAULT
    )
    ctx.fieldText = ctx.getText
    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_RANK_OVER * ctx.fieldContent().fieldType
    ctx.fieldValue = ctx.fieldContent().fieldValue
    ctx.fieldAlias = ctx.fieldContent().fieldAlias

    ctx.datatype = ctx.fieldContent().datatype
    ctx.entityNames = List(ctx.fieldContent().tableName).asJava
  }

  override def exitRankFieldFirst(ctx: RankFieldFirstContext): Unit = {


    val recognizedRankField =
      recognizeRankField(
        ctx.fieldContent(),
        numberValue = if (ctx.numberField() != null) ctx.numberField().value else null,
        overClause = ctx.overClause(),
        rankFunctionType = RANK_FUNCTION_TYPE_DEFAULT * RANK_FUNCTION_TYPE_TOP
      )
    ctx.fieldText = ctx.getText
    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_RANK_OVER * ctx.fieldContent().fieldType
    ctx.fieldValue = ctx.fieldContent().fieldValue
    ctx.fieldAlias = ctx.fieldContent().fieldAlias

    ctx.datatype = ctx.fieldContent().datatype
    ctx.entityNames = List(ctx.fieldContent().tableName).asJava
  }

  override def exitRankFieldLast(ctx: RankFieldLastContext): Unit = {


    val recognizedRankField =
      recognizeRankField(
        ctx.fieldContent(),
        numberValue = if (ctx.numberField() != null) ctx.numberField().value else null,
        overClause = ctx.overClause(),
        rankFunctionType = RANK_FUNCTION_TYPE_DEFAULT * RANK_FUNCTION_TYPE_LAST
      )
    ctx.fieldText = ctx.getText
    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_RANK_OVER * ctx.fieldContent().fieldType
    ctx.fieldValue = ctx.fieldContent().fieldValue
    ctx.fieldAlias = ctx.fieldContent().fieldAlias

    ctx.datatype = ctx.fieldContent().datatype
    ctx.entityNames = List(ctx.fieldContent().tableName).asJava
  }

  override def exitRankFieldNth(ctx: RankFieldNthContext): Unit = {

    val recognizedRankField =
      recognizeRankField(
        ctx.fieldContent(),
        numberValue = if (ctx.numberField() != null) ctx.numberField().value else null,
        overClause = ctx.overClause(),
        rankFunctionType = RANK_FUNCTION_TYPE_NTH
      )
    ctx.fieldText = ctx.getText
    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_RANK_OVER * ctx.fieldContent().fieldType
    ctx.fieldValue = ctx.fieldContent().fieldValue
    ctx.fieldAlias = ctx.fieldContent().fieldAlias

    ctx.datatype = ctx.fieldContent().datatype
    ctx.entityNames = List(ctx.fieldContent().tableName).asJava
  }

  override def exitRankFieldNthInverse(ctx: RankFieldNthInverseContext): Unit = {

    val recognizedRankField =
      recognizeRankField(
        ctx.fieldContent(),
        numberValue = if (ctx.numberField() != null) ctx.numberField().value else null,
        overClause = ctx.overClause(),
        rankFunctionType = RANK_FUNCTION_TYPE_NTH * RANK_FUNCTION_TYPE_LAST
      )
    ctx.fieldText = ctx.getText
    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_RANK_OVER * ctx.fieldContent().fieldType
    ctx.fieldValue = ctx.fieldContent().fieldValue
    ctx.fieldAlias = ctx.fieldContent().fieldAlias

    ctx.datatype = ctx.fieldContent().datatype
    ctx.entityNames = List(ctx.fieldContent().tableName).asJava
  }

  override def exitRankFieldLeading(ctx: RankFieldLeadingContext): Unit = {

    val recognizedRankField =
      recognizeRankField(
        ctx.fieldContent(),
        numberValue = null,
        overClause = ctx.overClause(),
        rankFunctionType = RANK_FUNCTION_TYPE_LEADING
      )
    ctx.fieldText = ctx.getText
    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_RANK_OVER * ctx.fieldContent().fieldType
    ctx.fieldValue = ctx.fieldContent().fieldValue
    ctx.fieldAlias = ctx.fieldContent().fieldAlias

    ctx.datatype = ctx.fieldContent().datatype
    ctx.entityNames = List(ctx.fieldContent().tableName).asJava
  }

  override def exitRankFieldFollowing(ctx: RankFieldFollowingContext): Unit = {

    val recognizedRankField =
      recognizeRankField(
        ctx.fieldContent(),
        numberValue = null,
        overClause = ctx.overClause(),
        rankFunctionType = RANK_FUNCTION_TYPE_FOLLOWING
      )
    ctx.fieldText = ctx.getText
    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_RANK_OVER * ctx.fieldContent().fieldType
    ctx.fieldValue = ctx.fieldContent().fieldValue
    ctx.fieldAlias = ctx.fieldContent().fieldAlias

    ctx.datatype = ctx.fieldContent().datatype
    ctx.entityNames = List(ctx.fieldContent().tableName).asJava
  }

  override def exitOverClausePartOrd(ctx: OverClausePartOrdContext): Unit = {

    val partitionFieldText = ctx.fieldContent().fieldValue

    val partitionField =
      if (NuoEvaEnglishListener.isGrammarTestMode && !partitionFieldText.contains("`.`"))
        s"`X`.`${partitionFieldText}`"
      else partitionFieldText

    recordAlias(fieldValue = ctx.fieldContent().fieldValue, fieldAlias = ctx.fieldContent().fieldAlias)
    this.recognizedRankPartFields += List(
      partitionField,
      ctx.fieldContent().fieldType.toString,
      ctx.fieldContent().datatype,
      ctx.fieldContent().fieldAlias
    )

    var orderFieldText: String = null
    if (ctx.KWD_ORDER_CLAUSE() != null && ctx.field() != null) {

      orderFieldText = ctx.field().fieldValue

      var qualifiedText = if (NuoEvaEnglishListener.isGrammarTestMode && !orderFieldText.contains("`.`"))
        s"`X`.`${orderFieldText}`"
      else s"$orderFieldText"

      var orderFieldType = ctx.field().fieldType
      if (ctx.field().fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG != 0) {
        orderFieldType = orderFieldType.toInt * NuoEvaEnglishListener.FIELD_TYPE_AGG
        orderFieldText = if (NuoDataTypeHandler.isNumberType(ctx.field().datatype)) {
          recordAlias(fieldValue = s"SUM($qualifiedText)", fieldAlias = ctx.field().fieldAlias)
          s"SUM($qualifiedText)"
        } else {
          recordAlias(fieldValue = s"MAX($qualifiedText)", fieldAlias = ctx.field().fieldAlias)
          s"MAX($qualifiedText)"
        }
      } else {
        orderFieldText = s"$qualifiedText"
      }
      this.recognizedRankOrdFields += List(
        orderFieldText,
        orderFieldType.toString,
        ctx.field().datatype,
        ctx.field().fieldAlias
      )
    }

    if (partitionField != null)
      ctx.partitionFields = List(ctx.fieldContent().fieldAlias).asJava
    if (orderFieldText != null)
      ctx.orderFields = List(ctx.field().fieldAlias).asJava
  }

  override def exitOverClauseOrdPart(ctx: OverClauseOrdPartContext): Unit = {

    var orderFieldText = ctx.field().fieldValue

    var qualifiedText = if (NuoEvaEnglishListener.isGrammarTestMode && !orderFieldText.contains("`.`"))
      s"`X`.`${orderFieldText}`"
    else orderFieldText

    var orderFieldType = ctx.field().fieldType
    if (ctx.field().fieldType % NuoEvaEnglishListener.FIELD_TYPE_AGG != 0) {
      orderFieldType = orderFieldType.toInt * NuoEvaEnglishListener.FIELD_TYPE_AGG
      orderFieldText = if (NuoDataTypeHandler.isNumberType(ctx.field().datatype)) {
        recordAlias(fieldValue = s"SUM($qualifiedText)", fieldAlias = ctx.field().fieldAlias)
        s"SUM($qualifiedText)"
      } else {
        recordAlias(fieldValue = s"MAX($qualifiedText)", fieldAlias = ctx.field().fieldAlias)
        s"MAX($qualifiedText)"
      }
    } else {
      s"$qualifiedText"
    }
    this.recognizedRankOrdFields += List(
      orderFieldText,
      orderFieldType.toString,
      ctx.field().datatype,
      ctx.field().fieldAlias
    )

    val partitionFieldText =
      if ((ctx.KWD_PARTITION_CLAUSE() != null | ctx.KWD_BY() != null | ctx.KWD_PER() != null) && ctx.fieldContent() != null) {
        var partitionFieldText: String = ctx.fieldContent().fieldValue

        if (NuoEvaEnglishListener.isGrammarTestMode && !partitionFieldText.contains("`.`"))
          s"`X`.`${partitionFieldText}`"
        else partitionFieldText
      } else null

    if (partitionFieldText != null) {

      recordAlias(fieldValue = ctx.fieldContent().fieldValue, fieldAlias = ctx.fieldContent().fieldAlias)
      this.recognizedRankPartFields += List(
        partitionFieldText,
        ctx.fieldContent().fieldType.toString,
        ctx.fieldContent().datatype,
        ctx.fieldContent().fieldAlias
      )
      ctx.partitionFields = List(ctx.fieldContent().fieldAlias).asJava
    }
    if (orderFieldText != null)
      ctx.orderFields = List(ctx.field().fieldAlias).asJava
  }

  override def exitFieldContentQualified(ctx: FieldContentQualifiedContext): Unit = {

    val params = ctx.BOXED_STRING().asScala.map(_.getText)
    ctx.fieldValue = ctx.getText
    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_DEFAULT

    val tableName = params.head.trim
    val columnName = params(1).trim
    val datatype = params(2).trim

    ctx.tableName = tableName
    ctx.columnName = columnName
    ctx.datatype = datatype

    ctx.fieldValue = s"`$tableName`.`$columnName`"
    ctx.fieldAlias = columnName
    recognizedFields += NuoField("", tableName, columnName, datatype)
    recordAlias(ctx.fieldValue, ctx.fieldAlias)
  }

  override def exitFieldContentDefault(ctx: FieldContentDefaultContext): Unit = {

    if (NuoEvaEnglishListener.isGrammarTestMode) {
      ctx.tableName = "X"
      ctx.fieldValue = ctx.getText.trim
    } else
      ctx.fieldValue = s"'${ctx.getText}'"
    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_QUOTED
    ctx.datatype = NuoDataType.String
    ctx.fieldAlias = ctx.getText.trim.replaceAll("[^\\w]+", "_")

    recognizedFields += NuoField("", if (NuoEvaEnglishListener.isGrammarTestMode) ctx.tableName else "", ctx.fieldValue, ctx.datatype)
  }

  override def exitFieldContentQuoted(ctx: FieldContentQuotedContext): Unit = {

    if (NuoEvaEnglishListener.isGrammarTestMode) {
      ctx.fieldValue = ctx.getText.trim
    } else {
      ctx.fieldValue = s"'${ctx.getText}'"
    }

    ctx.fieldType = NuoEvaEnglishListener.FIELD_TYPE_QUOTED
    ctx.datatype = NuoDataType.String
    ctx.fieldAlias = ctx.getText.trim.replaceAll("[^\\w]+", "_")
    recognizedFields += NuoField("", "", ctx.fieldValue, ctx.datatype)
  }

}


object NuoEvaEnglishListener {

  /*
  * 28FEb2018:Pulkit: Collection of properties that are universal across tenant.
  * */

  var isGrammarTestMode: Boolean = false

  val NuoEncryptionKeysDir: String = "Credentials/NuoEncryption/"
  val NuoEvaCredentialsDir: String = "Credentials/Eva/"
  val NuoCrmCredentialsDir: String = "Credentials/Crm/"
  val NuoEvaUsernameFile: String = s"${NuoEvaCredentialsDir}Usernames"
  val NuoCrmUsernameFile: String = s"${NuoCrmCredentialsDir}Usernames"
  val NuoEvaPasswordFile: String = s"${NuoEvaCredentialsDir}Passwords"
  val NuoCrmPasswordFile: String = s"${NuoCrmCredentialsDir}Passwords"
  val NuoCanvasAccountsConfigFile: String = s"Configuration/Accounts"

  def NuoSessionFilePrefix: String = "Sessions/"

  val NuoSessionFileSuffix: String = ".json"

  val NuoCrmUsersMetadataFileName: String = "CRM/Metadata/Users.txt"
  val GoogleMapApiKey = "xxx"

  //
  val SessionMaxInactiveTimeMillis = 60 * 60 * 1000L
  val ServerDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

  var nuoTenantDetails: NuoTenantDetails = _
  var nuoStorageDetails: NuoStorageDetails = _

  private var nuoStorageFieldsRef: List[NuoField] = List()

  def nuoStorageFields: List[NuoField] = if (nuoStorageFieldsRef.isEmpty) {

    nuoStorageFieldsRef = nuoStorageDetails.NuoDatasets.flatMap(_.NuoEntities.flatMap(_.Fields))
    nuoStorageFieldsRef
  } else nuoStorageFieldsRef

  private var nuoStorageEntitiesRef: List[NuoEntity] = List()

  def nuoStorageEntities: List[NuoEntity] = if (nuoStorageEntitiesRef.isEmpty) {

    nuoStorageEntitiesRef = nuoStorageDetails.NuoDatasets.flatMap(_.NuoEntities)
    nuoStorageEntitiesRef
  } else nuoStorageEntitiesRef

  var nuoEvaMessage: NuoRequestMetadata.NuoEvaMessage = _
  var nuoUserMessage: NuoRequestMetadata.NuoUserMessage = _

  var nuoRelationships: List[NuoRelationship] = _

  var nuoQuestionRecognitionData: List[NuoAnalysisRecognitionData] = _

  var lambdaContextRef: Context = _

  object MetadataTable {
    val nuoCredentials = s"Master_NuoCredentials"
    val nuoCredentialsCrm = s"Master_NuoCredentialsCrm"
    val nuoSession = s"Master_NuoSession"
    val nuoTenantDetails = s"Master_NuoTenantDetails"
    val nuoAddress = s"Master_NuoAddress"
    val nuoCompanyDetails = s"Master_NuoCompanyDetails"

  }

  def initNuoTenantDetails(requestId: String,
                           tenantId: String,
                           username: String,
                           lambdaContext: Context): Unit = {
    if (tenantId != null && tenantId.trim.nonEmpty && username != null && username.trim.nonEmpty)
      NuoEvaEnglishListener.nuoTenantDetails =
        new NuoTenantDetails(
          paramRequestId = requestId,
          paramTenantId = tenantId,
          paramUsername = username
        )
    if (lambdaContext != null)
      NuoEvaEnglishListener.this.lambdaContextRef = lambdaContext
  }

  object AwsDetails {

    val AccessKey = ""
    val SecretKey = ""
  }

  object Client {

    private var amazonS3ClientRef: AmazonS3Client = _

    def amazonS3Client: AmazonS3Client =

      if (amazonS3ClientRef != null)
        amazonS3ClientRef
      else if (lambdaContextRef != null) {
        amazonS3ClientRef = new AmazonS3Client(new DefaultAWSCredentialsProviderChain())
        amazonS3ClientRef
      } else {
        amazonS3ClientRef = NuoS3Client.createCustomAwsS3Client(AwsDetails.AccessKey, AwsDetails.SecretKey)
        amazonS3ClientRef
      }


    private var amazonDynamoDBRef: AmazonDynamoDBClient = _

    def amazonDynamoDBClient: AmazonDynamoDBClient =

      if (amazonDynamoDBRef != null)
        amazonDynamoDBRef
      else if (lambdaContextRef != null) {
        amazonDynamoDBRef = new AmazonDynamoDBClient(new DefaultAWSCredentialsProviderChain())
        amazonDynamoDBRef
      } else {
        amazonDynamoDBRef = NuoDynamoDBClient.createCustomDynamoDBClient(AwsDetails.AccessKey, AwsDetails.SecretKey)
        amazonDynamoDBRef
      }

    private var amazonMachineLearningClientRef: AmazonMachineLearningClient = _

    def amazonMachineLearningClient: AmazonMachineLearningClient =

      if (amazonMachineLearningClientRef != null)
        amazonMachineLearningClientRef
      else if (lambdaContextRef != null) {

        amazonMachineLearningClientRef = new AmazonMachineLearningClient(new DefaultAWSCredentialsProviderChain())
        amazonMachineLearningClientRef
      } else {
        amazonMachineLearningClientRef = new AmazonMachineLearningClient(new BasicAWSCredentials(AwsDetails.AccessKey, AwsDetails.SecretKey))
        amazonMachineLearningClientRef
      }

  }


  object BucketName {
    val IncomingBucketName: String = "incoming-backend-nuocanvas-com"
    val OutgoingBucketName: String = "outgoing-backend-nuocanvas-com"
    val MasterBucketName: String = "master-backend-nuocanvas-com"
    val MlaasDataBucketName: String = "mlaas-data-backend-nuocanvas-com"

  }

  //

  object GcpCredentialsDir {
    val GcpCredentialsUploadDir: String = "Credentials/GcpUpload/"
    val GcpCredentialsPoolDir: String = "Credentials/GcpPool/"
    val GcpCredentialsActiveDir: String = "Credentials/GcpActive/"

  }

  //
  val MaxMessageCountPerHistoryPage = 10

  //2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59,
  // 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127,
  // 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199
  val FIELD_TYPE_DEFAULT = 2
  val FIELD_TYPE_AGG = 3
  val FIELD_TYPE_GROUP = 59
  val FIELD_TYPE_QUOTED = 5
  val FIELD_TYPE_INT = 7
  val FIELD_TYPE_FLOAT = 11
  val FIELD_TYPE_DATE = 13
  val FIELD_TYPE_TIME = 17
  val FIELD_TYPE_TIMESTAMP = 19
  val FIELD_TYPE_DURATION = 23

  val FIELD_TYPE_DURATION_FOR = 31
  val FIELD_TYPE_DURATION_SINCE = 37
  val FIELD_TYPE_DURATION_UNTIL = 41
  val FIELD_TYPE_DURATION_PAST = 43
  val FIELD_TYPE_DURATION_NEXT = 47
  val FIELD_TYPE_DURATION_OUTER = 53

  val FIELD_TYPE_RANK_OVER = 59

  val DATE_PART_YEAR = "YEAR"
  val DATE_PART_MONTH = "MONTH"
  val DATE_PART_DAY = "DAY"
  val DATE_PART_HOUR = "HOUR"
  val DATE_PART_MINUTE = "MINUTE"
  val DATE_PART_SECOND = "SECOND"
  val DATE_PART_MILLISECOND = "MILLISECOND"


  def convertFieldTypeToDataType(fieldType: Int): String = {

    if (fieldType % NuoEvaEnglishListener.FIELD_TYPE_TIMESTAMP == 0) {

      NuoDataType.Timestamp
    } else if (fieldType % NuoEvaEnglishListener.FIELD_TYPE_TIME == 0) {

      NuoDataType.Time
    } else if (fieldType % NuoEvaEnglishListener.FIELD_TYPE_DATE == 0) {

      NuoDataType.Date
    } else if (fieldType % NuoEvaEnglishListener.FIELD_TYPE_FLOAT == 0) {

      NuoDataType.Float64
    } else if (fieldType % NuoEvaEnglishListener.FIELD_TYPE_INT == 0) {

      NuoDataType.Int64
    } else /*if(fieldType% NuoEvaEnglishListener.FIELD_TYPE_DEFAULT ==0
      || fieldType% NuoEvaEnglishListener.FIELD_TYPE_QUOTED ==0)*/ {

      NuoDataType.String
    }
  }


  def convertDataTypeToFieldType(dataType: String): Int = {

    if (dataType.equalsIgnoreCase(NuoDataType.Timestamp)) NuoEvaEnglishListener.FIELD_TYPE_TIMESTAMP
    else if (dataType.equalsIgnoreCase(NuoDataType.Time)) NuoEvaEnglishListener.FIELD_TYPE_TIME
    else if (dataType.equalsIgnoreCase(NuoDataType.Date)) NuoEvaEnglishListener.FIELD_TYPE_DATE
    else if (dataType.equalsIgnoreCase(NuoDataType.Float64)
      || dataType.equalsIgnoreCase(NuoDataType.Float)) NuoEvaEnglishListener.FIELD_TYPE_FLOAT
    else if (dataType.equalsIgnoreCase(NuoDataType.Int64)
      || dataType.equalsIgnoreCase(NuoDataType.Integer)) NuoEvaEnglishListener.FIELD_TYPE_INT
    else /*if( DataType.equalsIgnoreCase (NuoDataTypeHandler.NuoDataType.Boolean)
      || DataType.equalsIgnoreCase (NuoDataTypeHandler.NuoDataType.String))*/ NuoEvaEnglishListener.FIELD_TYPE_DEFAULT
  }

}