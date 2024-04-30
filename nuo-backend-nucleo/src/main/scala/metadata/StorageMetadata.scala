package metadata

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 11Apr2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object StorageMetadata {


  case class NuoFSDetails(NuoFiles: List[NuoFile])

  case class NuoFile(FileName: String,
                     CreationTime: String,
                     LastModifiedTime: String,
                     SizeBytes: String)

  case class NuoStorageDetails(Id: String,
                               NuoDatasets: List[NuoDataset])

  case class NuoDataset(DatasetName: String,
                        NuoEntities: List[NuoEntity])

  case class NuoEntity(DatasetName: String,
                       EntityName: String,
                       Description: Option[String],
                       CreationTime: Option[String],
                       LastModifiedTime: Option[String],
                       SizeBytes: Option[String],
                       SizeRows: Option[String],
                       Fields: List[NuoField])

  case class NuoField(DatasetName: String,
                      EntityName: String,
                      FieldName: String,
                      DataType: String)

  //  def getNuoRecognitionStorageMetadata: NuoRecognitionStorageMetadata = {
  //
  //    val storageDetails = StorageMetadata
  //      .readNuoStorageDetails(
  //        overridingContent =
  //          NuoS3Client
  //            .readFromS3(NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName,
  //              NuoEvaEnglishListener.NuoRecogStorageDetailsFileName))
  //
  //
  //    NuoRecognitionStorageMetadata(
  //      storageDetails.Id,
  //      storageDetails
  //        .NuoDatasets
  //        .flatMap { nuoDataset =>
  //          nuoDataset.NuoEntities
  //        }.map { nuoEntity =>
  //        NuoEntity(
  //          nuoEntity.EntityName,
  //          nuoEntity.Fields
  //            .map { nuoGrammarField =>
  //              NuoField(nuoEntity.EntityName, nuoGrammarField.FieldName, nuoGrammarField.DataType)
  //            }
  //        )
  //      }
  //    )
  //  }

  //  def readNuoStorageDetails(): NuoStorageDetails = {
  //
  //
  //    try {
  //
  //      /*
  //      Following statement is important to parse json.
  //      */
  //      implicit val formats = DefaultFormats
  //
  //      val content =
  //        if (NuoEvaEnglishListener.isGrammarTestMode) {
  //          Source.fromFile("src/test/data/NuoStorage_Metadata.json").getLines().mkString
  //        } else
  //          NuoS3Client.readFromS3(NuoEvaEnglishListener.nuoTenantDetails.tenantBucketName, NuoEvaEnglishListener.nuoTenantDetails.NuoStorageMetadataFileName)
  //
  //      if (content == null || content.trim.isEmpty) {
  //        if (NuoBqClient.writeStorageMetadataToFile())
  //          readNuoStorageDetails()
  //        else
  //          null
  //      } else {
  //        val jsonAST = parse(content)
  //        jsonAST.extract[NuoStorageDetails]
  //      }
  //
  //    } catch {
  //      case e: Exception => NuoLogger.printException(e)
  //        null
  //    }
  //  }
}
