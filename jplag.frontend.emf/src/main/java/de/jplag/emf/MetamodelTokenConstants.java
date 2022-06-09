package de.jplag.emf;

import de.jplag.TokenConstants;

/**
 * @author Timur Saglam
 */
public interface MetamodelTokenConstants extends TokenConstants {

    int PACKAGE = 2;
    int ANNOTATION = 3;
    int CLASS = 4;
    int DATATYPE = 5;
    int ENUM = 6;
    int ENUM_LITERAL = 7;
    int OPERATION = 8;
    int REFERENCE = 9;
    int ATTRIBUTE = 10;
    int PARAMETER = 11;
    int INTERFACE = 12;
    int SUPER_TYPE = 13;
    int ID_ATTRIBUTE = 14;
    int CONTAINMENT = 15;
    int ABSTRACT_CLASS = 16;
    int RETURN_TYPE = 17;
    int THROWS_DECLARATION = 18;

    /*
     * Number of token constants:
     */
    int NUM_DIFF_TOKENS = 14;
}
