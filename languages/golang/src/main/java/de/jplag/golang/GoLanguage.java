package de.jplag.golang;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

import com.google.auto.service.AutoService;

/**
 * Facade for the ANTLR-based Go language module.
 */
@AutoService(Language.class)
public class GoLanguage implements Language {

    @Override
    public List<String> fileExtensions() {
        return List.of(".go");
    }

    @Override
    public String getName() {
        return "Go";
    }

    @Override
    public String getIdentifier() {
        return "go";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new GoParserAdapter().parse(files);
    }
}
