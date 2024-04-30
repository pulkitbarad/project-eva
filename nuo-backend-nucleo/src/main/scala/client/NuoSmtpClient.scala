package client

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 11Sep2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

import java.util.{Date, Properties}
import javax.mail._
import javax.mail.internet.{InternetAddress, MimeMessage}

import logging.NuoLogger

object NuoSmtpClient {

  val passwordFromEmailAddress = "noreply@nuocanvas.com"

  def main(args: Array[String]): Unit = {

    sendEmail("pulkit@nuocanvas.com", "NuoCanvas account password", "Your NuoCanvas account password is adfE34%adslkIJD1245tWe")
  }

  /**
    * Utility method to send simple HTML email
    *
    * @param toEmail
    * @param subject
    * @param body
    */
  def sendEmail(toEmail: String, subject: String, body: String) {
    try {

      val props = new Properties()
      props.put("mail.smtp.host", "email-smtp.eu-west-1.amazonaws.com") //SMTP Host
      props.put("mail.smtp.port", "25") //TLS Port
      props.put("mail.smtp.auth", "true") //enable authentication
      props.put("mail.smtp.starttls.enable", "true") //enable STARTTLS

      //create Authenticator object to pass in Session.getInstance argument
      val auth = new Authenticator() {
        override def getPasswordAuthentication: PasswordAuthentication = {
          new PasswordAuthentication("AKIAJ7HG4IBJVN4VYUEA", "AlDuWPnG3vrMkuKby4naJQPEtMsyTf2YKs21GtISs9YA")
        }
      }
      val session = Session.getInstance(props, auth)

      val msg = new MimeMessage(session)
      msg.addHeader("Content-type", "text/HTML charset=UTF-8")
      msg.addHeader("format", "flowed")
      msg.addHeader("Content-Transfer-Encoding", "8bit")

      msg.setFrom(new InternetAddress(passwordFromEmailAddress, "NuoCanvas Security"))
      msg.setSubject(subject, "UTF-8")
      msg.setText(body, "UTF-8")
      msg.setSentDate(new Date())
      msg.setRecipients(Message.RecipientType.TO, toEmail)

      Transport.send(msg)

      NuoLogger.printInfo("Email Sent Successfully")
    }
    catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }
}
