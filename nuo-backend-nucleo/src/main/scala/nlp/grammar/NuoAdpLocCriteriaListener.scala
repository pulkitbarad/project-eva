package nlp.grammar

import nlp.parser._
import nlp.parser.adpLocationCriteriaParser._

/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 04Jan2018
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

class NuoAdpLocCriteriaListener extends adpLocationCriteriaBaseListener {

  override def exitFieldText(ctx: FieldTextContext): Unit = {

    ctx.value = ctx.locationText().getText
    //    ctx.value = ctx.getText
  }

  //  override def exitLocationText(ctx: LocationTextContext) = {
  //
  //    NuoLogger.printInfo("Context Location text"+ctx.getText)
  //  }
}
