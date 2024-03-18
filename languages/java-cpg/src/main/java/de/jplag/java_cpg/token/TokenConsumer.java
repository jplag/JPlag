package de.jplag.java_cpg.token;

import java.io.File;

import de.fraunhofer.aisec.cpg.graph.Name;
import de.jplag.Token;
import de.jplag.TokenType;

/**
 * This interface represents classes that can consume and save {@link Token}s.
 */
public interface TokenConsumer {

    void addToken(TokenType type, File file, int startLine, int startColumn, int length, Name name);

}
