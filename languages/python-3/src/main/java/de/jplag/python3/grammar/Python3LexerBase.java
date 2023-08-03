package de.jplag.python3.grammar;

import java.util.*;

import org.antlr.v4.runtime.*;

abstract class Python3LexerBase extends Lexer {
    private LinkedList<Token> tokens = new LinkedList<>();
    private Deque<Integer> indents = new LinkedList<>();
    private int opened = 0;
    private Token lastToken = null;

    protected Python3LexerBase(CharStream input) {
        super(input);
    }

    @Override
    public void emit(Token t) {
        super.setToken(t);
        tokens.offer(t);
    }

    @Override
    public Token nextToken() {
        if (_input.LA(1) == EOF && !this.indents.isEmpty()) {
            this.emit(commonToken(Python3Lexer.NEWLINE, "\n"));
            this.removeTrailingEofTokens();

            while (!indents.isEmpty()) {
                this.emit(createDedent());
                indents.pop();
            }

            this.emit(commonToken(EOF, "<EOF>"));
        }

        Token next = super.nextToken();

        if (next.getChannel() == Token.DEFAULT_CHANNEL) {
            this.lastToken = next;
        }

        return tokens.isEmpty() ? next : tokens.poll();
    }

    private void removeTrailingEofTokens() {
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if (tokens.get(i).getType() == EOF) {
                tokens.remove(i);
            }
        }
    }

    private Token createDedent() {
        CommonToken dedent = commonToken(Python3Lexer.DEDENT, "");
        dedent.setLine(this.lastToken.getLine());
        return dedent;
    }

    private CommonToken commonToken(int type, String text) {
        int stop = this.getCharIndex() - 1;
        int start = text.isEmpty() ? stop : stop - text.length() + 1;
        return new CommonToken(this._tokenFactorySourcePair, type, DEFAULT_TOKEN_CHANNEL, start, stop);
    }

    /**
     * Calculates the indentation of the provided spaces, taking the following rules into account:
     * <p>
     * "Tabs are replaced (from left to right) by one to eight spaces such that the total number of characters up to and
     * including the replacement is a multiple of eight [...]"
     * <p>
     * -- https://docs.python.org/3.1/reference/lexical_analysis.html#indentation
     **/
    static int getIndentationCount(String spaces) {
        int count = 0;
        for (char ch : spaces.toCharArray()) {
            if (ch == '\t') {
                count += 8 - (count % 8);
            } else {
                count++;
            }
        }

        return count;
    }

    boolean atStartOfInput() {
        return super.getCharPositionInLine() == 0 && super.getLine() == 1;
    }

    void openBrace() {
        this.opened++;
    }

    void closeBrace() {
        this.opened--;
    }

    void onNewLine() {
        String newLine = getText().replaceAll("[^\r\n\f]+", "");
        String spaces = getText().replaceAll("[\r\n\f]+", "");

        int next = _input.LA(1);
        int nextnext = _input.LA(2);
        if (opened > 0 || (nextnext != -1 && (next == '\r' || next == '\n' || next == '\f' || next == '#'))) {
            skip();
        } else {
            emit(commonToken(Python3Lexer.NEWLINE, newLine));
            int indent = getIndentationCount(spaces);
            int previous = indents.isEmpty() ? 0 : indents.peek();

            if (indent == previous) {
                skip();
            } else if (indent > previous) {
                indents.push(indent);
                emit(commonToken(Python3Lexer.INDENT, spaces));
            } else {
                while (!indents.isEmpty() && indents.peek() > indent) {
                    this.emit(createDedent());
                    indents.pop();
                }
            }
        }
    }

    @Override
    public void reset() {
        tokens = new LinkedList<>();
        indents = new LinkedList<>();
        opened = 0;
        lastToken = null;
        super.reset();
    }
}
