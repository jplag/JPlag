package de.jplag.python3;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

@MetaInfServices(Language.class)
public class PythonLanguage implements Language {

    @Override
    public List<String> fileExtensions() {
        return List.of(".py");
    }

    @Override
    public String getName() {
        return "Python";
    }

    @Override
    public String getIdentifier() {
        return "python3";
    }

    @Override
    public int minimumTokenMatch() {
        return 12;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new PythonParserAdapter().parse(files);
    }
}
