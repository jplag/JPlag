package de.jplag.scxml;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.scxml.parser.ScxmlParserAdapter;

/**
 * Language for statecharts in the State Chart XML (SCXML) format.
 */
@MetaInfServices(Language.class)
public class ScxmlLanguage implements Language {

    public static final String VIEW_FILE_SUFFIX = ".scxmlview";

    @Override
    public List<String> fileExtensions() {
        return List.of(".scxml");
    }

    @Override
    public String getName() {
        return "SCXML";
    }

    @Override
    public String getIdentifier() {
        return "scxml";
    }

    @Override
    public int minimumTokenMatch() {
        return 6;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new ScxmlParserAdapter().parse(files);
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
