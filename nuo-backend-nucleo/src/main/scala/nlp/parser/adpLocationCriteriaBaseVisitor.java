// Generated from D:\NuoCanvas\Source_Code\nuo-backend-nucleo\src\main\scala\nlp\grammar/adpLocationCriteria.g4 by ANTLR 4.7
package nlp.parser;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link adpLocationCriteriaVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public class adpLocationCriteriaBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements adpLocationCriteriaVisitor<T> {
    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public T visitFieldText(adpLocationCriteriaParser.FieldTextContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public T visitLocationText(adpLocationCriteriaParser.LocationTextContext ctx) {
        return visitChildren(ctx);
    }
}