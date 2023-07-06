package de.jplag.antlr;

import java.io.File;
import java.util.function.Function;
import java.util.function.Predicate;

import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.TokenType;
import de.jplag.semantics.VariableScope;

/**
 * Builds tokens for {@link ParserRuleContext}s.
 * @param <T> The type of context
 */
public class ContextTokenBuilder<T extends ParserRuleContext> extends TokenBuilder<T> {
    private final Type type;

    ContextTokenBuilder(TokenType tokenType, Predicate<T> condition, TokenCollector collector, File file, Type type) {
        super(tokenType, condition, collector, file);
        this.type = type;
    }

    /**
     * Adds this builder as a variable to the variable registry
     * @param scope The scope of the variable
     * @param mutable true, if the variable is mutable
     * @param nameGetter The getter for the name, from the current {@link ParserRuleContext}
     * @return Self
     */
    public ContextTokenBuilder<T> addAsVariable(VariableScope scope, boolean mutable, Function<T, String> nameGetter) {
        addSemanticsHandler((semantics, rule) -> semantics.registry().registerVariable(nameGetter.apply(rule), scope, mutable));
        return this;
    }

    @Override
    protected org.antlr.v4.runtime.Token getAntlrToken(T antlrContent) {
        if (this.type != Type.STOP) {
            return antlrContent.getStart();
        } else {
            return antlrContent.getStop();
        }
    }

    @Override
    protected int getLength(T antlrContent) {
        if (this.type != Type.RANGE) {
            return super.getLength(antlrContent);
        } else {
            return antlrContent.getStop().getStopIndex() - antlrContent.getStart().getStartIndex() + 1;
        }
    }

    enum Type {
        START,
        STOP,
        RANGE
    }
}
