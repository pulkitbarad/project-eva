package client

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 29SEP2018
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

import java.sql.{Connection, DriverManager, Statement}

import canvas.NuoDataTypeHandler
import metadata.StorageMetadata.NuoField
import nlp.grammar.NuoEvaEnglishListener

import scala.collection.mutable

object NuoJdbcClient {

  private val metadataUser = "xxx"
  private val metadataPassword = "xxx"


  def main(args: Array[String]): Unit = {
    //    executeMetadataQuery("SELECT * FROM tenant1_NuoHistoryFeed")
    NuoEvaEnglishListener.initNuoTenantDetails("", "tenant1", "pulkit", null)
    //    NuoStorage.Metadata.createNuoTenantDetailsTable()
    //    NuoStorage.Metadata.createNuoHistoryTable("tenant1")
    //    insertIntoMetadataTable(
    //      "master_nuotenantdetails",
    //      List(
    //        NuoField("", "", "TenantId", NuoDataTypeHandler.PostgresDataType.Text),
    //        NuoField("", "", "TenantName", NuoDataTypeHandler.PostgresDataType.Text),
    //        NuoField("", "", "email", NuoDataTypeHandler.PostgresDataType.Text),
    //        NuoField("", "", "AwsAccessKey", NuoDataTypeHandler.PostgresDataType.Text),
    //        NuoField("", "", "AwsSecretKey", NuoDataTypeHandler.PostgresDataType.Text),
    //        NuoField("", "", "GcpProjectId", NuoDataTypeHandler.PostgresDataType.Text),
    //        NuoField("", "", "GcpCredentials", NuoDataTypeHandler.PostgresDataType.Text)
    //      ),
    //      List(
    //        "tenant1",
    //        "tenant1",
    //        "pulkit@nuocanvas.com",
    //        "",
    //        "",
    //        "",
    //        "{ \"type\": \"service_account\", \"project_id\": \"geometric-watch-153714\", \"private_key_id\": \"xxx\", \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nxxx\n-----END PRIVATE KEY-----\\n\", \"client_email\": \"svc-nuocanvas@geometric-watch-153714.iam.gserviceaccount.com\", \"client_id\": \"106890565604422907087\", \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\", \"token_uri\": \"https://accounts.google.com/o/oauth2/token\", \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\", \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/svc-nuocanvas%40geometric-watch-153714.iam.gserviceaccount.com\" }")
    //    )
  }

  def createMetadataTable(tableName: String,
                          nuoFields: List[NuoField]): Unit = {

    Class.forName("org.postgresql.Driver")
    var connection: Connection = null
    var statement: Statement = null
    try {

      val jdbcUrl = "xxx"

      connection = DriverManager.getConnection(jdbcUrl, metadataUser, metadataPassword)

      val createTableQuery = new StringBuilder(s"CREATE TABLE ")
      createTableQuery.append(tableName + " (")
      createTableQuery.append(nuoFields.map(ele => ele.FieldName + " " + ele.DataType).mkString(","))
      createTableQuery.append(")")

      statement = connection.createStatement()

      statement.executeUpdate(createTableQuery.toString())
    } catch {
      case e: Exception =>
        e.printStackTrace()
    } finally {
      statement.close()
      connection.close()
    }
  }

  def insertIntoMetadataTable(tableName: String,
                              metadata: List[NuoField],
                              values: List[String]): Unit = {

    Class.forName("org.postgresql.Driver")
    var connection: Connection = null
    var statement: Statement = null
    try {

      val jdbcUrl = "jdbc:postgresql://35.204.44.33/nuo_metadata"

      connection = DriverManager.getConnection(jdbcUrl, metadataUser, metadataPassword)

      val insertQuery = new StringBuilder(s"INSERT INTO ")
      insertQuery.append(tableName + " (")
      insertQuery.append(metadata.map(_.FieldName).mkString(","))
      insertQuery.append(") VALUES (")
      var index = 0
      insertQuery.append(
        values
          .map { value =>
            metadata(index).DataType match {
              case x if x.equalsIgnoreCase(NuoDataTypeHandler.PostgresDataType.Text) =>
                s"'$value'"
              case _ =>
                value
            }
          }
          .mkString(",")
      )
      insertQuery.append(")")

      statement = connection.createStatement()

      statement.executeUpdate(insertQuery.toString())
    } catch {
      case e: Exception =>
        e.printStackTrace()
    } finally {
      statement.close()
      connection.close()
    }
  }

  def updateMetadataTable(tableName: String,
                          metadata: List[NuoField],
                          values: List[String],
                          condition: String): Unit = {

    Class.forName("org.postgresql.Driver")
    var connection: Connection = null
    var statement: Statement = null
    try {

      val jdbcUrl = "jdbc:postgresql://35.204.44.33/nuo_metadata"

      connection = DriverManager.getConnection(jdbcUrl, metadataUser, metadataPassword)

      val updateQuery = new StringBuilder(s"UPDATE ")
      updateQuery.append(tableName)
      updateQuery.append(" SET ")
      var index = -1
      updateQuery.append(
        values
          .map { value =>
            index += 1
            metadata(index).DataType match {
              case x if x.equalsIgnoreCase(NuoDataTypeHandler.PostgresDataType.Text) =>
                s"${metadata(index).FieldName} = '$value'"
              case _ =>
                s"${metadata(index).FieldName} = $value"
            }
          }
          .mkString(" , ")
      )
      updateQuery.append(" WHERE " + condition)

      statement = connection.createStatement()

      statement.executeUpdate(updateQuery.toString())
    } catch {
      case e: Exception =>
        e.printStackTrace()
    } finally {
      statement.close()
      connection.close()
    }
  }

  def insertElseUpdateMetadataTable(tableName: String,
                                    metadata: List[NuoField],
                                    values: List[String],
                                    condition: String): Unit = {

    Class.forName("org.postgresql.Driver")
    var connection: Connection = null
    var statement: Statement = null
    try {

      val jdbcUrl = "jdbc:postgresql://35.204.44.33/nuo_metadata"

      connection = DriverManager.getConnection(jdbcUrl, metadataUser, metadataPassword)

      val updateQuery = new StringBuilder(s"UPDATE ")
      updateQuery.append(tableName)
      updateQuery.append(" SET ")
      var index = -1
      updateQuery.append(
        values
          .map { value =>
            index += 1
            metadata(index).DataType match {
              case x if x.equalsIgnoreCase(NuoDataTypeHandler.PostgresDataType.Text) =>
                s"${metadata(index).FieldName} = '$value'"
              case _ =>
                s"${metadata(index).FieldName} = $value"
            }
          }
          .mkString(" , ")
      )
      updateQuery.append(" WHERE " + condition)

      val insertQuery = new StringBuilder(s"INSERT INTO ")
      insertQuery.append(tableName + " (")
      insertQuery.append(metadata.map(_.FieldName).mkString(","))
      insertQuery.append(") SELECT ")
      index = 0
      insertQuery.append(
        values
          .map { value =>
            metadata(index).DataType match {
              case x if x.equalsIgnoreCase(NuoDataTypeHandler.PostgresDataType.Text) =>
                s"'$value'"
              case _ =>
                value
            }
          }
          .mkString(",")
      )
      insertQuery.append(s" WHERE NOT EXISTS (SELECT 1 FROM $tableName WHERE $condition)")

      statement = connection.createStatement()

      statement.executeUpdate(updateQuery.toString())
      statement.executeUpdate(insertQuery.toString())

    } catch {
      case e: Exception =>
        e.printStackTrace()
    } finally {
      statement.close()
      connection.close()
    }
  }

  def deleteFromMetadataTable(tableName: String,
                              condition: String): Unit = {

    Class.forName("org.postgresql.Driver")
    var connection: Connection = null
    var statement: Statement = null
    try {

      val jdbcUrl = "jdbc:postgresql://35.204.44.33/nuo_metadata"

      connection = DriverManager.getConnection(jdbcUrl, metadataUser, metadataPassword)

      val insertQuery = new StringBuilder(s"DELETE FROM ")
      insertQuery.append(tableName)
      insertQuery.append(" WHERE " + condition)

      statement = connection.createStatement()

      statement.executeUpdate(insertQuery.toString())
    } catch {
      case e: Exception =>
        e.printStackTrace()
    } finally {
      statement.close()
      connection.close()
    }
  }

  def insertAsSelectMetadataTable(tableName: String,
                                  condition: String): Unit = {

    Class.forName("org.postgresql.Driver")
    var connection: Connection = null
    var statement: Statement = null
    try {

      val jdbcUrl = "jdbc:postgresql://35.204.44.33/nuo_metadata"

      connection = DriverManager.getConnection(jdbcUrl, metadataUser, metadataPassword)

      val insertQuery = new StringBuilder(s"INSERT INTO ")
      insertQuery.append(tableName)
      insertQuery.append(" SELECT * FROM " + tableName)
      insertQuery.append(" WHERE " + condition)

      statement = connection.createStatement()

      statement.executeUpdate(insertQuery.toString())
    } catch {
      case e: Exception =>
        e.printStackTrace()
    } finally {
      statement.close()
      connection.close()
    }
  }

  def executeMetadataQuery(query: String): List[List[String]] = {

    Class.forName("org.postgresql.Driver")
    var connection: Connection = null
    try {
      val jdbcUrl = "jdbc:postgresql://35.204.44.33/nuo_metadata"

      connection = DriverManager.getConnection(jdbcUrl, metadataUser, metadataPassword)

      val resultSet = connection.createStatement().executeQuery(query)
      val rows = mutable.ArrayBuffer[List[String]]()

      while (resultSet.next) {

        val column_count = resultSet.getMetaData.getColumnCount
        val row = mutable.ArrayBuffer[String]()

        for (j <- 1 to column_count) {
          val columnValue = resultSet.getString(j)
          if (List("bool", "boolean").contains(resultSet.getMetaData.getColumnTypeName(j).toLowerCase)) {

            row += List("t", "true", "1", "Y").contains(columnValue.toLowerCase).toString
          }
          else if (columnValue != null)
            row += columnValue
          else
            row += ""
        }
        rows += row.toList
      }
      rows.toList
    } finally {
      if (connection != null)
        connection.close()
    }
  }
}
