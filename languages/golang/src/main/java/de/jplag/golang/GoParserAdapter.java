package de.jplag.golang;

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
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.golang.grammar.GoLexer;
import de.jplag.golang.grammar.GoParser;

public class GoParserAdapter extends AbstractParser {
    private String currentFile;
    private List<Token> tokens;

    public List<Token> parse(Set<File> files) {
        tokens = new ArrayList<>();
        for (File file : files) {
            if (!parseFile(file)) {
                errors++;
            }
            tokens.add(Token.fileEnd(file.getName()));
        }
        return tokens;
    }

    private boolean parseFile(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            currentFile = file.getName();

            GoLexer lexer = new GoLexer(CharStreams.fromStream(inputStream));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            GoParser parser = new GoParser(tokenStream);

            ParserRuleContext entryContext = parser.sourceFile();
            ParseTreeWalker treeWalker = new ParseTreeWalker();

            JPlagGoListener listener = new JPlagGoListener(this);
            for (int i = 0; i < entryContext.getChildCount(); i++) {
                ParseTree parseTree = entryContext.getChild(i);
                treeWalker.walk(listener, parseTree);
            }
        } catch (IOException exception) {
            logger.error("Parsing Error in '%s':".formatted(file.getName()), exception);
            return false;
        }
        return true;
    }

    public void addToken(TokenType tokenType, int line, int column, int length) {
        tokens.add(new Token(tokenType, currentFile, line, column, length));
    }
}
