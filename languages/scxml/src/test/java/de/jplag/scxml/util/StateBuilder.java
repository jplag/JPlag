package de.jplag.scxml.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.jplag.scxml.parser.model.State;
import de.jplag.scxml.parser.model.Transition;
import de.jplag.scxml.parser.model.executable_content.Action;
import de.jplag.scxml.parser.model.executable_content.ExecutableContent;

public class StateBuilder {

    private final String id;
    private final List<Action> actions = new ArrayList<>();
    private ArrayList<Transition> transitions = new ArrayList<>();
    private List<State> substates = new ArrayList<>();
    private boolean initial;
    private boolean parallel;

    public StateBuilder(String id) {
        this.id = id;
    }

    public StateBuilder setParallel() {
        parallel = true;
        return this;
    }

    public StateBuilder setInitial() {
        initial = true;
        return this;
    }

    public StateBuilder addTransitions(Transition... transitions) {
        this.transitions = new ArrayList<>(List.of(transitions));
        return this;
    }

    public StateBuilder addSubstates(State... substates) {
        this.substates = Arrays.asList(substates);
        return this;
    }

    public StateBuilder addOnEntry(ExecutableContent... contents) {
        this.actions.add(new Action(Action.Type.ON_ENTRY, List.of(contents)));
        return this;
    }

    public StateBuilder addOnExit(ExecutableContent... contents) {
        this.actions.add(new Action(Action.Type.ON_EXIT, List.of(contents)));
        return this;
    }

    public State build() {
        return new State(id, transitions, substates, actions, initial, parallel);
    }
}
