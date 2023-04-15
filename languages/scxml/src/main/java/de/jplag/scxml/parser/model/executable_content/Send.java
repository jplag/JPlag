package de.jplag.scxml.parser.model.executable_content;

public record Send(String event, String delay) implements ExecutableContent {

    @Override
    public String toString() {
        return String.format("Send (event='%s', delay='%s') {", event, delay);
    }
}
