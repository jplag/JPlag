package de.jplag.text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
        currentFile = file;
        Path filePath = directory.toPath().resolve(file);
        ParsedDocument parsed = readDocument(filePath);
        if (parsed == null) {
            return false;
        }
        for (CoreLabel token : parsed.document().tokens()) {
            if (isWord(token)) {
                addToken(token, parsed.linebreaks());
            }
        }
        return true;
    }

    private boolean isWord(CoreLabel token) {
        if (true) {
            return true;
        }
        String text = token.originalText();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!Character.isAlphabetic(c) || Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private void addToken(CoreLabel label, List<Integer> linebreaks) {
        String text = label.originalText();
        int type = getTokenType(text);
        // assuming we never hit a line break with the beginPosition,
        // we can directly negate the value to get the line
        int line = -Collections.binarySearch(linebreaks, label.beginPosition());
        int column;
        // line 1 has no start position in the linebreaks list
        if (line == 1) {
            column = label.beginPosition();
        } else {
            // while token lines start at 1, the list is 0-indexed.
            // additionally, we need to skip line 1 here => - 2
            column = label.beginPosition() - linebreaks.get(line - 2);
        }
        int length = label.endPosition() - label.beginPosition();
        TokenPosition position = new TokenPosition(line, column, length);
        tokens.addToken(new TextToken(text, type, currentFile, position));
    }

    private ParsedDocument readDocument(Path filePath) {
        String content;
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            logger.error("Error reading from file {}", filePath, e);
            return null;
        }
        List<Integer> linebreaks = createLinebreaksLookup(content);
        return new ParsedDocument(pipeline.processToCoreDocument(content), linebreaks);
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

    // TODO we can probably get that data from CoreNLP?
    private List<Integer> createLinebreaksLookup(String content) {
        List<Integer> linebreaks = new ArrayList<>();
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '\n') {
                linebreaks.add(i);
            }
        }
        return linebreaks;
    }

    record ParsedDocument(CoreDocument document, List<Integer> linebreaks) {

    }
}
