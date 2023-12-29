package de.jplag.java_cpg.token;

import de.jplag.Token;
import de.jplag.TokenType;
import java.io.File;

/**
 * This interface represents classes that can consume and save {@link Token}s.
 */
public interface TokenConsumer {

    void addToken(TokenType type, File file, int startLine, int startColumn, int length);

}
