package de.jplag.emf;

import java.io.File;
import java.util.Arrays;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

/**
 * Language for Ecore metamodels from the Eclipse Modeling Framework (EMF).
 * @author Timur Saglam
 */
public class Language implements de.jplag.Language {
    public static final String VIEW_FILE_SUFFIX = ".TreeView";
    private static final String NAME = "EMF metamodels";
    private static final String SHORT_NAME = "EMF";
    private static final String[] FILE_ENDINGS = new String[] {".ecore"};
    private static final int DEFAULT_MIN_TOKEN_MATCH = 5;

    private final EcoreParser parser;

    public Language(ErrorConsumer program) {
        parser = new EcoreParser(program);
    }

    @Override
    public String[] suffixes() {
        return FILE_ENDINGS;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getShortName() {
        return SHORT_NAME;
    }

    @Override
    public int minimumTokenMatch() {
        return DEFAULT_MIN_TOKEN_MATCH;
    }

    @Override
    public TokenList parse(File dir, String[] files) {
        return parser.parse(dir, Arrays.asList(files));
    }

    @Override
    public boolean hasErrors() {
        return parser.hasErrors();
    }

    @Override
    public boolean supportsColumns() {
        return true;
    }

    @Override
    public boolean isPreformatted() {
        return true;
    }

    @Override
    public int numberOfTokens() {
        return MetamodelTokenConstants.NUM_DIFF_TOKENS;
    }

    @Override
    public boolean useViewFiles() {
        return true;
    }

    @Override
    public String viewFileSuffix() {
        return VIEW_FILE_SUFFIX;
    }
}
