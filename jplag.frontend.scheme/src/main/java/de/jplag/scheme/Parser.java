package de.jplag.scheme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.jplag.AbstractParser;
import de.jplag.ErrorConsumer;

public class Parser extends AbstractParser {
    private String currentFile;

    private List<de.jplag.Token> tokens;

    /**
     * Creates the parser.
     * @param errorConsumer is the consumer for any occurring errors.
     */
    public Parser(ErrorConsumer errorConsumer) {
        super(errorConsumer);
    }

    public List<de.jplag.Token> parse(File directory, String[] files) {
        tokens = new ArrayList<>();
        errors = 0;
        for (int i = 0; i < files.length; i++) {
            currentFile = files[i];
            getErrorConsumer().print(null, "Parsing file " + files[i]);
            if (!SchemeParser.parseFile(directory, files[i], null, this))
                errors++;
            tokens.add(new SchemeToken(SchemeTokenConstants.FILE_END, currentFile));
        }
        return tokens;
    }

    public void add(int type, Token token) {
        int length = token.endColumn - token.beginColumn + 1;
        tokens.add(new SchemeToken(type, currentFile, token.beginLine, token.endLine, length));
    }

}
