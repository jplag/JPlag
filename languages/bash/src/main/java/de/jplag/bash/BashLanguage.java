package de.jplag.bash;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

import com.google.auto.service.AutoService;

/**
 * This represents the Bash language as a language supported by JPlag.
 */
@AutoService(Language.class)
public class BashLanguage implements Language {

    @Override
    public List<String> fileExtensions() {
        return List.of(".sh", ".bash");
    }

    @Override
    public String getName() {
        return "Bash";
    }

    @Override
    public String getIdentifier() {
        return "bash";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new BashParserAdapter().parse(files);
    }
}
