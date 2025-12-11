package de.jplag.scxml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenTrace;
import de.jplag.scxml.ScxmlLanguage;
import de.jplag.scxml.ScxmlToken;
import de.jplag.scxml.ScxmlTokenType;
import de.jplag.scxml.parser.model.Statechart;
import de.jplag.scxml.parser.model.StatechartElement;
import de.jplag.scxml.util.AbstractScxmlVisitor;
import de.jplag.scxml.util.ScxmlView;

/**
 * Parser adapter for SCXML statecharts that uses a Statechart object obtained from an instance of ScxmlParser to
 * extract tokens.
 */
public class ScxmlParserAdapter {

    /**
     * The list of extracted tokens for the current file.
     */
    protected List<Token> tokens;

    /**
     * The current statechart input file.
     */
    protected File currentStatechartFile;

    /**
     * The visitor to use for recursively iterating over the statechart to extract tokens.
     */
    protected AbstractScxmlVisitor visitor;
    protected ScxmlView view;

    /**
     * Creates the adapter.
     */
    public ScxmlParserAdapter() {
        this.visitor = new HandcraftedScxmlTokenGenerator(this);
    }

    /**
     * Extracts all tokens from a set of files.
     * @param files the set of files
     * @return the list of parsed tokens
     * @throws ParsingException if the statechart could not be parsed
     */
    public List<Token> parse(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        for (File file : files) {
            parseStatechartFile(file);
        }
        return tokens;
    }

    /**
     * Loads a statechart from a file, parses it and extracts tokens from it.
     * @param file the statechart file
     * @throws ParsingException if the statechart could not be parsed
     */
    protected void parseStatechartFile(File file) throws ParsingException {
        currentStatechartFile = file;
        Statechart statechart;
        view = new ScxmlView(file);

        try {
            statechart = new ScxmlParser().parse(file);
        } catch (ParserConfigurationException e) {
            throw new ParsingException(file, "failed to construct XML document builder:\n" + e.getMessage());
        }

        visitor.visit(statechart);
        tokens.add(Token.fileEnd(currentStatechartFile));
        view.writeToFile(ScxmlLanguage.VIEW_FILE_EXTENSION);
    }

    /**
     * Creates a token from the given type plus the associated statechart element and adds it to the token stream. The token
     * is enhanced with view information (see {@link ScxmlView}).
     * @param type the type of the token
     * @param source the statechart element associated with the token
     */
    public void addToken(ScxmlTokenType type, StatechartElement source) {
        TokenTrace trace = view.appendElement(type, source, visitor.getCurrentStatechartDepth());
        tokens.add(new ScxmlToken(type, currentStatechartFile, trace, source));
    }

    /**
     * Creates a token from the given type without an associated statechart element. The token is enhanced with view
     * information (see {@link ScxmlView}).
     * @param type the type of the token
     */
    public void addEndToken(ScxmlTokenType type) {
        addToken(type, null);
    }

}
