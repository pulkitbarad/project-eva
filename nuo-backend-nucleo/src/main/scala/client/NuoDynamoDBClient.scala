package client

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.internal.InternalUtils
import execution.NuoRequestHandler
import nlp.grammar.NuoEvaEnglishListener

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 13Mar2018
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoDynamoDBClient {


  //  def main(args: Array[String]): Unit = {
  //
  //    implicit val formats = DefaultFormats
  //
  //    Source
  //      .fromFile("src/test/data/NuoRelationships.txt")
  //      .getLines()
  //      .map(ele => (parse(ele).extract[NuoRelationship], ele))
  //      .foreach(ele =>
  //        putItem(EntityName = "NuoRelationship",
  //          tenantId = "tenant1",
  //          sortKey = List(ele._1.leftEntityName, ele._1.rightEntityName)
  //            .map(_.toLowerCase)
  //            .sorted
  //            .mkString("+"),
  //          content = ele._2))
  //  }

  def createCustomDynamoDBClient(accessKey: String,
                                 secretKey: String): AmazonDynamoDBClient = {

    val awsCredentials = new BasicAWSCredentials(accessKey, secretKey)
    new AmazonDynamoDBClient(awsCredentials)
  }

  def putItem(tableName: String,
              tenantId: String,
              sortKey: String,
              content: String): Unit = {

    val accessKey = ""
    val secretKey = ""

    val awsCredentials = new BasicAWSCredentials(accessKey, secretKey)
    new AmazonDynamoDBClient(awsCredentials)
      /*NuoEvaEnglishListener.nuoTenantDetails.amazonDynamoDBClient*/ .putItem(tableName,
      InternalUtils.toAttributeValues(new Item()
        .withPrimaryKey("tenantId", tenantId)
        .withString("sortKey", NuoRequestHandler.getMd5Hash(sortKey))
        .withString("content", content)))
  }

  def getItem(tableName: String,
              tenantId: String,
              sortKey: String): String = {

    NuoEvaEnglishListener.Client.amazonDynamoDBClient.getItem(tableName, InternalUtils.toAttributeValues(new Item()
      .withPrimaryKey("tenantId", tenantId)
      .withString("sortKey", NuoRequestHandler.getMd5Hash(sortKey)))).toString
  }

  def deleteItem(tableName: String,
                 tenantId: String,
                 sortKey: String): Unit = {

    NuoEvaEnglishListener.Client.amazonDynamoDBClient.deleteItem(tableName, InternalUtils.toAttributeValues(new Item()
      .withPrimaryKey("tenantId", tenantId)
      .withString("sortKey", NuoRequestHandler.getMd5Hash(sortKey))))
  }
}
