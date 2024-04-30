package canvas

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import execution.NuoRequestHandler
import logging.NuoLogger
import metadata.NuoRecognitionMetadata.NuoGrammarField
import nlp.grammar.NuoEvaEnglishListener

import scala.collection.mutable


/**
  * Copyright 2015 Nuocanvas Inc.
  *
  *
  * Created by Pulkit on 29SEP2015.
  *
  * Content of this file is proprietary and confidential.
  * It should not be reused or disclosed without prior consent
  * of distributor
  **/
object NuoDataTypeHandler {

  object NuoDataType {

    val String = "STRING"
    val Bytes = "BYTES"
    val Integer = "INTEGER"
    val Int64 = "INT64"
    val Float = "FLOAT"
    val Float64 = "FLOAT64"
    val Boolean = "BOOLEAN"
    val Date = "DATE"
    val Time = "TIME"
    val Timestamp = "TIMESTAMP"

  }

  object PostgresDataType {

    val Text = "text"
    val Int = "int"
    val BigInt = "bigint"
    val Float8 = "FLOAT8"
    val Boolean = "BOOLEAN"
    val Date = "DATE"
    val Time = "TIME"
    val Timestamp = "TIMESTAMP"

  }

  def isNumberType(canvasDataType: String): Boolean = {
    List(NuoDataType.Float,
      NuoDataType.Float64,
      NuoDataType.Integer,
      NuoDataType.Int64).map(_.toUpperCase).contains(canvasDataType.toUpperCase)
  }

  def isDateType(canvasDataType: String): Boolean = {
    List(NuoDataType.Timestamp,
      NuoDataType.Time,
      NuoDataType.Date).map(_.toUpperCase).contains(canvasDataType.toUpperCase)
  }

  /*
  * This function returns the type cast expression based on input content
  * */
  def getExpressionCastFunction(input: String): String = {

    val dataType = parseBQDataType(input = input)
    if (dataType.isDefined) {
      dataType.get match {
        case NuoDataType.Date | NuoDataType.Time | NuoDataType.Timestamp =>
          getExpressionCastFunction(input, dataType.get)
        case _ => input
      }
    } else {
      NuoRequestHandler.reportErrorToUser(new Exception(s"I encountered an error because I could could not understand the data type from input $input."))
      throw new Exception("Unreachable Code statement")
    }
  }

  /*
  * This function parses returns the data type of the input value.
  * String is default data type in-case of the parsing failure
  * */
  def parseBQDataType(input: String): Option[String] = {
    if (input.isEmpty || (input.startsWith("'") && input.endsWith("'"))) {
      Some(NuoDataType.String)
    } else if (input.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.?\\d{0,6}\\+?\\d{0,2}:?\\d{0,2}$")) {

      /*
      * Regex can be further improved, e.g., ^(0?[0-9]|1[0-9]|2[0-3]):[0-5]?\d:[0-5]?\d\.?\d{0,6}\+\d{1,2}:\d{1,2}$
      */
      /*
      * Canonical format:
      * YYYY-[M]M-[D]D[ [H]H:[M]M:[S]S[.DDDDDD]][time zone]
      * */
      Some(NuoDataType.Timestamp)
    } else if (input.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {

      /*
      * Canonical format:
      * YYYY-[M]M-[D]D
      * */
      Some(NuoDataType.Date)

    } else if (input.matches("^\\d{1,2}:\\d{1,2}:\\d{1,2}\\.?\\d{0,6}$")) {
      /*
      * Regular expression can be improved further.
      * We support following Canonical format
      * [H]H:[M]M:[S]S[.DDDDDD]
      * */
      Some(NuoDataType.Time)
    } else if (input.matches("^\\-?\\d*\\.\\d*$")) {
      Some(NuoDataType.Float64)
    } else if (input.matches("^\\-?\\d*$")) {
      Some(NuoDataType.Int64)
    } else if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
      Some(NuoDataType.Boolean)
    } else {
      None
    }
  }

  /*
  * This function returns the type cast expression based on input content and input data type
  * */
  @throws[Exception]
  def getExpressionCastFunction(input: String, inputDatatype: String): String = {

    val bqDatatype = if (inputDatatype.equalsIgnoreCase("Integer")) {
      NuoDataType.Int64
    } else if (inputDatatype.equalsIgnoreCase("Float")) {
      NuoDataType.Float64
    } else inputDatatype

    bqDatatype match {
      case NuoDataType.Date | NuoDataType.Time | NuoDataType.Timestamp =>
        s"SAFE_CAST('$input' AS $bqDatatype)"
      case NuoDataType.Float64 | NuoDataType.Int64 | NuoDataType.Boolean =>
        input
      case _ =>
        //        String type
        s"'$input'"
    }
  }


  def getHighestOrderDatatype(dataTypes: List[String]): String = {
    NuoEvaEnglishListener
      .convertFieldTypeToDataType(
        dataTypes
          .map(NuoEvaEnglishListener.convertDataTypeToFieldType)
          .max
      )
  }

  def getLowestOrderDatatype(dataTypes: List[String]): String = {
    NuoEvaEnglishListener
      .convertFieldTypeToDataType(
        dataTypes
          .map(NuoEvaEnglishListener.convertDataTypeToFieldType)
          .min
      )
  }

  def castDataTypes(grammarFields: List[NuoGrammarField],
                    expectedDataType: String,
                    nuoGrammarListener: NuoEvaEnglishListener): (List[String], String) = {

    val updGrammarFields = grammarFields.filter(_ != null)

    var highestOrderDataType =
      if (expectedDataType != null && expectedDataType.nonEmpty) expectedDataType
      else getHighestOrderDatatype(updGrammarFields.map(_.Datatype))

    val isHighestOrderDate = List(NuoDataTypeHandler.NuoDataType.Timestamp,
      NuoDataTypeHandler.NuoDataType.Time,
      NuoDataTypeHandler.NuoDataType.Date).map(_.toUpperCase).contains(highestOrderDataType.toUpperCase)

    if (updGrammarFields.map(_.Datatype).contains(NuoDataTypeHandler.NuoDataType.Date)
      && updGrammarFields.map(_.Datatype).contains(NuoDataTypeHandler.NuoDataType.Time)) {
      NuoLogger.printInfo("Warning:You are trying to compare two columns with date and time which may yield undesired results!")
    }
    val shouldTimeConvToTS = highestOrderDataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.Timestamp) ||
      (highestOrderDataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.Time) &&
        updGrammarFields.exists(_.Datatype.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.Date)))

    if (shouldTimeConvToTS) highestOrderDataType = NuoDataTypeHandler.NuoDataType.Timestamp

    val truncParts = mutable.ArrayBuffer[String]()
    var stage1Results = updGrammarFields
      .map { grammarField =>

        val value = grammarField.FieldValue


        val dataType = grammarField.Datatype
        if (dataType.equalsIgnoreCase(highestOrderDataType)
          || (NuoDataTypeHandler.isNumberType(dataType) && NuoDataTypeHandler.isNumberType(highestOrderDataType))) {

          value
        } else if (shouldTimeConvToTS && dataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.Time)) {

          val updValue = if ("TIME\\'[0-9\\.\\:]+\\'".r.findFirstMatchIn(value).isDefined) value.replace("TIME", "") else value
          s"SAFE_CAST(CONCAT(${new SimpleDateFormat("yyyy-MM-dd").format(new Date())},' ',$updValue) AS TIMESTAMP)"

        } else if (
          isHighestOrderDate &&
            NuoDataTypeHandler
              .NuoDataType
              .Boolean.toUpperCase
              .equalsIgnoreCase(dataType.toUpperCase)
        ) {

          NuoRequestHandler.reportErrorToUser(new Exception(s"I cannot compare column ${grammarField.FieldValue} with date/time."))
          throw new Exception("Unreachable Code statement")
        } else if (
          isHighestOrderDate &&
            List(
              NuoDataTypeHandler.NuoDataType.Float,
              NuoDataTypeHandler.NuoDataType.Float64,
              NuoDataTypeHandler.NuoDataType.Int64,
              NuoDataTypeHandler.NuoDataType.Integer)
              .map(_.toUpperCase)
              .contains(dataType.toUpperCase)
        ) {
          if (grammarField.FieldType % NuoEvaEnglishListener.FIELD_TYPE_DEFAULT == 0) {
            NuoRequestHandler.reportErrorToUser(new Exception(s"I cannot compare column ${grammarField.FieldValue} with date/time."))
            throw new Exception("Unreachable Code statement")

          } else {

            var year, month,day, hour, minute = -1
            if (value.toFloat % 1 == 0 ||
              List(
                NuoDataTypeHandler.NuoDataType.Int64,
                NuoDataTypeHandler.NuoDataType.Integer
              ).map(_.toUpperCase)
                .contains(dataType.toUpperCase)) {
              if (highestOrderDataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.Time)) {

                NuoRequestHandler.reportErrorToUser(new Exception(s"I cannot compare $value with time. It's ambiguous."))
                throw new Exception("Unreachable Code statement")
              } else {
                year = value.toInt
                truncParts +="YEAR"
              }
            } else {
              var numDecParts = value.split("\\.")
              val numWholePart = numDecParts.head.toInt
              val numFractionPart = numDecParts.tail.head.toInt

              if (numWholePart < 24 && numFractionPart < 60) {

                hour = numFractionPart
                minute = numWholePart
                truncParts +="MINUTE"

              } else if (numWholePart <= 12 && (numFractionPart < 100 || numFractionPart > 1000)) {

                month = numWholePart
                year = numFractionPart
                truncParts +="MONTH"

              } else if (numFractionPart <= 12 && (numWholePart < 100 || numWholePart > 1000)) {

                month = numFractionPart
                year = numWholePart
                truncParts +="MONTH"

              } else {

                //No comparision between given types of values possible
                NuoRequestHandler.reportErrorToUser(new Exception(s"I cannot compare $value with date/time. It's ambiguous."))
                throw new Exception("Unreachable Code statement")
              }
            }

            val date = Calendar.getInstance()

            if (year != -1) {
              date.set(Calendar.YEAR,year)
            }
            if (month != -1) {
              date.set(Calendar.MONTH,month-1)
            }else{
              date.set(Calendar.MONTH,0)
            }
            if (day != -1) {
              date.set(Calendar.DAY_OF_MONTH,day)
            }else{
              date.set(Calendar.DAY_OF_MONTH,1)
            }

            if (hour != -1) {
              date.set(Calendar.HOUR_OF_DAY,hour)
            }else{
              date.set(Calendar.HOUR_OF_DAY,0)
            }
            if (minute != -1) {
              date.set(Calendar.MINUTE,minute)
            }else{
              date.set(Calendar.MINUTE,0)
            }

            if (highestOrderDataType.equalsIgnoreCase(NuoDataType.Timestamp)) {

              s"TIMESTAMP'${new SimpleDateFormat("yyyy-MM-dd HH:MM:ss.SSS").format(date.getTime)}'"
            } else if (highestOrderDataType.equalsIgnoreCase(NuoDataType.Time)) {

              s"TIME'${new SimpleDateFormat("HH:MM:ss").format(date.getTime)}'"
            } else {
              s"DATE'${new SimpleDateFormat("yyyy-MM-dd").format(date.getTime)}'"
            }
          }
        }
        //          /*
        //          *
        //          * *******************************************************************************************
        //          * IMPORTANT: 14JAN2018:Pulkit:
        //          * Generated SQL need to add following UDF code to query in order to use DATE_FROM_MILLIS UDF
        //          * *******************************************************************************************
        //            CREATE TEMPORARY FUNCTION
        //            DATE_FROM_MILLIS(x FLOAT64)
        //            RETURNS STRING
        //            LANGUAGE js AS """
        //            var date = new Date(parseInt(x)+3600*1000*8); //Javascript default timezone is GMT-8.
        //            var ss = date.getSeconds();
        //            var MM = date.getMinutes();
        //            var hh = date.getHours();
        //            var yy = date.getFullYear();
        //            var mm = date.getMonth()+1;
        //            var dd = date.getDate();
        //            return yy+'-'+mm+'-'+dd+' '+hh+':'+MM+':'+ss+'.0';
        //          * *******************************************************************************************
        else {
          val updValue = if ("TIME\\'[0-9\\.\\:]+\\'".r.findFirstMatchIn(value).isDefined)
            value.replace("TIME", "")
          else if ("DATE\\'[0-9\\-]+\\'".r.findFirstMatchIn(value).isDefined)
            value.replace("DATE", "")
          else value

          s"SAFE_CAST($updValue AS $highestOrderDataType)"
        }
      }

    truncParts
      .distinct
        .filter{ele=>
          if (highestOrderDataType.equalsIgnoreCase(NuoDataType.Date)) {

            List("MONTH","DAY","YEAR").contains(ele.toUpperCase)
          } else if (highestOrderDataType.equalsIgnoreCase(NuoDataType.Time)) {

            List("HOUR","MINUTE","SECOND").contains(ele.toUpperCase)
          }else true
        }
      .foreach { ele =>

        stage1Results = truncateDateTime(stage1Results, highestOrderDataType, ele)
      }

    (stage1Results,
      highestOrderDataType)
  }

  def truncateDateTime(values: List[String],
                       highestOrderDataType: String,
                       datePart: String): List[String] = {

    var stage1Results = values
    if (NuoDataTypeHandler.isDateType(highestOrderDataType)) {
      var functionName =
        if (highestOrderDataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.Timestamp)) {

          "TIMESTAMP_TRUNC"
        } else if (highestOrderDataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.Time)) {

          "TIME_TRUNC"
        } else if (highestOrderDataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.Date)) {

          "DATE_TRUNC"
        }
      stage1Results = stage1Results.map(ele => s"$functionName($ele,$datePart)")
      (stage1Results, highestOrderDataType)
    }
    stage1Results
  }

  //  def compareDateParts(fields: List[NuoGrammarField],
  //
  //                       highestOrderDataType: String): List[String] = {
  //
  //    fields.map { ele =>
  //
  //      val dateField = fields.find(_.DateParts == null)
  //      if (NuoDataTypeHandler.isDateType(highestOrderDataType)) {
  //        val functionName =
  //          if (highestOrderDataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.Timestamp)) {
  //            "TIMESTAMP_TRUNC"
  //          } else if (highestOrderDataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.Time)) {
  //            "TIME"
  //          } else if (highestOrderDataType.equalsIgnoreCase(NuoDataTypeHandler.NuoDataType.Date)) {
  //            "DATE"
  //          }
  //       if(ele.FieldType % NuoEvaEnglishListener.FIELD_TYPE_DEFAULT ==0){
  //         if(ele.DateParts.dd != null ){
  //
  //         }
  //       }else{
  //
  //       }
  //      } else
  //        ele.FieldValue
  //    }
  //  }

}
