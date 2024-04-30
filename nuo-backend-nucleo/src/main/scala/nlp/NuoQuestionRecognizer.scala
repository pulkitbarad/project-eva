package nlp

import canvas.NuoDataTypeHandler
import client.NuoJdbcClient
import execution.NuoRequestHandler.sendNuoEvaMessage
import logging.NuoLogger
import metadata.NuoRecognitionMetadata
import metadata.NuoRecognitionMetadata._
import metadata.StorageMetadata.NuoField
import nlp.grammar.NuoEvaEnglishListener
import org.antlr.v4.runtime.{RecognitionException, _}
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.similarity.{JaccardSimilarity, JaroWinklerDistance, LevenshteinDistance}

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 22Dec2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoQuestionRecognizer {

  val jaroWinklerDistance = new JaroWinklerDistance()
  val jaccardSimilarity = new JaccardSimilarity()
  val similarityEligibility = 0.70D
  val similaritySuccess = 0.90D
  val similarityExactMatch = 0.98D
  val similarityTolerance = 0.10D

  val SynonymsOfName = List("Name",
    "Id",
    "Code" /*,
    "Number",
    "Key",
    "Title",
    "Header",
    "Identifier",
    "Index",
    "Sequence",
    "Serial",
    "Series"*/)

  /*
  * Improve with https://en.wikipedia.org/wiki/Lists_of_cities_by_country
  * and https://blueprints.launchpad.net/openobject-server/+spec/countries-states-and-provinces
  * */
  val SynonymsOfLocation = List("Address",
    "Street",
    "State",
    "Country",
    "City",
    "Town",
    "District",
    "County",
    "Village",
    //    "Metro",
    //    "Metropolitan",
    //    "Continent",
    "Capital",
    "Province",
    //    "Administrative Division",
    "Territory" /*,
    "Union Territory"*/)
  val SynonymDictionary = List(SynonymsOfName) ++ List(SynonymsOfLocation) ++ List(
    List("Contact",
      "Telephone",
      "Phone",
      "Cell Phone",
      "Phone number"),
    List("Start",
      "Begin",
      "Open",
      "Initial",
      "Departure"),
    List("End",
      "Close",
      "Final",
      "Arrival")
  )

  val WordsToBeIgnored = List(
    //Articles
    "A",
    "AN",
    "THE",
    //Adp
    "ALL",
    "SOME",
    "FEW",
    "OF",
    "FROM",
    "IN",
    "FOR",
    //Verbs
    "DO",
    "DOES",
    "DID",
    "HAS",
    "HAVE",
    "HAD",
    "CAN",
    "COULD",
    "SHALL",
    "SHOULD",
    "WILL",
    "WOULD",
    "IS",
    "ARE"
  )

  val VerbsToBeIgnored = List(
  )

  val LocationIndicator = List(
    "Of",
    "From",
    "In",
    "at",
    "Staying",
    "Stayed",
    "Stays",
    "Lives",
    "Living",
    "Headquartered in",
    "Located in",
    "Based Out of",
    "Based In"
  )

  val AbbreviationDictionary = List(
    List("Address", "Add", "Addr"),
    List("College", "Clg"),
    List("Date", "Dt"),
    List("Date Of Birth", "DOB"),
    List("Department", "Dept"),
    List("District", "Dist"),
    List("Employee", "Emp"),
    List("Identifier", "Id"),
    List("Index", "Idx", "Indx"),
    List("Junior", "Jr"),
    List("Name", "Nm"),
    List("Number", "Num", "No"),
    List("Phone", "Ph"),
    List("Senior", "Sr"),
    List("Serial", "Sr No"),
    List("Sequence", "Seq", "Sq")
  )

  def getSynonymOf(word: String): List[String] = {
    SynonymDictionary.find(words =>
      words.exists(calcSimilarityScore(_, word, useStrictComparision = true, useLevenshteinDistance = false) >= similaritySuccess))
      .getOrElse(List[String]())
  }

  //
  //  case class PossibleEntity(var DatasetName: String,
  //                            var EntityName: String,
  //                            var SimilarityScore: Double)
  //
  //  case class PossibleField(var DatasetName: String,
  //                           var EntityName: String,
  //                           var FieldName: String,
  //                           var DataType: String,
  //                           var SimilarityScore: Double)
  //
  //  def getFieldPossibilities(fieldText: String,
  //                            expectedEntity: String,
  //                            useLevenshteinDistance: Boolean,
  //                            nuoGrammarListener: NuoEvaEnglishListener): List[PossibleField] = {
  //    var knownPossibleFields = getPossibleFieldsForAlias(fieldText, nuoGrammarListener)
  //    if (expectedEntity != null && expectedEntity.trim.nonEmpty)
  //      knownPossibleFields = knownPossibleFields.filter(field => field.EntityName.equalsIgnoreCase(expectedEntity))
  //    if (knownPossibleFields.nonEmpty) knownPossibleFields
  //    else {
  //
  //      var nuoFields =
  //        NuoEvaEnglishListener
  //          .nuoStorageFields
  //      if (expectedEntity != null && expectedEntity.trim.nonEmpty)
  //        nuoFields = nuoFields.filter(field => field.EntityName.equalsIgnoreCase(expectedEntity))
  //
  //      var fieldPossibilitiesBuffer = mutable.ArrayBuffer[PossibleField]()
  //      (2 until FieldMatchingStage.SortedComparision).flatMap { stage =>
  //        fieldPossibilitiesBuffer ++= getNextFieldPossibilities(fieldText = fieldText,
  //          stage = stage,
  //          useStrictComparision = true,
  //          useLevenshteinDistance = false,
  //          nuoFields = nuoFields,
  //          prevFieldPossibilities = fieldPossibilitiesBuffer,
  //          nuoGrammarListener)
  //        fieldPossibilitiesBuffer
  //      }.distinct.toList
  //        .sortBy(-_.SimilarityScore)
  //    }
  //  }
  //
  //  def getEntityPossibilities(entityText: String,
  //                             nuoGrammarListener: NuoEvaEnglishListener): List[PossibleEntity] = {
  //
  //    val knownPossibleFields = getPossibleFieldsForAlias(entityText, nuoGrammarListener)
  //
  //    val result =
  //      if (knownPossibleFields.nonEmpty)
  //        knownPossibleFields.map(ele => PossibleEntity(ele.DatasetName, ele.EntityName, ele.SimilarityScore))
  //      else {
  //
  //        NuoEvaEnglishListener
  //          .nuoStorageEntities
  //          .map { nuoEntity =>
  //            PossibleEntity(
  //              nuoEntity.DatasetName,
  //              nuoEntity.EntityName,
  //              calcSimilarityScore(
  //                nuoEntity.EntityName,
  //                entityText,
  //                useStrictComparision = true,
  //                useLevenshteinDistance = false
  //              ))
  //          }
  //          .distinct
  //          .sortBy(-_.SimilarityScore)
  //      }
  //    result
  //  }
  //
  //  def getFieldPossibilities(fieldText: String,
  //                            stageList: List[Int],
  //                            //                            useLevenshteinDistance: Boolean,
  //                            nuoFields: List[NuoField],
  //                            nuoGrammarListener: NuoEvaEnglishListener): List[PossibleField] = {
  //
  //    val knownPossibleFields = getPossibleFieldsForAlias(fieldText, nuoGrammarListener)
  //    if (knownPossibleFields.nonEmpty) knownPossibleFields
  //    else {
  //      val updNuoFields = if (nuoFields == null || nuoFields.isEmpty) {
  //        NuoEvaEnglishListener.nuoStorageFields
  //      } else nuoFields
  //
  //      var fieldPossibilitiesBuffer = mutable.ArrayBuffer[PossibleField]()
  //      stageList
  //        .sorted
  //        .flatMap { stage =>
  //          fieldPossibilitiesBuffer ++= getNextFieldPossibilities(fieldText = fieldText,
  //            stage = stage,
  //            useStrictComparision = true,
  //            useLevenshteinDistance = false,
  //            nuoFields = updNuoFields,
  //            prevFieldPossibilities = fieldPossibilitiesBuffer, nuoGrammarListener)
  //          fieldPossibilitiesBuffer
  //        }
  //        .distinct
  //        .sortBy(-_.SimilarityScore)
  //    }
  //  }
  //
  //  object FieldMatchingStage {
  //
  //    val AgainstColNameStrict = 2
  //    val TableNameAsColName = 3
  //    val TableNameAsPrefix = 5
  //    val AgainstColName = 7
  //    val AbbreviationMatches = 11
  //    val SynonymsMatches = 13
  //    val AbbrSynonymsPossibilities = 17
  //    val UsingLevenshteinDistance = 19
  //    val SortedComparision = 23
  //  }
  //
  //  def getMd5Hash(input: String): String = DigestUtils.md5Hex(NuoRequestHandler.removeConsecutiveWhiteSpace(input.trim).toLowerCase)
  //
  //  def isPrimeNumber(input: Int): Boolean = {
  //    if (input <= 1)
  //      false
  //    else if (input == 2)
  //      true
  //    else
  //      !(2 until input - 1).exists(x => input % x == 0)
  //  }
  //
  //  def getNextFieldPossibilities(fieldText: String,
  //                                stage: Int,
  //                                useStrictComparision: Boolean,
  //                                useLevenshteinDistance: Boolean,
  //                                nuoFields: List[NuoField],
  //                                prevFieldPossibilities: mutable.ArrayBuffer[PossibleField],
  //                                nuoGrammarListener: NuoEvaEnglishListener): List[PossibleField] = {
  //
  //    if (prevFieldPossibilities.exists(_.SimilarityScore >= similaritySuccess) || !isPrimeNumber(stage)) {
  //      List[PossibleField]()
  //    } else if (stage == FieldMatchingStage.AgainstColNameStrict) {
  //
  //      /*
  //      * Match input text against column name
  //      * */
  //      getPossibleFields(fieldText,
  //        nuoFields.map(nuoField => (nuoField, nuoField.FieldName)),
  //        useStrictComparision = true,
  //        useLevenshteinDistance = useLevenshteinDistance)
  //        .filter(_.SimilarityScore >= similarityExactMatch)
  //
  //    } else if (stage == FieldMatchingStage.TableNameAsColName) {
  //
  //      /*
  //      * Match input text against Table name as column name
  //      * */
  //      getPossibleFields(fieldText, nuoFields
  //        .filter(nuoField => calcSimilarityScore(nuoField.EntityName,
  //          fieldText,
  //          useStrictComparision = false,
  //          useLevenshteinDistance = false) >= similaritySuccess)
  //        .filter(nuoField => standardizeName(nuoField.FieldName).split(" ").diff(SynonymsOfName.map(_.toUpperCase)).nonEmpty)
  //        .map { nuoField =>
  //          (NuoField(DatasetName = nuoField.DatasetName,
  //            EntityName = nuoField.EntityName,
  //            FieldName = nuoField.EntityName,
  //            DataType = nuoField.DataType), nuoField.FieldName)
  //        }, useStrictComparision = false,
  //        useLevenshteinDistance = useLevenshteinDistance)
  //
  //    } else if (stage == FieldMatchingStage.TableNameAsPrefix) {
  //
  //      /*
  //      * Match input text against Table name as prefix to column name
  //      * */
  //      getPossibleFields(fieldText, nuoFields.map { nuoField =>
  //        (NuoField(DatasetName = nuoField.DatasetName,
  //          EntityName = nuoField.EntityName,
  //          FieldName = s"${nuoField.EntityName} ${nuoField.FieldName}",
  //          DataType = nuoField.DataType), nuoField.FieldName)
  //      }, useStrictComparision = false,
  //        useLevenshteinDistance = useLevenshteinDistance)
  //
  //    } else if (stage == FieldMatchingStage.AgainstColName) {
  //
  //      /*
  //      * Match input text against column name
  //      * */
  //      getPossibleFields(fieldText,
  //        nuoFields.map(nuoField => (nuoField, nuoField.FieldName)),
  //        useStrictComparision = false,
  //        useLevenshteinDistance = useLevenshteinDistance)
  //
  //    } else if (stage == FieldMatchingStage.AbbreviationMatches) {
  //
  //      /*
  //      * Match input text against column name with possibility of abbreviation
  //      * */
  //      getAbbrMatches(fieldText,
  //        nuoFields,
  //        useStrictComparision = false,
  //        useLevenshteinDistance = useLevenshteinDistance)
  //
  //    } else if (stage == FieldMatchingStage.SynonymsMatches) {
  //
  //      /*
  //      * Match input text against column name with possibility of synonyms
  //      * */
  //      getSynonymMatches(fieldText,
  //        nuoFields,
  //        useStrictComparision = false,
  //        useLevenshteinDistance = useLevenshteinDistance)
  //
  //    } else if (stage == FieldMatchingStage.AbbrSynonymsPossibilities) {
  //
  //      /*
  //      * Match input text against column name with possibility of abbreviation within synonyms
  //      * */
  //      getAbbrSynonymMatches(fieldText,
  //        nuoFields,
  //        useStrictComparision = false,
  //        useLevenshteinDistance = useLevenshteinDistance)
  //
  //    } else if (stage == FieldMatchingStage.UsingLevenshteinDistance || stage == FieldMatchingStage.SortedComparision) {
  //
  //      /*
  //      * Repeat above rules using levenshtein distance
  //      * */
  //      (0 until FieldMatchingStage.UsingLevenshteinDistance - 1)
  //        .flatMap { ele =>
  //
  //          getNextFieldPossibilities(fieldText,
  //            stage = ele,
  //            useStrictComparision = false,
  //            useLevenshteinDistance = true,
  //            nuoFields = nuoFields,
  //            prevFieldPossibilities = prevFieldPossibilities,
  //            nuoGrammarListener)
  //        }.toList
  //    }
  //    /* else if (stage == FieldMatchingStage.SortedComparision) {
  //
  //         /*
  //         * Repeat above rules using by turning off the strict comparision
  //         * */
  //         (0 until FieldMatchingStage.UsingLevenshteinDistance - 1)
  //           .flatMap { ele =>
  //
  //             getNextFieldPossibilities(RuleText,
  //               stage = ele,
  //               useStrictComparision = false,
  //               useLevenshteinDistance = false,
  //               nuoFields = nuoFields,
  //               prevFieldPossibilities = prevFieldPossibilities,
  //               nuoStorageDetails = paramNuoGrammarListener.nuoStorageDetails)
  //           }.toList
  //       }*/ else {
  //      List[PossibleField]()
  //    }
  //    //      getPossibleFields(convertAdpOF(RuleText), nuoFieldAndOriginalNames.map { ele =>
  //    //        (NuoField(ele.EntityName, convertAdpOF(ele.Name), ele.Type), ele.Name)
  //    //      }, useStrictComparision = false,
  //    //        useLevenshteinDistance)
  //  }
  //
  //  def getAbbrMatches(fieldText: String,
  //                     nuoFields: List[NuoField],
  //                     useStrictComparision: Boolean,
  //                     useLevenshteinDistance: Boolean): List[PossibleField] = {
  //
  //    val encounteredAbbrs = mutable.ArrayBuffer[List[String]]()
  //    //    AbbreviationDictionary.filter(abbr =>
  //    //      abbr.exists(abbrWord =>
  //    //        getStandardizedWords(RuleText).exists(fieldTextWord =>
  //    //          calcSimilarityScore(abbrWord, fieldTextWord, useStrictComparision, useLevenshteinDistance) >= similaritySuccess)))
  //
  //    nuoFields.map { nuoField =>
  //
  //      val columnName = nuoField.FieldName
  //
  //      val fieldTextArray = getStandardizedWords(fieldText).map { fieldTextWord =>
  //        val matchingFieldTextAbbr = AbbreviationDictionary.find(abbr => abbr.exists(calcSimilarityScore(_, fieldTextWord, useStrictComparision, useLevenshteinDistance) >= similaritySuccess))
  //        if (matchingFieldTextAbbr.isDefined) {
  //          encounteredAbbrs += matchingFieldTextAbbr.get
  //
  //          matchingFieldTextAbbr.get.head
  //        }
  //        else fieldTextWord
  //
  //      }
  //      val columnNameArray = getStandardizedWords(columnName).map { columnNameWord =>
  //        val matchingColumnAbbr = encounteredAbbrs.find(abbr => abbr.exists(calcSimilarityScore(_, columnNameWord, useStrictComparision, useLevenshteinDistance) >= similaritySuccess))
  //        if (matchingColumnAbbr.isDefined) matchingColumnAbbr.get.head
  //        else columnNameWord
  //
  //      }
  //      PossibleField(nuoField.DatasetName, nuoField.EntityName, columnName, nuoField.DataType, calcSimilarityScore(fieldTextArray.toList, columnNameArray.toList, useStrictComparision, useLevenshteinDistance))
  //    }.filter(_.SimilarityScore >= similarityEligibility)
  //  }
  //
  //  def getAbbrSynonymMatches(fieldText: String,
  //                            nuoFields: List[NuoField],
  //                            useStrictComparision: Boolean,
  //                            useLevenshteinDistance: Boolean): List[PossibleField] = {
  //
  //    nuoFields.map { nuoField =>
  //
  //      val columnName = nuoField.FieldName
  //      val encounteredSynonyms = mutable.ArrayBuffer[List[String]]()
  //
  //      val fieldTextArray = getStandardizedWords(fieldText).map { fieldTextWord =>
  //        val matchingSynonym = SynonymDictionary.find(synonym => synonym.exists(calcSimilarityScore(_, fieldTextWord, useStrictComparision, useLevenshteinDistance) >= similaritySuccess))
  //        if (matchingSynonym.isDefined) {
  //          encounteredSynonyms += matchingSynonym.get
  //          matchingSynonym.get.head
  //        } else fieldTextWord
  //      }
  //      val columnNameArray = getStandardizedWords(columnName).map { columnNameWord =>
  //        val matchingSynonym = encounteredSynonyms.find(synonym => synonym.exists(calcSimilarityScore(_, columnNameWord, useStrictComparision, useLevenshteinDistance) >= similaritySuccess))
  //        if (matchingSynonym.isDefined) {
  //          matchingSynonym.get.head
  //        } else columnNameWord
  //
  //      }
  //      PossibleField(nuoField.DatasetName, nuoField.EntityName, columnName, nuoField.DataType, calcSimilarityScore(fieldTextArray.toList, columnNameArray.toList, useStrictComparision, useLevenshteinDistance))
  //    }.filter(_.SimilarityScore >= similarityEligibility)
  //  }
  //
  //  def getPossibleFields(fieldText: String,
  //                        nuoFieldAndOriginalNames: List[(NuoField, String)],
  //                        useStrictComparision: Boolean,
  //                        useLevenshteinDistance: Boolean): List[PossibleField] = {
  //
  //    nuoFieldAndOriginalNames
  //      //      .filter(ele => standardizeName(ele._1.Name).equalsIgnoreCase(standardizeName(RuleText)))
  //      .map(ele => (getPossibleField(fieldText, ele._1, useStrictComparision, useLevenshteinDistance), ele._2))
  //      .filter(_._1.SimilarityScore >= similarityEligibility)
  //      .map { ele =>
  //        val possibleField = ele._1
  //        possibleField.FieldName = ele._2
  //        possibleField
  //      }
  //  }
  //
  //  def getSynonymMatches(fieldText: String,
  //                        nuoFields: List[NuoField],
  //                        useStrictComparision: Boolean,
  //                        useLevenshteinDistance: Boolean): List[PossibleField] = {
  //
  //    nuoFields.map { nuoField =>
  //
  //      val columnName = nuoField.FieldName
  //      val encounteredSynonyms = mutable.ArrayBuffer[List[String]]()
  //
  //      val fieldTextArray = getStandardizedWords(fieldText).map { fieldTextWord =>
  //        val matchingFieldTextAbbr = AbbreviationDictionary.find(abbr => abbr.exists(calcSimilarityScore(_, fieldTextWord, useStrictComparision, useLevenshteinDistance) >= similaritySuccess))
  //        if (matchingFieldTextAbbr.isDefined) matchingFieldTextAbbr.get.head
  //        else fieldTextWord
  //
  //      }
  //        .map { fieldTextWord =>
  //          val matchingSynonym = SynonymDictionary.find(synonym => synonym.exists(calcSimilarityScore(_, fieldTextWord, useStrictComparision, useLevenshteinDistance) >= similaritySuccess))
  //          if (matchingSynonym.isDefined) {
  //            encounteredSynonyms += matchingSynonym.get
  //            matchingSynonym.get.head
  //          }
  //          else fieldTextWord
  //        }
  //      val columnNameArray = getStandardizedWords(columnName).map { columnNameWord =>
  //        val matchingColumnAbbr = AbbreviationDictionary.find(abbr => abbr.exists(calcSimilarityScore(_, columnNameWord, useStrictComparision, useLevenshteinDistance) >= similaritySuccess))
  //        if (matchingColumnAbbr.isDefined) matchingColumnAbbr.get.head
  //        else columnNameWord
  //
  //      }.map { columnNameWord =>
  //        val matchingSynonym = encounteredSynonyms.find(synonym => synonym.exists(calcSimilarityScore(_, columnNameWord, useStrictComparision, useLevenshteinDistance) >= similaritySuccess))
  //        if (matchingSynonym.isDefined) {
  //          matchingSynonym.get.head
  //        }
  //        else columnNameWord
  //
  //      }
  //      PossibleField(nuoField.DatasetName, nuoField.EntityName, columnName, nuoField.DataType, calcSimilarityScore(fieldTextArray.toList, columnNameArray.toList, useStrictComparision, useLevenshteinDistance))
  //    }.filter(_.SimilarityScore >= similarityEligibility)
  //  }
  //
  //  def getPossibleField(fieldText: String,
  //                       nuoField: NuoField,
  //                       useStrictComparision: Boolean,
  //                       useLevenshteinDistance: Boolean): PossibleField = {
  //
  //    val columnName = nuoField.FieldName
  //    PossibleField(DatasetName = nuoField.DatasetName, EntityName = nuoField.EntityName,
  //      FieldName = columnName,
  //      DataType = nuoField.DataType,
  //      SimilarityScore = calcSimilarityScore(fieldText, columnName, useStrictComparision, useLevenshteinDistance))
  //  }
  //
  //  case class RecognizedField(var fieldText: String,
  //                             var possibleFields: List[PossibleField])
  //
  //  def recognizeFieldList(fieldTextList: List[String],
  //                         nuoGrammarListener: NuoEvaEnglishListener): List[Option[NuoField]] = {
  //
  //    var prelimList = reviseRecognizedFields(fieldTextList.map { fieldText =>
  //      RecognizedField(fieldText, getFieldPossibilities(fieldText, "", useLevenshteinDistance = false, nuoGrammarListener))
  //    })
  //
  //    val reversedPrelimList = prelimList.reverse
  //    prelimList = if (reversedPrelimList.head.possibleFields.size == 1 && reversedPrelimList.tail.forall(_.possibleFields.size != 1))
  //      reviseRecognizedFields(reversedPrelimList).reverse
  //    else prelimList
  //    prelimList.map { recognizedField =>
  //      val recognizedNuoField = recordRecognizedField(recognizedField.fieldText,
  //        confirmPossibleField(recognizedField.fieldText,
  //          recognizedField.possibleFields,
  //          expectedDataTypes = null,
  //          allowLiteralChoice = true,
  //          allowQuestionChoice = true,
  //          nuoGrammarListener),
  //        nuoGrammarListener)
  //      if (!nuoGrammarListener.currAnalysisRecognitionData.SelectFields.exists(_.FieldText.equalsIgnoreCase(recognizedField.fieldText)))
  //        nuoGrammarListener.currAnalysisRecognitionData.SelectFields :+= NuoQuestionField(recognizedField.fieldText, recognizedNuoField.get)
  //      recognizedNuoField
  //    }
  //  }
  //
  //  def recognizeField(fieldText: String,
  //                     expectedDataTypes: List[String],
  //                     expectedEntity: String,
  //                     expectedFields: List[NuoField],
  //                     allowLiteralChoice: Boolean,
  //                     allowQuestionChoice: Boolean,
  //                     nuoGrammarListener: NuoEvaEnglishListener): Option[NuoField] = {
  //
  //    /*
  //    *
  //    * 14FEB2018:Pulkit: There is one more  method that recognizes fields, "recognizeFieldList"
  //    * */
  //    var fieldPossibilities =
  //      if (expectedFields != null && expectedFields.nonEmpty) {
  //        expectedFields
  //          .map { nuoField =>
  //            PossibleField(
  //              DatasetName = nuoField.DatasetName,
  //              EntityName = nuoField.EntityName,
  //              FieldName = nuoField.FieldName,
  //              DataType = nuoField.DataType,
  //              SimilarityScore = similaritySuccess
  //            )
  //          }
  //      } else {
  //        getFieldPossibilities(fieldText = fieldText,
  //          expectedEntity,
  //          useLevenshteinDistance = false,
  //          nuoGrammarListener)
  //      }
  //
  //    val confirmedField = confirmPossibleField(fieldText = fieldText,
  //      fieldsToBeConfirmed = fieldPossibilities,
  //      expectedDataTypes = expectedDataTypes,
  //      allowLiteralChoice,
  //      allowQuestionChoice,
  //      nuoGrammarListener)
  //
  //    recordRecognizedField(fieldText,
  //      if (confirmedField.isDefined) confirmedField
  //      else requestField(fieldText, allowLiteralChoice, expectedEntity, nuoGrammarListener),
  //      nuoGrammarListener)
  //  }
  //
  //  def recognizeEntity(entityText: String,
  //                      allowQuestionChoice: Boolean,
  //                      nuoGrammarListener: NuoEvaEnglishListener): Option[NuoField] = {
  //
  //    /*
  //    *
  //    * 14FEB2018:Pulkit: There is one more  method that recognizes fields, "recognizeFieldList"
  //    * */
  //    val confirmedField =
  //      confirmPossibleEntity(
  //        entityText,
  //        getEntityPossibilities(
  //          entityText,
  //          nuoGrammarListener
  //        ),
  //        allowQuestionChoice,
  //        nuoGrammarListener
  //      )
  //    recordRecognizedField(
  //      entityText,
  //      if (confirmedField.isDefined) confirmedField
  //      else requestField(
  //        entityText,
  //        allowLiteralChoice = false,
  //        "",
  //        nuoGrammarListener
  //      ),
  //      nuoGrammarListener
  //    )
  //
  //  }
  //
  //  def getPossibleFieldsForAlias(name: String,
  //                                nuoGrammarListener: NuoEvaEnglishListener): List[PossibleField] = {
  //
  //    if (nuoGrammarListener.currAnalysisRecognitionData != null) {
  //
  //      val nuoUserMessage = getCurrNuoUserMessage(name, nuoGrammarListener)
  //      val mayBeChildField = /*if (
  //        nuoGrammarListener.currAnalysisRecognitionData.ChildQuestionAliasNames.contains(name)
  //          ||
  //          (nuoUserMessage != null
  //            && nuoUserMessage.RuleText.isDefined
  //            && nuoUserMessage.RuleText.get.equalsIgnoreCase(name)
  //            &&
  //            nuoUserMessage
  //              .CommunicationType.get %
  //              NuoRecognitionMetadata
  //                .RecognitionCommunicationType
  //                .COMMUNICATION_TYPE_QUESTION_SUGG == 0)) {
  //
  //        val childQuestionData =
  //          NuoEvaEnglishListener
  //            .nuoQuestionRecognitionData
  //            .find(_.QuestionAlias.equalsIgnoreCase(name))
  //
  //        if (childQuestionData.isDefined
  //          && childQuestionData.get.IsRecognitionComplete) {
  //
  //          val headField = childQuestionData.get
  //            .SelectFields.head.Field
  //
  //          updCurrQuestionDataForChild(childQuestionAlias = childQuestionData.get.QuestionAlias,
  //            childEntities = childQuestionData.get.Entities,
  //            childWhereClause = childQuestionData.get.WhereClause,
  //            childHavingClause = childQuestionData.get.HavingClause,
  //            childQuestionData.get.ChildQuestionAliasNames,
  //            nuoGrammarListener = nuoGrammarListener)
  //
  //          Some(PossibleField(DatasetName = headField.DatasetName, EntityName = headField.EntityName,
  //            FieldName = headField.FieldName,
  //            DataType = headField.DataType,
  //            SimilarityScore = 1.0))
  //        } else None
  //      } else */None
  //
  //      if (mayBeChildField.isDefined) List(mayBeChildField.get)
  //      else {
  //
  //        val mayBeAlias = NuoEvaEnglishListener
  //          .nuoFieldRecognitionData
  //          .filter(_.AliasType % NuoRecognitionMetadata.AliasType.ALIAS_TYPE_FIELD == 0)
  //          .find(alias => calcSimilarityScore(alias.AliasName, name, useStrictComparision = true, useLevenshteinDistance = false) >= similarityExactMatch)
  //        if (mayBeAlias.isDefined)
  //          mayBeAlias.get
  //            .Fields
  //            .map(nuoField => PossibleField(nuoField.DatasetName, nuoField.EntityName, nuoField.FieldName, nuoField.DataType, 1.0))
  //        else List()
  //      }
  //    } else List()
  //  }
  //
  //  def getUnaryExpressionsForAlias(name: String,
  //                                  nuoGrammarListener: NuoEvaEnglishListener): List[NuoRecognitionMetadata.NuoUnaryExpression] = {
  //    val mayBeAlias = NuoEvaEnglishListener
  //      .nuoFieldRecognitionData
  //      .filter(alias => alias.AliasType % NuoRecognitionMetadata.AliasType.ALIAS_TYPE_UNARY_EXP == 0
  //        || alias.AliasType % NuoRecognitionMetadata.AliasType.ALIAS_TYPE_FIELD == 0)
  //      .find(alias => calcSimilarityScore(alias.AliasName, name, useStrictComparision = true, useLevenshteinDistance = false) >= similarityExactMatch)
  //    if (mayBeAlias.isDefined) {
  //
  //      val unaryExpressions = mayBeAlias.get.UnaryExpressions
  //      val leftFields = mayBeAlias.get.Fields
  //      if (unaryExpressions != null && unaryExpressions.nonEmpty) unaryExpressions
  //      else if (leftFields != null && leftFields.nonEmpty) leftFields.map(field => NuoUnaryExpression(field, ""))
  //      else List()
  //    } else List()
  //  }
  //
  //  def recordRecognizedField(fieldText: String,
  //                            recognizedNuoField: Option[NuoField],
  //                            nuoGrammarListener: NuoEvaEnglishListener): Option[NuoField] = {
  //
  //    if (recognizedNuoField.isDefined
  //      && !nuoGrammarListener.currAnalysisRecognitionData.ChildQuestionAliasNames.contains(fieldText)) {
  //
  //      val knownAlias = NuoEvaEnglishListener
  //        .nuoFieldRecognitionData
  //        .find(alias => alias.AliasName.equalsIgnoreCase(fieldText) && alias.AliasType % NuoRecognitionMetadata.AliasType.ALIAS_TYPE_FIELD == 0)
  //      if (knownAlias.isDefined) {
  //        if (!knownAlias.get.Fields.contains(recognizedNuoField.get)) {
  //
  //          NuoEvaEnglishListener.nuoFieldRecognitionData =
  //            List(NuoFieldRecognitionData(AliasName = fieldText,
  //              AliasType = NuoRecognitionMetadata.AliasType.ALIAS_TYPE_FIELD,
  //              Fields = List(recognizedNuoField.get) ++ knownAlias.get.Fields,
  //              UnaryExpressions = List())) ++
  //              NuoEvaEnglishListener
  //                .nuoFieldRecognitionData
  //                .filterNot(_.AliasName.equalsIgnoreCase(knownAlias.get.AliasName))
  //
  //        }
  //      } else {
  //        NuoEvaEnglishListener.nuoFieldRecognitionData +:= NuoFieldRecognitionData(AliasName = fieldText,
  //          AliasType = NuoRecognitionMetadata.AliasType.ALIAS_TYPE_FIELD,
  //          Fields = List(recognizedNuoField.get),
  //          UnaryExpressions = List())
  //
  //      }
  //      NuoEvaEnglishListener.hasNuoFieldRecognitionChanged = true
  //      if (recognizedNuoField.get.EntityName.trim.nonEmpty) {
  //
  //        if (!nuoGrammarListener.currAnalysisRecognitionData.Entities.contains(recognizedNuoField.get.EntityName))
  //          nuoGrammarListener.currAnalysisRecognitionData.Entities :+= recognizedNuoField.get.EntityName
  //
  //      }
  //    }
  //    recognizedNuoField
  //  }
  //
  //  def removeRecognizedFieldFromRecord(fieldText: String,
  //                                      recognizedNuoField: Option[NuoField],
  //                                      nuoGrammarListener: NuoEvaEnglishListener): Unit = {
  //
  //    if (recognizedNuoField.isDefined) {
  //
  //      val knownAlias = NuoEvaEnglishListener
  //        .nuoFieldRecognitionData
  //        .find(alias => alias.AliasName.equalsIgnoreCase(fieldText) && alias.AliasType % NuoRecognitionMetadata.AliasType.ALIAS_TYPE_FIELD == 0)
  //      if (knownAlias.isDefined) {
  //        if (knownAlias.get.Fields.contains(recognizedNuoField.get)) {
  //          NuoEvaEnglishListener.nuoFieldRecognitionData =
  //            List(NuoFieldRecognitionData(AliasName = fieldText,
  //              AliasType = NuoRecognitionMetadata.AliasType.ALIAS_TYPE_FIELD,
  //              Fields =
  //                knownAlias.get
  //                  .Fields
  //                  .filterNot(ele =>
  //                    ele.EntityName.equalsIgnoreCase(recognizedNuoField.get.EntityName)
  //                      && ele.EntityName.equalsIgnoreCase(recognizedNuoField.get.EntityName)),
  //              UnaryExpressions = List())) ++
  //              NuoEvaEnglishListener
  //                .nuoFieldRecognitionData
  //                .filterNot(_.AliasName.equalsIgnoreCase(knownAlias.get.AliasName))
  //          NuoEvaEnglishListener.hasNuoFieldRecognitionChanged = true
  //
  //        }
  //      }
  //    }
  //  }
  //
  //  def recordRecognizedUnaryExpression(ruleText: String,
  //                                      nuoUnaryExpression: Option[NuoRecognitionMetadata.NuoUnaryExpression],
  //                                      nuoGrammarListener: NuoEvaEnglishListener): Option[NuoRecognitionMetadata.NuoUnaryExpression] = {
  //
  //    if (nuoUnaryExpression.get.RightExpText != null
  //      && nuoUnaryExpression.get.RightExpText.nonEmpty
  //      && !nuoGrammarListener.currAnalysisRecognitionData.ChildQuestionAliasNames.contains(ruleText)) {
  //
  //      val knownAlias = NuoEvaEnglishListener.nuoFieldRecognitionData
  //        .find(alias => alias.AliasName.equalsIgnoreCase(ruleText)
  //          && alias.AliasType % NuoRecognitionMetadata.AliasType.ALIAS_TYPE_UNARY_EXP == 0)
  //
  //      if (knownAlias.isDefined) {
  //        if (!knownAlias.get.UnaryExpressions.contains(nuoUnaryExpression.get)) {
  //
  //          NuoEvaEnglishListener.nuoFieldRecognitionData =
  //            List(NuoFieldRecognitionData(AliasName = ruleText,
  //              AliasType = NuoRecognitionMetadata.AliasType.ALIAS_TYPE_UNARY_EXP,
  //              Fields = List(),
  //              UnaryExpressions = List(nuoUnaryExpression.get) ++ knownAlias.get.UnaryExpressions)) ++
  //              NuoEvaEnglishListener
  //                .nuoFieldRecognitionData
  //                .filterNot(_.AliasName.equalsIgnoreCase(knownAlias.get.AliasName))
  //          NuoEvaEnglishListener.hasNuoFieldRecognitionChanged = true
  //
  //        }
  //      } else {
  //        NuoEvaEnglishListener.nuoFieldRecognitionData +:= NuoFieldRecognitionData(AliasName = ruleText,
  //          AliasType = NuoRecognitionMetadata.AliasType.ALIAS_TYPE_UNARY_EXP,
  //          Fields = List(),
  //          UnaryExpressions = List(nuoUnaryExpression.get))
  //      }
  //      if (nuoUnaryExpression.get.LeftField.EntityName.trim.nonEmpty
  //        && !nuoGrammarListener.currAnalysisRecognitionData.Entities.contains(nuoUnaryExpression.get.LeftField.EntityName))
  //        nuoGrammarListener.currAnalysisRecognitionData.Entities :+= nuoUnaryExpression.get.LeftField.EntityName
  //
  //      if (!nuoGrammarListener.currAnalysisRecognitionData.UnaryExpressions.contains(nuoUnaryExpression.get))
  //        nuoGrammarListener.currAnalysisRecognitionData.UnaryExpressions :+= nuoUnaryExpression.get
  //
  //    } else recordRecognizedField(ruleText, Some(nuoUnaryExpression.get.LeftField), nuoGrammarListener)
  //    nuoUnaryExpression
  //  }
  //
  //  def confirmPossibleEntity(entityText: String,
  //                            entitiesToBeConfirmed: List[PossibleEntity],
  //                            allowQuestionChoice: Boolean,
  //                            nuoGrammarListener: NuoEvaEnglishListener): Option[NuoField] = {
  //
  //    var index = 0
  //
  //    val possibleEntities = findMaxScorePossibleEntities(entitiesToBeConfirmed, useSimilarityTolerance = true)
  //    if (possibleEntities.size > 1) {
  //
  //
  //      if (possibleEntities.size == 1) {
  //
  //        val entity = possibleEntities.head
  //        Some(NuoField(entity.DatasetName, entity.EntityName, "", ""))
  //      } else {
  //
  //        val userResponse = getCurrNuoUserMessage(entityText, nuoGrammarListener)
  //
  //        if (userResponse != null
  //          && userResponse.AnalysisId.isDefined
  //          && userResponse.RuleText.isDefined
  //          && userResponse.AnalysisId.get.equalsIgnoreCase(nuoGrammarListener.currAnalysisRecognitionData.AnalysisId)
  //          && userResponse.RuleText.get.equalsIgnoreCase(entityText)) {
  //
  //
  //          if (userResponse.CommunicationType.get %
  //            NuoRecognitionMetadata
  //              .RecognitionCommunicationType
  //              .COMMUNICATION_TYPE_CHOICE == 0) {
  //
  //            val field = possibleEntities(userResponse.Responses.get.head.trim.toInt - 1)
  //            Some(NuoField(field.DatasetName, field.EntityName, "", ""))
  //
  //          } else if (userResponse.CommunicationType.get %
  //            NuoRecognitionMetadata
  //              .RecognitionCommunicationType
  //              .COMMUNICATION_TYPE_FIELD_SUGG == 0) {
  //            getFieldSuggestion(
  //              userResponse.Responses.get.head,
  //              allowLiteralChoice = false,
  //              allowQuestionChoice = allowQuestionChoice,
  //              expectedEntity = "",
  //              nuoGrammarListener = nuoGrammarListener)
  //
  //          } else if (userResponse.CommunicationType.get %
  //            NuoRecognitionMetadata
  //              .RecognitionCommunicationType
  //              .COMMUNICATION_TYPE_QUESTION_SUGG == 0) {
  //
  //            getFirstFieldFromChildQuestion(
  //              ruleText = entityText,
  //              childQuestionAlias = entityText,
  //              childQuestionText = userResponse.Responses.get.head,
  //              nuoGrammarListener = nuoGrammarListener
  //            )
  //
  //          } else {
  //            NuoRequestHandler.reportErrorToUser(new Exception(s"I could not understand the user input during $entityText recognition."))
  //            throw new Exception("Unreachable Code statement")
  //          }
  //        } else {
  //
  //          val possibleFieldCoveredSoFar = mutable.ArrayBuffer[String]()
  //          index = 0
  //          var requestChoices = mutable.ArrayBuffer[NuoRequestMetadata.NuoUserChoice]()
  //          requestChoices ++= possibleEntities.sortBy(-_.SimilarityScore).map { possibleField =>
  //            index += 1
  //            val possibleFieldText = s"Table <u>${possibleField.EntityName}</u>"
  //            if (!possibleFieldCoveredSoFar.contains(possibleFieldText)) {
  //
  //              possibleFieldCoveredSoFar += possibleFieldText
  //              NuoUserChoice(possibleFieldText, index.toString)
  //            } else null
  //          }.filter(_ != null)
  //          var userInputType = NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_CHOICE
  //
  //          //          requestChoices += NuoUserChoice("I have a suggestion for you", (index + 2).toString)
  //          userInputType *= NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_FIELD_SUGG
  //
  //          if (allowQuestionChoice) {
  //            //            requestChoices += NuoUserChoice("There is a question behind it", (index + 3).toString)
  //            userInputType *= NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_QUESTION_SUGG
  //          }
  //
  //          sendNuoEvaMessage(
  //            analysisId = nuoGrammarListener.currAnalysisRecognitionData.AnalysisId,
  //            ruleText = entityText,
  //            communicationType = userInputType,
  //            evaResponseMessage = s"Please help me choose the expected table for <mark>$entityText</mark>",
  //            leftEntityName = "",
  //            rightEntityName = "",
  //            nuoEntities = List(),
  //            nuoUserChoices = requestChoices.toList,
  //            nuoMappingInput = None,
  //            nuoFileLoadOptions = None,
  //            nuoAnalyzeImageOptions = None,
  //            nuoPollingDetails = None,
  //            nuoGrammarListener = nuoGrammarListener)
  //          None
  //        }
  //      }
  //    } else if (possibleEntities.nonEmpty) {
  //      val field = possibleEntities.head
  //      Some(NuoField(field.DatasetName, field.EntityName, "", ""))
  //    } else {
  //      None
  //    }
  //  }
  //
  //  def confirmPossibleField(fieldText: String,
  //                           fieldsToBeConfirmed: List[PossibleField],
  //                           expectedDataTypes: List[String],
  //                           allowLiteralChoice: Boolean,
  //                           allowQuestionChoice: Boolean,
  //                           nuoGrammarListener: NuoEvaEnglishListener): Option[NuoField] = {
  //
  //    var index = 0
  //
  //    val possibleFields = findMaxScorePossibleFields(fieldsToBeConfirmed, useSimilarityTolerance = true)
  //    if (possibleFields != null && possibleFields.nonEmpty && possibleFields.size == 1) {
  //      val field = possibleFields.head
  //      Some(NuoField(field.DatasetName, field.EntityName, field.FieldName, field.DataType))
  //    } else {
  //
  //      var fieldsMatchingDataType = if (expectedDataTypes != null && expectedDataTypes.nonEmpty)
  //        possibleFields.filter(e => expectedDataTypes.map(_.toUpperCase).contains(e.DataType.toUpperCase))
  //      else possibleFields
  //
  //      if (fieldsMatchingDataType.size == 1) {
  //
  //        val field = fieldsMatchingDataType.head
  //        Some(NuoField(field.DatasetName, field.EntityName, field.FieldName, field.DataType))
  //      } else {
  //
  //        val userResponse = getCurrNuoUserMessage(fieldText, nuoGrammarListener)
  //
  //        if (userResponse != null
  //          && userResponse.AnalysisId.isDefined
  //          && userResponse.RuleText.isDefined
  //          && userResponse.AnalysisId.get.equalsIgnoreCase(nuoGrammarListener.currAnalysisRecognitionData.AnalysisId)
  //          && userResponse.RuleText.get.equalsIgnoreCase(fieldText)) {
  //
  //
  //          if (userResponse.CommunicationType.get %
  //            NuoRecognitionMetadata
  //              .RecognitionCommunicationType
  //              .COMMUNICATION_TYPE_CHOICE == 0) {
  //
  //            val field = possibleFields(userResponse.Responses.get.head.trim.toInt - 1)
  //            Some(NuoField(field.DatasetName, field.EntityName, field.FieldName, field.DataType))
  //
  //          } else if (userResponse.CommunicationType.get %
  //            NuoRecognitionMetadata
  //              .RecognitionCommunicationType
  //              .COMMUNICATION_TYPE_LITERAL == 0) {
  //            Some(NuoField("", "", s"'$fieldText'", NuoDataTypeHandler.NuoDataType.String))
  //
  //          } else if (userResponse.CommunicationType.get %
  //            NuoRecognitionMetadata
  //              .RecognitionCommunicationType
  //              .COMMUNICATION_TYPE_FIELD_SUGG == 0) {
  //            getFieldSuggestion(userResponse.Responses.get.head, allowLiteralChoice, allowQuestionChoice, "", nuoGrammarListener)
  //
  //          } else if (userResponse.CommunicationType.get %
  //            NuoRecognitionMetadata
  //              .RecognitionCommunicationType
  //              .COMMUNICATION_TYPE_QUESTION_SUGG == 0) {
  //
  //            getFirstFieldFromChildQuestion(ruleText = fieldText,
  //              childQuestionAlias = fieldText,
  //              childQuestionText = userResponse.Responses.get.head,
  //              nuoGrammarListener = nuoGrammarListener)
  //
  //          } else {
  //            NuoRequestHandler.reportErrorToUser(new Exception(s"I could not understand the user input during $fieldText recognition."))
  //            throw new Exception("Unreachable Code Statement")
  //          }
  //        } else {
  //
  //          val possibleFieldCoveredSoFar = mutable.ArrayBuffer[String]()
  //          index = 0
  //          var requestChoices = mutable.ArrayBuffer[NuoRequestMetadata.NuoUserChoice]()
  //          requestChoices ++= possibleFields.sortBy(-_.SimilarityScore).map { possibleField =>
  //            index += 1
  //            val possibleFieldText = s"Column <b>${possibleField.FieldName}</b> of table <u>${possibleField.EntityName}</u>"
  //            if (!possibleFieldCoveredSoFar.contains(possibleFieldText)) {
  //
  //              possibleFieldCoveredSoFar += possibleFieldText
  //              NuoUserChoice(possibleFieldText, index.toString)
  //            } else null
  //          }.filter(_ != null)
  //          var userInputType = NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_CHOICE
  //
  //          if (allowLiteralChoice) {
  //            //            requestChoices += NuoUserChoice("Take it literally", (index + 1).toString)
  //            userInputType *= NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_LITERAL
  //          }
  //
  //          //          requestChoices += NuoUserChoice("I have a suggestion for you", (index + 2).toString)
  //          userInputType *= NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_FIELD_SUGG
  //
  //          if (allowQuestionChoice) {
  //            //            requestChoices += NuoUserChoice("There is a question behind it", (index + 3).toString)
  //            userInputType *= NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_QUESTION_SUGG
  //          }
  //
  //          sendNuoEvaMessage(
  //            analysisId = nuoGrammarListener.currAnalysisRecognitionData.AnalysisId,
  //            ruleText = fieldText,
  //            communicationType = userInputType,
  //            evaResponseMessage = s"Please help me choose the expected column for <mark>$fieldText</mark>",
  //            leftEntityName = "",
  //            rightEntityName = "",
  //            nuoEntities = List(),
  //            nuoUserChoices = requestChoices.toList,
  //            nuoMappingInput = None,
  //            nuoFileLoadOptions = None,
  //            nuoAnalyzeImageOptions = None,
  //            nuoPollingDetails = None,
  //            nuoGrammarListener = nuoGrammarListener)
  //          None
  //        }
  //      }
  //    }
  //  }
  //

  //
  //
  //  def updateQuestionRecognizedField(suggestedNuoField: Option[NuoField],
  //                                    nuoGrammarListener: NuoEvaEnglishListener): Unit = {
  //
  //    val userResponseRuleText = NuoEvaEnglishListener.nuoUserMessage.RuleText.get
  //    val currQuestionRecogData = nuoGrammarListener.currAnalysisRecognitionData
  //
  //    val existingField = if (NuoEvaEnglishListener.nuoUserMessage.CommunicationType.get
  //      % NuoRecognitionMetadata
  //      .RecognitionCommunicationType
  //      .COMMUNICATION_TYPE_UPD_CRIT_FIELD == 0) {
  //
  //      val mayBeCriteria =
  //        currQuestionRecogData
  //          .Criteria
  //          .find(_.RelatedFields.exists(_.FieldText.equalsIgnoreCase(userResponseRuleText)))
  //
  //      if (mayBeCriteria.isDefined) {
  //
  //        val oldField =
  //          mayBeCriteria.get
  //            .RelatedFields
  //            .find(_.FieldText.equalsIgnoreCase(userResponseRuleText)).get
  //
  //        val oldExpression =
  //          if (oldField.Field.EntityName.nonEmpty)
  //            oldField.Field.EntityName + "." + oldField.Field.FieldName
  //          else
  //            oldField.Field.FieldName
  //
  //        val newExpression =
  //          if (suggestedNuoField.get.EntityName.nonEmpty)
  //            suggestedNuoField.get.EntityName + "." + suggestedNuoField.get.FieldName
  //          else
  //            suggestedNuoField.get.FieldName
  //
  //        nuoGrammarListener
  //          .currAnalysisRecognitionData
  //          .Criteria =
  //          currQuestionRecogData
  //            .Criteria
  //            .filterNot(_.RuleText.equalsIgnoreCase(mayBeCriteria.get.RuleText))
  //
  //        Some(oldField.Field)
  //      } else {
  //        NuoRequestHandler.reportErrorToUser(new Exception("Sorry, I could not understood what you want to update."))
  //        throw new Exception("Unreachable Code Statement")
  //      }
  //
  //    } else if (NuoEvaEnglishListener.nuoUserMessage.CommunicationType.get
  //      % NuoRecognitionMetadata
  //      .RecognitionCommunicationType
  //      .COMMUNICATION_TYPE_UPD_SELECT_FIELD == 0) {
  //
  //      val mayBeSelectField =
  //        currQuestionRecogData
  //          .SelectFields
  //          .find(_.FieldText.equalsIgnoreCase(userResponseRuleText))
  //
  //      if (mayBeSelectField.isDefined) {
  //
  //        nuoGrammarListener
  //          .currAnalysisRecognitionData
  //          .SelectFields =
  //          currQuestionRecogData
  //            .SelectFields
  //            .filterNot(_.FieldText.equalsIgnoreCase(mayBeSelectField.get.FieldText)) :+
  //            NuoQuestionField(
  //              FieldText = userResponseRuleText,
  //              Field = suggestedNuoField.get)
  //        Some(mayBeSelectField.get.Field)
  //      } else {
  //        NuoRequestHandler.reportErrorToUser(new Exception("Sorry, I could not understood what you want to update."))
  //        throw new Exception("Unreachable Code Statement")
  //      }
  //    } else None
  //
  //    if (currQuestionRecogData.ChildQuestionAliasNames.contains(userResponseRuleText))
  //      nuoGrammarListener
  //        .currAnalysisRecognitionData
  //        .ChildQuestionAliasNames =
  //        currQuestionRecogData
  //          .ChildQuestionAliasNames
  //          .filterNot(_.equalsIgnoreCase(userResponseRuleText))
  //    removeRecognizedFieldFromRecord(userResponseRuleText, existingField, nuoGrammarListener)
  //  }
  //
  //  def getFirstFieldFromChildQuestion(ruleText: String,
  //                                     childQuestionAlias: String,
  //                                     childQuestionText: String,
  //                                     nuoGrammarListener: NuoEvaEnglishListener): Option[NuoField] = {
  //
  //    val childGrammarListener = new NuoEvaEnglishListener(
  //      isChildQuestion = true,
  //      initConfigurations = false,
  //      questionAlias = childQuestionAlias,
  //      questionText = childQuestionText,
  //      nuoUserMessage = null,
  //      nuoTenantDetails = NuoEvaEnglishListener.nuoTenantDetails)
  //
  //    updCurrQuestionDataForChild(childQuestionAlias = childQuestionAlias,
  //      childEntities = childGrammarListener.currAnalysisRecognitionData.Entities,
  //      childWhereClause = childGrammarListener.currAnalysisRecognitionData.WhereClause,
  //      childHavingClause = childGrammarListener.currAnalysisRecognitionData.HavingClause,
  //      grandChildrenAliases = childGrammarListener.currAnalysisRecognitionData.ChildQuestionAliasNames,
  //      nuoGrammarListener = nuoGrammarListener)
  //
  //
  //    NuoQuestionParser.parseQuestion(requestTokenAndTree = false, childGrammarListener)
  //
  //    NuoEvaEnglishListener.nuoQuestionRecognitionData +:= childGrammarListener.currAnalysisRecognitionData
  //
  //    updCurrQuestionDataForChild(childQuestionAlias = childQuestionAlias,
  //      childEntities = childGrammarListener.currAnalysisRecognitionData.Entities,
  //      childWhereClause = childGrammarListener.currAnalysisRecognitionData.WhereClause,
  //      childHavingClause = childGrammarListener.currAnalysisRecognitionData.HavingClause,
  //      grandChildrenAliases = childGrammarListener.currAnalysisRecognitionData.ChildQuestionAliasNames,
  //      nuoGrammarListener = nuoGrammarListener)
  //
  //    Some(childGrammarListener.currAnalysisRecognitionData.SelectFields.head.Field)
  //  }
  //
  //
  //  def updCurrQuestionAliases(childQuestionAlias: String,
  //                             grandChildrenAliases: List[String],
  //                             nuoGrammarListener: NuoEvaEnglishListener): Boolean = {
  //
  //    var combinedAliases = List(childQuestionAlias)
  //
  //    if (grandChildrenAliases != null && grandChildrenAliases.nonEmpty)
  //      combinedAliases ++= grandChildrenAliases
  //
  //    if (combinedAliases != null && combinedAliases.nonEmpty
  //      && combinedAliases.exists(ele => !nuoGrammarListener.currAnalysisRecognitionData.ChildQuestionAliasNames.contains(ele))) {
  //
  //      nuoGrammarListener.currAnalysisRecognitionData.ChildQuestionAliasNames ++:=
  //        combinedAliases
  //          .filterNot(nuoGrammarListener.currAnalysisRecognitionData.ChildQuestionAliasNames.contains)
  //      true
  //    } else false
  //  }
  //
  //  def updCurrQuestionDataForChild(childQuestionAlias: String,
  //                                  childEntities: List[String],
  //                                  childWhereClause: String,
  //                                  childHavingClause: String,
  //                                  grandChildrenAliases: List[String],
  //                                  nuoGrammarListener: NuoEvaEnglishListener): Unit = {
  //
  //    var changeDetected = false
  //
  //    changeDetected = updCurrQuestionAliases(
  //      childQuestionAlias,
  //      grandChildrenAliases,
  //      nuoGrammarListener)
  //
  //    val newlyIdentifiedChildEntites =
  //      childEntities
  //        .filterNot(childEntity =>
  //          nuoGrammarListener
  //            .currAnalysisRecognitionData
  //            .Entities.contains(childEntity))
  //
  //    if (newlyIdentifiedChildEntites.nonEmpty) {
  //
  //      nuoGrammarListener
  //        .currAnalysisRecognitionData
  //        .Entities ++:=
  //        newlyIdentifiedChildEntites
  //          .filterNot(nuoGrammarListener
  //            .currAnalysisRecognitionData
  //            .Entities.contains)
  //      changeDetected = true
  //    }
  //
  //
  //    if (childWhereClause != null
  //      && childWhereClause.nonEmpty
  //      && !nuoGrammarListener
  //      .currAnalysisRecognitionData
  //      .ChildWhereClauses.contains(childWhereClause)) {
  //
  //      nuoGrammarListener
  //        .currAnalysisRecognitionData
  //        .ChildWhereClauses +:= childWhereClause
  //      changeDetected = true
  //    }
  //
  //
  //    if (childHavingClause != null
  //      && childHavingClause.nonEmpty
  //      && !nuoGrammarListener
  //      .currAnalysisRecognitionData
  //      .ChildHavingClauses.contains(childHavingClause)) {
  //
  //      nuoGrammarListener
  //        .currAnalysisRecognitionData
  //        .ChildHavingClauses +:= childHavingClause
  //      changeDetected = true
  //    }
  //    if (changeDetected)
  //      NuoRecognitionMetadata.writeCurrQuestionRecognitionData(nuoGrammarListener)
  //
  //
  //  }
  //
  //  def confirmNuoUnaryExpression(fieldText: String,
  //                                expsToBeConfirmed: List[NuoRecognitionMetadata.NuoUnaryExpression],
  //                                nuoGrammarListener: NuoEvaEnglishListener): Option[NuoRecognitionMetadata.NuoUnaryExpression] = {
  //
  //    var index = 0
  //    val confirmedNuoUnaryExpression =
  //      if (expsToBeConfirmed != null && expsToBeConfirmed.size == 1) {
  //        Some(expsToBeConfirmed.head)
  //      } else {
  //
  //        if (expsToBeConfirmed.size == 1) {
  //          Some(expsToBeConfirmed.head)
  //        } else {
  //
  //          val userResponse = getCurrNuoUserMessage(fieldText, nuoGrammarListener)
  //
  //          if (userResponse != null
  //            && userResponse.AnalysisId.isDefined
  //            && userResponse.RuleText.isDefined
  //            && userResponse.AnalysisId.get.equalsIgnoreCase(nuoGrammarListener.currAnalysisRecognitionData.AnalysisId)
  //            && userResponse.RuleText.get.equalsIgnoreCase(fieldText)) {
  //
  //            val choice = userResponse.Responses.get.head.trim.toInt
  //            if (userResponse != null &&
  //              userResponse.CommunicationType.get %
  //                NuoRecognitionMetadata
  //                  .RecognitionCommunicationType
  //                  .COMMUNICATION_TYPE_CHOICE == 0) {
  //
  //              Some(expsToBeConfirmed(choice - 1))
  //            } else {
  //              NuoRequestHandler.reportErrorToUser(new Exception(s"I could not understand the user input during $fieldText recognition."))
  //              throw new Exception("Unreachable Code Statement")
  //            }
  //          } else {
  //            index = 0
  //            val requestChoices = expsToBeConfirmed.map { nuoUnaryExpression =>
  //              index += 1
  //              NuoUserChoice(s"Column <b>${nuoUnaryExpression.LeftField.FieldName}</b> of table <u>${nuoUnaryExpression.LeftField.EntityName}</u>" +
  //                s" = <i>'${nuoUnaryExpression.RightExpText}'</i>", s"$index")
  //            }
  //            sendNuoEvaMessage(
  //              analysisId = nuoGrammarListener.currAnalysisRecognitionData.AnalysisId,
  //              ruleText = fieldText,
  //              communicationType = NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_CHOICE,
  //              evaResponseMessage = s"Please help me choose correct expression for <mark>$fieldText</mark>",
  //              nuoUserChoices = requestChoices,
  //              leftEntityName = "",
  //              rightEntityName = "",
  //              nuoEntities = List(),
  //              nuoMappingInput = None,
  //              nuoFileLoadOptions = None,
  //              nuoAnalyzeImageOptions = None,
  //              nuoPollingDetails = None,
  //              nuoGrammarListener = nuoGrammarListener)
  //            None
  //          }
  //        }
  //      }
  //    recordRecognizedUnaryExpression(ruleText = fieldText,
  //      nuoUnaryExpression = confirmedNuoUnaryExpression,
  //      nuoGrammarListener = nuoGrammarListener)
  //
  //  }
  //
  //  def confirmUnaryField(ruleText: String,
  //                        leftOnlyCriteria: List[UnaryCriteria],
  //                        nuoGrammarListener: NuoEvaEnglishListener): Option[NuoRecognitionMetadata.NuoUnaryExpression] = {
  //
  //
  //    val updInputCriteria = leftOnlyCriteria.filter(_.leftExpression.possibleFields.nonEmpty)
  //
  //    val userChoice =
  //      if (updInputCriteria != null
  //        && updInputCriteria.nonEmpty
  //        && updInputCriteria.size == 1
  //        && updInputCriteria.head.leftExpression.possibleFields.size == 1) {
  //
  //        val confirmedCriteria = updInputCriteria.head
  //        val confirmedPossibleField = updInputCriteria.head.leftExpression.possibleFields.head
  //        val confirmedNuoField =
  //          NuoField(
  //            confirmedPossibleField.DatasetName,
  //            confirmedPossibleField.EntityName,
  //            confirmedPossibleField.FieldName,
  //            confirmedPossibleField.DataType
  //          )
  //        (confirmedNuoField,
  //          confirmedCriteria.rightExpressionText)
  //      } else {
  //
  //        /*
  //        * 21FEb2018:Pulkit: Child question as a part of unary expression indirectly, while getting field suggestion for left side of expression.
  //        * */
  //        //        requestChoices += s"There is a question behind it:${criteriaIndex + 1}.2\n"
  //
  //        val userResponse = getCurrNuoUserMessage(ruleText, nuoGrammarListener)
  //
  //        if (userResponse != null
  //          && userResponse.AnalysisId.isDefined
  //          && userResponse.RuleText.isDefined
  //          && userResponse.AnalysisId.get.equalsIgnoreCase(nuoGrammarListener.currAnalysisRecognitionData.AnalysisId)
  //          && userResponse.RuleText.get.equalsIgnoreCase(ruleText)) {
  //
  //          if (userResponse.CommunicationType.get %
  //            NuoRecognitionMetadata
  //              .RecognitionCommunicationType
  //              .COMMUNICATION_TYPE_CHOICE == 0) {
  //
  //
  //            val choice = userResponse.Responses.get.head.trim
  //            val fieldChoice = choice.substring(choice.indexOf(".") + 1).toInt
  //            val criteriaChoice = choice.substring(0, choice.indexOf(".")).toInt
  //
  //            val confirmedCriteria = updInputCriteria(criteriaChoice - 1)
  //            val confirmedPossibleField = confirmedCriteria.leftExpression.possibleFields(fieldChoice - 1)
  //            val confirmedNuoField = NuoField(confirmedPossibleField.DatasetName, confirmedPossibleField.EntityName, confirmedPossibleField.FieldName, confirmedPossibleField.DataType)
  //
  //            (confirmedNuoField,
  //              confirmedCriteria.rightExpressionText)
  //
  //          } else if (userResponse.CommunicationType.get %
  //            NuoRecognitionMetadata
  //              .RecognitionCommunicationType
  //              .COMMUNICATION_TYPE_UNARY_SUGG == 0) {
  //
  //            val suggestedField = getFieldSuggestion(userResponse.Responses.get.head, allowLiteralChoice = false, allowQuestionChoice = true, "", nuoGrammarListener)
  //            (suggestedField.get, userResponse.Responses.get(1))
  //
  //          } else {
  //            NuoRequestHandler.reportErrorToUser(new Exception(s"I could not understand the user input during $ruleText recognition."))
  //            throw new Exception("Unreachable Code Statement")
  //          }
  //
  //        } else {
  //          var criteriaIndex = 0
  //          val choicesCoveredSoFar = mutable.ArrayBuffer[String]()
  //          var requestChoices = mutable.ArrayBuffer[NuoUserChoice]()
  //
  //          requestChoices ++= updInputCriteria.flatMap { unaryCriteria =>
  //            var fieldIndex = 0
  //            criteriaIndex += 1
  //            if (unaryCriteria.rightExpressionText == null || unaryCriteria.rightExpressionText.trim.isEmpty) {
  //
  //              unaryCriteria.leftExpression.possibleFields.map { possibleField =>
  //                fieldIndex += 1
  //                if (choicesCoveredSoFar.contains(possibleField.EntityName + "." + possibleField.FieldName)) null
  //                else {
  //                  choicesCoveredSoFar += possibleField.EntityName + "." + possibleField.FieldName
  //                  NuoUserChoice(s"Condition check of the column <b>${possibleField.FieldName}</b>" +
  //                    s" of table <u>${possibleField.EntityName}</u>", s"${criteriaIndex + "." + fieldIndex}")
  //                }
  //              }.filterNot(_ == null)
  //            } else {
  //
  //              unaryCriteria.leftExpression.possibleFields.map { possibleField =>
  //                fieldIndex += 1
  //                if (choicesCoveredSoFar.contains(unaryCriteria.rightExpressionText + "." + possibleField.EntityName + "." + possibleField.FieldName)) null
  //                else {
  //                  choicesCoveredSoFar += unaryCriteria.rightExpressionText + "." + possibleField.EntityName + "." + possibleField.FieldName
  //                  NuoUserChoice(s"Comparison between" +
  //                    s" and the column <b>${possibleField.FieldName}</b> of <u>${possibleField.EntityName}</u>" +
  //                    s" value <i>'${unaryCriteria.rightExpressionText}'</i>",
  //                    s"${criteriaIndex + "." + fieldIndex}")
  //                }
  //              }.filterNot(_ == null)
  //            }
  //          }
  //          var userInputType = NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_CHOICE
  //
  //          //          requestChoices += NuoUserChoice(s"I have a suggestion for you", s":${criteriaIndex + 1}.1")
  //          userInputType *= NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_UNARY_SUGG
  //
  //          sendNuoEvaMessage(
  //            analysisId = nuoGrammarListener.currAnalysisRecognitionData.AnalysisId,
  //            ruleText = ruleText,
  //            communicationType = userInputType,
  //            evaResponseMessage = s"I found multiple possible variants of expression <mark>$ruleText</mark>. Please help me choose the expected.",
  //            nuoUserChoices = requestChoices.toList,
  //            leftEntityName = "",
  //            rightEntityName = "",
  //            nuoEntities = List(),
  //            nuoMappingInput = None,
  //            nuoFileLoadOptions = None,
  //            nuoAnalyzeImageOptions = None,
  //            nuoPollingDetails = None,
  //            nuoGrammarListener = nuoGrammarListener)
  //          throw new Exception(s"I was not supposed to do this!")
  //        }
  //      }
  //
  //    recordRecognizedUnaryExpression(ruleText = ruleText,
  //      nuoUnaryExpression = Some(NuoRecognitionMetadata.NuoUnaryExpression(userChoice._1, userChoice._2)),
  //      nuoGrammarListener = nuoGrammarListener)
  //  }
  //
  //  def reviseRecognizedFields(recognizedFields: List[RecognizedField]): List[RecognizedField] = {
  //
  //    var referenceEntity: String = null
  //    recognizedFields
  //      .map {
  //        recognizedField =>
  //
  //          val possibleFields = recognizedField.possibleFields
  //          if (possibleFields.size == 1) {
  //            referenceEntity = possibleFields.head.EntityName
  //            recognizedField
  //          } else if (possibleFields.size > 1) {
  //
  //            val revisedPossibleFields = revisePossibleFields(if (referenceEntity == null) List() else List(referenceEntity), possibleFields)
  //
  //            if (revisedPossibleFields.map(_.EntityName).distinct.size == 1) referenceEntity = revisedPossibleFields.head.EntityName
  //            RecognizedField(recognizedField.fieldText, revisedPossibleFields)
  //          } else recognizedField
  //      }
  //  }
  //
  //  def revisePossibleFields(referenceEntities: List[String],
  //                           possibleFields: List[PossibleField]): List[PossibleField] = {
  //
  //
  //    if (possibleFields.nonEmpty) {
  //
  //      val toleratedScore = possibleFields.maxBy(_.SimilarityScore).SimilarityScore - similarityTolerance
  //
  //      if (referenceEntities != null && referenceEntities.nonEmpty) {
  //
  //        val recognizedEntityFields = possibleFields.filter(possibleField =>
  //          referenceEntities.map(_.toUpperCase).contains(possibleField.EntityName.toUpperCase)
  //            && possibleField.SimilarityScore >= toleratedScore)
  //        if (recognizedEntityFields.nonEmpty) {
  //
  //          findMaxScorePossibleFields(recognizedEntityFields, useSimilarityTolerance = true)
  //        } else findMaxScorePossibleFields(possibleFields, useSimilarityTolerance = true)
  //      } else findMaxScorePossibleFields(possibleFields, useSimilarityTolerance = true)
  //    } else possibleFields
  //  }
  //
  //  def findMaxScorePossibleEntities(possibleEntities: List[PossibleEntity],
  //                                   useSimilarityTolerance: Boolean): List[PossibleEntity] = {
  //
  //    /*
  //    ********************************************************************************************************************
  //    ********************************************************************************************************************
  //    * 13JAN2018:Pulkit: Certain recursive recognized field revision are finding max score fields.
  //    * However, further down the line in same function call stack, confirmUnaryField is also called which also finds max score fields.
  //    * An analysis needs to be performed to remove all the redundant calls to this function.
  //    ********************************************************************************************************************
  //    ********************************************************************************************************************
  //    * */
  //    if (possibleEntities.nonEmpty) {
  //
  //      var toleratedScore = possibleEntities.maxBy(_.SimilarityScore).SimilarityScore
  //      if (useSimilarityTolerance) toleratedScore -= similarityTolerance
  //      possibleEntities.filter(_.SimilarityScore >= toleratedScore)
  //    } else List[PossibleEntity]()
  //  }
  //
  //  def findMaxScorePossibleFields(possibleFields: List[PossibleField],
  //                                 useSimilarityTolerance: Boolean): List[PossibleField] = {
  //
  //    /*
  //    ********************************************************************************************************************
  //    ********************************************************************************************************************
  //    * 13JAN2018:Pulkit: Certain recursive recognized field revision are finding max score fields.
  //    * However, further down the line in same function call stack, confirmUnaryField is also called which also finds max score fields.
  //    * An analysis needs to be performed to remove all the redundant calls to this function.
  //    ********************************************************************************************************************
  //    ********************************************************************************************************************
  //    * */
  //    if (possibleFields.nonEmpty) {
  //
  //      var toleratedScore = possibleFields.maxBy(_.SimilarityScore).SimilarityScore
  //      if (useSimilarityTolerance) toleratedScore -= similarityTolerance
  //      possibleFields.filter(_.SimilarityScore >= toleratedScore)
  //    } else List[PossibleField]()
  //  }
  //
  //  def recognizeTimeFrameField(ruleText: String,
  //                              expressionTexts: List[String],
  //                              referenceNuoFields: List[NuoField],
  //                              nuoGrammarListener: NuoEvaEnglishListener): Option[NuoField] = {
  //
  //    val knownTimeFrameExp = nuoGrammarListener.currAnalysisRecognitionData.TimeFrameExpressions.find(_.RuleText.equalsIgnoreCase(ruleText))
  //
  //    val knownTimeFrameField = if (knownTimeFrameExp.isDefined) {
  //      val knownTimeFrameField = knownTimeFrameExp.get.Fields.find(_.FieldText.equalsIgnoreCase(expressionTexts.head))
  //      if (knownTimeFrameField.isDefined) Some(knownTimeFrameField.get.Field)
  //      else None
  //    } else None
  //
  //    if (knownTimeFrameField.isDefined) knownTimeFrameField
  //    else {
  //
  //      val referenceFields = referenceNuoFields.map(_.FieldName)
  //      val referenceEntities = referenceNuoFields.map(_.EntityName)
  //      val possibleRecognizedFields = mutable.ArrayBuffer[RecognizedField]()
  //      if (expressionTexts != null && expressionTexts.size == 1 && expressionTexts.head.equalsIgnoreCase("DATE")) {
  //        //      throw new Exception("I cannot recognize timeFrame field with empty expression")
  //        //    } else {
  //
  //        if (referenceFields != null && referenceFields.nonEmpty) {
  //
  //          referenceFields.map { word =>
  //            val fieldText = word
  //            possibleRecognizedFields += RecognizedField(fieldText, getFieldPossibilities(fieldText,
  //              stageList = List(FieldMatchingStage.AgainstColName),
  //              nuoFields =
  //                NuoEvaEnglishListener
  //                  .nuoStorageEntities
  //                  .flatMap(_.Fields)
  //                  .filter(nuoField => NuoDataTypeHandler.isDateType(nuoField.DataType)),
  //              nuoGrammarListener))
  //          }
  //        }
  //      }
  //
  //      if (!possibleRecognizedFields.exists(_.possibleFields.exists(_.SimilarityScore >= similaritySuccess))) {
  //
  //        if (referenceEntities != null && referenceEntities.nonEmpty) {
  //
  //          val nuoFields =
  //            NuoEvaEnglishListener
  //              .nuoStorageEntities
  //              .filter(ele => referenceEntities.map(_.toUpperCase).contains(ele.EntityName.toUpperCase))
  //              .flatMap(_.Fields)
  //              .filter(ele => NuoDataTypeHandler.isDateType(ele.DataType))
  //
  //          expressionTexts.flatMap { expressionText =>
  //            nuoFields.map { word =>
  //              val fieldText = word.FieldName + " " + expressionText
  //              possibleRecognizedFields += RecognizedField(fieldText, getFieldPossibilities(fieldText,
  //                stageList = List(FieldMatchingStage.AgainstColName),
  //                nuoFields = nuoFields,
  //                nuoGrammarListener))
  //            }
  //          }
  //        }
  //      }
  //
  //      val recognizedFields = possibleRecognizedFields.map { recognizedField =>
  //        RecognizedField(fieldText = recognizedField.fieldText,
  //          possibleFields = revisePossibleFields(referenceEntities = referenceEntities,
  //            possibleFields = recognizedField.possibleFields))
  //      }.toList
  //
  //
  //      val confirmedField = if (recognizedFields == null || recognizedFields.isEmpty || recognizedFields.flatMap(_.possibleFields).isEmpty) {
  //
  //
  //        val userResponse = getCurrNuoUserMessage(ruleText, nuoGrammarListener)
  //        if (userResponse != null
  //          && userResponse.AnalysisId.isDefined
  //          && userResponse.RuleText.isDefined
  //          && userResponse.AnalysisId.get.equalsIgnoreCase(nuoGrammarListener.currAnalysisRecognitionData.AnalysisId)
  //          && userResponse.RuleText.get.equalsIgnoreCase(ruleText)) {
  //
  //          val submittedField = userResponse.Responses.get.head.trim
  //          val updatedDateField = recognizeField(fieldText = submittedField,
  //            expectedDataTypes = null,
  //            expectedEntity = "",
  //            expectedFields = null,
  //            allowLiteralChoice = false,
  //            allowQuestionChoice = false,
  //            nuoGrammarListener = nuoGrammarListener)
  //
  //          if (updatedDateField.isDefined) {
  //            updatedDateField
  //          } else {
  //            NuoRequestHandler.reportErrorToUser(new Exception(s"I could not find the suggested ${expressionTexts.head} date column $submittedField."))
  //            throw new Exception("Unreachable Code Statement")
  //          }
  //        } else {
  //
  //          val evaResponseMessage = s"I could not find column" +
  //            s" to determine the<i>${if (expressionTexts.head.trim.nonEmpty) " " + expressionTexts.head else ""}</i> date column of" +
  //            s" time-frame <mark>$ruleText</mark> Please help me with the expected column name."
  //
  //          sendNuoEvaMessage(
  //            analysisId = nuoGrammarListener.currAnalysisRecognitionData.AnalysisId,
  //            ruleText = ruleText,
  //            communicationType = NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_FIELD_SUGG,
  //            evaResponseMessage = evaResponseMessage,
  //            nuoUserChoices = List(),
  //            leftEntityName = "",
  //            rightEntityName = "",
  //            nuoEntities = List(),
  //            nuoMappingInput = None,
  //            nuoFileLoadOptions = None,
  //            nuoAnalyzeImageOptions = None,
  //            nuoPollingDetails = None,
  //            nuoGrammarListener = nuoGrammarListener)
  //          None
  //        }
  //
  //      } else {
  //        confirmPossibleField(ruleText, recognizedFields.flatMap(_.possibleFields), List(NuoDataTypeHandler.NuoDataType.Timestamp,
  //          NuoDataTypeHandler.NuoDataType.Date,
  //          NuoDataTypeHandler.NuoDataType.Time),
  //          allowLiteralChoice = false,
  //          allowQuestionChoice = false,
  //          nuoGrammarListener)
  //      }
  //
  //      if (confirmedField.isDefined && knownTimeFrameExp.isDefined) {
  //        knownTimeFrameExp.get.Fields :+= NuoQuestionField(expressionTexts.head, confirmedField.get)
  //      } else {
  //        nuoGrammarListener
  //          .currAnalysisRecognitionData
  //          .TimeFrameExpressions :+=
  //          NuoTimeFrameExpression(RuleText = ruleText,
  //            Fields = List(NuoQuestionField(FieldText = expressionTexts.head,
  //              Field = confirmedField.get)))
  //      }
  //      confirmedField
  //    }
  //  }
  //
  //  case class UnaryCriteria(var leftExpression: RecognizedField,
  //                           var rightExpressionText: String)
  //
  //  def recognizeUnaryField(fieldText: String,
  //                          referenceEntities: List[String],
  //                          nuoGrammarListener: NuoEvaEnglishListener): Option[NuoRecognitionMetadata.NuoUnaryExpression] = {
  //
  //
  //    if (fieldText == null || fieldText.trim.isEmpty) {
  //      NuoRequestHandler.reportErrorToUser(new Exception("I cannot recognize column with empty text."))
  //      throw new Exception("Unreachable Code Statement")
  //    } else {
  //
  //      val knownUnaryExpressions = getUnaryExpressionsForAlias(fieldText, nuoGrammarListener)
  //      if (knownUnaryExpressions.nonEmpty) confirmNuoUnaryExpression(fieldText, knownUnaryExpressions, nuoGrammarListener)
  //      else {
  //
  //        var buffer = mutable.ArrayBuffer[String]()
  //        val possibleCriteria = mutable.ArrayBuffer[UnaryCriteria]()
  //
  //
  //        val nuoFields =
  //          NuoEvaEnglishListener
  //            .nuoStorageEntities
  //            .flatMap(_.Fields)
  //        val unaryField = RecognizedField(fieldText = fieldText, possibleFields =
  //          getFieldPossibilities(fieldText,
  //            stageList = (0 until FieldMatchingStage.SynonymsMatches).toList,
  //            nuoFields,
  //            nuoGrammarListener))
  //        possibleCriteria += UnaryCriteria(leftExpression = unaryField, "")
  //
  //        val leftExpTextWords = if (unaryField.possibleFields.exists(_.SimilarityScore >= 1.0)) Array[String]()
  //        else fieldText.split(" ").filterNot(word => WordsToBeIgnored.contains(word.toUpperCase))
  //
  //        leftExpTextWords.foreach {
  //          word =>
  //            buffer += word
  //
  //            if (!possibleCriteria.exists(_.leftExpression.possibleFields.exists(_.SimilarityScore >= 1.0))) {
  //              val criteria = recognizeUnaryCriteria(buffer.toArray, leftExpTextWords, nuoGrammarListener)
  //              if (criteria.leftExpression.possibleFields.nonEmpty)
  //                possibleCriteria += criteria
  //            }
  //        }
  //        buffer.clear()
  //
  //        if (leftExpTextWords.nonEmpty && !possibleCriteria.exists(_.leftExpression.possibleFields.exists(_.SimilarityScore >= 1.0))) {
  //          leftExpTextWords.reverse.foreach {
  //            word =>
  //              buffer += word
  //              if (!possibleCriteria.exists(_.leftExpression.possibleFields.exists(_.SimilarityScore >= 1.0))) {
  //                val criteria = recognizeUnaryCriteria(buffer.toArray, leftExpTextWords, nuoGrammarListener)
  //                if (criteria.leftExpression.possibleFields.nonEmpty)
  //                  possibleCriteria += criteria
  //              }
  //          }
  //        }
  //
  //        if (!possibleCriteria.exists(_.leftExpression.possibleFields.exists(_.SimilarityScore >= 1.0))) {
  //          val locationText = recognizeLocationText(fieldText)
  //
  //          if (locationText != null) {
  //
  //            possibleCriteria ++= SynonymsOfLocation.map { word =>
  //              recognizeLocationCriteria(currLocationWord = word,
  //                locationText = locationText,
  //                nuoFields,
  //                nuoGrammarListener)
  //            }.filter(criteria => criteria.leftExpression.possibleFields.exists(_.SimilarityScore >= 1.0))
  //          }
  //        }
  //
  //        val recognizedCriteria = if (possibleCriteria.isEmpty) List[UnaryCriteria]()
  //        else if (referenceEntities != null && referenceEntities.nonEmpty) {
  //
  //          val maxScores = possibleCriteria.flatMap(_.leftExpression.possibleFields.map(_.SimilarityScore))
  //          val toleratedScore = if (maxScores.nonEmpty)
  //            possibleCriteria
  //              .flatMap(_.leftExpression.possibleFields.map(_.SimilarityScore))
  //              .max - similarityTolerance
  //          else similaritySuccess
  //
  //          val preferredCriteria =
  //            possibleCriteria
  //              .map(orgCriteria =>
  //                UnaryCriteria(RecognizedField(orgCriteria.leftExpression.fieldText,
  //                  orgCriteria.leftExpression.possibleFields
  //                    .filter(possibleField => referenceEntities.map(_.toUpperCase).contains(possibleField.EntityName.toUpperCase)
  //                      && possibleField.SimilarityScore >= toleratedScore)),
  //                  orgCriteria.rightExpressionText))
  //              .filter(_.leftExpression.possibleFields.nonEmpty)
  //
  //          if (preferredCriteria.nonEmpty) findMaxScoreUnaryCriteria(preferredCriteria.toList)
  //          else findMaxScoreUnaryCriteria(possibleCriteria.toList)
  //        } else findMaxScoreUnaryCriteria(possibleCriteria.toList)
  //
  //        confirmUnaryField(fieldText, recognizedCriteria, nuoGrammarListener)
  //
  //      }
  //    }
  //  }
  //
  //  //
  //  //  def recognizeCriteriaField(expressionText: String,
  //  //                             referenceEntities: List[String],
  //  //                             paramNuoGrammarListener: NuoEvaEnglishListener): RecognizedField = {
  //  //
  //  //
  //  //    if (expressionText == null || expressionText.trim.isEmpty) {
  //  //      throw new Exception("I cannot recognize criteria with empty expression")
  //  //    } else {
  //  //
  //  //      val fieldPossibilities = getFieldPossibilities(expressionText, useLevenshteinDistance = false, paramNuoGrammarListener)
  //  //
  //  //      RecognizedField(expressionText, revisePossibleFields(referenceEntities, fieldPossibilities))
  //  //    }
  //  //  }
  //
  //  def findMaxScoreUnaryCriteria(possibleCriteria: List[UnaryCriteria]): List[UnaryCriteria] = {
  //
  //    val flatList = possibleCriteria.flatMap(_.leftExpression.possibleFields.map(_.SimilarityScore))
  //    if (flatList.isEmpty) List[UnaryCriteria]()
  //    else {
  //      val toleratedScore = flatList.max - similarityTolerance
  //
  //      possibleCriteria.map(criteria => UnaryCriteria(leftExpression = RecognizedField(fieldText = criteria.leftExpression.fieldText,
  //        possibleFields = criteria.leftExpression.possibleFields.filter(_.SimilarityScore >= toleratedScore)),
  //        rightExpressionText = criteria.rightExpressionText)).filter(_.leftExpression.possibleFields.nonEmpty)
  //    }
  //  }
  //
  //  def recognizeUnaryCriteria(currLeftTextWords: Array[String],
  //                             standardizedLeftExpTextWords: Array[String],
  //                             nuoGrammarListener: NuoEvaEnglishListener): UnaryCriteria = {
  //    val currLeftText = currLeftTextWords.mkString(" ")
  //    val rightExp = standardizedLeftExpTextWords.diff(currLeftTextWords).mkString(" ")
  //
  //    val possibleFields =
  //      getFieldPossibilities(currLeftText,
  //        (0 until FieldMatchingStage.UsingLevenshteinDistance - 1).toList,
  //
  //        NuoEvaEnglishListener
  //          .nuoStorageEntities
  //          .flatMap(_.Fields).filter(field => calcSimilarityScore(field.EntityName,
  //          rightExp,
  //          useStrictComparision = false,
  //          useLevenshteinDistance = false) >= similaritySuccess),
  //        nuoGrammarListener)
  //
  //
  //    if (possibleFields.exists(_.SimilarityScore >= similaritySuccess)) {
  //      UnaryCriteria(leftExpression = RecognizedField(fieldText = currLeftText,
  //        possibleFields = possibleFields),
  //        rightExpressionText = "")
  //    } else {
  //      UnaryCriteria(leftExpression = RecognizedField(fieldText = currLeftText,
  //        possibleFields = possibleFields ++ getFieldPossibilities(fieldText = currLeftText,
  //          expectedEntity = "",
  //          useLevenshteinDistance = false,
  //          nuoGrammarListener = nuoGrammarListener)),
  //        rightExpressionText = rightExp)
  //    }
  //  }
  //
  //  def recognizeLocationCriteria(currLocationWord: String,
  //                                locationText: String,
  //                                nuoFields: List[NuoField],
  //                                nuoGrammarListener: NuoEvaEnglishListener): UnaryCriteria = {
  //    UnaryCriteria(leftExpression = RecognizedField(fieldText = currLocationWord,
  //      possibleFields = getFieldPossibilities(fieldText = currLocationWord,
  //        stageList = List(FieldMatchingStage.AgainstColName),
  //        nuoFields = nuoFields,
  //        nuoGrammarListener)),
  //      rightExpressionText = locationText)
  //  }

  def getNuoRelsForRecognizedEntities(recognizedEntities: List[String],
                                      nuoGrammarListener: NuoEvaEnglishListener): List[NuoRelationship] = {

    if (recognizedEntities.isEmpty) {
      List()
    } else if (recognizedEntities.size >= 2) {

      var identifiedRels = recognizedEntities.map(entity =>
        NuoRelationship(leftEntityName = null,
          rightEntityName = entity,
          intermediateEntities = null,
          commonFields = null,
          joinClause = null,
          isVerified = false)
      )
      var totalEntities = identifiedRels.length

      var index = 0
      while (index < totalEntities - 1) {

        val leftEntityName = identifiedRels(index).rightEntityName
        val rightEntityName = identifiedRels(index + 1).rightEntityName
        if (!identifiedRels.map(_.leftEntityName).contains(rightEntityName)) {

          val splitRelationships = identifiedRels.splitAt(index + 1)
          val currPairRelationships = getNuoRelsForEntityPair(leftEntityName,
            rightEntityName,
            nuoGrammarListener)

          val newlyIdentifiedRels = currPairRelationships.filter { rel =>
            !identifiedRels.map(relInner => relInner.rightEntityName).contains(rel.rightEntityName) ||
              rel.rightEntityName.equalsIgnoreCase(rightEntityName) ||
              splitRelationships._2.tail.map(_.rightEntityName).contains(rel.rightEntityName)
          }
          identifiedRels =
            splitRelationships._1 ++
              newlyIdentifiedRels ++
              splitRelationships._2.tail.filterNot(rel => newlyIdentifiedRels.map(_.rightEntityName).contains(rel.rightEntityName))
          //              splitRelationships._2.tail
          identifiedRels = if (currPairRelationships.size > 1)
            getRelsWithIdentifiedEntities(rightEntityIndex = index + 1,
              identifiedRels,
              nuoGrammarListener)
          else identifiedRels
        }
        totalEntities = identifiedRels.length
        index += 1
      }
      List(identifiedRels.head) ++ identifiedRels.tail.filter(rel => rel.leftEntityName != null)
    } else {
      List(NuoRelationship(leftEntityName = null,
        rightEntityName = recognizedEntities.head,
        intermediateEntities = null,
        commonFields = null,
        joinClause = null,
        isVerified = false))
    }
  }

  def getRelsWithIdentifiedEntities(rightEntityIndex: Int,
                                    //                         identifiedRels: List[NuoRelationship],
                                    identifiedRels: List[NuoRelationship],
                                    nuoGrammarListener: NuoEvaEnglishListener): List[NuoRelationship] = {

    val rightRel = identifiedRels(rightEntityIndex)
    val rightEntityName = rightRel.rightEntityName
    var newlyIdentifiedRelationships = mutable.ArrayBuffer[NuoRelationship]()

    identifiedRels
      .splitAt(rightEntityIndex - 1)._1
      .foreach { leftRel =>

        val leftEntityName = leftRel.rightEntityName
        val childRelationships = getNuoRelsForEntityPair(leftEntityName,
          rightEntityName,
          nuoGrammarListener)

        if (childRelationships.length == 1 && List(leftEntityName, rightEntityName)
          .forall(List(childRelationships.head.leftEntityName,
            childRelationships.head.rightEntityName).contains)) {

          /*
          * Logic to amend existing join clause
          * */
          val newlyIdentifiedRel = childRelationships.head

          if (rightRel.commonFields == null || rightRel.commonFields.isEmpty) {
            rightRel.commonFields = newlyIdentifiedRel.commonFields
            if (rightRel.joinClause == null || rightRel.joinClause.trim.isEmpty)
              rightRel.joinClause = newlyIdentifiedRel.joinClause
          }

          if (rightRel.commonFields != null
            && newlyIdentifiedRel.commonFields != null
            && rightRel.commonFields.nonEmpty
            && newlyIdentifiedRel.commonFields.nonEmpty
            && newlyIdentifiedRel.commonFields.forall(newCommonField =>
            rightRel.commonFields.forall(rightCommonField =>
              !List(rightCommonField.leftField, rightCommonField.rightField).forall(
                List(newCommonField.leftField, newCommonField.rightField).contains) //Do not forget the Negation at the beginning
            ))) {

            rightRel.commonFields ++= newlyIdentifiedRel.commonFields
            if (rightRel.joinClause != null
              && newlyIdentifiedRel.joinClause != null
              && rightRel.joinClause.trim.nonEmpty
              && newlyIdentifiedRel.joinClause.trim.nonEmpty)
              rightRel.joinClause += s" AND ${newlyIdentifiedRel.joinClause}"
          }
          rightRel
        } else {
          newlyIdentifiedRelationships ++=
            childRelationships
              .filterNot(rel =>
                identifiedRels
                  .map(_.rightEntityName)
                  .exists(List(rel.leftEntityName, rel.rightEntityName)
                    .contains))
        }
      }

    if (newlyIdentifiedRelationships.nonEmpty) {
      val splitRelationships = identifiedRels.splitAt(rightEntityIndex)
      val updIdentifiedEntities = splitRelationships._1 ++ newlyIdentifiedRelationships ++ splitRelationships._2

      newlyIdentifiedRelationships ++=
        getRelsWithIdentifiedEntities(
          rightEntityIndex,
          updIdentifiedEntities,
          nuoGrammarListener)
      splitRelationships._1 ++ newlyIdentifiedRelationships ++ splitRelationships._2
    } else {
      identifiedRels
    }
  }

  def getNuoRelsForEntityPair(leftEntityName: String,
                              rightEntityName: String,
                              nuoGrammarListener: NuoEvaEnglishListener): List[NuoRelationship] = {

    if (leftEntityName == null
      || rightEntityName == null
      || leftEntityName.equalsIgnoreCase(rightEntityName)
      || leftEntityName.trim.isEmpty
      || rightEntityName.trim.isEmpty) {
      List()
    } else {

      val identifiedRelationship = NuoEvaEnglishListener.nuoRelationships.find(rel =>
        List(leftEntityName, rightEntityName)
          .sorted
          .mkString
          .equalsIgnoreCase(
            List(rel.leftEntityName, rel.rightEntityName)
              .sorted
              .mkString))

      val identifiedRelationships =
        if (identifiedRelationship.isEmpty
          || (identifiedRelationship.isDefined
          && identifiedRelationship.get.intermediateEntities.nonEmpty)) {

          if (NuoEvaEnglishListener.nuoRelationships.exists(rel =>
            rel.leftEntityName.equalsIgnoreCase(leftEntityName)
              || rel.rightEntityName.equalsIgnoreCase(leftEntityName)) &&
            NuoEvaEnglishListener.nuoRelationships.exists(rel =>
              rel.leftEntityName.equalsIgnoreCase(rightEntityName)
                || rel.rightEntityName.equalsIgnoreCase(rightEntityName))) {

            findNuoRelationship(leftEntityName = leftEntityName,
              rightEntityName = rightEntityName,
              identifiedRels = List(),
              nuoGrammarListener = nuoGrammarListener)
          } else List()
        } else List(identifiedRelationship.get)

      val commonFields = mutable.ArrayBuffer[NuoCommonField]()

      if (identifiedRelationships.nonEmpty && identifiedRelationships.forall(_.isVerified)) {

        arrangeRelationships(leftEntityName, rightEntityName, identifiedRelationships)

      } else {

        val dummyRuleText = leftEntityName + "+" + rightEntityName + "+Missed"
        val userMessage = NuoEvaEnglishListener.nuoUserMessage

        if (userMessage != null
          && userMessage.NuoRelationshipInput.isDefined
          && userMessage.NuoRelationshipInput.get.LeftEntityName.equalsIgnoreCase(leftEntityName)
          && userMessage.NuoRelationshipInput.get.RightEntityName.equalsIgnoreCase(rightEntityName)
          && userMessage.NuoRelationshipInput.get.NuoCommonFields.isDefined
          && userMessage.NuoRelationshipInput.get.NuoCommonFields.get.nonEmpty) {

          commonFields ++= userMessage.NuoRelationshipInput.get.NuoCommonFields.get
          if (commonFields.nonEmpty) {

            arrangeRelationships(leftEntityName = leftEntityName,
              rightEntityName = rightEntityName,
              relationshipsToBeArranged = identifyAndWriteIntermediateRels(leftEntityName,
                rightEntityName,
                identifiedRelationships,
                commonFields.toList,
                nuoGrammarListener))
          } else List()
        } else {
          sendNuoEvaMessage(
            analysisId = nuoGrammarListener.currAnalysisRecognitionData.AnalysisId,
            ruleText = dummyRuleText,
            communicationType = NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_RELATIONSHIPS,
            evaResponseMessage = s"Could you select the columns which relate tables <u>$leftEntityName</u> and <u>$rightEntityName</u>?",
            leftEntityName = Some(leftEntityName),
            rightEntityName = Some(rightEntityName),
            nuoMappingInput = None,
            nuoPollingDetails = None,
            nuoGrammarListener = nuoGrammarListener)
          List()
        }
      }
    }
  }

  def identifyAndWriteIntermediateRels(leftEntityName: String,
                                       rightEntityName: String,
                                       identifiedRels: List[NuoRelationship],
                                       commonFields: List[NuoCommonField],
                                       nuoGrammarListener: NuoEvaEnglishListener): List[NuoRelationship] = {
    var verifiedRelationships = commonFields.groupBy(x => List(x.leftField.EntityName, x.rightField.EntityName).sorted)
      .map { pair =>
        val entityList = pair._1
        val joinClause =
          pair._2
            .map { commonField =>
              val castedValues = NuoDataTypeHandler.castDataTypes(grammarFields =
                List(commonField.rightField, commonField.leftField)
                  .map { nuoField =>
                    NuoGrammarField(
                      FieldText = nuoField.FieldName,
                      FieldType = NuoEvaEnglishListener.convertDataTypeToFieldType(nuoField.DataType),
                      FieldValue = s"`${nuoField.EntityName}`.`${nuoField.FieldName}`",
                      DateParts = null,
                      Datatype = nuoField.DataType,
                      EntityNames = List(nuoField.EntityName).asJava
                    )
                  },
                expectedDataType = null,
                nuoGrammarListener = nuoGrammarListener)._1
              castedValues.mkString(" = ")
            }
            .mkString(" AND ")
        NuoRelationship(leftEntityName = entityList.head,
          rightEntityName = entityList(1),
          intermediateEntities = List(),
          commonFields = pair._2,
          joinClause = joinClause,
          isVerified = true)
      }.toList

    val intermediateEntities: List[String] = verifiedRelationships
      .flatMap(ele => List(ele.leftEntityName, ele.rightEntityName))
      .filterNot(List(leftEntityName, rightEntityName).contains)
      .distinct

    val relationshipsToBeWritten = if (intermediateEntities.nonEmpty) {
      List(NuoRelationship(leftEntityName,
        rightEntityName,
        intermediateEntities,
        commonFields = List(),
        joinClause = "",
        isVerified = true)) ++ verifiedRelationships
    } else verifiedRelationships

    NuoEvaEnglishListener.nuoRelationships =
      relationshipsToBeWritten ++ NuoEvaEnglishListener.nuoRelationships.filterNot(rel =>
        (verifiedRelationships ++ identifiedRels.filterNot(_.isVerified))
          .exists(ele =>
            List(ele.leftEntityName, ele.rightEntityName).forall(List(rel.leftEntityName, rel.rightEntityName).contains)))

    relationshipsToBeWritten.foreach { relationship =>

      NuoJdbcClient.insertElseUpdateMetadataTable(
        NuoEvaEnglishListener.nuoTenantDetails.MetadataTable.nuoRelationships,
        List(
          NuoField("", "", "LeftEntityName", NuoDataTypeHandler.PostgresDataType.Text),
          NuoField("", "", "RightEntityName", NuoDataTypeHandler.PostgresDataType.Text),
          NuoField("", "", "IntermediateEntities", NuoDataTypeHandler.PostgresDataType.Text),
          NuoField("", "", "CommonFields", NuoDataTypeHandler.PostgresDataType.Text),
          NuoField("", "", "JoinClause", NuoDataTypeHandler.PostgresDataType.Text),
          NuoField("", "", "IsVerified", NuoDataTypeHandler.PostgresDataType.Boolean)
        ),
        List(
          relationship.leftEntityName,
          relationship.rightEntityName,
          relationship.intermediateEntities.mkString(","),
          relationship.commonFields.map { pair =>
            pair.leftField.EntityName + "|" + pair.leftField.FieldName + "|" + pair.leftField.DataType +
              "+" + pair.rightField.EntityName + "|" + pair.rightField.FieldName + "|" + pair.rightField.DataType
          }.mkString(","),
          relationship.joinClause,
          relationship.isVerified.toString
        ),
        s"(LeftEntityName = '${relationship.leftEntityName}' and RightEntityName = '${relationship.rightEntityName}')" +
          s" OR (RightEntityName = '${relationship.leftEntityName}' and LeftEntityName = '${relationship.rightEntityName}')"
      )
    }

    verifiedRelationships
  }

  def arrangeRelationships(leftEntityName: String,
                           rightEntityName: String,
                           relationshipsToBeArranged: List[NuoRelationship]): List[NuoRelationship] = {

    var currLeftEntity = leftEntityName
    var prevLeftEntity = ""
    val result = mutable.ArrayBuffer[NuoRelationship]()
    var endLoop = false
    while (!endLoop && result.size < relationshipsToBeArranged.size && !currLeftEntity.equalsIgnoreCase(rightEntityName)) {

      val tempRel = relationshipsToBeArranged
        .filter { rel =>
          val entityNames = List(rel.leftEntityName, rel.rightEntityName)
          entityNames.contains(currLeftEntity) &&
            (prevLeftEntity.isEmpty || !entityNames.contains(prevLeftEntity)) &&
            (result.isEmpty || !result.map(_.leftEntityName).exists(entityNames.contains))
        }
      if (tempRel.size == 1) {

        prevLeftEntity = currLeftEntity
        currLeftEntity = if (tempRel.head.rightEntityName.equalsIgnoreCase(currLeftEntity)) {

          result += NuoRelationship(currLeftEntity,
            tempRel.head.leftEntityName,
            tempRel.head.intermediateEntities,
            tempRel.head.commonFields,
            tempRel.head.joinClause,
            tempRel.head.isVerified)
          tempRel.head.leftEntityName
        } else {
          result += tempRel.head
          tempRel.head.rightEntityName
        }
      } else if (tempRel.size > 1) {
        val endRelationship = tempRel.find(rel => List(rel.leftEntityName, rel.rightEntityName).contains(rightEntityName))

        if (endRelationship.isDefined) {
          result += endRelationship.get
        } else {
          val nextLeftEntities = tempRel.flatMap(rel => List(rel.leftEntityName, rel.rightEntityName)).filterNot(Set(currLeftEntity))

          val nextLeftEntitiesCopy = nextLeftEntities

          val childRelationships = nextLeftEntities.map { nextLeftEntity =>
            arrangeRelationships(leftEntityName = nextLeftEntity,
              rightEntityName = rightEntityName,
              relationshipsToBeArranged = relationshipsToBeArranged.filter { rel =>
                val entityNames = List(rel.leftEntityName, rel.rightEntityName)
                (entityNames.forall(List(nextLeftEntity, currLeftEntity).contains) || !entityNames.forall(nextLeftEntitiesCopy.contains)) &&
                  (result.isEmpty || !result.map(_.leftEntityName).exists(entityNames.contains))
              })
          }.filter { rels =>
            if (rels.isEmpty) false
            else {
              val lastRel = rels.reverse.head
              List(lastRel.leftEntityName, lastRel.rightEntityName).contains(rightEntityName)
            }
          }
          if (childRelationships.nonEmpty) result ++= childRelationships.minBy(_.size)
        }
        endLoop = true
      } else {
        endLoop = true
      }
    }
    if (result.nonEmpty && result.reverse.head.rightEntityName.equalsIgnoreCase(rightEntityName)) result.toList
    else List()
  }


  def findNuoRelationship(leftEntityName: String,
                          rightEntityName: String,
                          identifiedRels: List[NuoRelationship],
                          nuoGrammarListener: NuoEvaEnglishListener): List[NuoRelationship] = {

    /*
    * 01FEB2018:Pulkit: Unlike NuoTenantDetails, NuoRelationship graph can be either of Acyclic or Cyclic in nature.
    * Hence, check on both nodes of an edge during traversal
    * */
    val existingRelationship = NuoEvaEnglishListener.nuoRelationships
      .find(rel =>
        List(rel.leftEntityName, rel.rightEntityName).forall(List(leftEntityName, rightEntityName).contains))
    if (existingRelationship.isDefined) {
      if (existingRelationship.get.commonFields == null || existingRelationship.get.commonFields.isEmpty) {

        NuoEvaEnglishListener.nuoRelationships
          .filter { rel =>
            !List(rel.leftEntityName, rel.rightEntityName).forall(List(leftEntityName, rightEntityName).contains) &&
              rel.isVerified &&
              rel.intermediateEntities.isEmpty
          }
          .filter(rel => List(rel.leftEntityName, rel.rightEntityName).forall((List(leftEntityName, rightEntityName) ++ existingRelationship.get.intermediateEntities).contains))
      } else List(existingRelationship.get)
    } else List()
  }

  //  def getCurrNuoUserMessage(ruleText: String,
  //                            nuoGrammarListener: NuoEvaEnglishListener): NuoUserMessage = {
  //
  //    val knownUserMessage =
  //      nuoGrammarListener
  //        .currAnalysisRecognitionData
  //        .NuoUserMessage
  //        .find(userMsg =>
  //          userMsg.AnalysisId.equalsIgnoreCase(nuoGrammarListener.currAnalysisRecognitionData.QuestionAlias)
  //            && userMsg.RuleText.isDefined
  //            && userMsg.RuleText.get.equalsIgnoreCase(ruleText)
  //        )
  //    if (knownUserMessage.isDefined)
  //      knownUserMessage.get
  //    else NuoEvaEnglishListener.nuoUserMessage
  //  }
  //  def populateNuoRelationships(nuoGrammarListener: NuoEvaEnglishListener): List[NuoRelationship] = {
  //
  //    NuoEvaEnglishListener
  //      .nuoStorageEntities
  //      .flatMap(entity => recognizeNuoRelationships(entity.EntityName, nuoGrammarListener))
  //  }
  //
  //  def populateAnyNewNuoRelationships(existingRelationships: List[NuoRelationship],
  //                                     nuoGrammarListener: NuoEvaEnglishListener): List[NuoRelationship] = {
  //
  //    NuoEvaEnglishListener
  //      .nuoStorageEntities
  //      .filterNot(entity =>
  //        existingRelationships
  //          .flatMap(rel =>
  //            List(rel.leftEntityName,
  //              rel.rightEntityName)).contains(entity.EntityName))
  //      .flatMap(entity => recognizeNuoRelationships(entity.EntityName, nuoGrammarListener))
  //
  //  }
  //
  //  def recognizeUserRelationshipInput(userInputConditions: List[String],
  //                                     nuoGrammarListener: NuoEvaEnglishListener): List[NuoCommonField] = {
  //
  //    if (userInputConditions.isEmpty) List()
  //    else {
  //
  //      userInputConditions.map { condition =>
  //
  //        val charStream = CharStreams.fromString(condition)
  //
  //        val lexer = new evaEnglishLexer(charStream)
  //        val tokens = new CommonTokenStream(lexer)
  //        val parser = new evaEnglishParser(tokens)
  //        val tree = parser.whereClauseContent()
  //
  //
  //        new ParseTreeWalker().walk(new NuoEvaEnglishListener(
  //          isChildQuestion = false,
  //          initConfigurations = false,
  //          questionAlias = condition,
  //          questionText = condition,
  //          nuoUserMessage = null,
  //          NuoEvaEnglishListener.nuoTenantDetails),
  //          tree)
  //
  //        val recognizedFields = tree.nuoFields.asScala.map(ele => NuoField(ele.get(0), ele.get(1), ele.get(2), ele.get(3))).distinct
  //        NuoCommonField(recognizedFields.head, recognizedFields.tail.toList, Some(tree.value))
  //      }
  //    }
  //  }
  //
  //  def recognizeNuoRelationships(entityName: String,
  //                                nuoGrammarListener: NuoEvaEnglishListener): List[NuoRelationship] = {
  //
  //    val nuoEntities = NuoEvaEnglishListener.nuoStorageEntities
  //    val inputNuoEntity = nuoEntities.find(_.EntityName.equalsIgnoreCase(entityName))
  //
  //    if (inputNuoEntity.isDefined) {
  //      nuoEntities
  //        .filterNot(_.EntityName.equalsIgnoreCase(entityName))
  //        .map { nuoEntity =>
  //          NuoRelationship(leftEntityName = inputNuoEntity.get.EntityName,
  //            rightEntityName = nuoEntity.EntityName,
  //            intermediateEntities = List(),
  //            commonFields = inputNuoEntity.get.Fields.map { inputNuoField =>
  //
  //              val updInputFieldName = if (SynonymsOfName.map(_.toUpperCase).contains(inputNuoField.FieldName.toUpperCase))
  //                inputNuoField.EntityName + " " + inputNuoField.FieldName
  //              else inputNuoField.FieldName
  //
  //              NuoCommonField(leftField = inputNuoField,
  //                rightField = findMaxScorePossibleFields(getFieldPossibilities(updInputFieldName,
  //                  List(FieldMatchingStage.TableNameAsPrefix,
  //                    FieldMatchingStage.AbbreviationMatches,
  //                    FieldMatchingStage.SynonymsMatches,
  //                    FieldMatchingStage.AgainstColName),
  //                  nuoEntity.Fields,
  //                  nuoGrammarListener).filter(_.SimilarityScore >= similarityEligibility),
  //                  useSimilarityTolerance = false)
  //                  .map { possibleField =>
  //                    NuoField(DatasetName = possibleField.DatasetName,
  //                      EntityName = possibleField.EntityName,
  //                      FieldName = possibleField.FieldName,
  //                      DataType = possibleField.DataType)
  //                  },
  //                joinCondition = Some("")
  //              )
  //            }.filterNot(_.rightField.isEmpty),
  //            joinClause = "",
  //            isVerified = false
  //          )
  //        }.filterNot(rel => rel.commonFields.isEmpty)
  //    } else {
  //      //      List[NuoRelationship]()
  //      NuoRequestHandler.reportErrorToUser(new Exception(s"I could not find the table with name $entityName."))
  //      throw new Exception("Unreachable Code Statement")
  //    }
  //  }

  def calcSimilarityScore(target: String,
                          input: String,
                          useStrictComparision: Boolean,
                          useLevenshteinDistance: Boolean): Double = {

    var targetArray = standardizeName(target).toUpperCase.split(" ").map(_.trim).distinct
    var inputArray = standardizeName(input).toUpperCase.split(" ").map(_.trim).distinct

    targetArray = if (useStrictComparision || useLevenshteinDistance) targetArray else targetArray.sorted.filterNot(WordsToBeIgnored.contains)
    inputArray = if (useStrictComparision || useLevenshteinDistance) inputArray else inputArray.sorted.filterNot(WordsToBeIgnored.contains)

    val updTarget = targetArray.mkString(" ")
    val updInput = inputArray.mkString(" ")

    if (useLevenshteinDistance) {
      val res = 1 - (LevenshteinDistance.getDefaultInstance.apply(updTarget, updInput).toDouble / (targetArray.length + inputArray.length))
      if (res < 0) 0.0
      else res
    } else {
      jaroWinklerDistance.apply(updTarget, updInput)
    }
  }

  def calcSimilarityScore(target: List[String],
                          input: List[String],
                          useStrictComparision: Boolean,
                          useLevenshteinDistance: Boolean): Double = {

    val targetArray = if (useStrictComparision || useLevenshteinDistance) target else target.sorted.filterNot(WordsToBeIgnored.contains)
    val inputArray = if (useStrictComparision || useLevenshteinDistance) input else input.sorted.filterNot(WordsToBeIgnored.contains)

    val updTarget = targetArray.distinct.map(_.toUpperCase).map(_.trim).mkString(" ")
    val updInput = inputArray.distinct.mkString(" ")

    if (useLevenshteinDistance) {
      val res = 1 - (LevenshteinDistance.getDefaultInstance.apply(updTarget, updInput).toDouble / (targetArray.length + inputArray.length))
      if (res < 0) 0.0
      else res
    } else {
      jaroWinklerDistance.apply(updTarget, updInput)
    }
  }

  def removeConsecutiveWhiteSpace(input: String): String = {
    input.replaceAll("[ \\t\\r?\\n]+", " ")
  }

  def standardizeName(input: String): String = {
    removeConsecutiveWhiteSpace(
      StringUtils.splitByCharacterTypeCamelCase(input)
        .flatMap(_.split("[-_\\.]"))
        .filter(_.trim.length > 0)
        .filterNot(ele => List("A", "AN", "THE").contains(ele.trim.toLowerCase))
        .mkString(" ")
    )
  }

  def getStandardizedWords(input: String): Array[String] = {
    StringUtils.splitByCharacterTypeCamelCase(input).flatMap(_.split("[-_\\.]") map removeConsecutiveWhiteSpace).filter(_.trim.length > 0)
  }

  //  def convertAdpOF(input: String): String = {
  //
  //    var output = ""
  //    /*
  //    * Input: Enrollment Id of High School Student of State of California
  //    * Output: California State High School Student Enrollment Id
  //    * */
  //
  //    var prefix = input
  //
  //    while (prefix.toUpperCase.contains("OF")) {
  //
  //      var ofIndex = prefix.toUpperCase.lastIndexOf("OF")
  //      output += " " + prefix.substring(ofIndex + 2).trim
  //      prefix = prefix.substring(0, ofIndex)
  //    }
  //    if (prefix.nonEmpty) output += " " + prefix
  //    output
  //  }
  //
  //  def recognizeDateTime(input: String): List[String] = {
  //
  //    StringUtils.splitByCharacterTypeCamelCase(input)
  //      .filterNot(ele => List("'", "ST", "ND", "RD", "TH", ",").contains(ele.toUpperCase))
  //
  //    List("")
  //
  //
  //  }
  //
  //  def getTime(input: String): String = {
  //    val inputArray = input.split(":")
  //    inputArray.filterNot(word => word.equalsIgnoreCase("AM") || word.trim.isEmpty)
  //
  //    if (input.toUpperCase.contains("PM")) {
  //      (inputArray.tail +: (inputArray.head.toInt + 12).toString).mkString(":")
  //    } else input
  //  }
  //
  //  def getYear(input: String): String = {
  //    var monthInput = input
  //    if (input.length < 2) monthInput = "0" + input
  //    if (input.length == 2) {
  //
  //      if (monthInput.toInt > 18) "19" + monthInput
  //      else "20" + monthInput
  //    } else monthInput
  //  }
  //
  //  def getMonth(input: String): String = {
  //
  //    input match {
  //      //      case x if x.toUpperCase.startsWith("JAN") => "1"
  //      case x if x.toUpperCase.startsWith("FEB") => "2"
  //      case x if x.toUpperCase.startsWith("MAR") => "3"
  //      case x if x.toUpperCase.startsWith("APR") => "4"
  //      case x if x.toUpperCase.startsWith("MAY") => "5"
  //      case x if x.toUpperCase.startsWith("JUN") => "6"
  //      case x if x.toUpperCase.startsWith("JUL") => "7"
  //      case x if x.toUpperCase.startsWith("AUG") => "8"
  //      case x if x.toUpperCase.startsWith("SEP") => "9"
  //      case x if x.toUpperCase.startsWith("OCT") => "10"
  //      case x if x.toUpperCase.startsWith("NOV") => "11"
  //      case x if x.toUpperCase.startsWith("DEC") => "12"
  //      case _ => "1"
  //    }
  //  }
  //
  //  def recognizeLocationText(fieldText: String): String = {
  //
  //    try {
  //      val charStream = CharStreams.fromString(fieldText)
  //      val lexer = new adpLocationCriteriaLexer(charStream)
  //      val tokens = new CommonTokenStream(lexer)
  //      val parser = new adpLocationCriteriaParser(tokens)
  //
  //      parser.setErrorHandler(new BailErrorStrategy())
  //      val tree = parser.fieldText()
  //
  //      new ParseTreeWalker().walk(new NuoAdpLocCriteriaListener(), tree)
  //      tree.value
  //
  //    } catch {
  //      case e: RuntimeException =>
  //        null
  //    }
  //  }

  class BailErrorStrategy extends DefaultErrorStrategy {
    /** Instead of recovering from exception e, rethrow it wrapped
      * in a generic RuntimeException so it is not caught by the
      * rule function catches.  Exception e is the "cause" of the
      * RuntimeException.
      */

    override def recover(recognizer: Parser, e: RecognitionException): Unit = {
      throw new RuntimeException(e)
    }

    /** Make sure we don't attempt to recover inline; if the parser
      * successfully recovers, it won't throw an exception.
      */
    override def recoverInline(recognizer: Parser): Token = {

      throw new RuntimeException(new InputMismatchException(recognizer))
    }

    /** Make sure we don't attempt to recover from problems in subrules.
      * */
    override def sync(recognizer: Parser): Unit = {

    }

  }

  //
  //  def requestField(fieldText: String,
  //                   allowLiteralChoice: Boolean,
  //                   expectedEntity: String,
  //                   nuoGrammarListener: NuoEvaEnglishListener): Option[NuoField] = {
  //
  //    val userResponse = getCurrNuoUserMessage(fieldText, nuoGrammarListener)
  //
  //    if (userResponse != null
  //      && userResponse.AnalysisId.isDefined
  //      && userResponse.RuleText.isDefined
  //      && userResponse.AnalysisId.get.equalsIgnoreCase(nuoGrammarListener.currAnalysisRecognitionData.AnalysisId)
  //      && userResponse.RuleText.get.equalsIgnoreCase(fieldText)) {
  //
  //      if (userResponse.CommunicationType.get %
  //        NuoRecognitionMetadata
  //          .RecognitionCommunicationType
  //          .COMMUNICATION_TYPE_LITERAL == 0) {
  //        Some(NuoField("", "", s"'$fieldText'", NuoDataTypeHandler.NuoDataType.String))
  //
  //      } else if (userResponse.CommunicationType.get %
  //        NuoRecognitionMetadata
  //          .RecognitionCommunicationType
  //          .COMMUNICATION_TYPE_FIELD_SUGG == 0) {
  //        getFieldSuggestion(userResponse.Responses.get.head, allowLiteralChoice, allowQuestionChoice = false, expectedEntity, nuoGrammarListener)
  //
  //      } else {
  //        NuoRequestHandler.reportErrorToUser(new Exception("I could not understand the user input."))
  //        throw new Exception("Unreachable Code Statement")
  //      }
  //    } else {
  //
  //      var requestChoices = mutable.ArrayBuffer[NuoUserChoice]()
  //
  //      //      requestChoices += NuoUserChoice("I have a suggestion for you", "2")
  //      var userInputType = NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_FIELD_SUGG
  //
  //      if (allowLiteralChoice) {
  //        //        requestChoices += NuoUserChoice("Take it literally", "1")
  //        userInputType *= NuoRecognitionMetadata.RecognitionCommunicationType.COMMUNICATION_TYPE_LITERAL
  //      }
  //
  //      sendNuoEvaMessage(
  //        analysisId = nuoGrammarListener.currAnalysisRecognitionData.AnalysisId,
  //        ruleText = fieldText,
  //        communicationType = userInputType,
  //        evaResponseMessage = s"I could not recognize column <mark>$fieldText<mark>. Could you please let me know what you are expecting?",
  //        nuoUserChoices = requestChoices.toList,
  //        leftEntityName = "",
  //        rightEntityName = "",
  //        nuoEntities = List(),
  //        nuoMappingInput = None,
  //        nuoFileLoadOptions = None,
  //        nuoAnalyzeImageOptions = None,
  //        nuoPollingDetails = None,
  //        nuoGrammarListener = nuoGrammarListener)
  //      None
  //    }
  //  }
  //
  //  def getFieldSuggestion(userSuggestedFieldName: String,
  //                         allowLiteralChoice: Boolean,
  //                         allowQuestionChoice: Boolean,
  //                         expectedEntity: String,
  //                         nuoGrammarListener: NuoEvaEnglishListener): Option[NuoField] = {
  //
  //    val suggestedFields = getFieldPossibilities(userSuggestedFieldName, expectedEntity, useLevenshteinDistance = false, nuoGrammarListener)
  //
  //    if (suggestedFields.isEmpty) {
  //      NuoRequestHandler.reportErrorToUser(new Exception(s"Duh! I could not find '$userSuggestedFieldName'."))
  //      throw new Exception("Unreachable Code Statement")
  //    } else if (suggestedFields.size == 1) {
  //      val possibleField = suggestedFields.head
  //      Some(NuoField(possibleField.DatasetName, possibleField.EntityName, possibleField.FieldName, possibleField.DataType))
  //    } else {
  //      confirmPossibleField(fieldText = userSuggestedFieldName,
  //        fieldsToBeConfirmed = suggestedFields,
  //        expectedDataTypes = null,
  //        allowLiteralChoice,
  //        allowQuestionChoice,
  //        nuoGrammarListener)
  //    }
  //  }

  def main(args: Array[String]): Unit = {

    val fieldTextList = List("populated level",
      "Student-Id",
      "Generated Student Id",
      "Dept stdnt id",
      "Dept student id",
      "Departmet student id",
      "Injected items",
      "metermodel code",
      "meetr modle",
      "meter code",
      "cod for meter",
      "meter name",
      "meter nme"
    )

    //    System.out.NuoLogger.printInfo(calcSimilarityScore("is valid", "valid credit card", useStrictComparision = true, useLevenshteinDistance = false))
    //    System.out.NuoLogger.printInfo(calcSimilarityScore("Credit_Card_ID", "valid credit card", useStrictComparision = true, useLevenshteinDistance = false))
    //    System.out.NuoLogger.printInfo(StringUtils.splitByCharacterTypeCamelCase("GMT+5:30").mkString("\n"))

    var x = "change of money"
    var y = "change op money"
    NuoLogger.printInfo(calcSimilarityScore(x, y, useStrictComparision = true, useLevenshteinDistance = false))
    NuoLogger.printInfo(new JaccardSimilarity().apply(x, y))
  }
}
