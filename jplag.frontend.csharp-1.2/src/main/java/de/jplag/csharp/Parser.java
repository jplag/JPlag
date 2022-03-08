package de.jplag.csharp;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import antlr.Token;

import de.jplag.AbstractParser;
import de.jplag.TokenList;
import de.jplag.csharp.grammar.CSharpLexer;
import de.jplag.csharp.grammar.CSharpParser;

public class Parser extends AbstractParser implements CSharpTokenConstants {
    private TokenList struct;
    private String currentFile;

    public TokenList parse(File dir, String files[]) {
        struct = new TokenList();
        errors = 0;
        for (int i = 0; i < files.length; i++) {
            if (!parseFile(dir, files[i]))
                errors++;
            struct.addToken(new CSharpToken(FILE_END, files[i], -1, -1, -1));
        }
        this.parseEnd();
        return struct;
    }

    private boolean parseFile(File dir, String file) {
        try {
            FileInputStream fis = new FileInputStream(new File(dir, file));
            currentFile = file;
            // Create a scanner that reads from the input stream passed to us
            CSharpLexer lexer = new CSharpLexer(new UnicodeReader(fis, StandardCharsets.UTF_8));
            lexer.setFilename(file);
            lexer.setTabSize(1);

            // Create a parser that reads from the scanner
            CSharpParser parser = new CSharpParser(lexer);
            parser.setFilename(file);
            parser.parser = this;// Added by emeric 22.01.05
            // start parsing at the compilationUnit rule
            parser.compilation_unit();

            // close file
            fis.close();
        } catch (Exception e) {
            getErrorConsumer().addError("  Parsing Error in '" + file + "':\n  " + e.toString());
            return false;
        }
        return true;
    }

    private void add(int type, Token tok) {
        if (tok == null) {
            System.out.println("tok == null  ERROR!");
            return;
        }
        struct.addToken(new CSharpToken(type, currentFile, tok.getLine(), tok.getColumn(), tok.getText().length()));
    }

    public void add(int type, CSharpParser p) {
        add(type, p.getLastConsumedToken());
    }
}
