package de.jplag.text;

import de.jplag.AbstractParser;
import de.jplag.SharedTokenType;
import de.jplag.Token;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ParserAdapter extends AbstractParser {

    private static final char LF = '\n';
    private static final char CR = '\r';
    private static final String ANNOTATORS_KEY = "annotators";
    private static final String ANNOTATORS_VALUE = "tokenize";
    private final Map<String, Integer> tokenTypes = new HashMap<>();
    private final StanfordCoreNLP pipeline;

    private List<Token> tokens;
    private String currentFile;
    private int currentLine;
    /**
     * The position of the current line break in the content string
     */
    private int currentLineBreakIndex;

    public ParserAdapter() {
        Properties properties = new Properties();
        properties.put(ANNOTATORS_KEY, ANNOTATORS_VALUE);
        this.pipeline = new StanfordCoreNLP(properties);
    }

    public List<Token> parse(File directory, String[] files) {
        tokens = new ArrayList<>();
        errors = 0;
        for (String file : files) {
            logger.trace("Parsing file {}", file);
            if (!parseFile(directory, file)) {
                errors++;
            }
            tokens.add(new TextToken(SharedTokenType.FILE_END, file));
        }
        return tokens;
    }

    private boolean parseFile(File directory, String file) {
        this.currentFile = file;
        this.currentLine = 1; // lines start at 1
        this.currentLineBreakIndex = 0;
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
     * Scan for line breaks and increase {@link #currentLine} and {@link #currentLineBreakIndex} accordingly.
     * @param content the file content
     * @param lastTokenEnd the end position of the last token
     * @param nextTokenBegin the begin position of the next token
     */
    private void advanceLineBreaks(String content, int lastTokenEnd, int nextTokenBegin) {
        for (int i = lastTokenEnd; i < nextTokenBegin; i++) {
            if (content.charAt(i) == LF) {
                currentLine++;
                currentLineBreakIndex = i;
            } else if (content.charAt(i) == CR) {
                if (i + 1 < content.length() && content.charAt(i + 1) == LF) { // CRLF
                    i++; // skip following LF
                }
                currentLine++;
                currentLineBreakIndex = i;
            }
        }
    }

    private boolean isWord(CoreLabel token) {
        // consider a token as a word if it contains any alphanumeric character
        String text = token.originalText();
        return text.chars().anyMatch(it -> Character.isAlphabetic(it) || Character.isDigit(it));
    }

    private void addToken(CoreLabel label) {
        String text = label.originalText();
        int column = label.beginPosition() - currentLineBreakIndex;
        int length = label.endPosition() - label.beginPosition();
        tokens.add(new TextToken(text, new TextTokenType(text), currentFile, currentLine, column, length));
    }

    private String readFile(Path filePath) {
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            logger.error("Error reading from file {}", filePath, e);
            return null;
        }
    }
}
