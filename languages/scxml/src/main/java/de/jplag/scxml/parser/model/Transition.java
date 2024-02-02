package de.jplag.scxml.parser.model;

import java.util.List;
import java.util.Objects;

import de.jplag.scxml.parser.model.executable_content.ExecutableContent;

/**
 * Represents an SCXML {@literal <transition>} element. A transition defines the behavior of a statechart when a
 * specific event occurs or when a condition is met.
 * @param target the ID of the target state
 * @param event the value of the {@literal <event>} attribute of this transition
 * @param cond the cond attribute of the {@literal <if>} element which is the condition expression for the transition to
 * be executed
 * @param contents the list of executable contents to be executed when the transition is performed
 * @param timed whether this transition is timed (not part of standard SCXML, but can be modelled by using
 * {@literal <onentry>}, {@literal <onexit>} and {@literal <cancel>} elements)
 */
public record Transition(String target, String event, String cond, List<ExecutableContent> contents, boolean timed) implements StatechartElement {

    /**
     * Creates a new timed transition based on the given transition.
     * @param transition the original transition
     * @return a new transition with the timed flag set to true
     */
    public static Transition makeTimed(Transition transition) {
        return new Transition(transition.target, null, transition.cond, transition.contents, true);
    }

    /**
     * Checks if the transition is an initial transition.
     * @return whether the transition is initial
     */
    public boolean isInitial() {
        return target != null && event == null && cond == null;
    }

    /**
     * Checks if the transition is guarded.
     * @return whether the condition is not null
     */
    public boolean isGuarded() {
        return cond != null;
    }

    /**
     * Checks if the transition is timed.
     * @return whether the transition is timed
     */
    public boolean isTimed() {
        return timed;
    }

    @Override
    public String toString() {
        String prefix = isTimed() ? "Timed t" : "T";
        String suffix;
        if (event == null && cond == null) {
            suffix = "";
        } else if (event != null && cond != null) {
            suffix = String.format("(event='%s', cond='%s')", event, cond);
        } else if (event != null) {
            suffix = String.format("(event='%s')", event);
        } else {
            suffix = String.format("(cond='%s')", cond);
        }
        return String.format("%sransition (-> %s) %s {", prefix, target, suffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, event, cond, contents, timed);
    }
}
