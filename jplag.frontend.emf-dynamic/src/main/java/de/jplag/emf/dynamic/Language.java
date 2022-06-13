package de.jplag.emf.dynamic;

import de.jplag.ErrorConsumer;
import de.jplag.emf.dynamic.parser.DynamicEcoreParser;

/**
 * Language for EMF metamodels from the Eclipse Modeling Framework (EMF). This language is based on a dynamically
 * created token set instead of a hand-picked one.
 * @author Timur Saglam
 */
public class Language extends de.jplag.emf.Language {
    public static final String VIEW_FILE_SUFFIX = ".TreeView";
    private static final String NAME = "EMF metamodels (dynamically created token set)";
    private static final String SHORT_NAME = "EMF (dynamic)";

    public Language(ErrorConsumer program) {
        super(new DynamicEcoreParser(program));
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
    public int numberOfTokens() {
        return DynamicMetamodelTokenConstants.getNumberOfTokens();
    }
}
