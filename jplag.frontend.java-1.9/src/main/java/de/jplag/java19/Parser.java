package de.jplag.java19;

import java.io.File;

public class Parser extends de.jplag.AbstractParser {
    private de.jplag.TokenList tokenList;

    public de.jplag.TokenList parse(File dir, String files[]) {
        tokenList = new de.jplag.TokenList();
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
