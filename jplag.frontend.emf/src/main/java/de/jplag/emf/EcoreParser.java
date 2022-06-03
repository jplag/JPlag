package de.jplag.emf;

import static de.jplag.emf.MetamodelTokenConstants.ANNOTATION;
import static de.jplag.emf.MetamodelTokenConstants.ATTRIBUTE;
import static de.jplag.emf.MetamodelTokenConstants.CLASS;
import static de.jplag.emf.MetamodelTokenConstants.DATATYPE;
import static de.jplag.emf.MetamodelTokenConstants.ENUM;
import static de.jplag.emf.MetamodelTokenConstants.ENUM_LITERAL;
import static de.jplag.emf.MetamodelTokenConstants.INTERFACE;
import static de.jplag.emf.MetamodelTokenConstants.OPERATION;
import static de.jplag.emf.MetamodelTokenConstants.PACKAGE;
import static de.jplag.emf.MetamodelTokenConstants.PARAMETER;
import static de.jplag.emf.MetamodelTokenConstants.REFERENCE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.jplag.AbstractParser;
import de.jplag.ErrorConsumer;
import de.jplag.TokenConstants;
import de.jplag.TokenList;

/**
 * Parser for EMF metamodels.
 * @author Timur Saglam
 */
public class EcoreParser extends AbstractParser {
    private static final String TREE_VIEW_FILE_SUFFIX = ".tmp";
    private TokenList tokens;
    private String currentFile;
    private StringBuilder treeView;
    private int lineIndex;
    private int columnIndex;

    /**
     * Creates the parser.
     * @param errorConsumer is the consumer for any occurring errors.
     */
    public EcoreParser(ErrorConsumer errorConsumer) {
        super(errorConsumer);
    }

    /**
     * Parses all tokens form a list of files.
     * @param directory is the base directory.
     * @param fileNames is the list of file names.
     * @return the list of parsed tokens.
     */
    public TokenList parse(File directory, List<String> fileNames) {
        tokens = new TokenList();
        errors = 0;
        for (String fileName : fileNames) {
            EPackage root = loadModel(directory.toString(), fileName);
            currentFile = fileName + TREE_VIEW_FILE_SUFFIX;
            treeView = new StringBuilder();
            lineIndex = 0;
            columnIndex = 0;
            parseEPackage(root);

            tokens.addToken(new MetamodelToken(TokenConstants.FILE_END, fileName, -1, -1, -1));
            createTreeViewFile(directory, fileName);
        }
        return tokens;
    }

    private void createTreeViewFile(File directory, String fileName) {
        File treeViewFile = new File(directory, currentFile);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(treeViewFile));) {
            if (!treeViewFile.exists()) {
                treeViewFile.createNewFile();
            }
            writer.append(treeView.toString());
        } catch (IOException exception) {
            errors++;
            getErrorConsumer().addError("Parsing Error in '" + fileName + "':" + exception.toString());
            exception.printStackTrace();
        }
    }

    private EPackage loadModel(String directory, String name) {
        EcorePackage.eINSTANCE.eClass();
        final Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
        final Map<String, Object> extensionMap = registry.getExtensionToFactoryMap();
        extensionMap.put(EcorePackage.eNAME, new XMIResourceFactoryImpl());
        final ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.getResource(URI.createFileURI(directory + File.separator + name), true);
        EObject content = resource.getContents().get(0);
        if (content instanceof EPackage root) {
            return root;
        }
        throw new IllegalStateException("Root element of model " + name + " is not a package: " + content);
    }

    private void parseEPackage(EPackage ePackage) {
        addToken(PACKAGE, ePackage, true);
        ePackage.getEClassifiers().forEach(it -> parseEClassifier(it));
        ePackage.getEAnnotations().forEach(it -> parseEAnnotation(it));
        ePackage.getESubpackages().forEach(it -> parseEPackage(it));
    }

    private void parseEAnnotation(EAnnotation annotation) {
        addToken(ANNOTATION, annotation, true);
    }

    private void parseEClassifier(EClassifier classifier) {
        if (classifier instanceof EClass eClass) {
            parseEClass(eClass);
        } else if (classifier instanceof EDataType dataType) {
            parseEDataType(dataType);
        }
        // TODO TS: Parse type parameters & generic type?
        classifier.getEAnnotations().forEach(it -> parseEAnnotation(it));
    }

    private void parseEClass(EClass eClass) {
        if (eClass.isInterface()) {
            addToken(INTERFACE, eClass, true);
        } else {
            addToken(CLASS, eClass, true);
        }
        // TODO TS: Extract abstract? probably not
        // TODO TS: Extract super type relation? probably
        eClass.getEStructuralFeatures().forEach(it -> parseEStructuralFeature(it));
        eClass.getEOperations().forEach(it -> parseEOperation(it));
    }

    private void parseEOperation(EOperation operation) {
        addToken(OPERATION, operation, true);
        operation.getEParameters().forEach(it -> addToken(PARAMETER, it, false));
        // TODO TS: Extract return type?
        // TODO TS: Extract throws declarations?
        // TODO TS: Parse type parameters & generic type?
        operation.getEAnnotations().forEach(it -> parseEAnnotation(it));
    }

    private void parseEStructuralFeature(EStructuralFeature structuralFeature) {
        if (structuralFeature instanceof EAttribute attribute) {
            addToken(ATTRIBUTE, attribute, true);
        } else if (structuralFeature instanceof EReference reference) {
            addToken(REFERENCE, reference, true);
            // TODO TS: Extract containment?
        }
        structuralFeature.getEAnnotations().forEach(it -> parseEAnnotation(it));
    }

    private void parseEDataType(EDataType dataType) {
        if (dataType instanceof EEnum enumeration) {
            parseEEnum(enumeration);
        } else {
            addToken(DATATYPE, dataType, true);
        }
    }

    private void parseEEnum(EEnum enumeration) {
        addToken(ENUM, enumeration, true);
        enumeration.getELiterals().forEach(it -> parseEEnumLiteral(it));
    }

    private void parseEEnumLiteral(EEnumLiteral enumeral) {
        addToken(ENUM_LITERAL, enumeral, false);
        enumeral.getEAnnotations().forEach(it -> parseEAnnotation(it));
    }

    private void addToken(int type, EObject source, boolean newLine) {
        if (newLine) {
            lineIndex++;
            treeView.append(System.lineSeparator());
        }
        String tokenText = source.getClass().getSimpleName().toString() + " ";
        if (source instanceof ENamedElement element) {
            tokenText += " " + element.getName();
        }
        int tokenLength = tokenText.length();
        treeView.append(tokenText);
        tokens.addToken(new MetamodelToken(type, currentFile, lineIndex, columnIndex, tokenLength));
        columnIndex += tokenLength;
    }
}
