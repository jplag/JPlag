package de.jplag.scxml.parser.model.executable_content;

public record Cancel(String sendid) implements ExecutableContent {

    @Override
    public String toString() {
        return "Cancel";
    }
}
