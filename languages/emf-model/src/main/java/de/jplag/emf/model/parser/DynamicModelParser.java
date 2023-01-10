package de.jplag.emf.model.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

import de.jplag.ParsingException;
import de.jplag.emf.dynamic.parser.DynamicEcoreParser;
import de.jplag.emf.util.AbstractModelView;
import de.jplag.emf.util.EMFUtil;
import de.jplag.emf.util.MetamodelTreeView;

/**
 * Parser for EMF metamodels based on dynamically created tokens.
 */
public class DynamicModelParser extends DynamicEcoreParser {
    private static final List<EPackage> metapackages = new ArrayList<>();
    private static final String ALL_EXTENSIONS = "*";

    /**
     * Creates the parser.
     */
    public DynamicModelParser() {
        EMFUtil.registerModelExtension(ALL_EXTENSIONS);
    }

    @Override
    protected void parseModelFile(File file) throws ParsingException {
        // implicit assumption: Metamodel gets parsed first!
        if (file.getName().endsWith(de.jplag.emf.Language.FILE_ENDING)) {
            parseMetamodelFile(file);
        } else {
            if (metapackages.isEmpty()) {
                logger.warn("Loading model instances without any metamodel!");
            }
            super.parseModelFile(file);
        }
    }

    @Override
    protected AbstractModelView createView(File file, Resource modelResource) {
        return new MetamodelTreeView(file, modelResource);
    }

    private void parseMetamodelFile(File file) throws ParsingException {
        metapackages.clear();
        Resource modelResource = EMFUtil.loadModelResource(file);
        if (modelResource == null) {
            throw new ParsingException(file, "Could not load metamodel file!");
        } else {
            for (EObject object : modelResource.getContents()) {
                if (object instanceof EPackage ePackage) {
                    metapackages.add(ePackage);
                } else {
                    logger.error("Error, not a metapackage: {}", object);
                }
            }
            EMFUtil.registerEPackageURIs(metapackages);
        }
    }
}
