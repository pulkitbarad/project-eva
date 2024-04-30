
/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 29Dec2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

grammar adpLocationCriteria;

fieldText
    returns [String value]
        : LOCATION_INDICATOR locationText
        ;

locationText
        : ENCLOSED_STRING
        | ~(LOCATION_INDICATOR)+?
        ;


// Alphabets for case-insesitive matching
fragment A: [aA];
fragment B: [bB];
fragment C: [cC];
fragment D: [dD];
fragment E: [eE];
fragment F: [fF];
fragment G: [gG];
fragment H: [hH];
fragment I: [iI];
fragment J: [jJ];
fragment K: [kK];
fragment L: [lL];
fragment M: [mM];
fragment N: [nN];
fragment O: [oO];
fragment P: [pP];
fragment Q: [qQ];
fragment R: [rR];
fragment S: [sS];
fragment T: [tT];
fragment U: [uU];
fragment V: [vV];
fragment W: [wW];
fragment X: [xX];
fragment Y: [yY];
fragment Z: [zZ];


LOCATION_INDICATOR
        : KWD_STAY_IN WS
        | KWD_LIVE_IN WS
        | KWD_BASED_IN WS
        | KWD_LOCATED_AT WS
        | KWD_SITUATED_AT WS
        | KWD_HQ_AT WS
        | KWD_FROM WS
        | KWD_OF WS
        | KWD_IN WS
        | KWD_AT WS
        ;

KWD_STAY_IN
        : KWD_STAY WS (KWD_IN | KWD_AT)
        ;

KWD_LIVE_IN
        : KWD_LIVE WS (KWD_IN | KWD_AT)
        ;

KWD_BASED_IN
        : B A S E D WS (O U T WS KWD_OF | KWD_IN)?
        ;

KWD_LOCATED_AT
        : L O C A T E D WS (KWD_IN | KWD_AT)?
        ;

KWD_SITUATED_AT
        : S I T U A T E D  WS (KWD_IN | KWD_AT)?
        ;

KWD_HQ_AT
        : KWD_HEADQUARTERED WS (KWD_IN | KWD_AT)?
        ;


KWD_STAY
        : S T A Y (S | E D | I N G)?
        ;

KWD_LIVE
        : L I V  (E D| E S | E | I N G)
        ;

KWD_HEADQUARTERED
        : H E A D (WS|'-')? Q U A R T E R (E D)?
        ;

KWD_FROM
        : F R O M
        ;
KWD_IN
        : I N
        ;
KWD_AT
        : A T
        ;
KWD_OF
        : O F
        ;


ENCLOSED_STRING
        : WS? ('\'') ~('\'')* ('\'') WS? {setText(getText().replace("'",""));}
        |  WS? ('"') ~('"')* ('"') WS? {setText(getText().replace("\"",""));}
        ;
LETTER : [a-zA-Z_\-];
WS  : [ \t]+;
