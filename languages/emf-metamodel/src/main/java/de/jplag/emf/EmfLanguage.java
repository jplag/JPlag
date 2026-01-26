package de.jplag.emf;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EcorePackage;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.emf.parser.EcoreParser;
import de.jplag.emf.util.EMFUtil;

import com.google.auto.service.AutoService;

/**
 * Language for EMF metamodels from the Eclipse Modeling Framework (EMF).
 * @author Timur Saglam
 */
@AutoService(Language.class)
public class EmfLanguage implements Language {

    /**
     * Creates the language facade.
     */
    public EmfLanguage() {
        EMFUtil.registerEcoreExtension();
    }

    /** File extension for the textual view. **/
    public static final String VIEW_FILE_EXTENSION = ".emfatic";
    /** File extension for model files. **/
    public static final String FILE_ENDING = "." + EcorePackage.eNAME;

    @Override
    public List<String> fileExtensions() {
        return List.of(FILE_ENDING);
    }

    @Override
    public String getName() {
        return "EMF metamodel";
    }

    @Override
    public String getIdentifier() {
        return "emf";
    }

    @Override
    public int minimumTokenMatch() {
        return 6;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new EcoreParser().parse(files, normalize);
    }

    @Override
    public boolean useViewFiles() {
        return true;
    }

    @Override
    public String viewFileExtension() {
        return VIEW_FILE_EXTENSION;
    }
}
