package de.jplag.scxml.parser;

import static de.jplag.scxml.ScxmlTokenType.*;

import de.jplag.scxml.parser.model.State;
import de.jplag.scxml.parser.model.Transition;
import de.jplag.scxml.parser.model.executable_content.ExecutableContent;

/**
 * Visits a statechart and its contained elements to extract tokens using a handcrafted strategy, i.e. a larger token
 * set than for the simple strategy (see {@link SimpleScxmlTokenGenerator}). Additional tokens are extracted depending
 * on the attributes of the statechart elements.
 */
public class HandcraftedScxmlTokenGenerator extends SimpleScxmlTokenGenerator {

    /**
     * Creates the visitor.
     * @param adapter is the parser adapter which receives the generated tokens.
     */
    public HandcraftedScxmlTokenGenerator(ScxmlParserAdapter adapter) {
        super(adapter);
    }

    protected void visitStateAttributes(State state) {
        if (state.initial()) {
            adapter.addToken(INITIAL_STATE, state);
        }
        if (state.parallel()) {
            adapter.addToken(PARALLEL_STATE, state);
        }
    }

    @Override
    public void visitState(State state) {
        adapter.addToken(state.isRegion() ? REGION : STATE, state);
        depth++;
        visitStateAttributes(state);
        visitStateContents(state);
        depth--;
        adapter.addToken(STATE_END, state);
    }

    @Override
    public void visitTransition(Transition transition) {
        if (transition.isTimed()) {
            adapter.addToken(TIMED_TRANSITION, transition);
        } else if (transition.isGuarded()) {
            adapter.addToken(GUARDED_TRANSITION, transition);
        } else {
            adapter.addToken(TRANSITION, transition);
        }

        depth++;
        for (ExecutableContent content : transition.contents()) {
            visitExecutableContent(content);
        }
        depth--;
        adapter.addToken(TRANSITION_END, transition);
    }
}
