package de.jplag.text;

import java.io.File;

import de.jplag.TokenList;

/**
 * Language class for parsing (natural language) text. This language module employs a primitive approach where
 * individual words are interpreted as token types. Whitespace and special characters are ignored. This approach works,
 * but there are better approaches for text plagiarism out there (based on NLP techniques).
 */
public class Language implements de.jplag.Language {

    private final ParserAdapter parserAdapter;

    public Language() {
        parserAdapter = new ParserAdapter();
    }

    @Override
    public String[] suffixes() {
        return new String[] {".TXT", ".txt", ".ASC", ".asc", ".TEX", ".tex"};
    }

    @Override
    public String getName() {
        return "Text Parser (naive)";
    }

    @Override
    public String getShortName() {
        return "text";
    }

    @Override
    public int minimumTokenMatch() {
        return 5;
    }

    @Override
    public TokenList parse(File dir, String[] files) {
        return parserAdapter.parse(dir, files);
    }

    @Override
    public boolean hasErrors() {
        return parserAdapter.hasErrors();
    }

    @Override
    public boolean isPreformatted() {
        return false;
    }
}
