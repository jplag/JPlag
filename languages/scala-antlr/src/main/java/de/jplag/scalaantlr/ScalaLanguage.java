package de.jplag.scalaantlr;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

import com.google.auto.service.AutoService;

/**
 * The scala language
 */
@AutoService(Language.class)
public class ScalaLanguage implements Language {
    @Override
    public List<String> fileExtensions() {
        return List.of("scala", "sc");
    }

    @Override
    public String getName() {
        return "scala2";
    }

    @Override
    public String getIdentifier() {
        return "ScalaAntlr";
    }

    @Override
    public int minimumTokenMatch() {
        return 7;
    }

    @Override
    public String getVersionFlagInformation() {
        return "Scala 3";
    }

    @Override
    public List<Token> parse(Set<Path> files, boolean normalize) throws ParsingException {
        return new ScalaParserAdapter().parse(files);
    }
}
