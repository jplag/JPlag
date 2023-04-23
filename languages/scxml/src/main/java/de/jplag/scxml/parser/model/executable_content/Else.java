package de.jplag.scxml.parser.model.executable_content;

import java.util.List;

public record Else(List<ExecutableContent> contents) implements ExecutableContent {

    @Override
    public String toString() {
        return "Else {";
    }
}
