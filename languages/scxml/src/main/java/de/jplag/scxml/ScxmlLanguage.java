package de.jplag.scxml;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.scxml.parser.ScxmlParserAdapter;

import com.google.auto.service.AutoService;

/**
 * Language for statecharts in the State Chart XML (SCXML) format.
 */
@AutoService(Language.class)
public class ScxmlLanguage implements Language {

    /** File extension for the view files. **/
    public static final String VIEW_FILE_EXTENSION = ".scxmlview";

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
    public String viewFileExtension() {
        return VIEW_FILE_EXTENSION;
    }
}
