package de.jplag.antlr;

import java.util.function.Consumer;
import java.util.function.Function;

import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.VariableRegistry;
import de.jplag.semantics.VariableScope;

/**
 * Builder for semantics on range mappings
 * @param <T> The type of rule
 */
@SuppressWarnings("unused")
public class RangeBuilder<T extends ParserRuleContext> {
    private final ContextTokenBuilder<T> start;
    private final ContextTokenBuilder<T> end;

    /**
     * New instance
     * @param start The builder for the start token
     * @param end The builder for the end token
     */
    RangeBuilder(ContextTokenBuilder<T> start, ContextTokenBuilder<T> end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Adds a class context to the variable registry
     * @return Self
     */
    public RangeBuilder<T> addClassContext() {
        this.start.addSemanticsHandler(VariableRegistry::enterClass);
        this.end.addSemanticsHandler(VariableRegistry::exitClass);
        return this;
    }

    /**
     * Adds a local scope to the variable registry
     * @return Self
     */
    public RangeBuilder<T> addLocalScope() {
        this.start.addSemanticsHandler(VariableRegistry::enterLocalScope);
        this.end.addSemanticsHandler(VariableRegistry::exitLocalScope);
        return this;
    }

    /**
     * Adds a semantics handler to the start token builder
     * @param handler The handler
     * @return Self
     */
    public RangeBuilder<T> addStartSemanticHandler(Consumer<VariableRegistry> handler) {
        this.start.addSemanticsHandler(handler);
        return this;
    }

    /**
     * Adds a semantic handler to the end token builder
     * @param handler The handler
     * @return Self
     */
    public RangeBuilder<T> addEndSemanticHandler(Consumer<VariableRegistry> handler) {
        this.end.addSemanticsHandler(handler);
        return this;
    }

    /**
     * Adds the start token as a variable when it is extracted
     * @param scope The scope for the variable
     * @param mutable true if the variable is mutable
     * @param nameGetter The getter for the name
     * @return Self
     */
    public RangeBuilder<T> addAsVariableOnStart(VariableScope scope, boolean mutable, Function<T, String> nameGetter) {
        this.start.addAsVariable(scope, mutable, nameGetter);
        return this;
    }

    /**
     * Sets the given semantic for the start token
     * @param semantics The semantic
     * @return Self
     */
    public RangeBuilder<T> withStartSemantics(CodeSemantics semantics) {
        this.start.withSemantics(semantics);
        return this;
    }

    /**
     * Sets the given semantic for the end token
     * @param semantics The semantic
     * @return Self
     */
    public RangeBuilder<T> withEndSemantics(CodeSemantics semantics) {
        this.end.withSemantics(semantics);
        return this;
    }

    /**
     * Sets a control semantics for both tokens
     * @return Self
     */
    public RangeBuilder<T> withControlSemantics() {
        this.start.withControlSemantics();
        this.end.withControlSemantics();
        return this;
    }
}
