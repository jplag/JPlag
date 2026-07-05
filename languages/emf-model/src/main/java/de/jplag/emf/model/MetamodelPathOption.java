package de.jplag.emf.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.emf.EmfLanguage;
import de.jplag.emf.util.EMFUtil;
import de.jplag.options.LanguageOption;
import de.jplag.options.OptionType;

/**
 * Language option for specifying one or more {@code .ecore} metamodel files required to parse EMF model instances.
 * Multiple paths can be provided as a comma-separated list.
 */
public class MetamodelPathOption implements LanguageOption<String> {

    private static final Logger logger = LoggerFactory.getLogger(MetamodelPathOption.class);

    private static final String ALL_EXTENSIONS = "*";
    private static final String PATH_SEPARATOR = ",";

    private static final String METAPACKAGE_ERROR = "Error, not a metapackage: {}";
    private static final String METAMODEL_LOADING_ERROR = "Could not load metamodel file {}";
    private static final String NOT_A_METAMODEL_ERROR = "Metamodel file does not end in .ecore: {}";
    private static final String NOT_A_FILE_ERROR = "Metamodel path does not point to a valid file: {}";

    private String value;

    @Override
    public OptionType<String> getType() {
        return OptionType.string();
    }

    @Override
    public String getName() {
        return "metamodel";
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDescription() {
        return "Path(s) to metamodel files (.ecore), multiple paths should be comma-separated.";
    }

    @Override
    public void setValue(String value) {
        this.value = value;
        if (value != null) {
            EMFUtil.registerModelExtension(ALL_EXTENSIONS);
            for (String path : value.split(PATH_SEPARATOR)) {
                parseMetamodelFileIfValid(path);
            }
        }
    }

    @Override
    public boolean hasValue() {
        return value != null;
    }

    private void parseMetamodelFileIfValid(String path) {
        File file = new File(path.trim());
        if (!file.exists() || !file.isFile()) {
            logger.error(NOT_A_FILE_ERROR, file);
        } else if (!file.getName().endsWith(EmfLanguage.FILE_ENDING)) {
            logger.error(NOT_A_METAMODEL_ERROR, file);
        } else {
            parseMetamodelFile(file);
        }
    }

    private void parseMetamodelFile(File file) {
        List<EPackage> metapackages = new ArrayList<>();
        Resource modelResource = EMFUtil.loadModelResource(file);
        if (modelResource == null) {
            logger.error(METAMODEL_LOADING_ERROR, file);
        } else {
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

}
