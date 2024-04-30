package execution

import java.net.{InetAddress, InetSocketAddress}
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.{Executors, ScheduledExecutorService, ScheduledFuture}

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import execution.NuoRequestHandler.getMessageResponseRef
import logging.NuoLogger
import logging.NuoLogger.{printException, printInfo}
import metadata.NuoRecognitionMetadata.RecognitionCommunicationType
import metadata.NuoRequestMetadata
import metadata.NuoRequestMetadata.NuoQueryResponse
import nlp.grammar.NuoEvaEnglishListener

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.io.Source

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 22Aug2018
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoBackendServer {

  val serverDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
  val serverStartTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
  val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(10000)

  def main(args: Array[String]): Unit = {

    try {
      println("Starting nuo-backend-controllore")
      NuoBackendServer.startServer("localhost", "5656")
      //      throw new Exception("Sample exception")
    } catch {
      case e: Exception =>
        printException(e)
    }
  }


  val scheduledExecutions = mutable.HashMap[String, mutable.HashMap[String, ScheduledFuture[_]]]()

  def startServer(myServerHost: String, myServerPort: String): Unit = {


    val server = HttpServer.create(new InetSocketAddress(InetAddress.getByName(myServerHost), myServerPort.toInt), Integer.MAX_VALUE)

    server.createContext("/", new NuoHttpHandler())

    server.createContext(ServerMetadata.Uri.CheckStatus, new NuoHttpHandler())
    server.createContext(ServerMetadata.Uri.v1, new NuoHttpHandler())

    server.setExecutor(scheduler)
    server.start()
    //    printInfo(s"local ip address is =>${InetAddress.getLocalHost.getHostAddress}")
    printInfo(s"I started NuoBackend Server on ${InetAddress.getByName(myServerHost)}:$myServerPort")

  }
}


class NuoHttpHandler extends HttpHandler {


  def getHeader(headerName: String, httpExchange: HttpExchange): String = {
    if (httpExchange.getRequestHeaders.containsKey(headerName))
      httpExchange.getRequestHeaders.get(headerName).asScala.head
    else
      ""
  }

  def getRequestBody(httpExchange: HttpExchange): String = {
    Source.fromInputStream(httpExchange.getRequestBody).getLines().mkString
  }

  private def sendPlainTextResponse(httpExchange: HttpExchange, responseCode: Int, responseContent: String) {

    httpExchange.getResponseHeaders.add("Access-Control-Allow-Origin", "*")
    httpExchange.getResponseHeaders.add("Content-type", "text/plain")
    httpExchange.sendResponseHeaders(responseCode, 0)
    val os = httpExchange.getResponseBody
    os.write(responseContent.getBytes)
    os.close()
    printInfo("Sent the response.")
  }

  private def sendResponse(httpExchange: HttpExchange, responseCode: Int, responseContent: String) {

    httpExchange.getResponseHeaders.add("Access-Control-Allow-Origin", "*")
    httpExchange.getResponseHeaders.add("Content-type", "application/json")
    httpExchange.sendResponseHeaders(responseCode, 0)
    val os = httpExchange.getResponseBody
    os.write(responseContent.getBytes)
    os.close()
    printInfo("Sent the response.")
  }

  private def sendPreflightResponse(httpExchange: HttpExchange, responseCode: Int) {

    httpExchange.getResponseHeaders.add("Access-Control-Allow-Origin", "*")
    httpExchange.getResponseHeaders.add("Access-Control-Allow-Headers", "*")
    httpExchange.getResponseHeaders.add("Access-Control-Allow-Methods", "POST")
    //    httpExchange.getResponseHeaders.add("Access-Control-Request-Method", "POST")
    //    httpExchange.getResponseHeaders.add("Content-type", "application/json")
    httpExchange.getResponseHeaders.add("Allow", "POST")
    //    httpExchange.setAttribute("Content-type", "text/plain")
    //    httpExchange.setAttribute("charset", "UTF-8")
    httpExchange.sendResponseHeaders(responseCode, 0)
    val os = httpExchange.getResponseBody
    os.write("OK".getBytes)
    os.close()
    printInfo("Sent the response.")
  }

  def handle(reqXchange: HttpExchange): Unit = {

    try {
      val contextPath = reqXchange.getHttpContext.getPath
      reqXchange.getRequestMethod.toUpperCase match {
        case "GET" =>

          contextPath match {

            case default =>

              val contextPath = reqXchange.getHttpContext.getPath
              sendPlainTextResponse(reqXchange, 200, "OK")
          }
        case "OPTIONS" =>

          contextPath match {

            case default =>

              val contextPath = reqXchange.getHttpContext.getPath
              sendPreflightResponse(reqXchange, 200)
          }

        case "POST" =>

          printInfo(s"This is the received POST request context==> $contextPath")

          contextPath match {

            case x if x.equalsIgnoreCase(ServerMetadata.Uri.CheckStatus) =>

              sendResponse(reqXchange, 200, System.currentTimeMillis().toString)

            case x if x.equalsIgnoreCase(ServerMetadata.Uri.v1) =>

              sendResponse(reqXchange, 200, NuoRequestHandler.performRequestOperation(NuoRequestHandler.getRequestBody(reqXchange.getRequestBody), null))

            case default =>

              val contextPath = reqXchange.getHttpContext.getPath
              sendResponse(reqXchange, 404, "-1")
              printInfo(s"I don't know about this URI [$default] in the context [$contextPath] of received POST request!")
          }
      }
    } catch {
      case e: Exception =>
        NuoLogger.printInfo("e.getMessage = " + e.getMessage)
        if (e.getMessage != null && e.getMessage.equalsIgnoreCase("UserInputRequested")) {
          NuoLogger.printInfo("NuoEvaMessage = " + NuoEvaEnglishListener.nuoEvaMessage)
          sendResponse(
            httpExchange = reqXchange,
            responseCode = 200,
            responseContent = getMessageResponseRef(statusCode = 200,
              status = "OK",
              messageRef =
                NuoQueryResponse(
                  NuoEvaMessage = NuoEvaEnglishListener.nuoEvaMessage,
                  Result = None,
                  ProfilingResult = None,
                  Pattern = None)
            ))

        } else {

          val errorMessage = if (e.getMessage == null) {
            NuoLogger.printInfo(e.getStackTraceString)
            NuoLogger.printException(e)
            NuoLogger.printInfo("Error without any message.")
            "I encountered an internal error. Please contact <a href=\"mailto:support@nuocanvas.com\" target=\"_top\">the NuoCanvas support</a>."

          } else if (e.getMessage != null && e.getMessage.toUpperCase.startsWith("ERROR_REPORT")) {
            NuoLogger.printInfo(e.getStackTraceString)
            NuoLogger.printInfo("Error Message = " + e.getMessage)
            e.getMessage

          } else {
            NuoLogger.printInfo(e.getStackTraceString)
            NuoLogger.printInfo("Error at unexpected location = " + e.getMessage)
            "I encountered an internal error: " +
              e.getMessage
                .replaceAll(NuoEvaEnglishListener.nuoTenantDetails.tenantId, "")
                .replaceAll(NuoEvaEnglishListener.nuoTenantDetails.bqProjectId, "") +
              ". Please contact <a href=\"mailto:support@nuocanvas.com\" target=\"_top\">the NuoCanvas support</a>."
          }
          sendResponse(
            httpExchange = reqXchange,
            responseCode = 200,
            responseContent = getMessageResponseRef(statusCode = 200,
              status = "OK",
              messageRef =
                NuoQueryResponse(
                  NuoEvaMessage =
                    NuoRequestMetadata.NuoEvaMessage(
                      AnalysisId = "",
                      RuleText = "",
                      CommunicationType = RecognitionCommunicationType.COMMUNICATION_TYPE_ERROR,
                      Message = errorMessage,
                      LeftEntityName = None,
                      RightEntityName = None,
                      NuoMappingInput = None,
                      NuoPollingDetails = None
                    ),
                  Result = None,
                  ProfilingResult = None,
                  Pattern = None)
            ))
        }
    }
  }
}

object ServerMetadata {

  object Uri {

    val CheckStatus = "/CheckStatus"
    val v1 = "/v1"
  }

}
