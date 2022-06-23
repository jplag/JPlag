package de.jplag.scheme;

import java.io.File;

import org.kohsuke.MetaInfServices;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {
    private final de.jplag.scheme.Parser parser;

    /**
     * Prototype Constructor for {@link MetaInfServices}.
     */
    public Language() {
        this.parser = null;
    }

    private Language(ErrorConsumer program) {
        parser = new Parser(program);
    }

    @Override
    public de.jplag.Language initializeLanguage(ErrorConsumer errorConsumer) {
        return new Language(errorConsumer);
    }

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
