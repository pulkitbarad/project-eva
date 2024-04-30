
/**
  * Copyright 2015-2018 NuoCanvas.
  *
  *
  * Created by Pulkit on 05Dec2017
  *
  * Content of this file is proprietary and confidential.
  * It shall not be reused or disclosed without prior consent
  * of distributor
  **/

grammar evaEnglish;
//import v0GlobalLexer;
//@header {
//import java.util.*;
//}
//
//@nlp::members {
//    //Some code to be injected.
//    List<String> fields = new ArrayList<String>();
//}


/*
 *******Parser Grammar**********
*/

question
    returns[String sql,
            List<String> fields,
            List<String> nonAggFields,
            List<String> entities,
            String whereClause,
            String havingClause]
        : /*CMD_LEARN WS fieldContent                                                                                #commandLearn
        | CMD_PREDICT WS fieldContent (KWD_FROM | KWD_OUT_OF | KWD_OF | KWD_IN | KWD_USING | KWD_FOR) fieldContent #commandPredict
        | */selectClause ((POS_DET | POS_WH_WORDS | KWD_WITH | positiveVerb) criteriaClause)? SYM_QUESTION?          #commandSelect
        ;

selectClause
        : POS_WH_WORDS positiveVerb? fieldList
        | CMD_SELECT fieldList
        | fieldList
        ;

fieldList
    returns[List<List<String>> values]
        : (rankField | field ) ((SYM_COMMA | KWD_AND | KWD_BY | KWD_PER) ( rankField | field))*
        ;


criteriaClause
    returns[String defaultValue,
            String sumCaseValue,
            int fieldType]
        : SYM_LPAREN criteriaClause SYM_RPAREN                                                                      #criteriaParen
        | criteriaClause KWD_ALSO? (POS_DET | POS_WH_WORDS | KWD_WITH) KWD_ALSO? criteriaClause                     #criteriaAnd
        | criteriaClause (KWD_AND | KWD_BUT) KWD_ALSO? (POS_DET | POS_WH_WORDS | KWD_WITH) KWD_ALSO? criteriaClause #criteriaAnd
        | criteriaClause (KWD_AND | KWD_BUT) KWD_ALSO? criteriaClause                                               #criteriaAnd
        | criteriaClause KWD_OR (POS_DET | POS_WH_WORDS | KWD_WITH)? criteriaClause                                 #criteriaOr
        | (POS_DET | POS_WH_WORDS | KWD_WITH)? negativeVerb SYM_LPAREN  criteriaClause SYM_RPAREN                   #criteriaNegative
        | (POS_DET | POS_WH_WORDS | KWD_WITH) positiveVerb? criteriaClause                                          #criteriaPositive
        | KWD_ALSO? positiveVerb criteriaClause                                                                     #criteriaPositive
//        | whereClauseComparisionContent                                                                             #criteriaWhereComparision
        | (KWD_WITHOUT | negativeVerb) whereClauseOrphanContent                                                     #criteriaWhereOrphanNegative
//        | (KWD_WITHOUT | negativeVerb) whereClauseContent                                                           #criteriaWhereNegative
        | whereClauseOrphanContent                                                                                  #criteriaWhereOrphanDefault
        | whereClauseContent                                                                                        #criteriaWhereDefault
        ;
//
//
//whereClauseComparisionContent
//    returns[String value,
//            int fieldType,
//            List<List<String>> nuoFields]
//        : (KWD_WITHOUT | negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? ((negativeVerb (KWD_LT | KWD_LT_TIME)) | positiveVerb? (KWD_GT_EQ | KWD_GT_EQ_TIME)) field (comparisionPhrases field)+                                                   #whereComparisionGtEq
//        | (KWD_WITHOUT | negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? ((negativeVerb (KWD_GT | KWD_GT_TIME)) | positiveVerb? (KWD_LT_EQ | KWD_UNTIL | KWD_TILL)) field (comparisionPhrases field)+                                             #whereComparisionLtEq
//        | (KWD_WITHOUT | negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? ((negativeVerb (KWD_LT_EQ | KWD_UNTIL | KWD_TILL)) | positiveVerb? (KWD_GT | KWD_GT_TIME)) field (comparisionPhrases field)+                                             #whereComparisionGt
//        | (KWD_WITHOUT | negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? ((negativeVerb (KWD_GT_EQ | KWD_GT_EQ_TIME)) | positiveVerb? (KWD_LT | KWD_LT_TIME)) field (comparisionPhrases field)+                                                   #whereComparisionLt
//        | (KWD_WITHOUT | negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? (negativeVerb (KWD_FROM | KWD_IN | KWD_DURING)? | positiveVerb? (KWD_FROM | KWD_IN | KWD_DURING) | positiveVerb)  durationField (comparisionPhrases durationField)+      #whereComparisionDuration
//        | (KWD_WITHOUT | negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? (negativeVerb (KWD_DATE_EQUAL_TO | KWD_FROM | KWD_IN)? | positiveVerb? (KWD_DATE_EQUAL_TO | KWD_FROM | KWD_IN) | positiveVerb) dateField (comparisionPhrases dateField)+ #whereComparisionDateEq
//        | (KWD_WITHOUT | negativeVerb)? field (POS_DET | POS_WH_WORDS)? (positiveVerb? | negativeVerb?) (KWD_STARTS_WITH | KWD_ENDS_WITH | KWD_CONTAINS) field (comparisionPhrases field)+                                                                  #whereComparisionStringFunctions
//        | (KWD_WITHOUT | negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? (negativeVerb (KWD_EQUAL_TO | KWD_IN)? | positiveVerb? (KWD_EQUAL_TO | KWD_IN) | positiveVerb) field (comparisionPhrases field)+                                         #whereComparisionEq
//        ;

whereClauseOrphanContent
    returns[String value,
            int fieldType]
        : (KWD_BETWEEN | KWD_OUTSIDE)  numberField KWD_AND numberField (KWD_IN| KWD_OF)? field   #whereOrphanBetween
        | ((negativeVerb KWD_LT_EQ) | positiveVerb? KWD_GT) numberField (KWD_IN| KWD_OF)? field  #whereOrphanGt
        | ((negativeVerb KWD_GT_EQ) | positiveVerb? KWD_LT)  numberField (KWD_IN| KWD_OF)? field #whereOrphanLt
        | ((negativeVerb KWD_LT) | positiveVerb? KWD_GT_EQ) numberField (KWD_IN| KWD_OF)? field  #whereOrphanGtEq
        | ((negativeVerb KWD_GT) | positiveVerb? KWD_LT_EQ)  numberField (KWD_IN| KWD_OF)? field #whereOrphanLtEq
        | negativeVerb? KWD_EQUAL_TO? numberField (KWD_IN| KWD_OF)? field                        #whereOrphanEq
        | negativeVerb? KWD_EQUAL_TO? field KWD_OF numberField                                   #whereOrphanEq
        ;

//Example for standard date time formats are derived from https://docs.microsoft.com/en-us/dotnet/standard/base-types/standard-date-and-time-format-strings
dateField
    returns[String fieldText,
            int fieldType,
            String fieldValue,
            String datatype,
            long timeInMillis,
            int dd,
            int mm,
            int yyyy,
            int hh,
            int MM,
            int ss,
            int SSS,
            String tz]
        : KWD_DAY_OF_WEEK? (SYM_COMMA|SYM_PERIOD)? date time #dateFieldDateTime
        | KWD_DAY_OF_WEEK? (SYM_COMMA|SYM_PERIOD)? time date #dateFieldDateTime
        | KWD_DAY_OF_WEEK? (SYM_COMMA|SYM_PERIOD)? date      #dateFieldDateOnly
        | time                                               #dateFieldTimeOnly
        ;

whereClauseContent
    returns[String value,
            int fieldType,
            List<List<String>> nuoFields]
        : (KWD_WITHOUT | prefixNegVerb=negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? positiveVerb? (KWD_BETWEEN | KWD_OUTSIDE)  field KWD_AND field                                                                                                                             #whereBetween
        | (KWD_WITHOUT | prefixNegVerb=negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? positiveVerb? (KWD_GT_TIME | KWD_GT_EQ_TIME) field (KWD_AND | KWD_BUT)? (KWD_LT_TIME | KWD_UNTIL | KWD_TILL) field                                                                         #whereBetween
        | (KWD_WITHOUT | prefixNegVerb=negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? ((negativeVerb (KWD_LT | KWD_LT_TIME)) | positiveVerb? (KWD_GT_EQ | KWD_GT_EQ_TIME)) field (KWD_OR (criteriaClause | field))*                                                              #whereGtEq
        | (KWD_WITHOUT | prefixNegVerb=negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? ((negativeVerb (KWD_GT | KWD_GT_TIME)) | positiveVerb? (KWD_LT_EQ | KWD_UNTIL | KWD_TILL)) field (KWD_OR (criteriaClause | field))*                                                        #whereLtEq
        | (KWD_WITHOUT | prefixNegVerb=negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? ((negativeVerb (KWD_LT_EQ | KWD_UNTIL | KWD_TILL)) | positiveVerb? (KWD_GT | KWD_GT_TIME)) field (KWD_OR (criteriaClause | field))*                                                        #whereGt
        | (KWD_WITHOUT | prefixNegVerb=negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? ((negativeVerb (KWD_GT_EQ | KWD_GT_EQ_TIME)) | positiveVerb? (KWD_LT | KWD_LT_TIME)) field (KWD_OR (criteriaClause | field))*                                                              #whereLt
        | (KWD_WITHOUT | prefixNegVerb=negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? positiveVerb? (KWD_FROM | KWD_IN | KWD_DURING)? durationField                                                                                                                              #whereDuration
        | (KWD_WITHOUT | prefixNegVerb=negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? (subNegVerb=negativeVerb)? (KWD_DATE_EQUAL_TO | KWD_FROM | KWD_IN) dateField (KWD_OR (criteriaClause | field))*                                                                            #whereDateEq
        | (KWD_WITHOUT | prefixNegVerb=negativeVerb)? field (POS_DET | POS_WH_WORDS)? (positiveVerb? | (subNegVerb=negativeVerb)?) (KWD_STARTS_WITH | KWD_ENDS_WITH | KWD_CONTAINS) field ((SYM_COMMA | KWD_OR) (criteriaClause | field))*                                                  #whereStringFunctions
        | KWD_WITH? fieldContent (positiveVerb? KWD_AVAILABLE | negativeVerb unavailablePhrases)                                                                                                                                                                                            #whereDefault
        | ((KWD_WITH negativeVerb | KWD_WITHOUT KWD_ANY?) fieldContent | fieldContent (positiveVerb unavailablePhrases | negativeVerb KWD_AVAILABLE))                                                                                                                                       #whereDefaultNegative
        | (KWD_WITHOUT | prefixNegVerb=negativeVerb)? field (POS_DET | POS_WH_WORDS | KWD_WITH)? (subNegVerb=negativeVerb (KWD_EQUAL_TO | KWD_IN)? | positiveVerb (KWD_EQUAL_TO | KWD_IN) | positiveVerb | (KWD_EQUAL_TO | KWD_IN) ) field ((SYM_COMMA | KWD_OR) (criteriaClause | field))* #whereEq
//        | fieldContent                                                                                                                                                              #whereDefault
        ;

negativeVerb
        : KWD_NO
        | POS_VERB_HAVE KWD_NO
        | (POS_VERB_NEG | POS_VERB_HAVE_NEG) POS_VERB_BE? POS_VERB_HAVE?
        ;

positiveVerb
        : (POS_VERB | POS_VERB_PREDICT | POS_VERB_HAVE) POS_VERB_BE? POS_VERB_HAVE?
        ;

comparisionPhrases
        : KWD_VS
        | (KWD_COMPARING | KWD_COMPARED) (KWD_TO | KWD_WITH)?
        ;

unavailablePhrases
        : KWD_UNAVAILABLE
        | KWD_EMPTY
        | KWD_NULL
        | KWD_BLANK
        ;

duration
    returns[String numOfYears,
            String numOfMonths,
            String numOfDays,
            String numOfHours,
            String numOfMinutes,
            String numOfSeconds,
            int  durationSeconds]
        : duration (KWD_AND? duration)+
        | years=NUMBER? KWD_YEAR
        | months=NUMBER? KWD_MONTH
        | weeks=NUMBER? KWD_WEEK
        | days=NUMBER? KWD_DAY
        | hours=NUMBER? KWD_HOUR
        | minutes=NUMBER? KWD_MINUTE
        | seconds=NUMBER? KWD_SECOND
        ;

//Example for standard date time formats are derived from https://docs.microsoft.com/en-us/dotnet/standard/base-types/standard-date-and-time-format-strings
durationField
    returns[String value,
            int fieldType,
            long timeInMillis,
            int durationSeconds]
        : (KWD_LAST | KWD_PAST) duration #durationFieldPast
        | duration KWD_AGO               #durationFieldPast
        | KWD_NEXT duration              #durationFieldNext
        | duration KWD_FROM WS? KWD_NOW  #durationFieldNext
        ;

date
    returns[int yyyy,
            int mm,
            int dd]
        : KWD_TODAY                                                                           #dateAdvDay
        | KWD_TOMORROW                                                                        #dateAdvDay
        | KWD_YESTERDAY                                                                       #dateAdvDay
        | KWD_YEAR year=NUMBER                                                                #dateAdjNum
        | KWD_MONTH_OF_YEAR dayOfMonth? (SYM_COMMA? year=NUMBER)?                             #dateAdjNum  //Jan 31st, 2015 | January 10, 15
        | dayOfMonth? KWD_OF? KWD_MONTH_OF_YEAR                                               #dateAdjNum  //12 of Jan | 56 February
        | (year=NUMBER SYM_COMMA?)?  dayOfMonth? KWD_OF? KWD_MONTH_OF_YEAR                    #dateAdjNum  //2015, 12th of Jan | 80, 2 February
        | dayOfMonth KWD_OF? KWD_MONTH_OF_YEAR SYM_COMMA? (year=NUMBER)                       #dateDefault //3rd of April, 2015 | 10 Feb 1950
        | (year=NUMBER) SYM_COMMA? KWD_MONTH_OF_YEAR dayOfMonth                               #dateDefault //2015, January 12 | 1980. Feb 2nd | 2012 September 22nd
        | year=NUMBER SYM_DASH (monthOfYear = NUMBER | KWD_MONTH_OF_YEAR) SYM_DASH dayOfMonth #dateDefault // 1995-06-12 | 1991-Apr-26
        ;

dayOfMonth
    returns[String value]
        : NUMBER WS? (KWD_ST | KWD_ND | KWD_RD | KWD_TH)? WS?
        ;

time
    returns[int hh,
            int MM,
            int ss,
            int SSS,
            String tz]
        : NUMBER WS? (KWD_AM | KWD_PM) WS?                 #timeAmPmHour
        | time WS? (KWD_AM | KWD_PM) WS?                   #timeAmPmDefault
        | time WS? (timeZone | KWD_Z) WS?                  #timeTz
        | WS? NUMBER SYM_COLON NUMBER SYM_COLON NUMBER WS? #timeComplete
        | WS? NUMBER SYM_COLON NUMBER WS?                  #timePartial
        ;

timeZone
    returns[String value]
        : (KWD_GMT | KWD_UTC)? (SYM_DASH|SYM_PLUS) NUMBER ((SYM_COLON | SYM_PERIOD) NUMBER)?
        ;

aggregateField
    returns[String fieldText,
            int fieldType,
            String fieldValue,
            String datatype,
            String fieldAlias,
            boolean isAggregated,
            List<String> entityNames,
            int dd,
            int mm,
            int yyyy,
            int hh,
            int MM,
            int ss,
            int SSS,
            String tz]
        : KWD_AGG_SUM  WS? KWD_ALL? WS? fieldContent                                                                   #aggFieldSum
        | (KWD_AGG_MAX | KWD_HIGHEST) WS? KWD_POSSIBLE? WS? (KWD_OF | KWD_FOR | KWD_IN)? WS? KWD_ALL? WS? fieldContent #aggFieldMax
        | (KWD_AGG_MIN | KWD_LOWEST) WS? KWD_POSSIBLE? WS? (KWD_OF | KWD_FOR | KWD_IN)? WS? KWD_ALL? WS? fieldContent  #aggFieldMin
        | KWD_AGG_AVG WS? (KWD_OF | KWD_FOR | KWD_IN)? WS? KWD_ALL? WS? fieldContent                                   #aggFieldAvg
        | KWD_AGG_COUNT KWD_ALL? WS? fieldContent                                                                      #aggFieldCount
        | fieldContent (KWD_AGG_SUM | KWD_SUM)                                                                         #aggFieldSum
        | fieldContent KWD_AGG_MAX                                                                                     #aggFieldMax
        | fieldContent KWD_AGG_MIN                                                                                     #aggFieldMin
        | fieldContent KWD_AGG_AVG                                                                                     #aggFieldAvg
        | fieldContent KWD_AGG_COUNT                                                                                   #aggFieldCount
        ;

aggregateNumber
    returns[String value,
            String aggFunction]
        : KWD_AGG_SUM  WS? KWD_ALL? WS? numberField                                                                    #aggNumberSum
        | (KWD_AGG_MAX | KWD_HIGHEST) WS? KWD_POSSIBLE? WS? (KWD_OF | KWD_FOR | KWD_IN)? WS? KWD_ALL? WS?  numberField #aggNumberMax
        | (KWD_AGG_MIN | KWD_LOWEST) WS? KWD_POSSIBLE? WS? (KWD_OF | KWD_FOR | KWD_IN)? WS? KWD_ALL? WS?  numberField  #aggNumberMin
        | KWD_AGG_AVG WS? (KWD_OF | KWD_FOR | KWD_IN)? WS? KWD_ALL? WS?  numberField                                   #aggNumberAvg
        | KWD_AGG_COUNT KWD_ALL? WS?  numberField                                                                      #aggNumberCount
        ;

numberField
    returns[String value,
            String aggFunction]
        : aggregateNumber #numberAgg
        | KWD_NUMBER_T    #numberT
        | KWD_NUMBER_B    #numberB
        | KWD_NUMBER_M    #numberM
        | KWD_NUMBER_K    #numberK
        | NUMBER          #numberDefault
        ;

rankField
    returns[String fieldText,
            int fieldType,
            String fieldValue,
            String fieldAlias,
            String datatype,
            boolean isAggregated,
            List<String> entityNames]
        : (KWD_FIRST | KWD_TOP) (numberField fieldContent)? overClause                                            #rankFieldFirst
        | (KWD_LAST | KWD_BOTTOM) (numberField fieldContent)? overClause                                          #rankFieldLast
        | numberField (KWD_ST | KWD_ND | KWD_RD | KWD_TH) KWD_LAST fieldContent overClause                        #rankFieldNthInverse
        | numberField (KWD_ST | KWD_ND | KWD_RD | KWD_TH) KWD_LOWEST? fieldContent KWD_FROM KWD_BOTTOM overClause #rankFieldNthInverse
        | numberField (KWD_ST | KWD_ND | KWD_RD | KWD_TH) KWD_HIGHEST? fieldContent overClause                    #rankFieldNth
        | (KWD_LEADING | KWD_PRECEDING) fieldContent overClause                                                   #rankFieldLeading
        | (KWD_FOLLOWING | KWD_LAGGING) fieldContent overClause                                                   #rankFieldFollowing
        | KWD_RANK KWD_OF fieldContent overClause                                                                 #rankFieldDefault
        | fieldContent KWD_RANK overClause                                                                        #rankFieldDefault
        ;

overClause
    returns[List<String>  partitionFields,
            List<String>  orderFields]
        : KWD_ORDER_CLAUSE POS_POSESV_PRN? field ((KWD_PARTITION_CLAUSE | KWD_BY | KWD_PER) POS_POSESV_PRN? fieldContent)? #overClauseOrdPart
        | (KWD_PARTITION_CLAUSE | KWD_BY | KWD_PER) POS_POSESV_PRN? fieldContent KWD_ORDER_CLAUSE POS_POSESV_PRN? field    #overClausePartOrd
        ;

functionTwoArgs
    returns[String value,
            String targetDatatype]
        : FUNC_CORRELATION
        ;

functionOneArg
    returns[String value,
            boolean isAggregated,
            String targetDatatype]
        : FUNC_STD_DEV
        | FUNC_VARIANCE
        | FUNC_ABSOLUTE
        | FUNC_SIGN
        | FUNC_SQRT
        | FUNC_EXPONENTIAL
        | FUNC_LOG
        | FUNC_ROUND
        | FUNC_COS
        | FUNC_SIN
        | FUNC_TAN
        | FUNC_MD5
        | FUNC_SHA1
        | FUNC_SHA256
        | FUNC_SHA512
        ;

field
    returns[String fieldText,
            int fieldType,
            String fieldValue,
            String datatype,
            String fieldAlias,
            boolean isAggregated,
            List<String> entityNames,
            int dd,
            int mm,
            int yyyy,
            int hh,
            int MM,
            int ss,
            int SSS,
            String tz]
        : SYM_LPAREN field SYM_RPAREN                                                                            #fieldParen
        | dateField                                                                                              #fieldDate
        | duration (SYM_PLUS | SYM_DASH) field                                                                   #fieldOperationDuration
        | field SYM_PLUS (duration | field)                                                                      #fieldOperationPlus
        | field SYM_DASH (duration | field)                                                                      #fieldOperationMinus
        | field (SYM_F_SLASH | SYM_STAR | SYM_PERCENT | SYM_CARET) field                                         #fieldOperationOther
        | article? functionOneArg KWD_OF? field                                                                  #fieldOneArgFunction
        | field functionOneArg                                                                                   #fieldOneArgFunction
        | article? (FUNC_SUBSTRING KWD_OF? | KWD_PART KWD_OF) field KWD_FROM field ((KWD_TO | KWD_UNTIL) field)? #fieldSubstrFunction
        | article? (FUNC_SUBSTRING KWD_OF? | KWD_PART KWD_OF) field (KWD_BETWEEN) field (KWD_AND field)?         #fieldSubstrFunction
        | article? KWD_DIVISION KWD_OF field (KWD_BY | KWD_AND) field                                            #fieldDivFunction
        | article? functionTwoArgs KWD_OF? (KWD_BETWEEN | KWD_FOR | KWD_USING) field KWD_AND field               #fieldTwoArgFunction
        | numberField                                                                                            #fieldNumber
        | durationField                                                                                          #fieldDuration
//        | rankField                                                                                              #fieldRank
        | (POS_POSESV_PRN | article)? aggregateField                                                             #fieldAgg
        | (POS_POSESV_PRN)? fieldContent                                                                         #fieldDefault
        | article field                                                                                          #fieldArticle
        ;

fieldContent
    returns[String fieldText,
            int fieldType,
            String fieldValue,
            String fieldAlias,
            String datatype,
            String columnName,
            String tableName]
        : BOXED_STRING BOXED_STRING BOXED_STRING #fieldContentQualified
        | QUOTED_STRING                          #fieldContentQuoted
        | WS? ~(KWD_AND | KWD_OR| KWD_BUT)+? WS? #fieldContentDefault
        ;

article
        : ARTICLE_A
        | ARTICLE_AN
        | ARTICLE_THE
        ;

//convertOF
//    returns[String value]
//        : fieldContent KWD_OF fieldContent
//        ;

/*
 *******Lexer Grammar**********
*/

/*
  ** Reference to abbreviataion for Part of Speech tags.
  ** ADJ = 'Adjective';
  ** ADP = 'Adposition (preposition and postposition)';
  ** ADV = 'Adverb';
  ** AFFIX = 'Affix';
  ** CONJ = 'Conjunction';
  ** DET = 'Determiner';
  ** NOUN = 'Noun (common and proper)';
  ** NUM = 'Cardinal number';
  ** PRON = 'Pronoun';
  ** PRT = 'Particle or other function word';
  ** PUNCT = 'Punctuation';
  ** UNKNOWN = 'Unknown';
  ** VERB = 'Verb (all tenses and modes)';
  ** X = 'Other (foreign words, typos, abbreviations)';
*/


//Commands
CMD_SELECT
        : KWD_FIND (WS M E)?
        | KWD_GET (WS M E)?
        | KWD_SELECT
        | KWD_SHOW (WS M E)?
        ;

CMD_LEARN
        : KWD_LEARN
        | KWD_TRAIN
        ;

CMD_PREDICT
        : KWD_PREDICT
        | KWD_ESTIMATE
        | KWD_FORECAST
        | KWD_RECOMMEND
        | KWD_HOW_MUCH
        | KWD_HOW_MANY
        ;

//Function Names

FUNC_STD_DEV      : KWD_STD_DEV;

FUNC_VARIANCE     : KWD_VARIANCE;

FUNC_ABSOLUTE     : KWD_ABSOLUTE;

FUNC_SIGN         : KWD_SIGN;

FUNC_SQRT         : KWD_SQRT;

FUNC_EXPONENTIAL  : KWD_EXPONENTIAL;

FUNC_LOG          : KWD_LOG;

FUNC_ROUND        : KWD_ROUND;

FUNC_COS          : KWD_COS;

FUNC_SIN          : KWD_SIN;

FUNC_TAN          : KWD_TAN;

FUNC_MD5          : KWD_MD5;

FUNC_SHA1         : KWD_SHA1;

FUNC_SHA256       : KWD_SHA256;

FUNC_SHA512       : KWD_SHA512;

FUNC_SUBSTRING    : KWD_SUB WS? KWD_STRING;

FUNC_LEFT_TRIM    : (KWD_LEFT | L) WS? KWD_TRIM;

FUNC_RIGHT_TRIM   : (KWD_RIGHT | R) WS? KWD_TRIM;

FUNC_TRIM         : KWD_TRIM;

FUNC_INDEX_OF     : (KWD_INDEX | KWD_POSITION | KWD_LOCATION) WS? KWD_OF;

FUNC_LENGHT       : KWD_LENGTH;

FUNC_CORRELATION  : KWD_CORRELATION
                  | KWD_CORR
                  ;


KWD_LEFT          : WS? L E F T WS?;

KWD_RIGHT         : WS? R I G H T WS?;

KWD_TRIM          : WS? T R I M WS?;

KWD_INDEX         : WS? I N D E X WS?;

KWD_POSITION      : WS? P O S I T I O N WS?;

KWD_LOCATION      : WS? L O C A T I O N WS?;

KWD_LENGTH        : WS? L E N G T H WS?;

KWD_CORRELATION   : WS? C O R R E L A T I O N  WS?;

KWD_CORR          : WS? C O R R WS?;


//Part of speech tags
POS_DET
        : KWD_FOR KWD_THOSE (KWD_WHO | KWD_WHICH | KWD_THAT)
//        | KWD_OF_WHICH
        | (KWD_FOR| KWD_OF) POS_WH_WORDS
        | (KWD_FOR| KWD_OF)? KWD_WHOM
        | KWD_THAT
//        | KWD_WHO
//        | KWD_WHICH
//        | KWD_WITH
        | KWD_WHOSE
        | KWD_WHERE
        | KWD_WHEN
        ;

POS_WH_WORDS
        : KWD_WHO
        | KWD_WHICH
        | KWD_WHAT
//        | KWD_WHERE
//        | KWD_WHY
//        | KWD_HOW_MUCH
//        | KWD_HOW_MANY
        ;

POS_VERB_NEG
        : ((POS_VERB | POS_VERB_PREDICT) POS_ADV_NEG)
        | KWD_WONT
        | KWD_SHANT
        ;

POS_VERB
        : KWD_CAN
        | KWD_SHALL
        | KWD_SHOULD
        | KWD_DO
        | KWD_DOES
        | KWD_IS
        | KWD_ARE
        | KWD_DID
        | KWD_WAS
        | KWD_WERE
        | KWD_HAS
        ;

POS_VERB_PREDICT
        : KWD_COULD
        | KWD_MIGHT
//        | KWD_SHOULD
        | KWD_WOULD
        | KWD_WILL
        ;

POS_VERB_HAVE_NEG
        : (POS_VERB_HAVE) WS? POS_ADV_NEG
        ;

POS_VERB_HAVE
        : KWD_HAVE
        | KWD_HAD
        ;

POS_VERB_BE
        : KWD_BEEN
        | KWD_BE
        ;

POS_ADV_NEG
        : KWD_NOT
        | KWD_NT
        ;

POS_POSESV_PRN
        : KWD_HIS
        | KWD_HER
        | KWD_THEIR
        | KWD_ITS
        ;

//Kewywords used in order by clause of ranking functions

KWD_ORDER_CLAUSE
        : KWD_BASE WS KWD_ON
        | KWD_ACCORDING WS KWD_TO
        | (KWD_ORDER | KWD_SORT | KWD_ARRANGE) (KWD_USING | KWD_WITH | KWD_BY | KWD_ON)
        ;

KWD_PARTITION_CLAUSE
        : (KWD_PARTITION | KWD_GROUP) KWD_BY
        | KWD_FOR (KWD_EACH | KWD_EVERY)
        | KWD_WITHIN (KWD_EACH | KWD_EVERY)?
        | (KWD_FROM | KWD_IN) (KWD_EACH | KWD_EVERY)
        ;

//Articles used in english grammar
ARTICLE_A
        : WS? A WS?
        | WS? A WS
        ;

ARTICLE_AN
        : WS A N WS?
        | WS? A N WS
        ;
ARTICLE_THE
        : WS T H E WS?
        | WS? T H E WS
        ;

//Keywords used in english grammar
KWD_HIS       : WS? H I S WS?;
KWD_HER       : WS? H E R WS?;
KWD_THEIR     : WS? T H E I R WS?;
KWD_ITS       : WS? I T S WS?;



//Keywords for number
// Reference to SI unit list http://www.ebyte.it/library/educards/siunits/TablesOfSiUnitsAndPrefixes.html
KWD_NUMBER_T  : NUMBER WS? (KWD_TRILLION S? WS? | WS? T N WS | T WS);
KWD_NUMBER_B  : NUMBER WS? (KWD_BILLION S? WS? | WS? B N WS | B WS);
KWD_NUMBER_M  : NUMBER WS? (KWD_MILLION S? WS? | WS? M N WS | M WS);
KWD_NUMBER_K  : NUMBER WS? (KWD_THOUSAND S? WS? | KWD_GRAND S? WS | K WS | G WS);



//Keyword used in aggregated

KWD_AGG_SUM
        : KWD_GRAND WS? (KWD_SUM | KWD_SUMMATION) WS? (KWD_OF | KWD_FOR | KWD_IN)
        | KWD_TOTAL WS? (KWD_SUM | KWD_SUMMATION) WS? (KWD_OF | KWD_FOR | KWD_IN)
        | KWD_GRAND WS? (KWD_OF | KWD_FOR | KWD_IN)?
        | KWD_TOTAL WS? (KWD_OF | KWD_FOR | KWD_IN)?
        | KWD_SUMMATION WS? (KWD_OF | KWD_FOR | KWD_IN)
        | KWD_SUM WS? (KWD_OF | KWD_FOR | KWD_IN)
        | KWD_AMOUNT WS? (KWD_OF | KWD_FOR | KWD_IN)
        | KWD_OVERALL WS? (KWD_OF | KWD_FOR | KWD_IN)
        | KWD_GROSS
        | KWD_NET
        ;

KWD_AGG_MAX
        : KWD_TOP WS? KWD_MOST
        | KWD_MAX
        | KWD_MAXIMUM
        | KWD_BIGGEST
//        | KWD_HIGHEST
        | KWD_LARGEST
        | KWD_GREATEST
        | KWD_MAXIMAL
        | KWD_EXTREME
        | KWD_MOST
//        | KWD_TOP
        | KWD_UTMOST
        | KWD_LATEST
        ;


KWD_AGG_MIN
        : KWD_MIN
        | KWD_MINIMUM
        | KWD_LEAST
        | KWD_SMALLEST
//        | KWD_LOWEST
        | KWD_LITTLEST
        | KWD_SLIGHTEST
//        | KWD_BOTTOM
        | KWD_EARLIEST
        ;

KWD_AGG_AVG
        : KWD_AVG
        | KWD_AVERAGE
        | KWD_USUAL
        | KWD_MEAN
        ;

KWD_AGG_COUNT
        : KWD_TOTAL WS? KWD_NUMBER WS? (KWD_OF | KWD_FOR | KWD_IN)
        | KWD_NUMBER WS? (KWD_OF | KWD_FOR | KWD_IN)
        | KWD_COUNT WS? (KWD_OF | KWD_FOR | KWD_IN)
        | KWD_QUANTITY WS? (KWD_OF | KWD_FOR | KWD_IN)
        ;

//Keywords as conjuction in comparision

KWD_GT_EQ
        : KWD_EQUAL_TO (KWD_AND | KWD_OR)? KWD_GT
        | KWD_GT (KWD_AND | KWD_OR)? KWD_EQUAL_TO
        ;

KWD_LT_EQ
        : KWD_EQUAL_TO (KWD_AND | KWD_OR)? KWD_LT
        | KWD_LT (KWD_AND | KWD_OR)? KWD_EQUAL_TO
        ;

KWD_DATE_EQUAL_TO
        : KWD_ON
        | KWD_AT
        ;

KWD_EQUAL_TO
        : KWD_EQUALS WS? KWD_TO?
        | KWD_EQUAL WS? KWD_TO?
        | KWD_AS WS? KWD_SAME WS? KWD_AS
        | KWD_SAME WS? KWD_AS
        | KWD_AS WS? (KWD_MANY | KWD_MUCH | KWD_HIGH | KWD_LOW | KWD_LITTLE | KWD_FEW) WS? KWD_AS
        | KWD_AS
        | KWD_EQUALS
        | KWD_EQUALED
        | KWD_EQUALLED
        | KWD_EQUALING
        | KWD_EQUALLING
        | KWD_EQUAL
        | SYM_EQUAL
        ;

KWD_LT
        : WS? KWD_LESS (E R)? WS? KWD_THAN
        | WS? KWD_SMALLER WS? KWD_THAN
        | WS? KWD_SHORTER WS? KWD_THAN
        | WS? KWD_LOWER WS? KWD_THAN
        | WS? KWD_BELOW WS?
        | WS? KWD_UPTO WS?
        | KWD_EXCEED WS? KWD_BY WS?
        | SYM_LANGB
        ;

KWD_LT_TIME
        : WS? KWD_BEFORE WS?
        | WS? KWD_EARLIER WS? KWD_THAN
        | WS? KWD_SOONER WS? KWD_THAN
        ;

KWD_GT
        : WS? KWD_MORE WS? KWD_THAN
        | WS? KWD_LARGER WS? KWD_THAN
        | WS? KWD_BIGGER WS? KWD_THAN
        | WS? KWD_GREATER WS? KWD_THAN
        | WS? KWD_LONGER WS? KWD_THAN
        | WS? KWD_HIGHER WS? KWD_THAN
        | WS? KWD_ABOVE WS?
        | WS? KWD_BEYOND WS?
        | KWD_EXCEED
        | SYM_RANGB
        ;

KWD_GT_TIME
        : WS? KWD_AFTER WS?
        | WS? KWD_LATER WS? KWD_THAN
        ;

KWD_GT_EQ_TIME
        : WS? KWD_SINCE WS?
        ;

// Calander Months
KWD_MONTH_OF_YEAR
        : KWD_JAN
        | KWD_FEB
        | KWD_MAR
        | KWD_APR
        | KWD_MAY
        | KWD_JUN
        | KWD_JUL
        | KWD_AUG
        | KWD_SEP
        | KWD_OCT
        | KWD_NOV
        | KWD_DEC
        ;

//Week Days
KWD_DAY_OF_WEEK
        : KWD_MON
        | KWD_TUE
        | KWD_WED
        | KWD_THU
        | KWD_FRI
        | KWD_SAT
        | KWD_SUN
        ;

/*
* Keywords definition
*/

//Symbol characters
SYM_STAR        : WS? '*' WS?;
SYM_F_SLASH     : WS? '/' WS?;
SYM_B_SLASH     : WS? '\\' WS?;
SYM_PLUS        : WS? '+' WS?;
SYM_DASH        : WS? '-' WS?;
SYM_EQUAL       : WS? '=' WS?;
SYM_PERIOD      : WS? '.' WS?;
SYM_COMMA       : WS? ',' WS?;
SYM_LPAREN      : WS? '(' WS?;
SYM_RPAREN      : WS? ')' WS?;
SYM_LBRCS       : WS? '{' WS?;
SYM_RBRCS       : WS? '}' WS?;
SYM_LANGB       : WS? '<' WS?;
SYM_RANGB       : WS? '>' WS?;
SYM_LBRCT       : WS? '[' WS?;
SYM_RBRCT       : WS? ']' WS?;
SYM_COLON       : WS? ':' WS?;
SYM_SEMI_COLON  : WS? ';' WS?;
SYM_PERCENT     : WS? '%' WS?;
SYM_CARET       : WS? '^' WS?;
SYM_QUESTION    : WS? '?' WS?;

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

//Commands
KWD_FIND         : WS? F I N D WS?;

KWD_GET          : WS? G E T WS?;

KWD_SELECT       : WS? S E L E C T WS?;

KWD_SHOW         : WS? S H O W WS?;

KWD_LEARN        : WS? L E A R N WS?;

KWD_TRAIN        : WS? T R A I N WS?;

KWD_PREDICT      : WS? P R E D I C T WS?;

KWD_ESTIMATE     : WS? E S T I M A T E WS?;

KWD_FORECAST     : WS? F O R E C A S T WS?;

KWD_RECOMMEND    : WS? R E C O M M E N D WS?;

/*
  ** Reference to abbreviataion for Part of Speech tags.
  ** ADJ = 'Adjective';
  ** ADP = 'Adposition (preposition and postposition)';
  ** ADV = 'Adverb';
  ** AFFIX = 'Affix';
  ** CONJ = 'Conjunction';
  ** DET = 'Determiner';
  ** NOUN = 'Noun (common and proper)';
  ** NUM = 'Cardinal number';
  ** PRON = 'Pronoun';
  ** PRT = 'Particle or other function word';
  ** PUNCT = 'Punctuation';
  ** UNKNOWN = 'Unknown';
  ** VERB = 'Verb (all tenses and modes)';
  ** X = 'Other (foreign words, typos, abbreviations)';
*/

//Keywords for number

KWD_TRILLION     : WS? T R I L L I O N S? WS?;

KWD_BILLION      : WS? B I L L I O N S? WS?;

KWD_MILLION      : WS? M I L L I O N S? WS?;

KWD_THOUSAND     : WS? T H O U S A N D WS?;

KWD_GRAND        : WS? G R A N D WS?;

//Keyword to ignore

KWD_ABSOLUTELY   : WS? A B S O L U T E L Y WS?;

KWD_DEFINITELY   : WS? D E F I N I T E L Y WS?;

KWD_SURELY       : WS? S U R E L Y WS?;

KWD_DETAILS      : WS? D E T A I L S? WS?;


//Keywords used in Function Names
KWD_DIVISION     : WS? D I V (I S I O N)? WS?;

KWD_DIVIDED      : WS? D I V I D (E D | I N G)? WS?;

KWD_STD_DEV      : WS? (S T A N D A R D | S T D) WS? D E V (I A T I O N)? WS? ;

KWD_VARIANCE     : WS? V A R (I A N C E)? WS?;

KWD_ABSOLUTE     : WS? A B S (O L U T E )?WS?;

KWD_SIGN         : WS? S I G N WS?;

KWD_SQRT         : WS? S Q U A R E WS R O O T WS?
                 | WS? S Q R T WS?
                 ;

KWD_EXPONENTIAL  : WS? E X P (O N E N T I A L)? WS?;

KWD_LOG          : WS? L O G WS?;

KWD_NATURAL      : WS? N A T U R A L WS?;

KWD_BASE         : WS? B A S E D? WS?;

KWD_ROUND        : WS? R O U N D (E D)? WS?;

KWD_COS          : WS? C O S (I N E)? WS?;

KWD_SIN          : WS? S I N E? WS?;

KWD_TAN          : WS? T A N (G E N T)? WS?;

KWD_MD5          : WS? M D '5' WS?;

KWD_SHA1         : WS? S H A '1' WS?;

KWD_SHA256       : WS? S H A '2' '5' '6' WS?;

KWD_SHA512       : WS? S H A '5' '1' '2' WS?;

KWD_SUB          : WS? S U B WS?;

KWD_STRING       : WS? S T R I N G WS?;

KWD_STARTS_WITH  : WS? S T A R T (S | I N G | E D)? KWD_WITH WS?;

KWD_ENDS_WITH    : WS? E N D (S | I N G | E D)? KWD_WITH WS?;

KWD_CONTAINS     : WS? C O N T A I N (S | I N G | E D)? WS?;

KWD_PART         : WS? P A R T WS?;
//Keywords used in ranking functions
KWD_RANK         : WS? R A N K WS?;

KWD_FIRST        : WS? F I R S T WS?;

KWD_ACCORDING    : WS? A C C O R D I N G WS?;

KWD_PARTITION    : WS? P A R T I T I O N (E D) WS?;

KWD_GROUP        : WS? G R O U P (E D) WS?;

KWD_ORDER        : WS? O R D E R (E D)? WS?;

KWD_SORT         : WS? S O R T (E D)? WS?;

KWD_ARRANGE      : WS? A R R A N G (E D)? WS?;

KWD_LEADING      : WS? L E A D I N G WS?;

KWD_PRECEDING    : WS? P R E C E D I N G WS?;

KWD_FOLLOWING    : WS? F O L L O W I N G WS?;

KWD_LAGGING      : WS? L A G G I N G WS?;


//Keyword used in aggregated

KWD_TOTAL        : WS? T O T A L WS?;

KWD_SUMMATION    : WS? S U M M A T I O N WS?;

KWD_SUM          : WS? S U M WS?;

KWD_OVERALL      : WS? O V E R A L L WS?;

KWD_GROSS        : WS? G R O S S WS?;

KWD_NET          : WS? N E T WS?;

KWD_MINIMUM      : WS? M I N I M U M WS?;

KWD_MIN          : WS? M I N WS?;

KWD_LEAST        : WS? L E A S T WS?;

KWD_SMALLEST     : WS? S M A L L E S T WS?;

KWD_LOWEST       : WS? L O W E S T WS?;

KWD_LITTLEST     : WS? L I T T L E S T WS?;

KWD_SLIGHTEST    : WS? S L I G H T E S T WS?;

KWD_EARLIEST     : WS? E A R L I E S T WS?;

KWD_BOTTOM       : WS? B O T T O M WS?;

KWD_POSSIBLE     : WS? P O S S I B L E WS?;

KWD_MAXIMUM      : WS? M A X I M U M WS?;

KWD_MAXIMAL      : WS? M A X I M A L WS?;

KWD_MAX          : WS? M A X WS?;

KWD_BIGGEST      : WS? B I G G E S T WS?;

KWD_HIGHEST      : WS? H I G H E S T WS?;

KWD_LARGEST      : WS? L A R G E S T WS?;

KWD_GREATEST     : WS? G R E A T E S T WS?;

KWD_LATEST       : WS? L A T E S T WS?;

KWD_EXTREME      : WS? E X T R E M E WS?;

KWD_TOP          : WS? T O P WS?;

KWD_MOST         : WS? M O S T WS?;

KWD_UTMOST       : WS? U T M O S T WS?;

KWD_AVG          : WS? A V G WS?;

KWD_AVERAGE      : WS? A V E R A G E WS?;

KWD_USUAL        : WS? U S U A L WS?;

KWD_MEAN         : WS? M E A N WS?;

KWD_COUNT        : WS? C O U N T WS?;

KWD_NUMBER       : WS? N U M B E R WS?;

KWD_AMOUNT       : WS? A M O U N T WS?;

KWD_QUANTITY     : WS? Q U A N T I T Y WS?;

KWD_AVAILABLE    : WS? A V A I L A B L E WS?;

KWD_UNAVAILABLE  : WS? U N A V A I L A B L E WS?;

KWD_COMPARING    : WS? C O M P A R I N G WS?;

KWD_COMPARED     : WS? C O M P A R E D? WS?;

KWD_VS           : WS? V S WS?;

KWD_NULL         : WS? N U L L WS?;

KWD_BLANK        : WS? B L A N K WS?;

KWD_EMPTY        : WS? E M P T Y WS?;


//Keywords as conjuction in comparision

KWD_BETWEEN      : WS? B E T W E E N WS?;

KWD_OUTSIDE      : WS? O U T S I D E WS?;

KWD_EQUALS       : WS? E Q U A L S WS?;

KWD_EQUALED      : WS? E Q U A L E D WS?;

KWD_EQUALLED     : WS? E Q U A L L E D WS?;

KWD_EQUALING     : WS? E Q U A L I N G WS?;

KWD_EQUALLING    : WS? E Q U A L L I N G WS?;

KWD_EQUAL        : WS? E Q U A L WS?;

KWD_SAME         : WS? S A M E WS?;

KWD_HOW_MANY     : WS? H O W WS M A N Y WS?;

KWD_HOW_MUCH     : WS? H O W WS M U C H WS?;

KWD_MANY         : WS? M A N Y WS?;

KWD_MUCH         : WS? M U C H  WS?;

KWD_HIGH         : WS? H I G H WS?;

KWD_LOW          : WS? L O W WS?;

KWD_LITTLE       : WS? L I T T L E WS?;

KWD_FEW          : WS? F E W WS?;

KWD_LESS         : WS? L E S S WS?;

KWD_SMALLER      : WS? S M A L L E R WS?;

KWD_EARLIER      : WS? E A R L I E R WS?;

KWD_SOONER      : WS? S O O N E R WS?;

KWD_SHORTER      : WS? S H O R T E R WS?;

KWD_LOWER        : WS? L O W E R WS?;

KWD_BEHIND       : WS? B E H I N D WS?;

KWD_BELOW        : WS? B E L O W WS?;

KWD_AHEAD        : WS? A H E A D WS?;

KWD_MORE         : WS? M O R E WS?;

KWD_LATER        : WS? L A T E R WS?;

KWD_LARGER       : WS? L A R G E R WS?;

KWD_BIGGER       : WS? B I G G E R WS?;

KWD_GREATER      : WS? G R E A T E R WS?;

KWD_LONGER       : WS? L O N G E R WS?;

KWD_HIGHER       : WS? H I G H E R WS?;

KWD_ABOVE        : WS? A B O V E WS?;

KWD_BEYOND       : WS? B E Y O N D WS?;

KWD_EXCEED       : WS? E X C E E D (S | E D | I N G)? WS?;

KWD_THAN         : WS? T H A N WS?;

KWD_AND          : WS? A N D WS?;

KWD_ALSO         : WS? A L S O WS?;

KWD_EVEN         : WS? E V E N WS?;

KWD_BUT          : WS? B U T WS?;

KWD_PER          : WS? P E R WS?;


//
KWD_CAN          : WS? C A N WS?;

KWD_COULD        : WS? C O U L D WS?;

KWD_SHALL        : WS? S H A L L WS?;

KWD_MIGHT        : WS? M I G H T WS?; // We will not support verb may because it has conflict with month of may

KWD_SHOULD       : WS? S H O U L D WS?;

KWD_WOULD        : WS? W O U L D WS?;

KWD_DO           : WS? D O WS?;

KWD_DOES         : WS? D O E S WS?;

KWD_BEEN         : WS? B E E N WS?;

KWD_BE           : WS? B E WS?;

KWD_IS           : WS? I S WS?;

KWD_ARE          : WS? A R E WS?;

KWD_DID          : WS? D I D WS?;

KWD_WAS          : WS? W A S WS?;

KWD_WERE         : WS? W E R E WS?;

KWD_WILL         : WS? W I L L WS?;

KWD_HAS          : WS? H A S WS?;

KWD_HAVE         : WS? H A V E WS?;

KWD_HAD          : WS? H A D WS?;

//
KWD_SHANT        : WS? S H A N T WS?;

KWD_WONT         : WS? W O N T WS?;
//
//KWD_OF_WHICH     : WS? O F WS? W H I C H WS?;

//KWD_FOR_WHICH    : WS? F O R WS? W H I C H WS?;

KWD_WHOSE        : WS? W H O S E  WS?;

KWD_WHERE        : WS? W H E R E  WS?;

KWD_WHEN         : WS? W H E N  WS?;

KWD_THAT         : WS? T H A T WS?;

KWD_THOSE        : WS? T H O S E WS?;

KWD_WHO          : WS? W H O WS?;

KWD_WHOM         : WS? W H O M WS?;

KWD_WHICH        : WS? W H I C H WS?;

KWD_WHAT         : WS? W H A T WS?;

KWD_WITH         : WS? W I T H WS?;

KWD_WITHOUT      : WS? W I T H O U T WS?;

KWD_WITHIN       : WS? W I T H I N WS?;

KWD_BY           : WS? B Y WS?;

KWD_ANY          : WS? A N Y WS?;

KWD_OF           : WS? O F WS?;

KWD_EACH         : WS? E A C H WS?;

KWD_EVERY        : WS? E V E R Y WS?;

KWD_IN           : WS? I N  WS?;

KWD_USING        : WS? U S I N G WS?;

KWD_FOR          : WS? F O R WS?;

KWD_OUT_OF       : WS? O U T WS O F WS?;

KWD_TAKE         : WS? T A K E (S | I N G)? WS?;

KWD_TOOK         : WS? T O O K WS?;

KWD_ALL          : WS? A L L WS?;

KWD_AS           : WS? A S WS?;

KWD_TO           : WS? T O WS?;

KWD_OR           : WS? O R WS?;

//Keywords for Negative preposition
KWD_NOT          : WS? N O T WS?;

KWD_NO           : WS? N O WS?;

KWD_NT           : WS? N '\'' T WS?;

KWD_FROM         : WS? F R O M WS?;

KWD_SINCE        : WS? S I N C E WS?;

KWD_UNTIL        : WS? U N T I L WS?;

KWD_TILL         : WS? T I L L WS?;   // 13JAN2016:Pulkit: Remember to add Till as an alternative to Until

KWD_AGO          : WS? A G O WS?;

KWD_AFTER        : WS? A F T E R WS?;

KWD_BEFORE       : WS? B E F O R E WS?;

KWD_UPTO         : WS? U P WS? T O WS?;

KWD_DURING       : WS? D U R I N G WS?;

KWD_PAST         : WS? P A S T WS?;

KWD_LAST         : WS? L A S T WS?;

KWD_NEXT         : WS? N E X T WS?;

KWD_NOW          : WS? N O W WS?;

KWD_YEAR         : WS? Y E A R S? WS?;

KWD_MONTH        : WS? M O N T H S? WS?;

KWD_WEEK         : WS? W E E K S? WS?;

KWD_TODAY        : WS? T O D A Y WS?;

KWD_TOMORROW     : WS? T O M O R R O W WS?;

KWD_YESTERDAY    : WS? Y E S T E R D A Y WS?;

KWD_DAY          : WS? D A Y S? WS?;

KWD_HOUR         : WS? H O U R S? WS?;

KWD_MINUTE       : WS? M I N U T E S? WS?;

KWD_SECOND       : WS? S E C O N D S? WS?;

KWD_JAN          : WS? J A N (U A R Y)? WS?;

KWD_FEB          : WS? F E B (R U A R Y)? WS?;

KWD_MAR          : WS? M A R (C H)? WS?;

KWD_APR          : WS? A P R (I L)? WS?;

KWD_MAY          : WS? M A Y WS?;

KWD_JUN          : WS? J U N E? WS?;

KWD_JUL          : WS? J U L Y? WS?;

KWD_AUG          : WS? A U G (U S T)? WS?;

KWD_SEP          : WS? S E P (T E M B E R)? WS?;

KWD_OCT          : WS? O C T (O B E R)? WS?;

KWD_NOV          : WS? N O V (E M B E R)? WS?;

KWD_DEC          : WS? D E C (E M B E R)? WS?;

KWD_MON          : WS? M O N (D A Y)? WS?;

KWD_TUE          : WS? T U E  (S D A Y)? WS?;

KWD_WED          : WS? W E D ( N E S D A Y)? WS?;

KWD_THU          : WS? T H U (R S D A Y)? WS?;

KWD_FRI          : WS? F R I (D A Y)? WS?;

KWD_SAT          : WS? S A T (U R D A Y)? WS?;

KWD_SUN          : WS? S U N (D A Y)? WS?;

KWD_ON           : WS? O N WS?;

KWD_AT           : WS? A T WS?;

//Number suffix

KWD_ST           : S T WS?;

KWD_ND           : N D WS?;

KWD_RD           : R D WS?;

KWD_TH           : T H WS?;

//Timezone and timestamp support words
KWD_AM           : WS? A M WS?;

KWD_PM           : WS? P M WS?;

KWD_GMT          : WS? G M T WS?;

KWD_UTC          : WS? U T C WS?;

KWD_Z            : WS? Z WS?;

//KWD_T            : WS? T WS?;

/*
IMPORTANT: 12DEC2017: Pulkit: Following number rules are following international standard for now. We need to perform impact analysis before changing to European standard.
AS of now comma cannot be used in numbers until conflict with field separator has been resolved.
*/
NUMBER
        : WS? (DIGIT)+ '.' (DIGIT)+ (E (SYM_PLUS | SYM_DASH) (DIGIT)+)? WS? /*{setText(getText().trim().replace(",",""));}*/
        | WS? DIGIT+ '.'                                                    {String org = getText(); setText(org.substring(0,org.length() - 1)/*.replace(",","")*/);}
        | '.' (DIGIT)+ (E (SYM_PLUS | SYM_DASH) (DIGIT)+)? WS?              {setText(Float.valueOf(getText()).toString());}
        | WS? (DIGIT)+ (E (SYM_PLUS | SYM_DASH) (DIGIT)+)? WS?              {setText(getText().replaceAll("(?i)(ST|ND|RD|TH)", ""));}
        ;

WORD
        : LETTER+
        ;
//ALPHA_NUMERIC
//        : (LETTER|DIGIT)+;

BOXED_STRING
        : WS? ('[') ~('[')* (']') WS?    {setText(getText().replace("[","").replace("]",""));}
        ;

QUOTED_STRING
        : WS? ('\'') ~('\'')* ('\'') WS? {setText(getText().replace("'",""));}
        | WS? ('"') ~('"')* ('"') WS?   {setText(getText().replace("\"",""));}
//        |  WS? ('"'|'\'') ~('"'| '\'')* ('"'|'\'') WS? {setText(getText().replace("\"","").replace("'",""));}
        ;
//Combining underscore as a part of letter token in order to minimize number of tokens
fragment LETTER
        :  [a-zA-Z_];

fragment DIGIT
        :   [0-9] ;
//
NEWLINE:'\r'? '\n' -> skip;
WS : [ \t]+;
ANY : .+?;
