package de.jplag.scala

import de.jplag.Token

import scala.meta.inputs.Position

class ScalaToken(tokenType: Int,
                 file: java.lang.String,
                 var line: Int,
                 var column: Int,
                 var length: Int)
  extends Token(tokenType, file, line, column, length) {

    def this(tokenType: ScalaTokenConstants.Value, file: String, position: Position, fromEnd: Boolean) {
        this(
            tokenType.id,
            file,
            (if (fromEnd) position.endLine else position.startLine) + 1,
            (if (fromEnd) position.endColumn else position.startColumn) + 1,
            if (fromEnd) 0 else position.text.length,
        )
    }

    override def toString: String = {
        try {
            ScalaTokenConstants(`type`).toString
        } catch {
            case _: Throwable => "Unknown!"
        }
    }

    /**
     * @return a string representation depending on the type of the token.
     */
    override protected def type2string(): String = ScalaTokenConstants.apply(tokenType).toString
}
