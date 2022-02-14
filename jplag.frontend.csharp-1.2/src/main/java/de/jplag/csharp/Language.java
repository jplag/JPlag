package de.jplag.csharp;

import java.io.File;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

public class Language implements de.jplag.Language {
    private Parser parser;

    public Language(ErrorConsumer program) {
        this.parser = new Parser();
        this.parser.setProgram(program);

    }

    @Override
    public String[] suffixes() {
        return new String[] {".cs", ".CS"};
    }

    @Override
    public int errorCount() {
        return this.parser.errorsCount();
    }

    @Override
    public String getName() {
        return "C# 1.2 Parser";
    }

    @Override
    public String getShortName() {
        return "c#-1.2";
    }

    @Override
    public int minimumTokenMatch() {
        return 8;
    }

    @Override
    public TokenList parse(File dir, String[] files) {
        return this.parser.parse(dir, files);
    }

    @Override
    public boolean hasErrors() {
        return parser.hasErrors();
    }

    @Override
    public boolean supportsColumns() {
        return true;
    }

    @Override
    public boolean isPreformatted() {
        return true;
    }

    @Override
    public boolean usesIndex() {
        return false;
    }

    @Override
    public int numberOfTokens() {
        return CSharpTokenConstants.NUM_DIFF_TOKENS;
    }
}
