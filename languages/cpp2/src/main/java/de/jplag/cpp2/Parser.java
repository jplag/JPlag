package de.jplag.cpp2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.cpp2.grammar.CPP14Lexer;
import de.jplag.cpp2.grammar.CPP14Parser;

public class Parser extends AbstractParser {
    private File currentFile;

    private List<Token> tokens;

    /**
     * Creates the parser.
     */
    public Parser() {
        super();
    }

    public List<Token> scan(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        for (File file : files) {
            this.currentFile = file;
            logger.trace("Scanning file {}", currentFile);
            try {
                CPP14Lexer lexer = new CPP14Lexer(CharStreams.fromStream(Files.newInputStream(file.toPath())));
                // create a buffer of tokens pulled from the lexer
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                CPP14Parser parser = new CPP14Parser(tokens);
                CPP14Parser.TranslationUnitContext translationUnit = parser.translationUnit();

                ParseTreeWalker.DEFAULT.walk(new CPPTokenListener(this), translationUnit);
            } catch (IOException e) {
                throw new ParsingException(file, "", e);
            }
            tokens.add(Token.fileEnd(currentFile));
        }
        return tokens;
    }

    public void addEnter(TokenType type, org.antlr.v4.runtime.Token token) {
        int column = token.getCharPositionInLine() + 1;
        int length = token.getText().length();
        tokens.add(new Token(type, currentFile, token.getLine(), column, length));
    }

    public void addExit(TokenType type, org.antlr.v4.runtime.Token token) {
        int column = tokens.get(tokens.size() - 1).getColumn() + 1;
        int length = 0;
        tokens.add(new Token(type, currentFile, token.getLine(), column, length));
    }

}
