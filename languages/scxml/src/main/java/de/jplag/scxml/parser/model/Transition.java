package de.jplag.scxml.parser.model;

import java.util.List;
import java.util.Objects;

import de.jplag.scxml.parser.model.executable_content.ExecutableContent;

public record Transition(String target, String event, String cond, List<ExecutableContent> contents, boolean timed) implements StatechartElement {

    public static Transition makeTimed(Transition transition) {
        return new Transition(transition.target, null, transition.cond, transition.contents, true);
    }

    public boolean isInitial() {
        return target != null && event == null && cond == null;
    }

    public boolean isGuarded() {
        return cond != null;
    }

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
