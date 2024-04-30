package metadata

import client.NuoS3Client
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import nlp.grammar.NuoEvaEnglishListener

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 17Nov2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object MLaaSMetadata {


  object MLDataType {
    val Binary = "BINARY"
    val Categorical = "CATEGORICAL"
    val Numeric = "NUMERIC"
    val Text = "TEXT"
  }

  object MLDatasetSuffix {
    var trainData = "_TrainData"
    var testData = "_TestData"
    var evaluationData = "_EvaluationData"
    var predictData = "_PredictData"
  }

  case class TrainDataSchema(excludedAttributeNames: List[String],
                             version: Option[String],
                             dataFormat: Option[String],
                             rowId: Option[String],
                             dataFileContainsHeader: Option[String],
                             attributes: List[DataSchemaAttribute],
                             targetAttributeName: Option[String])

  case class PredictDataSchema(excludedAttributeNames: List[String],
                               version: Option[String],
                               dataFormat: Option[String],
                               rowId: Option[String],
                               dataFileContainsHeader: Option[String],
                               attributes: List[DataSchemaAttribute])

  case class DataSchemaAttribute(attributeName: String,
                                 attributeType: String)

  def saveTrainAndPredictDataSchema(trainDataSchemaFileName: String,
                                    predictDataSchemaFileName: String,
                                    trainDataSchema: TrainDataSchema,
                                    targetColumnName: String): Unit = {


    implicit val formats = DefaultFormats

    /*
    * Save the training data schema with target attribute
    * */
    NuoS3Client.writeToS3(NuoEvaEnglishListener.BucketName.MlaasDataBucketName,
      trainDataSchemaFileName,
      write(trainDataSchema),
      appendMode = false)

    /*
    * Derive the prediction data schema from training data schema that does not have target attribute and save it
    * */
    NuoS3Client.writeToS3(NuoEvaEnglishListener.BucketName.MlaasDataBucketName,
      predictDataSchemaFileName,
      write(PredictDataSchema(excludedAttributeNames = trainDataSchema.excludedAttributeNames,
        version = trainDataSchema.version,
        dataFormat = trainDataSchema.dataFormat,
        rowId = Some("__rownum"),
        dataFileContainsHeader = trainDataSchema.dataFileContainsHeader,
        attributes = trainDataSchema
          .attributes
          .filterNot(ele => ele.attributeName.equalsIgnoreCase(targetColumnName))
      )),
      appendMode = false)

  }
}
