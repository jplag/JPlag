package de.jplag.emf;

import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import de.jplag.Token;
import de.jplag.TokenConstants;

/**
 * Ecore metamodel token class.
 * @author Timur Saglam
 */
public class MetamodelToken extends Token implements MetamodelTokenConstants {

    private final Optional<EObject> eObject;

    /**
     * Creates an Ecore metamodel token.
     * @param type is the corresponding ID of the {@link TokenConstants}.
     * @param file is the name of the source model file.
     * @param line is the line index in the metamodel where the token resides. Cannot be smaller than 1.
     * @param column is the column index, meaning where the token starts in the line.
     * @param length is the length of the token in the metamodel.
     * @param eObject is the corresponding eObject from which this token was extracted.
     */
    public MetamodelToken(int type, String file, EObject eObject) {
        super(type, file, -1);
        this.eObject = Optional.of(eObject);
    }

    public MetamodelToken(int type, String file) {
        super(type, file, -1);
        this.eObject = Optional.empty();
    }

    public Optional<EObject> getEObject() {
        return eObject;
    }

    @Override
    protected String type2string() {
        return switch (type) {
            case PACKAGE -> "EPackage";
            case ANNOTATION -> "EAnnotation";
            case CLASS -> "EClass";
            case DATATYPE -> "EDatatype";
            case ENUM -> "EEnum";
            case ENUM_LITERAL -> "EEnumLiteral";
            case OPERATION -> "EOperation";
            case REFERENCE -> "EReference";
            case ATTRIBUTE -> "EAttribute";
            case PARAMETER -> "EParameter";
            case INTERFACE -> "EClass (Interface)";
            case SUPER_TYPE -> "ESuperType";
            case ID_ATTRIBUTE -> "EAttribute (ID)";
            case CONTAINMENT -> "EReference (Containment)";
            case ABSTRACT_CLASS -> "EClass (Abstract)";
            case RETURN_TYPE -> "EClassifier (Return Type)";
            case THROWS_DECLARATION -> "EClassifier (Exception)";
            case FILE_END -> "(EOF)";
            default -> "<UNKNOWN" + type + ">";
        };
    }
}
