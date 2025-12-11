package de.jplag.emf.model.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.emf.EmfLanguage;
import de.jplag.emf.dynamic.parser.DynamicEcoreParser;
import de.jplag.emf.util.AbstractModelView;
import de.jplag.emf.util.EMFUtil;
import de.jplag.emf.util.GenericEmfTreeView;

/**
 * Parser for EMF metamodels based on dynamically created tokens. This means each model element corresponds to a token
 * which is typed according to the model elements type.
 */
public class DynamicModelParser extends DynamicEcoreParser {
    private static final Logger logger = LoggerFactory.getLogger(DynamicModelParser.class);

    private static final String VIEW_FILE_WARNING = "Skipping view file {} as submission!";
    private static final String METAPACKAGE_WARNING = "Loading model instance {} without any metamodel!";
    private static final String METAPACKAGE_ERROR = "Error, not a metapackage: ";
    private static final String METAMODEL_LOADING_ERROR = "Could not load metamodel file!";

    private static final List<EPackage> metapackages = new ArrayList<>();
    private static final String ALL_EXTENSIONS = "*";

    /**
     * Creates the parser.
     */
    public DynamicModelParser() {
        EMFUtil.registerModelExtension(ALL_EXTENSIONS);
    }

    @Override
    protected void parseModelFile(File file, boolean normalize) throws ParsingException {
        // implicit assumption: Metamodel gets parsed first!
        if (file.getName().endsWith(EmfLanguage.FILE_ENDING)) {
            parseMetamodelFile(file);
        } else if (file.getName().endsWith(EmfLanguage.VIEW_FILE_EXTENSION)) {
            logger.warn(VIEW_FILE_WARNING, file.getName());
        } else {
            if (metapackages.isEmpty()) {
                logger.warn(METAPACKAGE_WARNING, file.getName());
            }
            super.parseModelFile(file, normalize);
        }
    }

    @Override
    protected String getCorrespondingViewFileExtension() {
        return EmfLanguage.VIEW_FILE_EXTENSION;
    }

    @Override
    protected AbstractModelView createView(File file, Resource modelResource) {
        return new GenericEmfTreeView(file, modelResource);
    }

    private void parseMetamodelFile(File file) throws ParsingException {
        metapackages.clear();
        Resource modelResource = EMFUtil.loadModelResource(file);
        if (modelResource == null) {
            throw new ParsingException(file, METAMODEL_LOADING_ERROR);
        }
        for (EObject object : modelResource.getContents()) {
            if (object instanceof EPackage ePackage) {
                metapackages.add(ePackage);
            } else {
                logger.error(METAPACKAGE_ERROR, object);
            }
        }
        EMFUtil.registerEPackageURIs(metapackages);
    }
}
