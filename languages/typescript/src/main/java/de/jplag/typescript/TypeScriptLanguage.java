package de.jplag.typescript;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

import java.io.File;
import java.util.List;
import java.util.Set;

public class TypeScriptLanguage implements Language {

    private static final String IDENTIFIER = "typescript";
    private final Parser parser;

    public TypeScriptLanguage() {
        this.parser = new Parser();
    }

    @Override
    public String[] suffixes() {
        return new String[]{".ts", ".js"};
    }

    @Override
    public String getName() {
        return "Typescript Parser";
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public int minimumTokenMatch() {
        return 9;
    }

    @Override
    public List<Token> parse(Set<File> files) throws ParsingException {
        return parser.parse(files);
    }
}
