package de.jplag.emf.model;

import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;

/**
 * Language options for the EMF model instance language, allowing users to specify metamodel files ({@code .ecore})
 * required for parsing model instance files.
 */
public class EmfLanguageOptions extends LanguageOptions {

    private final LanguageOption<String> metamodelPathOption;

    /**
     * Creates the EMF options.
     */
    public EmfLanguageOptions() {
        metamodelPathOption = new MetamodelPathOption();
        addOption(metamodelPathOption);
    }

    /**
     * @return the option for the paths to the {@code .ecore} metamodel files.
     */
    public LanguageOption<String> getMetamodelPathOption() {
        return metamodelPathOption;
    }
}