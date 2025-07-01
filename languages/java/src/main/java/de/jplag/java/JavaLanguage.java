package de.jplag.java;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * Language for Java 9 and newer.
 */
@MetaInfServices(Language.class)
public class JavaLanguage implements Language {

    @Override
    public List<String> fileExtensions() {
        return List.of(".java");
    }

    @Override
    public String getName() {
        return "Java";
    }

    @Override
    public String getIdentifier() {
        return "java";
    }

    @Override
    public int minimumTokenMatch() {
        return 9;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new Parser().parse(files);
    }

    @Override
    public boolean tokensHaveSemantics() {
        return true;
    }

    @Override
    public boolean supportsNormalization() {
        return true;
    }

    @Override
    public String toString() {
        return this.getIdentifier();
    }
}
