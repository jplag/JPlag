package de.jplag.kotlin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.kotlin.grammar.KotlinLexer;
import de.jplag.kotlin.grammar.KotlinParser;

public class KotlinParserAdapter extends AbstractParser {
    private File currentFile;
    private List<Token> tokens;

    /**
     * Creates the KotlinParserAdapter
     */
    public KotlinParserAdapter() {
        super();
    }

    /**
     * Parsers a set of files into a single list of {@link Token}s.
     * @param files the set of files.
     * @return a list containing all tokens of all files.
     */
    public List<Token> parse(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        for (File file : files) {
            parseFile(file);
            tokens.add(Token.fileEnd(file));
        }
        return tokens;
    }

    private void parseFile(File file) throws ParsingException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            currentFile = file;

            KotlinLexer lexer = new KotlinLexer(CharStreams.fromStream(inputStream));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            KotlinParser parser = new KotlinParser(tokenStream);

            ParserRuleContext entryContext = parser.kotlinFile();
            ParseTreeWalker treeWalker = new ParseTreeWalker();

            JPlagKotlinListener listener = new JPlagKotlinListener(this);
            for (int i = 0; i < entryContext.getChildCount(); i++) {
                ParseTree parseTree = entryContext.getChild(i);
                treeWalker.walk(listener, parseTree);
            }
        } catch (IOException exception) {
            throw new ParsingException(file, exception.getMessage(), exception);
        }
    }

    /**
     * Adds a new {@link Token} to the current token list.
     * @param tokenType the type of the new {@link Token}
     * @param line the line of the Token in the current file
     * @param column the start column of the Token in the line
     * @param length the length of the Token
     */
    /* package-private */ void addToken(KotlinTokenType tokenType, int line, int column, int length) {
        tokens.add(new Token(tokenType, currentFile, line, column, length));
    }
}
