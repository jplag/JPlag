package de.jplag.scala

import de.jplag.Token

import java.io.File
import scala.collection.JavaConverters._

class Language extends de.jplag.Language {
  private val parser = new Parser
  private final val fileExtensions = Array(".scala", ".sc")

  override def suffixes: Array[String] = fileExtensions

  override def getName = "Scala parser"

  override def getShortName = "scala"

  override def minimumTokenMatch = 8

  override def parse(dir: File, files: Array[String]): java.util.List[Token] = this.parser.parse(dir, files).asJava

  override def hasErrors: Boolean = this.parser.hasErrors

}
