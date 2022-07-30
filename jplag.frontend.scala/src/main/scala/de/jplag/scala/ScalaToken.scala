package de.jplag.scala

import de.jplag.Token

import scala.meta.inputs.Position

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
