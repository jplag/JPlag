package de.jplag.swift;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * This represents the Swift language as a language supported by JPlag.
 */
@MetaInfServices(Language.class)
public class SwiftLanguage implements Language {

    @Override
    public String[] suffixes() {
        return new String[] {".swift"};
    }

    @Override
    public String getName() {
        return "Swift";
    }

    @Override
    public String getIdentifier() {
        return "swift";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new SwiftParserAdapter().parse(files);
    }
}
