package de.jplag.scxml.parser.model.executable_content;

/**
 * Represents a {@literal <cancel>} SCXML element.
 * @param sendid represents the sendid attribute of the SCXML element which is the ID of the event to be cancelled
 */
public record Cancel(String sendid) implements ExecutableContent {

    @Override
    public String toString() {
        return "Cancel";
    }
}
