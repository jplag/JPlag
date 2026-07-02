package de.jplag.python3;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

import com.google.auto.service.AutoService;
import de.jplag.inputs.SubmissionFolder;

/**
 * Language facade for Python 3.
 */
@AutoService(Language.class)
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
    public List<Token> parse(SubmissionFolder folder, boolean normalize) throws ParsingException {
        return new PythonParserAdapter().parse(folder);
    }
}
