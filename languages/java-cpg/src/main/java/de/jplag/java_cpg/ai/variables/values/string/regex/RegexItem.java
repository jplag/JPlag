package de.jplag.java_cpg.ai.variables.values.string.regex;

import org.jetbrains.annotations.NotNull;

/**
 * Simplified regex syntax with only single or multiple characters.
 * @author ujiqk
 * @version 1.0
 */
public abstract class RegexItem {

    /**
     * Merges two regex items.
     * @param one the first regex item.
     * @param two the second regex item.
     * @return the merged regex item.
     */
    public static RegexItem merge(@NotNull RegexItem one, @NotNull RegexItem two) {
        return one.merge(two);
    }

    /**
     * Merges this regex item with another regex item.
     * @param other the other regex item.
     * @return the merged regex item.
     */
    public abstract RegexItem merge(RegexItem other);

}
