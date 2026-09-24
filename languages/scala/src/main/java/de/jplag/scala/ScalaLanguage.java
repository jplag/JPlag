package de.jplag.scala;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

import com.google.auto.service.AutoService;

/**
 * Scala language module.
 */
@AutoService(Language.class)
public class ScalaLanguage implements Language {
    @Override
    public List<String> fileExtensions() {
        return List.of(".scala", ".sc");
    }

    @Override
    public String getName() {
        return "Scala";
    }

    @Override
    public String getIdentifier() {
        return "scala";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new ScalaParser().parse(files);
    }
}
