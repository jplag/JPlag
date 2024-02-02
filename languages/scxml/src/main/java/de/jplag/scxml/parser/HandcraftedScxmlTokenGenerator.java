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
     * Creates the token generator.
     * @param adapter the parser adapter which receives the generated tokens
     */
    public HandcraftedScxmlTokenGenerator(ScxmlParserAdapter adapter) {
        super(adapter);
    }

    /**
     * Visits a state and extracts tokens based on whether its {@code initial} and {@code isRegion} attributes are set to
     * {@code true}.
     * @param state the state to visit
     */
    protected void visitStateAttributes(State state) {
        if (state.isRegion() && state.initial()) {
            adapter.addToken(INITIAL_REGION, state);
        } else if (state.isRegion()) {
            adapter.addToken(REGION, state);
        } else if (state.initial()) {
            adapter.addToken(INITIAL_STATE, state);
        } else {
            adapter.addToken(STATE, state);
        }
    }

    @Override
    public void visitState(State state) {
        visitStateAttributes(state);
        depth++;
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
