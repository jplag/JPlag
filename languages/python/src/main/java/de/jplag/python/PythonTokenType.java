package de.jplag.python;

import de.jplag.TokenType;

public enum PythonTokenType implements TokenType {
    // Python 3.6
    IMPORT("IMPORT", 6), // import
    CLASS_BEGIN("CLASS{", 5), // class
    CLASS_END("}CLASS", 5), // class
    METHOD_BEGIN("METHOD{", 3), // def
    METHOD_END("}METHOD", 3), // def
    ASSIGN("ASSIGN", 1), // =
    WHILE_BEGIN("WHILE{", 5), // while
    WHILE_END("}WHILE", 5), // while
    FOR_BEGIN("FOR{", 3), // for
    FOR_END("}FOR", 3), // for
    TRY_BEGIN("TRY{", 3), // try
    TRY_END("}TRY", 3), // try
    EXCEPT_BEGIN("EXCEPT{", 6), // except
    EXCEPT_END("}EXCEPT", 6), // except
    FINALLY_BEGIN("FINALLY{", 7), // finally
    FINALLY_END("}FINALLY", 7), // finally
    IF_BEGIN("IF{", 2), // if
    IF_END("}IF", 2), // if
    APPLY("APPLY", -1), // function call (dynamic length)
    BREAK("BREAK", 5), // break
    CONTINUE("CONTINUE", 8), // continue
    RETURN("RETURN", 6), // return
    RAISE("RAISE", 5), // raise
    DECORATOR_BEGIN("DECOR{", 1), // @
    DECORATOR_END("}DECOR", 1), // @
    LAMBDA("LAMBDA", 6), // lambda
    ASSERT("ASSERT", 6), // assert
    YIELD("YIELD", 5), // yield
    DEL("DEL", 3), // del
    WITH_BEGIN("WITH{", 4), // with
    WITH_END("}WITH", 4), // with
    ASYNC("ASYNC", 5), // async
    AWAIT("AWAIT", 5), // await
    PASS("PASS", 4), // pass
    GLOBAL("GLOBAL", 6), // global
    NONLOCAL("NONLOCAL", 8), // nonlocal
    LIST("LIST", -1), // list literal (dynamic length)
    SET("SET", -1), // set literal (dynamic length)
    DICTIONARY("DICTIONARY", -1), // dict literal (dynamic length)

    // Python 3.8
    NAMED_EXPR("NAMED", 2), // := (PEP 572)

    // Python 3.10
    MATCH_BEGIN("MATCH{", 5), // match
    MATCH_END("}MATCH", 5), // match
    CASE("CASE", 4), // case

    // Python 3.11
    EXCEPT_GROUP_BEGIN("EXCEPT*{", 7), // except*
    EXCEPT_GROUP_END("}EXCEPT*", 7), // except*

    // Python 3.12
    TYPE_ALIAS("TYPE", 4); // type (PEP 695)

    private final String description;
    private final int length;

    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets the length of this token type.
     * @return The length of the token, or -1 if the length is dynamic and needs to be calculated
     */
    public int getLength() {
        return this.length;
    }

    PythonTokenType(String description, int length) {
        this.description = description;
        this.length = length;
    }
}
