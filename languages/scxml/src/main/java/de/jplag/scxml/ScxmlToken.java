package de.jplag.scxml;

import java.io.File;

import de.jplag.LanguageLoader;
import de.jplag.Token;
import de.jplag.TokenAttribute;
import de.jplag.scxml.parser.model.StatechartElement;

/**
 * Represents a SCXML token.
 */
public class ScxmlToken extends Token {

    private final StatechartElement element;

    /**
     * Creates an SCXML token that corresponds to a StatechartElement.
     * @param type the type of the token
     * @param file the source statechart file
     * @param element the corresponding StatechartElement this token was extracted from
     */
    public ScxmlToken(TokenAttribute type, File file, StatechartElement element) {
        super(type, file, NO_VALUE, NO_VALUE, NO_VALUE, LanguageLoader.getLanguage(ScxmlLanguage.class).get());
        this.element = element;
    }

    /**
     * Creates an SCXML token that corresponds to a StatechartElement including file information.
     * @param type the type of the token
     * @param file the source statechart file
     * @param line the line index in the source code where the token resides, 1-based
     * @param column the column index, meaning where the token starts in the line, 1-based
     * @param length the length of the token in the view file
     * @param element the corresponding StatechartElement this token was extracted from
     */
    public ScxmlToken(TokenAttribute type, File file, int line, int column, int length, StatechartElement element) {
        super(type, file, line, column, length, LanguageLoader.getLanguage(ScxmlLanguage.class).get());
        this.element = element;
    }

    /**
     * @return the StatechartElement corresponding to the token
     */
    public StatechartElement getStatechartElement() {
        return element;
    }
}
