package de.jplag.scheme;

import java.io.File;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

public class Language implements de.jplag.Language {

    public Language(ErrorConsumer program) {
        this.parser = new Parser();
        this.parser.setProgram(program);

    }

    @Override
    public int errorCount() {
        return this.parser.errorsCount();
    }

    private de.jplag.scheme.Parser parser; // Not yet instantiated? See constructor!

    @Override
    public String[] suffixes() {
        return new String[] {".scm", ".SCM", ".ss", ".SS"};
    }

    @Override
    public String getName() {
        return "SchemeR4RS Parser [basic markup]";
    }

    @Override
    public String getShortName() {
        return "scheme";
    }

    @Override
    public int minimumTokenMatch() {
        return 13;
    }

    @Override
    public boolean supportsColumns() {
        return false;
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
    public TokenList parse(File dir, String[] files) {
        return this.parser.parse(dir, files);
    }

    @Override
    public boolean hasErrors() {
        return this.parser.hasErrors();
    }

    @Override
    public int numberOfTokens() {
        return SchemeTokenConstants.NUM_DIFF_TOKENS;
    }
}
