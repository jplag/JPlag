package de.jplag.java_cpg.ai.variables.values.string.regex;

import java.util.List;

/**
 * {@link RegexItem} representing multiple characters.
 * @author ujiqk
 * @version 1.0
 */
public class RegexChars extends RegexItem {

    // null: represents an empty-non existent char
    private final List<Character> content;

    /**
     * Constructor for {@link RegexChars}.
     * @param content the list of characters; null represents an empty-non-existent char.
     */
    public RegexChars(List<Character> content) {
        this.content = content;
    }

    /**
     * @return the list of characters; null represents an empty-non-existent char.
     */
    public List<Character> getContent() {
        return content;
    }

    @Override
    public RegexItem merge(RegexItem other) {
        switch (other) {
            case null -> {
                content.add(null);
                return this;
            }
            case RegexChars o -> {
                content.addAll(o.getContent());
                return this;
            }
            case RegexChar o -> {
                content.add(o.getContent());
                return this;
            }
            default -> throw new IllegalStateException("Unexpected value: " + other);
        }
    }

    /**
     * @return whether this regex item can represent an empty string.
     */
    public boolean canBeEmpty() {
        for (Character c : content) {
            if (c == null) {
                return true;
            }
        }
        return false;
    }

}
