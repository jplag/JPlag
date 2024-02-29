package de.jplag.scxml.parser.model.executable_content;

import java.util.List;

/**
 * Represents a {@code <foreach>} SCXML element, which is executable content that executes its contents for each item in
 * a given data set.
 * @param contents the list of executable contents within the {@code <foreach>} element
 */
public record ForEach(List<ExecutableContent> contents) implements ExecutableContent {

    @Override
    public String toString() {
        return "ForEach";
    }
}
