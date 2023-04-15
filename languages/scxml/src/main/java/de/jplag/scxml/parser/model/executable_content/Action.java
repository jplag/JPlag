package de.jplag.scxml.parser.model.executable_content;

import java.util.List;
import java.util.Objects;

public record Action(Type type, List<ExecutableContent> contents) implements ExecutableContent {

    @Override
    public int hashCode() {
        return Objects.hash(type, contents);
    }

    @Override
    public String toString() {
        return String.format("Action (type=%s) {", type == Type.ON_ENTRY ? "OnEntry" : "OnExit");
    }

    public enum Type {
        ON_ENTRY,
        ON_EXIT,
    }
}
