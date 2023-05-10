package de.jplag.scxml.parser.model.executable_content;

/**
 * Represents a {@literal <send>} SCXML element.
 * @param event represents the event attribute of the SCXML element, which is the name of the event to be sent
 * @param delay represents the delay attribute of the SCXML element, which is the duration to wait before sending the
 * event
 */
public record Send(String event, String delay) implements ExecutableContent {

    @Override
    public String toString() {
        return String.format("Send (event='%s', delay='%s') {", event, delay);
    }
}
