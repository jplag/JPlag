package de.jplag.scxml.parser.model.executable_content;

import java.util.List;
import java.util.Objects;

/**
 * Represents {@literal <onentry>} and {@literal <onexit>} SCXML elements which contain executable content to be
 * executed when a state is entered / exited.
 * @param type the type of the action ({@link Type#ON_ENTRY} or {@link Type#ON_EXIT})
 * @param contents the list of executable contents within the action
 */
public record Action(Type type, List<ExecutableContent> contents) implements ExecutableContent {

    @Override
    public int hashCode() {
        return Objects.hash(type, contents);
    }

    @Override
    public String toString() {
        return String.format("%s {", type == Type.ON_ENTRY ? "OnEntry" : "OnExit");
    }

    /**
     * The type of the action.
     */
    public enum Type {
        /**
         * Represents an {@literal <onentry>} SCXML element.
         */
        ON_ENTRY,
        /**
         * Represents an {@literal <onexit>} SCXML element.
         */
        ON_EXIT,
    }
}
