package de.jplag.scala

import de.jplag.{Token, TokenConstants}

import scala.meta.inputs.Position

object ScalaTokenConstants extends Enumeration with TokenConstants {

  val FileEnd: Value = Value(TokenConstants.FILE_END, "<EOF>")
  val SeparatorToken: Value = Value(TokenConstants.SEPARATOR_TOKEN, "--------")
  val Package: Value = Value("PACKAGE")
  val Import: Value = Value("IMPORT")
  val ClassBegin: Value = Value("CLASS{")
  val ClassEnd: Value = Value("}CLASS")
  val MethodBegin: Value = Value("METHOD{")
  val MethodEnd: Value = Value("}METHOD")
  val VariableDefinition: Value = Value("VAR_DEF")
  val DoBegin: Value = Value("DO{")
  val DoEnd: Value = Value("}DO")
  val WhileBegin: Value = Value("WHILE{")
  val WhileEnd: Value = Value("}WHILE")
  val ForBegin: Value = Value("FOR{")
  val ForEnd: Value = Value("}FOR")
  val CaseBegin: Value = Value("CASE{")
  val CaseEnd: Value = Value("}CASE")
  val TryBegin: Value = Value("TRY{")
  val CatchBegin: Value = Value("CATCH{")
  val CatchEnd: Value = Value("}CATCH")
  val Finally: Value = Value("FINALLY")
  val If: Value = Value("IF")
  val IfBegin: Value = Value("IF{")
  val IfEnd: Value = Value("}IF")
  val Else: Value = Value("ELSE")
  val ElseBegin: Value = Value("ELSE{")
  val ElseEnd: Value = Value("}ELSE")
  val Return: Value = Value("RETURN")
  val Throw: Value = Value("THROW")
  val NewCreationBegin: Value = Value("NEW{")
  val NewCreationEnd: Value = Value("}NEW")
  val Apply: Value = Value("APPLY")
  val Assign: Value = Value("ASSIGN")
  val TraitBegin: Value = Value("TRAIT{")
  val TraitEnd: Value = Value("}TRAIT")
  val ConstructorBegin: Value = Value("CONSTR{")
  val ConstructorEnd: Value = Value("}CONSTR")
  val InitBegin: Value = Value("INIT{")
  val InitEnd: Value = Value("}INIT")
  val ArrayInitBegin: Value = Value("ARRAY(){")
  val ArrayInitEnd: Value = Value("}ARRAY")
  val MatchBegin: Value = Value("MATCH{")
  val MatchEnd: Value = Value("}MATCH")
  val Guard: Value = Value("GUARD")
  val Anno: Value = Value("ANNO")
  val AnnoMarker: Value = Value("ANNO_MARKER")
  val AnnoMBegin: Value = Value("ANNO_M{")
  val AnnoMEnd: Value = Value("}ANNO_M")
  val AnnoTBegin: Value = Value("ANNO_T{")
  val AnnoTEnd: Value = Value("}ANNO_T")
  val AnnoCBegin: Value = Value("ANNO_C{")
  val AnnoCEnd: Value = Value("}ANNO_C")
  val ObjectBegin: Value = Value("OBJECT{")
  val ObjectEnd: Value = Value("}OBJECT")
  val MacroBegin: Value = Value("MACRO{")
  val MacroEnd: Value = Value("}MACRO")
  val Type: Value = Value("TYPE")

  val FunctionBegin: Value = Value("FUNC{")
  val FunctionEnd: Value = Value("}FUNC")
  val PartialFunctionBegin: Value = Value("PFUNC{")
  val PartialFunctionEnd: Value = Value("}PFUNC")

  val Yield: Value = Value("YIELD")

  val Parameter: Value = Value("PARAM")
  val Assignment: Value = Value("ASSIGN")
  val Argument: Value = Value("ARG")
  val NewObject: Value = Value("NEW()")
  val SelfType: Value = Value("SELF")
  val TypeArgument: Value = Value("T_ARG")
  val BlockStart: Value = Value("{")
  val BlockEnd: Value = Value("}")
  val EnumGenerator: Value = Value("ENUMERATE")
}

class ScalaToken(tType: Int,
                 file: java.lang.String,
                 var line: Int,
                 var column: Int,
                 var length: Int)
  extends Token(tType, file, line, column, length) {

  def this(tType: ScalaTokenConstants.Value, file: String, pos: Position, fromEnd: Boolean) {
    this(
      tType.id,
      file,
      (if (fromEnd) pos.endLine else pos.startLine) + 1,
      (if (fromEnd) pos.endColumn else pos.startColumn) + 1,
      pos.text.length
    )
  }

  override def toString: String = {
    try {
      ScalaTokenConstants(`type`).toString
    } catch {
      case _: Throwable => "Unknown!"
    }
  }

  override def setLine(line: Int): Unit = this.line = line

  override def setColumn(column: Int): Unit = this.column = column

  override def setLength(length: Int): Unit = this.length = length

  override def getLine: Int = this.line

  override def getColumn: Int = this.column

  override def getLength: Int = this.length

  def numberOfTokens: Int = ScalaTokenConstants.maxId

  /**
   * @return a string representation depending on the type of the token.
   */
  override protected def type2string(): String = ScalaTokenConstants.apply(tType).toString
}