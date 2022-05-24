package de.jplag.R;

public class RToken extends de.jplag.Token implements RTokenConstants {
    private static final long serialVersionUID = 1L;
    private int line, column, length;

    public RToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getLength() {
        return length;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String type2string() {
        switch (this.type) {
            case FILE_END:
                return "***********";
            case SEPARATOR_TOKEN:
                return "METHOD_SEPARATOR";
            case BEGIN_FUNCTION:
                return "FUNCTION{  ";
            case END_FUNCTION:
                return "}FUNCTION  ";
            case FUNCTION_CALL:
                return "FUNCTION() ";
            case NUMBER:
                return "NUMBER     ";
            case STRING:
                return "STRING     ";
            case BOOL:
                return "BOOL       ";
            case ASSIGN:
                return "ASSIGN     ";
            case ASSIGN_FUNC:
                return "ASSIGN_FUNC";
            case ASSIGN_LIST:
                return "ASSIGN_LIST";
            case HELP:
                return "HELP       ";
            case INDEX:
                return "INDEX      ";
            case PACKAGE:
                return "PACKAGE    ";
            case IF_BEGIN:
                return "IF{        ";
            case IF_END:
                return "}IF        ";
            case FOR_BEGIN:
                return "FOR{       ";
            case FOR_END:
                return "}FOR       ";
            case WHILE_BEGIN:
                return "WHILE{     ";
            case WHILE_END:
                return "}WHILE     ";
            case REPEAT_BEGIN:
                return "REPEAT{    ";
            case REPEAT_END:
                return "}REPEAT    ";
            case NEXT:
                return "NEXT       ";
            case BREAK:
                return "BREAK      ";
            case COMPOUND_BEGIN:
                return "COMPOUND{   ";
            case COMPOUND_END:
                return "}COMPOUND   ";
            default:
                System.err.println("*UNKNOWN: " + type);
                return "*UNKNOWN" + type;
        }
    }

}
