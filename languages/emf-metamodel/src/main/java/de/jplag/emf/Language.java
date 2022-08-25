package de.jplag.emf;

import java.io.File;
import java.util.Arrays;

import org.eclipse.emf.ecore.EcorePackage;
import org.kohsuke.MetaInfServices;

import de.jplag.TokenList;
import de.jplag.emf.parser.EcoreParser;

/**
 * Language for EMF metamodels from the Eclipse Modeling Framework (EMF).
 * @author Timur Saglam
 */
@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {
    public static final String VIEW_FILE_SUFFIX = ".TreeView";
    public static final String FILE_ENDING = "." + EcorePackage.eNAME;

    private static final String NAME = "EMF metamodel";
    public static final String IDENTIFIER = "emf-metamodel";
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
    public TokenList parse(File dir, String[] files) {
        return parser.parse(dir, Arrays.asList(files));
    }

    @Override
    public boolean hasErrors() {
        return parser.hasErrors();
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
