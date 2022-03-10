package de.jplag.csharp;

import de.jplag.Token;

public class CSharpToken extends Token implements CSharpTokenConstants {

    public CSharpToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    protected String type2string() {
        return switch (type) {
            case CSharpTokenConstants.FILE_END -> "EOF";
            case _INVOCATION -> "INVOCATION";
            case _OBJECT_CREATION -> "OBJECT_CRE";
            case _ARRAY_CREATION -> "ARRAY_CREA";
            case _ASSIGNMENT -> "ASSIGNMENT";
            case _L_BRACE -> "L_BRACE { ";
            case _R_BRACE -> "R_BRACE } ";
            case _DECLARE_VAR -> "DECLAREVAR";
            case _DECLARE_CONST -> "DECLARE_CO";
            case _IF -> "IF {      ";
            case _ELSE -> "ELSE      ";
            case _END_IF -> "IF }      ";
            case _SWITCH_BEGIN -> "SWITCH {  ";
            case _SWITCH_END -> "SWITCH }  ";
            case _CASE -> "CASE      ";
            case _DO -> "DO        ";
            case _WHILE -> "WHILE     ";
            case _FOR -> "FOR       ";
            case _FOREACH -> "FOREACH   ";
            case _BREAK -> "BREAK     ";
            case _CONTINUE -> "CONTINUE  ";
            case _GOTO -> "GOTO      ";
            case _RETURN -> "RETURN    ";
            case _THROW -> "THROW     ";
            case _CHECKED -> "CHECKED   ";
            case _UNCHECKED -> "UNCHECKED ";
            case _LOCK -> "LOCK      ";
            case _USING -> "USING     ";
            case _TRY -> "TRY       ";
            case _CATCH -> "CATCH     ";
            case _FINALLY -> "FINALLY   ";
            case _NAMESPACE_BEGIN -> "NAMESPACE{";
            case _NAMESPACE_END -> "NAMESPACE}";
            case _USING_DIRECTIVE -> "USING_DIR ";
            case _CLASS_BEGIN -> "CLASS {   ";
            case _CLASS_END -> "CLASS }   ";
            case _METHOD -> "METHOD    ";
            case _PROPERTY -> "PROPERTY  ";
            case _EVENT -> "EVENT     ";
            case _INDEXER -> "INDEXER   ";
            case _OPERATOR -> "OPERATOR  ";
            case _CONSTRUCTOR -> "CONSTR    ";
            case _STATIC_CONSTR -> "ST_CONSTR ";
            case _DESTRUCTOR -> "DESTRUCTOR";
            case _STRUCT_BEGIN -> "STRUCT {  ";
            case _STRUCT_END -> "STRUCT }  ";
            case _INTERFACE_BEGIN -> "INTERFACE{";
            case _INTERFACE_END -> "INTERFACE}";
            case _ENUM -> "ENUM      ";
            case _DELEGATE -> "DELEGATE  ";
            case _ATTRIBUTE -> "ATTRIBUTE ";
            case _UNSAFE -> "UNSAFE    ";
            case _FIXED -> "FIXED     ";
            default -> "<UNKNOWN> ";
        };
    }
}
