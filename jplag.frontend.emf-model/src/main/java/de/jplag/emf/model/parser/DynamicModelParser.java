package de.jplag.emf.model.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ErrorConsumer;
import de.jplag.emf.dynamic.parser.DynamicEcoreParser;
import de.jplag.emf.parser.EMFUtil;

/**
 * Parser for EMF metamodels based on dynamically created tokens.
 * @author Timur Saglam
 */
public class DynamicModelParser extends DynamicEcoreParser {
    private static final Logger logger = LoggerFactory.getLogger(DynamicModelParser.class.getSimpleName());
    private static final List<EPackage> metapackages = new ArrayList<>();
    private static final String ALL_EXTENSIONS = "*";

    /**
     * Creates the parser.
     * @param errorConsumer is the consumer for any occurring errors.
     */
    public DynamicModelParser(ErrorConsumer errorConsumer) {
        super(errorConsumer);
        EMFUtil.registerModelExtension(ALL_EXTENSIONS);
    }

    @Override
    protected void parseModelFile(String filePath) {
        // implicit assumption: Metamodel gets parsed first!
        if (filePath.endsWith(de.jplag.emf.Language.FILE_ENDING)) {
            parseMetamodelFile(filePath);
        } else {
            if (metapackages.isEmpty()) {
                logger.warn("Loading model instances without any metamodel!");
            }
            super.parseModelFile(filePath);
        }
    }

    private void parseMetamodelFile(String filePath) {
        metapackages.clear();
        List<EObject> model = EMFUtil.loadModel(filePath);
        if (model == null) {
            errors++;
        } else {
            for (EObject object : model) {
                if (object instanceof EPackage ePackage) {
                    metapackages.add(ePackage);
                } else {
                    logger.error("Error, not a metapackage: " + object);
                }
            }
            EMFUtil.registerEPackageURIs(metapackages);
        }
    }
}
