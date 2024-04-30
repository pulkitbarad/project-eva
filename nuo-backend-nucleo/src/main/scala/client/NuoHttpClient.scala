package client

import java.io.IOException
import java.net.{InetSocketAddress, Socket}

import logging.NuoLogger
import net.liftweb.json.Serialization._
import net.liftweb.json._
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder

import scala.io.Source

/**
  * Copyright 2016 NuoCanvas.
  *
  *
  * Created by Pulkit on 05OCT2016
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/


object NuoHttpClient {


  def sendHttpPostReqCaseClass(url: String,
                               port: String,
                               uri: String,
                               requestBody: AnyRef): String = {


    implicit val formats = DefaultFormats

    val requestBodyString = write(requestBody)

    val post = if (port.isEmpty) {
      new HttpPost(s"http://$url/$uri")
    } else {
      new HttpPost(s"http://$url:$port/$uri")
    }
    post.setHeader("Content-type", "application/json")
    post.setEntity(new StringEntity(requestBodyString))

    // send the POST request
    val httpClient = HttpClientBuilder.create().build()
    val response = httpClient.execute(post)

    val responseContent = Source.fromInputStream(response.getEntity.getContent).getLines().toList.head
    httpClient.close()
    responseContent
  }

  def sendHttpsPostReqCaseClass(url: String,
                                port: String,
                                uri: String,
                                requestBody: AnyRef): String = {


    implicit val formats = DefaultFormats

    val requestBodyString = write(requestBody)

    val post = if (port.isEmpty) {
      new HttpPost(s"https://$url/$uri")
    } else {
      new HttpPost(s"https://$url:$port/$uri")
    }
    post.setHeader("Content-type", "application/json")
    post.setEntity(new StringEntity(requestBodyString))

    // send the POST request
    val httpClient = HttpClientBuilder.create().build()
    val response = httpClient.execute(post)

    val responseContent = Source.fromInputStream(response.getEntity.getContent).getLines().toList.head
    httpClient.close()
    responseContent
  }

  def sendHttpPostRequestPlainText(url: String,
                                   port: String,
                                   uri: String,
                                   requestBody: String): String = {

    val post = if (port.isEmpty) {
      new HttpPost(s"http://$url/$uri")
    } else {
      new HttpPost(s"http://$url:$port/$uri")
    }

    post.setHeader("Content-type", "application/json")
    post.setEntity(new StringEntity(requestBody))

    // send the POST request
    val httpClient = HttpClientBuilder.create().build()
    val response = httpClient.execute(post)

    val responseContent = Source.fromInputStream(response.getEntity.getContent).getLines().toList.head
    httpClient.close()
    responseContent
  }

  //  def createConnectionManager(): Unit = {
  //    val httpConnManager = new PoolingHttpClientConnectionManager()
  //
  //    /*
  //    * setDefaultMaxPerRoute:
  //    * The maximum number of connections allowed for a route that has not been specified otherwise by a call to setMaxPerRoute.
  //    * Use setMaxPerRoute when you know the route ahead of time and setDefaultMaxPerRoute when you do not.
  //    * */
  ////    httpConnManager.setDefaultMaxPerRoute(4)
  //    httpConnManager.setMaxTotal(1000)
  //    val host = new HttpHost(s"https://$gcpRegion-$bqProjectId.cloudfunctions.net/nuo-function-sentiment", 443)
  //    httpConnManager.setMaxPerRoute(new HttpRoute(host), 1000)
  //    httpClient = HttpClients.custom().setConnectionManager(httpConnManager).build()
  //
  //  }

  def sendHttpsPostRequestPlainText(url: String,
                                    port: String,
                                    uri: String,
                                    timeoutMillis: Int,
                                    requestBody: String): Unit = {

    //    val connManager = new PoolingHttpClientConnectionManager()
    //    connManager.setMaxTotal(5)
    //    connManager.setDefaultMaxPerRoute(4)
    //    val host = new HttpHost("www.baeldung.com", 80)
    //    connManager.setMaxPerRoute(new HttpRoute(host), 5)
    //    val client
    //      = HttpClients.custom().setConnectionManager(connManager)
    //      .build()
    //    client.execute(post)


    val post = if (port.isEmpty) {
      new HttpPost(s"https://$url:443/$uri")
    } else {
      new HttpPost(s"https://$url:$port/$uri")
    }
    NuoLogger.printInfo(s"Sending https request to ${post.toString}")
    post.setHeader("Content-type", "application/json")
    post.setEntity(new StringEntity(requestBody))

    /*
    * IMPORTANT: Implement new version with Timeout.
    * */

    post.setHeader("Content-type", "application/json")
    post.setEntity(new StringEntity(requestBody))

    // send the POST request
    val httpClient = HttpClientBuilder.create().build()
    val response = httpClient.execute(post)

    val responseContent = Source.fromInputStream(response.getEntity.getContent).getLines().toList.head
    httpClient.close()
  }


  def pingHost(host: String,
               port: String,
               timeout: Int): Boolean = {

    val socket = new Socket()
    try {

      //      printInfo(s"I am pinging ===> $host:$port")
      socket.connect(new InetSocketAddress(host, port.toInt), timeout)
      true;
    } catch {
      case _: IOException =>
        //        printInfo(s"I could not ping ===> $host:$port")
        false; // Either timeout or unreachable or failed DNS lookup.
    }
  }

  //
  //  object MasterClient{
  //
  //    def register(): String ={
  //
  //
  //      val nuoUserMessage = TenantServerMetadata.RequestStructure.RunProcess(System.currentTimeMillis().toString,Some(TenantDetails.tenantId),
  //        Some(myServerHost),Some(myServerPort),None,None,None,None,None,None,None,None,None,None,None,None,None)
  //
  //      sendHttpPostReqCaseClass(masterHost,
  //        masterPort,
  //        TenantServerMetadata.TenatServerUri.TenantServerToMaster.Register,
  //        nuoUserMessage)
  //    }
  //
  //    def acquireAwsCredentials(): Unit ={
  //
  //
  //      val nuoUserMessage = TenantServerMetadata.RequestStructure.RunProcess(System.currentTimeMillis().toString,Some(TenantDetails.tenantId),
  //        None,None,None,None,None,None,None,None,None,None,None,None,None,None,None)
  //
  //      val responseContent = sendHttpPostReqCaseClass(masterHost,
  //        masterPort,
  //        TenantServerMetadata.TenatServerUri.TenantServerToMaster.GetAwsCredentials,
  //        nuoUserMessage)
  //
  //      implicit val formats = DefaultFormats
  //
  //      val reqJsonAST = parse(responseContent)
  //      val response = reqJsonAST.extract[RunProcess]
  //
  //      AwsS3Details.bucketName = response.BucketName.get
  //      AwsS3Details.accessKey= response.AccessKey.get
  //      AwsS3Details.secretKey= response.SecretKey.get
  //    }
  //
  //    def runWithHigherDriverMemory(runProcessReq:TenantServerMetadata.RequestStructure.RunProcess): Unit ={
  //
  //      sendHttpPostReqCaseClass(leaderHost,
  //        leaderPort,
  //        TenantServerMetadata.TenatServerUri.TenantServerToLeader.RunWithHigherDriverMemory,
  //        runProcessReq)
  //    }
  //  }
}
