package de.jplag;

/**
 * Indicates the type of a token. Needs to be implemented for each language module to declare what types of tokens can
 * be extracted from code written in that language. A token type is expected to be stateless, thus it is recommended to
 * use an <code>enum</code> or <code>record</code>.
 * @see SharedTokenType
 */
public interface TokenType {
    /**
     * Returns the user-readable description of this token type.
     */
    String getDescription();

    /**
     * Indicates that no matches containing this token type shall be generated. Defaults to <code>false</code>.
     * @return <code>true</code> if token type is excluded from matching, otherwise <code>false</code>.
     */
    default Boolean isExcludedFromMatching() {
        return false;
    }
}
