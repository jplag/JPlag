package de.jplag.x86;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.util.FileUtils;

public class X86_64AssemblerLanguage implements Language {
    @Override
    public List<String> fileExtensions() {
        return List.of();
    }

    @Override
    public String getName() {
        return "X86_64";
    }

    @Override
    public String getIdentifier() {
        return "x86";
    }

    @Override
    public int minimumTokenMatch() {
        return 7;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        List<Token> allTokens = new ArrayList<>();
        for (File file : files) {
            try {
                X86_64AssemblerParser parser = new X86_64AssemblerParser(FileUtils.readFileContent(file).split(System.lineSeparator()), file);
                allTokens.addAll(parser.parse());
            } catch (IOException e) {
                throw new ParsingException(file, e);
            }
        }
        return allTokens;
    }
}
