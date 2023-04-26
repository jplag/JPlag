package de.jplag.scxml.parser.model;

import java.util.List;

/**
 * Represents an SCXML statechart.
 * @param name the name of the statechart
 * @param states a list of states comprising this statechart
 */
public record Statechart(String name, List<State> states) implements StatechartElement {

    @Override
    public String toString() {
        return "%s: Statechart {";
    }
}
