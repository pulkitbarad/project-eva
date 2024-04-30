package server


/**
  * Copyright 2016 Nuocanvas Inc.
  *
  *
  * Created by Pulkit (pulkit@nuocanvas.com) on 06APR2016
  *
  * Content of this file is proprietary and confidential.
  * It should not be reused or disclosed without prior consent
  * of distributor
  **/

import java.util.{Calendar, TimeZone}

import com.amazonaws.services.lambda.runtime.Context
import logging.NuoLogger._


object ApiEntry {

  def lambdaHandler(requestContent: String, context: Context): String = {

//    logInfo(s"Execution started at ${Calendar.getInstance(TimeZone.getDefault).getTime.toString}")

    ""
  }
}
