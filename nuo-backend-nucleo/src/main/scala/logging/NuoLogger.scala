package logging

import java.io.{File, FileOutputStream, PrintWriter, StringWriter}
import java.text.SimpleDateFormat
import java.util
import java.util.Date

import client.NuoBqClient
import nlp.grammar.NuoEvaEnglishListener

/**
  * Copyright 2016 NuoCanvas.
  *
  *
  * Created by Pulkit on 12OCT2016
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/


object NuoLogger {


  //Level of logging to be selected by user. It is more related to front-end preference for logging
  object LoggingLevelName {
    val Emergency = "EMERGENCY"
    val Alert = "ALERT"
    val Critical = "CRITICAL"
    val Error = "ERROR"
    val Warning = "WARNING"
    val Notice = "NOTICE"
    val Info = "INFO"
    val Debug = "DEBUG"
    val Internal = "INTERNAL"
  }

  /*Level 1*/
  def logEmergency(message: String): Unit = {
    NuoEvaEnglishListener.nuoTenantDetails.errorEncountered = true
    logMessage(LoggingLevelName.Emergency, message)
  }

  /*Level 2*/
  def logAlert(message: String): Unit = {
    logMessage(LoggingLevelName.Alert, message)
  }

  /*Level 3*/
  def logCritical(message: String): Unit = {
    NuoEvaEnglishListener.nuoTenantDetails.errorEncountered = true
    logMessage(LoggingLevelName.Critical, message)
  }

  /*Level 4*/
  def logError(message: String): Unit = {
    NuoEvaEnglishListener.nuoTenantDetails.errorEncountered = true
    logMessage(LoggingLevelName.Error, message)
  }

  /*Level 5*/
  def logWarning(message: String): Unit = {
    logMessage(LoggingLevelName.Warning, message)
  }

  /*Level 6*/
  def logNotice(message: String): Unit = {
    logMessage(LoggingLevelName.Notice, message)
  }

  /*Level 7*/
  def logInfo(message: String): Unit = {
    logMessage(LoggingLevelName.Info, message)
  }

  /*Level 8*/
  def logDebug(message: String): Unit = {
    logMessage(LoggingLevelName.Debug, message)
  }

  /*Level 9*/
  def logInternal(message: String): Unit = {
    logMessage(LoggingLevelName.Internal, message)
  }

  private def logMessage(severity: String,
                         message: String): Unit = {


    val entry = new util.HashMap[String, String]()
    entry.put("Timestamp", System.currentTimeMillis().toString)
    entry.put("RequestId", "")
    //    entry.put("ProcessName", NuoEvaEnglishListener.nuoTenantDetails.processName)
    entry.put("Severity", severity)
    entry.put("Message", message)
    NuoBqClient.streamRecordIntoTable(NuoEvaEnglishListener.nuoTenantDetails.userMetadataDatasetNameWOTenantId, NuoEvaEnglishListener.nuoTenantDetails.loggingTable, entry)
    printInfo(message)
  }


  def logUsage(serviceName: String,
               usageCount: Double): Unit = {

    val entry = new util.HashMap[String, String]()
    entry.put("Timestamp", System.currentTimeMillis().toString)
    //      entry.put("ProcessName", NuoEvaEnglishListener.nuoTenantDetails.processName)
    entry.put("UsageCount", usageCount.toString)
    entry.put("ServiceName", serviceName)
    entry.put("UnitOfMeasure", NuoBiller.getUnitOfMeasure(serviceName))
    entry.put("UnitPriceEuro", "0.0")
    entry.put("UsageCostEuro", "0.0")
    entry.put("UsageCount2", "0.0")

    NuoBqClient.streamRecordIntoTable(NuoEvaEnglishListener.nuoTenantDetails.userMetadataDatasetNameWOTenantId, NuoEvaEnglishListener.nuoTenantDetails.usageTable, entry)
  }

  def logServerMessage(message: String): Unit = {

    print(message)
    val logDir = new File("./nuo-backend-nucleo/logs")
    logDir.mkdirs()
    val logFileName = s"logs_${new SimpleDateFormat("yyyyMMdd_HH").format(new Date())}.txt"
    val logFile = new File(logDir, logFileName)

    if (!logFile.exists()) {
      logFile.createNewFile()
    }

    val outStream = new FileOutputStream(logFile, true)
    outStream.write(message.getBytes())
    outStream.close()
  }

  def printInfo(info: Any): Unit = {

    logServerMessage(s"${NuoEvaEnglishListener.ServerDateFormat.format(System.currentTimeMillis())}:nuo-backend-nucleo:INFO:$info\n")
  }

  def printException(exception: Exception): Unit = {
    var header = s"${NuoEvaEnglishListener.ServerDateFormat.format(System.currentTimeMillis())}:nuo-backend-nucleo:EXCEPTION:"
    var msg = header + "\n"
    val errors = new StringWriter()
    exception.printStackTrace(new PrintWriter(errors))

    logServerMessage(errors.toString)
  }


  //
//  def printInfo(info: String): Unit = {
//
//    val msg = if (NuoEvaEnglishListener.nuoTenantDetails != null)
//      s"${NuoEvaEnglishListener.ServerDateFormat.format(System.currentTimeMillis())}: nuo-backend-nucleo:${NuoEvaEnglishListener.nuoTenantDetails.requestId}: $info"
//    else
//      s"${NuoEvaEnglishListener.ServerDateFormat.format(System.currentTimeMillis())}: nuo-backend-nucleo: $info"
//
//    if (NuoEvaEnglishListener.lambdaContextRef != null)
//      NuoEvaEnglishListener.lambdaContextRef.getLogger.log(msg)
//    else
//      println(msg)
//  }
//
//  def printException(exception: Exception): Unit = {
//    NuoEvaEnglishListener.nuoTenantDetails.errorEncountered = true
//    throw exception
//    //    printInfo("ERR:" + exception.toString)
//    //    printInfo("ERR_STACK:" + exception.getStackTraceString)
//    //    logInternal(exception.getStackTrace.map(_.toString).mkString("\n"))
//    //    logError("Internal Server error. Please contact the NuoCanvas Support!")
//  }

}
