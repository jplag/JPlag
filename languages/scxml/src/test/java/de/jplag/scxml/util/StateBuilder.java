package de.jplag.scxml.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.jplag.scxml.parser.model.State;
import de.jplag.scxml.parser.model.Transition;
import de.jplag.scxml.parser.model.executable_content.Action;
import de.jplag.scxml.parser.model.executable_content.ExecutableContent;

/**
 * Builder class for constructing {@link State} instances in a fluent and configurable manner. Supports setting the
 * state ID, marking as initial or parallel, adding transitions, substates, and specifying actions to execute on entry
 * or exit.
 */
public class StateBuilder {

    private final String id;
    private final List<Action> actions = new ArrayList<>();
    private ArrayList<Transition> transitions = new ArrayList<>();
    private List<State> substates = new ArrayList<>();
    private boolean initial;
    private boolean parallel;

    /**
     * Initializes a new state builder with the given identifier.
     * @param id the unique identifier for the state
     */
    public StateBuilder(String id) {
        this.id = id;
    }

    /**
     * Marks the state as parallel, allowing concurrent substates.
     * @return the current {@link StateBuilder} instance for chaining
     */
    public StateBuilder setParallel() {
        parallel = true;
        return this;
    }

    /**
     * Marks the state as the initial state within its parent context.
     * @return the current {@link StateBuilder} instance for chaining
     */
    public StateBuilder setInitial() {
        initial = true;
        return this;
    }

    /**
     * Adds transitions to the state being built.
     * @param transitions one or more {@link Transition} objects to add
     * @return the current {@link StateBuilder} instance for chaining
     */
    public StateBuilder addTransitions(Transition... transitions) {
        this.transitions = new ArrayList<>(List.of(transitions));
        return this;
    }

    /**
     * Adds substates to the state being built.
     * @param substates one or more {@link State} objects to add as children
     * @return the current {@link StateBuilder} instance for chaining
     */
    public StateBuilder addSubstates(State... substates) {
        this.substates = Arrays.asList(substates);
        return this;
    }

    /**
     * Adds actions to be executed upon entering the state.
     * @param contents one or more {@link ExecutableContent} to run on entry
     * @return the current {@link StateBuilder} instance for chaining
     */
    public StateBuilder addOnEntry(ExecutableContent... contents) {
        this.actions.add(new Action(Action.Type.ON_ENTRY, List.of(contents)));
        return this;
    }

    /**
     * Adds actions to be executed upon exiting the state.
     * @param contents one or more {@link ExecutableContent} to run on exit
     * @return the current {@link StateBuilder} instance for chaining
     */
    public StateBuilder addOnExit(ExecutableContent... contents) {
        this.actions.add(new Action(Action.Type.ON_EXIT, List.of(contents)));
        return this;
    }

    /**
     * Constructs a {@link State} instance with the configured properties, transitions, substates, and actions.
     * @return a new {@link State} object
     */
    public State build() {
        return new State(id, transitions, substates, actions, initial, parallel);
    }
}
