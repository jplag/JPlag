package de.jplag.scxml.parser.model.executable_content;

/**
 * Represents simple executable content as defined in <a href="https://www.w3.org/TR/scxml/#executable">sections 4.2,
 * 4.6, 4.7 and 4.8 </a> of the SCXML specification. Other executable content is defined in the other subclasses of
 * {@link ExecutableContent}.
 * @param type is the content type.
 */
public record SimpleExecutableContent(Type type) implements ExecutableContent {

    @Override
    public String toString() {
        String result = type.toString().toLowerCase();
        // Capitalize the type
        return String.valueOf(result.charAt(0)).toUpperCase() + result.substring(1);
    }

    /**
     * Represents the types of simple executable content.
     */
    public enum Type {
        /**
         * Corresponds to the {@literal <raise>} element, which generates an internal event when executed.
         */
        RAISE,

        /**
         * Corresponds to the {@literal <assign>} element, which assigns a new value to a specified data model location when
         * executed.
         */
        ASSIGNMENT,

        /**
         * Corresponds to the {@literal <script>} element, which generates an internal event when executed.
         */
        SCRIPT,

        /**
         * Corresponds to the {@literal <foreach>} element, which iterates over a collection.
         */
        FOREACH,

        /**
         * Corresponds to the {@literal <log>} element, which prints a log message when executed.
         */
        LOG
    }
}
