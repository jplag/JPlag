package de.jplag.emf.model;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import org.kohsuke.MetaInfServices;

import de.jplag.emf.dynamic.DynamicEmfLanguage;
import de.jplag.emf.model.parser.DynamicModelParser;

/**
 * Language for EMF metamodels from the Eclipse Modeling Framework (EMF). This language is based on a dynamically
 * created token set.
 * @author Timur Saglam
 */
@MetaInfServices(de.jplag.Language.class)
public class EmfModelLanguage extends DynamicEmfLanguage {
    private static final String NAME = "EMF models (dynamically created token set)";
    private static final String IDENTIFIER = "emf-model";

    public static final String VIEW_FILE_SUFFIX = ".treeview";

    public EmfModelLanguage() {
        super(new DynamicModelParser());
    }

    @Override
    public String[] suffixes() {
        return new String[] {};
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
    public String viewFileSuffix() {
        return VIEW_FILE_SUFFIX;
    }

    @Override
    public boolean expectsSubmissionOrder() {
        return true;
    }

    @Override
    public List<File> customizeSubmissionOrder(List<File> sub) {
        return sub.stream().sorted(Comparator.comparing(file -> file.getName().endsWith(FILE_ENDING) ? 0 : 1)).toList();
    }
}
