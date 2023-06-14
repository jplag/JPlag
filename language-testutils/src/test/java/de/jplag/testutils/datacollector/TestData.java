package de.jplag.testutils.datacollector;

import java.io.IOException;
import java.util.List;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * Provides test code. Each instance represents a single source file. Serves as a way to encapsulate various locations
 * for test code, such as files or java strings.
 */
public interface TestData {
    /**
     * Parses the tokens for this providers source.
     * @param language The language to parse in
     * @return The parsed tokens
     * @throws ParsingException From language
     * @throws IOException If any IO errors occur
     */
    List<Token> parseTokens(Language language) throws ParsingException, IOException;

    /**
     * @return A list of all source lines
     * @throws IOException If any IO errors occur
     */
    String[] getSourceLines() throws IOException;

    /**
     * Describe the test source, to that it can be identified in error messages.
     * @return The source description
     */
    String describeTestSource();
}
