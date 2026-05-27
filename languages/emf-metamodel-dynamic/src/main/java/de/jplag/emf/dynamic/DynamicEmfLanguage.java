package de.jplag.emf.dynamic;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.emf.EmfLanguage;

/**
 * Language for EMF metamodels from the Eclipse Modeling Framework (EMF). This language is based on a dynamically
 * created token set instead of a hand-picked one.
 * @author Timur Saglam
 * @deprecated this language module was never available in JPlag as it was prototypical. It is now deprecated and should
 * not be used, as it is non-functional and only serves as deprecation placeholder.
 */
@Deprecated(since = "7.0.0", forRemoval = true)
public class DynamicEmfLanguage extends EmfLanguage { // currently not included in the CLI

    @Override
    public String getName() {
        return "EMF metamodels (dynamically created token set)";
    }

    @Override
    public String getIdentifier() {
        return "emf-dynamic";
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        throw new UnsupportedOperationException("Language module is deprecated and no longer supported!");
    }
}
