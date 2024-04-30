package metadata

import logging.NuoLogger._
import net.liftweb.json.{DefaultFormats, _}
/**
  * Copyright 2016 NuoCanvas.
  *
  *
  * Created by Pulkit on 29Oct2016
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/


object RunMetadata {


  case class RunPlan (var RequestId :String,
                      var RunPlanId :Option[String],
                      var RunPlanName :Option[String],
                      var RunPlanType :Option[String],
                      var IsActive :Option[String],
                      var IsExecutionLenient :Option[String],
                      var LogLevel :Option[String],
                      var IsSampleExecution :Option[String],
                      var SampleSize :Option[String],
                      var RerunStrategy :Option[String],
                      var NumOfRetries :Option[String],
                      var StartPoints : List[String],
                      var EndPoints :List[String],
                      var AllowedPaths :List[String],
                      var BlockedPaths : List[String],
                      var IsFixedRate:Option[String],
                      var IsFixedDelay:Option[String],
                      var InitialDelayMillis:Option[String],
                      var ScheduleDelayOrPeriodMillis:Option[String])

  def getRunMetadata(content: String): RunPlan = {

    try{

      implicit val formats = DefaultFormats

      val runMetadataAST = parse(content)

      runMetadataAST.extract[RunMetadata.RunPlan]

    }catch{
      case e: Exception => printException(e)
        null
    }
  }
}
