package de.jplag.text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.util.FileUtils;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * Parser adapter for natural-language text. Delegates to the Stanford CoreNLP API.
 */
public class ParserAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ParserAdapter.class);

    private static final char LF = '\n';
    private static final char CR = '\r';
    private static final String ANNOTATORS_KEY = "annotators";
    private static final String ANNOTATORS_VALUE = "tokenize";
    private final StanfordCoreNLP pipeline;

    private List<Token> tokens;
    private File currentFile;
    private int currentLine;
    /**
     * The position of the current line break in the content string.
     */
    private int currentLineBreakIndex;

    /**
     * Created the parser adapter.
     */
    public ParserAdapter() {
        Properties properties = new Properties();
        properties.put(ANNOTATORS_KEY, ANNOTATORS_VALUE);
        this.pipeline = new StanfordCoreNLP(properties);
    }

    /**
     * Parses a set of files.
     * @param files are the files to parse.
     * @return the token sequence.
     * @throws ParsingException is parsing fails.
     */
    public List<Token> parse(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        for (File file : files) {
            logger.trace("Parsing file {}", file);
            parseFile(file);
            tokens.add(Token.fileEnd(file));
        }
        return tokens;
    }

    private void parseFile(File file) throws ParsingException {
        this.currentFile = file;
        this.currentLine = 1; // lines start at 1
        this.currentLineBreakIndex = -1;
        String content = readFile(file);
        int lastTokenEnd = 0;
        CoreDocument coreDocument = pipeline.processToCoreDocument(content);
        for (CoreLabel token : coreDocument.tokens()) {
            advanceLineBreaks(content, lastTokenEnd, token.beginPosition());
            lastTokenEnd = token.endPosition();
            if (isWord(token)) {
                addToken(token);
            }
        }
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
        int startColumn = label.beginPosition() - currentLineBreakIndex;
        int endColumn = label.endPosition() - currentLineBreakIndex;
        int length = label.endPosition() - label.beginPosition();
        // As a token can not stretch multiple lines, the startLine is equal to the end line
        tokens.add(new Token(new TextTokenType(text), currentFile, currentLine, startColumn, currentLine, endColumn, length));
    }

    private String readFile(File file) throws ParsingException {
        try {
            return FileUtils.readFileContent(file, true);
        } catch (IOException e) {
            throw new ParsingException(file, e.getMessage(), e);
        }
    }
}
