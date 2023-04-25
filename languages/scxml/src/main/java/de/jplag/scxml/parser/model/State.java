package de.jplag.scxml.parser.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import de.jplag.scxml.parser.model.executable_content.Action;
import de.jplag.scxml.parser.model.executable_content.Cancel;
import de.jplag.scxml.parser.model.executable_content.ExecutableContent;
import de.jplag.scxml.parser.model.executable_content.Send;

/**
 * Represents an SCXML {@code <state>} element in the statechart model. A state can be a simple state, an initial state,
 * a parallel state, or a region (a state containing substates). A state can have outgoing transitions, actions (such as
 * onentry and onexit), and timed transitions (a concept specific to itemis CREATE).
 * @param id the ID of the state
 * @param transitions a non-null list of outgoing transitions of this state
 * @param substates a non-null list of substates of this state
 * @param actions a non-null list of actions associated with this state
 * @param initial whether this state is an initial state
 * @param parallel whether this state is a parallel state
 */
public record State(String id, List<Transition> transitions, List<State> substates, List<Action> actions, boolean initial, boolean parallel)
        implements StatechartElement {

    /**
     * Constructs a new state.
     * @throws IllegalArgumentException if {@code transitions} or {@code substates} is null
     */
    public State(String id, List<Transition> transitions, List<State> substates, List<Action> actions, boolean initial, boolean parallel) {
        if (transitions == null) {
            throw new IllegalArgumentException("State.transitions must not be null");
        }

        if (substates == null) {
            throw new IllegalArgumentException("State.substates must not be null");
        }

        this.id = id;
        this.transitions = transitions;
        this.substates = substates;
        this.actions = actions;
        this.initial = initial;
        this.parallel = parallel;
        updateTimedTransitions();
    }

    /**
     * Constructs a state with an ID, setting all other variables to default values.
     * @param id the ID of the state
     */
    public State(String id) {
        this(id, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, false);
    }

    /**
     * @return whether this state is a region, i.e. it contains at least one substate
     */
    public boolean isRegion() {
        return !substates.isEmpty();
    }

    /**
     * @return whether this state is a simple state, meaning that is neither an initial state nor a parallel state
     */
    public boolean isSimple() {
        return !initial && !parallel;
    }

    private Stream<Action> onEntries() {
        return actions.stream().filter(a -> a.type() == Action.Type.ON_ENTRY);
    }

    private Stream<Action> onExits() {
        return actions.stream().filter(a -> a.type() == Action.Type.ON_EXIT);
    }

    private List<Send> getOnEntrySends() {
        Stream<List<ExecutableContent>> onEntryContents = this.onEntries().map(Action::contents);
        return onEntryContents.flatMap(List::stream).filter(Send.class::isInstance).map(s -> (Send) s).toList();
    }

    /**
     * Sets the timed attribute of each transition of this state that is timed. To model a timed transition, itemis Create
     * adds onentry.send, onexit.cancel and transition elements with matching IDs. These elements will be removed if they
     * are part of a timed transition.
     **/
    private void updateTimedTransitions() {
        if (this.transitions().isEmpty() || this.actions().isEmpty()) {
            return;
        }

        for (Action onExit : onExits().toList()) {
            var cancelElements = onExit.contents().stream().filter(Cancel.class::isInstance).map(c -> (Cancel) c).toList();
            for (Cancel cancel : cancelElements) {
                replaceMatchingTransitions(cancel.sendid(), onExit, cancel);
            }
        }
    }

    private void replaceMatchingTransitions(String sendId, Action onExit, Cancel cancel) {
        List<Send> onEntrySends = getOnEntrySends();
        for (Transition transition : transitions) {
            boolean foundTimedTransition = false;
            // Then check if there is also a matching send element in <onentry>
            if (isMatchingTransition(transition, sendId) && onEntries().toList().stream().anyMatch(onEntry -> {
                Optional<Send> matchingSend = onEntrySends.stream().filter(send -> send.event().equals(sendId)).map(send -> {
                    removeTimedTransitionElements(onEntry, send, onExit, cancel);
                    return send;
                }).findFirst();
                return matchingSend.isPresent();
            })) {
                foundTimedTransition = true;
            }
            if (foundTimedTransition) {
                // Replace the transition with a timed transition
                transitions.set(transitions.indexOf(transition), Transition.makeTimed(transition));
            }
        }
    }

    private boolean isMatchingTransition(Transition transition, String sendId) {
        return transition.event() != null && transition.event().equals(sendId);
    }

    private void removeTimedTransitionElements(Action onEntry, Send send, Action onExit, Cancel cancel) {
        List<ExecutableContent> filteredContents = onEntry.contents().stream().filter(c -> !(c instanceof Send && c.equals(send))).toList();
        if (filteredContents.isEmpty()) {
            // Remove onEntry entirely if it is now empty
            actions.remove(onEntry);
        } else {
            // Only remove the matching onEntry.send
            Action filteredOnEntry = new Action(Action.Type.ON_ENTRY, filteredContents);
            actions.set(actions.indexOf(onEntry), filteredOnEntry);
        }

        // Do something similar for onExit
        filteredContents = onExit.contents().stream().filter(c -> !(c instanceof Cancel && c.equals(cancel))).toList();
        if (filteredContents.isEmpty()) {
            actions.remove(onExit);
        } else {
            Action filteredOnExit = new Action(Action.Type.ON_EXIT, filteredContents);
            actions.set(actions.indexOf(onExit), filteredOnExit);
        }
    }

    @Override
    public String toString() {
        String[] parts = {"", ""};
        parts[1] = isRegion() ? "Region" : "State";
        if (initial) {
            parts[0] = "Initial ";
            parts[1] = parts[1].toLowerCase();
        }
        return String.format("%s: %s%s {", id, parts[0], parts[1]);
    }

}
