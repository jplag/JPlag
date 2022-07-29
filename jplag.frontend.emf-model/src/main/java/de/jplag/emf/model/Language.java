package de.jplag.emf.model;

import java.io.File;
import java.util.List;

import de.jplag.ErrorConsumer;
import de.jplag.emf.model.parser.DynamicModelParser;

/**
 * Language for EMF metamodels from the Eclipse Modeling Framework (EMF). This language is based on a dynamically
 * created token set instead of a hand-picked one.
 * @author Timur Saglam
 */
public class Language extends de.jplag.emf.dynamic.Language {
    private static final String NAME = "EMF models (dynamically created token set)";
    private static final String SHORT_NAME = "EMF models (dynamic)";

    public Language(ErrorConsumer program) {
        super(new DynamicModelParser(program));
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
    public String getShortName() {
        return SHORT_NAME;
    }

    @Override
    public boolean expectsSubmissionOrder() {
        return true;
    }

    @Override
    public List<File> customizeSubmissionOrder(List<File> submissionNames) {
        submissionNames.sort((File first, File second) -> {
            return Boolean.compare(second.getName().endsWith(FILE_ENDING), first.getName().endsWith(FILE_ENDING));
        });
        return submissionNames;
    }
}
