package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

class TokenPrinterTest {
    @Test
    void testTokenShorterThanLabel() {
        final String in = "|----| \t\t\t\ttoken shorter than label, no bar printed";
        final String out = "└────┘ STRING";
        final Token token = makeSingleLineTestToken(1, 6);

        testInputCreatesOutput(List.of(in), List.of(out), List.of(token));
    }

    @Test
    void testLabelFitsExactly() {
        final String in = "|------|	token length matches label length";
        final String out = "└STRING┘";
        final Token token = makeSingleLineTestToken(1, 8);

        testInputCreatesOutput(List.of(in), List.of(out), List.of(token));
    }

    @Test
    void testLabelLongerThanToken() {
        final String in = "|--------|	token longer than label length";
        final String out = "└─STRING─┘";
        final Token token = makeSingleLineTestToken(1, 10);

        testInputCreatesOutput(List.of(in), List.of(out), List.of(token));
    }

    @Test
    void testTabIndent() {
        final String in = "\t\t|--------|	tab indented";
        final String out = "        └─STRING─┘";
        final Token token = makeSingleLineTestToken(3, 12);

        testInputCreatesOutput(List.of(in), List.of(out), List.of(token));
    }

    @Test
    void testTwoLabelsInOneLine() {
        final String in = "|--------|  |--------|   token longer than label length";
        final String out = "└─STRING─┘  └─STRING─┘";
        final List<Token> tokens = List.of(makeSingleLineTestToken(1, 10), makeSingleLineTestToken(13, 22));

        testInputCreatesOutput(List.of(in), List.of(out), tokens);
    }

    @Test
    void testMultilineToken() {
        final String in1 = "|--------";
        final String out1 = "└STRING─────";
        final String in2 = "-----------|";
        final String out2 = "─────STRING┘";
        final Token token = new Token(TestTokenType.STRING, null, 1, 1, 2, 12, 22);

        testInputCreatesOutput(List.of(in1, in2), List.of(out1, out2), List.of(token));
    }

    private void testInputCreatesOutput(List<String> inLines, List<String> outLines, List<Token> tokens) {
        TokenPrinter printer = new TokenPrinter(inLines, tokens);
        Assumptions.assumeTrue(inLines.size() == outLines.size());
        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < inLines.size(); i++) {
            resultBuilder.append(i + 1).append(" ").append(inLines.get(i).replaceAll("\t", " ".repeat(4))).append(System.lineSeparator());
            resultBuilder.append("  ").append(outLines.get(i)).append(System.lineSeparator());
        }
        assertEquals(resultBuilder.toString().trim(), printer.printTokens().trim());
    }

    private Token makeSingleLineTestToken(int from, int to) {
        return new Token(TestTokenType.STRING, null, 1, from, 1, to, to - from + 1);
    }

    private enum TestTokenType implements TokenType {
        /**
         * Represents a token of type STRING, used for testing purposes.
         */
        STRING("STRING");

        private final String description;

        @Override
        public String getDescription() {
            return description;
        }

        TestTokenType(String description) {
            this.description = description;
        }
    }
}
