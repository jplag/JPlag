package de.jplag.java_cpg.ai.variables.values.string.regex;

import java.util.Arrays;
import java.util.List;

/**
 * {@link RegexItem} representing a single character.
 * @author ujiqk
 * @version 1.0
 */
public class RegexChar extends RegexItem {

    private final char content;

    /**
     * Constructor for {@link RegexChar}.
     * @param content the character.
     */
    public RegexChar(char content) {
        this.content = content;
    }

    /**
     * @return the character.
     */
    public char getContent() {
        return content;
    }

    @Override
    public RegexItem merge(RegexItem other) {
        switch (other) {
            case null -> {
                return new RegexChars(Arrays.asList(this.content, null));
            }
            case RegexChars o -> {
                return o.merge(this);
            }
            case RegexChar o -> {
                if (o.content == this.content) {
                    return this;
                }
                return new RegexChars(List.of(this.content, o.content));
            }
            default -> throw new IllegalStateException("Unexpected value: " + other);
        }
    }

    @Override
    public int hashCode() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        RegexChar regexChar = (RegexChar) o;
        return content == regexChar.content;
    }

}
