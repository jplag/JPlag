package de.jplag.kotlin;

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
import de.jplag.kotlin.grammar.KotlinLexer;
import de.jplag.kotlin.grammar.KotlinParser;

public class KotlinParserAdapter extends AbstractParser {

    private String currentFile;
    private TokenList tokens;

    public KotlinParserAdapter(ErrorConsumer consumer) {
        super(consumer);
    }

    public TokenList parse(File directory, String[] fileNames) {
        tokens = new TokenList();
        for (String file : fileNames) {
            if (!parseFile(directory, file)) {
                errors++;
            }
            tokens.addToken(new KotlinToken(KotlinTokenConstants.FILE_END, file, -1, -1, -1));
        }
        return tokens;
    }

    private boolean parseFile(File directory, String fileName) {
        File file = new File(directory, fileName);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            currentFile = fileName;

            KotlinLexer lexer = new KotlinLexer(CharStreams.fromStream(inputStream));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            KotlinParser parser = new KotlinParser(tokens);

            ParserRuleContext entryContext = parser.kotlinFile();
            ParseTreeWalker treeWalker = new ParseTreeWalker();

            KotlinListener listener = new KotlinListener(this);
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
        tokens.addToken(new KotlinToken(tokenType, currentFile, line, column, length));
    }
}
