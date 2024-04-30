// Generated from D:\NuoCanvas\Source_Code\nuo-backend-nucleo\src\main\scala\nlp\grammar/adpLocationCriteria.g4 by ANTLR 4.7
package nlp.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link adpLocationCriteriaParser}.
 */
public interface adpLocationCriteriaListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link adpLocationCriteriaParser#fieldText}.
     *
     * @param ctx the parse tree
     */
    void enterFieldText(adpLocationCriteriaParser.FieldTextContext ctx);

    /**
     * Exit a parse tree produced by {@link adpLocationCriteriaParser#fieldText}.
     *
     * @param ctx the parse tree
     */
    void exitFieldText(adpLocationCriteriaParser.FieldTextContext ctx);

    /**
     * Enter a parse tree produced by {@link adpLocationCriteriaParser#locationText}.
     *
     * @param ctx the parse tree
     */
    void enterLocationText(adpLocationCriteriaParser.LocationTextContext ctx);

    /**
     * Exit a parse tree produced by {@link adpLocationCriteriaParser#locationText}.
     *
     * @param ctx the parse tree
     */
    void exitLocationText(adpLocationCriteriaParser.LocationTextContext ctx);
}