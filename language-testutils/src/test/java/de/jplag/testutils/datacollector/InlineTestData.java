package de.jplag.testutils.datacollector;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.testutils.TemporaryFileHolder;
import de.jplag.util.FileUtils;

/**
 * Provides test source from a string.
 */
class InlineTestData implements TestData {
    private final String testData;

    InlineTestData(String testData) {
        this.testData = testData;
    }

    @Override
    public List<Token> parseTokens(Language language) throws ParsingException, IOException {
        File file = File.createTempFile("testSource", language.fileExtensions().getFirst());
        FileUtils.write(file, this.testData);
        List<Token> tokens = language.parse(Collections.singleton(file), false);
        TemporaryFileHolder.addTemporaryFile(file);
        return tokens;
    }

    @Override
    public String[] getSourceLines() {
        return this.testData.lines().toArray(String[]::new);
    }

    @Override
    public String describeTestSource() {
        return "(inline source: " + this.testData + " )";
    }

    @Override
    public String toString() {
        return "inline: " + System.lineSeparator() + testData;
    }
}
