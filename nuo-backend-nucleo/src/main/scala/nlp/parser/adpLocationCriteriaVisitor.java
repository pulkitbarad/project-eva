// Generated from D:\NuoCanvas\Source_Code\nuo-backend-nucleo\src\main\scala\nlp\grammar/adpLocationCriteria.g4 by ANTLR 4.7
package nlp.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link adpLocationCriteriaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface adpLocationCriteriaVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link adpLocationCriteriaParser#fieldText}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFieldText(adpLocationCriteriaParser.FieldTextContext ctx);

    /**
     * Visit a parse tree produced by {@link adpLocationCriteriaParser#locationText}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLocationText(adpLocationCriteriaParser.LocationTextContext ctx);
}