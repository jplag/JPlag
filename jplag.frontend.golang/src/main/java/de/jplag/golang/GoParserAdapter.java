package de.jplag.golang;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.jplag.AbstractParser;
import de.jplag.ErrorConsumer;
import de.jplag.TokenList;
import de.jplag.golang.grammar.GoLexer;
import de.jplag.golang.grammar.GoParser;

import static de.jplag.TokenConstants.FILE_END;
import static de.jplag.golang.grammar.GoTokenUtils.getDummyToken;

public class GoParserAdapter extends AbstractParser {
    private String currentFile;
    private TokenList tokens;

    public GoParserAdapter(ErrorConsumer consumer) {
        super(consumer);
    }

    public TokenList parse(File directory, String[] fileNames) {
        tokens = new TokenList();
        for (String file : fileNames) {
            if (!parseFile(directory, file)) {
                errors++;
            }
            tokens.addToken(getDummyToken(FILE_END, file));
        }
        return tokens;
    }

    private boolean parseFile(File directory, String fileName) {
        File file = new File(directory, fileName);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            currentFile = fileName;

            GoLexer lexer = new GoLexer(CharStreams.fromStream(inputStream));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            GoParser parser = new GoParser(tokens);

            ParserRuleContext entryContext = parser.sourceFile();
            ParseTreeWalker treeWalker = new ParseTreeWalker();

            GoListener listener = new GoListener(this);
            for (int i = 0; i < entryContext.getChildCount(); i++) {
                ParseTree parseTree = entryContext.getChild(i);
                treeWalker.walk(listener, parseTree);
            }
        } catch (IOException exception) {
            getErrorConsumer().addError("Parsing Error in '%s': %s%s".formatted(fileName, File.separator, exception));
            return false;
        }
        return true;
    }

    public void addToken(int tokenType, int line, int column, int length) {
        tokens.addToken(new GoToken(tokenType, currentFile, line, column, length));
    }
}
