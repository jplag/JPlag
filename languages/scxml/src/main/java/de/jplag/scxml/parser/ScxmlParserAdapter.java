package de.jplag.scxml.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.Token;
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
public class ScxmlParserAdapter extends AbstractParser {

    protected List<Token> tokens;
    protected File currentFile;
    protected AbstractScxmlVisitor visitor;
    protected ScxmlView view;

    public ScxmlParserAdapter() {
        this.visitor = new HandcraftedScxmlTokenGenerator(this);
    }

    /**
     * Parses all tokens from a set of files.
     * @param files the set of files.
     * @return the list of parsed tokens.
     */
    public List<Token> parse(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        for (File file : files) {
            parseModelFile(file);
        }
        return tokens;
    }

    /**
     * Loads a statechart from a file, parses it and extracts tokens from it.
     * @param file is the statechart file.
     */
    protected void parseModelFile(File file) throws ParsingException {
        currentFile = file;
        Statechart statechart;
        view = new ScxmlView(file);

        try {
            statechart = new ScxmlParser().parse(file);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new ParsingException(file, "failed to parse statechart:\n" + e.getMessage());
        }

        visitor.visit(statechart);
        tokens.add(Token.fileEnd(currentFile));
        view.writeToFile(ScxmlLanguage.VIEW_FILE_SUFFIX);
    }

    /**
     * Creates a token from the given type plus the associated statechart element and adds it to the token stream. The token
     * is enhanced with view information (see {@link ScxmlView}).
     * @param type the type of the token
     * @param source the statechart element associated with the token
     */
    public void addToken(ScxmlTokenType type, StatechartElement source) {
        ScxmlToken token = new ScxmlToken(type, currentFile, source);
        Token enhancedToken = view.enhanceToken(token, visitor.getCurrentStatechartDepth());
        tokens.add(enhancedToken);
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