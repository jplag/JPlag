package de.jplag.emf.model;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.emf.dynamic.DynamicEmfLanguage;
import de.jplag.emf.model.parser.DynamicModelParser;

/**
 * Language for EMF metamodels from the Eclipse Modeling Framework (EMF). This language is based on a dynamically
 * created token set.
 * @author Timur Saglam
 */
@MetaInfServices(Language.class)
public class EmfModelLanguage extends DynamicEmfLanguage {

    @Override
    public String[] suffixes() {
        return new String[] {};
    }

    @Override
    public String getName() {
        return "EMF models (dynamically created token set)";
    }

    @Override
    public String getIdentifier() {
        return "emf-model";
    }

    @Override
    public String viewFileSuffix() {
        return ".treeview";
    }

    @Override
    public boolean expectsSubmissionOrder() {
        return true;
    }

    @Override
    public List<File> customizeSubmissionOrder(List<File> sub) {
        return sub.stream().sorted(Comparator.comparing(file -> file.getName().endsWith(FILE_ENDING) ? 0 : 1)).toList();
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new DynamicModelParser().parse(files, normalize);
    }
}
