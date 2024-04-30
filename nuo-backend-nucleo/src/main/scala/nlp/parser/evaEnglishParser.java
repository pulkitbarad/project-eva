// Generated from D:\NuoCanvas\Source_Code\nuo-backend-nucleo\src\main\scala\nlp\grammar/evaEnglish.g4 by ANTLR 4.7
package nlp.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class evaEnglishParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		CMD_SELECT=1, CMD_LEARN=2, CMD_PREDICT=3, FUNC_STD_DEV=4, FUNC_VARIANCE=5, 
		FUNC_ABSOLUTE=6, FUNC_SIGN=7, FUNC_SQRT=8, FUNC_EXPONENTIAL=9, FUNC_LOG=10, 
		FUNC_ROUND=11, FUNC_COS=12, FUNC_SIN=13, FUNC_TAN=14, FUNC_MD5=15, FUNC_SHA1=16, 
		FUNC_SHA256=17, FUNC_SHA512=18, FUNC_SUBSTRING=19, FUNC_LEFT_TRIM=20, 
		FUNC_RIGHT_TRIM=21, FUNC_TRIM=22, FUNC_INDEX_OF=23, FUNC_LENGHT=24, FUNC_CORRELATION=25, 
		KWD_LEFT=26, KWD_RIGHT=27, KWD_TRIM=28, KWD_INDEX=29, KWD_POSITION=30, 
		KWD_LOCATION=31, KWD_LENGTH=32, KWD_CORRELATION=33, KWD_CORR=34, POS_DET=35, 
		POS_WH_WORDS=36, POS_VERB_NEG=37, POS_VERB=38, POS_VERB_PREDICT=39, POS_VERB_HAVE_NEG=40, 
		POS_VERB_HAVE=41, POS_VERB_BE=42, POS_ADV_NEG=43, POS_POSESV_PRN=44, KWD_ORDER_CLAUSE=45, 
		KWD_PARTITION_CLAUSE=46, ARTICLE_A=47, ARTICLE_AN=48, ARTICLE_THE=49, 
		KWD_HIS=50, KWD_HER=51, KWD_THEIR=52, KWD_ITS=53, KWD_NUMBER_T=54, KWD_NUMBER_B=55, 
		KWD_NUMBER_M=56, KWD_NUMBER_K=57, KWD_AGG_SUM=58, KWD_AGG_MAX=59, KWD_AGG_MIN=60, 
		KWD_AGG_AVG=61, KWD_AGG_COUNT=62, KWD_GT_EQ=63, KWD_LT_EQ=64, KWD_DATE_EQUAL_TO=65, 
		KWD_EQUAL_TO=66, KWD_LT=67, KWD_LT_TIME=68, KWD_GT=69, KWD_GT_TIME=70, 
		KWD_GT_EQ_TIME=71, KWD_MONTH_OF_YEAR=72, KWD_DAY_OF_WEEK=73, SYM_STAR=74, 
		SYM_F_SLASH=75, SYM_B_SLASH=76, SYM_PLUS=77, SYM_DASH=78, SYM_EQUAL=79, 
		SYM_PERIOD=80, SYM_COMMA=81, SYM_LPAREN=82, SYM_RPAREN=83, SYM_LBRCS=84, 
		SYM_RBRCS=85, SYM_LANGB=86, SYM_RANGB=87, SYM_LBRCT=88, SYM_RBRCT=89, 
		SYM_COLON=90, SYM_SEMI_COLON=91, SYM_PERCENT=92, SYM_CARET=93, SYM_QUESTION=94, 
		KWD_FIND=95, KWD_GET=96, KWD_SELECT=97, KWD_SHOW=98, KWD_LEARN=99, KWD_TRAIN=100, 
		KWD_PREDICT=101, KWD_ESTIMATE=102, KWD_FORECAST=103, KWD_RECOMMEND=104, 
		KWD_TRILLION=105, KWD_BILLION=106, KWD_MILLION=107, KWD_THOUSAND=108, 
		KWD_GRAND=109, KWD_ABSOLUTELY=110, KWD_DEFINITELY=111, KWD_SURELY=112, 
		KWD_DETAILS=113, KWD_DIVISION=114, KWD_DIVIDED=115, KWD_STD_DEV=116, KWD_VARIANCE=117, 
		KWD_ABSOLUTE=118, KWD_SIGN=119, KWD_SQRT=120, KWD_EXPONENTIAL=121, KWD_LOG=122, 
		KWD_NATURAL=123, KWD_BASE=124, KWD_ROUND=125, KWD_COS=126, KWD_SIN=127, 
		KWD_TAN=128, KWD_MD5=129, KWD_SHA1=130, KWD_SHA256=131, KWD_SHA512=132, 
		KWD_SUB=133, KWD_STRING=134, KWD_STARTS_WITH=135, KWD_ENDS_WITH=136, KWD_CONTAINS=137, 
		KWD_PART=138, KWD_RANK=139, KWD_FIRST=140, KWD_ACCORDING=141, KWD_PARTITION=142, 
		KWD_GROUP=143, KWD_ORDER=144, KWD_SORT=145, KWD_ARRANGE=146, KWD_LEADING=147, 
		KWD_PRECEDING=148, KWD_FOLLOWING=149, KWD_LAGGING=150, KWD_TOTAL=151, 
		KWD_SUMMATION=152, KWD_SUM=153, KWD_OVERALL=154, KWD_GROSS=155, KWD_NET=156, 
		KWD_MINIMUM=157, KWD_MIN=158, KWD_LEAST=159, KWD_SMALLEST=160, KWD_LOWEST=161, 
		KWD_LITTLEST=162, KWD_SLIGHTEST=163, KWD_EARLIEST=164, KWD_BOTTOM=165, 
		KWD_POSSIBLE=166, KWD_MAXIMUM=167, KWD_MAXIMAL=168, KWD_MAX=169, KWD_BIGGEST=170, 
		KWD_HIGHEST=171, KWD_LARGEST=172, KWD_GREATEST=173, KWD_LATEST=174, KWD_EXTREME=175, 
		KWD_TOP=176, KWD_MOST=177, KWD_UTMOST=178, KWD_AVG=179, KWD_AVERAGE=180, 
		KWD_USUAL=181, KWD_MEAN=182, KWD_COUNT=183, KWD_NUMBER=184, KWD_AMOUNT=185, 
		KWD_QUANTITY=186, KWD_AVAILABLE=187, KWD_UNAVAILABLE=188, KWD_COMPARING=189, 
		KWD_COMPARED=190, KWD_VS=191, KWD_NULL=192, KWD_BLANK=193, KWD_EMPTY=194, 
		KWD_BETWEEN=195, KWD_OUTSIDE=196, KWD_EQUALS=197, KWD_EQUALED=198, KWD_EQUALLED=199, 
		KWD_EQUALING=200, KWD_EQUALLING=201, KWD_EQUAL=202, KWD_SAME=203, KWD_HOW_MANY=204, 
		KWD_HOW_MUCH=205, KWD_MANY=206, KWD_MUCH=207, KWD_HIGH=208, KWD_LOW=209, 
		KWD_LITTLE=210, KWD_FEW=211, KWD_LESS=212, KWD_SMALLER=213, KWD_EARLIER=214, 
		KWD_SOONER=215, KWD_SHORTER=216, KWD_LOWER=217, KWD_BEHIND=218, KWD_BELOW=219, 
		KWD_AHEAD=220, KWD_MORE=221, KWD_LATER=222, KWD_LARGER=223, KWD_BIGGER=224, 
		KWD_GREATER=225, KWD_LONGER=226, KWD_HIGHER=227, KWD_ABOVE=228, KWD_BEYOND=229, 
		KWD_EXCEED=230, KWD_THAN=231, KWD_AND=232, KWD_ALSO=233, KWD_EVEN=234, 
		KWD_BUT=235, KWD_PER=236, KWD_CAN=237, KWD_COULD=238, KWD_SHALL=239, KWD_MIGHT=240, 
		KWD_SHOULD=241, KWD_WOULD=242, KWD_DO=243, KWD_DOES=244, KWD_BEEN=245, 
		KWD_BE=246, KWD_IS=247, KWD_ARE=248, KWD_DID=249, KWD_WAS=250, KWD_WERE=251, 
		KWD_WILL=252, KWD_HAS=253, KWD_HAVE=254, KWD_HAD=255, KWD_SHANT=256, KWD_WONT=257, 
		KWD_WHOSE=258, KWD_WHERE=259, KWD_WHEN=260, KWD_THAT=261, KWD_THOSE=262, 
		KWD_WHO=263, KWD_WHOM=264, KWD_WHICH=265, KWD_WHAT=266, KWD_WITH=267, 
		KWD_WITHOUT=268, KWD_WITHIN=269, KWD_BY=270, KWD_ANY=271, KWD_OF=272, 
		KWD_EACH=273, KWD_EVERY=274, KWD_IN=275, KWD_USING=276, KWD_FOR=277, KWD_OUT_OF=278, 
		KWD_TAKE=279, KWD_TOOK=280, KWD_ALL=281, KWD_AS=282, KWD_TO=283, KWD_OR=284, 
		KWD_NOT=285, KWD_NO=286, KWD_NT=287, KWD_FROM=288, KWD_SINCE=289, KWD_UNTIL=290, 
		KWD_TILL=291, KWD_AGO=292, KWD_AFTER=293, KWD_BEFORE=294, KWD_UPTO=295, 
		KWD_DURING=296, KWD_PAST=297, KWD_LAST=298, KWD_NEXT=299, KWD_NOW=300, 
		KWD_YEAR=301, KWD_MONTH=302, KWD_WEEK=303, KWD_TODAY=304, KWD_TOMORROW=305, 
		KWD_YESTERDAY=306, KWD_DAY=307, KWD_HOUR=308, KWD_MINUTE=309, KWD_SECOND=310, 
		KWD_JAN=311, KWD_FEB=312, KWD_MAR=313, KWD_APR=314, KWD_MAY=315, KWD_JUN=316, 
		KWD_JUL=317, KWD_AUG=318, KWD_SEP=319, KWD_OCT=320, KWD_NOV=321, KWD_DEC=322, 
		KWD_MON=323, KWD_TUE=324, KWD_WED=325, KWD_THU=326, KWD_FRI=327, KWD_SAT=328, 
		KWD_SUN=329, KWD_ON=330, KWD_AT=331, KWD_ST=332, KWD_ND=333, KWD_RD=334, 
		KWD_TH=335, KWD_AM=336, KWD_PM=337, KWD_GMT=338, KWD_UTC=339, KWD_Z=340, 
		NUMBER=341, WORD=342, BOXED_STRING=343, QUOTED_STRING=344, NEWLINE=345, 
		WS=346, ANY=347;
	public static final int
		RULE_question = 0, RULE_selectClause = 1, RULE_fieldList = 2, RULE_criteriaClause = 3, 
		RULE_whereClauseOrphanContent = 4, RULE_dateField = 5, RULE_whereClauseContent = 6, 
		RULE_negativeVerb = 7, RULE_positiveVerb = 8, RULE_comparisionPhrases = 9, 
		RULE_unavailablePhrases = 10, RULE_duration = 11, RULE_durationField = 12, 
		RULE_date = 13, RULE_dayOfMonth = 14, RULE_time = 15, RULE_timeZone = 16, 
		RULE_aggregateField = 17, RULE_aggregateNumber = 18, RULE_numberField = 19, 
		RULE_rankField = 20, RULE_overClause = 21, RULE_functionTwoArgs = 22, 
		RULE_functionOneArg = 23, RULE_field = 24, RULE_fieldContent = 25, RULE_article = 26;
	public static final String[] ruleNames = {
		"question", "selectClause", "fieldList", "criteriaClause", "whereClauseOrphanContent", 
		"dateField", "whereClauseContent", "negativeVerb", "positiveVerb", "comparisionPhrases", 
		"unavailablePhrases", "duration", "durationField", "date", "dayOfMonth", 
		"time", "timeZone", "aggregateField", "aggregateNumber", "numberField", 
		"rankField", "overClause", "functionTwoArgs", "functionOneArg", "field", 
		"fieldContent", "article"
	};

	private static final String[] _LITERAL_NAMES = {
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "CMD_SELECT", "CMD_LEARN", "CMD_PREDICT", "FUNC_STD_DEV", "FUNC_VARIANCE", 
		"FUNC_ABSOLUTE", "FUNC_SIGN", "FUNC_SQRT", "FUNC_EXPONENTIAL", "FUNC_LOG", 
		"FUNC_ROUND", "FUNC_COS", "FUNC_SIN", "FUNC_TAN", "FUNC_MD5", "FUNC_SHA1", 
		"FUNC_SHA256", "FUNC_SHA512", "FUNC_SUBSTRING", "FUNC_LEFT_TRIM", "FUNC_RIGHT_TRIM", 
		"FUNC_TRIM", "FUNC_INDEX_OF", "FUNC_LENGHT", "FUNC_CORRELATION", "KWD_LEFT", 
		"KWD_RIGHT", "KWD_TRIM", "KWD_INDEX", "KWD_POSITION", "KWD_LOCATION", 
		"KWD_LENGTH", "KWD_CORRELATION", "KWD_CORR", "POS_DET", "POS_WH_WORDS", 
		"POS_VERB_NEG", "POS_VERB", "POS_VERB_PREDICT", "POS_VERB_HAVE_NEG", "POS_VERB_HAVE", 
		"POS_VERB_BE", "POS_ADV_NEG", "POS_POSESV_PRN", "KWD_ORDER_CLAUSE", "KWD_PARTITION_CLAUSE", 
		"ARTICLE_A", "ARTICLE_AN", "ARTICLE_THE", "KWD_HIS", "KWD_HER", "KWD_THEIR", 
		"KWD_ITS", "KWD_NUMBER_T", "KWD_NUMBER_B", "KWD_NUMBER_M", "KWD_NUMBER_K", 
		"KWD_AGG_SUM", "KWD_AGG_MAX", "KWD_AGG_MIN", "KWD_AGG_AVG", "KWD_AGG_COUNT", 
		"KWD_GT_EQ", "KWD_LT_EQ", "KWD_DATE_EQUAL_TO", "KWD_EQUAL_TO", "KWD_LT", 
		"KWD_LT_TIME", "KWD_GT", "KWD_GT_TIME", "KWD_GT_EQ_TIME", "KWD_MONTH_OF_YEAR", 
		"KWD_DAY_OF_WEEK", "SYM_STAR", "SYM_F_SLASH", "SYM_B_SLASH", "SYM_PLUS", 
		"SYM_DASH", "SYM_EQUAL", "SYM_PERIOD", "SYM_COMMA", "SYM_LPAREN", "SYM_RPAREN", 
		"SYM_LBRCS", "SYM_RBRCS", "SYM_LANGB", "SYM_RANGB", "SYM_LBRCT", "SYM_RBRCT", 
		"SYM_COLON", "SYM_SEMI_COLON", "SYM_PERCENT", "SYM_CARET", "SYM_QUESTION", 
		"KWD_FIND", "KWD_GET", "KWD_SELECT", "KWD_SHOW", "KWD_LEARN", "KWD_TRAIN", 
		"KWD_PREDICT", "KWD_ESTIMATE", "KWD_FORECAST", "KWD_RECOMMEND", "KWD_TRILLION", 
		"KWD_BILLION", "KWD_MILLION", "KWD_THOUSAND", "KWD_GRAND", "KWD_ABSOLUTELY", 
		"KWD_DEFINITELY", "KWD_SURELY", "KWD_DETAILS", "KWD_DIVISION", "KWD_DIVIDED", 
		"KWD_STD_DEV", "KWD_VARIANCE", "KWD_ABSOLUTE", "KWD_SIGN", "KWD_SQRT", 
		"KWD_EXPONENTIAL", "KWD_LOG", "KWD_NATURAL", "KWD_BASE", "KWD_ROUND", 
		"KWD_COS", "KWD_SIN", "KWD_TAN", "KWD_MD5", "KWD_SHA1", "KWD_SHA256", 
		"KWD_SHA512", "KWD_SUB", "KWD_STRING", "KWD_STARTS_WITH", "KWD_ENDS_WITH", 
		"KWD_CONTAINS", "KWD_PART", "KWD_RANK", "KWD_FIRST", "KWD_ACCORDING", 
		"KWD_PARTITION", "KWD_GROUP", "KWD_ORDER", "KWD_SORT", "KWD_ARRANGE", 
		"KWD_LEADING", "KWD_PRECEDING", "KWD_FOLLOWING", "KWD_LAGGING", "KWD_TOTAL", 
		"KWD_SUMMATION", "KWD_SUM", "KWD_OVERALL", "KWD_GROSS", "KWD_NET", "KWD_MINIMUM", 
		"KWD_MIN", "KWD_LEAST", "KWD_SMALLEST", "KWD_LOWEST", "KWD_LITTLEST", 
		"KWD_SLIGHTEST", "KWD_EARLIEST", "KWD_BOTTOM", "KWD_POSSIBLE", "KWD_MAXIMUM", 
		"KWD_MAXIMAL", "KWD_MAX", "KWD_BIGGEST", "KWD_HIGHEST", "KWD_LARGEST", 
		"KWD_GREATEST", "KWD_LATEST", "KWD_EXTREME", "KWD_TOP", "KWD_MOST", "KWD_UTMOST", 
		"KWD_AVG", "KWD_AVERAGE", "KWD_USUAL", "KWD_MEAN", "KWD_COUNT", "KWD_NUMBER", 
		"KWD_AMOUNT", "KWD_QUANTITY", "KWD_AVAILABLE", "KWD_UNAVAILABLE", "KWD_COMPARING", 
		"KWD_COMPARED", "KWD_VS", "KWD_NULL", "KWD_BLANK", "KWD_EMPTY", "KWD_BETWEEN", 
		"KWD_OUTSIDE", "KWD_EQUALS", "KWD_EQUALED", "KWD_EQUALLED", "KWD_EQUALING", 
		"KWD_EQUALLING", "KWD_EQUAL", "KWD_SAME", "KWD_HOW_MANY", "KWD_HOW_MUCH", 
		"KWD_MANY", "KWD_MUCH", "KWD_HIGH", "KWD_LOW", "KWD_LITTLE", "KWD_FEW", 
		"KWD_LESS", "KWD_SMALLER", "KWD_EARLIER", "KWD_SOONER", "KWD_SHORTER", 
		"KWD_LOWER", "KWD_BEHIND", "KWD_BELOW", "KWD_AHEAD", "KWD_MORE", "KWD_LATER", 
		"KWD_LARGER", "KWD_BIGGER", "KWD_GREATER", "KWD_LONGER", "KWD_HIGHER", 
		"KWD_ABOVE", "KWD_BEYOND", "KWD_EXCEED", "KWD_THAN", "KWD_AND", "KWD_ALSO", 
		"KWD_EVEN", "KWD_BUT", "KWD_PER", "KWD_CAN", "KWD_COULD", "KWD_SHALL", 
		"KWD_MIGHT", "KWD_SHOULD", "KWD_WOULD", "KWD_DO", "KWD_DOES", "KWD_BEEN", 
		"KWD_BE", "KWD_IS", "KWD_ARE", "KWD_DID", "KWD_WAS", "KWD_WERE", "KWD_WILL", 
		"KWD_HAS", "KWD_HAVE", "KWD_HAD", "KWD_SHANT", "KWD_WONT", "KWD_WHOSE", 
		"KWD_WHERE", "KWD_WHEN", "KWD_THAT", "KWD_THOSE", "KWD_WHO", "KWD_WHOM", 
		"KWD_WHICH", "KWD_WHAT", "KWD_WITH", "KWD_WITHOUT", "KWD_WITHIN", "KWD_BY", 
		"KWD_ANY", "KWD_OF", "KWD_EACH", "KWD_EVERY", "KWD_IN", "KWD_USING", "KWD_FOR", 
		"KWD_OUT_OF", "KWD_TAKE", "KWD_TOOK", "KWD_ALL", "KWD_AS", "KWD_TO", "KWD_OR", 
		"KWD_NOT", "KWD_NO", "KWD_NT", "KWD_FROM", "KWD_SINCE", "KWD_UNTIL", "KWD_TILL", 
		"KWD_AGO", "KWD_AFTER", "KWD_BEFORE", "KWD_UPTO", "KWD_DURING", "KWD_PAST", 
		"KWD_LAST", "KWD_NEXT", "KWD_NOW", "KWD_YEAR", "KWD_MONTH", "KWD_WEEK", 
		"KWD_TODAY", "KWD_TOMORROW", "KWD_YESTERDAY", "KWD_DAY", "KWD_HOUR", "KWD_MINUTE", 
		"KWD_SECOND", "KWD_JAN", "KWD_FEB", "KWD_MAR", "KWD_APR", "KWD_MAY", "KWD_JUN", 
		"KWD_JUL", "KWD_AUG", "KWD_SEP", "KWD_OCT", "KWD_NOV", "KWD_DEC", "KWD_MON", 
		"KWD_TUE", "KWD_WED", "KWD_THU", "KWD_FRI", "KWD_SAT", "KWD_SUN", "KWD_ON", 
		"KWD_AT", "KWD_ST", "KWD_ND", "KWD_RD", "KWD_TH", "KWD_AM", "KWD_PM", 
		"KWD_GMT", "KWD_UTC", "KWD_Z", "NUMBER", "WORD", "BOXED_STRING", "QUOTED_STRING", 
		"NEWLINE", "WS", "ANY"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "evaEnglish.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public evaEnglishParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class QuestionContext extends ParserRuleContext {
		public String sql;
		public List<String> fields;
		public List<String> nonAggFields;
		public List<String> entities;
		public String whereClause;
		public String havingClause;
		public QuestionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_question; }
	 
		public QuestionContext() { }
		public void copyFrom(QuestionContext ctx) {
			super.copyFrom(ctx);
			this.sql = ctx.sql;
			this.fields = ctx.fields;
			this.nonAggFields = ctx.nonAggFields;
			this.entities = ctx.entities;
			this.whereClause = ctx.whereClause;
			this.havingClause = ctx.havingClause;
		}
	}
	public static class CommandSelectContext extends QuestionContext {
		public SelectClauseContext selectClause() {
			return getRuleContext(SelectClauseContext.class,0);
		}
		public CriteriaClauseContext criteriaClause() {
			return getRuleContext(CriteriaClauseContext.class,0);
		}
		public TerminalNode SYM_QUESTION() { return getToken(evaEnglishParser.SYM_QUESTION, 0); }
		public TerminalNode POS_DET() { return getToken(evaEnglishParser.POS_DET, 0); }
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public CommandSelectContext(QuestionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterCommandSelect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitCommandSelect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitCommandSelect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QuestionContext question() throws RecognitionException {
		QuestionContext _localctx = new QuestionContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_question);
		int _la;
		try {
			_localctx = new CommandSelectContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			selectClause();
			setState(62);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_DET) | (1L << POS_WH_WORDS) | (1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0) || _la==KWD_WITH) {
				{
				setState(59);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case POS_DET:
					{
					setState(55);
					match(POS_DET);
					}
					break;
				case POS_WH_WORDS:
					{
					setState(56);
					match(POS_WH_WORDS);
					}
					break;
				case KWD_WITH:
					{
					setState(57);
					match(KWD_WITH);
					}
					break;
				case POS_VERB:
				case POS_VERB_PREDICT:
				case POS_VERB_HAVE:
					{
					setState(58);
					positiveVerb();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(61);
				criteriaClause(0);
				}
			}

			setState(65);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SYM_QUESTION) {
				{
				setState(64);
				match(SYM_QUESTION);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectClauseContext extends ParserRuleContext {
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public FieldListContext fieldList() {
			return getRuleContext(FieldListContext.class,0);
		}
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public TerminalNode CMD_SELECT() { return getToken(evaEnglishParser.CMD_SELECT, 0); }
		public SelectClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterSelectClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitSelectClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitSelectClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectClauseContext selectClause() throws RecognitionException {
		SelectClauseContext _localctx = new SelectClauseContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_selectClause);
		try {
			setState(75);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(67);
				match(POS_WH_WORDS);
				setState(69);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
				case 1:
					{
					setState(68);
					positiveVerb();
					}
					break;
				}
				setState(71);
				fieldList();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(72);
				match(CMD_SELECT);
				setState(73);
				fieldList();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(74);
				fieldList();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FieldListContext extends ParserRuleContext {
		public List<List<String>> values;
		public List<RankFieldContext> rankField() {
			return getRuleContexts(RankFieldContext.class);
		}
		public RankFieldContext rankField(int i) {
			return getRuleContext(RankFieldContext.class,i);
		}
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public List<TerminalNode> SYM_COMMA() { return getTokens(evaEnglishParser.SYM_COMMA); }
		public TerminalNode SYM_COMMA(int i) {
			return getToken(evaEnglishParser.SYM_COMMA, i);
		}
		public List<TerminalNode> KWD_AND() { return getTokens(evaEnglishParser.KWD_AND); }
		public TerminalNode KWD_AND(int i) {
			return getToken(evaEnglishParser.KWD_AND, i);
		}
		public List<TerminalNode> KWD_BY() { return getTokens(evaEnglishParser.KWD_BY); }
		public TerminalNode KWD_BY(int i) {
			return getToken(evaEnglishParser.KWD_BY, i);
		}
		public List<TerminalNode> KWD_PER() { return getTokens(evaEnglishParser.KWD_PER); }
		public TerminalNode KWD_PER(int i) {
			return getToken(evaEnglishParser.KWD_PER, i);
		}
		public FieldListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FieldListContext fieldList() throws RecognitionException {
		FieldListContext _localctx = new FieldListContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_fieldList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(77);
				rankField();
				}
				break;
			case 2:
				{
				setState(78);
				field(0);
				}
				break;
			}
			setState(88);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SYM_COMMA || ((((_la - 232)) & ~0x3f) == 0 && ((1L << (_la - 232)) & ((1L << (KWD_AND - 232)) | (1L << (KWD_PER - 232)) | (1L << (KWD_BY - 232)))) != 0)) {
				{
				{
				setState(81);
				_la = _input.LA(1);
				if ( !(_la==SYM_COMMA || ((((_la - 232)) & ~0x3f) == 0 && ((1L << (_la - 232)) & ((1L << (KWD_AND - 232)) | (1L << (KWD_PER - 232)) | (1L << (KWD_BY - 232)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(84);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
				case 1:
					{
					setState(82);
					rankField();
					}
					break;
				case 2:
					{
					setState(83);
					field(0);
					}
					break;
				}
				}
				}
				setState(90);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CriteriaClauseContext extends ParserRuleContext {
		public String defaultValue;
		public String sumCaseValue;
		public int fieldType;
		public CriteriaClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_criteriaClause; }
	 
		public CriteriaClauseContext() { }
		public void copyFrom(CriteriaClauseContext ctx) {
			super.copyFrom(ctx);
			this.defaultValue = ctx.defaultValue;
			this.sumCaseValue = ctx.sumCaseValue;
			this.fieldType = ctx.fieldType;
		}
	}
	public static class CriteriaParenContext extends CriteriaClauseContext {
		public TerminalNode SYM_LPAREN() { return getToken(evaEnglishParser.SYM_LPAREN, 0); }
		public CriteriaClauseContext criteriaClause() {
			return getRuleContext(CriteriaClauseContext.class,0);
		}
		public TerminalNode SYM_RPAREN() { return getToken(evaEnglishParser.SYM_RPAREN, 0); }
		public CriteriaParenContext(CriteriaClauseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterCriteriaParen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitCriteriaParen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitCriteriaParen(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CriteriaNegativeContext extends CriteriaClauseContext {
		public NegativeVerbContext negativeVerb() {
			return getRuleContext(NegativeVerbContext.class,0);
		}
		public TerminalNode SYM_LPAREN() { return getToken(evaEnglishParser.SYM_LPAREN, 0); }
		public CriteriaClauseContext criteriaClause() {
			return getRuleContext(CriteriaClauseContext.class,0);
		}
		public TerminalNode SYM_RPAREN() { return getToken(evaEnglishParser.SYM_RPAREN, 0); }
		public TerminalNode POS_DET() { return getToken(evaEnglishParser.POS_DET, 0); }
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public CriteriaNegativeContext(CriteriaClauseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterCriteriaNegative(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitCriteriaNegative(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitCriteriaNegative(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CriteriaPositiveContext extends CriteriaClauseContext {
		public CriteriaClauseContext criteriaClause() {
			return getRuleContext(CriteriaClauseContext.class,0);
		}
		public TerminalNode POS_DET() { return getToken(evaEnglishParser.POS_DET, 0); }
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public TerminalNode KWD_ALSO() { return getToken(evaEnglishParser.KWD_ALSO, 0); }
		public CriteriaPositiveContext(CriteriaClauseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterCriteriaPositive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitCriteriaPositive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitCriteriaPositive(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CriteriaWhereDefaultContext extends CriteriaClauseContext {
		public WhereClauseContentContext whereClauseContent() {
			return getRuleContext(WhereClauseContentContext.class,0);
		}
		public CriteriaWhereDefaultContext(CriteriaClauseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterCriteriaWhereDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitCriteriaWhereDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitCriteriaWhereDefault(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CriteriaAndContext extends CriteriaClauseContext {
		public List<CriteriaClauseContext> criteriaClause() {
			return getRuleContexts(CriteriaClauseContext.class);
		}
		public CriteriaClauseContext criteriaClause(int i) {
			return getRuleContext(CriteriaClauseContext.class,i);
		}
		public TerminalNode POS_DET() { return getToken(evaEnglishParser.POS_DET, 0); }
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public List<TerminalNode> KWD_ALSO() { return getTokens(evaEnglishParser.KWD_ALSO); }
		public TerminalNode KWD_ALSO(int i) {
			return getToken(evaEnglishParser.KWD_ALSO, i);
		}
		public TerminalNode KWD_AND() { return getToken(evaEnglishParser.KWD_AND, 0); }
		public TerminalNode KWD_BUT() { return getToken(evaEnglishParser.KWD_BUT, 0); }
		public CriteriaAndContext(CriteriaClauseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterCriteriaAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitCriteriaAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitCriteriaAnd(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CriteriaWhereOrphanNegativeContext extends CriteriaClauseContext {
		public WhereClauseOrphanContentContext whereClauseOrphanContent() {
			return getRuleContext(WhereClauseOrphanContentContext.class,0);
		}
		public TerminalNode KWD_WITHOUT() { return getToken(evaEnglishParser.KWD_WITHOUT, 0); }
		public NegativeVerbContext negativeVerb() {
			return getRuleContext(NegativeVerbContext.class,0);
		}
		public CriteriaWhereOrphanNegativeContext(CriteriaClauseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterCriteriaWhereOrphanNegative(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitCriteriaWhereOrphanNegative(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitCriteriaWhereOrphanNegative(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CriteriaWhereOrphanDefaultContext extends CriteriaClauseContext {
		public WhereClauseOrphanContentContext whereClauseOrphanContent() {
			return getRuleContext(WhereClauseOrphanContentContext.class,0);
		}
		public CriteriaWhereOrphanDefaultContext(CriteriaClauseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterCriteriaWhereOrphanDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitCriteriaWhereOrphanDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitCriteriaWhereOrphanDefault(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CriteriaOrContext extends CriteriaClauseContext {
		public List<CriteriaClauseContext> criteriaClause() {
			return getRuleContexts(CriteriaClauseContext.class);
		}
		public CriteriaClauseContext criteriaClause(int i) {
			return getRuleContext(CriteriaClauseContext.class,i);
		}
		public TerminalNode KWD_OR() { return getToken(evaEnglishParser.KWD_OR, 0); }
		public TerminalNode POS_DET() { return getToken(evaEnglishParser.POS_DET, 0); }
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public CriteriaOrContext(CriteriaClauseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterCriteriaOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitCriteriaOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitCriteriaOr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CriteriaClauseContext criteriaClause() throws RecognitionException {
		return criteriaClause(0);
	}

	private CriteriaClauseContext criteriaClause(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		CriteriaClauseContext _localctx = new CriteriaClauseContext(_ctx, _parentState);
		CriteriaClauseContext _prevctx = _localctx;
		int _startState = 6;
		enterRecursionRule(_localctx, 6, RULE_criteriaClause, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				_localctx = new CriteriaParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(92);
				match(SYM_LPAREN);
				setState(93);
				criteriaClause(0);
				setState(94);
				match(SYM_RPAREN);
				}
				break;
			case 2:
				{
				_localctx = new CriteriaNegativeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(97);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) {
					{
					setState(96);
					_la = _input.LA(1);
					if ( !(_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(99);
				negativeVerb();
				setState(100);
				match(SYM_LPAREN);
				setState(101);
				criteriaClause(0);
				setState(102);
				match(SYM_RPAREN);
				}
				break;
			case 3:
				{
				_localctx = new CriteriaPositiveContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(104);
				_la = _input.LA(1);
				if ( !(_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(106);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
				case 1:
					{
					setState(105);
					positiveVerb();
					}
					break;
				}
				setState(108);
				criteriaClause(5);
				}
				break;
			case 4:
				{
				_localctx = new CriteriaPositiveContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(110);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_ALSO) {
					{
					setState(109);
					match(KWD_ALSO);
					}
				}

				setState(112);
				positiveVerb();
				setState(113);
				criteriaClause(4);
				}
				break;
			case 5:
				{
				_localctx = new CriteriaWhereOrphanNegativeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(117);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case KWD_WITHOUT:
					{
					setState(115);
					match(KWD_WITHOUT);
					}
					break;
				case POS_VERB_NEG:
				case POS_VERB_HAVE_NEG:
				case POS_VERB_HAVE:
				case KWD_NO:
					{
					setState(116);
					negativeVerb();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(119);
				whereClauseOrphanContent();
				}
				break;
			case 6:
				{
				_localctx = new CriteriaWhereOrphanDefaultContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(120);
				whereClauseOrphanContent();
				}
				break;
			case 7:
				{
				_localctx = new CriteriaWhereDefaultContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(121);
				whereClauseContent();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(157);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(155);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
					case 1:
						{
						_localctx = new CriteriaAndContext(new CriteriaClauseContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_criteriaClause);
						setState(124);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(126);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==KWD_ALSO) {
							{
							setState(125);
							match(KWD_ALSO);
							}
						}

						setState(128);
						_la = _input.LA(1);
						if ( !(_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(130);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
						case 1:
							{
							setState(129);
							match(KWD_ALSO);
							}
							break;
						}
						setState(132);
						criteriaClause(11);
						}
						break;
					case 2:
						{
						_localctx = new CriteriaAndContext(new CriteriaClauseContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_criteriaClause);
						setState(133);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(134);
						_la = _input.LA(1);
						if ( !(_la==KWD_AND || _la==KWD_BUT) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(136);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==KWD_ALSO) {
							{
							setState(135);
							match(KWD_ALSO);
							}
						}

						setState(138);
						_la = _input.LA(1);
						if ( !(_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(140);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
						case 1:
							{
							setState(139);
							match(KWD_ALSO);
							}
							break;
						}
						setState(142);
						criteriaClause(10);
						}
						break;
					case 3:
						{
						_localctx = new CriteriaAndContext(new CriteriaClauseContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_criteriaClause);
						setState(143);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(144);
						_la = _input.LA(1);
						if ( !(_la==KWD_AND || _la==KWD_BUT) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(146);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
						case 1:
							{
							setState(145);
							match(KWD_ALSO);
							}
							break;
						}
						setState(148);
						criteriaClause(9);
						}
						break;
					case 4:
						{
						_localctx = new CriteriaOrContext(new CriteriaClauseContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_criteriaClause);
						setState(149);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(150);
						match(KWD_OR);
						setState(152);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
						case 1:
							{
							setState(151);
							_la = _input.LA(1);
							if ( !(_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) ) {
							_errHandler.recoverInline(this);
							}
							else {
								if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
								_errHandler.reportMatch(this);
								consume();
							}
							}
							break;
						}
						setState(154);
						criteriaClause(8);
						}
						break;
					}
					} 
				}
				setState(159);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class WhereClauseOrphanContentContext extends ParserRuleContext {
		public String value;
		public int fieldType;
		public WhereClauseOrphanContentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereClauseOrphanContent; }
	 
		public WhereClauseOrphanContentContext() { }
		public void copyFrom(WhereClauseOrphanContentContext ctx) {
			super.copyFrom(ctx);
			this.value = ctx.value;
			this.fieldType = ctx.fieldType;
		}
	}
	public static class WhereOrphanEqContext extends WhereClauseOrphanContentContext {
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public NegativeVerbContext negativeVerb() {
			return getRuleContext(NegativeVerbContext.class,0);
		}
		public TerminalNode KWD_EQUAL_TO() { return getToken(evaEnglishParser.KWD_EQUAL_TO, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public WhereOrphanEqContext(WhereClauseOrphanContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereOrphanEq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereOrphanEq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereOrphanEq(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereOrphanLtEqContext extends WhereClauseOrphanContentContext {
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public TerminalNode KWD_LT_EQ() { return getToken(evaEnglishParser.KWD_LT_EQ, 0); }
		public NegativeVerbContext negativeVerb() {
			return getRuleContext(NegativeVerbContext.class,0);
		}
		public TerminalNode KWD_GT() { return getToken(evaEnglishParser.KWD_GT, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public WhereOrphanLtEqContext(WhereClauseOrphanContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereOrphanLtEq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereOrphanLtEq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereOrphanLtEq(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereOrphanGtContext extends WhereClauseOrphanContentContext {
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public TerminalNode KWD_GT() { return getToken(evaEnglishParser.KWD_GT, 0); }
		public NegativeVerbContext negativeVerb() {
			return getRuleContext(NegativeVerbContext.class,0);
		}
		public TerminalNode KWD_LT_EQ() { return getToken(evaEnglishParser.KWD_LT_EQ, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public WhereOrphanGtContext(WhereClauseOrphanContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereOrphanGt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereOrphanGt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereOrphanGt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereOrphanBetweenContext extends WhereClauseOrphanContentContext {
		public List<NumberFieldContext> numberField() {
			return getRuleContexts(NumberFieldContext.class);
		}
		public NumberFieldContext numberField(int i) {
			return getRuleContext(NumberFieldContext.class,i);
		}
		public TerminalNode KWD_AND() { return getToken(evaEnglishParser.KWD_AND, 0); }
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public TerminalNode KWD_BETWEEN() { return getToken(evaEnglishParser.KWD_BETWEEN, 0); }
		public TerminalNode KWD_OUTSIDE() { return getToken(evaEnglishParser.KWD_OUTSIDE, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public WhereOrphanBetweenContext(WhereClauseOrphanContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereOrphanBetween(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereOrphanBetween(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereOrphanBetween(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereOrphanLtContext extends WhereClauseOrphanContentContext {
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public TerminalNode KWD_LT() { return getToken(evaEnglishParser.KWD_LT, 0); }
		public NegativeVerbContext negativeVerb() {
			return getRuleContext(NegativeVerbContext.class,0);
		}
		public TerminalNode KWD_GT_EQ() { return getToken(evaEnglishParser.KWD_GT_EQ, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public WhereOrphanLtContext(WhereClauseOrphanContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereOrphanLt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereOrphanLt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereOrphanLt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereOrphanGtEqContext extends WhereClauseOrphanContentContext {
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public TerminalNode KWD_GT_EQ() { return getToken(evaEnglishParser.KWD_GT_EQ, 0); }
		public NegativeVerbContext negativeVerb() {
			return getRuleContext(NegativeVerbContext.class,0);
		}
		public TerminalNode KWD_LT() { return getToken(evaEnglishParser.KWD_LT, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public WhereOrphanGtEqContext(WhereClauseOrphanContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereOrphanGtEq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereOrphanGtEq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereOrphanGtEq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhereClauseOrphanContentContext whereClauseOrphanContent() throws RecognitionException {
		WhereClauseOrphanContentContext _localctx = new WhereClauseOrphanContentContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_whereClauseOrphanContent);
		int _la;
		try {
			setState(251);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				_localctx = new WhereOrphanBetweenContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(160);
				_la = _input.LA(1);
				if ( !(_la==KWD_BETWEEN || _la==KWD_OUTSIDE) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(161);
				numberField();
				setState(162);
				match(KWD_AND);
				setState(163);
				numberField();
				setState(165);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
				case 1:
					{
					setState(164);
					_la = _input.LA(1);
					if ( !(_la==KWD_OF || _la==KWD_IN) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(167);
				field(0);
				}
				break;
			case 2:
				_localctx = new WhereOrphanGtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(176);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
				case 1:
					{
					{
					setState(169);
					negativeVerb();
					setState(170);
					match(KWD_LT_EQ);
					}
					}
					break;
				case 2:
					{
					setState(173);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0)) {
						{
						setState(172);
						positiveVerb();
						}
					}

					setState(175);
					match(KWD_GT);
					}
					break;
				}
				setState(178);
				numberField();
				setState(180);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
				case 1:
					{
					setState(179);
					_la = _input.LA(1);
					if ( !(_la==KWD_OF || _la==KWD_IN) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(182);
				field(0);
				}
				break;
			case 3:
				_localctx = new WhereOrphanLtContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(191);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
				case 1:
					{
					{
					setState(184);
					negativeVerb();
					setState(185);
					match(KWD_GT_EQ);
					}
					}
					break;
				case 2:
					{
					setState(188);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0)) {
						{
						setState(187);
						positiveVerb();
						}
					}

					setState(190);
					match(KWD_LT);
					}
					break;
				}
				setState(193);
				numberField();
				setState(195);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
				case 1:
					{
					setState(194);
					_la = _input.LA(1);
					if ( !(_la==KWD_OF || _la==KWD_IN) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(197);
				field(0);
				}
				break;
			case 4:
				_localctx = new WhereOrphanGtEqContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(206);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
				case 1:
					{
					{
					setState(199);
					negativeVerb();
					setState(200);
					match(KWD_LT);
					}
					}
					break;
				case 2:
					{
					setState(203);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0)) {
						{
						setState(202);
						positiveVerb();
						}
					}

					setState(205);
					match(KWD_GT_EQ);
					}
					break;
				}
				setState(208);
				numberField();
				setState(210);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
				case 1:
					{
					setState(209);
					_la = _input.LA(1);
					if ( !(_la==KWD_OF || _la==KWD_IN) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(212);
				field(0);
				}
				break;
			case 5:
				_localctx = new WhereOrphanLtEqContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(221);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
				case 1:
					{
					{
					setState(214);
					negativeVerb();
					setState(215);
					match(KWD_GT);
					}
					}
					break;
				case 2:
					{
					setState(218);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0)) {
						{
						setState(217);
						positiveVerb();
						}
					}

					setState(220);
					match(KWD_LT_EQ);
					}
					break;
				}
				setState(223);
				numberField();
				setState(225);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
				case 1:
					{
					setState(224);
					_la = _input.LA(1);
					if ( !(_la==KWD_OF || _la==KWD_IN) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(227);
				field(0);
				}
				break;
			case 6:
				_localctx = new WhereOrphanEqContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(230);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB_NEG) | (1L << POS_VERB_HAVE_NEG) | (1L << POS_VERB_HAVE))) != 0) || _la==KWD_NO) {
					{
					setState(229);
					negativeVerb();
					}
				}

				setState(233);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_EQUAL_TO) {
					{
					setState(232);
					match(KWD_EQUAL_TO);
					}
				}

				setState(235);
				numberField();
				setState(237);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
				case 1:
					{
					setState(236);
					_la = _input.LA(1);
					if ( !(_la==KWD_OF || _la==KWD_IN) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(239);
				field(0);
				}
				break;
			case 7:
				_localctx = new WhereOrphanEqContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(242);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
				case 1:
					{
					setState(241);
					negativeVerb();
					}
					break;
				}
				setState(245);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
				case 1:
					{
					setState(244);
					match(KWD_EQUAL_TO);
					}
					break;
				}
				setState(247);
				field(0);
				setState(248);
				match(KWD_OF);
				setState(249);
				numberField();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DateFieldContext extends ParserRuleContext {
		public String fieldText;
		public int fieldType;
		public String fieldValue;
		public String datatype;
		public long timeInMillis;
		public int dd;
		public int mm;
		public int yyyy;
		public int hh;
		public int MM;
		public int ss;
		public int SSS;
		public String tz;
		public DateFieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dateField; }
	 
		public DateFieldContext() { }
		public void copyFrom(DateFieldContext ctx) {
			super.copyFrom(ctx);
			this.fieldText = ctx.fieldText;
			this.fieldType = ctx.fieldType;
			this.fieldValue = ctx.fieldValue;
			this.datatype = ctx.datatype;
			this.timeInMillis = ctx.timeInMillis;
			this.dd = ctx.dd;
			this.mm = ctx.mm;
			this.yyyy = ctx.yyyy;
			this.hh = ctx.hh;
			this.MM = ctx.MM;
			this.ss = ctx.ss;
			this.SSS = ctx.SSS;
			this.tz = ctx.tz;
		}
	}
	public static class DateFieldTimeOnlyContext extends DateFieldContext {
		public TimeContext time() {
			return getRuleContext(TimeContext.class,0);
		}
		public DateFieldTimeOnlyContext(DateFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterDateFieldTimeOnly(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitDateFieldTimeOnly(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitDateFieldTimeOnly(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DateFieldDateTimeContext extends DateFieldContext {
		public DateContext date() {
			return getRuleContext(DateContext.class,0);
		}
		public TimeContext time() {
			return getRuleContext(TimeContext.class,0);
		}
		public TerminalNode KWD_DAY_OF_WEEK() { return getToken(evaEnglishParser.KWD_DAY_OF_WEEK, 0); }
		public TerminalNode SYM_COMMA() { return getToken(evaEnglishParser.SYM_COMMA, 0); }
		public TerminalNode SYM_PERIOD() { return getToken(evaEnglishParser.SYM_PERIOD, 0); }
		public DateFieldDateTimeContext(DateFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterDateFieldDateTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitDateFieldDateTime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitDateFieldDateTime(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DateFieldDateOnlyContext extends DateFieldContext {
		public DateContext date() {
			return getRuleContext(DateContext.class,0);
		}
		public TerminalNode KWD_DAY_OF_WEEK() { return getToken(evaEnglishParser.KWD_DAY_OF_WEEK, 0); }
		public TerminalNode SYM_COMMA() { return getToken(evaEnglishParser.SYM_COMMA, 0); }
		public TerminalNode SYM_PERIOD() { return getToken(evaEnglishParser.SYM_PERIOD, 0); }
		public DateFieldDateOnlyContext(DateFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterDateFieldDateOnly(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitDateFieldDateOnly(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitDateFieldDateOnly(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DateFieldContext dateField() throws RecognitionException {
		DateFieldContext _localctx = new DateFieldContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_dateField);
		int _la;
		try {
			setState(279);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				_localctx = new DateFieldDateTimeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(254);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_DAY_OF_WEEK) {
					{
					setState(253);
					match(KWD_DAY_OF_WEEK);
					}
				}

				setState(257);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SYM_PERIOD || _la==SYM_COMMA) {
					{
					setState(256);
					_la = _input.LA(1);
					if ( !(_la==SYM_PERIOD || _la==SYM_COMMA) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(259);
				date();
				setState(260);
				time(0);
				}
				break;
			case 2:
				_localctx = new DateFieldDateTimeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(263);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_DAY_OF_WEEK) {
					{
					setState(262);
					match(KWD_DAY_OF_WEEK);
					}
				}

				setState(266);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SYM_PERIOD || _la==SYM_COMMA) {
					{
					setState(265);
					_la = _input.LA(1);
					if ( !(_la==SYM_PERIOD || _la==SYM_COMMA) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(268);
				time(0);
				setState(269);
				date();
				}
				break;
			case 3:
				_localctx = new DateFieldDateOnlyContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(272);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_DAY_OF_WEEK) {
					{
					setState(271);
					match(KWD_DAY_OF_WEEK);
					}
				}

				setState(275);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SYM_PERIOD || _la==SYM_COMMA) {
					{
					setState(274);
					_la = _input.LA(1);
					if ( !(_la==SYM_PERIOD || _la==SYM_COMMA) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(277);
				date();
				}
				break;
			case 4:
				_localctx = new DateFieldTimeOnlyContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(278);
				time(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WhereClauseContentContext extends ParserRuleContext {
		public String value;
		public int fieldType;
		public List<List<String>> nuoFields;
		public WhereClauseContentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereClauseContent; }
	 
		public WhereClauseContentContext() { }
		public void copyFrom(WhereClauseContentContext ctx) {
			super.copyFrom(ctx);
			this.value = ctx.value;
			this.fieldType = ctx.fieldType;
			this.nuoFields = ctx.nuoFields;
		}
	}
	public static class WhereLtEqContext extends WhereClauseContentContext {
		public NegativeVerbContext prefixNegVerb;
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public TerminalNode KWD_WITHOUT() { return getToken(evaEnglishParser.KWD_WITHOUT, 0); }
		public TerminalNode KWD_LT_EQ() { return getToken(evaEnglishParser.KWD_LT_EQ, 0); }
		public TerminalNode KWD_UNTIL() { return getToken(evaEnglishParser.KWD_UNTIL, 0); }
		public TerminalNode KWD_TILL() { return getToken(evaEnglishParser.KWD_TILL, 0); }
		public List<TerminalNode> KWD_OR() { return getTokens(evaEnglishParser.KWD_OR); }
		public TerminalNode KWD_OR(int i) {
			return getToken(evaEnglishParser.KWD_OR, i);
		}
		public List<NegativeVerbContext> negativeVerb() {
			return getRuleContexts(NegativeVerbContext.class);
		}
		public NegativeVerbContext negativeVerb(int i) {
			return getRuleContext(NegativeVerbContext.class,i);
		}
		public TerminalNode POS_DET() { return getToken(evaEnglishParser.POS_DET, 0); }
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public TerminalNode KWD_GT() { return getToken(evaEnglishParser.KWD_GT, 0); }
		public TerminalNode KWD_GT_TIME() { return getToken(evaEnglishParser.KWD_GT_TIME, 0); }
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public List<CriteriaClauseContext> criteriaClause() {
			return getRuleContexts(CriteriaClauseContext.class);
		}
		public CriteriaClauseContext criteriaClause(int i) {
			return getRuleContext(CriteriaClauseContext.class,i);
		}
		public WhereLtEqContext(WhereClauseContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereLtEq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereLtEq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereLtEq(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereEqContext extends WhereClauseContentContext {
		public NegativeVerbContext prefixNegVerb;
		public NegativeVerbContext subNegVerb;
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public TerminalNode KWD_WITHOUT() { return getToken(evaEnglishParser.KWD_WITHOUT, 0); }
		public List<NegativeVerbContext> negativeVerb() {
			return getRuleContexts(NegativeVerbContext.class);
		}
		public NegativeVerbContext negativeVerb(int i) {
			return getRuleContext(NegativeVerbContext.class,i);
		}
		public TerminalNode KWD_EQUAL_TO() { return getToken(evaEnglishParser.KWD_EQUAL_TO, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public TerminalNode POS_DET() { return getToken(evaEnglishParser.POS_DET, 0); }
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public List<TerminalNode> SYM_COMMA() { return getTokens(evaEnglishParser.SYM_COMMA); }
		public TerminalNode SYM_COMMA(int i) {
			return getToken(evaEnglishParser.SYM_COMMA, i);
		}
		public List<TerminalNode> KWD_OR() { return getTokens(evaEnglishParser.KWD_OR); }
		public TerminalNode KWD_OR(int i) {
			return getToken(evaEnglishParser.KWD_OR, i);
		}
		public List<CriteriaClauseContext> criteriaClause() {
			return getRuleContexts(CriteriaClauseContext.class);
		}
		public CriteriaClauseContext criteriaClause(int i) {
			return getRuleContext(CriteriaClauseContext.class,i);
		}
		public WhereEqContext(WhereClauseContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereEq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereEq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereEq(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereBetweenContext extends WhereClauseContentContext {
		public NegativeVerbContext prefixNegVerb;
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public TerminalNode KWD_AND() { return getToken(evaEnglishParser.KWD_AND, 0); }
		public TerminalNode KWD_BETWEEN() { return getToken(evaEnglishParser.KWD_BETWEEN, 0); }
		public TerminalNode KWD_OUTSIDE() { return getToken(evaEnglishParser.KWD_OUTSIDE, 0); }
		public TerminalNode KWD_WITHOUT() { return getToken(evaEnglishParser.KWD_WITHOUT, 0); }
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public NegativeVerbContext negativeVerb() {
			return getRuleContext(NegativeVerbContext.class,0);
		}
		public TerminalNode POS_DET() { return getToken(evaEnglishParser.POS_DET, 0); }
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public TerminalNode KWD_GT_TIME() { return getToken(evaEnglishParser.KWD_GT_TIME, 0); }
		public TerminalNode KWD_GT_EQ_TIME() { return getToken(evaEnglishParser.KWD_GT_EQ_TIME, 0); }
		public TerminalNode KWD_LT_TIME() { return getToken(evaEnglishParser.KWD_LT_TIME, 0); }
		public TerminalNode KWD_UNTIL() { return getToken(evaEnglishParser.KWD_UNTIL, 0); }
		public TerminalNode KWD_TILL() { return getToken(evaEnglishParser.KWD_TILL, 0); }
		public TerminalNode KWD_BUT() { return getToken(evaEnglishParser.KWD_BUT, 0); }
		public WhereBetweenContext(WhereClauseContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereBetween(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereBetween(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereBetween(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereGtContext extends WhereClauseContentContext {
		public NegativeVerbContext prefixNegVerb;
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public TerminalNode KWD_WITHOUT() { return getToken(evaEnglishParser.KWD_WITHOUT, 0); }
		public TerminalNode KWD_GT() { return getToken(evaEnglishParser.KWD_GT, 0); }
		public TerminalNode KWD_GT_TIME() { return getToken(evaEnglishParser.KWD_GT_TIME, 0); }
		public List<TerminalNode> KWD_OR() { return getTokens(evaEnglishParser.KWD_OR); }
		public TerminalNode KWD_OR(int i) {
			return getToken(evaEnglishParser.KWD_OR, i);
		}
		public List<NegativeVerbContext> negativeVerb() {
			return getRuleContexts(NegativeVerbContext.class);
		}
		public NegativeVerbContext negativeVerb(int i) {
			return getRuleContext(NegativeVerbContext.class,i);
		}
		public TerminalNode POS_DET() { return getToken(evaEnglishParser.POS_DET, 0); }
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public TerminalNode KWD_LT_EQ() { return getToken(evaEnglishParser.KWD_LT_EQ, 0); }
		public TerminalNode KWD_UNTIL() { return getToken(evaEnglishParser.KWD_UNTIL, 0); }
		public TerminalNode KWD_TILL() { return getToken(evaEnglishParser.KWD_TILL, 0); }
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public List<CriteriaClauseContext> criteriaClause() {
			return getRuleContexts(CriteriaClauseContext.class);
		}
		public CriteriaClauseContext criteriaClause(int i) {
			return getRuleContext(CriteriaClauseContext.class,i);
		}
		public WhereGtContext(WhereClauseContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereGt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereGt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereGt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereDateEqContext extends WhereClauseContentContext {
		public NegativeVerbContext prefixNegVerb;
		public NegativeVerbContext subNegVerb;
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public DateFieldContext dateField() {
			return getRuleContext(DateFieldContext.class,0);
		}
		public TerminalNode KWD_DATE_EQUAL_TO() { return getToken(evaEnglishParser.KWD_DATE_EQUAL_TO, 0); }
		public TerminalNode KWD_FROM() { return getToken(evaEnglishParser.KWD_FROM, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public TerminalNode KWD_WITHOUT() { return getToken(evaEnglishParser.KWD_WITHOUT, 0); }
		public List<TerminalNode> KWD_OR() { return getTokens(evaEnglishParser.KWD_OR); }
		public TerminalNode KWD_OR(int i) {
			return getToken(evaEnglishParser.KWD_OR, i);
		}
		public List<NegativeVerbContext> negativeVerb() {
			return getRuleContexts(NegativeVerbContext.class);
		}
		public NegativeVerbContext negativeVerb(int i) {
			return getRuleContext(NegativeVerbContext.class,i);
		}
		public TerminalNode POS_DET() { return getToken(evaEnglishParser.POS_DET, 0); }
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public List<CriteriaClauseContext> criteriaClause() {
			return getRuleContexts(CriteriaClauseContext.class);
		}
		public CriteriaClauseContext criteriaClause(int i) {
			return getRuleContext(CriteriaClauseContext.class,i);
		}
		public WhereDateEqContext(WhereClauseContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereDateEq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereDateEq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereDateEq(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereGtEqContext extends WhereClauseContentContext {
		public NegativeVerbContext prefixNegVerb;
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public TerminalNode KWD_WITHOUT() { return getToken(evaEnglishParser.KWD_WITHOUT, 0); }
		public TerminalNode KWD_GT_EQ() { return getToken(evaEnglishParser.KWD_GT_EQ, 0); }
		public TerminalNode KWD_GT_EQ_TIME() { return getToken(evaEnglishParser.KWD_GT_EQ_TIME, 0); }
		public List<TerminalNode> KWD_OR() { return getTokens(evaEnglishParser.KWD_OR); }
		public TerminalNode KWD_OR(int i) {
			return getToken(evaEnglishParser.KWD_OR, i);
		}
		public List<NegativeVerbContext> negativeVerb() {
			return getRuleContexts(NegativeVerbContext.class);
		}
		public NegativeVerbContext negativeVerb(int i) {
			return getRuleContext(NegativeVerbContext.class,i);
		}
		public TerminalNode POS_DET() { return getToken(evaEnglishParser.POS_DET, 0); }
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public TerminalNode KWD_LT() { return getToken(evaEnglishParser.KWD_LT, 0); }
		public TerminalNode KWD_LT_TIME() { return getToken(evaEnglishParser.KWD_LT_TIME, 0); }
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public List<CriteriaClauseContext> criteriaClause() {
			return getRuleContexts(CriteriaClauseContext.class);
		}
		public CriteriaClauseContext criteriaClause(int i) {
			return getRuleContext(CriteriaClauseContext.class,i);
		}
		public WhereGtEqContext(WhereClauseContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereGtEq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereGtEq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereGtEq(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereDefaultContext extends WhereClauseContentContext {
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public TerminalNode KWD_AVAILABLE() { return getToken(evaEnglishParser.KWD_AVAILABLE, 0); }
		public NegativeVerbContext negativeVerb() {
			return getRuleContext(NegativeVerbContext.class,0);
		}
		public UnavailablePhrasesContext unavailablePhrases() {
			return getRuleContext(UnavailablePhrasesContext.class,0);
		}
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public WhereDefaultContext(WhereClauseContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereDefault(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereDurationContext extends WhereClauseContentContext {
		public NegativeVerbContext prefixNegVerb;
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public DurationFieldContext durationField() {
			return getRuleContext(DurationFieldContext.class,0);
		}
		public TerminalNode KWD_WITHOUT() { return getToken(evaEnglishParser.KWD_WITHOUT, 0); }
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public NegativeVerbContext negativeVerb() {
			return getRuleContext(NegativeVerbContext.class,0);
		}
		public TerminalNode POS_DET() { return getToken(evaEnglishParser.POS_DET, 0); }
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public TerminalNode KWD_FROM() { return getToken(evaEnglishParser.KWD_FROM, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public TerminalNode KWD_DURING() { return getToken(evaEnglishParser.KWD_DURING, 0); }
		public WhereDurationContext(WhereClauseContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereDuration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereDuration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereDuration(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereDefaultNegativeContext extends WhereClauseContentContext {
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public NegativeVerbContext negativeVerb() {
			return getRuleContext(NegativeVerbContext.class,0);
		}
		public TerminalNode KWD_WITHOUT() { return getToken(evaEnglishParser.KWD_WITHOUT, 0); }
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public UnavailablePhrasesContext unavailablePhrases() {
			return getRuleContext(UnavailablePhrasesContext.class,0);
		}
		public TerminalNode KWD_AVAILABLE() { return getToken(evaEnglishParser.KWD_AVAILABLE, 0); }
		public TerminalNode KWD_ANY() { return getToken(evaEnglishParser.KWD_ANY, 0); }
		public WhereDefaultNegativeContext(WhereClauseContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereDefaultNegative(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereDefaultNegative(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereDefaultNegative(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereLtContext extends WhereClauseContentContext {
		public NegativeVerbContext prefixNegVerb;
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public TerminalNode KWD_WITHOUT() { return getToken(evaEnglishParser.KWD_WITHOUT, 0); }
		public TerminalNode KWD_LT() { return getToken(evaEnglishParser.KWD_LT, 0); }
		public TerminalNode KWD_LT_TIME() { return getToken(evaEnglishParser.KWD_LT_TIME, 0); }
		public List<TerminalNode> KWD_OR() { return getTokens(evaEnglishParser.KWD_OR); }
		public TerminalNode KWD_OR(int i) {
			return getToken(evaEnglishParser.KWD_OR, i);
		}
		public List<NegativeVerbContext> negativeVerb() {
			return getRuleContexts(NegativeVerbContext.class);
		}
		public NegativeVerbContext negativeVerb(int i) {
			return getRuleContext(NegativeVerbContext.class,i);
		}
		public TerminalNode POS_DET() { return getToken(evaEnglishParser.POS_DET, 0); }
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public TerminalNode KWD_GT_EQ() { return getToken(evaEnglishParser.KWD_GT_EQ, 0); }
		public TerminalNode KWD_GT_EQ_TIME() { return getToken(evaEnglishParser.KWD_GT_EQ_TIME, 0); }
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public List<CriteriaClauseContext> criteriaClause() {
			return getRuleContexts(CriteriaClauseContext.class);
		}
		public CriteriaClauseContext criteriaClause(int i) {
			return getRuleContext(CriteriaClauseContext.class,i);
		}
		public WhereLtContext(WhereClauseContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereLt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereLt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereLt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhereStringFunctionsContext extends WhereClauseContentContext {
		public NegativeVerbContext prefixNegVerb;
		public NegativeVerbContext subNegVerb;
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public TerminalNode KWD_STARTS_WITH() { return getToken(evaEnglishParser.KWD_STARTS_WITH, 0); }
		public TerminalNode KWD_ENDS_WITH() { return getToken(evaEnglishParser.KWD_ENDS_WITH, 0); }
		public TerminalNode KWD_CONTAINS() { return getToken(evaEnglishParser.KWD_CONTAINS, 0); }
		public TerminalNode KWD_WITHOUT() { return getToken(evaEnglishParser.KWD_WITHOUT, 0); }
		public List<NegativeVerbContext> negativeVerb() {
			return getRuleContexts(NegativeVerbContext.class);
		}
		public NegativeVerbContext negativeVerb(int i) {
			return getRuleContext(NegativeVerbContext.class,i);
		}
		public TerminalNode POS_DET() { return getToken(evaEnglishParser.POS_DET, 0); }
		public TerminalNode POS_WH_WORDS() { return getToken(evaEnglishParser.POS_WH_WORDS, 0); }
		public List<TerminalNode> SYM_COMMA() { return getTokens(evaEnglishParser.SYM_COMMA); }
		public TerminalNode SYM_COMMA(int i) {
			return getToken(evaEnglishParser.SYM_COMMA, i);
		}
		public List<TerminalNode> KWD_OR() { return getTokens(evaEnglishParser.KWD_OR); }
		public TerminalNode KWD_OR(int i) {
			return getToken(evaEnglishParser.KWD_OR, i);
		}
		public PositiveVerbContext positiveVerb() {
			return getRuleContext(PositiveVerbContext.class,0);
		}
		public List<CriteriaClauseContext> criteriaClause() {
			return getRuleContexts(CriteriaClauseContext.class);
		}
		public CriteriaClauseContext criteriaClause(int i) {
			return getRuleContext(CriteriaClauseContext.class,i);
		}
		public WhereStringFunctionsContext(WhereClauseContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterWhereStringFunctions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitWhereStringFunctions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitWhereStringFunctions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhereClauseContentContext whereClauseContent() throws RecognitionException {
		WhereClauseContentContext _localctx = new WhereClauseContentContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_whereClauseContent);
		int _la;
		try {
			int _alt;
			setState(558);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,107,_ctx) ) {
			case 1:
				_localctx = new WhereBetweenContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(283);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
				case 1:
					{
					setState(281);
					match(KWD_WITHOUT);
					}
					break;
				case 2:
					{
					setState(282);
					((WhereBetweenContext)_localctx).prefixNegVerb = negativeVerb();
					}
					break;
				}
				setState(285);
				field(0);
				setState(287);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) {
					{
					setState(286);
					_la = _input.LA(1);
					if ( !(_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(290);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0)) {
					{
					setState(289);
					positiveVerb();
					}
				}

				setState(292);
				_la = _input.LA(1);
				if ( !(_la==KWD_BETWEEN || _la==KWD_OUTSIDE) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(293);
				field(0);
				setState(294);
				match(KWD_AND);
				setState(295);
				field(0);
				}
				break;
			case 2:
				_localctx = new WhereBetweenContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(299);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
				case 1:
					{
					setState(297);
					match(KWD_WITHOUT);
					}
					break;
				case 2:
					{
					setState(298);
					((WhereBetweenContext)_localctx).prefixNegVerb = negativeVerb();
					}
					break;
				}
				setState(301);
				field(0);
				setState(303);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) {
					{
					setState(302);
					_la = _input.LA(1);
					if ( !(_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(306);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0)) {
					{
					setState(305);
					positiveVerb();
					}
				}

				setState(308);
				_la = _input.LA(1);
				if ( !(_la==KWD_GT_TIME || _la==KWD_GT_EQ_TIME) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(309);
				field(0);
				setState(311);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_AND || _la==KWD_BUT) {
					{
					setState(310);
					_la = _input.LA(1);
					if ( !(_la==KWD_AND || _la==KWD_BUT) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(313);
				_la = _input.LA(1);
				if ( !(_la==KWD_LT_TIME || _la==KWD_UNTIL || _la==KWD_TILL) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(314);
				field(0);
				}
				break;
			case 3:
				_localctx = new WhereGtEqContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(318);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
				case 1:
					{
					setState(316);
					match(KWD_WITHOUT);
					}
					break;
				case 2:
					{
					setState(317);
					((WhereGtEqContext)_localctx).prefixNegVerb = negativeVerb();
					}
					break;
				}
				setState(320);
				field(0);
				setState(322);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) {
					{
					setState(321);
					_la = _input.LA(1);
					if ( !(_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(331);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
				case 1:
					{
					{
					setState(324);
					negativeVerb();
					setState(325);
					_la = _input.LA(1);
					if ( !(_la==KWD_LT || _la==KWD_LT_TIME) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					break;
				case 2:
					{
					setState(328);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0)) {
						{
						setState(327);
						positiveVerb();
						}
					}

					setState(330);
					_la = _input.LA(1);
					if ( !(_la==KWD_GT_EQ || _la==KWD_GT_EQ_TIME) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(333);
				field(0);
				setState(341);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,59,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(334);
						match(KWD_OR);
						setState(337);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
						case 1:
							{
							setState(335);
							criteriaClause(0);
							}
							break;
						case 2:
							{
							setState(336);
							field(0);
							}
							break;
						}
						}
						} 
					}
					setState(343);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,59,_ctx);
				}
				}
				break;
			case 4:
				_localctx = new WhereLtEqContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(346);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
				case 1:
					{
					setState(344);
					match(KWD_WITHOUT);
					}
					break;
				case 2:
					{
					setState(345);
					((WhereLtEqContext)_localctx).prefixNegVerb = negativeVerb();
					}
					break;
				}
				setState(348);
				field(0);
				setState(350);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) {
					{
					setState(349);
					_la = _input.LA(1);
					if ( !(_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(359);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
				case 1:
					{
					{
					setState(352);
					negativeVerb();
					setState(353);
					_la = _input.LA(1);
					if ( !(_la==KWD_GT || _la==KWD_GT_TIME) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					break;
				case 2:
					{
					setState(356);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0)) {
						{
						setState(355);
						positiveVerb();
						}
					}

					setState(358);
					_la = _input.LA(1);
					if ( !(_la==KWD_LT_EQ || _la==KWD_UNTIL || _la==KWD_TILL) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(361);
				field(0);
				setState(369);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,65,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(362);
						match(KWD_OR);
						setState(365);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
						case 1:
							{
							setState(363);
							criteriaClause(0);
							}
							break;
						case 2:
							{
							setState(364);
							field(0);
							}
							break;
						}
						}
						} 
					}
					setState(371);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,65,_ctx);
				}
				}
				break;
			case 5:
				_localctx = new WhereGtContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(374);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
				case 1:
					{
					setState(372);
					match(KWD_WITHOUT);
					}
					break;
				case 2:
					{
					setState(373);
					((WhereGtContext)_localctx).prefixNegVerb = negativeVerb();
					}
					break;
				}
				setState(376);
				field(0);
				setState(378);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) {
					{
					setState(377);
					_la = _input.LA(1);
					if ( !(_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(387);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
				case 1:
					{
					{
					setState(380);
					negativeVerb();
					setState(381);
					_la = _input.LA(1);
					if ( !(_la==KWD_LT_EQ || _la==KWD_UNTIL || _la==KWD_TILL) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					break;
				case 2:
					{
					setState(384);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0)) {
						{
						setState(383);
						positiveVerb();
						}
					}

					setState(386);
					_la = _input.LA(1);
					if ( !(_la==KWD_GT || _la==KWD_GT_TIME) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(389);
				field(0);
				setState(397);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,71,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(390);
						match(KWD_OR);
						setState(393);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
						case 1:
							{
							setState(391);
							criteriaClause(0);
							}
							break;
						case 2:
							{
							setState(392);
							field(0);
							}
							break;
						}
						}
						} 
					}
					setState(399);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,71,_ctx);
				}
				}
				break;
			case 6:
				_localctx = new WhereLtContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(402);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
				case 1:
					{
					setState(400);
					match(KWD_WITHOUT);
					}
					break;
				case 2:
					{
					setState(401);
					((WhereLtContext)_localctx).prefixNegVerb = negativeVerb();
					}
					break;
				}
				setState(404);
				field(0);
				setState(406);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) {
					{
					setState(405);
					_la = _input.LA(1);
					if ( !(_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(415);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
				case 1:
					{
					{
					setState(408);
					negativeVerb();
					setState(409);
					_la = _input.LA(1);
					if ( !(_la==KWD_GT_EQ || _la==KWD_GT_EQ_TIME) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					break;
				case 2:
					{
					setState(412);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0)) {
						{
						setState(411);
						positiveVerb();
						}
					}

					setState(414);
					_la = _input.LA(1);
					if ( !(_la==KWD_LT || _la==KWD_LT_TIME) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(417);
				field(0);
				setState(425);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,77,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(418);
						match(KWD_OR);
						setState(421);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
						case 1:
							{
							setState(419);
							criteriaClause(0);
							}
							break;
						case 2:
							{
							setState(420);
							field(0);
							}
							break;
						}
						}
						} 
					}
					setState(427);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,77,_ctx);
				}
				}
				break;
			case 7:
				_localctx = new WhereDurationContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(430);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,78,_ctx) ) {
				case 1:
					{
					setState(428);
					match(KWD_WITHOUT);
					}
					break;
				case 2:
					{
					setState(429);
					((WhereDurationContext)_localctx).prefixNegVerb = negativeVerb();
					}
					break;
				}
				setState(432);
				field(0);
				setState(434);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) {
					{
					setState(433);
					_la = _input.LA(1);
					if ( !(_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(437);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0)) {
					{
					setState(436);
					positiveVerb();
					}
				}

				setState(440);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 275)) & ~0x3f) == 0 && ((1L << (_la - 275)) & ((1L << (KWD_IN - 275)) | (1L << (KWD_FROM - 275)) | (1L << (KWD_DURING - 275)))) != 0)) {
					{
					setState(439);
					_la = _input.LA(1);
					if ( !(((((_la - 275)) & ~0x3f) == 0 && ((1L << (_la - 275)) & ((1L << (KWD_IN - 275)) | (1L << (KWD_FROM - 275)) | (1L << (KWD_DURING - 275)))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(442);
				durationField();
				}
				break;
			case 8:
				_localctx = new WhereDateEqContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(446);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
				case 1:
					{
					setState(444);
					match(KWD_WITHOUT);
					}
					break;
				case 2:
					{
					setState(445);
					((WhereDateEqContext)_localctx).prefixNegVerb = negativeVerb();
					}
					break;
				}
				setState(448);
				field(0);
				setState(450);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) {
					{
					setState(449);
					_la = _input.LA(1);
					if ( !(_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(453);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB_NEG) | (1L << POS_VERB_HAVE_NEG) | (1L << POS_VERB_HAVE))) != 0) || _la==KWD_NO) {
					{
					setState(452);
					((WhereDateEqContext)_localctx).subNegVerb = negativeVerb();
					}
				}

				setState(455);
				_la = _input.LA(1);
				if ( !(_la==KWD_DATE_EQUAL_TO || _la==KWD_IN || _la==KWD_FROM) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(456);
				dateField();
				setState(464);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,86,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(457);
						match(KWD_OR);
						setState(460);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,85,_ctx) ) {
						case 1:
							{
							setState(458);
							criteriaClause(0);
							}
							break;
						case 2:
							{
							setState(459);
							field(0);
							}
							break;
						}
						}
						} 
					}
					setState(466);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,86,_ctx);
				}
				}
				break;
			case 9:
				_localctx = new WhereStringFunctionsContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(469);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
				case 1:
					{
					setState(467);
					match(KWD_WITHOUT);
					}
					break;
				case 2:
					{
					setState(468);
					((WhereStringFunctionsContext)_localctx).prefixNegVerb = negativeVerb();
					}
					break;
				}
				setState(471);
				field(0);
				setState(473);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==POS_DET || _la==POS_WH_WORDS) {
					{
					setState(472);
					_la = _input.LA(1);
					if ( !(_la==POS_DET || _la==POS_WH_WORDS) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(481);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,91,_ctx) ) {
				case 1:
					{
					setState(476);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0)) {
						{
						setState(475);
						positiveVerb();
						}
					}

					}
					break;
				case 2:
					{
					setState(479);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB_NEG) | (1L << POS_VERB_HAVE_NEG) | (1L << POS_VERB_HAVE))) != 0) || _la==KWD_NO) {
						{
						setState(478);
						((WhereStringFunctionsContext)_localctx).subNegVerb = negativeVerb();
						}
					}

					}
					break;
				}
				setState(483);
				_la = _input.LA(1);
				if ( !(((((_la - 135)) & ~0x3f) == 0 && ((1L << (_la - 135)) & ((1L << (KWD_STARTS_WITH - 135)) | (1L << (KWD_ENDS_WITH - 135)) | (1L << (KWD_CONTAINS - 135)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(484);
				field(0);
				setState(492);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,93,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(485);
						_la = _input.LA(1);
						if ( !(_la==SYM_COMMA || _la==KWD_OR) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(488);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,92,_ctx) ) {
						case 1:
							{
							setState(486);
							criteriaClause(0);
							}
							break;
						case 2:
							{
							setState(487);
							field(0);
							}
							break;
						}
						}
						} 
					}
					setState(494);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,93,_ctx);
				}
				}
				break;
			case 10:
				_localctx = new WhereDefaultContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(496);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,94,_ctx) ) {
				case 1:
					{
					setState(495);
					match(KWD_WITH);
					}
					break;
				}
				setState(498);
				fieldContent();
				setState(506);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,96,_ctx) ) {
				case 1:
					{
					setState(500);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0)) {
						{
						setState(499);
						positiveVerb();
						}
					}

					setState(502);
					match(KWD_AVAILABLE);
					}
					break;
				case 2:
					{
					setState(503);
					negativeVerb();
					setState(504);
					unavailablePhrases();
					}
					break;
				}
				}
				break;
			case 11:
				_localctx = new WhereDefaultNegativeContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(526);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,100,_ctx) ) {
				case 1:
					{
					setState(514);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case KWD_WITH:
						{
						setState(508);
						match(KWD_WITH);
						setState(509);
						negativeVerb();
						}
						break;
					case KWD_WITHOUT:
						{
						setState(510);
						match(KWD_WITHOUT);
						setState(512);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
						case 1:
							{
							setState(511);
							match(KWD_ANY);
							}
							break;
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(516);
					fieldContent();
					}
					break;
				case 2:
					{
					setState(517);
					fieldContent();
					setState(524);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,99,_ctx) ) {
					case 1:
						{
						setState(518);
						positiveVerb();
						setState(519);
						unavailablePhrases();
						}
						break;
					case 2:
						{
						setState(521);
						negativeVerb();
						setState(522);
						match(KWD_AVAILABLE);
						}
						break;
					}
					}
					break;
				}
				}
				break;
			case 12:
				_localctx = new WhereEqContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(530);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,101,_ctx) ) {
				case 1:
					{
					setState(528);
					match(KWD_WITHOUT);
					}
					break;
				case 2:
					{
					setState(529);
					((WhereEqContext)_localctx).prefixNegVerb = negativeVerb();
					}
					break;
				}
				setState(532);
				field(0);
				setState(534);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) {
					{
					setState(533);
					_la = _input.LA(1);
					if ( !(_la==POS_DET || _la==POS_WH_WORDS || _la==KWD_WITH) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(545);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
				case 1:
					{
					setState(536);
					((WhereEqContext)_localctx).subNegVerb = negativeVerb();
					setState(538);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,103,_ctx) ) {
					case 1:
						{
						setState(537);
						_la = _input.LA(1);
						if ( !(_la==KWD_EQUAL_TO || _la==KWD_IN) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						break;
					}
					}
					break;
				case 2:
					{
					setState(540);
					positiveVerb();
					setState(541);
					_la = _input.LA(1);
					if ( !(_la==KWD_EQUAL_TO || _la==KWD_IN) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				case 3:
					{
					setState(543);
					positiveVerb();
					}
					break;
				case 4:
					{
					setState(544);
					_la = _input.LA(1);
					if ( !(_la==KWD_EQUAL_TO || _la==KWD_IN) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(547);
				field(0);
				setState(555);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,106,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(548);
						_la = _input.LA(1);
						if ( !(_la==SYM_COMMA || _la==KWD_OR) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(551);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,105,_ctx) ) {
						case 1:
							{
							setState(549);
							criteriaClause(0);
							}
							break;
						case 2:
							{
							setState(550);
							field(0);
							}
							break;
						}
						}
						} 
					}
					setState(557);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,106,_ctx);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NegativeVerbContext extends ParserRuleContext {
		public TerminalNode KWD_NO() { return getToken(evaEnglishParser.KWD_NO, 0); }
		public TerminalNode POS_VERB_HAVE() { return getToken(evaEnglishParser.POS_VERB_HAVE, 0); }
		public TerminalNode POS_VERB_NEG() { return getToken(evaEnglishParser.POS_VERB_NEG, 0); }
		public TerminalNode POS_VERB_HAVE_NEG() { return getToken(evaEnglishParser.POS_VERB_HAVE_NEG, 0); }
		public TerminalNode POS_VERB_BE() { return getToken(evaEnglishParser.POS_VERB_BE, 0); }
		public NegativeVerbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_negativeVerb; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterNegativeVerb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitNegativeVerb(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitNegativeVerb(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NegativeVerbContext negativeVerb() throws RecognitionException {
		NegativeVerbContext _localctx = new NegativeVerbContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_negativeVerb);
		int _la;
		try {
			setState(570);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case KWD_NO:
				enterOuterAlt(_localctx, 1);
				{
				setState(560);
				match(KWD_NO);
				}
				break;
			case POS_VERB_HAVE:
				enterOuterAlt(_localctx, 2);
				{
				setState(561);
				match(POS_VERB_HAVE);
				setState(562);
				match(KWD_NO);
				}
				break;
			case POS_VERB_NEG:
			case POS_VERB_HAVE_NEG:
				enterOuterAlt(_localctx, 3);
				{
				setState(563);
				_la = _input.LA(1);
				if ( !(_la==POS_VERB_NEG || _la==POS_VERB_HAVE_NEG) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(565);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,108,_ctx) ) {
				case 1:
					{
					setState(564);
					match(POS_VERB_BE);
					}
					break;
				}
				setState(568);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,109,_ctx) ) {
				case 1:
					{
					setState(567);
					match(POS_VERB_HAVE);
					}
					break;
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PositiveVerbContext extends ParserRuleContext {
		public TerminalNode POS_VERB() { return getToken(evaEnglishParser.POS_VERB, 0); }
		public TerminalNode POS_VERB_PREDICT() { return getToken(evaEnglishParser.POS_VERB_PREDICT, 0); }
		public List<TerminalNode> POS_VERB_HAVE() { return getTokens(evaEnglishParser.POS_VERB_HAVE); }
		public TerminalNode POS_VERB_HAVE(int i) {
			return getToken(evaEnglishParser.POS_VERB_HAVE, i);
		}
		public TerminalNode POS_VERB_BE() { return getToken(evaEnglishParser.POS_VERB_BE, 0); }
		public PositiveVerbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_positiveVerb; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterPositiveVerb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitPositiveVerb(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitPositiveVerb(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PositiveVerbContext positiveVerb() throws RecognitionException {
		PositiveVerbContext _localctx = new PositiveVerbContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_positiveVerb);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(572);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << POS_VERB) | (1L << POS_VERB_PREDICT) | (1L << POS_VERB_HAVE))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(574);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,111,_ctx) ) {
			case 1:
				{
				setState(573);
				match(POS_VERB_BE);
				}
				break;
			}
			setState(577);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,112,_ctx) ) {
			case 1:
				{
				setState(576);
				match(POS_VERB_HAVE);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComparisionPhrasesContext extends ParserRuleContext {
		public TerminalNode KWD_VS() { return getToken(evaEnglishParser.KWD_VS, 0); }
		public TerminalNode KWD_COMPARING() { return getToken(evaEnglishParser.KWD_COMPARING, 0); }
		public TerminalNode KWD_COMPARED() { return getToken(evaEnglishParser.KWD_COMPARED, 0); }
		public TerminalNode KWD_TO() { return getToken(evaEnglishParser.KWD_TO, 0); }
		public TerminalNode KWD_WITH() { return getToken(evaEnglishParser.KWD_WITH, 0); }
		public ComparisionPhrasesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparisionPhrases; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterComparisionPhrases(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitComparisionPhrases(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitComparisionPhrases(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparisionPhrasesContext comparisionPhrases() throws RecognitionException {
		ComparisionPhrasesContext _localctx = new ComparisionPhrasesContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_comparisionPhrases);
		int _la;
		try {
			setState(584);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case KWD_VS:
				enterOuterAlt(_localctx, 1);
				{
				setState(579);
				match(KWD_VS);
				}
				break;
			case KWD_COMPARING:
			case KWD_COMPARED:
				enterOuterAlt(_localctx, 2);
				{
				setState(580);
				_la = _input.LA(1);
				if ( !(_la==KWD_COMPARING || _la==KWD_COMPARED) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(582);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_WITH || _la==KWD_TO) {
					{
					setState(581);
					_la = _input.LA(1);
					if ( !(_la==KWD_WITH || _la==KWD_TO) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnavailablePhrasesContext extends ParserRuleContext {
		public TerminalNode KWD_UNAVAILABLE() { return getToken(evaEnglishParser.KWD_UNAVAILABLE, 0); }
		public TerminalNode KWD_EMPTY() { return getToken(evaEnglishParser.KWD_EMPTY, 0); }
		public TerminalNode KWD_NULL() { return getToken(evaEnglishParser.KWD_NULL, 0); }
		public TerminalNode KWD_BLANK() { return getToken(evaEnglishParser.KWD_BLANK, 0); }
		public UnavailablePhrasesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unavailablePhrases; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterUnavailablePhrases(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitUnavailablePhrases(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitUnavailablePhrases(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnavailablePhrasesContext unavailablePhrases() throws RecognitionException {
		UnavailablePhrasesContext _localctx = new UnavailablePhrasesContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_unavailablePhrases);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(586);
			_la = _input.LA(1);
			if ( !(((((_la - 188)) & ~0x3f) == 0 && ((1L << (_la - 188)) & ((1L << (KWD_UNAVAILABLE - 188)) | (1L << (KWD_NULL - 188)) | (1L << (KWD_BLANK - 188)) | (1L << (KWD_EMPTY - 188)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DurationContext extends ParserRuleContext {
		public String numOfYears;
		public String numOfMonths;
		public String numOfDays;
		public String numOfHours;
		public String numOfMinutes;
		public String numOfSeconds;
		public int durationSeconds;
		public Token years;
		public Token months;
		public Token weeks;
		public Token days;
		public Token hours;
		public Token minutes;
		public Token seconds;
		public TerminalNode KWD_YEAR() { return getToken(evaEnglishParser.KWD_YEAR, 0); }
		public TerminalNode NUMBER() { return getToken(evaEnglishParser.NUMBER, 0); }
		public TerminalNode KWD_MONTH() { return getToken(evaEnglishParser.KWD_MONTH, 0); }
		public TerminalNode KWD_WEEK() { return getToken(evaEnglishParser.KWD_WEEK, 0); }
		public TerminalNode KWD_DAY() { return getToken(evaEnglishParser.KWD_DAY, 0); }
		public TerminalNode KWD_HOUR() { return getToken(evaEnglishParser.KWD_HOUR, 0); }
		public TerminalNode KWD_MINUTE() { return getToken(evaEnglishParser.KWD_MINUTE, 0); }
		public TerminalNode KWD_SECOND() { return getToken(evaEnglishParser.KWD_SECOND, 0); }
		public List<DurationContext> duration() {
			return getRuleContexts(DurationContext.class);
		}
		public DurationContext duration(int i) {
			return getRuleContext(DurationContext.class,i);
		}
		public List<TerminalNode> KWD_AND() { return getTokens(evaEnglishParser.KWD_AND); }
		public TerminalNode KWD_AND(int i) {
			return getToken(evaEnglishParser.KWD_AND, i);
		}
		public DurationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_duration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterDuration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitDuration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitDuration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DurationContext duration() throws RecognitionException {
		return duration(0);
	}

	private DurationContext duration(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		DurationContext _localctx = new DurationContext(_ctx, _parentState);
		DurationContext _prevctx = _localctx;
		int _startState = 22;
		enterRecursionRule(_localctx, 22, RULE_duration, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(617);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,122,_ctx) ) {
			case 1:
				{
				setState(590);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(589);
					((DurationContext)_localctx).years = match(NUMBER);
					}
				}

				setState(592);
				match(KWD_YEAR);
				}
				break;
			case 2:
				{
				setState(594);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(593);
					((DurationContext)_localctx).months = match(NUMBER);
					}
				}

				setState(596);
				match(KWD_MONTH);
				}
				break;
			case 3:
				{
				setState(598);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(597);
					((DurationContext)_localctx).weeks = match(NUMBER);
					}
				}

				setState(600);
				match(KWD_WEEK);
				}
				break;
			case 4:
				{
				setState(602);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(601);
					((DurationContext)_localctx).days = match(NUMBER);
					}
				}

				setState(604);
				match(KWD_DAY);
				}
				break;
			case 5:
				{
				setState(606);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(605);
					((DurationContext)_localctx).hours = match(NUMBER);
					}
				}

				setState(608);
				match(KWD_HOUR);
				}
				break;
			case 6:
				{
				setState(610);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(609);
					((DurationContext)_localctx).minutes = match(NUMBER);
					}
				}

				setState(612);
				match(KWD_MINUTE);
				}
				break;
			case 7:
				{
				setState(614);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(613);
					((DurationContext)_localctx).seconds = match(NUMBER);
					}
				}

				setState(616);
				match(KWD_SECOND);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(630);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,125,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new DurationContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_duration);
					setState(619);
					if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
					setState(624); 
					_errHandler.sync(this);
					_alt = 1;
					do {
						switch (_alt) {
						case 1:
							{
							{
							setState(621);
							_errHandler.sync(this);
							_la = _input.LA(1);
							if (_la==KWD_AND) {
								{
								setState(620);
								match(KWD_AND);
								}
							}

							setState(623);
							duration(0);
							}
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(626); 
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,124,_ctx);
					} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
					}
					} 
				}
				setState(632);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,125,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class DurationFieldContext extends ParserRuleContext {
		public String value;
		public int fieldType;
		public long timeInMillis;
		public int durationSeconds;
		public DurationFieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_durationField; }
	 
		public DurationFieldContext() { }
		public void copyFrom(DurationFieldContext ctx) {
			super.copyFrom(ctx);
			this.value = ctx.value;
			this.fieldType = ctx.fieldType;
			this.timeInMillis = ctx.timeInMillis;
			this.durationSeconds = ctx.durationSeconds;
		}
	}
	public static class DurationFieldPastContext extends DurationFieldContext {
		public DurationContext duration() {
			return getRuleContext(DurationContext.class,0);
		}
		public TerminalNode KWD_LAST() { return getToken(evaEnglishParser.KWD_LAST, 0); }
		public TerminalNode KWD_PAST() { return getToken(evaEnglishParser.KWD_PAST, 0); }
		public TerminalNode KWD_AGO() { return getToken(evaEnglishParser.KWD_AGO, 0); }
		public DurationFieldPastContext(DurationFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterDurationFieldPast(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitDurationFieldPast(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitDurationFieldPast(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DurationFieldNextContext extends DurationFieldContext {
		public TerminalNode KWD_NEXT() { return getToken(evaEnglishParser.KWD_NEXT, 0); }
		public DurationContext duration() {
			return getRuleContext(DurationContext.class,0);
		}
		public TerminalNode KWD_FROM() { return getToken(evaEnglishParser.KWD_FROM, 0); }
		public TerminalNode KWD_NOW() { return getToken(evaEnglishParser.KWD_NOW, 0); }
		public TerminalNode WS() { return getToken(evaEnglishParser.WS, 0); }
		public DurationFieldNextContext(DurationFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterDurationFieldNext(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitDurationFieldNext(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitDurationFieldNext(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DurationFieldContext durationField() throws RecognitionException {
		DurationFieldContext _localctx = new DurationFieldContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_durationField);
		int _la;
		try {
			setState(647);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,127,_ctx) ) {
			case 1:
				_localctx = new DurationFieldPastContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(633);
				_la = _input.LA(1);
				if ( !(_la==KWD_PAST || _la==KWD_LAST) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(634);
				duration(0);
				}
				break;
			case 2:
				_localctx = new DurationFieldPastContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(635);
				duration(0);
				setState(636);
				match(KWD_AGO);
				}
				break;
			case 3:
				_localctx = new DurationFieldNextContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(638);
				match(KWD_NEXT);
				setState(639);
				duration(0);
				}
				break;
			case 4:
				_localctx = new DurationFieldNextContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(640);
				duration(0);
				setState(641);
				match(KWD_FROM);
				setState(643);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(642);
					match(WS);
					}
				}

				setState(645);
				match(KWD_NOW);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DateContext extends ParserRuleContext {
		public int yyyy;
		public int mm;
		public int dd;
		public DateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_date; }
	 
		public DateContext() { }
		public void copyFrom(DateContext ctx) {
			super.copyFrom(ctx);
			this.yyyy = ctx.yyyy;
			this.mm = ctx.mm;
			this.dd = ctx.dd;
		}
	}
	public static class DateAdjNumContext extends DateContext {
		public Token year;
		public TerminalNode KWD_YEAR() { return getToken(evaEnglishParser.KWD_YEAR, 0); }
		public TerminalNode NUMBER() { return getToken(evaEnglishParser.NUMBER, 0); }
		public TerminalNode KWD_MONTH_OF_YEAR() { return getToken(evaEnglishParser.KWD_MONTH_OF_YEAR, 0); }
		public DayOfMonthContext dayOfMonth() {
			return getRuleContext(DayOfMonthContext.class,0);
		}
		public TerminalNode SYM_COMMA() { return getToken(evaEnglishParser.SYM_COMMA, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public DateAdjNumContext(DateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterDateAdjNum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitDateAdjNum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitDateAdjNum(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DateAdvDayContext extends DateContext {
		public TerminalNode KWD_TODAY() { return getToken(evaEnglishParser.KWD_TODAY, 0); }
		public TerminalNode KWD_TOMORROW() { return getToken(evaEnglishParser.KWD_TOMORROW, 0); }
		public TerminalNode KWD_YESTERDAY() { return getToken(evaEnglishParser.KWD_YESTERDAY, 0); }
		public DateAdvDayContext(DateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterDateAdvDay(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitDateAdvDay(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitDateAdvDay(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DateDefaultContext extends DateContext {
		public Token year;
		public Token monthOfYear;
		public DayOfMonthContext dayOfMonth() {
			return getRuleContext(DayOfMonthContext.class,0);
		}
		public TerminalNode KWD_MONTH_OF_YEAR() { return getToken(evaEnglishParser.KWD_MONTH_OF_YEAR, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public TerminalNode SYM_COMMA() { return getToken(evaEnglishParser.SYM_COMMA, 0); }
		public List<TerminalNode> NUMBER() { return getTokens(evaEnglishParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(evaEnglishParser.NUMBER, i);
		}
		public List<TerminalNode> SYM_DASH() { return getTokens(evaEnglishParser.SYM_DASH); }
		public TerminalNode SYM_DASH(int i) {
			return getToken(evaEnglishParser.SYM_DASH, i);
		}
		public DateDefaultContext(DateContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterDateDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitDateDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitDateDefault(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DateContext date() throws RecognitionException {
		DateContext _localctx = new DateContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_date);
		int _la;
		try {
			setState(708);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,141,_ctx) ) {
			case 1:
				_localctx = new DateAdvDayContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(649);
				match(KWD_TODAY);
				}
				break;
			case 2:
				_localctx = new DateAdvDayContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(650);
				match(KWD_TOMORROW);
				}
				break;
			case 3:
				_localctx = new DateAdvDayContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(651);
				match(KWD_YESTERDAY);
				}
				break;
			case 4:
				_localctx = new DateAdjNumContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(652);
				match(KWD_YEAR);
				setState(653);
				((DateAdjNumContext)_localctx).year = match(NUMBER);
				}
				break;
			case 5:
				_localctx = new DateAdjNumContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(654);
				match(KWD_MONTH_OF_YEAR);
				setState(656);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,128,_ctx) ) {
				case 1:
					{
					setState(655);
					dayOfMonth();
					}
					break;
				}
				setState(662);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,130,_ctx) ) {
				case 1:
					{
					setState(659);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==SYM_COMMA) {
						{
						setState(658);
						match(SYM_COMMA);
						}
					}

					setState(661);
					((DateAdjNumContext)_localctx).year = match(NUMBER);
					}
					break;
				}
				}
				break;
			case 6:
				_localctx = new DateAdjNumContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(665);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(664);
					dayOfMonth();
					}
				}

				setState(668);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_OF) {
					{
					setState(667);
					match(KWD_OF);
					}
				}

				setState(670);
				match(KWD_MONTH_OF_YEAR);
				}
				break;
			case 7:
				_localctx = new DateAdjNumContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(675);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,134,_ctx) ) {
				case 1:
					{
					setState(671);
					((DateAdjNumContext)_localctx).year = match(NUMBER);
					setState(673);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==SYM_COMMA) {
						{
						setState(672);
						match(SYM_COMMA);
						}
					}

					}
					break;
				}
				setState(678);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(677);
					dayOfMonth();
					}
				}

				setState(681);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_OF) {
					{
					setState(680);
					match(KWD_OF);
					}
				}

				setState(683);
				match(KWD_MONTH_OF_YEAR);
				}
				break;
			case 8:
				_localctx = new DateDefaultContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(684);
				dayOfMonth();
				setState(686);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_OF) {
					{
					setState(685);
					match(KWD_OF);
					}
				}

				setState(688);
				match(KWD_MONTH_OF_YEAR);
				setState(690);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SYM_COMMA) {
					{
					setState(689);
					match(SYM_COMMA);
					}
				}

				{
				setState(692);
				((DateDefaultContext)_localctx).year = match(NUMBER);
				}
				}
				break;
			case 9:
				_localctx = new DateDefaultContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				{
				setState(694);
				((DateDefaultContext)_localctx).year = match(NUMBER);
				}
				setState(696);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SYM_COMMA) {
					{
					setState(695);
					match(SYM_COMMA);
					}
				}

				setState(698);
				match(KWD_MONTH_OF_YEAR);
				setState(699);
				dayOfMonth();
				}
				break;
			case 10:
				_localctx = new DateDefaultContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(700);
				((DateDefaultContext)_localctx).year = match(NUMBER);
				setState(701);
				match(SYM_DASH);
				setState(704);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NUMBER:
					{
					setState(702);
					((DateDefaultContext)_localctx).monthOfYear = match(NUMBER);
					}
					break;
				case KWD_MONTH_OF_YEAR:
					{
					setState(703);
					match(KWD_MONTH_OF_YEAR);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(706);
				match(SYM_DASH);
				setState(707);
				dayOfMonth();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DayOfMonthContext extends ParserRuleContext {
		public String value;
		public TerminalNode NUMBER() { return getToken(evaEnglishParser.NUMBER, 0); }
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public TerminalNode KWD_ST() { return getToken(evaEnglishParser.KWD_ST, 0); }
		public TerminalNode KWD_ND() { return getToken(evaEnglishParser.KWD_ND, 0); }
		public TerminalNode KWD_RD() { return getToken(evaEnglishParser.KWD_RD, 0); }
		public TerminalNode KWD_TH() { return getToken(evaEnglishParser.KWD_TH, 0); }
		public DayOfMonthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dayOfMonth; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterDayOfMonth(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitDayOfMonth(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitDayOfMonth(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DayOfMonthContext dayOfMonth() throws RecognitionException {
		DayOfMonthContext _localctx = new DayOfMonthContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_dayOfMonth);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(710);
			match(NUMBER);
			setState(712);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,142,_ctx) ) {
			case 1:
				{
				setState(711);
				match(WS);
				}
				break;
			}
			setState(715);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,143,_ctx) ) {
			case 1:
				{
				setState(714);
				_la = _input.LA(1);
				if ( !(((((_la - 332)) & ~0x3f) == 0 && ((1L << (_la - 332)) & ((1L << (KWD_ST - 332)) | (1L << (KWD_ND - 332)) | (1L << (KWD_RD - 332)) | (1L << (KWD_TH - 332)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			}
			setState(718);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,144,_ctx) ) {
			case 1:
				{
				setState(717);
				match(WS);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TimeContext extends ParserRuleContext {
		public int hh;
		public int MM;
		public int ss;
		public int SSS;
		public String tz;
		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time; }
	 
		public TimeContext() { }
		public void copyFrom(TimeContext ctx) {
			super.copyFrom(ctx);
			this.hh = ctx.hh;
			this.MM = ctx.MM;
			this.ss = ctx.ss;
			this.SSS = ctx.SSS;
			this.tz = ctx.tz;
		}
	}
	public static class TimeAmPmHourContext extends TimeContext {
		public TerminalNode NUMBER() { return getToken(evaEnglishParser.NUMBER, 0); }
		public TerminalNode KWD_AM() { return getToken(evaEnglishParser.KWD_AM, 0); }
		public TerminalNode KWD_PM() { return getToken(evaEnglishParser.KWD_PM, 0); }
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public TimeAmPmHourContext(TimeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterTimeAmPmHour(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitTimeAmPmHour(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitTimeAmPmHour(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TimeAmPmDefaultContext extends TimeContext {
		public TimeContext time() {
			return getRuleContext(TimeContext.class,0);
		}
		public TerminalNode KWD_AM() { return getToken(evaEnglishParser.KWD_AM, 0); }
		public TerminalNode KWD_PM() { return getToken(evaEnglishParser.KWD_PM, 0); }
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public TimeAmPmDefaultContext(TimeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterTimeAmPmDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitTimeAmPmDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitTimeAmPmDefault(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TimeCompleteContext extends TimeContext {
		public List<TerminalNode> NUMBER() { return getTokens(evaEnglishParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(evaEnglishParser.NUMBER, i);
		}
		public List<TerminalNode> SYM_COLON() { return getTokens(evaEnglishParser.SYM_COLON); }
		public TerminalNode SYM_COLON(int i) {
			return getToken(evaEnglishParser.SYM_COLON, i);
		}
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public TimeCompleteContext(TimeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterTimeComplete(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitTimeComplete(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitTimeComplete(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TimePartialContext extends TimeContext {
		public List<TerminalNode> NUMBER() { return getTokens(evaEnglishParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(evaEnglishParser.NUMBER, i);
		}
		public TerminalNode SYM_COLON() { return getToken(evaEnglishParser.SYM_COLON, 0); }
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public TimePartialContext(TimeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterTimePartial(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitTimePartial(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitTimePartial(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TimeTzContext extends TimeContext {
		public TimeContext time() {
			return getRuleContext(TimeContext.class,0);
		}
		public TimeZoneContext timeZone() {
			return getRuleContext(TimeZoneContext.class,0);
		}
		public TerminalNode KWD_Z() { return getToken(evaEnglishParser.KWD_Z, 0); }
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public TimeTzContext(TimeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterTimeTz(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitTimeTz(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitTimeTz(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		return time(0);
	}

	private TimeContext time(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TimeContext _localctx = new TimeContext(_ctx, _parentState);
		TimeContext _prevctx = _localctx;
		int _startState = 30;
		enterRecursionRule(_localctx, 30, RULE_time, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(749);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,151,_ctx) ) {
			case 1:
				{
				_localctx = new TimeAmPmHourContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(721);
				match(NUMBER);
				setState(723);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(722);
					match(WS);
					}
				}

				setState(725);
				_la = _input.LA(1);
				if ( !(_la==KWD_AM || _la==KWD_PM) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(727);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,146,_ctx) ) {
				case 1:
					{
					setState(726);
					match(WS);
					}
					break;
				}
				}
				break;
			case 2:
				{
				_localctx = new TimeCompleteContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(730);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(729);
					match(WS);
					}
				}

				setState(732);
				match(NUMBER);
				setState(733);
				match(SYM_COLON);
				setState(734);
				match(NUMBER);
				setState(735);
				match(SYM_COLON);
				setState(736);
				match(NUMBER);
				setState(738);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,148,_ctx) ) {
				case 1:
					{
					setState(737);
					match(WS);
					}
					break;
				}
				}
				break;
			case 3:
				{
				_localctx = new TimePartialContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(741);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(740);
					match(WS);
					}
				}

				setState(743);
				match(NUMBER);
				setState(744);
				match(SYM_COLON);
				setState(745);
				match(NUMBER);
				setState(747);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,150,_ctx) ) {
				case 1:
					{
					setState(746);
					match(WS);
					}
					break;
				}
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(772);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,158,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(770);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,157,_ctx) ) {
					case 1:
						{
						_localctx = new TimeAmPmDefaultContext(new TimeContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_time);
						setState(751);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(753);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==WS) {
							{
							setState(752);
							match(WS);
							}
						}

						setState(755);
						_la = _input.LA(1);
						if ( !(_la==KWD_AM || _la==KWD_PM) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(757);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,153,_ctx) ) {
						case 1:
							{
							setState(756);
							match(WS);
							}
							break;
						}
						}
						break;
					case 2:
						{
						_localctx = new TimeTzContext(new TimeContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_time);
						setState(759);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(761);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==WS) {
							{
							setState(760);
							match(WS);
							}
						}

						setState(765);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case SYM_PLUS:
						case SYM_DASH:
						case KWD_GMT:
						case KWD_UTC:
							{
							setState(763);
							timeZone();
							}
							break;
						case KWD_Z:
							{
							setState(764);
							match(KWD_Z);
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(768);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,156,_ctx) ) {
						case 1:
							{
							setState(767);
							match(WS);
							}
							break;
						}
						}
						break;
					}
					} 
				}
				setState(774);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,158,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class TimeZoneContext extends ParserRuleContext {
		public String value;
		public List<TerminalNode> NUMBER() { return getTokens(evaEnglishParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(evaEnglishParser.NUMBER, i);
		}
		public TerminalNode SYM_DASH() { return getToken(evaEnglishParser.SYM_DASH, 0); }
		public TerminalNode SYM_PLUS() { return getToken(evaEnglishParser.SYM_PLUS, 0); }
		public TerminalNode KWD_GMT() { return getToken(evaEnglishParser.KWD_GMT, 0); }
		public TerminalNode KWD_UTC() { return getToken(evaEnglishParser.KWD_UTC, 0); }
		public TerminalNode SYM_COLON() { return getToken(evaEnglishParser.SYM_COLON, 0); }
		public TerminalNode SYM_PERIOD() { return getToken(evaEnglishParser.SYM_PERIOD, 0); }
		public TimeZoneContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_timeZone; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterTimeZone(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitTimeZone(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitTimeZone(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeZoneContext timeZone() throws RecognitionException {
		TimeZoneContext _localctx = new TimeZoneContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_timeZone);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(776);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==KWD_GMT || _la==KWD_UTC) {
				{
				setState(775);
				_la = _input.LA(1);
				if ( !(_la==KWD_GMT || _la==KWD_UTC) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(778);
			_la = _input.LA(1);
			if ( !(_la==SYM_PLUS || _la==SYM_DASH) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(779);
			match(NUMBER);
			setState(782);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,160,_ctx) ) {
			case 1:
				{
				setState(780);
				_la = _input.LA(1);
				if ( !(_la==SYM_PERIOD || _la==SYM_COLON) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(781);
				match(NUMBER);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AggregateFieldContext extends ParserRuleContext {
		public String fieldText;
		public int fieldType;
		public String fieldValue;
		public String datatype;
		public String fieldAlias;
		public boolean isAggregated;
		public List<String> entityNames;
		public int dd;
		public int mm;
		public int yyyy;
		public int hh;
		public int MM;
		public int ss;
		public int SSS;
		public String tz;
		public AggregateFieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregateField; }
	 
		public AggregateFieldContext() { }
		public void copyFrom(AggregateFieldContext ctx) {
			super.copyFrom(ctx);
			this.fieldText = ctx.fieldText;
			this.fieldType = ctx.fieldType;
			this.fieldValue = ctx.fieldValue;
			this.datatype = ctx.datatype;
			this.fieldAlias = ctx.fieldAlias;
			this.isAggregated = ctx.isAggregated;
			this.entityNames = ctx.entityNames;
			this.dd = ctx.dd;
			this.mm = ctx.mm;
			this.yyyy = ctx.yyyy;
			this.hh = ctx.hh;
			this.MM = ctx.MM;
			this.ss = ctx.ss;
			this.SSS = ctx.SSS;
			this.tz = ctx.tz;
		}
	}
	public static class AggFieldSumContext extends AggregateFieldContext {
		public TerminalNode KWD_AGG_SUM() { return getToken(evaEnglishParser.KWD_AGG_SUM, 0); }
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public TerminalNode KWD_ALL() { return getToken(evaEnglishParser.KWD_ALL, 0); }
		public TerminalNode KWD_SUM() { return getToken(evaEnglishParser.KWD_SUM, 0); }
		public AggFieldSumContext(AggregateFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterAggFieldSum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitAggFieldSum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitAggFieldSum(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AggFieldMaxContext extends AggregateFieldContext {
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public TerminalNode KWD_AGG_MAX() { return getToken(evaEnglishParser.KWD_AGG_MAX, 0); }
		public TerminalNode KWD_HIGHEST() { return getToken(evaEnglishParser.KWD_HIGHEST, 0); }
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public TerminalNode KWD_POSSIBLE() { return getToken(evaEnglishParser.KWD_POSSIBLE, 0); }
		public TerminalNode KWD_ALL() { return getToken(evaEnglishParser.KWD_ALL, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public TerminalNode KWD_FOR() { return getToken(evaEnglishParser.KWD_FOR, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public AggFieldMaxContext(AggregateFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterAggFieldMax(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitAggFieldMax(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitAggFieldMax(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AggFieldCountContext extends AggregateFieldContext {
		public TerminalNode KWD_AGG_COUNT() { return getToken(evaEnglishParser.KWD_AGG_COUNT, 0); }
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public TerminalNode KWD_ALL() { return getToken(evaEnglishParser.KWD_ALL, 0); }
		public TerminalNode WS() { return getToken(evaEnglishParser.WS, 0); }
		public AggFieldCountContext(AggregateFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterAggFieldCount(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitAggFieldCount(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitAggFieldCount(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AggFieldMinContext extends AggregateFieldContext {
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public TerminalNode KWD_AGG_MIN() { return getToken(evaEnglishParser.KWD_AGG_MIN, 0); }
		public TerminalNode KWD_LOWEST() { return getToken(evaEnglishParser.KWD_LOWEST, 0); }
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public TerminalNode KWD_POSSIBLE() { return getToken(evaEnglishParser.KWD_POSSIBLE, 0); }
		public TerminalNode KWD_ALL() { return getToken(evaEnglishParser.KWD_ALL, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public TerminalNode KWD_FOR() { return getToken(evaEnglishParser.KWD_FOR, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public AggFieldMinContext(AggregateFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterAggFieldMin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitAggFieldMin(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitAggFieldMin(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AggFieldAvgContext extends AggregateFieldContext {
		public TerminalNode KWD_AGG_AVG() { return getToken(evaEnglishParser.KWD_AGG_AVG, 0); }
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public TerminalNode KWD_ALL() { return getToken(evaEnglishParser.KWD_ALL, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public TerminalNode KWD_FOR() { return getToken(evaEnglishParser.KWD_FOR, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public AggFieldAvgContext(AggregateFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterAggFieldAvg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitAggFieldAvg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitAggFieldAvg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AggregateFieldContext aggregateField() throws RecognitionException {
		AggregateFieldContext _localctx = new AggregateFieldContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_aggregateField);
		int _la;
		try {
			setState(881);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,185,_ctx) ) {
			case 1:
				_localctx = new AggFieldSumContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(784);
				match(KWD_AGG_SUM);
				setState(786);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,161,_ctx) ) {
				case 1:
					{
					setState(785);
					match(WS);
					}
					break;
				}
				setState(789);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,162,_ctx) ) {
				case 1:
					{
					setState(788);
					match(KWD_ALL);
					}
					break;
				}
				setState(792);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,163,_ctx) ) {
				case 1:
					{
					setState(791);
					match(WS);
					}
					break;
				}
				setState(794);
				fieldContent();
				}
				break;
			case 2:
				_localctx = new AggFieldMaxContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(795);
				_la = _input.LA(1);
				if ( !(_la==KWD_AGG_MAX || _la==KWD_HIGHEST) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(797);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,164,_ctx) ) {
				case 1:
					{
					setState(796);
					match(WS);
					}
					break;
				}
				setState(800);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,165,_ctx) ) {
				case 1:
					{
					setState(799);
					match(KWD_POSSIBLE);
					}
					break;
				}
				setState(803);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,166,_ctx) ) {
				case 1:
					{
					setState(802);
					match(WS);
					}
					break;
				}
				setState(806);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,167,_ctx) ) {
				case 1:
					{
					setState(805);
					_la = _input.LA(1);
					if ( !(((((_la - 272)) & ~0x3f) == 0 && ((1L << (_la - 272)) & ((1L << (KWD_OF - 272)) | (1L << (KWD_IN - 272)) | (1L << (KWD_FOR - 272)))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(809);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,168,_ctx) ) {
				case 1:
					{
					setState(808);
					match(WS);
					}
					break;
				}
				setState(812);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,169,_ctx) ) {
				case 1:
					{
					setState(811);
					match(KWD_ALL);
					}
					break;
				}
				setState(815);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,170,_ctx) ) {
				case 1:
					{
					setState(814);
					match(WS);
					}
					break;
				}
				setState(817);
				fieldContent();
				}
				break;
			case 3:
				_localctx = new AggFieldMinContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(818);
				_la = _input.LA(1);
				if ( !(_la==KWD_AGG_MIN || _la==KWD_LOWEST) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(820);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,171,_ctx) ) {
				case 1:
					{
					setState(819);
					match(WS);
					}
					break;
				}
				setState(823);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,172,_ctx) ) {
				case 1:
					{
					setState(822);
					match(KWD_POSSIBLE);
					}
					break;
				}
				setState(826);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,173,_ctx) ) {
				case 1:
					{
					setState(825);
					match(WS);
					}
					break;
				}
				setState(829);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,174,_ctx) ) {
				case 1:
					{
					setState(828);
					_la = _input.LA(1);
					if ( !(((((_la - 272)) & ~0x3f) == 0 && ((1L << (_la - 272)) & ((1L << (KWD_OF - 272)) | (1L << (KWD_IN - 272)) | (1L << (KWD_FOR - 272)))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(832);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,175,_ctx) ) {
				case 1:
					{
					setState(831);
					match(WS);
					}
					break;
				}
				setState(835);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,176,_ctx) ) {
				case 1:
					{
					setState(834);
					match(KWD_ALL);
					}
					break;
				}
				setState(838);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,177,_ctx) ) {
				case 1:
					{
					setState(837);
					match(WS);
					}
					break;
				}
				setState(840);
				fieldContent();
				}
				break;
			case 4:
				_localctx = new AggFieldAvgContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(841);
				match(KWD_AGG_AVG);
				setState(843);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,178,_ctx) ) {
				case 1:
					{
					setState(842);
					match(WS);
					}
					break;
				}
				setState(846);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,179,_ctx) ) {
				case 1:
					{
					setState(845);
					_la = _input.LA(1);
					if ( !(((((_la - 272)) & ~0x3f) == 0 && ((1L << (_la - 272)) & ((1L << (KWD_OF - 272)) | (1L << (KWD_IN - 272)) | (1L << (KWD_FOR - 272)))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(849);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,180,_ctx) ) {
				case 1:
					{
					setState(848);
					match(WS);
					}
					break;
				}
				setState(852);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,181,_ctx) ) {
				case 1:
					{
					setState(851);
					match(KWD_ALL);
					}
					break;
				}
				setState(855);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,182,_ctx) ) {
				case 1:
					{
					setState(854);
					match(WS);
					}
					break;
				}
				setState(857);
				fieldContent();
				}
				break;
			case 5:
				_localctx = new AggFieldCountContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(858);
				match(KWD_AGG_COUNT);
				setState(860);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,183,_ctx) ) {
				case 1:
					{
					setState(859);
					match(KWD_ALL);
					}
					break;
				}
				setState(863);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,184,_ctx) ) {
				case 1:
					{
					setState(862);
					match(WS);
					}
					break;
				}
				setState(865);
				fieldContent();
				}
				break;
			case 6:
				_localctx = new AggFieldSumContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(866);
				fieldContent();
				setState(867);
				_la = _input.LA(1);
				if ( !(_la==KWD_AGG_SUM || _la==KWD_SUM) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 7:
				_localctx = new AggFieldMaxContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(869);
				fieldContent();
				setState(870);
				match(KWD_AGG_MAX);
				}
				break;
			case 8:
				_localctx = new AggFieldMinContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(872);
				fieldContent();
				setState(873);
				match(KWD_AGG_MIN);
				}
				break;
			case 9:
				_localctx = new AggFieldAvgContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(875);
				fieldContent();
				setState(876);
				match(KWD_AGG_AVG);
				}
				break;
			case 10:
				_localctx = new AggFieldCountContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(878);
				fieldContent();
				setState(879);
				match(KWD_AGG_COUNT);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AggregateNumberContext extends ParserRuleContext {
		public String value;
		public String aggFunction;
		public AggregateNumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregateNumber; }
	 
		public AggregateNumberContext() { }
		public void copyFrom(AggregateNumberContext ctx) {
			super.copyFrom(ctx);
			this.value = ctx.value;
			this.aggFunction = ctx.aggFunction;
		}
	}
	public static class AggNumberMinContext extends AggregateNumberContext {
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public TerminalNode KWD_AGG_MIN() { return getToken(evaEnglishParser.KWD_AGG_MIN, 0); }
		public TerminalNode KWD_LOWEST() { return getToken(evaEnglishParser.KWD_LOWEST, 0); }
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public TerminalNode KWD_POSSIBLE() { return getToken(evaEnglishParser.KWD_POSSIBLE, 0); }
		public TerminalNode KWD_ALL() { return getToken(evaEnglishParser.KWD_ALL, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public TerminalNode KWD_FOR() { return getToken(evaEnglishParser.KWD_FOR, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public AggNumberMinContext(AggregateNumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterAggNumberMin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitAggNumberMin(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitAggNumberMin(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AggNumberAvgContext extends AggregateNumberContext {
		public TerminalNode KWD_AGG_AVG() { return getToken(evaEnglishParser.KWD_AGG_AVG, 0); }
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public TerminalNode KWD_ALL() { return getToken(evaEnglishParser.KWD_ALL, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public TerminalNode KWD_FOR() { return getToken(evaEnglishParser.KWD_FOR, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public AggNumberAvgContext(AggregateNumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterAggNumberAvg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitAggNumberAvg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitAggNumberAvg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AggNumberMaxContext extends AggregateNumberContext {
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public TerminalNode KWD_AGG_MAX() { return getToken(evaEnglishParser.KWD_AGG_MAX, 0); }
		public TerminalNode KWD_HIGHEST() { return getToken(evaEnglishParser.KWD_HIGHEST, 0); }
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public TerminalNode KWD_POSSIBLE() { return getToken(evaEnglishParser.KWD_POSSIBLE, 0); }
		public TerminalNode KWD_ALL() { return getToken(evaEnglishParser.KWD_ALL, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public TerminalNode KWD_FOR() { return getToken(evaEnglishParser.KWD_FOR, 0); }
		public TerminalNode KWD_IN() { return getToken(evaEnglishParser.KWD_IN, 0); }
		public AggNumberMaxContext(AggregateNumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterAggNumberMax(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitAggNumberMax(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitAggNumberMax(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AggNumberCountContext extends AggregateNumberContext {
		public TerminalNode KWD_AGG_COUNT() { return getToken(evaEnglishParser.KWD_AGG_COUNT, 0); }
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public TerminalNode KWD_ALL() { return getToken(evaEnglishParser.KWD_ALL, 0); }
		public TerminalNode WS() { return getToken(evaEnglishParser.WS, 0); }
		public AggNumberCountContext(AggregateNumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterAggNumberCount(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitAggNumberCount(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitAggNumberCount(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AggNumberSumContext extends AggregateNumberContext {
		public TerminalNode KWD_AGG_SUM() { return getToken(evaEnglishParser.KWD_AGG_SUM, 0); }
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public TerminalNode KWD_ALL() { return getToken(evaEnglishParser.KWD_ALL, 0); }
		public AggNumberSumContext(AggregateNumberContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterAggNumberSum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitAggNumberSum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitAggNumberSum(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AggregateNumberContext aggregateNumber() throws RecognitionException {
		AggregateNumberContext _localctx = new AggregateNumberContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_aggregateNumber);
		int _la;
		try {
			setState(965);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case KWD_AGG_SUM:
				_localctx = new AggNumberSumContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(883);
				match(KWD_AGG_SUM);
				setState(885);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,186,_ctx) ) {
				case 1:
					{
					setState(884);
					match(WS);
					}
					break;
				}
				setState(888);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_ALL) {
					{
					setState(887);
					match(KWD_ALL);
					}
				}

				setState(891);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(890);
					match(WS);
					}
				}

				setState(893);
				numberField();
				}
				break;
			case KWD_AGG_MAX:
			case KWD_HIGHEST:
				_localctx = new AggNumberMaxContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(894);
				_la = _input.LA(1);
				if ( !(_la==KWD_AGG_MAX || _la==KWD_HIGHEST) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(896);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,189,_ctx) ) {
				case 1:
					{
					setState(895);
					match(WS);
					}
					break;
				}
				setState(899);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_POSSIBLE) {
					{
					setState(898);
					match(KWD_POSSIBLE);
					}
				}

				setState(902);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,191,_ctx) ) {
				case 1:
					{
					setState(901);
					match(WS);
					}
					break;
				}
				setState(905);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 272)) & ~0x3f) == 0 && ((1L << (_la - 272)) & ((1L << (KWD_OF - 272)) | (1L << (KWD_IN - 272)) | (1L << (KWD_FOR - 272)))) != 0)) {
					{
					setState(904);
					_la = _input.LA(1);
					if ( !(((((_la - 272)) & ~0x3f) == 0 && ((1L << (_la - 272)) & ((1L << (KWD_OF - 272)) | (1L << (KWD_IN - 272)) | (1L << (KWD_FOR - 272)))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(908);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,193,_ctx) ) {
				case 1:
					{
					setState(907);
					match(WS);
					}
					break;
				}
				setState(911);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_ALL) {
					{
					setState(910);
					match(KWD_ALL);
					}
				}

				setState(914);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(913);
					match(WS);
					}
				}

				setState(916);
				numberField();
				}
				break;
			case KWD_AGG_MIN:
			case KWD_LOWEST:
				_localctx = new AggNumberMinContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(917);
				_la = _input.LA(1);
				if ( !(_la==KWD_AGG_MIN || _la==KWD_LOWEST) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(919);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,196,_ctx) ) {
				case 1:
					{
					setState(918);
					match(WS);
					}
					break;
				}
				setState(922);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_POSSIBLE) {
					{
					setState(921);
					match(KWD_POSSIBLE);
					}
				}

				setState(925);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,198,_ctx) ) {
				case 1:
					{
					setState(924);
					match(WS);
					}
					break;
				}
				setState(928);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 272)) & ~0x3f) == 0 && ((1L << (_la - 272)) & ((1L << (KWD_OF - 272)) | (1L << (KWD_IN - 272)) | (1L << (KWD_FOR - 272)))) != 0)) {
					{
					setState(927);
					_la = _input.LA(1);
					if ( !(((((_la - 272)) & ~0x3f) == 0 && ((1L << (_la - 272)) & ((1L << (KWD_OF - 272)) | (1L << (KWD_IN - 272)) | (1L << (KWD_FOR - 272)))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(931);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,200,_ctx) ) {
				case 1:
					{
					setState(930);
					match(WS);
					}
					break;
				}
				setState(934);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_ALL) {
					{
					setState(933);
					match(KWD_ALL);
					}
				}

				setState(937);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(936);
					match(WS);
					}
				}

				setState(939);
				numberField();
				}
				break;
			case KWD_AGG_AVG:
				_localctx = new AggNumberAvgContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(940);
				match(KWD_AGG_AVG);
				setState(942);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,203,_ctx) ) {
				case 1:
					{
					setState(941);
					match(WS);
					}
					break;
				}
				setState(945);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 272)) & ~0x3f) == 0 && ((1L << (_la - 272)) & ((1L << (KWD_OF - 272)) | (1L << (KWD_IN - 272)) | (1L << (KWD_FOR - 272)))) != 0)) {
					{
					setState(944);
					_la = _input.LA(1);
					if ( !(((((_la - 272)) & ~0x3f) == 0 && ((1L << (_la - 272)) & ((1L << (KWD_OF - 272)) | (1L << (KWD_IN - 272)) | (1L << (KWD_FOR - 272)))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(948);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,205,_ctx) ) {
				case 1:
					{
					setState(947);
					match(WS);
					}
					break;
				}
				setState(951);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_ALL) {
					{
					setState(950);
					match(KWD_ALL);
					}
				}

				setState(954);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(953);
					match(WS);
					}
				}

				setState(956);
				numberField();
				}
				break;
			case KWD_AGG_COUNT:
				_localctx = new AggNumberCountContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(957);
				match(KWD_AGG_COUNT);
				setState(959);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_ALL) {
					{
					setState(958);
					match(KWD_ALL);
					}
				}

				setState(962);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(961);
					match(WS);
					}
				}

				setState(964);
				numberField();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberFieldContext extends ParserRuleContext {
		public String value;
		public String aggFunction;
		public NumberFieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numberField; }
	 
		public NumberFieldContext() { }
		public void copyFrom(NumberFieldContext ctx) {
			super.copyFrom(ctx);
			this.value = ctx.value;
			this.aggFunction = ctx.aggFunction;
		}
	}
	public static class NumberKContext extends NumberFieldContext {
		public TerminalNode KWD_NUMBER_K() { return getToken(evaEnglishParser.KWD_NUMBER_K, 0); }
		public NumberKContext(NumberFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterNumberK(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitNumberK(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitNumberK(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumberDefaultContext extends NumberFieldContext {
		public TerminalNode NUMBER() { return getToken(evaEnglishParser.NUMBER, 0); }
		public NumberDefaultContext(NumberFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterNumberDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitNumberDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitNumberDefault(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumberMContext extends NumberFieldContext {
		public TerminalNode KWD_NUMBER_M() { return getToken(evaEnglishParser.KWD_NUMBER_M, 0); }
		public NumberMContext(NumberFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterNumberM(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitNumberM(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitNumberM(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumberTContext extends NumberFieldContext {
		public TerminalNode KWD_NUMBER_T() { return getToken(evaEnglishParser.KWD_NUMBER_T, 0); }
		public NumberTContext(NumberFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterNumberT(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitNumberT(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitNumberT(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumberBContext extends NumberFieldContext {
		public TerminalNode KWD_NUMBER_B() { return getToken(evaEnglishParser.KWD_NUMBER_B, 0); }
		public NumberBContext(NumberFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterNumberB(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitNumberB(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitNumberB(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumberAggContext extends NumberFieldContext {
		public AggregateNumberContext aggregateNumber() {
			return getRuleContext(AggregateNumberContext.class,0);
		}
		public NumberAggContext(NumberFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterNumberAgg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitNumberAgg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitNumberAgg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberFieldContext numberField() throws RecognitionException {
		NumberFieldContext _localctx = new NumberFieldContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_numberField);
		try {
			setState(973);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case KWD_AGG_SUM:
			case KWD_AGG_MAX:
			case KWD_AGG_MIN:
			case KWD_AGG_AVG:
			case KWD_AGG_COUNT:
			case KWD_LOWEST:
			case KWD_HIGHEST:
				_localctx = new NumberAggContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(967);
				aggregateNumber();
				}
				break;
			case KWD_NUMBER_T:
				_localctx = new NumberTContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(968);
				match(KWD_NUMBER_T);
				}
				break;
			case KWD_NUMBER_B:
				_localctx = new NumberBContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(969);
				match(KWD_NUMBER_B);
				}
				break;
			case KWD_NUMBER_M:
				_localctx = new NumberMContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(970);
				match(KWD_NUMBER_M);
				}
				break;
			case KWD_NUMBER_K:
				_localctx = new NumberKContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(971);
				match(KWD_NUMBER_K);
				}
				break;
			case NUMBER:
				_localctx = new NumberDefaultContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(972);
				match(NUMBER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RankFieldContext extends ParserRuleContext {
		public String fieldText;
		public int fieldType;
		public String fieldValue;
		public String fieldAlias;
		public String datatype;
		public boolean isAggregated;
		public List<String> entityNames;
		public RankFieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rankField; }
	 
		public RankFieldContext() { }
		public void copyFrom(RankFieldContext ctx) {
			super.copyFrom(ctx);
			this.fieldText = ctx.fieldText;
			this.fieldType = ctx.fieldType;
			this.fieldValue = ctx.fieldValue;
			this.fieldAlias = ctx.fieldAlias;
			this.datatype = ctx.datatype;
			this.isAggregated = ctx.isAggregated;
			this.entityNames = ctx.entityNames;
		}
	}
	public static class RankFieldDefaultContext extends RankFieldContext {
		public TerminalNode KWD_RANK() { return getToken(evaEnglishParser.KWD_RANK, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public OverClauseContext overClause() {
			return getRuleContext(OverClauseContext.class,0);
		}
		public RankFieldDefaultContext(RankFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterRankFieldDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitRankFieldDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitRankFieldDefault(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RankFieldFirstContext extends RankFieldContext {
		public OverClauseContext overClause() {
			return getRuleContext(OverClauseContext.class,0);
		}
		public TerminalNode KWD_FIRST() { return getToken(evaEnglishParser.KWD_FIRST, 0); }
		public TerminalNode KWD_TOP() { return getToken(evaEnglishParser.KWD_TOP, 0); }
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public RankFieldFirstContext(RankFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterRankFieldFirst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitRankFieldFirst(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitRankFieldFirst(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RankFieldNthContext extends RankFieldContext {
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public OverClauseContext overClause() {
			return getRuleContext(OverClauseContext.class,0);
		}
		public TerminalNode KWD_ST() { return getToken(evaEnglishParser.KWD_ST, 0); }
		public TerminalNode KWD_ND() { return getToken(evaEnglishParser.KWD_ND, 0); }
		public TerminalNode KWD_RD() { return getToken(evaEnglishParser.KWD_RD, 0); }
		public TerminalNode KWD_TH() { return getToken(evaEnglishParser.KWD_TH, 0); }
		public TerminalNode KWD_HIGHEST() { return getToken(evaEnglishParser.KWD_HIGHEST, 0); }
		public RankFieldNthContext(RankFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterRankFieldNth(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitRankFieldNth(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitRankFieldNth(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RankFieldLastContext extends RankFieldContext {
		public OverClauseContext overClause() {
			return getRuleContext(OverClauseContext.class,0);
		}
		public TerminalNode KWD_LAST() { return getToken(evaEnglishParser.KWD_LAST, 0); }
		public TerminalNode KWD_BOTTOM() { return getToken(evaEnglishParser.KWD_BOTTOM, 0); }
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public RankFieldLastContext(RankFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterRankFieldLast(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitRankFieldLast(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitRankFieldLast(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RankFieldLeadingContext extends RankFieldContext {
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public OverClauseContext overClause() {
			return getRuleContext(OverClauseContext.class,0);
		}
		public TerminalNode KWD_LEADING() { return getToken(evaEnglishParser.KWD_LEADING, 0); }
		public TerminalNode KWD_PRECEDING() { return getToken(evaEnglishParser.KWD_PRECEDING, 0); }
		public RankFieldLeadingContext(RankFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterRankFieldLeading(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitRankFieldLeading(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitRankFieldLeading(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RankFieldFollowingContext extends RankFieldContext {
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public OverClauseContext overClause() {
			return getRuleContext(OverClauseContext.class,0);
		}
		public TerminalNode KWD_FOLLOWING() { return getToken(evaEnglishParser.KWD_FOLLOWING, 0); }
		public TerminalNode KWD_LAGGING() { return getToken(evaEnglishParser.KWD_LAGGING, 0); }
		public RankFieldFollowingContext(RankFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterRankFieldFollowing(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitRankFieldFollowing(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitRankFieldFollowing(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RankFieldNthInverseContext extends RankFieldContext {
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public TerminalNode KWD_LAST() { return getToken(evaEnglishParser.KWD_LAST, 0); }
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public OverClauseContext overClause() {
			return getRuleContext(OverClauseContext.class,0);
		}
		public TerminalNode KWD_ST() { return getToken(evaEnglishParser.KWD_ST, 0); }
		public TerminalNode KWD_ND() { return getToken(evaEnglishParser.KWD_ND, 0); }
		public TerminalNode KWD_RD() { return getToken(evaEnglishParser.KWD_RD, 0); }
		public TerminalNode KWD_TH() { return getToken(evaEnglishParser.KWD_TH, 0); }
		public TerminalNode KWD_FROM() { return getToken(evaEnglishParser.KWD_FROM, 0); }
		public TerminalNode KWD_BOTTOM() { return getToken(evaEnglishParser.KWD_BOTTOM, 0); }
		public TerminalNode KWD_LOWEST() { return getToken(evaEnglishParser.KWD_LOWEST, 0); }
		public RankFieldNthInverseContext(RankFieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterRankFieldNthInverse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitRankFieldNthInverse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitRankFieldNthInverse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RankFieldContext rankField() throws RecognitionException {
		RankFieldContext _localctx = new RankFieldContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_rankField);
		int _la;
		try {
			setState(1030);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,216,_ctx) ) {
			case 1:
				_localctx = new RankFieldFirstContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(975);
				_la = _input.LA(1);
				if ( !(_la==KWD_FIRST || _la==KWD_TOP) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(979);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << KWD_NUMBER_T) | (1L << KWD_NUMBER_B) | (1L << KWD_NUMBER_M) | (1L << KWD_NUMBER_K) | (1L << KWD_AGG_SUM) | (1L << KWD_AGG_MAX) | (1L << KWD_AGG_MIN) | (1L << KWD_AGG_AVG) | (1L << KWD_AGG_COUNT))) != 0) || _la==KWD_LOWEST || _la==KWD_HIGHEST || _la==NUMBER) {
					{
					setState(976);
					numberField();
					setState(977);
					fieldContent();
					}
				}

				setState(981);
				overClause();
				}
				break;
			case 2:
				_localctx = new RankFieldLastContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(982);
				_la = _input.LA(1);
				if ( !(_la==KWD_BOTTOM || _la==KWD_LAST) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(986);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << KWD_NUMBER_T) | (1L << KWD_NUMBER_B) | (1L << KWD_NUMBER_M) | (1L << KWD_NUMBER_K) | (1L << KWD_AGG_SUM) | (1L << KWD_AGG_MAX) | (1L << KWD_AGG_MIN) | (1L << KWD_AGG_AVG) | (1L << KWD_AGG_COUNT))) != 0) || _la==KWD_LOWEST || _la==KWD_HIGHEST || _la==NUMBER) {
					{
					setState(983);
					numberField();
					setState(984);
					fieldContent();
					}
				}

				setState(988);
				overClause();
				}
				break;
			case 3:
				_localctx = new RankFieldNthInverseContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(989);
				numberField();
				setState(990);
				_la = _input.LA(1);
				if ( !(((((_la - 332)) & ~0x3f) == 0 && ((1L << (_la - 332)) & ((1L << (KWD_ST - 332)) | (1L << (KWD_ND - 332)) | (1L << (KWD_RD - 332)) | (1L << (KWD_TH - 332)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(991);
				match(KWD_LAST);
				setState(992);
				fieldContent();
				setState(993);
				overClause();
				}
				break;
			case 4:
				_localctx = new RankFieldNthInverseContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(995);
				numberField();
				setState(996);
				_la = _input.LA(1);
				if ( !(((((_la - 332)) & ~0x3f) == 0 && ((1L << (_la - 332)) & ((1L << (KWD_ST - 332)) | (1L << (KWD_ND - 332)) | (1L << (KWD_RD - 332)) | (1L << (KWD_TH - 332)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(998);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,214,_ctx) ) {
				case 1:
					{
					setState(997);
					match(KWD_LOWEST);
					}
					break;
				}
				setState(1000);
				fieldContent();
				setState(1001);
				match(KWD_FROM);
				setState(1002);
				match(KWD_BOTTOM);
				setState(1003);
				overClause();
				}
				break;
			case 5:
				_localctx = new RankFieldNthContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(1005);
				numberField();
				setState(1006);
				_la = _input.LA(1);
				if ( !(((((_la - 332)) & ~0x3f) == 0 && ((1L << (_la - 332)) & ((1L << (KWD_ST - 332)) | (1L << (KWD_ND - 332)) | (1L << (KWD_RD - 332)) | (1L << (KWD_TH - 332)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1008);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,215,_ctx) ) {
				case 1:
					{
					setState(1007);
					match(KWD_HIGHEST);
					}
					break;
				}
				setState(1010);
				fieldContent();
				setState(1011);
				overClause();
				}
				break;
			case 6:
				_localctx = new RankFieldLeadingContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(1013);
				_la = _input.LA(1);
				if ( !(_la==KWD_LEADING || _la==KWD_PRECEDING) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1014);
				fieldContent();
				setState(1015);
				overClause();
				}
				break;
			case 7:
				_localctx = new RankFieldFollowingContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(1017);
				_la = _input.LA(1);
				if ( !(_la==KWD_FOLLOWING || _la==KWD_LAGGING) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1018);
				fieldContent();
				setState(1019);
				overClause();
				}
				break;
			case 8:
				_localctx = new RankFieldDefaultContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(1021);
				match(KWD_RANK);
				setState(1022);
				match(KWD_OF);
				setState(1023);
				fieldContent();
				setState(1024);
				overClause();
				}
				break;
			case 9:
				_localctx = new RankFieldDefaultContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(1026);
				fieldContent();
				setState(1027);
				match(KWD_RANK);
				setState(1028);
				overClause();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OverClauseContext extends ParserRuleContext {
		public List<String> partitionFields;
		public List<String> orderFields;
		public OverClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_overClause; }
	 
		public OverClauseContext() { }
		public void copyFrom(OverClauseContext ctx) {
			super.copyFrom(ctx);
			this.partitionFields = ctx.partitionFields;
			this.orderFields = ctx.orderFields;
		}
	}
	public static class OverClauseOrdPartContext extends OverClauseContext {
		public TerminalNode KWD_ORDER_CLAUSE() { return getToken(evaEnglishParser.KWD_ORDER_CLAUSE, 0); }
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public List<TerminalNode> POS_POSESV_PRN() { return getTokens(evaEnglishParser.POS_POSESV_PRN); }
		public TerminalNode POS_POSESV_PRN(int i) {
			return getToken(evaEnglishParser.POS_POSESV_PRN, i);
		}
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public TerminalNode KWD_PARTITION_CLAUSE() { return getToken(evaEnglishParser.KWD_PARTITION_CLAUSE, 0); }
		public TerminalNode KWD_BY() { return getToken(evaEnglishParser.KWD_BY, 0); }
		public TerminalNode KWD_PER() { return getToken(evaEnglishParser.KWD_PER, 0); }
		public OverClauseOrdPartContext(OverClauseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterOverClauseOrdPart(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitOverClauseOrdPart(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitOverClauseOrdPart(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class OverClausePartOrdContext extends OverClauseContext {
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public TerminalNode KWD_ORDER_CLAUSE() { return getToken(evaEnglishParser.KWD_ORDER_CLAUSE, 0); }
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public TerminalNode KWD_PARTITION_CLAUSE() { return getToken(evaEnglishParser.KWD_PARTITION_CLAUSE, 0); }
		public TerminalNode KWD_BY() { return getToken(evaEnglishParser.KWD_BY, 0); }
		public TerminalNode KWD_PER() { return getToken(evaEnglishParser.KWD_PER, 0); }
		public List<TerminalNode> POS_POSESV_PRN() { return getTokens(evaEnglishParser.POS_POSESV_PRN); }
		public TerminalNode POS_POSESV_PRN(int i) {
			return getToken(evaEnglishParser.POS_POSESV_PRN, i);
		}
		public OverClausePartOrdContext(OverClauseContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterOverClausePartOrd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitOverClausePartOrd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitOverClausePartOrd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OverClauseContext overClause() throws RecognitionException {
		OverClauseContext _localctx = new OverClauseContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_overClause);
		int _la;
		try {
			setState(1055);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case KWD_ORDER_CLAUSE:
				_localctx = new OverClauseOrdPartContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1032);
				match(KWD_ORDER_CLAUSE);
				setState(1034);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,217,_ctx) ) {
				case 1:
					{
					setState(1033);
					match(POS_POSESV_PRN);
					}
					break;
				}
				setState(1036);
				field(0);
				setState(1042);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,219,_ctx) ) {
				case 1:
					{
					setState(1037);
					_la = _input.LA(1);
					if ( !(_la==KWD_PARTITION_CLAUSE || _la==KWD_PER || _la==KWD_BY) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(1039);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,218,_ctx) ) {
					case 1:
						{
						setState(1038);
						match(POS_POSESV_PRN);
						}
						break;
					}
					setState(1041);
					fieldContent();
					}
					break;
				}
				}
				break;
			case KWD_PARTITION_CLAUSE:
			case KWD_PER:
			case KWD_BY:
				_localctx = new OverClausePartOrdContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1044);
				_la = _input.LA(1);
				if ( !(_la==KWD_PARTITION_CLAUSE || _la==KWD_PER || _la==KWD_BY) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1046);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,220,_ctx) ) {
				case 1:
					{
					setState(1045);
					match(POS_POSESV_PRN);
					}
					break;
				}
				setState(1048);
				fieldContent();
				setState(1049);
				match(KWD_ORDER_CLAUSE);
				setState(1051);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,221,_ctx) ) {
				case 1:
					{
					setState(1050);
					match(POS_POSESV_PRN);
					}
					break;
				}
				setState(1053);
				field(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionTwoArgsContext extends ParserRuleContext {
		public String value;
		public String targetDatatype;
		public TerminalNode FUNC_CORRELATION() { return getToken(evaEnglishParser.FUNC_CORRELATION, 0); }
		public FunctionTwoArgsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionTwoArgs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFunctionTwoArgs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFunctionTwoArgs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFunctionTwoArgs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionTwoArgsContext functionTwoArgs() throws RecognitionException {
		FunctionTwoArgsContext _localctx = new FunctionTwoArgsContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_functionTwoArgs);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1057);
			match(FUNC_CORRELATION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionOneArgContext extends ParserRuleContext {
		public String value;
		public boolean isAggregated;
		public String targetDatatype;
		public TerminalNode FUNC_STD_DEV() { return getToken(evaEnglishParser.FUNC_STD_DEV, 0); }
		public TerminalNode FUNC_VARIANCE() { return getToken(evaEnglishParser.FUNC_VARIANCE, 0); }
		public TerminalNode FUNC_ABSOLUTE() { return getToken(evaEnglishParser.FUNC_ABSOLUTE, 0); }
		public TerminalNode FUNC_SIGN() { return getToken(evaEnglishParser.FUNC_SIGN, 0); }
		public TerminalNode FUNC_SQRT() { return getToken(evaEnglishParser.FUNC_SQRT, 0); }
		public TerminalNode FUNC_EXPONENTIAL() { return getToken(evaEnglishParser.FUNC_EXPONENTIAL, 0); }
		public TerminalNode FUNC_LOG() { return getToken(evaEnglishParser.FUNC_LOG, 0); }
		public TerminalNode FUNC_ROUND() { return getToken(evaEnglishParser.FUNC_ROUND, 0); }
		public TerminalNode FUNC_COS() { return getToken(evaEnglishParser.FUNC_COS, 0); }
		public TerminalNode FUNC_SIN() { return getToken(evaEnglishParser.FUNC_SIN, 0); }
		public TerminalNode FUNC_TAN() { return getToken(evaEnglishParser.FUNC_TAN, 0); }
		public TerminalNode FUNC_MD5() { return getToken(evaEnglishParser.FUNC_MD5, 0); }
		public TerminalNode FUNC_SHA1() { return getToken(evaEnglishParser.FUNC_SHA1, 0); }
		public TerminalNode FUNC_SHA256() { return getToken(evaEnglishParser.FUNC_SHA256, 0); }
		public TerminalNode FUNC_SHA512() { return getToken(evaEnglishParser.FUNC_SHA512, 0); }
		public FunctionOneArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionOneArg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFunctionOneArg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFunctionOneArg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFunctionOneArg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionOneArgContext functionOneArg() throws RecognitionException {
		FunctionOneArgContext _localctx = new FunctionOneArgContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_functionOneArg);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1059);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC_STD_DEV) | (1L << FUNC_VARIANCE) | (1L << FUNC_ABSOLUTE) | (1L << FUNC_SIGN) | (1L << FUNC_SQRT) | (1L << FUNC_EXPONENTIAL) | (1L << FUNC_LOG) | (1L << FUNC_ROUND) | (1L << FUNC_COS) | (1L << FUNC_SIN) | (1L << FUNC_TAN) | (1L << FUNC_MD5) | (1L << FUNC_SHA1) | (1L << FUNC_SHA256) | (1L << FUNC_SHA512))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FieldContext extends ParserRuleContext {
		public String fieldText;
		public int fieldType;
		public String fieldValue;
		public String datatype;
		public String fieldAlias;
		public boolean isAggregated;
		public List<String> entityNames;
		public int dd;
		public int mm;
		public int yyyy;
		public int hh;
		public int MM;
		public int ss;
		public int SSS;
		public String tz;
		public FieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field; }
	 
		public FieldContext() { }
		public void copyFrom(FieldContext ctx) {
			super.copyFrom(ctx);
			this.fieldText = ctx.fieldText;
			this.fieldType = ctx.fieldType;
			this.fieldValue = ctx.fieldValue;
			this.datatype = ctx.datatype;
			this.fieldAlias = ctx.fieldAlias;
			this.isAggregated = ctx.isAggregated;
			this.entityNames = ctx.entityNames;
			this.dd = ctx.dd;
			this.mm = ctx.mm;
			this.yyyy = ctx.yyyy;
			this.hh = ctx.hh;
			this.MM = ctx.MM;
			this.ss = ctx.ss;
			this.SSS = ctx.SSS;
			this.tz = ctx.tz;
		}
	}
	public static class FieldOperationPlusContext extends FieldContext {
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public TerminalNode SYM_PLUS() { return getToken(evaEnglishParser.SYM_PLUS, 0); }
		public DurationContext duration() {
			return getRuleContext(DurationContext.class,0);
		}
		public FieldOperationPlusContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldOperationPlus(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldOperationPlus(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldOperationPlus(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldAggContext extends FieldContext {
		public AggregateFieldContext aggregateField() {
			return getRuleContext(AggregateFieldContext.class,0);
		}
		public TerminalNode POS_POSESV_PRN() { return getToken(evaEnglishParser.POS_POSESV_PRN, 0); }
		public ArticleContext article() {
			return getRuleContext(ArticleContext.class,0);
		}
		public FieldAggContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldAgg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldAgg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldAgg(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldDefaultContext extends FieldContext {
		public FieldContentContext fieldContent() {
			return getRuleContext(FieldContentContext.class,0);
		}
		public TerminalNode POS_POSESV_PRN() { return getToken(evaEnglishParser.POS_POSESV_PRN, 0); }
		public FieldDefaultContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldDefault(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldArticleContext extends FieldContext {
		public ArticleContext article() {
			return getRuleContext(ArticleContext.class,0);
		}
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public FieldArticleContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldArticle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldArticle(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldArticle(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldDateContext extends FieldContext {
		public DateFieldContext dateField() {
			return getRuleContext(DateFieldContext.class,0);
		}
		public FieldDateContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldDate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldDate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldDate(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldNumberContext extends FieldContext {
		public NumberFieldContext numberField() {
			return getRuleContext(NumberFieldContext.class,0);
		}
		public FieldNumberContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldNumber(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldOneArgFunctionContext extends FieldContext {
		public FunctionOneArgContext functionOneArg() {
			return getRuleContext(FunctionOneArgContext.class,0);
		}
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public ArticleContext article() {
			return getRuleContext(ArticleContext.class,0);
		}
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public FieldOneArgFunctionContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldOneArgFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldOneArgFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldOneArgFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldSubstrFunctionContext extends FieldContext {
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public TerminalNode KWD_FROM() { return getToken(evaEnglishParser.KWD_FROM, 0); }
		public TerminalNode FUNC_SUBSTRING() { return getToken(evaEnglishParser.FUNC_SUBSTRING, 0); }
		public TerminalNode KWD_PART() { return getToken(evaEnglishParser.KWD_PART, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public ArticleContext article() {
			return getRuleContext(ArticleContext.class,0);
		}
		public TerminalNode KWD_TO() { return getToken(evaEnglishParser.KWD_TO, 0); }
		public TerminalNode KWD_UNTIL() { return getToken(evaEnglishParser.KWD_UNTIL, 0); }
		public TerminalNode KWD_BETWEEN() { return getToken(evaEnglishParser.KWD_BETWEEN, 0); }
		public TerminalNode KWD_AND() { return getToken(evaEnglishParser.KWD_AND, 0); }
		public FieldSubstrFunctionContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldSubstrFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldSubstrFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldSubstrFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldDurationContext extends FieldContext {
		public DurationFieldContext durationField() {
			return getRuleContext(DurationFieldContext.class,0);
		}
		public FieldDurationContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldDuration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldDuration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldDuration(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldDivFunctionContext extends FieldContext {
		public TerminalNode KWD_DIVISION() { return getToken(evaEnglishParser.KWD_DIVISION, 0); }
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public TerminalNode KWD_BY() { return getToken(evaEnglishParser.KWD_BY, 0); }
		public TerminalNode KWD_AND() { return getToken(evaEnglishParser.KWD_AND, 0); }
		public ArticleContext article() {
			return getRuleContext(ArticleContext.class,0);
		}
		public FieldDivFunctionContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldDivFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldDivFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldDivFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldOperationMinusContext extends FieldContext {
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public TerminalNode SYM_DASH() { return getToken(evaEnglishParser.SYM_DASH, 0); }
		public DurationContext duration() {
			return getRuleContext(DurationContext.class,0);
		}
		public FieldOperationMinusContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldOperationMinus(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldOperationMinus(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldOperationMinus(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldTwoArgFunctionContext extends FieldContext {
		public FunctionTwoArgsContext functionTwoArgs() {
			return getRuleContext(FunctionTwoArgsContext.class,0);
		}
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public TerminalNode KWD_AND() { return getToken(evaEnglishParser.KWD_AND, 0); }
		public TerminalNode KWD_BETWEEN() { return getToken(evaEnglishParser.KWD_BETWEEN, 0); }
		public TerminalNode KWD_FOR() { return getToken(evaEnglishParser.KWD_FOR, 0); }
		public TerminalNode KWD_USING() { return getToken(evaEnglishParser.KWD_USING, 0); }
		public ArticleContext article() {
			return getRuleContext(ArticleContext.class,0);
		}
		public TerminalNode KWD_OF() { return getToken(evaEnglishParser.KWD_OF, 0); }
		public FieldTwoArgFunctionContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldTwoArgFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldTwoArgFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldTwoArgFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldParenContext extends FieldContext {
		public TerminalNode SYM_LPAREN() { return getToken(evaEnglishParser.SYM_LPAREN, 0); }
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public TerminalNode SYM_RPAREN() { return getToken(evaEnglishParser.SYM_RPAREN, 0); }
		public FieldParenContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldParen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldParen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldParen(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldOperationOtherContext extends FieldContext {
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public TerminalNode SYM_F_SLASH() { return getToken(evaEnglishParser.SYM_F_SLASH, 0); }
		public TerminalNode SYM_STAR() { return getToken(evaEnglishParser.SYM_STAR, 0); }
		public TerminalNode SYM_PERCENT() { return getToken(evaEnglishParser.SYM_PERCENT, 0); }
		public TerminalNode SYM_CARET() { return getToken(evaEnglishParser.SYM_CARET, 0); }
		public FieldOperationOtherContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldOperationOther(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldOperationOther(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldOperationOther(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldOperationDurationContext extends FieldContext {
		public DurationContext duration() {
			return getRuleContext(DurationContext.class,0);
		}
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public TerminalNode SYM_PLUS() { return getToken(evaEnglishParser.SYM_PLUS, 0); }
		public TerminalNode SYM_DASH() { return getToken(evaEnglishParser.SYM_DASH, 0); }
		public FieldOperationDurationContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldOperationDuration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldOperationDuration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldOperationDuration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FieldContext field() throws RecognitionException {
		return field(0);
	}

	private FieldContext field(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		FieldContext _localctx = new FieldContext(_ctx, _parentState);
		FieldContext _prevctx = _localctx;
		int _startState = 48;
		enterRecursionRule(_localctx, 48, RULE_field, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1151);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,238,_ctx) ) {
			case 1:
				{
				_localctx = new FieldParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(1062);
				match(SYM_LPAREN);
				setState(1063);
				field(0);
				setState(1064);
				match(SYM_RPAREN);
				}
				break;
			case 2:
				{
				_localctx = new FieldDateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1066);
				dateField();
				}
				break;
			case 3:
				{
				_localctx = new FieldOperationDurationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1067);
				duration(0);
				setState(1068);
				_la = _input.LA(1);
				if ( !(_la==SYM_PLUS || _la==SYM_DASH) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1069);
				field(15);
				}
				break;
			case 4:
				{
				_localctx = new FieldOneArgFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1072);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ARTICLE_A) | (1L << ARTICLE_AN) | (1L << ARTICLE_THE))) != 0)) {
					{
					setState(1071);
					article();
					}
				}

				setState(1074);
				functionOneArg();
				setState(1076);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,224,_ctx) ) {
				case 1:
					{
					setState(1075);
					match(KWD_OF);
					}
					break;
				}
				setState(1078);
				field(11);
				}
				break;
			case 5:
				{
				_localctx = new FieldSubstrFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1081);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ARTICLE_A) | (1L << ARTICLE_AN) | (1L << ARTICLE_THE))) != 0)) {
					{
					setState(1080);
					article();
					}
				}

				setState(1089);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case FUNC_SUBSTRING:
					{
					setState(1083);
					match(FUNC_SUBSTRING);
					setState(1085);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,226,_ctx) ) {
					case 1:
						{
						setState(1084);
						match(KWD_OF);
						}
						break;
					}
					}
					break;
				case KWD_PART:
					{
					setState(1087);
					match(KWD_PART);
					setState(1088);
					match(KWD_OF);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(1091);
				field(0);
				setState(1092);
				match(KWD_FROM);
				setState(1093);
				field(0);
				setState(1096);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,228,_ctx) ) {
				case 1:
					{
					setState(1094);
					_la = _input.LA(1);
					if ( !(_la==KWD_TO || _la==KWD_UNTIL) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(1095);
					field(0);
					}
					break;
				}
				}
				break;
			case 6:
				{
				_localctx = new FieldSubstrFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1099);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ARTICLE_A) | (1L << ARTICLE_AN) | (1L << ARTICLE_THE))) != 0)) {
					{
					setState(1098);
					article();
					}
				}

				setState(1107);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case FUNC_SUBSTRING:
					{
					setState(1101);
					match(FUNC_SUBSTRING);
					setState(1103);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,230,_ctx) ) {
					case 1:
						{
						setState(1102);
						match(KWD_OF);
						}
						break;
					}
					}
					break;
				case KWD_PART:
					{
					setState(1105);
					match(KWD_PART);
					setState(1106);
					match(KWD_OF);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(1109);
				field(0);
				{
				setState(1110);
				match(KWD_BETWEEN);
				}
				setState(1111);
				field(0);
				setState(1114);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,232,_ctx) ) {
				case 1:
					{
					setState(1112);
					match(KWD_AND);
					setState(1113);
					field(0);
					}
					break;
				}
				}
				break;
			case 7:
				{
				_localctx = new FieldDivFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1117);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ARTICLE_A) | (1L << ARTICLE_AN) | (1L << ARTICLE_THE))) != 0)) {
					{
					setState(1116);
					article();
					}
				}

				setState(1119);
				match(KWD_DIVISION);
				setState(1120);
				match(KWD_OF);
				setState(1121);
				field(0);
				setState(1122);
				_la = _input.LA(1);
				if ( !(_la==KWD_AND || _la==KWD_BY) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1123);
				field(7);
				}
				break;
			case 8:
				{
				_localctx = new FieldTwoArgFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1126);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ARTICLE_A) | (1L << ARTICLE_AN) | (1L << ARTICLE_THE))) != 0)) {
					{
					setState(1125);
					article();
					}
				}

				setState(1128);
				functionTwoArgs();
				setState(1130);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==KWD_OF) {
					{
					setState(1129);
					match(KWD_OF);
					}
				}

				setState(1132);
				_la = _input.LA(1);
				if ( !(_la==KWD_BETWEEN || _la==KWD_USING || _la==KWD_FOR) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1133);
				field(0);
				setState(1134);
				match(KWD_AND);
				setState(1135);
				field(6);
				}
				break;
			case 9:
				{
				_localctx = new FieldNumberContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1137);
				numberField();
				}
				break;
			case 10:
				{
				_localctx = new FieldDurationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1138);
				durationField();
				}
				break;
			case 11:
				{
				_localctx = new FieldAggContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1141);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,236,_ctx) ) {
				case 1:
					{
					setState(1139);
					match(POS_POSESV_PRN);
					}
					break;
				case 2:
					{
					setState(1140);
					article();
					}
					break;
				}
				setState(1143);
				aggregateField();
				}
				break;
			case 12:
				{
				_localctx = new FieldDefaultContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1145);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,237,_ctx) ) {
				case 1:
					{
					setState(1144);
					match(POS_POSESV_PRN);
					}
					break;
				}
				setState(1147);
				fieldContent();
				}
				break;
			case 13:
				{
				_localctx = new FieldArticleContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1148);
				article();
				setState(1149);
				field(1);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(1172);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,242,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(1170);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,241,_ctx) ) {
					case 1:
						{
						_localctx = new FieldOperationOtherContext(new FieldContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_field);
						setState(1153);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(1154);
						_la = _input.LA(1);
						if ( !(((((_la - 74)) & ~0x3f) == 0 && ((1L << (_la - 74)) & ((1L << (SYM_STAR - 74)) | (1L << (SYM_F_SLASH - 74)) | (1L << (SYM_PERCENT - 74)) | (1L << (SYM_CARET - 74)))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1155);
						field(13);
						}
						break;
					case 2:
						{
						_localctx = new FieldOperationPlusContext(new FieldContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_field);
						setState(1156);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(1157);
						match(SYM_PLUS);
						setState(1160);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,239,_ctx) ) {
						case 1:
							{
							setState(1158);
							duration(0);
							}
							break;
						case 2:
							{
							setState(1159);
							field(0);
							}
							break;
						}
						}
						break;
					case 3:
						{
						_localctx = new FieldOperationMinusContext(new FieldContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_field);
						setState(1162);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(1163);
						match(SYM_DASH);
						setState(1166);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,240,_ctx) ) {
						case 1:
							{
							setState(1164);
							duration(0);
							}
							break;
						case 2:
							{
							setState(1165);
							field(0);
							}
							break;
						}
						}
						break;
					case 4:
						{
						_localctx = new FieldOneArgFunctionContext(new FieldContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_field);
						setState(1168);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(1169);
						functionOneArg();
						}
						break;
					}
					} 
				}
				setState(1174);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,242,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class FieldContentContext extends ParserRuleContext {
		public String fieldText;
		public int fieldType;
		public String fieldValue;
		public String fieldAlias;
		public String datatype;
		public String columnName;
		public String tableName;
		public FieldContentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldContent; }
	 
		public FieldContentContext() { }
		public void copyFrom(FieldContentContext ctx) {
			super.copyFrom(ctx);
			this.fieldText = ctx.fieldText;
			this.fieldType = ctx.fieldType;
			this.fieldValue = ctx.fieldValue;
			this.fieldAlias = ctx.fieldAlias;
			this.datatype = ctx.datatype;
			this.columnName = ctx.columnName;
			this.tableName = ctx.tableName;
		}
	}
	public static class FieldContentQualifiedContext extends FieldContentContext {
		public List<TerminalNode> BOXED_STRING() { return getTokens(evaEnglishParser.BOXED_STRING); }
		public TerminalNode BOXED_STRING(int i) {
			return getToken(evaEnglishParser.BOXED_STRING, i);
		}
		public FieldContentQualifiedContext(FieldContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldContentQualified(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldContentQualified(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldContentQualified(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldContentDefaultContext extends FieldContentContext {
		public List<TerminalNode> WS() { return getTokens(evaEnglishParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(evaEnglishParser.WS, i);
		}
		public List<TerminalNode> KWD_AND() { return getTokens(evaEnglishParser.KWD_AND); }
		public TerminalNode KWD_AND(int i) {
			return getToken(evaEnglishParser.KWD_AND, i);
		}
		public List<TerminalNode> KWD_OR() { return getTokens(evaEnglishParser.KWD_OR); }
		public TerminalNode KWD_OR(int i) {
			return getToken(evaEnglishParser.KWD_OR, i);
		}
		public List<TerminalNode> KWD_BUT() { return getTokens(evaEnglishParser.KWD_BUT); }
		public TerminalNode KWD_BUT(int i) {
			return getToken(evaEnglishParser.KWD_BUT, i);
		}
		public FieldContentDefaultContext(FieldContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldContentDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldContentDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldContentDefault(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldContentQuotedContext extends FieldContentContext {
		public TerminalNode QUOTED_STRING() { return getToken(evaEnglishParser.QUOTED_STRING, 0); }
		public FieldContentQuotedContext(FieldContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterFieldContentQuoted(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitFieldContentQuoted(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitFieldContentQuoted(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FieldContentContext fieldContent() throws RecognitionException {
		FieldContentContext _localctx = new FieldContentContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_fieldContent);
		int _la;
		try {
			int _alt;
			setState(1190);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,246,_ctx) ) {
			case 1:
				_localctx = new FieldContentQualifiedContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(1175);
				match(BOXED_STRING);
				setState(1176);
				match(BOXED_STRING);
				setState(1177);
				match(BOXED_STRING);
				}
				break;
			case 2:
				_localctx = new FieldContentQuotedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(1178);
				match(QUOTED_STRING);
				}
				break;
			case 3:
				_localctx = new FieldContentDefaultContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(1180);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,243,_ctx) ) {
				case 1:
					{
					setState(1179);
					match(WS);
					}
					break;
				}
				setState(1183); 
				_errHandler.sync(this);
				_alt = 1+1;
				do {
					switch (_alt) {
					case 1+1:
						{
						{
						setState(1182);
						_la = _input.LA(1);
						if ( _la <= 0 || (((((_la - 232)) & ~0x3f) == 0 && ((1L << (_la - 232)) & ((1L << (KWD_AND - 232)) | (1L << (KWD_BUT - 232)) | (1L << (KWD_OR - 232)))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(1185); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,244,_ctx);
				} while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(1188);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,245,_ctx) ) {
				case 1:
					{
					setState(1187);
					match(WS);
					}
					break;
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArticleContext extends ParserRuleContext {
		public TerminalNode ARTICLE_A() { return getToken(evaEnglishParser.ARTICLE_A, 0); }
		public TerminalNode ARTICLE_AN() { return getToken(evaEnglishParser.ARTICLE_AN, 0); }
		public TerminalNode ARTICLE_THE() { return getToken(evaEnglishParser.ARTICLE_THE, 0); }
		public ArticleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_article; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).enterArticle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof evaEnglishListener ) ((evaEnglishListener)listener).exitArticle(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof evaEnglishVisitor ) return ((evaEnglishVisitor<? extends T>)visitor).visitArticle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArticleContext article() throws RecognitionException {
		ArticleContext _localctx = new ArticleContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_article);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1192);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ARTICLE_A) | (1L << ARTICLE_AN) | (1L << ARTICLE_THE))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 3:
			return criteriaClause_sempred((CriteriaClauseContext)_localctx, predIndex);
		case 11:
			return duration_sempred((DurationContext)_localctx, predIndex);
		case 15:
			return time_sempred((TimeContext)_localctx, predIndex);
		case 24:
			return field_sempred((FieldContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean criteriaClause_sempred(CriteriaClauseContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 10);
		case 1:
			return precpred(_ctx, 9);
		case 2:
			return precpred(_ctx, 8);
		case 3:
			return precpred(_ctx, 7);
		}
		return true;
	}
	private boolean duration_sempred(DurationContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return precpred(_ctx, 8);
		}
		return true;
	}
	private boolean time_sempred(TimeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return precpred(_ctx, 4);
		case 6:
			return precpred(_ctx, 3);
		}
		return true;
	}
	private boolean field_sempred(FieldContext _localctx, int predIndex) {
		switch (predIndex) {
		case 7:
			return precpred(_ctx, 12);
		case 8:
			return precpred(_ctx, 14);
		case 9:
			return precpred(_ctx, 13);
		case 10:
			return precpred(_ctx, 10);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\u015d\u04ad\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\2\3\2\3\2\5\2>\n\2\3\2\5\2A\n"+
		"\2\3\2\5\2D\n\2\3\3\3\3\5\3H\n\3\3\3\3\3\3\3\3\3\5\3N\n\3\3\4\3\4\5\4"+
		"R\n\4\3\4\3\4\3\4\5\4W\n\4\7\4Y\n\4\f\4\16\4\\\13\4\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\5\5d\n\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5m\n\5\3\5\3\5\5\5q\n\5"+
		"\3\5\3\5\3\5\3\5\3\5\5\5x\n\5\3\5\3\5\3\5\5\5}\n\5\3\5\3\5\5\5\u0081\n"+
		"\5\3\5\3\5\5\5\u0085\n\5\3\5\3\5\3\5\3\5\5\5\u008b\n\5\3\5\3\5\5\5\u008f"+
		"\n\5\3\5\3\5\3\5\3\5\5\5\u0095\n\5\3\5\3\5\3\5\3\5\5\5\u009b\n\5\3\5\7"+
		"\5\u009e\n\5\f\5\16\5\u00a1\13\5\3\6\3\6\3\6\3\6\3\6\5\6\u00a8\n\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\5\6\u00b0\n\6\3\6\5\6\u00b3\n\6\3\6\3\6\5\6\u00b7"+
		"\n\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6\u00bf\n\6\3\6\5\6\u00c2\n\6\3\6\3\6\5"+
		"\6\u00c6\n\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6\u00ce\n\6\3\6\5\6\u00d1\n\6\3"+
		"\6\3\6\5\6\u00d5\n\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6\u00dd\n\6\3\6\5\6\u00e0"+
		"\n\6\3\6\3\6\5\6\u00e4\n\6\3\6\3\6\3\6\5\6\u00e9\n\6\3\6\5\6\u00ec\n\6"+
		"\3\6\3\6\5\6\u00f0\n\6\3\6\3\6\3\6\5\6\u00f5\n\6\3\6\5\6\u00f8\n\6\3\6"+
		"\3\6\3\6\3\6\5\6\u00fe\n\6\3\7\5\7\u0101\n\7\3\7\5\7\u0104\n\7\3\7\3\7"+
		"\3\7\3\7\5\7\u010a\n\7\3\7\5\7\u010d\n\7\3\7\3\7\3\7\3\7\5\7\u0113\n\7"+
		"\3\7\5\7\u0116\n\7\3\7\3\7\5\7\u011a\n\7\3\b\3\b\5\b\u011e\n\b\3\b\3\b"+
		"\5\b\u0122\n\b\3\b\5\b\u0125\n\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u012e"+
		"\n\b\3\b\3\b\5\b\u0132\n\b\3\b\5\b\u0135\n\b\3\b\3\b\3\b\5\b\u013a\n\b"+
		"\3\b\3\b\3\b\3\b\3\b\5\b\u0141\n\b\3\b\3\b\5\b\u0145\n\b\3\b\3\b\3\b\3"+
		"\b\5\b\u014b\n\b\3\b\5\b\u014e\n\b\3\b\3\b\3\b\3\b\5\b\u0154\n\b\7\b\u0156"+
		"\n\b\f\b\16\b\u0159\13\b\3\b\3\b\5\b\u015d\n\b\3\b\3\b\5\b\u0161\n\b\3"+
		"\b\3\b\3\b\3\b\5\b\u0167\n\b\3\b\5\b\u016a\n\b\3\b\3\b\3\b\3\b\5\b\u0170"+
		"\n\b\7\b\u0172\n\b\f\b\16\b\u0175\13\b\3\b\3\b\5\b\u0179\n\b\3\b\3\b\5"+
		"\b\u017d\n\b\3\b\3\b\3\b\3\b\5\b\u0183\n\b\3\b\5\b\u0186\n\b\3\b\3\b\3"+
		"\b\3\b\5\b\u018c\n\b\7\b\u018e\n\b\f\b\16\b\u0191\13\b\3\b\3\b\5\b\u0195"+
		"\n\b\3\b\3\b\5\b\u0199\n\b\3\b\3\b\3\b\3\b\5\b\u019f\n\b\3\b\5\b\u01a2"+
		"\n\b\3\b\3\b\3\b\3\b\5\b\u01a8\n\b\7\b\u01aa\n\b\f\b\16\b\u01ad\13\b\3"+
		"\b\3\b\5\b\u01b1\n\b\3\b\3\b\5\b\u01b5\n\b\3\b\5\b\u01b8\n\b\3\b\5\b\u01bb"+
		"\n\b\3\b\3\b\3\b\3\b\5\b\u01c1\n\b\3\b\3\b\5\b\u01c5\n\b\3\b\5\b\u01c8"+
		"\n\b\3\b\3\b\3\b\3\b\3\b\5\b\u01cf\n\b\7\b\u01d1\n\b\f\b\16\b\u01d4\13"+
		"\b\3\b\3\b\5\b\u01d8\n\b\3\b\3\b\5\b\u01dc\n\b\3\b\5\b\u01df\n\b\3\b\5"+
		"\b\u01e2\n\b\5\b\u01e4\n\b\3\b\3\b\3\b\3\b\3\b\5\b\u01eb\n\b\7\b\u01ed"+
		"\n\b\f\b\16\b\u01f0\13\b\3\b\5\b\u01f3\n\b\3\b\3\b\5\b\u01f7\n\b\3\b\3"+
		"\b\3\b\3\b\5\b\u01fd\n\b\3\b\3\b\3\b\3\b\5\b\u0203\n\b\5\b\u0205\n\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u020f\n\b\5\b\u0211\n\b\3\b\3\b\5\b"+
		"\u0215\n\b\3\b\3\b\5\b\u0219\n\b\3\b\3\b\5\b\u021d\n\b\3\b\3\b\3\b\3\b"+
		"\3\b\5\b\u0224\n\b\3\b\3\b\3\b\3\b\5\b\u022a\n\b\7\b\u022c\n\b\f\b\16"+
		"\b\u022f\13\b\5\b\u0231\n\b\3\t\3\t\3\t\3\t\3\t\5\t\u0238\n\t\3\t\5\t"+
		"\u023b\n\t\5\t\u023d\n\t\3\n\3\n\5\n\u0241\n\n\3\n\5\n\u0244\n\n\3\13"+
		"\3\13\3\13\5\13\u0249\n\13\5\13\u024b\n\13\3\f\3\f\3\r\3\r\5\r\u0251\n"+
		"\r\3\r\3\r\5\r\u0255\n\r\3\r\3\r\5\r\u0259\n\r\3\r\3\r\5\r\u025d\n\r\3"+
		"\r\3\r\5\r\u0261\n\r\3\r\3\r\5\r\u0265\n\r\3\r\3\r\5\r\u0269\n\r\3\r\5"+
		"\r\u026c\n\r\3\r\3\r\5\r\u0270\n\r\3\r\6\r\u0273\n\r\r\r\16\r\u0274\7"+
		"\r\u0277\n\r\f\r\16\r\u027a\13\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\5\16\u0286\n\16\3\16\3\16\5\16\u028a\n\16\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\5\17\u0293\n\17\3\17\5\17\u0296\n\17\3\17\5\17\u0299"+
		"\n\17\3\17\5\17\u029c\n\17\3\17\5\17\u029f\n\17\3\17\3\17\3\17\5\17\u02a4"+
		"\n\17\5\17\u02a6\n\17\3\17\5\17\u02a9\n\17\3\17\5\17\u02ac\n\17\3\17\3"+
		"\17\3\17\5\17\u02b1\n\17\3\17\3\17\5\17\u02b5\n\17\3\17\3\17\3\17\3\17"+
		"\5\17\u02bb\n\17\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u02c3\n\17\3\17\3"+
		"\17\5\17\u02c7\n\17\3\20\3\20\5\20\u02cb\n\20\3\20\5\20\u02ce\n\20\3\20"+
		"\5\20\u02d1\n\20\3\21\3\21\3\21\5\21\u02d6\n\21\3\21\3\21\5\21\u02da\n"+
		"\21\3\21\5\21\u02dd\n\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21\u02e5\n\21"+
		"\3\21\5\21\u02e8\n\21\3\21\3\21\3\21\3\21\5\21\u02ee\n\21\5\21\u02f0\n"+
		"\21\3\21\3\21\5\21\u02f4\n\21\3\21\3\21\5\21\u02f8\n\21\3\21\3\21\5\21"+
		"\u02fc\n\21\3\21\3\21\5\21\u0300\n\21\3\21\5\21\u0303\n\21\7\21\u0305"+
		"\n\21\f\21\16\21\u0308\13\21\3\22\5\22\u030b\n\22\3\22\3\22\3\22\3\22"+
		"\5\22\u0311\n\22\3\23\3\23\5\23\u0315\n\23\3\23\5\23\u0318\n\23\3\23\5"+
		"\23\u031b\n\23\3\23\3\23\3\23\5\23\u0320\n\23\3\23\5\23\u0323\n\23\3\23"+
		"\5\23\u0326\n\23\3\23\5\23\u0329\n\23\3\23\5\23\u032c\n\23\3\23\5\23\u032f"+
		"\n\23\3\23\5\23\u0332\n\23\3\23\3\23\3\23\5\23\u0337\n\23\3\23\5\23\u033a"+
		"\n\23\3\23\5\23\u033d\n\23\3\23\5\23\u0340\n\23\3\23\5\23\u0343\n\23\3"+
		"\23\5\23\u0346\n\23\3\23\5\23\u0349\n\23\3\23\3\23\3\23\5\23\u034e\n\23"+
		"\3\23\5\23\u0351\n\23\3\23\5\23\u0354\n\23\3\23\5\23\u0357\n\23\3\23\5"+
		"\23\u035a\n\23\3\23\3\23\3\23\5\23\u035f\n\23\3\23\5\23\u0362\n\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\5\23\u0374\n\23\3\24\3\24\5\24\u0378\n\24\3\24\5\24\u037b\n\24\3"+
		"\24\5\24\u037e\n\24\3\24\3\24\3\24\5\24\u0383\n\24\3\24\5\24\u0386\n\24"+
		"\3\24\5\24\u0389\n\24\3\24\5\24\u038c\n\24\3\24\5\24\u038f\n\24\3\24\5"+
		"\24\u0392\n\24\3\24\5\24\u0395\n\24\3\24\3\24\3\24\5\24\u039a\n\24\3\24"+
		"\5\24\u039d\n\24\3\24\5\24\u03a0\n\24\3\24\5\24\u03a3\n\24\3\24\5\24\u03a6"+
		"\n\24\3\24\5\24\u03a9\n\24\3\24\5\24\u03ac\n\24\3\24\3\24\3\24\5\24\u03b1"+
		"\n\24\3\24\5\24\u03b4\n\24\3\24\5\24\u03b7\n\24\3\24\5\24\u03ba\n\24\3"+
		"\24\5\24\u03bd\n\24\3\24\3\24\3\24\5\24\u03c2\n\24\3\24\5\24\u03c5\n\24"+
		"\3\24\5\24\u03c8\n\24\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u03d0\n\25\3"+
		"\26\3\26\3\26\3\26\5\26\u03d6\n\26\3\26\3\26\3\26\3\26\3\26\5\26\u03dd"+
		"\n\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u03e9\n\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u03f3\n\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\5\26\u0409\n\26\3\27\3\27\5\27\u040d\n\27\3\27\3\27\3"+
		"\27\5\27\u0412\n\27\3\27\5\27\u0415\n\27\3\27\3\27\5\27\u0419\n\27\3\27"+
		"\3\27\3\27\5\27\u041e\n\27\3\27\3\27\5\27\u0422\n\27\3\30\3\30\3\31\3"+
		"\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\5\32\u0433"+
		"\n\32\3\32\3\32\5\32\u0437\n\32\3\32\3\32\3\32\5\32\u043c\n\32\3\32\3"+
		"\32\5\32\u0440\n\32\3\32\3\32\5\32\u0444\n\32\3\32\3\32\3\32\3\32\3\32"+
		"\5\32\u044b\n\32\3\32\5\32\u044e\n\32\3\32\3\32\5\32\u0452\n\32\3\32\3"+
		"\32\5\32\u0456\n\32\3\32\3\32\3\32\3\32\3\32\5\32\u045d\n\32\3\32\5\32"+
		"\u0460\n\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\5\32\u0469\n\32\3\32\3"+
		"\32\5\32\u046d\n\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\5\32"+
		"\u0478\n\32\3\32\3\32\5\32\u047c\n\32\3\32\3\32\3\32\3\32\5\32\u0482\n"+
		"\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\5\32\u048b\n\32\3\32\3\32\3\32"+
		"\3\32\5\32\u0491\n\32\3\32\3\32\7\32\u0495\n\32\f\32\16\32\u0498\13\32"+
		"\3\33\3\33\3\33\3\33\3\33\5\33\u049f\n\33\3\33\6\33\u04a2\n\33\r\33\16"+
		"\33\u04a3\3\33\5\33\u04a7\n\33\5\33\u04a9\n\33\3\34\3\34\3\34\3\u04a3"+
		"\6\b\30 \62\35\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64"+
		"\66\2/\6\2SS\u00ea\u00ea\u00ee\u00ee\u0110\u0110\4\2%&\u010d\u010d\4\2"+
		"\u00ea\u00ea\u00ed\u00ed\3\2\u00c5\u00c6\4\2\u0112\u0112\u0115\u0115\3"+
		"\2RS\3\2HI\4\2FF\u0124\u0125\3\2EF\4\2AAII\3\2GH\4\2BB\u0124\u0125\5\2"+
		"\u0115\u0115\u0122\u0122\u012a\u012a\5\2CC\u0115\u0115\u0122\u0122\3\2"+
		"%&\3\2\u0089\u008b\4\2SS\u011e\u011e\4\2DD\u0115\u0115\4\2\'\'**\4\2("+
		")++\3\2\u00bf\u00c0\4\2\u010d\u010d\u011d\u011d\4\2\u00be\u00be\u00c2"+
		"\u00c4\3\2\u012b\u012c\3\2\u014e\u0151\3\2\u0152\u0153\3\2\u0154\u0155"+
		"\3\2OP\4\2RR\\\\\4\2==\u00ad\u00ad\5\2\u0112\u0112\u0115\u0115\u0117\u0117"+
		"\4\2>>\u00a3\u00a3\4\2<<\u009b\u009b\4\2\u008e\u008e\u00b2\u00b2\4\2\u00a7"+
		"\u00a7\u012c\u012c\3\2\u0095\u0096\3\2\u0097\u0098\5\2\60\60\u00ee\u00ee"+
		"\u0110\u0110\3\2\6\24\4\2\u011d\u011d\u0124\u0124\4\2\u00ea\u00ea\u0110"+
		"\u0110\4\2\u00c5\u00c5\u0116\u0117\4\2LM^_\5\2\u00ea\u00ea\u00ed\u00ed"+
		"\u011e\u011e\3\2\61\63\2\u05e5\28\3\2\2\2\4M\3\2\2\2\6Q\3\2\2\2\b|\3\2"+
		"\2\2\n\u00fd\3\2\2\2\f\u0119\3\2\2\2\16\u0230\3\2\2\2\20\u023c\3\2\2\2"+
		"\22\u023e\3\2\2\2\24\u024a\3\2\2\2\26\u024c\3\2\2\2\30\u026b\3\2\2\2\32"+
		"\u0289\3\2\2\2\34\u02c6\3\2\2\2\36\u02c8\3\2\2\2 \u02ef\3\2\2\2\"\u030a"+
		"\3\2\2\2$\u0373\3\2\2\2&\u03c7\3\2\2\2(\u03cf\3\2\2\2*\u0408\3\2\2\2,"+
		"\u0421\3\2\2\2.\u0423\3\2\2\2\60\u0425\3\2\2\2\62\u0481\3\2\2\2\64\u04a8"+
		"\3\2\2\2\66\u04aa\3\2\2\28@\5\4\3\29>\7%\2\2:>\7&\2\2;>\7\u010d\2\2<>"+
		"\5\22\n\2=9\3\2\2\2=:\3\2\2\2=;\3\2\2\2=<\3\2\2\2>?\3\2\2\2?A\5\b\5\2"+
		"@=\3\2\2\2@A\3\2\2\2AC\3\2\2\2BD\7`\2\2CB\3\2\2\2CD\3\2\2\2D\3\3\2\2\2"+
		"EG\7&\2\2FH\5\22\n\2GF\3\2\2\2GH\3\2\2\2HI\3\2\2\2IN\5\6\4\2JK\7\3\2\2"+
		"KN\5\6\4\2LN\5\6\4\2ME\3\2\2\2MJ\3\2\2\2ML\3\2\2\2N\5\3\2\2\2OR\5*\26"+
		"\2PR\5\62\32\2QO\3\2\2\2QP\3\2\2\2RZ\3\2\2\2SV\t\2\2\2TW\5*\26\2UW\5\62"+
		"\32\2VT\3\2\2\2VU\3\2\2\2WY\3\2\2\2XS\3\2\2\2Y\\\3\2\2\2ZX\3\2\2\2Z[\3"+
		"\2\2\2[\7\3\2\2\2\\Z\3\2\2\2]^\b\5\1\2^_\7T\2\2_`\5\b\5\2`a\7U\2\2a}\3"+
		"\2\2\2bd\t\3\2\2cb\3\2\2\2cd\3\2\2\2de\3\2\2\2ef\5\20\t\2fg\7T\2\2gh\5"+
		"\b\5\2hi\7U\2\2i}\3\2\2\2jl\t\3\2\2km\5\22\n\2lk\3\2\2\2lm\3\2\2\2mn\3"+
		"\2\2\2n}\5\b\5\7oq\7\u00eb\2\2po\3\2\2\2pq\3\2\2\2qr\3\2\2\2rs\5\22\n"+
		"\2st\5\b\5\6t}\3\2\2\2ux\7\u010e\2\2vx\5\20\t\2wu\3\2\2\2wv\3\2\2\2xy"+
		"\3\2\2\2y}\5\n\6\2z}\5\n\6\2{}\5\16\b\2|]\3\2\2\2|c\3\2\2\2|j\3\2\2\2"+
		"|p\3\2\2\2|w\3\2\2\2|z\3\2\2\2|{\3\2\2\2}\u009f\3\2\2\2~\u0080\f\f\2\2"+
		"\177\u0081\7\u00eb\2\2\u0080\177\3\2\2\2\u0080\u0081\3\2\2\2\u0081\u0082"+
		"\3\2\2\2\u0082\u0084\t\3\2\2\u0083\u0085\7\u00eb\2\2\u0084\u0083\3\2\2"+
		"\2\u0084\u0085\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u009e\5\b\5\r\u0087\u0088"+
		"\f\13\2\2\u0088\u008a\t\4\2\2\u0089\u008b\7\u00eb\2\2\u008a\u0089\3\2"+
		"\2\2\u008a\u008b\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008e\t\3\2\2\u008d"+
		"\u008f\7\u00eb\2\2\u008e\u008d\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0090"+
		"\3\2\2\2\u0090\u009e\5\b\5\f\u0091\u0092\f\n\2\2\u0092\u0094\t\4\2\2\u0093"+
		"\u0095\7\u00eb\2\2\u0094\u0093\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0096"+
		"\3\2\2\2\u0096\u009e\5\b\5\13\u0097\u0098\f\t\2\2\u0098\u009a\7\u011e"+
		"\2\2\u0099\u009b\t\3\2\2\u009a\u0099\3\2\2\2\u009a\u009b\3\2\2\2\u009b"+
		"\u009c\3\2\2\2\u009c\u009e\5\b\5\n\u009d~\3\2\2\2\u009d\u0087\3\2\2\2"+
		"\u009d\u0091\3\2\2\2\u009d\u0097\3\2\2\2\u009e\u00a1\3\2\2\2\u009f\u009d"+
		"\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\t\3\2\2\2\u00a1\u009f\3\2\2\2\u00a2"+
		"\u00a3\t\5\2\2\u00a3\u00a4\5(\25\2\u00a4\u00a5\7\u00ea\2\2\u00a5\u00a7"+
		"\5(\25\2\u00a6\u00a8\t\6\2\2\u00a7\u00a6\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8"+
		"\u00a9\3\2\2\2\u00a9\u00aa\5\62\32\2\u00aa\u00fe\3\2\2\2\u00ab\u00ac\5"+
		"\20\t\2\u00ac\u00ad\7B\2\2\u00ad\u00b3\3\2\2\2\u00ae\u00b0\5\22\n\2\u00af"+
		"\u00ae\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b3\7G"+
		"\2\2\u00b2\u00ab\3\2\2\2\u00b2\u00af\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4"+
		"\u00b6\5(\25\2\u00b5\u00b7\t\6\2\2\u00b6\u00b5\3\2\2\2\u00b6\u00b7\3\2"+
		"\2\2\u00b7\u00b8\3\2\2\2\u00b8\u00b9\5\62\32\2\u00b9\u00fe\3\2\2\2\u00ba"+
		"\u00bb\5\20\t\2\u00bb\u00bc\7A\2\2\u00bc\u00c2\3\2\2\2\u00bd\u00bf\5\22"+
		"\n\2\u00be\u00bd\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0"+
		"\u00c2\7E\2\2\u00c1\u00ba\3\2\2\2\u00c1\u00be\3\2\2\2\u00c2\u00c3\3\2"+
		"\2\2\u00c3\u00c5\5(\25\2\u00c4\u00c6\t\6\2\2\u00c5\u00c4\3\2\2\2\u00c5"+
		"\u00c6\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7\u00c8\5\62\32\2\u00c8\u00fe\3"+
		"\2\2\2\u00c9\u00ca\5\20\t\2\u00ca\u00cb\7E\2\2\u00cb\u00d1\3\2\2\2\u00cc"+
		"\u00ce\5\22\n\2\u00cd\u00cc\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce\u00cf\3"+
		"\2\2\2\u00cf\u00d1\7A\2\2\u00d0\u00c9\3\2\2\2\u00d0\u00cd\3\2\2\2\u00d1"+
		"\u00d2\3\2\2\2\u00d2\u00d4\5(\25\2\u00d3\u00d5\t\6\2\2\u00d4\u00d3\3\2"+
		"\2\2\u00d4\u00d5\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6\u00d7\5\62\32\2\u00d7"+
		"\u00fe\3\2\2\2\u00d8\u00d9\5\20\t\2\u00d9\u00da\7G\2\2\u00da\u00e0\3\2"+
		"\2\2\u00db\u00dd\5\22\n\2\u00dc\u00db\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd"+
		"\u00de\3\2\2\2\u00de\u00e0\7B\2\2\u00df\u00d8\3\2\2\2\u00df\u00dc\3\2"+
		"\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e3\5(\25\2\u00e2\u00e4\t\6\2\2\u00e3"+
		"\u00e2\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\u00e6\5\62"+
		"\32\2\u00e6\u00fe\3\2\2\2\u00e7\u00e9\5\20\t\2\u00e8\u00e7\3\2\2\2\u00e8"+
		"\u00e9\3\2\2\2\u00e9\u00eb\3\2\2\2\u00ea\u00ec\7D\2\2\u00eb\u00ea\3\2"+
		"\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00ef\5(\25\2\u00ee"+
		"\u00f0\t\6\2\2\u00ef\u00ee\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00f1\3\2"+
		"\2\2\u00f1\u00f2\5\62\32\2\u00f2\u00fe\3\2\2\2\u00f3\u00f5\5\20\t\2\u00f4"+
		"\u00f3\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\u00f7\3\2\2\2\u00f6\u00f8\7D"+
		"\2\2\u00f7\u00f6\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9"+
		"\u00fa\5\62\32\2\u00fa\u00fb\7\u0112\2\2\u00fb\u00fc\5(\25\2\u00fc\u00fe"+
		"\3\2\2\2\u00fd\u00a2\3\2\2\2\u00fd\u00b2\3\2\2\2\u00fd\u00c1\3\2\2\2\u00fd"+
		"\u00d0\3\2\2\2\u00fd\u00df\3\2\2\2\u00fd\u00e8\3\2\2\2\u00fd\u00f4\3\2"+
		"\2\2\u00fe\13\3\2\2\2\u00ff\u0101\7K\2\2\u0100\u00ff\3\2\2\2\u0100\u0101"+
		"\3\2\2\2\u0101\u0103\3\2\2\2\u0102\u0104\t\7\2\2\u0103\u0102\3\2\2\2\u0103"+
		"\u0104\3\2\2\2\u0104\u0105\3\2\2\2\u0105\u0106\5\34\17\2\u0106\u0107\5"+
		" \21\2\u0107\u011a\3\2\2\2\u0108\u010a\7K\2\2\u0109\u0108\3\2\2\2\u0109"+
		"\u010a\3\2\2\2\u010a\u010c\3\2\2\2\u010b\u010d\t\7\2\2\u010c\u010b\3\2"+
		"\2\2\u010c\u010d\3\2\2\2\u010d\u010e\3\2\2\2\u010e\u010f\5 \21\2\u010f"+
		"\u0110\5\34\17\2\u0110\u011a\3\2\2\2\u0111\u0113\7K\2\2\u0112\u0111\3"+
		"\2\2\2\u0112\u0113\3\2\2\2\u0113\u0115\3\2\2\2\u0114\u0116\t\7\2\2\u0115"+
		"\u0114\3\2\2\2\u0115\u0116\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u011a\5\34"+
		"\17\2\u0118\u011a\5 \21\2\u0119\u0100\3\2\2\2\u0119\u0109\3\2\2\2\u0119"+
		"\u0112\3\2\2\2\u0119\u0118\3\2\2\2\u011a\r\3\2\2\2\u011b\u011e\7\u010e"+
		"\2\2\u011c\u011e\5\20\t\2\u011d\u011b\3\2\2\2\u011d\u011c\3\2\2\2\u011d"+
		"\u011e\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u0121\5\62\32\2\u0120\u0122\t"+
		"\3\2\2\u0121\u0120\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0124\3\2\2\2\u0123"+
		"\u0125\5\22\n\2\u0124\u0123\3\2\2\2\u0124\u0125\3\2\2\2\u0125\u0126\3"+
		"\2\2\2\u0126\u0127\t\5\2\2\u0127\u0128\5\62\32\2\u0128\u0129\7\u00ea\2"+
		"\2\u0129\u012a\5\62\32\2\u012a\u0231\3\2\2\2\u012b\u012e\7\u010e\2\2\u012c"+
		"\u012e\5\20\t\2\u012d\u012b\3\2\2\2\u012d\u012c\3\2\2\2\u012d\u012e\3"+
		"\2\2\2\u012e\u012f\3\2\2\2\u012f\u0131\5\62\32\2\u0130\u0132\t\3\2\2\u0131"+
		"\u0130\3\2\2\2\u0131\u0132\3\2\2\2\u0132\u0134\3\2\2\2\u0133\u0135\5\22"+
		"\n\2\u0134\u0133\3\2\2\2\u0134\u0135\3\2\2\2\u0135\u0136\3\2\2\2\u0136"+
		"\u0137\t\b\2\2\u0137\u0139\5\62\32\2\u0138\u013a\t\4\2\2\u0139\u0138\3"+
		"\2\2\2\u0139\u013a\3\2\2\2\u013a\u013b\3\2\2\2\u013b\u013c\t\t\2\2\u013c"+
		"\u013d\5\62\32\2\u013d\u0231\3\2\2\2\u013e\u0141\7\u010e\2\2\u013f\u0141"+
		"\5\20\t\2\u0140\u013e\3\2\2\2\u0140\u013f\3\2\2\2\u0140\u0141\3\2\2\2"+
		"\u0141\u0142\3\2\2\2\u0142\u0144\5\62\32\2\u0143\u0145\t\3\2\2\u0144\u0143"+
		"\3\2\2\2\u0144\u0145\3\2\2\2\u0145\u014d\3\2\2\2\u0146\u0147\5\20\t\2"+
		"\u0147\u0148\t\n\2\2\u0148\u014e\3\2\2\2\u0149\u014b\5\22\n\2\u014a\u0149"+
		"\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u014c\3\2\2\2\u014c\u014e\t\13\2\2"+
		"\u014d\u0146\3\2\2\2\u014d\u014a\3\2\2\2\u014e\u014f\3\2\2\2\u014f\u0157"+
		"\5\62\32\2\u0150\u0153\7\u011e\2\2\u0151\u0154\5\b\5\2\u0152\u0154\5\62"+
		"\32\2\u0153\u0151\3\2\2\2\u0153\u0152\3\2\2\2\u0154\u0156\3\2\2\2\u0155"+
		"\u0150\3\2\2\2\u0156\u0159\3\2\2\2\u0157\u0155\3\2\2\2\u0157\u0158\3\2"+
		"\2\2\u0158\u0231\3\2\2\2\u0159\u0157\3\2\2\2\u015a\u015d\7\u010e\2\2\u015b"+
		"\u015d\5\20\t\2\u015c\u015a\3\2\2\2\u015c\u015b\3\2\2\2\u015c\u015d\3"+
		"\2\2\2\u015d\u015e\3\2\2\2\u015e\u0160\5\62\32\2\u015f\u0161\t\3\2\2\u0160"+
		"\u015f\3\2\2\2\u0160\u0161\3\2\2\2\u0161\u0169\3\2\2\2\u0162\u0163\5\20"+
		"\t\2\u0163\u0164\t\f\2\2\u0164\u016a\3\2\2\2\u0165\u0167\5\22\n\2\u0166"+
		"\u0165\3\2\2\2\u0166\u0167\3\2\2\2\u0167\u0168\3\2\2\2\u0168\u016a\t\r"+
		"\2\2\u0169\u0162\3\2\2\2\u0169\u0166\3\2\2\2\u016a\u016b\3\2\2\2\u016b"+
		"\u0173\5\62\32\2\u016c\u016f\7\u011e\2\2\u016d\u0170\5\b\5\2\u016e\u0170"+
		"\5\62\32\2\u016f\u016d\3\2\2\2\u016f\u016e\3\2\2\2\u0170\u0172\3\2\2\2"+
		"\u0171\u016c\3\2\2\2\u0172\u0175\3\2\2\2\u0173\u0171\3\2\2\2\u0173\u0174"+
		"\3\2\2\2\u0174\u0231\3\2\2\2\u0175\u0173\3\2\2\2\u0176\u0179\7\u010e\2"+
		"\2\u0177\u0179\5\20\t\2\u0178\u0176\3\2\2\2\u0178\u0177\3\2\2\2\u0178"+
		"\u0179\3\2\2\2\u0179\u017a\3\2\2\2\u017a\u017c\5\62\32\2\u017b\u017d\t"+
		"\3\2\2\u017c\u017b\3\2\2\2\u017c\u017d\3\2\2\2\u017d\u0185\3\2\2\2\u017e"+
		"\u017f\5\20\t\2\u017f\u0180\t\r\2\2\u0180\u0186\3\2\2\2\u0181\u0183\5"+
		"\22\n\2\u0182\u0181\3\2\2\2\u0182\u0183\3\2\2\2\u0183\u0184\3\2\2\2\u0184"+
		"\u0186\t\f\2\2\u0185\u017e\3\2\2\2\u0185\u0182\3\2\2\2\u0186\u0187\3\2"+
		"\2\2\u0187\u018f\5\62\32\2\u0188\u018b\7\u011e\2\2\u0189\u018c\5\b\5\2"+
		"\u018a\u018c\5\62\32\2\u018b\u0189\3\2\2\2\u018b\u018a\3\2\2\2\u018c\u018e"+
		"\3\2\2\2\u018d\u0188\3\2\2\2\u018e\u0191\3\2\2\2\u018f\u018d\3\2\2\2\u018f"+
		"\u0190\3\2\2\2\u0190\u0231\3\2\2\2\u0191\u018f\3\2\2\2\u0192\u0195\7\u010e"+
		"\2\2\u0193\u0195\5\20\t\2\u0194\u0192\3\2\2\2\u0194\u0193\3\2\2\2\u0194"+
		"\u0195\3\2\2\2\u0195\u0196\3\2\2\2\u0196\u0198\5\62\32\2\u0197\u0199\t"+
		"\3\2\2\u0198\u0197\3\2\2\2\u0198\u0199\3\2\2\2\u0199\u01a1\3\2\2\2\u019a"+
		"\u019b\5\20\t\2\u019b\u019c\t\13\2\2\u019c\u01a2\3\2\2\2\u019d\u019f\5"+
		"\22\n\2\u019e\u019d\3\2\2\2\u019e\u019f\3\2\2\2\u019f\u01a0\3\2\2\2\u01a0"+
		"\u01a2\t\n\2\2\u01a1\u019a\3\2\2\2\u01a1\u019e\3\2\2\2\u01a2\u01a3\3\2"+
		"\2\2\u01a3\u01ab\5\62\32\2\u01a4\u01a7\7\u011e\2\2\u01a5\u01a8\5\b\5\2"+
		"\u01a6\u01a8\5\62\32\2\u01a7\u01a5\3\2\2\2\u01a7\u01a6\3\2\2\2\u01a8\u01aa"+
		"\3\2\2\2\u01a9\u01a4\3\2\2\2\u01aa\u01ad\3\2\2\2\u01ab\u01a9\3\2\2\2\u01ab"+
		"\u01ac\3\2\2\2\u01ac\u0231\3\2\2\2\u01ad\u01ab\3\2\2\2\u01ae\u01b1\7\u010e"+
		"\2\2\u01af\u01b1\5\20\t\2\u01b0\u01ae\3\2\2\2\u01b0\u01af\3\2\2\2\u01b0"+
		"\u01b1\3\2\2\2\u01b1\u01b2\3\2\2\2\u01b2\u01b4\5\62\32\2\u01b3\u01b5\t"+
		"\3\2\2\u01b4\u01b3\3\2\2\2\u01b4\u01b5\3\2\2\2\u01b5\u01b7\3\2\2\2\u01b6"+
		"\u01b8\5\22\n\2\u01b7\u01b6\3\2\2\2\u01b7\u01b8\3\2\2\2\u01b8\u01ba\3"+
		"\2\2\2\u01b9\u01bb\t\16\2\2\u01ba\u01b9\3\2\2\2\u01ba\u01bb\3\2\2\2\u01bb"+
		"\u01bc\3\2\2\2\u01bc\u01bd\5\32\16\2\u01bd\u0231\3\2\2\2\u01be\u01c1\7"+
		"\u010e\2\2\u01bf\u01c1\5\20\t\2\u01c0\u01be\3\2\2\2\u01c0\u01bf\3\2\2"+
		"\2\u01c0\u01c1\3\2\2\2\u01c1\u01c2\3\2\2\2\u01c2\u01c4\5\62\32\2\u01c3"+
		"\u01c5\t\3\2\2\u01c4\u01c3\3\2\2\2\u01c4\u01c5\3\2\2\2\u01c5\u01c7\3\2"+
		"\2\2\u01c6\u01c8\5\20\t\2\u01c7\u01c6\3\2\2\2\u01c7\u01c8\3\2\2\2\u01c8"+
		"\u01c9\3\2\2\2\u01c9\u01ca\t\17\2\2\u01ca\u01d2\5\f\7\2\u01cb\u01ce\7"+
		"\u011e\2\2\u01cc\u01cf\5\b\5\2\u01cd\u01cf\5\62\32\2\u01ce\u01cc\3\2\2"+
		"\2\u01ce\u01cd\3\2\2\2\u01cf\u01d1\3\2\2\2\u01d0\u01cb\3\2\2\2\u01d1\u01d4"+
		"\3\2\2\2\u01d2\u01d0\3\2\2\2\u01d2\u01d3\3\2\2\2\u01d3\u0231\3\2\2\2\u01d4"+
		"\u01d2\3\2\2\2\u01d5\u01d8\7\u010e\2\2\u01d6\u01d8\5\20\t\2\u01d7\u01d5"+
		"\3\2\2\2\u01d7\u01d6\3\2\2\2\u01d7\u01d8\3\2\2\2\u01d8\u01d9\3\2\2\2\u01d9"+
		"\u01db\5\62\32\2\u01da\u01dc\t\20\2\2\u01db\u01da\3\2\2\2\u01db\u01dc"+
		"\3\2\2\2\u01dc\u01e3\3\2\2\2\u01dd\u01df\5\22\n\2\u01de\u01dd\3\2\2\2"+
		"\u01de\u01df\3\2\2\2\u01df\u01e4\3\2\2\2\u01e0\u01e2\5\20\t\2\u01e1\u01e0"+
		"\3\2\2\2\u01e1\u01e2\3\2\2\2\u01e2\u01e4\3\2\2\2\u01e3\u01de\3\2\2\2\u01e3"+
		"\u01e1\3\2\2\2\u01e4\u01e5\3\2\2\2\u01e5\u01e6\t\21\2\2\u01e6\u01ee\5"+
		"\62\32\2\u01e7\u01ea\t\22\2\2\u01e8\u01eb\5\b\5\2\u01e9\u01eb\5\62\32"+
		"\2\u01ea\u01e8\3\2\2\2\u01ea\u01e9\3\2\2\2\u01eb\u01ed\3\2\2\2\u01ec\u01e7"+
		"\3\2\2\2\u01ed\u01f0\3\2\2\2\u01ee\u01ec\3\2\2\2\u01ee\u01ef\3\2\2\2\u01ef"+
		"\u0231\3\2\2\2\u01f0\u01ee\3\2\2\2\u01f1\u01f3\7\u010d\2\2\u01f2\u01f1"+
		"\3\2\2\2\u01f2\u01f3\3\2\2\2\u01f3\u01f4\3\2\2\2\u01f4\u01fc\5\64\33\2"+
		"\u01f5\u01f7\5\22\n\2\u01f6\u01f5\3\2\2\2\u01f6\u01f7\3\2\2\2\u01f7\u01f8"+
		"\3\2\2\2\u01f8\u01fd\7\u00bd\2\2\u01f9\u01fa\5\20\t\2\u01fa\u01fb\5\26"+
		"\f\2\u01fb\u01fd\3\2\2\2\u01fc\u01f6\3\2\2\2\u01fc\u01f9\3\2\2\2\u01fd"+
		"\u0231\3\2\2\2\u01fe\u01ff\7\u010d\2\2\u01ff\u0205\5\20\t\2\u0200\u0202"+
		"\7\u010e\2\2\u0201\u0203\7\u0111\2\2\u0202\u0201\3\2\2\2\u0202\u0203\3"+
		"\2\2\2\u0203\u0205\3\2\2\2\u0204\u01fe\3\2\2\2\u0204\u0200\3\2\2\2\u0205"+
		"\u0206\3\2\2\2\u0206\u0211\5\64\33\2\u0207\u020e\5\64\33\2\u0208\u0209"+
		"\5\22\n\2\u0209\u020a\5\26\f\2\u020a\u020f\3\2\2\2\u020b\u020c\5\20\t"+
		"\2\u020c\u020d\7\u00bd\2\2\u020d\u020f\3\2\2\2\u020e\u0208\3\2\2\2\u020e"+
		"\u020b\3\2\2\2\u020f\u0211\3\2\2\2\u0210\u0204\3\2\2\2\u0210\u0207\3\2"+
		"\2\2\u0211\u0231\3\2\2\2\u0212\u0215\7\u010e\2\2\u0213\u0215\5\20\t\2"+
		"\u0214\u0212\3\2\2\2\u0214\u0213\3\2\2\2\u0214\u0215\3\2\2\2\u0215\u0216"+
		"\3\2\2\2\u0216\u0218\5\62\32\2\u0217\u0219\t\3\2\2\u0218\u0217\3\2\2\2"+
		"\u0218\u0219\3\2\2\2\u0219\u0223\3\2\2\2\u021a\u021c\5\20\t\2\u021b\u021d"+
		"\t\23\2\2\u021c\u021b\3\2\2\2\u021c\u021d\3\2\2\2\u021d\u0224\3\2\2\2"+
		"\u021e\u021f\5\22\n\2\u021f\u0220\t\23\2\2\u0220\u0224\3\2\2\2\u0221\u0224"+
		"\5\22\n\2\u0222\u0224\t\23\2\2\u0223\u021a\3\2\2\2\u0223\u021e\3\2\2\2"+
		"\u0223\u0221\3\2\2\2\u0223\u0222\3\2\2\2\u0224\u0225\3\2\2\2\u0225\u022d"+
		"\5\62\32\2\u0226\u0229\t\22\2\2\u0227\u022a\5\b\5\2\u0228\u022a\5\62\32"+
		"\2\u0229\u0227\3\2\2\2\u0229\u0228\3\2\2\2\u022a\u022c\3\2\2\2\u022b\u0226"+
		"\3\2\2\2\u022c\u022f\3\2\2\2\u022d\u022b\3\2\2\2\u022d\u022e\3\2\2\2\u022e"+
		"\u0231\3\2\2\2\u022f\u022d\3\2\2\2\u0230\u011d\3\2\2\2\u0230\u012d\3\2"+
		"\2\2\u0230\u0140\3\2\2\2\u0230\u015c\3\2\2\2\u0230\u0178\3\2\2\2\u0230"+
		"\u0194\3\2\2\2\u0230\u01b0\3\2\2\2\u0230\u01c0\3\2\2\2\u0230\u01d7\3\2"+
		"\2\2\u0230\u01f2\3\2\2\2\u0230\u0210\3\2\2\2\u0230\u0214\3\2\2\2\u0231"+
		"\17\3\2\2\2\u0232\u023d\7\u0120\2\2\u0233\u0234\7+\2\2\u0234\u023d\7\u0120"+
		"\2\2\u0235\u0237\t\24\2\2\u0236\u0238\7,\2\2\u0237\u0236\3\2\2\2\u0237"+
		"\u0238\3\2\2\2\u0238\u023a\3\2\2\2\u0239\u023b\7+\2\2\u023a\u0239\3\2"+
		"\2\2\u023a\u023b\3\2\2\2\u023b\u023d\3\2\2\2\u023c\u0232\3\2\2\2\u023c"+
		"\u0233\3\2\2\2\u023c\u0235\3\2\2\2\u023d\21\3\2\2\2\u023e\u0240\t\25\2"+
		"\2\u023f\u0241\7,\2\2\u0240\u023f\3\2\2\2\u0240\u0241\3\2\2\2\u0241\u0243"+
		"\3\2\2\2\u0242\u0244\7+\2\2\u0243\u0242\3\2\2\2\u0243\u0244\3\2\2\2\u0244"+
		"\23\3\2\2\2\u0245\u024b\7\u00c1\2\2\u0246\u0248\t\26\2\2\u0247\u0249\t"+
		"\27\2\2\u0248\u0247\3\2\2\2\u0248\u0249\3\2\2\2\u0249\u024b\3\2\2\2\u024a"+
		"\u0245\3\2\2\2\u024a\u0246\3\2\2\2\u024b\25\3\2\2\2\u024c\u024d\t\30\2"+
		"\2\u024d\27\3\2\2\2\u024e\u0250\b\r\1\2\u024f\u0251\7\u0157\2\2\u0250"+
		"\u024f\3\2\2\2\u0250\u0251\3\2\2\2\u0251\u0252\3\2\2\2\u0252\u026c\7\u012f"+
		"\2\2\u0253\u0255\7\u0157\2\2\u0254\u0253\3\2\2\2\u0254\u0255\3\2\2\2\u0255"+
		"\u0256\3\2\2\2\u0256\u026c\7\u0130\2\2\u0257\u0259\7\u0157\2\2\u0258\u0257"+
		"\3\2\2\2\u0258\u0259\3\2\2\2\u0259\u025a\3\2\2\2\u025a\u026c\7\u0131\2"+
		"\2\u025b\u025d\7\u0157\2\2\u025c\u025b\3\2\2\2\u025c\u025d\3\2\2\2\u025d"+
		"\u025e\3\2\2\2\u025e\u026c\7\u0135\2\2\u025f\u0261\7\u0157\2\2\u0260\u025f"+
		"\3\2\2\2\u0260\u0261\3\2\2\2\u0261\u0262\3\2\2\2\u0262\u026c\7\u0136\2"+
		"\2\u0263\u0265\7\u0157\2\2\u0264\u0263\3\2\2\2\u0264\u0265\3\2\2\2\u0265"+
		"\u0266\3\2\2\2\u0266\u026c\7\u0137\2\2\u0267\u0269\7\u0157\2\2\u0268\u0267"+
		"\3\2\2\2\u0268\u0269\3\2\2\2\u0269\u026a\3\2\2\2\u026a\u026c\7\u0138\2"+
		"\2\u026b\u024e\3\2\2\2\u026b\u0254\3\2\2\2\u026b\u0258\3\2\2\2\u026b\u025c"+
		"\3\2\2\2\u026b\u0260\3\2\2\2\u026b\u0264\3\2\2\2\u026b\u0268\3\2\2\2\u026c"+
		"\u0278\3\2\2\2\u026d\u0272\f\n\2\2\u026e\u0270\7\u00ea\2\2\u026f\u026e"+
		"\3\2\2\2\u026f\u0270\3\2\2\2\u0270\u0271\3\2\2\2\u0271\u0273\5\30\r\2"+
		"\u0272\u026f\3\2\2\2\u0273\u0274\3\2\2\2\u0274\u0272\3\2\2\2\u0274\u0275"+
		"\3\2\2\2\u0275\u0277\3\2\2\2\u0276\u026d\3\2\2\2\u0277\u027a\3\2\2\2\u0278"+
		"\u0276\3\2\2\2\u0278\u0279\3\2\2\2\u0279\31\3\2\2\2\u027a\u0278\3\2\2"+
		"\2\u027b\u027c\t\31\2\2\u027c\u028a\5\30\r\2\u027d\u027e\5\30\r\2\u027e"+
		"\u027f\7\u0126\2\2\u027f\u028a\3\2\2\2\u0280\u0281\7\u012d\2\2\u0281\u028a"+
		"\5\30\r\2\u0282\u0283\5\30\r\2\u0283\u0285\7\u0122\2\2\u0284\u0286\7\u015c"+
		"\2\2\u0285\u0284\3\2\2\2\u0285\u0286\3\2\2\2\u0286\u0287\3\2\2\2\u0287"+
		"\u0288\7\u012e\2\2\u0288\u028a\3\2\2\2\u0289\u027b\3\2\2\2\u0289\u027d"+
		"\3\2\2\2\u0289\u0280\3\2\2\2\u0289\u0282\3\2\2\2\u028a\33\3\2\2\2\u028b"+
		"\u02c7\7\u0132\2\2\u028c\u02c7\7\u0133\2\2\u028d\u02c7\7\u0134\2\2\u028e"+
		"\u028f\7\u012f\2\2\u028f\u02c7\7\u0157\2\2\u0290\u0292\7J\2\2\u0291\u0293"+
		"\5\36\20\2\u0292\u0291\3\2\2\2\u0292\u0293\3\2\2\2\u0293\u0298\3\2\2\2"+
		"\u0294\u0296\7S\2\2\u0295\u0294\3\2\2\2\u0295\u0296\3\2\2\2\u0296\u0297"+
		"\3\2\2\2\u0297\u0299\7\u0157\2\2\u0298\u0295\3\2\2\2\u0298\u0299\3\2\2"+
		"\2\u0299\u02c7\3\2\2\2\u029a\u029c\5\36\20\2\u029b\u029a\3\2\2\2\u029b"+
		"\u029c\3\2\2\2\u029c\u029e\3\2\2\2\u029d\u029f\7\u0112\2\2\u029e\u029d"+
		"\3\2\2\2\u029e\u029f\3\2\2\2\u029f\u02a0\3\2\2\2\u02a0\u02c7\7J\2\2\u02a1"+
		"\u02a3\7\u0157\2\2\u02a2\u02a4\7S\2\2\u02a3\u02a2\3\2\2\2\u02a3\u02a4"+
		"\3\2\2\2\u02a4\u02a6\3\2\2\2\u02a5\u02a1\3\2\2\2\u02a5\u02a6\3\2\2\2\u02a6"+
		"\u02a8\3\2\2\2\u02a7\u02a9\5\36\20\2\u02a8\u02a7\3\2\2\2\u02a8\u02a9\3"+
		"\2\2\2\u02a9\u02ab\3\2\2\2\u02aa\u02ac\7\u0112\2\2\u02ab\u02aa\3\2\2\2"+
		"\u02ab\u02ac\3\2\2\2\u02ac\u02ad\3\2\2\2\u02ad\u02c7\7J\2\2\u02ae\u02b0"+
		"\5\36\20\2\u02af\u02b1\7\u0112\2\2\u02b0\u02af\3\2\2\2\u02b0\u02b1\3\2"+
		"\2\2\u02b1\u02b2\3\2\2\2\u02b2\u02b4\7J\2\2\u02b3\u02b5\7S\2\2\u02b4\u02b3"+
		"\3\2\2\2\u02b4\u02b5\3\2\2\2\u02b5\u02b6\3\2\2\2\u02b6\u02b7\7\u0157\2"+
		"\2\u02b7\u02c7\3\2\2\2\u02b8\u02ba\7\u0157\2\2\u02b9\u02bb\7S\2\2\u02ba"+
		"\u02b9\3\2\2\2\u02ba\u02bb\3\2\2\2\u02bb\u02bc\3\2\2\2\u02bc\u02bd\7J"+
		"\2\2\u02bd\u02c7\5\36\20\2\u02be\u02bf\7\u0157\2\2\u02bf\u02c2\7P\2\2"+
		"\u02c0\u02c3\7\u0157\2\2\u02c1\u02c3\7J\2\2\u02c2\u02c0\3\2\2\2\u02c2"+
		"\u02c1\3\2\2\2\u02c3\u02c4\3\2\2\2\u02c4\u02c5\7P\2\2\u02c5\u02c7\5\36"+
		"\20\2\u02c6\u028b\3\2\2\2\u02c6\u028c\3\2\2\2\u02c6\u028d\3\2\2\2\u02c6"+
		"\u028e\3\2\2\2\u02c6\u0290\3\2\2\2\u02c6\u029b\3\2\2\2\u02c6\u02a5\3\2"+
		"\2\2\u02c6\u02ae\3\2\2\2\u02c6\u02b8\3\2\2\2\u02c6\u02be\3\2\2\2\u02c7"+
		"\35\3\2\2\2\u02c8\u02ca\7\u0157\2\2\u02c9\u02cb\7\u015c\2\2\u02ca\u02c9"+
		"\3\2\2\2\u02ca\u02cb\3\2\2\2\u02cb\u02cd\3\2\2\2\u02cc\u02ce\t\32\2\2"+
		"\u02cd\u02cc\3\2\2\2\u02cd\u02ce\3\2\2\2\u02ce\u02d0\3\2\2\2\u02cf\u02d1"+
		"\7\u015c\2\2\u02d0\u02cf\3\2\2\2\u02d0\u02d1\3\2\2\2\u02d1\37\3\2\2\2"+
		"\u02d2\u02d3\b\21\1\2\u02d3\u02d5\7\u0157\2\2\u02d4\u02d6\7\u015c\2\2"+
		"\u02d5\u02d4\3\2\2\2\u02d5\u02d6\3\2\2\2\u02d6\u02d7\3\2\2\2\u02d7\u02d9"+
		"\t\33\2\2\u02d8\u02da\7\u015c\2\2\u02d9\u02d8\3\2\2\2\u02d9\u02da\3\2"+
		"\2\2\u02da\u02f0\3\2\2\2\u02db\u02dd\7\u015c\2\2\u02dc\u02db\3\2\2\2\u02dc"+
		"\u02dd\3\2\2\2\u02dd\u02de\3\2\2\2\u02de\u02df\7\u0157\2\2\u02df\u02e0"+
		"\7\\\2\2\u02e0\u02e1\7\u0157\2\2\u02e1\u02e2\7\\\2\2\u02e2\u02e4\7\u0157"+
		"\2\2\u02e3\u02e5\7\u015c\2\2\u02e4\u02e3\3\2\2\2\u02e4\u02e5\3\2\2\2\u02e5"+
		"\u02f0\3\2\2\2\u02e6\u02e8\7\u015c\2\2\u02e7\u02e6\3\2\2\2\u02e7\u02e8"+
		"\3\2\2\2\u02e8\u02e9\3\2\2\2\u02e9\u02ea\7\u0157\2\2\u02ea\u02eb\7\\\2"+
		"\2\u02eb\u02ed\7\u0157\2\2\u02ec\u02ee\7\u015c\2\2\u02ed\u02ec\3\2\2\2"+
		"\u02ed\u02ee\3\2\2\2\u02ee\u02f0\3\2\2\2\u02ef\u02d2\3\2\2\2\u02ef\u02dc"+
		"\3\2\2\2\u02ef\u02e7\3\2\2\2\u02f0\u0306\3\2\2\2\u02f1\u02f3\f\6\2\2\u02f2"+
		"\u02f4\7\u015c\2\2\u02f3\u02f2\3\2\2\2\u02f3\u02f4\3\2\2\2\u02f4\u02f5"+
		"\3\2\2\2\u02f5\u02f7\t\33\2\2\u02f6\u02f8\7\u015c\2\2\u02f7\u02f6\3\2"+
		"\2\2\u02f7\u02f8\3\2\2\2\u02f8\u0305\3\2\2\2\u02f9\u02fb\f\5\2\2\u02fa"+
		"\u02fc\7\u015c\2\2\u02fb\u02fa\3\2\2\2\u02fb\u02fc\3\2\2\2\u02fc\u02ff"+
		"\3\2\2\2\u02fd\u0300\5\"\22\2\u02fe\u0300\7\u0156\2\2\u02ff\u02fd\3\2"+
		"\2\2\u02ff\u02fe\3\2\2\2\u0300\u0302\3\2\2\2\u0301\u0303\7\u015c\2\2\u0302"+
		"\u0301\3\2\2\2\u0302\u0303\3\2\2\2\u0303\u0305\3\2\2\2\u0304\u02f1\3\2"+
		"\2\2\u0304\u02f9\3\2\2\2\u0305\u0308\3\2\2\2\u0306\u0304\3\2\2\2\u0306"+
		"\u0307\3\2\2\2\u0307!\3\2\2\2\u0308\u0306\3\2\2\2\u0309\u030b\t\34\2\2"+
		"\u030a\u0309\3\2\2\2\u030a\u030b\3\2\2\2\u030b\u030c\3\2\2\2\u030c\u030d"+
		"\t\35\2\2\u030d\u0310\7\u0157\2\2\u030e\u030f\t\36\2\2\u030f\u0311\7\u0157"+
		"\2\2\u0310\u030e\3\2\2\2\u0310\u0311\3\2\2\2\u0311#\3\2\2\2\u0312\u0314"+
		"\7<\2\2\u0313\u0315\7\u015c\2\2\u0314\u0313\3\2\2\2\u0314\u0315\3\2\2"+
		"\2\u0315\u0317\3\2\2\2\u0316\u0318\7\u011b\2\2\u0317\u0316\3\2\2\2\u0317"+
		"\u0318\3\2\2\2\u0318\u031a\3\2\2\2\u0319\u031b\7\u015c\2\2\u031a\u0319"+
		"\3\2\2\2\u031a\u031b\3\2\2\2\u031b\u031c\3\2\2\2\u031c\u0374\5\64\33\2"+
		"\u031d\u031f\t\37\2\2\u031e\u0320\7\u015c\2\2\u031f\u031e\3\2\2\2\u031f"+
		"\u0320\3\2\2\2\u0320\u0322\3\2\2\2\u0321\u0323\7\u00a8\2\2\u0322\u0321"+
		"\3\2\2\2\u0322\u0323\3\2\2\2\u0323\u0325\3\2\2\2\u0324\u0326\7\u015c\2"+
		"\2\u0325\u0324\3\2\2\2\u0325\u0326\3\2\2\2\u0326\u0328\3\2\2\2\u0327\u0329"+
		"\t \2\2\u0328\u0327\3\2\2\2\u0328\u0329\3\2\2\2\u0329\u032b\3\2\2\2\u032a"+
		"\u032c\7\u015c\2\2\u032b\u032a\3\2\2\2\u032b\u032c\3\2\2\2\u032c\u032e"+
		"\3\2\2\2\u032d\u032f\7\u011b\2\2\u032e\u032d\3\2\2\2\u032e\u032f\3\2\2"+
		"\2\u032f\u0331\3\2\2\2\u0330\u0332\7\u015c\2\2\u0331\u0330\3\2\2\2\u0331"+
		"\u0332\3\2\2\2\u0332\u0333\3\2\2\2\u0333\u0374\5\64\33\2\u0334\u0336\t"+
		"!\2\2\u0335\u0337\7\u015c\2\2\u0336\u0335\3\2\2\2\u0336\u0337\3\2\2\2"+
		"\u0337\u0339\3\2\2\2\u0338\u033a\7\u00a8\2\2\u0339\u0338\3\2\2\2\u0339"+
		"\u033a\3\2\2\2\u033a\u033c\3\2\2\2\u033b\u033d\7\u015c\2\2\u033c\u033b"+
		"\3\2\2\2\u033c\u033d\3\2\2\2\u033d\u033f\3\2\2\2\u033e\u0340\t \2\2\u033f"+
		"\u033e\3\2\2\2\u033f\u0340\3\2\2\2\u0340\u0342\3\2\2\2\u0341\u0343\7\u015c"+
		"\2\2\u0342\u0341\3\2\2\2\u0342\u0343\3\2\2\2\u0343\u0345\3\2\2\2\u0344"+
		"\u0346\7\u011b\2\2\u0345\u0344\3\2\2\2\u0345\u0346\3\2\2\2\u0346\u0348"+
		"\3\2\2\2\u0347\u0349\7\u015c\2\2\u0348\u0347\3\2\2\2\u0348\u0349\3\2\2"+
		"\2\u0349\u034a\3\2\2\2\u034a\u0374\5\64\33\2\u034b\u034d\7?\2\2\u034c"+
		"\u034e\7\u015c\2\2\u034d\u034c\3\2\2\2\u034d\u034e\3\2\2\2\u034e\u0350"+
		"\3\2\2\2\u034f\u0351\t \2\2\u0350\u034f\3\2\2\2\u0350\u0351\3\2\2\2\u0351"+
		"\u0353\3\2\2\2\u0352\u0354\7\u015c\2\2\u0353\u0352\3\2\2\2\u0353\u0354"+
		"\3\2\2\2\u0354\u0356\3\2\2\2\u0355\u0357\7\u011b\2\2\u0356\u0355\3\2\2"+
		"\2\u0356\u0357\3\2\2\2\u0357\u0359\3\2\2\2\u0358\u035a\7\u015c\2\2\u0359"+
		"\u0358\3\2\2\2\u0359\u035a\3\2\2\2\u035a\u035b\3\2\2\2\u035b\u0374\5\64"+
		"\33\2\u035c\u035e\7@\2\2\u035d\u035f\7\u011b\2\2\u035e\u035d\3\2\2\2\u035e"+
		"\u035f\3\2\2\2\u035f\u0361\3\2\2\2\u0360\u0362\7\u015c\2\2\u0361\u0360"+
		"\3\2\2\2\u0361\u0362\3\2\2\2\u0362\u0363\3\2\2\2\u0363\u0374\5\64\33\2"+
		"\u0364\u0365\5\64\33\2\u0365\u0366\t\"\2\2\u0366\u0374\3\2\2\2\u0367\u0368"+
		"\5\64\33\2\u0368\u0369\7=\2\2\u0369\u0374\3\2\2\2\u036a\u036b\5\64\33"+
		"\2\u036b\u036c\7>\2\2\u036c\u0374\3\2\2\2\u036d\u036e\5\64\33\2\u036e"+
		"\u036f\7?\2\2\u036f\u0374\3\2\2\2\u0370\u0371\5\64\33\2\u0371\u0372\7"+
		"@\2\2\u0372\u0374\3\2\2\2\u0373\u0312\3\2\2\2\u0373\u031d\3\2\2\2\u0373"+
		"\u0334\3\2\2\2\u0373\u034b\3\2\2\2\u0373\u035c\3\2\2\2\u0373\u0364\3\2"+
		"\2\2\u0373\u0367\3\2\2\2\u0373\u036a\3\2\2\2\u0373\u036d\3\2\2\2\u0373"+
		"\u0370\3\2\2\2\u0374%\3\2\2\2\u0375\u0377\7<\2\2\u0376\u0378\7\u015c\2"+
		"\2\u0377\u0376\3\2\2\2\u0377\u0378\3\2\2\2\u0378\u037a\3\2\2\2\u0379\u037b"+
		"\7\u011b\2\2\u037a\u0379\3\2\2\2\u037a\u037b\3\2\2\2\u037b\u037d\3\2\2"+
		"\2\u037c\u037e\7\u015c\2\2\u037d\u037c\3\2\2\2\u037d\u037e\3\2\2\2\u037e"+
		"\u037f\3\2\2\2\u037f\u03c8\5(\25\2\u0380\u0382\t\37\2\2\u0381\u0383\7"+
		"\u015c\2\2\u0382\u0381\3\2\2\2\u0382\u0383\3\2\2\2\u0383\u0385\3\2\2\2"+
		"\u0384\u0386\7\u00a8\2\2\u0385\u0384\3\2\2\2\u0385\u0386\3\2\2\2\u0386"+
		"\u0388\3\2\2\2\u0387\u0389\7\u015c\2\2\u0388\u0387\3\2\2\2\u0388\u0389"+
		"\3\2\2\2\u0389\u038b\3\2\2\2\u038a\u038c\t \2\2\u038b\u038a\3\2\2\2\u038b"+
		"\u038c\3\2\2\2\u038c\u038e\3\2\2\2\u038d\u038f\7\u015c\2\2\u038e\u038d"+
		"\3\2\2\2\u038e\u038f\3\2\2\2\u038f\u0391\3\2\2\2\u0390\u0392\7\u011b\2"+
		"\2\u0391\u0390\3\2\2\2\u0391\u0392\3\2\2\2\u0392\u0394\3\2\2\2\u0393\u0395"+
		"\7\u015c\2\2\u0394\u0393\3\2\2\2\u0394\u0395\3\2\2\2\u0395\u0396\3\2\2"+
		"\2\u0396\u03c8\5(\25\2\u0397\u0399\t!\2\2\u0398\u039a\7\u015c\2\2\u0399"+
		"\u0398\3\2\2\2\u0399\u039a\3\2\2\2\u039a\u039c\3\2\2\2\u039b\u039d\7\u00a8"+
		"\2\2\u039c\u039b\3\2\2\2\u039c\u039d\3\2\2\2\u039d\u039f\3\2\2\2\u039e"+
		"\u03a0\7\u015c\2\2\u039f\u039e\3\2\2\2\u039f\u03a0\3\2\2\2\u03a0\u03a2"+
		"\3\2\2\2\u03a1\u03a3\t \2\2\u03a2\u03a1\3\2\2\2\u03a2\u03a3\3\2\2\2\u03a3"+
		"\u03a5\3\2\2\2\u03a4\u03a6\7\u015c\2\2\u03a5\u03a4\3\2\2\2\u03a5\u03a6"+
		"\3\2\2\2\u03a6\u03a8\3\2\2\2\u03a7\u03a9\7\u011b\2\2\u03a8\u03a7\3\2\2"+
		"\2\u03a8\u03a9\3\2\2\2\u03a9\u03ab\3\2\2\2\u03aa\u03ac\7\u015c\2\2\u03ab"+
		"\u03aa\3\2\2\2\u03ab\u03ac\3\2\2\2\u03ac\u03ad\3\2\2\2\u03ad\u03c8\5("+
		"\25\2\u03ae\u03b0\7?\2\2\u03af\u03b1\7\u015c\2\2\u03b0\u03af\3\2\2\2\u03b0"+
		"\u03b1\3\2\2\2\u03b1\u03b3\3\2\2\2\u03b2\u03b4\t \2\2\u03b3\u03b2\3\2"+
		"\2\2\u03b3\u03b4\3\2\2\2\u03b4\u03b6\3\2\2\2\u03b5\u03b7\7\u015c\2\2\u03b6"+
		"\u03b5\3\2\2\2\u03b6\u03b7\3\2\2\2\u03b7\u03b9\3\2\2\2\u03b8\u03ba\7\u011b"+
		"\2\2\u03b9\u03b8\3\2\2\2\u03b9\u03ba\3\2\2\2\u03ba\u03bc\3\2\2\2\u03bb"+
		"\u03bd\7\u015c\2\2\u03bc\u03bb\3\2\2\2\u03bc\u03bd\3\2\2\2\u03bd\u03be"+
		"\3\2\2\2\u03be\u03c8\5(\25\2\u03bf\u03c1\7@\2\2\u03c0\u03c2\7\u011b\2"+
		"\2\u03c1\u03c0\3\2\2\2\u03c1\u03c2\3\2\2\2\u03c2\u03c4\3\2\2\2\u03c3\u03c5"+
		"\7\u015c\2\2\u03c4\u03c3\3\2\2\2\u03c4\u03c5\3\2\2\2\u03c5\u03c6\3\2\2"+
		"\2\u03c6\u03c8\5(\25\2\u03c7\u0375\3\2\2\2\u03c7\u0380\3\2\2\2\u03c7\u0397"+
		"\3\2\2\2\u03c7\u03ae\3\2\2\2\u03c7\u03bf\3\2\2\2\u03c8\'\3\2\2\2\u03c9"+
		"\u03d0\5&\24\2\u03ca\u03d0\78\2\2\u03cb\u03d0\79\2\2\u03cc\u03d0\7:\2"+
		"\2\u03cd\u03d0\7;\2\2\u03ce\u03d0\7\u0157\2\2\u03cf\u03c9\3\2\2\2\u03cf"+
		"\u03ca\3\2\2\2\u03cf\u03cb\3\2\2\2\u03cf\u03cc\3\2\2\2\u03cf\u03cd\3\2"+
		"\2\2\u03cf\u03ce\3\2\2\2\u03d0)\3\2\2\2\u03d1\u03d5\t#\2\2\u03d2\u03d3"+
		"\5(\25\2\u03d3\u03d4\5\64\33\2\u03d4\u03d6\3\2\2\2\u03d5\u03d2\3\2\2\2"+
		"\u03d5\u03d6\3\2\2\2\u03d6\u03d7\3\2\2\2\u03d7\u0409\5,\27\2\u03d8\u03dc"+
		"\t$\2\2\u03d9\u03da\5(\25\2\u03da\u03db\5\64\33\2\u03db\u03dd\3\2\2\2"+
		"\u03dc\u03d9\3\2\2\2\u03dc\u03dd\3\2\2\2\u03dd\u03de\3\2\2\2\u03de\u0409"+
		"\5,\27\2\u03df\u03e0\5(\25\2\u03e0\u03e1\t\32\2\2\u03e1\u03e2\7\u012c"+
		"\2\2\u03e2\u03e3\5\64\33\2\u03e3\u03e4\5,\27\2\u03e4\u0409\3\2\2\2\u03e5"+
		"\u03e6\5(\25\2\u03e6\u03e8\t\32\2\2\u03e7\u03e9\7\u00a3\2\2\u03e8\u03e7"+
		"\3\2\2\2\u03e8\u03e9\3\2\2\2\u03e9\u03ea\3\2\2\2\u03ea\u03eb\5\64\33\2"+
		"\u03eb\u03ec\7\u0122\2\2\u03ec\u03ed\7\u00a7\2\2\u03ed\u03ee\5,\27\2\u03ee"+
		"\u0409\3\2\2\2\u03ef\u03f0\5(\25\2\u03f0\u03f2\t\32\2\2\u03f1\u03f3\7"+
		"\u00ad\2\2\u03f2\u03f1\3\2\2\2\u03f2\u03f3\3\2\2\2\u03f3\u03f4\3\2\2\2"+
		"\u03f4\u03f5\5\64\33\2\u03f5\u03f6\5,\27\2\u03f6\u0409\3\2\2\2\u03f7\u03f8"+
		"\t%\2\2\u03f8\u03f9\5\64\33\2\u03f9\u03fa\5,\27\2\u03fa\u0409\3\2\2\2"+
		"\u03fb\u03fc\t&\2\2\u03fc\u03fd\5\64\33\2\u03fd\u03fe\5,\27\2\u03fe\u0409"+
		"\3\2\2\2\u03ff\u0400\7\u008d\2\2\u0400\u0401\7\u0112\2\2\u0401\u0402\5"+
		"\64\33\2\u0402\u0403\5,\27\2\u0403\u0409\3\2\2\2\u0404\u0405\5\64\33\2"+
		"\u0405\u0406\7\u008d\2\2\u0406\u0407\5,\27\2\u0407\u0409\3\2\2\2\u0408"+
		"\u03d1\3\2\2\2\u0408\u03d8\3\2\2\2\u0408\u03df\3\2\2\2\u0408\u03e5\3\2"+
		"\2\2\u0408\u03ef\3\2\2\2\u0408\u03f7\3\2\2\2\u0408\u03fb\3\2\2\2\u0408"+
		"\u03ff\3\2\2\2\u0408\u0404\3\2\2\2\u0409+\3\2\2\2\u040a\u040c\7/\2\2\u040b"+
		"\u040d\7.\2\2\u040c\u040b\3\2\2\2\u040c\u040d\3\2\2\2\u040d\u040e\3\2"+
		"\2\2\u040e\u0414\5\62\32\2\u040f\u0411\t\'\2\2\u0410\u0412\7.\2\2\u0411"+
		"\u0410\3\2\2\2\u0411\u0412\3\2\2\2\u0412\u0413\3\2\2\2\u0413\u0415\5\64"+
		"\33\2\u0414\u040f\3\2\2\2\u0414\u0415\3\2\2\2\u0415\u0422\3\2\2\2\u0416"+
		"\u0418\t\'\2\2\u0417\u0419\7.\2\2\u0418\u0417\3\2\2\2\u0418\u0419\3\2"+
		"\2\2\u0419\u041a\3\2\2\2\u041a\u041b\5\64\33\2\u041b\u041d\7/\2\2\u041c"+
		"\u041e\7.\2\2\u041d\u041c\3\2\2\2\u041d\u041e\3\2\2\2\u041e\u041f\3\2"+
		"\2\2\u041f\u0420\5\62\32\2\u0420\u0422\3\2\2\2\u0421\u040a\3\2\2\2\u0421"+
		"\u0416\3\2\2\2\u0422-\3\2\2\2\u0423\u0424\7\33\2\2\u0424/\3\2\2\2\u0425"+
		"\u0426\t(\2\2\u0426\61\3\2\2\2\u0427\u0428\b\32\1\2\u0428\u0429\7T\2\2"+
		"\u0429\u042a\5\62\32\2\u042a\u042b\7U\2\2\u042b\u0482\3\2\2\2\u042c\u0482"+
		"\5\f\7\2\u042d\u042e\5\30\r\2\u042e\u042f\t\35\2\2\u042f\u0430\5\62\32"+
		"\21\u0430\u0482\3\2\2\2\u0431\u0433\5\66\34\2\u0432\u0431\3\2\2\2\u0432"+
		"\u0433\3\2\2\2\u0433\u0434\3\2\2\2\u0434\u0436\5\60\31\2\u0435\u0437\7"+
		"\u0112\2\2\u0436\u0435\3\2\2\2\u0436\u0437\3\2\2\2\u0437\u0438\3\2\2\2"+
		"\u0438\u0439\5\62\32\r\u0439\u0482\3\2\2\2\u043a\u043c\5\66\34\2\u043b"+
		"\u043a\3\2\2\2\u043b\u043c\3\2\2\2\u043c\u0443\3\2\2\2\u043d\u043f\7\25"+
		"\2\2\u043e\u0440\7\u0112\2\2\u043f\u043e\3\2\2\2\u043f\u0440\3\2\2\2\u0440"+
		"\u0444\3\2\2\2\u0441\u0442\7\u008c\2\2\u0442\u0444\7\u0112\2\2\u0443\u043d"+
		"\3\2\2\2\u0443\u0441\3\2\2\2\u0444\u0445\3\2\2\2\u0445\u0446\5\62\32\2"+
		"\u0446\u0447\7\u0122\2\2\u0447\u044a\5\62\32\2\u0448\u0449\t)\2\2\u0449"+
		"\u044b\5\62\32\2\u044a\u0448\3\2\2\2\u044a\u044b\3\2\2\2\u044b\u0482\3"+
		"\2\2\2\u044c\u044e\5\66\34\2\u044d\u044c\3\2\2\2\u044d\u044e\3\2\2\2\u044e"+
		"\u0455\3\2\2\2\u044f\u0451\7\25\2\2\u0450\u0452\7\u0112\2\2\u0451\u0450"+
		"\3\2\2\2\u0451\u0452\3\2\2\2\u0452\u0456\3\2\2\2\u0453\u0454\7\u008c\2"+
		"\2\u0454\u0456\7\u0112\2\2\u0455\u044f\3\2\2\2\u0455\u0453\3\2\2\2\u0456"+
		"\u0457\3\2\2\2\u0457\u0458\5\62\32\2\u0458\u0459\7\u00c5\2\2\u0459\u045c"+
		"\5\62\32\2\u045a\u045b\7\u00ea\2\2\u045b\u045d\5\62\32\2\u045c\u045a\3"+
		"\2\2\2\u045c\u045d\3\2\2\2\u045d\u0482\3\2\2\2\u045e\u0460\5\66\34\2\u045f"+
		"\u045e\3\2\2\2\u045f\u0460\3\2\2\2\u0460\u0461\3\2\2\2\u0461\u0462\7t"+
		"\2\2\u0462\u0463\7\u0112\2\2\u0463\u0464\5\62\32\2\u0464\u0465\t*\2\2"+
		"\u0465\u0466\5\62\32\t\u0466\u0482\3\2\2\2\u0467\u0469\5\66\34\2\u0468"+
		"\u0467\3\2\2\2\u0468\u0469\3\2\2\2\u0469\u046a\3\2\2\2\u046a\u046c\5."+
		"\30\2\u046b\u046d\7\u0112\2\2\u046c\u046b\3\2\2\2\u046c\u046d\3\2\2\2"+
		"\u046d\u046e\3\2\2\2\u046e\u046f\t+\2\2\u046f\u0470\5\62\32\2\u0470\u0471"+
		"\7\u00ea\2\2\u0471\u0472\5\62\32\b\u0472\u0482\3\2\2\2\u0473\u0482\5("+
		"\25\2\u0474\u0482\5\32\16\2\u0475\u0478\7.\2\2\u0476\u0478\5\66\34\2\u0477"+
		"\u0475\3\2\2\2\u0477\u0476\3\2\2\2\u0477\u0478\3\2\2\2\u0478\u0479\3\2"+
		"\2\2\u0479\u0482\5$\23\2\u047a\u047c\7.\2\2\u047b\u047a\3\2\2\2\u047b"+
		"\u047c\3\2\2\2\u047c\u047d\3\2\2\2\u047d\u0482\5\64\33\2\u047e\u047f\5"+
		"\66\34\2\u047f\u0480\5\62\32\3\u0480\u0482\3\2\2\2\u0481\u0427\3\2\2\2"+
		"\u0481\u042c\3\2\2\2\u0481\u042d\3\2\2\2\u0481\u0432\3\2\2\2\u0481\u043b"+
		"\3\2\2\2\u0481\u044d\3\2\2\2\u0481\u045f\3\2\2\2\u0481\u0468\3\2\2\2\u0481"+
		"\u0473\3\2\2\2\u0481\u0474\3\2\2\2\u0481\u0477\3\2\2\2\u0481\u047b\3\2"+
		"\2\2\u0481\u047e\3\2\2\2\u0482\u0496\3\2\2\2\u0483\u0484\f\16\2\2\u0484"+
		"\u0485\t,\2\2\u0485\u0495\5\62\32\17\u0486\u0487\f\20\2\2\u0487\u048a"+
		"\7O\2\2\u0488\u048b\5\30\r\2\u0489\u048b\5\62\32\2\u048a\u0488\3\2\2\2"+
		"\u048a\u0489\3\2\2\2\u048b\u0495\3\2\2\2\u048c\u048d\f\17\2\2\u048d\u0490"+
		"\7P\2\2\u048e\u0491\5\30\r\2\u048f\u0491\5\62\32\2\u0490\u048e\3\2\2\2"+
		"\u0490\u048f\3\2\2\2\u0491\u0495\3\2\2\2\u0492\u0493\f\f\2\2\u0493\u0495"+
		"\5\60\31\2\u0494\u0483\3\2\2\2\u0494\u0486\3\2\2\2\u0494\u048c\3\2\2\2"+
		"\u0494\u0492\3\2\2\2\u0495\u0498\3\2\2\2\u0496\u0494\3\2\2\2\u0496\u0497"+
		"\3\2\2\2\u0497\63\3\2\2\2\u0498\u0496\3\2\2\2\u0499\u049a\7\u0159\2\2"+
		"\u049a\u049b\7\u0159\2\2\u049b\u04a9\7\u0159\2\2\u049c\u04a9\7\u015a\2"+
		"\2\u049d\u049f\7\u015c\2\2\u049e\u049d\3\2\2\2\u049e\u049f\3\2\2\2\u049f"+
		"\u04a1\3\2\2\2\u04a0\u04a2\n-\2\2\u04a1\u04a0\3\2\2\2\u04a2\u04a3\3\2"+
		"\2\2\u04a3\u04a4\3\2\2\2\u04a3\u04a1\3\2\2\2\u04a4\u04a6\3\2\2\2\u04a5"+
		"\u04a7\7\u015c\2\2\u04a6\u04a5\3\2\2\2\u04a6\u04a7\3\2\2\2\u04a7\u04a9"+
		"\3\2\2\2\u04a8\u0499\3\2\2\2\u04a8\u049c\3\2\2\2\u04a8\u049e\3\2\2\2\u04a9"+
		"\65\3\2\2\2\u04aa\u04ab\t.\2\2\u04ab\67\3\2\2\2\u00f9=@CGMQVZclpw|\u0080"+
		"\u0084\u008a\u008e\u0094\u009a\u009d\u009f\u00a7\u00af\u00b2\u00b6\u00be"+
		"\u00c1\u00c5\u00cd\u00d0\u00d4\u00dc\u00df\u00e3\u00e8\u00eb\u00ef\u00f4"+
		"\u00f7\u00fd\u0100\u0103\u0109\u010c\u0112\u0115\u0119\u011d\u0121\u0124"+
		"\u012d\u0131\u0134\u0139\u0140\u0144\u014a\u014d\u0153\u0157\u015c\u0160"+
		"\u0166\u0169\u016f\u0173\u0178\u017c\u0182\u0185\u018b\u018f\u0194\u0198"+
		"\u019e\u01a1\u01a7\u01ab\u01b0\u01b4\u01b7\u01ba\u01c0\u01c4\u01c7\u01ce"+
		"\u01d2\u01d7\u01db\u01de\u01e1\u01e3\u01ea\u01ee\u01f2\u01f6\u01fc\u0202"+
		"\u0204\u020e\u0210\u0214\u0218\u021c\u0223\u0229\u022d\u0230\u0237\u023a"+
		"\u023c\u0240\u0243\u0248\u024a\u0250\u0254\u0258\u025c\u0260\u0264\u0268"+
		"\u026b\u026f\u0274\u0278\u0285\u0289\u0292\u0295\u0298\u029b\u029e\u02a3"+
		"\u02a5\u02a8\u02ab\u02b0\u02b4\u02ba\u02c2\u02c6\u02ca\u02cd\u02d0\u02d5"+
		"\u02d9\u02dc\u02e4\u02e7\u02ed\u02ef\u02f3\u02f7\u02fb\u02ff\u0302\u0304"+
		"\u0306\u030a\u0310\u0314\u0317\u031a\u031f\u0322\u0325\u0328\u032b\u032e"+
		"\u0331\u0336\u0339\u033c\u033f\u0342\u0345\u0348\u034d\u0350\u0353\u0356"+
		"\u0359\u035e\u0361\u0373\u0377\u037a\u037d\u0382\u0385\u0388\u038b\u038e"+
		"\u0391\u0394\u0399\u039c\u039f\u03a2\u03a5\u03a8\u03ab\u03b0\u03b3\u03b6"+
		"\u03b9\u03bc\u03c1\u03c4\u03c7\u03cf\u03d5\u03dc\u03e8\u03f2\u0408\u040c"+
		"\u0411\u0414\u0418\u041d\u0421\u0432\u0436\u043b\u043f\u0443\u044a\u044d"+
		"\u0451\u0455\u045c\u045f\u0468\u046c\u0477\u047b\u0481\u048a\u0490\u0494"+
		"\u0496\u049e\u04a3\u04a6\u04a8";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}