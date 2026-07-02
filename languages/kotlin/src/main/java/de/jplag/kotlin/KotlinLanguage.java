package de.jplag.kotlin;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

import com.google.auto.service.AutoService;
import de.jplag.inputs.SubmissionFolder;

/**
 * This represents the Kotlin language as a language supported by JPlag.
 */
@AutoService(Language.class)
public class KotlinLanguage implements Language {

    @Override
    public List<String> fileExtensions() {
        return List.of(".kt");
    }

    @Override
    public String getName() {
        return "Kotlin";
    }

    @Override
    public String getIdentifier() {
        return "kotlin";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }

    @Override
    public List<Token> parse(SubmissionFolder folder, boolean normalize) throws ParsingException {
        return new KotlinParserAdapter().parse(folder);
    }
}
