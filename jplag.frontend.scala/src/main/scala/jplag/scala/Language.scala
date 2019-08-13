package jplag.scala

import java.io.File

import jplag.ProgramI

class Language(val program: ProgramI) extends jplag.Language {
  private val parser = Parser
  this.parser.setProgram(program)

  override def suffixes: Array[String] = {
    Array(".scala")
  }

  override def errorsCount: Int = this.parser.errorsCount

  override def name = "Scala parser"

  override def getShortName = "scala"

  override def min_token_match = 8

  override def parse(dir: File, files: Array[String]): jplag.Structure = this.parser.parse(dir, files)

  override def errors: Boolean = this.parser.getErrors

  override def supportsColumns = true

  override def isPreformated = true

  override def usesIndex = false

  override def noOfTokens: Int = jplag.scala.ScalaTokens.maxId

  override def type2string(typ: Int): String = jplag.scala.ScalaTokens(typ).toString
}
