// Generated from D:\NuoCanvas\Source_Code\nuo-backend-nucleo\src\main\scala\nlp\grammar/adpLocationCriteria.g4 by ANTLR 4.7
package nlp.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class adpLocationCriteriaParser extends Parser {
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
    public static final int
            RULE_fieldText = 0, RULE_locationText = 1;
    public static final String[] ruleNames = {
            "RuleText", "locationText"
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
    public ATN getATN() {
        return _ATN;
    }

    public adpLocationCriteriaParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public static class FieldTextContext extends ParserRuleContext {
        public String value;

        public TerminalNode LOCATION_INDICATOR() {
            return getToken(adpLocationCriteriaParser.LOCATION_INDICATOR, 0);
        }

        public LocationTextContext locationText() {
            return getRuleContext(LocationTextContext.class, 0);
        }

        public FieldTextContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_fieldText;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof adpLocationCriteriaListener)
                ((adpLocationCriteriaListener) listener).enterFieldText(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof adpLocationCriteriaListener)
                ((adpLocationCriteriaListener) listener).exitFieldText(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof adpLocationCriteriaVisitor)
                return ((adpLocationCriteriaVisitor<? extends T>) visitor).visitFieldText(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FieldTextContext fieldText() throws RecognitionException {
        FieldTextContext _localctx = new FieldTextContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_fieldText);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(4);
                match(LOCATION_INDICATOR);
                setState(5);
                locationText();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class LocationTextContext extends ParserRuleContext {
        public TerminalNode ENCLOSED_STRING() {
            return getToken(adpLocationCriteriaParser.ENCLOSED_STRING, 0);
        }

        public List<TerminalNode> LOCATION_INDICATOR() {
            return getTokens(adpLocationCriteriaParser.LOCATION_INDICATOR);
        }

        public TerminalNode LOCATION_INDICATOR(int i) {
            return getToken(adpLocationCriteriaParser.LOCATION_INDICATOR, i);
        }

        public LocationTextContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_locationText;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof adpLocationCriteriaListener)
                ((adpLocationCriteriaListener) listener).enterLocationText(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof adpLocationCriteriaListener)
                ((adpLocationCriteriaListener) listener).exitLocationText(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof adpLocationCriteriaVisitor)
                return ((adpLocationCriteriaVisitor<? extends T>) visitor).visitLocationText(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LocationTextContext locationText() throws RecognitionException {
        LocationTextContext _localctx = new LocationTextContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_locationText);
        int _la;
        try {
            int _alt;
            setState(13);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 1, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(7);
                    match(ENCLOSED_STRING);
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(9);
                    _errHandler.sync(this);
                    _alt = 1 + 1;
                    do {
                        switch (_alt) {
                            case 1 + 1: {
                                {
                                    setState(8);
                                    _la = _input.LA(1);
                                    if (_la <= 0 || (_la == LOCATION_INDICATOR)) {
                                        _errHandler.recoverInline(this);
                                    } else {
                                        if (_input.LA(1) == Token.EOF) matchedEOF = true;
                                        _errHandler.reportMatch(this);
                                        consume();
                                    }
                                }
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                        setState(11);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                    } while (_alt != 1 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static final String _serializedATN =
            "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\23\22\4\2\t\2\4\3" +
                    "\t\3\3\2\3\2\3\2\3\3\3\3\6\3\f\n\3\r\3\16\3\r\5\3\20\n\3\3\3\3\r\2\4\2" +
                    "\4\2\3\3\2\3\3\2\21\2\6\3\2\2\2\4\17\3\2\2\2\6\7\7\3\2\2\7\b\5\4\3\2\b" +
                    "\3\3\2\2\2\t\20\7\21\2\2\n\f\n\2\2\2\13\n\3\2\2\2\f\r\3\2\2\2\r\16\3\2" +
                    "\2\2\r\13\3\2\2\2\16\20\3\2\2\2\17\t\3\2\2\2\17\13\3\2\2\2\20\5\3\2\2" +
                    "\2\4\r\17";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}