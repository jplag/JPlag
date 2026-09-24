package de.jplag.emf.model;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.emf.EmfLanguage;
import de.jplag.emf.model.parser.EmfModelParser;
import de.jplag.options.LanguageOptions;

import com.google.auto.service.AutoService;

/**
 * Language for models conforming to the Eclipse Modeling Framework (EMF). This language is based on a dynamically
 * created token set. When using the language, the metamodel files have to be passed via the language options.
 * @author Timur Saglam
 */
@AutoService(Language.class)
public class EmfModelLanguage extends EmfLanguage {

    /** File extension for the textual view. **/
    public static final String VIEW_FILE_EXTENSION = ".treeview";

    private static final String NO_METAMODEL_ERROR = "EMF model language module requires metamodel to be specified via language options!";
    private final EmfLanguageOptions options;

    /**
     * Creates the language and its language options.
     */
    public EmfModelLanguage() {
        options = new EmfLanguageOptions();
    }

    @Override
    public List<String> fileExtensions() {
        return List.of();
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
    public int minimumTokenMatch() {
        return 10;
    }

    @Override
    public String viewFileExtension() {
        return VIEW_FILE_EXTENSION;
    }

    @Override
    public LanguageOptions getOptions() {
        return options;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        if (!options.getMetamodelPathOption().hasValue()) {
            throw new ParsingException(files.iterator().next(), NO_METAMODEL_ERROR);
        }
        return new EmfModelParser().parse(files, normalize);
    }

    @Override
    public boolean supportsMultiLanguage() {
        return false;
    }
}
