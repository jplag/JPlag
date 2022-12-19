package de.jplag.emf;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EcorePackage;
import org.kohsuke.MetaInfServices;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.emf.parser.EcoreParser;

/**
 * Language for EMF metamodels from the Eclipse Modeling Framework (EMF).
 * @author Timur Saglam
 */
@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {
    public static final String VIEW_FILE_SUFFIX = ".emfatic";
    public static final String FILE_ENDING = "." + EcorePackage.eNAME;

    private static final String NAME = "EMF metamodel";
    private static final String IDENTIFIER = "emf";
    private static final int DEFAULT_MIN_TOKEN_MATCH = 6;

    protected final EcoreParser parser;

    public Language() {
        this(new EcoreParser());
    }

    protected Language(EcoreParser parser) {
        this.parser = parser;
    }

    @Override
    public String[] suffixes() {
        return new String[] {FILE_ENDING};
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public int minimumTokenMatch() {
        return DEFAULT_MIN_TOKEN_MATCH;
    }

    @Override
    public List<Token> parse(Set<File> files) throws ParsingException {
        return parser.parse(files);
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
