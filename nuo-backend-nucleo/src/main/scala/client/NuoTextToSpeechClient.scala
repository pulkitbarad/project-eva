package client

import java.io.{FileOutputStream, InputStream}

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.polly.model._
import com.amazonaws.services.polly.{AmazonPollyClient, model}
import com.amazonaws.services.s3.AmazonS3Client
import com.google.api.services.texttospeech.v1.model.{AudioConfig, SynthesisInput, SynthesizeSpeechRequest, VoiceSelectionParams}
import logging.NuoLogger
import nlp.grammar.NuoEvaEnglishListener

import scala.io.Source
import scala.collection.JavaConverters._

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 13Nov2018
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

object NuoTextToSpeechClient {


  def createCustomAwsPollyClient(accessKey: String,
                                 secretKey: String): AmazonPollyClient = {

    val awsCredentials = new BasicAWSCredentials(accessKey, secretKey)
    new AmazonPollyClient(awsCredentials)
  }


  def convertTextToSpeechPolly(analysisId: String, ssml: String, languageCode: String): String = {
    //    var ssml =
    //      """
    //        |
    //        |<speak>
    //        |<amazon:auto-breaths>
    //        |<prosody pitch="medium">
    //        |
    //        |<s>For MasterCard Flying Blue Gold<break time="250ms"/> Total Amount is 1.64 Billion, which is the <emphasis level="moderate">lowest</emphasis> across the Card Type.</s><amazon:breath/><break time="250ms"/><s>For MasterCard Flying Blue Gold, COUNT Transaction Number is 327.59 Thousand<break time="250ms"/>which is 68.21%<emphasis level="moderate">above</emphasis> the average of 50.74 Thousand</s><amazon:breath/><break time="250ms"/> <s>For Visa Platinum<break time="250ms"/> Total Amount is 2.45 Billion, which is the <emphasis level="moderate">highest</emphasis> across the Card Type.</s><amazon:breath/><break time="250ms"/><s>For Visa Platinum, COUNT Transaction Number is 491.12 Thousand<break time="250ms"/>which is 108.49%<emphasis level="moderate">above</emphasis> the average of 50.74 Thousand</s><amazon:breath/><break time="250ms"/>   <s>For Greece<break time="250ms"/> Total Amount is 332.71 Million, which is the <emphasis level="moderate">lowest</emphasis> across the Country.</s><amazon:breath/><break time="250ms"/><s>For Greece, COUNT Transaction Number is 66.57 Thousand<break time="250ms"/>which is 33.69%<emphasis level="moderate">above</emphasis> the average of 9 Thousand</s><amazon:breath/><break time="250ms"/> <s>For Belgium<break time="250ms"/> Total Amount is 1.33 Billion, which is the <emphasis level="moderate">highest</emphasis> across the Country.</s><amazon:breath/><break time="250ms"/><s>For Belgium, COUNT Transaction Number is 265.39 Thousand<break time="250ms"/>which is 150.02%<emphasis level="moderate">above</emphasis> the average of 9 Thousand</s><amazon:breath/><break time="250ms"/>
    //        |</prosody>
    //        |</amazon:auto-breaths>
    //        |</speak>
    //        |
    //      """.stripMargin
    //    <s>Voor MasterCard  Gold<break time="250ms"/> is Total Amount 1.64 Miljard, wat het <emphasis level="moderate">laagste</emphasis> is over het Card Type</s><amazon:breath/><break time="250ms"/><s>Voor MasterCard  Gold<break time="250ms"/> is COUNT Transaction Number 327.59 Duizend<break time="250ms"/> dat is 68.21%<emphasis level="moderate">boven</emphasis>het gemiddlede van 50.74 Duizend</s><amazon:breath/><break time="250ms"/> <s>Voor Visa Platinum<break time="250ms"/> is Total Amount 2.45 Miljard, wat het <emphasis level="moderate">hoogste</emphasis> is over het Card Type</s><amazon:breath/><break time="250ms"/><s>Voor Visa Platinum<break time="250ms"/> is COUNT Transaction Number 491.12 Duizend<break time="250ms"/> dat is 108.49%<emphasis level="moderate">boven</emphasis>het gemiddlede van 50.74 Duizend</s><amazon:breath/><break time="250ms"/>   <s>Voor Greece<break time="250ms"/> is Total Amount 332.71 Miljoen, wat het <emphasis level="moderate">laagste</emphasis> is over het Country</s><amazon:breath/><break time="250ms"/><s>Voor Greece<break time="250ms"/> is COUNT Transaction Number 66.57 Duizend<break time="250ms"/> dat is 33.69%<emphasis level="moderate">boven</emphasis>het gemiddlede van 9 Duizend</s><amazon:breath/><break time="250ms"/> <s>Voor Belgium<break time="250ms"/> is Total Amount 1.33 Miljard, wat het <emphasis level="moderate">hoogste</emphasis> is over het Country</s><amazon:breath/><break time="250ms"/><s>Voor Belgium<break time="250ms"/> is COUNT Transaction Number 265.39 Duizend<break time="250ms"/> dat is 150.02%<emphasis level="moderate">boven</emphasis>het gemiddlede van 9 Duizend</s><amazon:breath/><break time="250ms"/>

    NuoLogger.printInfo("Inside text to speech function")

    if(NuoEvaEnglishListener.nuoTenantDetails.amazonPollyClient ==null ){
      NuoLogger.printInfo("Polly client is null")

    }else{
      NuoLogger.printInfo("Polly client is not null")

      NuoLogger.printInfo("List of english voices are as following:"
        + NuoEvaEnglishListener.nuoTenantDetails.amazonPollyClient
      )
    }
    NuoLogger.printInfo("Before synthesize request")

    val speechRequest = new model.SynthesizeSpeechRequest()
      .withVoiceId(if (languageCode.equalsIgnoreCase("nl")) VoiceId.Lotte else VoiceId.Joanna)
      .withTextType(TextType.Ssml)
      .withText(ssml)
      .withOutputFormat(OutputFormat.Mp3)
    val speechResponse = NuoEvaEnglishListener.nuoTenantDetails.amazonPollyClient.synthesizeSpeech(speechRequest)
    NuoLogger.printInfo("After synthesize request")
    NuoLogger.printInfo("Synthesize content type:" + speechResponse.getContentType)

    val buffer = Stream.continually(speechResponse.getAudioStream.read).takeWhile(_ != -1).map(_.toByte).toArray

    NuoSecurityClient.bytesToHex(buffer)
    //    val out = new FileOutputStream("./nuo-backend-nucleo/narratives/" + analysisId + ".mp3")
    //    try {
    //      out.write(buffer)
    //    } finally if (out != null) out.close()
    //    "OK"
  }

  def convertTextToSpeech(ssml: String): Unit = {


    val speechRequest =
      new SynthesizeSpeechRequest()
        .setAudioConfig(
          new AudioConfig()
            .setAudioEncoding("MP3")
        )
        .setVoice(
          new VoiceSelectionParams()
            .setLanguageCode("nl-NL")
        )
        .setInput(
          new SynthesisInput()
            .setSsml(ssml)
        )
    val speechResponse =
      NuoEvaEnglishListener
        .nuoTenantDetails
        .textToSpeechClientGcp
        .text()
        .synthesize(speechRequest)
        .execute()

    // Write the response to the output file.
    val out = new FileOutputStream("./output.mp3")
    try {
      out.write(speechResponse.decodeAudioContent())
      System.out.println("Audio content written to file \"output.mp3\"")
    } finally if (out != null) out.close()
  }
}
