package de.jplag.scala;

import de.jplag.TokenType;

public enum ScalaTokenType implements TokenType {
    Package("PACKAGE"),
    Import("IMPORT"),
    ClassBegin("CLASS{"),
    ClassEnd("}CLASS"),
    MethodDef("METHOD"),
    MethodBegin("METHOD{"),
    MethodEnd("}METHOD"),
    VariableDefinition("VAR_DEF"),
    DoWhile("DO-WHILE"),
    DoWhileEnd("END-DO-WHILE"),
    DoBodyBegin("DO{"),
    DoBodyEnd("}DO"),
    While("WHILE"),
    WhileBodyBegin("WHILE{"),
    WhileBodyEnd("}WHILE"),
    For("FOR"),
    ForBodyBegin("FOR{"),
    ForBodyEnd("}FOR"),
    CaseStatement("CASE"),
    CaseBegin("CASE{"),
    CaseEnd("}CASE"),
    TryBegin("TRY{"),
    CatchBegin("CATCH{"),
    CatchEnd("}CATCH"),
    Finally("FINALLY"),
    If("IF"),
    IfBegin("IF{"),
    IfEnd("}IF"),
    Else("ELSE"),
    ElseBegin("ELSE{"),
    ElseEnd("}ELSE"),
    Return("RETURN"),
    Throw("THROW"),
    NewCreationBegin("NEW{"),
    NewCreationEnd("}NEW"),
    Apply("APPLY"),
    Assign("ASSIGN"),
    TraitBegin("TRAIT{"),
    TraitEnd("}TRAIT"),
    ConstructorBegin("CONSTR{"),
    ConstructorEnd("}CONSTR"),
    MatchBegin("MATCH{"),
    MatchEnd("}MATCH"),
    Guard("GUARD"),
    ObjectBegin("OBJECT{"),
    ObjectEnd("}OBJECT"),
    Macro("MACRO"),
    MacroBegin("MACRO{"),
    MacroEnd("}MACRO"),
    Type("TYPE"),

    FunctionBegin("FUNC{"),
    FunctionEnd("}FUNC"),
    PartialFunctionBegin("PFUNC{"),
    PartialFunctionEnd("}PFUNC"),

    Yield("YIELD"),

    Parameter("PARAM"),
    Argument("ARG"),
    NewObject("NEW(),"),
    SelfType("SELF"),
    TypeParameter("T_PARAM"),
    TypeArgument("T_ARG"),
    BlockStart("{"),
    BlockEnd("}"),
    EnumGenerator("ENUMERATE"),
    Member("MEMBER");

    private final String description;

    public String getDescription() {
        return description;
    }

    private ScalaTokenType(String description) {
        this.description = description;
    }
}
