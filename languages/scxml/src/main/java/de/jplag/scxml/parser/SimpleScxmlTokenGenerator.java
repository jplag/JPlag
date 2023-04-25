package de.jplag.scxml.parser;

import static de.jplag.scxml.ScxmlTokenType.*;
import static java.util.Map.entry;

import java.util.List;
import java.util.Map;

import de.jplag.scxml.ScxmlTokenType;
import de.jplag.scxml.parser.model.State;
import de.jplag.scxml.parser.model.Statechart;
import de.jplag.scxml.parser.model.StatechartElement;
import de.jplag.scxml.parser.model.Transition;
import de.jplag.scxml.parser.model.executable_content.*;
import de.jplag.scxml.util.AbstractScxmlVisitor;

/**
 * Visits a statechart and its contained elements to extract tokens using a simple strategy, i.e. a smaller token set
 * than for the handcrafted strategy (see {@link HandcraftedScxmlTokenGenerator}).
 */
public class SimpleScxmlTokenGenerator extends AbstractScxmlVisitor {

    /**
     * Creates the token generator.
     * @param adapter the parser adapter which receives the generated tokens
     */
    public SimpleScxmlTokenGenerator(ScxmlParserAdapter adapter) {
        super(adapter);
    }

    @Override
    public void visitStatechart(Statechart statechart) {
        depth = 0;
        for (State state : sorter.sort(statechart.states())) {
            visitState(state);
        }
    }

    /**
     * Visits the actions, transitions and substates of a state. Either of these attributes may be an empty list in which
     * case no tokens are extracted.
     * @param state the state whose actions, transitions and substates to visit
     */
    protected void visitStateContents(State state) {
        visitActions(state.actions());
        for (Transition transition : sorter.sort(state.transitions())) {
            visitTransition(transition);
        }
        for (State substate : sorter.sort(state.substates())) {
            visitState(substate);
        }
    }

    @Override
    public void visitState(State state) {
        adapter.addToken(STATE, state);
        depth++;
        visitStateContents(state);
        depth--;
        adapter.addEndToken(STATE_END);
    }

    @Override
    public void visitActions(List<Action> actions) {
        // Group actions by their type
        List<Action> onEntries = actions.stream().filter(a -> a.type() == Action.Type.ON_ENTRY).toList();
        List<Action> onExits = actions.stream().filter(a -> a.type() == Action.Type.ON_EXIT).toList();
        visitActions(onEntries, ON_ENTRY);
        visitActions(onExits, ON_EXIT);
    }

    private void visitActions(List<Action> actions, ScxmlTokenType tokenType) {
        if (!actions.isEmpty()) {
            // Only extract a single ENTRY / EXIT token even if the state contains multiple.
            // Functionally, this makes no difference.
            adapter.addToken(tokenType, actions.get(0));
            List<ExecutableContent> actionContents = actions.stream().flatMap(a -> a.contents().stream()).toList();
            depth++;
            // Do not sort executable content because the order is important
            for (ExecutableContent content : actionContents) {
                visitExecutableContent(content);
            }
            depth--;
            adapter.addEndToken(ACTION_END);
        }
    }

    @Override
    public void visitTransition(Transition transition) {
        adapter.addToken(TRANSITION, transition);
        depth++;
        // Do not sort executable content because the order is important
        for (ExecutableContent content : transition.contents()) {
            visitExecutableContent(content);
        }
        depth--;
        adapter.addEndToken(TRANSITION_END);
    }

    @Override
    public void visitIf(If ifElement) {
        adapter.addToken(IF, ifElement);
        depth++;
        for (ExecutableContent content : ifElement.contents()) {
            visitExecutableContent(content);
        }
        for (ElseIf elseIf : ifElement.elseIfs()) {
            visitElseIf(elseIf);
        }
        visitElse(ifElement.else_());
        depth--;
        adapter.addEndToken(IF_END);
    }

    @Override
    public void visitElseIf(ElseIf elseIf) {
        adapter.addToken(ELSE_IF, elseIf);
        for (ExecutableContent content : elseIf.contents()) {
            visitExecutableContent(content);
        }
        adapter.addEndToken(ELSE_IF_END);
    }

    @Override
    public void visitElse(Else elseElement) {
        if (elseElement != null) {
            adapter.addToken(ELSE, elseElement);
            for (ExecutableContent content : elseElement.contents()) {
                visitExecutableContent(content);
            }
            adapter.addToken(ELSE_END, elseElement);
        }
    }

    @Override
    public void visitExecutableContent(ExecutableContent content) {
        if (content instanceof SimpleExecutableContent simpleExecutableContent) {
            visitSimpleExecutableContent(simpleExecutableContent);
            return;
        }

        if (content instanceof If visitElement) {
            visitIf(visitElement);
            return;
        }

        Map<Class<? extends StatechartElement>, ScxmlTokenType> tokenTypeMap = Map.ofEntries(entry(Send.class, SEND), entry(Cancel.class, CANCEL));
        ScxmlTokenType type = tokenTypeMap.get(content.getClass());
        adapter.addToken(type, content);
    }

    @Override
    public void visitSimpleExecutableContent(SimpleExecutableContent content) {
        ScxmlTokenType type = switch (content.type()) {
            case RAISE -> RAISE;
            case ASSIGNMENT -> ASSIGNMENT;
            case SCRIPT -> SCRIPT;
            case FOREACH -> FOREACH;
            // Don't extract a token for log elements
            case LOG -> null;
        };
        if (type != null) {
            adapter.addToken(type, content);
        }
    }

}
