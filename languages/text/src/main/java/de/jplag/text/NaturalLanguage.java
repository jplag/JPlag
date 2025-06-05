package de.jplag.text;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * Language class for parsing (natural language) text. This language module employs a primitive approach where
 * individual words are interpreted as token types. Whitespace and special characters are ignored. This approach works,
 * but there are better approaches for text plagiarism out there (based on NLP techniques).
 */
@MetaInfServices(Language.class)
public class NaturalLanguage implements Language {

    @Override
    public String[] suffixes() {
        return new String[] {".txt", ".asc", ".tex", ".md", ".rtf", ".csv", ".wiki", ".json", ".yaml", ".yml", ".xml"};
    }

    @Override
    public String getName() {
        return "Text (naive)";
    }

    @Override
    public String getIdentifier() {
        return "text";
    }

    @Override
    public int minimumTokenMatch() {
        return 5;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new ParserAdapter().parse(files);
    }

    @Override
    public boolean supportsMultiLanguage() {
        return false;
    }
}
