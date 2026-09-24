package de.jplag.csharp;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

import com.google.auto.service.AutoService;

/**
 * C# language with full support of C# 6 features and below.
 */
@AutoService(Language.class)
public class CSharpLanguage implements Language {

    @Override
    public List<String> fileExtensions() {
        return List.of(".cs");
    }

    @Override
    public String getName() {
        return "C#";
    }

    @Override
    public String getIdentifier() {
        return "csharp";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new CSharpParserAdapter().parse(files);
    }
}
