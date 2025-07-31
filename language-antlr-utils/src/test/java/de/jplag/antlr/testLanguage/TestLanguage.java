package de.jplag.antlr.testLanguage;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * Artificial ANTLR-based language for testing.
 */
public class TestLanguage implements Language {

    @Override
    public List<String> fileExtensions() {
        return List.of("expression");
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getIdentifier() {
        return "test";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new TestParserAdapter().parse(files);
    }
}
