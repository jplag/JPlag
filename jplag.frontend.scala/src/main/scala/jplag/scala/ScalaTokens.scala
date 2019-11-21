package jplag.scala

object ScalaTokens extends Enumeration with jplag.TokenConstants {
  val FileEnd = Value(jplag.TokenConstants.FILE_END)
  val SeperatorToken = Value(jplag.TokenConstants.SEPARATOR_TOKEN)
  val Package = Value
  val Import = Value
  val ClassBegin = Value
  val ClassEnd = Value
  val MethodBegin = Value
  val MethodEnd = Value
  val Vardef = Value
  val DoBegin = Value
  val DoEnd = Value
  val WhileBegin = Value
  val WhileEnd = Value
  val ForBegin = Value
  val ForEnd = Value
  val Case = Value
  val TryBegin = Value
  val CatchBegin = Value
  val CatchEnd = Value
  val Finally = Value
  val IfBegin = Value
  val Else = Value
  val IfEnd = Value
  val Return = Value
  val Throw = Value
  val NewCreationBegin = Value
  val NewCreationEnd = Value
  val Apply = Value
  val Assign = Value
  val TraitBegin = Value
  val TraitEnd = Value
  val ConstructorBegin = Value
  val ConstructorEnd = Value
  val InitBegin = Value
  val InitEnd = Value
  val ArrayInitBegin = Value
  val ArrayInitEnd = Value
  val MatchBegin = Value
  val MatchEnd = Value
  val Guard = Value
  val Anno = Value
  val AnnoMarker = Value
  val AnnoMBegin = Value
  val AnnoMEnd = Value
  val AnnoTBegin = Value
  val AnnoTEnd = Value
  val AnnoCBegin = Value
  val AnnoCEnd = Value
  val ObjectBegin = Value
  val ObjectEnd = Value
  val MacroBegin = Value
  val MacroEnd = Value
  val Type = Value

  val FunctionBegin = Value
  val FunctionEnd = Value
  val PartialFunctionBegin = Value
  val PartialFunctionEnd = Value

  val Yield = Value
}

class ScalaToken(t: Integer,
                 f: java.lang.String,
                 var line: Int,
                 var column: Int,
                 var length: Int)
  extends jplag.Token(t, f, line, column, length) {

  def this(node: scala.meta.Tree, fromEnd: Boolean, file: String, typ: ScalaTokens.Value) {
    this(
      typ.id,
      file,
      (if (fromEnd) node.pos.endLine else node.pos.startLine) + 1,
      (if (fromEnd) node.pos.endColumn else node.pos.startColumn) + 1,
      node.toString().length
    )
  }

  override def toString: String = {
    try {
      ScalaTokens(`type`).toString
    } catch {
      case _: Throwable => "Unknown!"
    }
  }

  override protected def setLine(line: Int): Unit = this.line = line

  override protected def setColumn(column: Int): Unit = this.column = column

  override protected def setLength(length: Int): Unit = this.length = length

  override def getLine: Int = this.line

  override def getColumn: Int = this.column

  override def getLength: Int = this.length

  override def numberOfTokens: Int = ScalaTokens.maxId
}