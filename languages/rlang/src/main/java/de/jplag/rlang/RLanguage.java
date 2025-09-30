package de.jplag.rlang;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

import com.google.auto.service.AutoService;

/**
 * This represents the R language as a language supported by JPlag.
 */
@AutoService(Language.class)
public class RLanguage implements Language {

    @Override
    public List<String> fileExtensions() {
        return List.of(".R");
    }

    @Override
    public String getName() {
        return "R";
    }

    @Override
    public String getIdentifier() {
        return "rlang";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new RParserAdapter().parse(files);
    }
}
