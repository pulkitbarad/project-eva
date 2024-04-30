//package client
//
//import action.NuoStorage
//import canvas.NuoModifier.ActionAttrName.{ColumnName, ColumnSource, DataType}
//import com.google.api.services.bigquery.model.TableReference
//import com.spotify.scio.ScioContext
//import com.spotify.scio.bigquery.TableRow
//import org.apache.beam.sdk.io.gcp.bigquery
//import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO
//import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.{CreateDisposition, WriteDisposition}
//import org.apache.beam.sdk.transforms.{DoFn, ParDo}
//
//import scala.collection.mutable
//
///*Spotify scio imports*/
//
//import com.spotify.scio._
//import com.spotify.scio.accumulators._
//
///**
//  * Copyright 2015-2018 NuoCanvas.
//  *
//  *
//  * Created by Pulkit on 28Jun2017
//  *
//  * Content of this file is proprietary and confidential.
//  * It shall not be reused or disclosed without prior consent
//  * of distributor
//  **/
//
//object NuoDataflowClient {
//
//
//  def main(args:Array[String]): Unit ={
//    fileTest()
//  }
//  /*
//    SBT
//    runMain
//    com.spotify.scio.examples.MinimalWordCount
//    --project=[PROJECT] --runner=DataflowRunner --zone=[ZONE]
//    --input=gs://dataflow-samples/shakespeare/kinglear.txt
//    --output=gs://[BUCKET]/[PATH]/minimal_wordcount
//    */
//
//  def fileTest(): Unit = {
//
//    val sc = ScioContext()
//
//    val input = "gs://dataflow-samples/shakespeare/kinglear.txt"
//    val output = "gs://tenant1-backend-nuocanvas-com/out.txt"
//
//    // initialize accumulators
//    val max = sc.maxAccumulator[Int]("maxLineLength")
//    val min = sc.minAccumulator[Int]("minLineLength")
//    val sumNonEmpty = sc.sumAccumulator[Long]("nonEmptyLines")
//    val sumEmpty = sc.sumAccumulator[Long]("emptyLines")
//
//    sc.textFile(input)
//      .map(_.trim)
//      .accumulateBy(max, min)(_.length)
//      .accumulateCountFilter(sumNonEmpty, sumEmpty)(_.nonEmpty)
//      .flatMap(_.split("[^a-zA-Z']+").filter(_.nonEmpty))
//      .countByValue
//      .map(t => t._1 + ": " + t._2)
//      .saveAsTextFile(output)
//
//    val result = sc.close().waitUntilFinish()
//
//    // retrieve accumulator values
//    println("Max: " + result.accumulatorTotalValue(max))
//    println("Min: " + result.accumulatorTotalValue(min))
//    println("Sum non-empty: " + result.accumulatorTotalValue(sumNonEmpty))
//    println("Sum empty: " + result.accumulatorTotalValue(sumEmpty))
//
//
//  }
//
//  def bqTest(): Unit = {
//
//    val tempLocation = "gs://tenant1-backend-nuocanvas-com/temp"
//    val project = "geometric-watch-153714"
//    val DatasetName = "internal"
//    val output = "gs://tenant1-backend-nuocanvas-com/out"
//
//
//    // initialize accumulators
//    val (sc, args) = ContextAndArgs(Array("--tempLocation="+tempLocation,"--project="+project,"--output="+output))
//
//    val columns = new mutable.ListBuffer[mutable.HashMap[String, String]]()
//
//    columns += mutable.HashMap[String, String](
//      ColumnName -> "Timestamp",
//      DataType -> "Int64",
//      ColumnSource -> "__null")
//
//    columns += mutable.HashMap[String, String](
//      ColumnName -> "ProcessName",
//      DataType -> "String",
//      ColumnSource -> "__null")
//
//    columns += mutable.HashMap[String, String](
//      ColumnName -> "ServiceName",
//      DataType -> "String",
//      ColumnSource -> "__null")
//
//    columns += mutable.HashMap[String, String](
//      ColumnName -> "UnitOfMeasure",
//      DataType -> "String",
//      ColumnSource -> "__null")
//
//    columns += mutable.HashMap[String, String](
//      ColumnName -> "UnitPriceEuro",
//      DataType -> "Float64",
//      ColumnSource -> "__null")
//
//    columns += mutable.HashMap[String, String](
//      ColumnName -> "UsageCount",
//      DataType -> "Float64",
//      ColumnSource -> "__null")
//
//    columns += mutable.HashMap[String, String](
//      ColumnName -> "UsageCostEuro",
//      DataType -> "Float64",
//      ColumnSource -> "__null")
//
//    columns += mutable.HashMap[String, String](
//      ColumnName -> "UsageCount2",
//      DataType -> "Float64",
//      ColumnSource -> "__null")
//
//    val tableObj = NuoStorage.createBqTableObject(DatasetName,"newTable",columns)
//
//    sc.bigQueryTable(new TableReference().setDatasetId(DatasetName).setProjectId(project).setTableId("nuo_usage"))
////    sc.bigQuerySelect("SELECT\n" +
////      "  FORMAT_TIMESTAMP('%Y%m',TIMESTAMP_MILLIS(MAX(Timestamp))),\n" +
////      "  ServiceName,\n" +
////      "  SUM(UsageCount),\n" +
////      "  0 - SUM(UsageCostEuro)\n" +
////      "FROM\n" +
////      "  internal.nuo_usage\n" +
////      "GROUP BY\n" +
////      "  ServiceName,\n" +
////      "  FORMAT_TIMESTAMP('%Y%m',TIMESTAMP_MILLIS(Timestamp))\n" +
////      "order by 1")
////      .map(NuoBqClient.transformBQRowToList(_).mkString("||"))
//        .saveAsBigQuery(tableObj.getTableReference.setProjectId(project),tableObj.getFields,WriteDisposition.WRITE_TRUNCATE,CreateDisposition.CREATE_IF_NEEDED,"Sample Table created by SDK")
//
//    val result = sc.close().waitUntilFinish()
//  }
//}
