package de.jplag.cpp;

import java.io.File;

import de.jplag.AbstractParser;
import de.jplag.TokenList;

public class Scanner extends AbstractParser {
    private String currentFile;

    private TokenList tokens;

    public TokenList scan(File directory, String files[]) {
        tokens = new TokenList();
        errors = 0;
        CPPScanner scanner = null;// will be initialized in Method scanFile
        for (int i = 0; i < files.length; i++) {
            currentFile = files[i];
            getErrorConsumer().print(null, "Scanning file " + files[i]);
            if (!CPPScanner.scanFile(directory, files[i], scanner, this)) {
                errors++;
            }
            tokens.addToken(new CPPToken(CPPTokenConstants.FILE_END, currentFile));
        }
        this.parseEnd();
        return tokens;
    }

    public void add(int type, Token token) {
        int length = token.endColumn - token.beginColumn + 1;
        tokens.addToken(new CPPToken(type, currentFile, token.beginLine, token.beginColumn, length));
    }
}
