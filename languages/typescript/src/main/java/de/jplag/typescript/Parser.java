package de.jplag.typescript;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.typescript.grammar.TypeScriptLexer;
import de.jplag.typescript.grammar.TypeScriptParser;
import de.jplag.util.FileUtils;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Parser extends AbstractParser {

    private List<Token> tokens;
    private File currentFile;

    public Parser() {
        super();
    }

    public List<Token> parse(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        for (File file : files) {
            logger.trace("Parsing file {}", file.getName());
            parseFile(file);
            tokens.add(Token.fileEnd(file));
        }
        return tokens;
    }

    private void parseFile(File file) throws ParsingException {
        try (BufferedReader reader = FileUtils.openFileReader(file)) {
            currentFile = file;

            // create a lexer that feeds off of input CharStream
            TypeScriptLexer lexer = new TypeScriptLexer(CharStreams.fromReader(reader));

            // create a buffer of tokens pulled from the lexer
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // create a parser that feeds off the tokens buffer
            TypeScriptParser parser = new TypeScriptParser(tokens);
            ParserRuleContext in = parser.initializer();

            ParseTreeWalker ptw = new ParseTreeWalker();
            for (int i = 0; i < in.getChildCount(); i++) {
                ParseTree pt = in.getChild(i);
                ptw.walk(new JPlagTypeScriptListener(this), pt);
            }

        } catch (IOException e) {
            throw new ParsingException(file, e.getMessage(), e);
        }
    }

    public void add(TokenType type, org.antlr.v4.runtime.Token token) {
        tokens.add(new Token(type, currentFile, token.getLine(), token.getCharPositionInLine() + 1, token.getText().length()));
    }

    public void addEnd(TokenType type, org.antlr.v4.runtime.Token token) {
        tokens.add(new Token(type, currentFile, token.getLine(), tokens.get(tokens.size() - 1).getColumn() + 1, 0));
    }

}
