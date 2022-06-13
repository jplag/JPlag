package de.jplag.emf;

import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import de.jplag.Token;
import de.jplag.TokenConstants;

/**
 * EMF metamodel token.
 * @author Timur Saglam
 */
public class MetamodelToken extends Token implements MetamodelTokenConstants {

    private final Optional<EObject> eObject;

    /**
     * Creates an Ecore metamodel token that corresponds to an EObject.
     * @param type is the corresponding ID of the {@link TokenConstants}.
     * @param file is the name of the source model file.
     * @param eObject is the corresponding eObject in the model from which this token was extracted.
     */
    public MetamodelToken(int type, String file, EObject eObject) {
        super(type, file, -1);
        this.eObject = Optional.of(eObject);
    }

    /**
     * Creates an Ecore metamodel token.
     * @param type is the corresponding ID of the {@link TokenConstants}.
     * @param file is the name of the source model file.
     */
    public MetamodelToken(int type, String file) {
        super(type, file, -1);
        this.eObject = Optional.empty();
    }

    /**
     * @return the optional corresponding EObject of the token.
     */
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
