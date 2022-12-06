package de.jplag.cpp2;

import de.jplag.TokenType;

public enum CPPTokenType implements TokenType {
    C_CLASS_BEGIN("CLASS{"),
    C_CLASS_END("}CLASS"),
    C_BLOCK_BEGIN("BLOCK{"),
    C_BLOCK_END("}BLOCK"),
    C_QUESTIONMARK("COND"),
    C_ELLIPSIS("..."),
    C_ASSIGN("ASSIGN"),
    C_DOT("DOT"),
    C_ARROW("ARROW"),
    C_ARROWSTAR("ARROWSTAR"),
    C_AUTO("AUTO"),
    C_BREAK("BREAK"),
    C_CASE("CASE"),
    C_CATCH("CATCH"),
    C_CHAR("CHAR"),
    C_CONST("CONST"),
    C_CONTINUE("CONTINUE"),
    C_DEFAULT("DEFAULT"),
    C_DELETE("DELETE"),
    C_DO("DO"),
    C_DOUBLE("DOUBLE"),
    C_ELSE("ELSE"),
    C_ENUM("ENUM"),
    C_EXTERN("EXTERN"),
    C_FLOAT("FLOAT"),
    C_FOR("FOR"),
    C_FRIEND("FRIEND"),
    C_GOTO("GOTO"),
    C_IF("IF"),
    C_INLINE("INLINE"),
    C_INT("INT"),
    C_LONG("LONG"),
    C_NEW("NEW"),
    C_PRIVATE("PRIVATE"),
    C_PROTECTED("PROTECTED"),
    C_PUBLIC("PUBLIC"),
    C_REDECLARED("REDECLARED"),
    C_REGISTER("REGISTER"),
    C_RETURN("RETURN"),
    C_SHORT("SHORT"),
    C_SIGNED("SIGNED"),
    C_SIZEOF("SIZEOF"),
    C_STATIC("STATIC"),
    C_STRUCT("STRUCT"),
    C_CLASS("CLASS"),
    C_SWITCH("SWITCH"),
    C_TEMPLATE("TEMPLATE"),
    C_THIS("THIS"),
    C_TRY("TRY"),
    C_TYPEDEF("TYPEDEF"),
    C_UNION("UNION"),
    C_UNSIGNED("UNSIGNED"),
    C_VIRTUAL("VIRTUAL"),
    C_VOID("VOID"),
    C_VOLATILE("VOLATILE"),
    C_WHILE("WHILE"),
    C_OPERATOR("OPERATOR"),
    C_THROW("THROW"),
    C_ID("ID"),
    C_FUN("FUN"),
    C_DOTSTAR("DOTSTAR"),
    C_NULL("NULL");

    private final String description;

    public String getDescription() {
        return this.description;
    }

    CPPTokenType(String description) {
        this.description = description;
    }
}
