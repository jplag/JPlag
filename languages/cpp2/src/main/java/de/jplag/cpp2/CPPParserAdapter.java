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

/**
 * The adapter between {@link AbstractParser} and the ANTLR based parser of this language module.
 */
public class CPPParserAdapter extends AbstractParser {
    private File currentFile;

    private List<Token> tokens;

    /**
     * {@return a list of tokens from a set of source files}
     * @param files the source files
     * @throws ParsingException if parsing fails.
     */
    public List<Token> scan(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        for (File file : files) {
            this.currentFile = file;
            logger.trace("Parsing file {}", currentFile);
            try {
                CPP14Lexer lexer = new CPP14Lexer(CharStreams.fromStream(Files.newInputStream(file.toPath())));
                // create a buffer of tokens pulled from the lexer
                CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                CPP14Parser parser = new CPP14Parser(tokenStream);
                CPP14Parser.TranslationUnitContext translationUnit = parser.translationUnit();

                ParseTreeWalker.DEFAULT.walk(new CPPTokenListener(this), translationUnit);
            } catch (IOException e) {
                throw new ParsingException(file, e);
            }
            tokens.add(Token.fileEnd(currentFile));
        }
        return tokens;
    }

    /**
     * Add a token with the given type at the given position (column and line) with the given length.
     * @param type the type of the token.
     * @param column the column where the token starts.
     * @param line the line where the token starts.
     * @param length the length of the token.
     */
    public void addToken(TokenType type, int column, int line, int length) {
        tokens.add(new Token(type, currentFile, line, column, length));
    }

}
