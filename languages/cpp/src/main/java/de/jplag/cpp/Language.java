package de.jplag.cpp;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Token;

@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {
    public static final String IDENTIFIER = "cpp";

    private final Scanner scanner; // cpp code is scanned not parsed

    public Language() {
        scanner = new Scanner();
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
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public int minimumTokenMatch() {
        return 12;
    }

    @Override
    public List<Token> parse(Set<File> files) {
        return this.scanner.scan(files);
    }

    @Override
    public boolean hasErrors() {
        return this.scanner.hasErrors();
    }
}
