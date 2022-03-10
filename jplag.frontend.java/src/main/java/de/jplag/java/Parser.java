package de.jplag.java;

import java.io.File;

import de.jplag.AbstractParser;
import de.jplag.TokenList;

public class Parser extends AbstractParser {
    private TokenList tokens;

    public TokenList parse(File directory, String files[]) {
        tokens = new TokenList();
        errors = 0;
        File pathedFiles[] = new File[files.length];
        for (int i = 0; i < files.length; i++) {
            pathedFiles[i] = new File(directory, files[i]);
        }
        JavacAdapter javac = new JavacAdapter();
        errors += javac.parseFiles(directory, pathedFiles, this);
        this.parseEnd();
        return tokens;
    }

    public void add(int type, String filename, long line, long col, long length) {
        tokens.addToken(new JavaToken(type, filename, (int) line, (int) col, (int) length));
    }

    public void errorsInc() {
        errors++;
    }
}
