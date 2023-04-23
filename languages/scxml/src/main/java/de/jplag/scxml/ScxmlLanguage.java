package de.jplag.scxml;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.scxml.parser.ScxmlParserAdapter;

/**
 * Language for statecharts in the State Chart XML (SCXML) format.
 */
@MetaInfServices(de.jplag.Language.class)
public class ScxmlLanguage implements de.jplag.Language {

    /**
     * The file ending of SCXML statechart files.
     */
    public static final String FILE_ENDING = ".scxml";

    /**
     * The file ending of view files.
     */
    public static final String VIEW_FILE_SUFFIX = ".scxmlview";

    private static final String NAME = "SCXML (Statechart XML)";
    private static final String IDENTIFIER = "scxml";
    private static final int DEFAULT_MIN_TOKEN_MATCH = 6;

    /**
     * The parser adapter to use for parsing and extracting tokens from statecharts.
     */
    protected final ScxmlParserAdapter parser;

    /**
     * Constructs a new language instance for SCXML statecharts.
     */
    public ScxmlLanguage() {
        this.parser = new ScxmlParserAdapter();
    }

    @Override
    public String[] suffixes() {
        return new String[] {FILE_ENDING};
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public int minimumTokenMatch() {
        return DEFAULT_MIN_TOKEN_MATCH;
    }

    @Override
    public List<Token> parse(Set<File> files) throws ParsingException {
        return parser.parse(files);
    }

    @Override
    public boolean useViewFiles() {
        return true;
    }

    @Override
    public String viewFileSuffix() {
        return VIEW_FILE_SUFFIX;
    }
}
