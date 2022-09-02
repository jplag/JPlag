package de.jplag.text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import de.jplag.AbstractParser;
import de.jplag.TokenConstants;
import de.jplag.TokenList;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class ParserAdapter extends AbstractParser {

    private final Map<String, Integer> tokenTypes = new HashMap<>();
    private final StanfordCoreNLP pipeline;
    private int tokenTypeIndex = 1; // 0 is FILE_END token, SEPARATOR is not used as there are no methods.

    private TokenList tokens;
    private String currentFile;
    private int currentLine;
    /**
     * The position of the current line break in the content string
     */
    private int currentLineIndex;

    public ParserAdapter() {
        Properties properties = new Properties();
        properties.put("annotators", "tokenize");
        this.pipeline = new StanfordCoreNLP(properties);
    }

    public TokenList parse(File directory, String[] files) {
        tokens = new TokenList();
        errors = 0;
        for (String file : files) {
            logger.trace("Parsing file {}", file);
            if (!parseFile(directory, file)) {
                errors++;
            }
            tokens.addToken(new TextToken(TokenConstants.FILE_END, file));
        }
        return tokens;
    }

    private boolean parseFile(File directory, String file) {
        this.currentFile = file;
        this.currentLine = 1; // lines start at 1
        this.currentLineIndex = 0;
        Path filePath = directory.toPath().resolve(file);
        String content = readFile(filePath);
        if (content == null) {
            return false;
        }
        int lastTokenEnd = 0;
        CoreDocument coreDocument = pipeline.processToCoreDocument(content);
        for (CoreLabel token : coreDocument.tokens()) {
            advanceLineBreaks(content, lastTokenEnd, token.beginPosition());
            lastTokenEnd = token.endPosition();
            if (isWord(token)) {
                addToken(token);
            }
        }
        return true;
    }

    /**
     * Scan for line breaks and increase {@link #currentLine} and {@link #currentLineIndex} accordingly.
     * @param content the file content
     * @param lastTokenEnd the end position of the last token
     * @param nextTokenBegin the begin position of the next token
     */
    private void advanceLineBreaks(String content, int lastTokenEnd, int nextTokenBegin) {
        for (int i = lastTokenEnd; i < nextTokenBegin; i++) {
            if (content.charAt(i) == '\n') { // LF
                currentLine++;
                currentLineIndex = i;
            } else if (content.charAt(i) == '\r') { // CR
                if (i + 1 < content.length() && content.charAt(i + 1) == '\n') { // CRLF
                    i++; // skip following LF
                }
                currentLine++;
                currentLineIndex = i;
            }
        }
    }

    private boolean isWord(CoreLabel token) {
        // consider a token as a word if it contains any alphanumeric character
        String text = token.originalText();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isAlphabetic(c) || Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private void addToken(CoreLabel label) {
        String text = label.originalText();
        int type = getTokenType(text);
        int column = label.beginPosition() - currentLineIndex;
        int length = label.endPosition() - label.beginPosition();
        tokens.addToken(new TextToken(text, type, currentFile, currentLine, column, length));
    }

    private String readFile(Path filePath) {
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            logger.error("Error reading from file {}", filePath, e);
            return null;
        }
    }

    private int getTokenType(String text) {
        text = text.toLowerCase();
        tokenTypes.computeIfAbsent(text, it -> {
            if (tokenTypeIndex == Integer.MAX_VALUE) {
                throw new IllegalStateException("Too many token types, should not happen!");
            }
            return ++tokenTypeIndex;
        });
        return tokenTypes.get(text);
    }
}
