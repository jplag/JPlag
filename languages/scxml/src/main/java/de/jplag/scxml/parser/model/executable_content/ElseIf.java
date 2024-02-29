package de.jplag.scxml.parser.model.executable_content;

import java.util.List;

/**
 * Represents an {@literal <else>} SCXML element, which is part of an {@link If} element used for handling multiple
 * conditions. The {@literal <elseif>} element is executed when the preceding {@literal <if>} condition and any prior
 * {@literal <elseif>} conditions are not met and its own condition is satisfied.
 * @param contents the list of executable content to be executed when the {@literal <elseif>} branch is reached
 */
public record ElseIf(List<ExecutableContent> contents) implements ExecutableContent {

    @Override
    public String toString() {
        return "ElseIf {";
    }
}
