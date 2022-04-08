package de.jplag.cpp;

import de.jplag.Token;

public class CPPToken extends Token implements CPPTokenConstants {

    public CPPToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    public CPPToken(int type, String file) {
        super(type, file, 1, 0, 0);
    }

    protected String type2string() {
        return switch (type) {
            case CPPTokenConstants.FILE_END -> "EOF";
            case C_BLOCK_BEGIN -> "BLOCK{";
            case C_BLOCK_END -> "}BLOCK";
            case C_SCOPE -> "SCOPE";
            case C_QUESTIONMARK -> "COND";
            case C_ELLIPSIS -> "...";
            case C_ASSIGN -> "ASSIGN";
            case C_DOT -> "DOT";
            case C_ARROW -> "ARROW";
            case C_DOTSTAR -> "DOTSTAR";
            case C_ARROWSTAR -> "ARROWSTAR";
            case C_AUTO -> "AUTO";
            case C_BREAK -> "BREAK";
            case C_CASE -> "CASE";
            case C_CATCH -> "CATCH";
            case C_CHAR -> "CHAR";
            case C_CONST -> "CONST";
            case C_CONTINUE -> "CONTINUE";
            case C_DEFAULT -> "DEFAULT";
            case C_DELETE -> "DELETE";
            case C_DO -> "DO";
            case C_DOUBLE -> "DOUBLE";
            case C_ELSE -> "ELSE";
            case C_ENUM -> "ENUM";
            case C_EXTERN -> "EXTERN";
            case C_FLOAT -> "FLOAT";
            case C_FOR -> "FOR";
            case C_FRIEND -> "FRIEND";
            case C_GOTO -> "GOTO";
            case C_IF -> "IF";
            case C_INLINE -> "INLINE";
            case C_INT -> "INT";
            case C_LONG -> "LONG";
            case C_NEW -> "NEW";
            case C_PRIVATE -> "PRIVATE";
            case C_PROTECTED -> "PROTECTED";
            case C_PUBLIC -> "PUBLIC";
            case C_REDECLARED -> "REDECLARED";
            case C_REGISTER -> "REGISTER";
            case C_RETURN -> "RETURN";
            case C_SHORT -> "SHORT";
            case C_SIGNED -> "SIGNED";
            case C_SIZEOF -> "SIZEOF";
            case C_STATIC -> "STATIC";
            case C_STRUCT -> "STRUCT";
            case C_CLASS -> "CLASS";
            case C_SWITCH -> "SWITCH";
            case C_TEMPLATE -> "TEMPLATE";
            case C_THIS -> "THIS";
            case C_TRY -> "TRY";
            case C_TYPEDEF -> "TYPEDEF";
            case C_UNION -> "UNION";
            case C_UNSIGNED -> "UNSIGNED";
            case C_VIRTUAL -> "VIRTUAL";
            case C_VOID -> "VOID";
            case C_VOLANTILE -> "VOLANTILE";
            case C_WHILE -> "WHILE";
            case C_OPERATOR -> "OPERATOR";
            case C_THROW -> "THROW";
            case C_ID -> "ID";
            case C_FUN -> "FUN";
            case C_NULL -> "NULL";
            default -> "<UNKNOWN" + type + ">";
        };
    }
}
