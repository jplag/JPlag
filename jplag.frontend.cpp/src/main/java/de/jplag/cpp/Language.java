package de.jplag.cpp;

import java.io.File;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

public class Language implements de.jplag.Language {
    private Scanner scanner; // cpp code is scanned not parsed

    public Language(ErrorConsumer program) {
        this.scanner = new Scanner();
        this.scanner.setProgram(program);
    }

    @Override
    public int errorCount() {
        return this.scanner.errorsCount();
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
