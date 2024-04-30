// Generated from D:\NuoCanvas\Source_Code\nuo-backend-nucleo\src\main\scala\nlp\grammar/adpLocationCriteria.g4 by ANTLR 4.7
package nlp.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class adpLocationCriteriaLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            LOCATION_INDICATOR = 1, KWD_STAY_IN = 2, KWD_LIVE_IN = 3, KWD_BASED_IN = 4, KWD_LOCATED_AT = 5,
            KWD_SITUATED_AT = 6, KWD_HQ_AT = 7, KWD_STAY = 8, KWD_LIVE = 9, KWD_HEADQUARTERED = 10,
            KWD_FROM = 11, KWD_IN = 12, KWD_AT = 13, KWD_OF = 14, ENCLOSED_STRING = 15, LETTER = 16,
            WS = 17;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    public static final String[] ruleNames = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "LOCATION_INDICATOR",
            "KWD_STAY_IN", "KWD_LIVE_IN", "KWD_BASED_IN", "KWD_LOCATED_AT", "KWD_SITUATED_AT",
            "KWD_HQ_AT", "KWD_STAY", "KWD_LIVE", "KWD_HEADQUARTERED", "KWD_FROM",
            "KWD_IN", "KWD_AT", "KWD_OF", "ENCLOSED_STRING", "LETTER", "WS"
    };

    private static final String[] _LITERAL_NAMES = {
    };
    private static final String[] _SYMBOLIC_NAMES = {
            null, "LOCATION_INDICATOR", "KWD_STAY_IN", "KWD_LIVE_IN", "KWD_BASED_IN",
            "KWD_LOCATED_AT", "KWD_SITUATED_AT", "KWD_HQ_AT", "KWD_STAY", "KWD_LIVE",
            "KWD_HEADQUARTERED", "KWD_FROM", "KWD_IN", "KWD_AT", "KWD_OF", "ENCLOSED_STRING",
            "LETTER", "WS"
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


    public adpLocationCriteriaLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "adpLocationCriteria.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public String[] getChannelNames() {
        return channelNames;
    }

    @Override
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    @Override
    public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
        switch (ruleIndex) {
            case 40:
                ENCLOSED_STRING_action((RuleContext) _localctx, actionIndex);
                break;
        }
    }

    private void ENCLOSED_STRING_action(RuleContext _localctx, int actionIndex) {
        switch (actionIndex) {
            case 0:
                setText(getText().replace("'", ""));
                break;
            case 1:
                setText(getText().replace("\"", ""));
                break;
        }
    }

    public static final String _serializedATN =
            "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\23\u014e\b\1\4\2" +
                    "\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4" +
                    "\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22" +
                    "\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31" +
                    "\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t" +
                    " \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t" +
                    "+\4,\t,\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3" +
                    "\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21" +
                    "\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30" +
                    "\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\34\3\34" +
                    "\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34" +
                    "\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\5\34\u00ac\n\34\3\35\3\35" +
                    "\3\35\3\35\5\35\u00b2\n\35\3\36\3\36\3\36\3\36\5\36\u00b8\n\36\3\37\3" +
                    "\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\5\37\u00c7" +
                    "\n\37\3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \5 \u00d3\n \3!\3!\3!\3!\3!\3!\3!\3" +
                    "!\3!\3!\3!\5!\u00e0\n!\3\"\3\"\3\"\3\"\5\"\u00e6\n\"\3#\3#\3#\3#\3#\3" +
                    "#\3#\3#\3#\3#\3#\3#\5#\u00f4\n#\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3" +
                    "$\3$\5$\u0104\n$\3%\3%\3%\3%\3%\3%\5%\u010c\n%\3%\3%\3%\3%\3%\3%\3%\3" +
                    "%\3%\3%\5%\u0118\n%\3&\3&\3&\3&\3&\3\'\3\'\3\'\3(\3(\3(\3)\3)\3)\3*\5" +
                    "*\u0129\n*\3*\3*\7*\u012d\n*\f*\16*\u0130\13*\3*\3*\5*\u0134\n*\3*\3*" +
                    "\5*\u0138\n*\3*\3*\7*\u013c\n*\f*\16*\u013f\13*\3*\3*\5*\u0143\n*\3*\5" +
                    "*\u0146\n*\3+\3+\3,\6,\u014b\n,\r,\16,\u014c\2\2-\3\2\5\2\7\2\t\2\13\2" +
                    "\r\2\17\2\21\2\23\2\25\2\27\2\31\2\33\2\35\2\37\2!\2#\2%\2\'\2)\2+\2-" +
                    "\2/\2\61\2\63\2\65\2\67\39\4;\5=\6?\7A\bC\tE\nG\13I\fK\rM\16O\17Q\20S" +
                    "\21U\22W\23\3\2 \4\2CCcc\4\2DDdd\4\2EEee\4\2FFff\4\2GGgg\4\2HHhh\4\2I" +
                    "Iii\4\2JJjj\4\2KKkk\4\2LLll\4\2MMmm\4\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4" +
                    "\2RRrr\4\2SSss\4\2TTtt\4\2UUuu\4\2VVvv\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZz" +
                    "z\4\2[[{{\4\2\\\\||\3\2))\3\2$$\6\2//C\\aac|\4\2\13\13\"\"\2\u0157\2\67" +
                    "\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2" +
                    "\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2" +
                    "\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\3Y\3\2\2\2\5[\3\2\2\2\7]" +
                    "\3\2\2\2\t_\3\2\2\2\13a\3\2\2\2\rc\3\2\2\2\17e\3\2\2\2\21g\3\2\2\2\23" +
                    "i\3\2\2\2\25k\3\2\2\2\27m\3\2\2\2\31o\3\2\2\2\33q\3\2\2\2\35s\3\2\2\2" +
                    "\37u\3\2\2\2!w\3\2\2\2#y\3\2\2\2%{\3\2\2\2\'}\3\2\2\2)\177\3\2\2\2+\u0081" +
                    "\3\2\2\2-\u0083\3\2\2\2/\u0085\3\2\2\2\61\u0087\3\2\2\2\63\u0089\3\2\2" +
                    "\2\65\u008b\3\2\2\2\67\u00ab\3\2\2\29\u00ad\3\2\2\2;\u00b3\3\2\2\2=\u00b9" +
                    "\3\2\2\2?\u00c8\3\2\2\2A\u00d4\3\2\2\2C\u00e1\3\2\2\2E\u00e7\3\2\2\2G" +
                    "\u00f5\3\2\2\2I\u0105\3\2\2\2K\u0119\3\2\2\2M\u011e\3\2\2\2O\u0121\3\2" +
                    "\2\2Q\u0124\3\2\2\2S\u0145\3\2\2\2U\u0147\3\2\2\2W\u014a\3\2\2\2YZ\t\2" +
                    "\2\2Z\4\3\2\2\2[\\\t\3\2\2\\\6\3\2\2\2]^\t\4\2\2^\b\3\2\2\2_`\t\5\2\2" +
                    "`\n\3\2\2\2ab\t\6\2\2b\f\3\2\2\2cd\t\7\2\2d\16\3\2\2\2ef\t\b\2\2f\20\3" +
                    "\2\2\2gh\t\t\2\2h\22\3\2\2\2ij\t\n\2\2j\24\3\2\2\2kl\t\13\2\2l\26\3\2" +
                    "\2\2mn\t\f\2\2n\30\3\2\2\2op\t\r\2\2p\32\3\2\2\2qr\t\16\2\2r\34\3\2\2" +
                    "\2st\t\17\2\2t\36\3\2\2\2uv\t\20\2\2v \3\2\2\2wx\t\21\2\2x\"\3\2\2\2y" +
                    "z\t\22\2\2z$\3\2\2\2{|\t\23\2\2|&\3\2\2\2}~\t\24\2\2~(\3\2\2\2\177\u0080" +
                    "\t\25\2\2\u0080*\3\2\2\2\u0081\u0082\t\26\2\2\u0082,\3\2\2\2\u0083\u0084" +
                    "\t\27\2\2\u0084.\3\2\2\2\u0085\u0086\t\30\2\2\u0086\60\3\2\2\2\u0087\u0088" +
                    "\t\31\2\2\u0088\62\3\2\2\2\u0089\u008a\t\32\2\2\u008a\64\3\2\2\2\u008b" +
                    "\u008c\t\33\2\2\u008c\66\3\2\2\2\u008d\u008e\59\35\2\u008e\u008f\5W,\2" +
                    "\u008f\u00ac\3\2\2\2\u0090\u0091\5;\36\2\u0091\u0092\5W,\2\u0092\u00ac" +
                    "\3\2\2\2\u0093\u0094\5=\37\2\u0094\u0095\5W,\2\u0095\u00ac\3\2\2\2\u0096" +
                    "\u0097\5? \2\u0097\u0098\5W,\2\u0098\u00ac\3\2\2\2\u0099\u009a\5A!\2\u009a" +
                    "\u009b\5W,\2\u009b\u00ac\3\2\2\2\u009c\u009d\5C\"\2\u009d\u009e\5W,\2" +
                    "\u009e\u00ac\3\2\2\2\u009f\u00a0\5K&\2\u00a0\u00a1\5W,\2\u00a1\u00ac\3" +
                    "\2\2\2\u00a2\u00a3\5Q)\2\u00a3\u00a4\5W,\2\u00a4\u00ac\3\2\2\2\u00a5\u00a6" +
                    "\5M\'\2\u00a6\u00a7\5W,\2\u00a7\u00ac\3\2\2\2\u00a8\u00a9\5O(\2\u00a9" +
                    "\u00aa\5W,\2\u00aa\u00ac\3\2\2\2\u00ab\u008d\3\2\2\2\u00ab\u0090\3\2\2" +
                    "\2\u00ab\u0093\3\2\2\2\u00ab\u0096\3\2\2\2\u00ab\u0099\3\2\2\2\u00ab\u009c" +
                    "\3\2\2\2\u00ab\u009f\3\2\2\2\u00ab\u00a2\3\2\2\2\u00ab\u00a5\3\2\2\2\u00ab" +
                    "\u00a8\3\2\2\2\u00ac8\3\2\2\2\u00ad\u00ae\5E#\2\u00ae\u00b1\5W,\2\u00af" +
                    "\u00b2\5M\'\2\u00b0\u00b2\5O(\2\u00b1\u00af\3\2\2\2\u00b1\u00b0\3\2\2" +
                    "\2\u00b2:\3\2\2\2\u00b3\u00b4\5G$\2\u00b4\u00b7\5W,\2\u00b5\u00b8\5M\'" +
                    "\2\u00b6\u00b8\5O(\2\u00b7\u00b5\3\2\2\2\u00b7\u00b6\3\2\2\2\u00b8<\3" +
                    "\2\2\2\u00b9\u00ba\5\5\3\2\u00ba\u00bb\5\3\2\2\u00bb\u00bc\5\'\24\2\u00bc" +
                    "\u00bd\5\13\6\2\u00bd\u00be\5\t\5\2\u00be\u00c6\5W,\2\u00bf\u00c0\5\37" +
                    "\20\2\u00c0\u00c1\5+\26\2\u00c1\u00c2\5)\25\2\u00c2\u00c3\5W,\2\u00c3" +
                    "\u00c4\5Q)\2\u00c4\u00c7\3\2\2\2\u00c5\u00c7\5M\'\2\u00c6\u00bf\3\2\2" +
                    "\2\u00c6\u00c5\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7>\3\2\2\2\u00c8\u00c9" +
                    "\5\31\r\2\u00c9\u00ca\5\37\20\2\u00ca\u00cb\5\7\4\2\u00cb\u00cc\5\3\2" +
                    "\2\u00cc\u00cd\5)\25\2\u00cd\u00ce\5\13\6\2\u00ce\u00cf\5\t\5\2\u00cf" +
                    "\u00d2\5W,\2\u00d0\u00d3\5M\'\2\u00d1\u00d3\5O(\2\u00d2\u00d0\3\2\2\2" +
                    "\u00d2\u00d1\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3@\3\2\2\2\u00d4\u00d5\5" +
                    "\'\24\2\u00d5\u00d6\5\23\n\2\u00d6\u00d7\5)\25\2\u00d7\u00d8\5+\26\2\u00d8" +
                    "\u00d9\5\3\2\2\u00d9\u00da\5)\25\2\u00da\u00db\5\13\6\2\u00db\u00dc\5" +
                    "\t\5\2\u00dc\u00df\5W,\2\u00dd\u00e0\5M\'\2\u00de\u00e0\5O(\2\u00df\u00dd" +
                    "\3\2\2\2\u00df\u00de\3\2\2\2\u00df\u00e0\3\2\2\2\u00e0B\3\2\2\2\u00e1" +
                    "\u00e2\5I%\2\u00e2\u00e5\5W,\2\u00e3\u00e6\5M\'\2\u00e4\u00e6\5O(\2\u00e5" +
                    "\u00e3\3\2\2\2\u00e5\u00e4\3\2\2\2\u00e5\u00e6\3\2\2\2\u00e6D\3\2\2\2" +
                    "\u00e7\u00e8\5\'\24\2\u00e8\u00e9\5)\25\2\u00e9\u00ea\5\3\2\2\u00ea\u00f3" +
                    "\5\63\32\2\u00eb\u00f4\5\'\24\2\u00ec\u00ed\5\13\6\2\u00ed\u00ee\5\t\5" +
                    "\2\u00ee\u00f4\3\2\2\2\u00ef\u00f0\5\23\n\2\u00f0\u00f1\5\35\17\2\u00f1" +
                    "\u00f2\5\17\b\2\u00f2\u00f4\3\2\2\2\u00f3\u00eb\3\2\2\2\u00f3\u00ec\3" +
                    "\2\2\2\u00f3\u00ef\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4F\3\2\2\2\u00f5\u00f6" +
                    "\5\31\r\2\u00f6\u00f7\5\23\n\2\u00f7\u0103\5-\27\2\u00f8\u00f9\5\13\6" +
                    "\2\u00f9\u00fa\5\t\5\2\u00fa\u0104\3\2\2\2\u00fb\u00fc\5\13\6\2\u00fc" +
                    "\u00fd\5\'\24\2\u00fd\u0104\3\2\2\2\u00fe\u0104\5\13\6\2\u00ff\u0100\5" +
                    "\23\n\2\u0100\u0101\5\35\17\2\u0101\u0102\5\17\b\2\u0102\u0104\3\2\2\2" +
                    "\u0103\u00f8\3\2\2\2\u0103\u00fb\3\2\2\2\u0103\u00fe\3\2\2\2\u0103\u00ff" +
                    "\3\2\2\2\u0104H\3\2\2\2\u0105\u0106\5\21\t\2\u0106\u0107\5\13\6\2\u0107" +
                    "\u0108\5\3\2\2\u0108\u010b\5\t\5\2\u0109\u010c\5W,\2\u010a\u010c\7/\2" +
                    "\2\u010b\u0109\3\2\2\2\u010b\u010a\3\2\2\2\u010b\u010c\3\2\2\2\u010c\u010d" +
                    "\3\2\2\2\u010d\u010e\5#\22\2\u010e\u010f\5+\26\2\u010f\u0110\5\3\2\2\u0110" +
                    "\u0111\5%\23\2\u0111\u0112\5)\25\2\u0112\u0113\5\13\6\2\u0113\u0117\5" +
                    "%\23\2\u0114\u0115\5\13\6\2\u0115\u0116\5\t\5\2\u0116\u0118\3\2\2\2\u0117" +
                    "\u0114\3\2\2\2\u0117\u0118\3\2\2\2\u0118J\3\2\2\2\u0119\u011a\5\r\7\2" +
                    "\u011a\u011b\5%\23\2\u011b\u011c\5\37\20\2\u011c\u011d\5\33\16\2\u011d" +
                    "L\3\2\2\2\u011e\u011f\5\23\n\2\u011f\u0120\5\35\17\2\u0120N\3\2\2\2\u0121" +
                    "\u0122\5\3\2\2\u0122\u0123\5)\25\2\u0123P\3\2\2\2\u0124\u0125\5\37\20" +
                    "\2\u0125\u0126\5\r\7\2\u0126R\3\2\2\2\u0127\u0129\5W,\2\u0128\u0127\3" +
                    "\2\2\2\u0128\u0129\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u012e\7)\2\2\u012b" +
                    "\u012d\n\34\2\2\u012c\u012b\3\2\2\2\u012d\u0130\3\2\2\2\u012e\u012c\3" +
                    "\2\2\2\u012e\u012f\3\2\2\2\u012f\u0131\3\2\2\2\u0130\u012e\3\2\2\2\u0131" +
                    "\u0133\7)\2\2\u0132\u0134\5W,\2\u0133\u0132\3\2\2\2\u0133\u0134\3\2\2" +
                    "\2\u0134\u0135\3\2\2\2\u0135\u0146\b*\2\2\u0136\u0138\5W,\2\u0137\u0136" +
                    "\3\2\2\2\u0137\u0138\3\2\2\2\u0138\u0139\3\2\2\2\u0139\u013d\7$\2\2\u013a" +
                    "\u013c\n\35\2\2\u013b\u013a\3\2\2\2\u013c\u013f\3\2\2\2\u013d\u013b\3" +
                    "\2\2\2\u013d\u013e\3\2\2\2\u013e\u0140\3\2\2\2\u013f\u013d\3\2\2\2\u0140" +
                    "\u0142\7$\2\2\u0141\u0143\5W,\2\u0142\u0141\3\2\2\2\u0142\u0143\3\2\2" +
                    "\2\u0143\u0144\3\2\2\2\u0144\u0146\b*\3\2\u0145\u0128\3\2\2\2\u0145\u0137" +
                    "\3\2\2\2\u0146T\3\2\2\2\u0147\u0148\t\36\2\2\u0148V\3\2\2\2\u0149\u014b" +
                    "\t\37\2\2\u014a\u0149\3\2\2\2\u014b\u014c\3\2\2\2\u014c\u014a\3\2\2\2" +
                    "\u014c\u014d\3\2\2\2\u014dX\3\2\2\2\26\2\u00ab\u00b1\u00b7\u00c6\u00d2" +
                    "\u00df\u00e5\u00f3\u0103\u010b\u0117\u0128\u012e\u0133\u0137\u013d\u0142" +
                    "\u0145\u014c\4\3*\2\3*\3";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}