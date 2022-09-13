package de.jplag;

/**
 * Indicates the type of a token. Needs to be implemented for each language module to declare what types of tokens can
 * be extracted from code written in that language. A token type is expected to be stateless, thus it is recommended to
 * use an <code>enum</code> or <code>record</code>.
 * @see SharedTokenType
 */
public interface TokenType {
    String getDescription();
}
