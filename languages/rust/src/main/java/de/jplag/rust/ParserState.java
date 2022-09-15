package de.jplag.rust;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

/**
 * A ParserState is a representation for the state of a parser, consisting of a stack of Contexts.
 * @param <C> The implementation of the Contexts.
 */
public class ParserState<C extends ParserState.Context> {
    private final Deque<C> blockContexts;

    /**
     * Creates a new ParserState().
     */
    public ParserState() {
        blockContexts = new LinkedList<>();
    }

    /**
     * Enters a context.
     * @param context the context to enter
     */
    protected void enter(C context) {
        blockContexts.push(context);
    }

    /**
     * Leaves the current context, making sure that it is one of the given ones.
     * @param contexts The contexts to expect to end here
     */
    @SafeVarargs
    protected final void leaveAsserted(C... contexts) {
        C topContext = blockContexts.pop();
        assert Arrays.stream(contexts).anyMatch(context -> context == topContext);
    }

    /**
     * Returns the current context.
     * @return the current context
     */
    protected C getCurrentContext() {
        return blockContexts.peek();
    }

    /**
     * Leaves the current context if it is the given one.
     * @param blockContext the context that may be expected to end here
     */
    protected void leaveIfInContext(C blockContext) {
        if (blockContexts.peek() == blockContext) {
            blockContexts.pop();
        }
    }

    /**
     * A Context is a grammatical situation, e.g. a class body, or a while statement. Each Context should have a startType
     * and an endType, designating the start and the end of the context as a token type.
     */
    protected interface Context {

        /**
         * Returns the token type that marks the start of the Context.
         * @return the start type
         */
        Optional<RustTokenType> getStartType();

        /**
         * Returns the token type that marks the end of the Context.
         * @return the end type
         */
        Optional<RustTokenType> getEndType();
    }
}