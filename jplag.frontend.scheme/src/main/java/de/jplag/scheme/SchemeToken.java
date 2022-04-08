package de.jplag.scheme;

import de.jplag.Token;

public class SchemeToken extends Token implements SchemeTokenConstants {

    public SchemeToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    public SchemeToken(int type, String file) {
        super(type, file, 1, 0, 0);
    }

    protected String type2string() {
        return switch (type) {
            case SchemeTokenConstants.FILE_END -> "EOF";
            case S_BOOL -> "BOOL    ";
            case S_NUMBER -> "NUMBER  ";
            case S_CHAR -> "CHAR    ";
            case S_STRING -> "STRING  ";
            case S_ID -> "ID      ";
            case S_LIST_BEGIN -> "(LIST   ";
            case S_LIST_END -> "LIST)   ";
            case S_VECTOR_BEGIN -> "(VECTOR ";
            case S_VECTOR_END -> "VECTOR) ";
            case S_LITERAL -> "LITERAL ";
            case S_QUOT_BEGIN -> "(QUOT   ";
            case S_QUOT_END -> "QUOT)   ";
            case S_CALL -> "CALL    ";
            case S_LAMBDA_BEGIN -> "(LAMBDA ";
            case S_LAMBDA_END -> "LAMBDA) ";
            case S_FORMAL_BEGIN -> "(FORMAL ";
            case S_FORMAL_END -> "FORMAL) ";
            case S_BODY_BEGIN -> "(BODY   ";
            case S_BODY_END -> "BODY)   ";
            case S_IF_BEGIN -> "(IF     ";
            case S_IF_END -> "IF)     ";
            case S_ALTERN -> "ALTERN  ";
            case S_ASSIGN_BEGIN -> "(ASSIGN ";
            case S_ASSIGN_END -> "ASSIGN) ";
            case S_COND_BEGIN -> "(COND   ";
            case S_COND_END -> "COND)   ";
            case S_ELSE -> "ELSE    ";
            case S_CASE_BEGIN -> "(CASE   ";
            case S_CASE_END -> "CASE)   ";
            case S_DO_BEGIN -> "(DO     ";
            case S_DO_END -> "DO)     ";
            case S_COMMAND -> "COMMAND ";
            case S_DEF_BEGIN -> "(DEF    ";
            case S_DEF_END -> "DEF)    ";
            case S_BEGIN -> "(       ";
            case S_END -> ")       ";
            case S_AND -> "AND     ";
            case S_OR -> "OR      ";
            case S_LET -> "LET     ";
            case S_DELAY -> "DELAY   ";
            case S_VAR -> "VAR     ";
            default -> "<?????> ";
        };
    }
}
