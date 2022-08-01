package de.jplag.kotlin;

import static de.jplag.kotlin.KotlinTokenConstants.ASSIGNMENT;
import static de.jplag.kotlin.KotlinTokenConstants.BREAK;
import static de.jplag.kotlin.KotlinTokenConstants.CATCH;
import static de.jplag.kotlin.KotlinTokenConstants.CATCH_BODY_END;
import static de.jplag.kotlin.KotlinTokenConstants.CATCH_BODY_START;
import static de.jplag.kotlin.KotlinTokenConstants.CLASS_BODY_BEGIN;
import static de.jplag.kotlin.KotlinTokenConstants.CLASS_BODY_END;
import static de.jplag.kotlin.KotlinTokenConstants.CLASS_DECLARATION;
import static de.jplag.kotlin.KotlinTokenConstants.COMPANION_DECLARATION;
import static de.jplag.kotlin.KotlinTokenConstants.CONSTRUCTOR;
import static de.jplag.kotlin.KotlinTokenConstants.CONTINUE;
import static de.jplag.kotlin.KotlinTokenConstants.CONTROL_STRUCTURE_BODY_END;
import static de.jplag.kotlin.KotlinTokenConstants.CONTROL_STRUCTURE_BODY_START;
import static de.jplag.kotlin.KotlinTokenConstants.CREATE_OBJECT;
import static de.jplag.kotlin.KotlinTokenConstants.DO_WHILE_EXPRESSION_END;
import static de.jplag.kotlin.KotlinTokenConstants.DO_WHILE_EXPRESSION_START;
import static de.jplag.kotlin.KotlinTokenConstants.ENUM_CLASS_BODY_BEGIN;
import static de.jplag.kotlin.KotlinTokenConstants.ENUM_CLASS_BODY_END;
import static de.jplag.kotlin.KotlinTokenConstants.ENUM_ENTRY;
import static de.jplag.kotlin.KotlinTokenConstants.FILE_END;
import static de.jplag.kotlin.KotlinTokenConstants.FINALLY;
import static de.jplag.kotlin.KotlinTokenConstants.FINALLY_BODY_END;
import static de.jplag.kotlin.KotlinTokenConstants.FINALLY_BODY_START;
import static de.jplag.kotlin.KotlinTokenConstants.FOR_EXPRESSION_BEGIN;
import static de.jplag.kotlin.KotlinTokenConstants.FOR_EXPRESSION_END;
import static de.jplag.kotlin.KotlinTokenConstants.FUNCTION;
import static de.jplag.kotlin.KotlinTokenConstants.FUNCTION_BODY_BEGIN;
import static de.jplag.kotlin.KotlinTokenConstants.FUNCTION_BODY_END;
import static de.jplag.kotlin.KotlinTokenConstants.FUNCTION_INVOCATION;
import static de.jplag.kotlin.KotlinTokenConstants.FUNCTION_LITERAL_BEGIN;
import static de.jplag.kotlin.KotlinTokenConstants.FUNCTION_LITERAL_END;
import static de.jplag.kotlin.KotlinTokenConstants.FUNCTION_PARAMETER;
import static de.jplag.kotlin.KotlinTokenConstants.GETTER;
import static de.jplag.kotlin.KotlinTokenConstants.IF_EXPRESSION_END;
import static de.jplag.kotlin.KotlinTokenConstants.IF_EXPRESSION_START;
import static de.jplag.kotlin.KotlinTokenConstants.IMPORT;
import static de.jplag.kotlin.KotlinTokenConstants.INITIALIZER;
import static de.jplag.kotlin.KotlinTokenConstants.INITIALIZER_BODY_END;
import static de.jplag.kotlin.KotlinTokenConstants.INITIALIZER_BODY_START;
import static de.jplag.kotlin.KotlinTokenConstants.OBJECT_DECLARATION;
import static de.jplag.kotlin.KotlinTokenConstants.PACKAGE;
import static de.jplag.kotlin.KotlinTokenConstants.PROPERTY_DECLARATION;
import static de.jplag.kotlin.KotlinTokenConstants.RETURN;
import static de.jplag.kotlin.KotlinTokenConstants.SEPARATOR_TOKEN;
import static de.jplag.kotlin.KotlinTokenConstants.SETTER;
import static de.jplag.kotlin.KotlinTokenConstants.THROW;
import static de.jplag.kotlin.KotlinTokenConstants.TRY_BODY_END;
import static de.jplag.kotlin.KotlinTokenConstants.TRY_BODY_START;
import static de.jplag.kotlin.KotlinTokenConstants.TRY_EXPRESSION;
import static de.jplag.kotlin.KotlinTokenConstants.TYPE_PARAMETER;
import static de.jplag.kotlin.KotlinTokenConstants.VARIABLE_DECLARATION;
import static de.jplag.kotlin.KotlinTokenConstants.WHEN_CONDITION;
import static de.jplag.kotlin.KotlinTokenConstants.WHEN_EXPRESSION_END;
import static de.jplag.kotlin.KotlinTokenConstants.WHEN_EXPRESSION_START;
import static de.jplag.kotlin.KotlinTokenConstants.WHILE_EXPRESSION_END;
import static de.jplag.kotlin.KotlinTokenConstants.WHILE_EXPRESSION_START;

import de.jplag.Token;

public class KotlinToken extends Token {

    public KotlinToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    @Override
    public String type2string() {
        return switch (type) {
            case FILE_END -> "<EOF>";
            case SEPARATOR_TOKEN -> "-----";
            case PACKAGE -> "PACKAGE";
            case IMPORT -> "IMPORT";
            case CLASS_DECLARATION -> "CLASS";
            case OBJECT_DECLARATION -> "OBJECT";
            case COMPANION_DECLARATION -> "COMPANION";
            case TYPE_PARAMETER -> "<T>";
            case CONSTRUCTOR -> "CONSTRUCTOR";
            case CLASS_BODY_BEGIN -> "CLASS{";
            case CLASS_BODY_END -> "}CLASS";
            case ENUM_CLASS_BODY_BEGIN -> "ENUM{";
            case ENUM_CLASS_BODY_END -> "}ENUM";
            case PROPERTY_DECLARATION -> "PROPERTY";
            case INITIALIZER -> "INIT";
            case INITIALIZER_BODY_START -> "INIT{";
            case INITIALIZER_BODY_END -> "}INIT";
            case FUNCTION -> "FUN";
            case GETTER -> "GET";
            case SETTER -> "SET";
            case FUNCTION_PARAMETER -> "PARAM";
            case FUNCTION_BODY_BEGIN -> "FUN{";
            case FUNCTION_BODY_END -> "}FUN";
            case FUNCTION_LITERAL_BEGIN -> "FUNC_LIT{";
            case FUNCTION_LITERAL_END -> "}FUNC_LIT";
            case FOR_EXPRESSION_BEGIN -> "FOR";
            case FOR_EXPRESSION_END -> "}FOR";
            case IF_EXPRESSION_START -> "IF";
            case IF_EXPRESSION_END -> "}IF(-ELSE)";
            case WHILE_EXPRESSION_START -> "WHILE";
            case WHILE_EXPRESSION_END -> "}WHILE";
            case DO_WHILE_EXPRESSION_START -> "DO";
            case DO_WHILE_EXPRESSION_END -> "}DO-WHILE";
            case TRY_EXPRESSION -> "TRY";
            case TRY_BODY_START -> "TRY{";
            case TRY_BODY_END -> "}TRY";
            case CATCH -> "CATCH";
            case CATCH_BODY_START -> "CATCH{";
            case CATCH_BODY_END -> "}CATCH";
            case FINALLY -> "FINALLY";
            case FINALLY_BODY_START -> "FINALLY{";
            case FINALLY_BODY_END -> "}FINALLY";
            case WHEN_EXPRESSION_START -> "WHEN";
            case WHEN_EXPRESSION_END -> "}WHEN";
            case WHEN_CONDITION -> "COND";
            case CONTROL_STRUCTURE_BODY_START -> "THEN{";
            case CONTROL_STRUCTURE_BODY_END -> "}THEN";
            case VARIABLE_DECLARATION -> "VARDECL";
            case ENUM_ENTRY -> "ENUM_ENTRY";
            case FUNCTION_INVOCATION -> "INVOC";
            case CREATE_OBJECT -> "CONST";
            case ASSIGNMENT -> "ASSIGN";
            case THROW -> "THROW";
            case RETURN -> "RETURN";
            case CONTINUE -> "CONTINUE";
            case BREAK -> "BREAK";
            default -> "<UNKNOWN%d>".formatted(type);
        };
    }
}
