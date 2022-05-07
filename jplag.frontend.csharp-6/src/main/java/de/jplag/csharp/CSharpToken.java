package de.jplag.csharp;

import de.jplag.Token;
import de.jplag.TokenConstants;

/**
 * C# token class.
 * @author Timur Saglam
 */
public class CSharpToken extends Token implements CSharpTokenConstants {

    /**
     * Creates a C# token.
     * @param type is the corresponding ID of the {@link TokenConstants}.
     * @param file is the name of the source code file.
     * @param line is the line index in the source code where the token resides. Cannot be smaller than 1.
     * @param column is the column index, meaning where the token starts in the line.
     * @param length is the length of the token in the source code.
     */
    public CSharpToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    @Override
    protected String type2string() {
        return switch (type) {
            case TokenConstants.FILE_END -> "EOF";
            case INVOCATION -> "INVOC";
            case OBJECT_CREATION -> "OBJECT";
            case ARRAY_CREATION -> "ARRAY";
            case ASSIGNMENT -> "ASSIGN";
            case FIELD -> "FIELD";
            case CONSTANT -> "CONST";
            case IF -> "IF";
            case IF_END -> "}IF";
            case SWITCH_BEGIN -> "SWITCH{";
            case SWITCH_END -> "}SWITCH";
            case CASE -> "CASE";
            case DO -> "DO";
            case WHILE -> "WHILE";
            case FOR -> "FOR";
            case FOREACH -> "FOREACH";
            case BREAK -> "BREAK";
            case CONTINUE -> "CONTINUE";
            case GOTO -> "GOTO";
            case RETURN -> "RETURN";
            case THROW -> "THROW";
            case CHECKED -> "CHECKED";
            case UNCHECKED -> "UNCHECKED";
            case LOCK -> "LOCK";
            case USING -> "USING";
            case TRY -> "TRY";
            case CATCH -> "CATCH";
            case FINALLY -> "FINALLY";
            case NAMESPACE_BEGIN -> "NAMESPACE{";
            case NAMESPACE_END -> "}NAMESPACE";
            case USING_DIRECTIVE -> "USING";
            case CLASS_BEGIN -> "CLASS{";
            case CLASS_END -> "}CLASS";
            case METHOD -> "METHOD";
            case PROPERTY -> "PROPERTY";
            case EVENT -> "EVENT";
            case INDEXER -> "INDEXER";
            case OPERATOR -> "OPERATOR";
            case CONSTRUCTOR -> "CONSTR";
            case DESTRUCTOR -> "DESTRUCTOR";
            case STRUCT_BEGIN -> "STRUCT{";
            case STRUCT_END -> "}STRUCT";
            case INTERFACE_BEGIN -> "INTERFACE{";
            case INTERFACE_END -> "}INTERFACE";
            case ENUM -> "ENUM";
            case DELEGATE -> "DELEGATE";
            case ATTRIBUTE -> "ATTRIBUTE";
            case UNSAFE -> "UNSAFE";
            case FIXED -> "FIXED";
            case METHOD_BEGIN -> "METHOD{";
            case METHOD_END -> "}METHOD";
            case STRUCT -> "STRUCT";
            case IF_BEGIN -> "IF{";
            case CLASS -> "CLASS";
            case INTERFACE -> "INTERFACE";
            case ENUM_BEGIN -> "ENUM{";
            case ENUM_END -> "}ENUM";
            case ENUMERAL -> "ENUMERAL";
            case LOCAL_VARIABLE -> "LOCAL_VAR";
            case ACCESSORS_BEGIN -> "ACCESSORS{";
            case ACCESSORS_END -> "}ACCESSORS";
            case ACCESSOR_BEGIN -> "ACCESSORS{";
            case ACCESSOR_END -> "}ACCESSORS";
            default -> "<UNKNOWN" + type + ">";
        };
    }
}
