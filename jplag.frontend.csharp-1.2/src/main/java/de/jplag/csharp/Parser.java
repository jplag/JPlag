package de.jplag.csharp;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import antlr.Token;

import de.jplag.AbstractParser;
import de.jplag.TokenList;
import de.jplag.csharp.grammar.CSharpLexer;
import de.jplag.csharp.grammar.CSharpParser;

public class Parser extends AbstractParser {
    private TokenList tokens;
    private String currentFile;

    public TokenList parse(File directory, String files[]) {
        tokens = new TokenList();
        errors = 0;
        for (int i = 0; i < files.length; i++) {
            if (!parseFile(directory, files[i]))
                errors++;
            tokens.addToken(new CSharpToken(CSharpTokenConstants.FILE_END, files[i], -1, -1, -1));
        }
        this.parseEnd();
        return tokens;
    }

    private boolean parseFile(File dir, String file) {
        try {
            FileInputStream input = new FileInputStream(new File(dir, file));
            currentFile = file;
            // Create a scanner that reads from the input stream passed to us
            CSharpLexer lexer = new CSharpLexer(new UnicodeReader(input, StandardCharsets.UTF_8));
            lexer.setFilename(file);
            lexer.setTabSize(1);

            // Create a parser that reads from the scanner
            CSharpParser parser = new CSharpParser(lexer);
            parser.setFilename(file);
            parser.parser = this;// Added by emeric 22.01.05
            // start parsing at the compilationUnit rule
            parser.compilation_unit();

            // close file
            input.close();
        } catch (Exception e) {
            getErrorConsumer().addError("  Parsing Error in '" + file + "':\n  " + e.toString());
            return false;
        }
        return true;
    }

    private void add(int type, Token token) {
        if (token == null) {
            System.out.println("tok == null  ERROR!");
            return;
        }
        tokens.addToken(new CSharpToken(type, currentFile, token.getLine(), token.getColumn(), token.getText().length()));
    }

    public void add(int type, CSharpParser parser) {
        add(type, parser.getLastConsumedToken());
    }
}
