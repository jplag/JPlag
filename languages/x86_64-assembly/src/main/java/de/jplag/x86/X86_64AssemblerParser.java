package de.jplag.x86;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import de.jplag.ParsingException;
import de.jplag.Token;

public class X86_64AssemblerParser {
    private static final String REGEX_SEGMENT_START = "SECTION \\..+";
    private static final String REGEX_LABEL_LINE = "[^:]+:";
    private static final List<String> operationSuffixes = List.of("b", "w", "l", "q");

    private final String[] fileLines;
    private final File file;
    private final List<Token> tokenCollector;

    public X86_64AssemblerParser(String[] fileLines, File file) {
        this.fileLines = fileLines;
        for (int i = 0; i < this.fileLines.length; i++) {
            if (fileLines[i].contains(";")) {
                fileLines[i] = fileLines[i].substring(0, fileLines[i].indexOf(';'));
            }
        }
        this.tokenCollector = new ArrayList<>();
        this.file = file;
    }

    public List<Token> parse() throws ParsingException {
        this.tokenCollector.clear();
        for (int i = 0; i < fileLines.length; i++) {
            parseLine(i);
        }
        this.tokenCollector.add(Token.fileEnd(file));
        return this.tokenCollector;
    }

    private void parseLine(int lineIndex) throws ParsingException {
        String line = fileLines[lineIndex];

        if (line.isBlank()) {
            return;
        }
        if (line.trim().startsWith("extern")) {
            addToken(X86_64AssemblerTokenType.EXTERN, lineIndex, 0, line.length());
        } else if (line.trim().matches(REGEX_SEGMENT_START)) {
            addToken(X86_64AssemblerTokenType.SECTION_MARKER, lineIndex, 0, line.length());
        } else if (line.trim().matches(REGEX_LABEL_LINE)) {
            addToken(X86_64AssemblerTokenType.LABEL, lineIndex, 0, line.length());
        } else {
            parseOperationLine(lineIndex);
        }
    }

    private void parseOperationLine(int lineIndex) throws ParsingException {
        String line = fileLines[lineIndex];

        int mainStart = 0;

        if (line.contains(":")) {
            Pair<Integer, StringSegment> labelPosition = findPart(line, 0, c -> c == ':');
            mainStart = labelPosition.left;
            addToken(X86_64AssemblerTokenType.LABEL, lineIndex, labelPosition.right.index,
                    labelPosition.right.index + labelPosition.right.value.length());
        }

        Pair<Integer, StringSegment> operationPosition = findPart(line, mainStart, Character::isWhitespace);
        addOperationToken(operationPosition.right, lineIndex);

        findRemainingParts(line, operationPosition.left, ',').forEach(operand -> addOperandToken(operand, lineIndex));
    }

    private void addOperationToken(StringSegment operation, int lineIndex) throws ParsingException {
        X86_64AssemblerTokenType type = Arrays.stream(X86_64AssemblerTokenType.values()).filter(it -> {
            return it.isOpCode() && (it.getDescription().equalsIgnoreCase(operation.value)
                    || operationSuffixes.stream().anyMatch(suffix -> (it.getDescription() + suffix).equals(operation.value)));
        }).findFirst().orElseThrow(() -> new ParsingException(this.file, "Could not find type for: " + operation.value));

        addToken(type, lineIndex, operation.index, operation.index + operation.value.length());
    }

    private void addOperandToken(StringSegment operand, int lineIndex) {
        String operandValue = operand.value;

        if (operandValue.startsWith("%")) {
            addToken(X86_64AssemblerTokenType.OPERAND_REGISTER, lineIndex, operand.index, operand.index + operand.value.length());
        } else if (operandValue.startsWith("$")) {
            addToken(X86_64AssemblerTokenType.OPERAND_VALUE, lineIndex, operand.index, operand.index + operand.value.length());
        } else {
            addToken(X86_64AssemblerTokenType.OPERAND_ADDRESS, lineIndex, operand.index, operand.index + operand.value.length());
        }
    }

    private void addToken(X86_64AssemblerTokenType tokenType, int lineIndex, int startIndex, int endIndex) {
        tokenCollector.add(new Token(tokenType, file, lineIndex + 1, startIndex + 1, lineIndex + 1, endIndex, endIndex - startIndex));
    }

    private List<StringSegment> findRemainingParts(String fullString, int startIndex, char splitChar) {
        List<StringSegment> result = new ArrayList<>();

        int currentStart = startIndex;
        boolean done = false;
        while (!done) {
            Pair<Integer, StringSegment> nextSegment = findPart(fullString, currentStart, (c) -> c == splitChar);
            if (nextSegment != null) {
                result.add(nextSegment.right);
                currentStart = nextSegment.left;
            } else {
                done = true;
            }
        }

        return result;
    }

    private Pair<Integer, StringSegment> findPart(String fullString, int startIndex, Predicate<Character> splitCondition) {
        char[] chars = fullString.toCharArray();

        int segmentStart = startIndex;
        int segmentEnd = -1;
        int currentIndex = startIndex;

        while (currentIndex < chars.length && Character.isWhitespace(chars[currentIndex])) {
            currentIndex++;
        }

        if (currentIndex >= chars.length) {
            return null;
        }
        segmentStart = currentIndex;
        segmentEnd = currentIndex;

        while (currentIndex < chars.length && !splitCondition.test(chars[currentIndex])) {
            currentIndex++;
            if (currentIndex < chars.length && !Character.isWhitespace(chars[currentIndex])) {
                segmentEnd = currentIndex;
            }
        }

        return new Pair<>(currentIndex + 1, new StringSegment(fullString.substring(segmentStart, segmentEnd + 1), segmentStart));
    }

    private static class StringSegment {
        private final String value;
        private final int index;

        public StringSegment(String value, int index) {
            this.value = value;
            this.index = index;
        }

        public String getValue() {
            return value;
        }

        public int getIndex() {
            return index;
        }
    }

    private static class Pair<L, R> {
        private final L left;
        private final R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public L getLeft() {
            return left;
        }

        public R getRight() {
            return right;
        }
    }
}
