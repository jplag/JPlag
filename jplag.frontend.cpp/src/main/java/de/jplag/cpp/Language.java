package de.jplag.cpp;

import java.io.File;

import org.kohsuke.MetaInfServices;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {
    private final Scanner scanner; // cpp code is scanned not parsed

    /**
     * Prototype Constructor for {@link MetaInfServices}.
     */
    public Language() {
        this.scanner = null;
    }

    private Language(ErrorConsumer errorConsumer) {
        scanner = new Scanner(errorConsumer);
    }

    @Override
    public de.jplag.Language createInitializedLanguage(ErrorConsumer errorConsumer) {
        return new Language(errorConsumer);
    }

    @Override
    public String[] suffixes() {
        return new String[] {".cpp", ".CPP", ".cxx", ".CXX", ".c++", ".C++", ".c", ".C", ".cc", ".CC", ".h", ".H", ".hpp", ".HPP", ".hh", ".HH"};
    }

    @Override
    public String getName() {
        return "C/C++ Scanner [basic markup]";
    }

    @Override
    public String getShortName() {
        return "cpp";
    }

    @Override
    public int minimumTokenMatch() {
        return 12;
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
        return this.scanner.scan(dir, files);
    }

    @Override
    public boolean hasErrors() {
        return this.scanner.hasErrors();
    }

    @Override
    public int numberOfTokens() {
        return CPPTokenConstants.NUM_DIFF_TOKENS;
    }
}
