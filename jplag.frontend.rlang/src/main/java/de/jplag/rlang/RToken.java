package de.jplag.rlang;

/**
 * This class represents the occurrence of an R Token in the source code. Based on an R frontend for JPlag v2.15 by Olmo
 * Kramer, see their <a href="https://github.com/CodeGra-de/jplag/tree/master/jplag.frontend.R">JPlag fork</a>.
 */
public class RToken extends de.jplag.Token implements RTokenConstants {

    public RToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    @Override
    public String type2string() {
        return switch (this.type) {
            case FILE_END -> "<EOF>";
            case SEPARATOR_TOKEN -> "METHOD_SEPARATOR";
            case BEGIN_FUNCTION -> "FUNCTION{";
            case END_FUNCTION -> "}FUNCTION";
            case FUNCTION_CALL -> "FUNCTION()";
            case NUMBER -> "NUMBER";
            case STRING -> "STRING";
            case BOOL -> "BOOL";
            case ASSIGN -> "ASSIGN";
            case ASSIGN_FUNC -> "ASSIGN_FUNC";
            case ASSIGN_LIST -> "ASSIGN_LIST";
            case HELP -> "HELP";
            case INDEX -> "INDEX";
            case PACKAGE -> "PACKAGE";
            case IF_BEGIN -> "IF{";
            case IF_END -> "}IF-ELSE";
            case FOR_BEGIN -> "FOR{";
            case FOR_END -> "}FOR";
            case WHILE_BEGIN -> "WHILE{";
            case WHILE_END -> "}WHILE";
            case REPEAT_BEGIN -> "REPEAT{";
            case REPEAT_END -> "}REPEAT";
            case NEXT -> "NEXT";
            case BREAK -> "BREAK";
            case COMPOUND_BEGIN -> "COMPOUND{";
            case COMPOUND_END -> "}COMPOUND";
            default -> "<UNKNOWN%d>".formatted(type);
        };
    }

}
