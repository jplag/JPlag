package de.jplag.emf;

import de.jplag.Token;
import de.jplag.TokenConstants;

/**
 * Ecore metamodel token class.
 * @author Timur Saglam
 */
public class MetamodelToken extends Token implements MetamodelTokenConstants {

    /**
     * Creates an Ecore metamodel token.
     * @param type is the corresponding ID of the {@link TokenConstants}.
     * @param file is the name of the source model file.
     * @param line is the line index in the metamodel where the token resides. Cannot be smaller than 1.
     * @param column is the column index, meaning where the token starts in the line.
     * @param length is the length of the token in the metamodel.
     */
    public MetamodelToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
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
            case INTERFACE -> "EInterface";
            case FILE_END -> "(EOF)";
            default -> "<UNKNOWN" + type + ">";
        };
    }
}
