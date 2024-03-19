package de.jplag.java_cpg.token;

import java.io.File;

import de.fraunhofer.aisec.cpg.graph.Name;
import de.jplag.TokenType;

/**
 * This interface represents classes that can consume and save {@link de.jplag.Token}s.
 */
public interface TokenConsumer {

    /**
     * Creates a new token to be consumed by this token consumer.
     * @param type the token type
     * @param file the file that contains the represented code
     * @param startLine the line where the represented code starts
     * @param startColumn the column where the represented code starts
     * @param length The length of the represented code
     * @param name the name of the represented {@link de.fraunhofer.aisec.cpg.graph.Node}
     */
    void addToken(TokenType type, File file, int startLine, int startColumn, int length, Name name);

}
