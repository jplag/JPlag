package de.jplag.scxml.parser.model.executable_content;

import java.util.List;

/**
 * Represents an {@literal <else>} SCXML element, which is part of an {@link If} element used when all the preceding
 * conditions in the {@literal <if>} and {@literal <elseif>} elements are not met.
 * @param contents the list of executable content to be executed when the {@literal <else>} branch is reached
 */
public record Else(List<ExecutableContent> contents) implements ExecutableContent {

    @Override
    public String toString() {
        return "Else {";
    }
}
