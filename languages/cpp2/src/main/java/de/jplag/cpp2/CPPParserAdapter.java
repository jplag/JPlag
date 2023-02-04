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

public class CPPParserAdapter extends AbstractParser {
    static final int USE_PREVIOUS_COLUMN = -1;
    private File currentFile;

    private List<Token> tokens;

    public List<Token> scan(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        for (File file : files) {
            this.currentFile = file;
            logger.trace("Parsing file {}", currentFile);
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

    /**
     * Add a token with the given type at the given position (column and line) with the given length.
     * @param type the type of the token.
     * @param column the column where the token starts, or {@value #USE_PREVIOUS_COLUMN} if the column should be taken from the previously extracted
     * token.
     * @param line the line where the token starts.
     * @param length the length of the token.
     */
    public void addToken(TokenType type, int column, int line, int length) {
        if (column == USE_PREVIOUS_COLUMN) {
            //
            column = tokens.isEmpty() ? 0 : tokens.get(tokens.size() - 1).getColumn() + 1;
        }
        tokens.add(new Token(type, currentFile, line, column, length));
    }

}
