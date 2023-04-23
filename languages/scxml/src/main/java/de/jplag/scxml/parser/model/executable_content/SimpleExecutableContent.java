package de.jplag.scxml.parser.model.executable_content;

/**
 * Represents simple executable content as defined in <a href="https://www.w3.org/TR/scxml/#executable">sections 4.2,
 * 4.6, 4.7 and 4.8 </a> of the SCXML specification. Other executable content is defined in the other subclasses of
 * {@link ExecutableContent}.
 */
public record SimpleExecutableContent(Type type) implements ExecutableContent {

    @Override
    public String toString() {
        String result = type.toString().toLowerCase();
        // Capitalize the type
        return String.valueOf(result.charAt(0)).toUpperCase() + result.substring(1);
    }

    public enum Type {
        RAISE,
        ASSIGNMENT,
        SCRIPT,
        FOREACH,
        LOG
    }
}
