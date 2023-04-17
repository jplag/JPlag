package de.jplag.scxml.parser.model.executable_content;

/**
 * Represents simple executable content as defined in <a href="https://www.w3.org/TR/scxml/#executable">sections 4.2 -
 * 4.7</a> of the SCXML specification. Other executable content is defined in the subclasses {@link Action},
 * {@link Send}, {@link Cancel}, {@link If}, {@link ElseIf} and {@link Else}.
 */
public record SimpleExecutableContent(Type type) implements ExecutableContent {

    @Override
    public String toString() {
        return String.format("SimpleExecutableContent (type=%s) {", type);
    }

    public enum Type {
        RAISE,
        ASSIGNMENT,
        SCRIPT,
        FOREACH,
        LOG
    }
}
