package de.jplag.java;

import java.io.File;

import de.jplag.AbstractParser;
import de.jplag.TokenList;

public class Parser extends AbstractParser {
    private TokenList tokenList;

    public TokenList parse(File dir, String files[]) {
        tokenList = new TokenList();
        errors = 0;
        File pathedFiles[] = new File[files.length];
        for (int i = 0; i < files.length; i++) {
            pathedFiles[i] = new File(dir, files[i]);
        }
        JavacAdapter javac = new JavacAdapter();
        errors += javac.parseFiles(dir, pathedFiles, this);
        this.parseEnd();
        return tokenList;
    }

    public void add(int type, String filename, long line, long col, long length) {
        tokenList.addToken(new JavaToken(type, filename, (int) line, (int) col, (int) length));
    }

    public void errorsInc() {
        errors++;
    }
}
