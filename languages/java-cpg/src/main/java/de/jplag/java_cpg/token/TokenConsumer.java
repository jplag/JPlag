package de.jplag.java_cpg.token;

import java.io.File;

import de.fraunhofer.aisec.cpg.graph.Name;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.sarif.Region;
import de.jplag.Token;
import de.jplag.TokenType;

/**
 * This interface represents classes that can consume and save {@link Token}s.
 */
public interface TokenConsumer {

    /**
     * Creates a new {@link Token} to be consumed by this token consumer.
     * @param type the token type
     * @param file the file that contains the represented code
     * @param region the region that represents the token location
     * @param length length of the Token
     * @param name the name of the represented {@link Node}
     */
    void addToken(TokenType type, File file, Region region, int length, Name name);

}
