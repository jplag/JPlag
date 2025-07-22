package de.jplag.scxml;

import java.io.File;

import de.jplag.Token;
import de.jplag.TokenTrace;
import de.jplag.TokenType;
import de.jplag.scxml.parser.model.StatechartElement;

/**
 * Represents a SCXML token.
 */
public class ScxmlToken extends Token {

    private final StatechartElement element;

    /**
     * Creates an SCXML token that corresponds to a StatechartElement including model-view tracing information.
     * @param type is the type of the token
     * @param file is the SCXML statechart source file.
     * @param trace is the token trace, containing line, column, and token length in the SCXMl view.
     * @param element the corresponding StatechartElement this token was extracted from
     */
    public ScxmlToken(TokenType type, File file, TokenTrace trace, StatechartElement element) {
        super(type, file, trace.line(), trace.column(), trace.line(), trace.column() + trace.length(), trace.length());
        this.element = element;
    }

    /**
     * @return the StatechartElement corresponding to the token
     */
    public StatechartElement getStatechartElement() {
        return element;
    }
}
