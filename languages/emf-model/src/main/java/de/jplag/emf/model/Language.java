package de.jplag.emf.model;

import java.io.File;
import java.util.List;

import de.jplag.emf.model.parser.DynamicModelParser;

/**
 * Language for EMF metamodels from the Eclipse Modeling Framework (EMF). This language is based on a dynamically
 * created token set.
 * @author Timur Saglam
 */
public class Language extends de.jplag.emf.dynamic.Language {
    private static final String NAME = "EMF models (dynamically created token set)";
    public static final String VIEW_FILE_SUFFIX = ".treeview";

    public Language() {
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
    public String viewFileSuffix() {
        return VIEW_FILE_SUFFIX;
    }

    @Override
    public boolean expectsSubmissionOrder() {
        return true;
    }

    @Override
    public List<File> customizeSubmissionOrder(List<File> submissions) {
        submissions.sort((File first, File second) -> {
            return Boolean.compare(second.getName().endsWith(FILE_ENDING), first.getName().endsWith(FILE_ENDING));
        });
        return submissions;
    }
}
