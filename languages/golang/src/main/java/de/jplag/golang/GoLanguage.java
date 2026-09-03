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
    public String getVersionFlagInformation() {
        return "Number literal prefixes, a feature of go1.13, are included." + System.lineSeparator()
                + "Generics, a feature of go1.18, are _not_ included." + System.lineSeparator()
                + "Between go1.13 and go1.18, there were no changes to the syntax. So, the grammar should be fully compatible with go1.17, released in mid-2021.";
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new GoParserAdapter().parse(files);
    }
}
