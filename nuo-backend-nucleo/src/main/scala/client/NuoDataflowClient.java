//package client;
//
//import com.google.api.services.bigquery.model.TableFieldSchema;
//import com.google.api.services.bigquery.model.TableReference;
//import com.google.api.services.bigquery.model.TableRow;
//import com.google.api.services.bigquery.model.TableSchema;
//import org.apache.beam.sdk.Pipeline;
//import org.apache.beam.sdk.io.TextIO;
//import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
//import org.apache.beam.sdk.options.PipelineOptions;
//import org.apache.beam.sdk.options.PipelineOptionsFactory;
//import org.apache.beam.sdk.transforms.*;
//import org.apache.beam.sdk.values.KV;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Copyright 2015-2018 NuoCanvas.
// * <p>
// * <p>
// * Created by Pulkit on 28Jun2017
// * <p>
// * Content of this file is proprietary and confidential.
// * It shall not be reused or disclosed without prior consent
// * of distributor
// **/
//
//public class NuoDataflowClient {
//
//
//    /**
//     * Examines each row in the input table. If a tornado was recorded
//     * in that sample, the month in which it occurred is output.
//     */
//    static class ExtractTornadoesFn extends DoFn<TableRow, Integer> {
//        @ProcessElement
//        public void processElement(ProcessContext c){
//            TableRow row = c.element();
//            if ((Boolean) row.get("tornado")) {
//                c.output(Integer.parseInt((String) row.get("month")));
//            }
//        }
//    }
//
//    /**
//     * Prepares the data for writing to BigQuery by building a TableRow object containing an
//     * integer representation of month and the number of tornadoes that occurred in each month.
//     */
//    static class FormatCountsFn extends DoFn<TableRow, TableRow> {
//        @ProcessElement
//        public void processElement(ProcessContext c) {
//            c.output(c.element());
//        }
//    }
//
//    public static void main(String[] args) {
//        // Create a PipelineOptions object. This object lets us set various execution
//        // options for our pipeline, such as the runner you wish to use. This example
//        // will run with the DirectRunner by default, based on the class path configured
//        // in its dependencies.
//
//        String tempLocation = "gs://tenant1-backend-nuocanvas-com/temp";
//        String project = "geometric-watch-153714";
//        String DatasetName = "internal";
//        String output = "gs://tenant1-backend-nuocanvas-com/out";
//        String[] options= {"--tempLocation="+tempLocation,"--project="+project};
//        PipelineOptions pipelineOptions = PipelineOptionsFactory.fromArgs(options).create();
//
//        List<TableFieldSchema> fields = new ArrayList();
//        fields.add(new TableFieldSchema().setName("Timestamp").setType("STRING"));
//        fields.add(new TableFieldSchema().setName("ProcessName").setType("STRING"));
//        fields.add(new TableFieldSchema().setName("ServiceName").setType("STRING"));
//        fields.add(new TableFieldSchema().setName("UnitOfMeasure").setType("STRING"));
//        fields.add(new TableFieldSchema().setName("UnitPriceEuro").setType("STRING"));
//        fields.add(new TableFieldSchema().setName("UsageCount").setType("STRING"));
//        fields.add(new TableFieldSchema().setName("UsageCostEuro").setType("STRING"));
//        fields.add(new TableFieldSchema().setName("UsageCount2").setType("STRING"));
//        TableSchema schema = new TableSchema().setFields(fields);
//
//        // Create the Pipeline object with the options we defined above.
//        Pipeline p = Pipeline.create(pipelineOptions);
//
//        // Apply the pipeline's transforms.
//
//        // Concept #1: Apply a root transform to the pipeline; in this case, TextIO.Read to read a set
//        // of input text files. TextIO.Read returns a PCollection where each element is one line from
//        // the input text (a set of Shakespeare's texts).
//
//        // This example reads a public data set consisting of the complete works of Shakespeare.
//        p.apply(BigQueryIO.read().fromQuery("SELECT\n" +
//                "  FORMAT_TIMESTAMP('%Y%m',TIMESTAMP_MILLIS(MAX(Timestamp))),\n" +
//                "  ServiceName,\n" +
//                "  SUM(UsageCount),\n" +
//                "  0 - SUM(UsageCostEuro)\n" +
//                "FROM\n" +
//                "  internal.nuo_usage\n" +
//                "-- WHERE\n" +
//                "--   ServiceName = 'Storage_In_Tables'\n" +
//                "--   AND Timestamp >= 1495047327588\n" +
//                "GROUP BY\n" +
//                "  ServiceName,\n" +
//                "  FORMAT_TIMESTAMP('%Y%m',TIMESTAMP_MILLIS(Timestamp))\n" +
//                "order by 1").usingStandardSql())
////                .apply("", ParDo.of(new DoFn<TableRow,String>(){
////
////                    @ProcessElement
////                    public void processElement(ProcessContext c) {
////                        c.output(NuoBqClient.transformBQRowToList(c.element()).mkString("||"));
////                    }
////                }))
//                // Concept #4: Apply a write transform, TextIO.Write, at the end of the pipeline.
//                // TextIO.Write writes the contents of a PCollection (in this case, our PCollection of
//                // formatted strings) to a series of text files.
//                //
//                // By default, it will write to a set of files with names like wordcount-00001-of-00005
//                .apply( ParDo.of(new FormatCountsFn()))
//                .apply(BigQueryIO.writeTableRows()
////                        .to("my-project:output.output_table")
//                        .withSchema(schema)
//                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE)
//                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
//                        .to(new TableReference().setProjectId(project).setDatasetId(DatasetName).setTableId("newTable")));
////                .apply(TextIO.write().to("gs://tenant1-backend-nuocanvas-com/out.txt"));
//
//        // Run the pipeline.
//        p.run().waitUntilFinish();
//    }
//}
