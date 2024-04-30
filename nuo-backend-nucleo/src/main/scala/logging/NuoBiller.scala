package logging

import java.util.Calendar

import canvas.NuoModifier.ActionType
import client.NuoBqClient
import nlp.grammar.NuoEvaEnglishListener

import scala.collection.mutable

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 30Aug2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoBiller {

  object ServiceName {
    //    val ModelTraining = "Model_Training"
    //    val ModelEvaluation = "Model_Evaluation"
    //    val ModelPrediction = "Model_Prediction"
    val StorageInTables = "Storage_In_Tables"
    val StorageInFiles = "Storage_In_Files"
    val DataTransferred = "Data_Transferred"
    val DataProcessed = "Data_Processed"
    val Custom = "Custom"
  }

  def isServiceAllowed(serviceName: String,
                       usageCount: Double): Boolean = {

//    val serviceConsumption = calcServiceConsumption(serviceName)
//    if ((serviceConsumption + usageCount) <= getEssentialPlanLimit(serviceName: String)) {
//      NuoLogger.logUsage(serviceName, usageCount)
//      true
//    } else {
//      NuoLogger.logError(s"Given operation is beyond your limit for $serviceName. Your current consumption is ${serviceConsumption}")
//      false
//    }
    true
  }

  def calcServiceConsumption(serviceName: String): Double = {

    val calendar = Calendar.getInstance()

    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val startOfMonth = calendar.getTimeInMillis
    val fullyQualifiedTable = s"`${NuoEvaEnglishListener.nuoTenantDetails.userMetadataDatasetName}`.`${NuoEvaEnglishListener.nuoTenantDetails.usageTable}`"
    val serviceUsageSum =
      if (isStorageService(serviceName)) {

        NuoBqClient
          .executeDMLAndGetResult(
            s"SELECT SUM(UsageCount) FROM (" +
              s" SELECT UsageCount, RANK() OVER(PARTITION BY ServiceName ORDER BY Timestamp DESC) as rank FROM $fullyQualifiedTable" +
              s" WHERE Timestamp >= $startOfMonth  AND ( ServiceName ='${ServiceName.StorageInTables}' OR ServiceName ='${ServiceName.StorageInFiles}'))" +
              s" WHERE rank = 1",
            100)
      } else {
        NuoBqClient
          .executeDMLAndGetResult(
            s"SELECT SUM(UsageCount)  FROM $fullyQualifiedTable" +
              s" WHERE Timestamp >= $startOfMonth  AND ServiceName ='$serviceName'",
            100)
      }

    if (serviceUsageSum.nonEmpty
      && !serviceUsageSum.head.head.equalsIgnoreCase("NULL")) {

      serviceUsageSum.head.head.toDouble
    }
    else 0.0
  }


  def getUnitOfMeasure(serviceName: String): String = {

    serviceName match {

      case ActionType.Storage.SaveAsTable | ActionType.Storage.MergeIntoTable | ServiceName.DataProcessed =>
        "Megabyte"

      case x if isStorageService(x) =>
        "Gigabyte"

      case ActionType.External.InHouse.AnalyzeSentiment =>
        "Record"

      case x if isImageService(x) =>
        "Image-Count"

      case x if isVideoService(x) =>
        "Video-Duration-Minute"

      case ActionType.External.SpeechIntelligence.ConvertSpeechToText =>
        "Megabyte"

      case x if x.equalsIgnoreCase(ActionType.External.SpeechIntelligence.ConvertTextToSpeech)
        || isNlpService(x) || isTranslateService(x) =>
        "Unicode-Character"

      case ActionType.External.MLaaS.PredictValue =>
        "Record"

      case ActionType.External.MLaaS.TrainModel =>
        "Model"
    }
  }

  def getEssentialPlanLimit(serviceName: String): Double = {

    serviceName match {

      case ActionType.Storage.SaveAsTable | ActionType.Storage.MergeIntoTable | ServiceName.DataProcessed =>
        100.0
      case x if isStorageService(x) =>
        50.0
      //
      //      case ActionType.External.InHouse.AnalyzeSentiment =>
      //        0.0
      //      case x if isImageService(x) =>
      //        0.0
      //      case x if isVideoService(x) =>
      //        0.0
      //      case ActionType.External.SpeechIntelligence.ConvertSpeechToText =>
      //        0.0
      //      case x if x.equalsIgnoreCase(ActionType.External.SpeechIntelligence.ConvertTextToSpeech)
      //        || isNlpService(x) || isTranslateService(x) =>
      //        0.0
      case ActionType.External.MLaaS.PredictValue =>
        100 * 1000.0
      case ActionType.External.MLaaS.TrainModel =>
        10.0
    }
  }

  def isStorageService(serviceName: String): Boolean = {

    List[String](ServiceName.StorageInTables, ServiceName.StorageInFiles).map(_.toLowerCase).contains(serviceName.toLowerCase)
  }

  def isImageService(serviceName: String): Boolean = {

    List[String](ActionType.External.ImageIntelligence.DetectCropHint,
      ActionType.External.ImageIntelligence.DetectExplicitContent,
      ActionType.External.ImageIntelligence.DetectFace,
      ActionType.External.ImageIntelligence.DetectLabel,
      ActionType.External.ImageIntelligence.DetectLandmark,
      ActionType.External.ImageIntelligence.DetectLogo,
      ActionType.External.ImageIntelligence.DetectProperties,
      ActionType.External.ImageIntelligence.DetectTextInDocument,
      ActionType.External.ImageIntelligence.DetectTextInImage,
      ActionType.External.ImageIntelligence.FindSimilarImage).map(_.toLowerCase).contains(serviceName.toLowerCase)
  }

  def isVideoService(serviceName: String): Boolean = {

    List[String](ActionType.External.VideoIntelligence.DetectExplicitContentInVideo,
      ActionType.External.VideoIntelligence.DetectFaceInVideo,
      ActionType.External.VideoIntelligence.DetectLabelInVideo,
      ActionType.External.VideoIntelligence.DetectShotChange).map(_.toLowerCase).contains(serviceName.toLowerCase)
  }

  def isNlpService(serviceName: String): Boolean = {

    List[String](ActionType.External.NlpIntelligence.AnalyzeTextEntities,
      ActionType.External.NlpIntelligence.AnalyzeTextSentiment,
      ActionType.External.NlpIntelligence.AnalyzeTextSyntax,
      ActionType.External.NlpIntelligence.ClassifyText).map(_.toLowerCase).contains(serviceName.toLowerCase)
  }

  def isTranslateService(serviceName: String): Boolean = {

    List[String](ActionType.External.TranslateIntelligence.TranslateText).map(_.toLowerCase).contains(serviceName.toLowerCase)
  }

  def getPriceBracket(paramBilledUsageCount: Double, paramUnbilledUsageCount: Double, usageUpperLimits: List[Double], rangePrice: List[Double]): List[(Double, Double)] = {
    var billedUsageCount = paramBilledUsageCount
    var unbilledUsageCount = paramUnbilledUsageCount
    val tieredCharges = mutable.ArrayBuffer[(Double, Double)]()

    var index = 0
    //    if (paramBilledUsageCount == 0.0 || paramUnbilledUsageCount == 0.0) {
    //      List((0.0, 0.0))
    //    } else {
    //  }

    while (billedUsageCount > 0 || unbilledUsageCount > 0) {

      var usageBracket = if (index >= usageUpperLimits.length) Double.MaxValue else usageUpperLimits(index)
      //      var usageBracket = if (index >= usageUpperLimits.length) Double.MaxValue else if (index==0) usageUpperLimits(index) else usageUpperLimits(index)-usageUpperLimits(index-1)

      if (usageBracket < billedUsageCount) {

        billedUsageCount -= usageBracket
      } else {
        if (usageBracket - billedUsageCount > unbilledUsageCount) {

          tieredCharges += ((unbilledUsageCount, rangePrice(index)))
          unbilledUsageCount = 0
        } else {
          tieredCharges += ((usageBracket - billedUsageCount, rangePrice(index)))
          unbilledUsageCount -= usageBracket - billedUsageCount
        }
        billedUsageCount = 0
      }
      index += 1
    }
    tieredCharges.toList
  }

  def getServiceTieredUsageAndPrice(serviceName: String,
                                    usageCount: Double): List[(Double, Double)] = {

    val calendar = Calendar.getInstance(); // this takes current date
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)

    val sumOfUsageCount = NuoBqClient.executeDMLAndGetResult(s"SELECT SUM(UsageCount)" +
      s" FROM `${NuoEvaEnglishListener.nuoTenantDetails.userMetadataDatasetName}`.`${NuoEvaEnglishListener.nuoTenantDetails.usageTable}`" +
      s" WHERE Timestamp  >= ${calendar.getTimeInMillis} AND ServiceName ='$serviceName'" +
      s" GROUP BY ServiceName",
      100)

    val monthToDateUsage = if (sumOfUsageCount.nonEmpty && !sumOfUsageCount.head.head.equalsIgnoreCase("NULL")) sumOfUsageCount.head.head.toDouble.toLong else 0.0

    serviceName match {

      case ServiceName.StorageInTables | ServiceName.StorageInFiles =>

        /*
        *
        *  Unit of Measure = GB-month
        *  Price:
        *  =============================
        *  Usage-Bracket   | Unit Prices
        *  First 100       | @ 0,050 EUR
        *  Next  900       | @ 0,040 EUR
        *  Over  1000      | @ 0,035 EUR
        * ===============================
        *  =============================
        *  Usage-Bracket | Unit Prices
        *  0-100         | @ 0,050 EUR
        *  101-1000      | @ 0,040 EUR
        *  1000+         | @ 0,035 EUR
        * ===============================
        * */

        getPriceBracket(paramBilledUsageCount = monthToDateUsage,
          paramUnbilledUsageCount = usageCount,
          usageUpperLimits = List(100, 900).map(_ * 744.0),
          rangePrice = List(0.05, 0.04, 0.035).map(_ / 744.0))

      case ActionType.Storage.SaveAsTable | ActionType.Storage.MergeIntoTable | ServiceName.DataProcessed =>

        /*
        *
        *  Unit of Measure = GB
        *  Price:
        *  =============================
        *  Usage-Tier    | Unit Prices
        *  First 100     | @ 0,075 EUR
        *  Next  900     | @ 0,060 EUR
        *  Over  1000    | @ 0,050 EUR
        * ===============================
        *  =============================
        *  Usage-Bracket | Unit Prices
        *  0-100         | @ 0,075 EUR
        *  101-1000      | @ 0,060 EUR
        *  1000+         | @ 0,050 EUR
        * ===============================
        * */
        getPriceBracket(paramBilledUsageCount = monthToDateUsage,
          paramUnbilledUsageCount = usageCount,
          usageUpperLimits = List(100, 900).map(_ * 1024.0),
          rangePrice = List(0.075, 0.06, 0.05).map(_ / 1024.0))


      case x if isImageService(x) =>

        /*
        *
        *  Unit of Measure = image-count
        *  Price:
        *  =============================
        *  Usage-Tier	 | Unit Prices
        *  First 100   | @ 0,0080 EUR
        *  Next  400   | @ 0,0070 EUR
        *  Over  500   | @ 0,0055 EUR
        * ===============================
        *  =============================
        *  Usage-Tier	 | Unit Prices
        *  0-100       | @ 0,0080 EUR
        *  101-500     | @ 0,0070 EUR
        *  500+        | @ 0,0055 EUR
        * ===============================
        * */
        getPriceBracket(paramBilledUsageCount = monthToDateUsage,
          paramUnbilledUsageCount = usageCount,
          usageUpperLimits = List(100, 400),
          rangePrice = List(0.0080, 0.0070, 0.0055))


      case x if isVideoService(x) =>

        /*
        *
        *  Unit of Measure = video-duration-minute
        *  Price:
        *  =============================
        *  Usage-Tier	 | Unit Prices
        *  First 60    | @ 0,5 EUR
        *  Next  540   | @ 0,35 EUR
        *  Over  600   | @ 0,2 EUR
        * ===============================
        *  =============================
        *  Usage-Tier	 | Unit Prices
        *  0-60        | @ 0,5 EUR
        *  61-600      | @ 0,35 EUR
        *  600+        | @ 0,2 EUR
        * ===============================
        * */
        getPriceBracket(paramBilledUsageCount = monthToDateUsage,
          paramUnbilledUsageCount = usageCount,
          usageUpperLimits = List(60, 540),
          rangePrice = List(0.5, 0.35, 0.2))

      case ActionType.External.SpeechIntelligence.ConvertSpeechToText =>

        /*
        *
        *  Unit of Measure = MB
        *  Price:
        *  =============================
        *  Usage-Tier | Unit Prices
        *  First 10   | @ 0,10 EUR
        *  Next  90   | @ 0,08 EUR
        *  Over  100  | @ 0,04 EUR
        * ===============================
        * =============================
        *  Usage-Tier | Unit Prices
        *  1-10       | @ 0,10 EUR
        *  11-100     | @ 0,08 EUR
        *  100+       | @ 0,04 EUR
        * ===============================
        * */

        getPriceBracket(paramBilledUsageCount = monthToDateUsage,
          paramUnbilledUsageCount = usageCount,
          usageUpperLimits = List(10, 90).map(_ * 1024.0),
          rangePrice = List(0.1, 0.08, 0.04).map(_ / 1024.0))

      case ActionType.External.SpeechIntelligence.ConvertTextToSpeech | ActionType.External.TranslateIntelligence.TranslateText =>

        /*
        *
        *  Unit of Measure = Unicode-Character
        *  Price:
        *  ====================================
        *  Usage-Bracket   | Unit Prices
        *  First 100.000   | @ 0,000060 EUR
        *  Next  400.000   | @ 0,000050 EUR
        *  Over  500.000   | @ 0,000035 EUR
        * =====================================
        *  ====================================
        *  Usage-Bracket   | Unit Prices
        *  1-100.000       | @ 0,000060 EUR
        *  100.001-500.000 | @ 0,000050 EUR
        *  500.000+        | @ 0,000035 EUR
        * =====================================
        * */

        getPriceBracket(paramBilledUsageCount = monthToDateUsage,
          paramUnbilledUsageCount = usageCount,
          usageUpperLimits = List(100, 400).map(_ * 1000.0),
          rangePrice = List(60, 50, 35).map(_ / 1000 / 1000.0))

      case x if isNlpService(x) =>

        /*
        *
        *  Unit of Measure = Unicode-Character
        *  Price:
        *  =========================================
        *  Usage-Bracket   | Unit Prices
        *  First 100.000   | @ 0,000005 EUR
        *  Next  400.000   | @ 0,000004 EUR
        *  Next  500.000   | @ 0,000003 EUR
        * ==========================================
        *  =========================================
        *  Usage-Bracket   | Unit Prices
        *  1-100.000       | @ 0,000005 EUR
        *  100.001-500.000 | @ 0,000004 EUR
        *  500.000+        | @ 0,000003 EUR
        * ==========================================
        * */

        getPriceBracket(paramBilledUsageCount = monthToDateUsage,
          paramUnbilledUsageCount = usageCount,
          usageUpperLimits = List(100, 400).map(_ * 1000.0),
          rangePrice = List(5, 4, 3).map(_ / 1000.0 / 1000.0))

      case ActionType.External.MLaaS.PredictValue =>

        /*
        *
        *  Unit of Measure = Block-of-1000-Records
        *  Price:
        *  =========================================
        *  Usage-Bracket   | Unit Prices
        *  First 100.000   | @ 0,50 EUR
        *  Next  400.000   | @ 0,40 EUR
        *  Next  500.000   | @ 0,30 EUR
        * ==========================================
        *  =========================================
        *  Usage-Bracket   | Unit Prices
        *  1-100.000       | @ 0,5 EUR
        *  100.001-500.000 | @ 0,4 EUR
        *  500.000+        | @ 0,3 EUR
        * ==========================================
        * */

        getPriceBracket(paramBilledUsageCount = monthToDateUsage,
          paramUnbilledUsageCount = usageCount,
          usageUpperLimits = List(100, 400).map(_ * 1000.0),
          rangePrice = List(0.5, 0.4, 0.3))

      case ActionType.External.MLaaS.TrainModel =>

        /*
        *
        *  Unit of Measure = Hour
        *  Price:
        *  =========================================
        *  Usage-Bracket   | Unit Prices
        *  First 1.000     | @ 2,00 EUR
        *  Next  4.000     | @ 1,75 EUR
        *  Next  5.000     | @ 1,50 EUR
        * ==========================================
        *  =========================================
        *  Usage-Bracket   | Unit Prices
        *  1-1.000         | @ 2,00 EUR
        *  1.001-5.000     | @ 1,75 EUR
        *  5.000+          | @ 1,50 EUR
        * ==========================================
        * */

        getPriceBracket(paramBilledUsageCount = monthToDateUsage,
          paramUnbilledUsageCount = usageCount,
          usageUpperLimits = List(1, 4).map(_ * 1000.0),
          rangePrice = List(2.0, 1.75, 1.50))

    }
  }

}
