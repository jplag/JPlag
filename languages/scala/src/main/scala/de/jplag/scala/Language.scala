package de.jplag.scala

import de.jplag.TokenList

import java.io.File

import org.kohsuke.MetaInfServices

class Language extends de.jplag.Language {
  private val parser = new Parser
  private final val fileExtensions = Array(".scala", ".sc")

  override def suffixes: Array[String] = fileExtensions

  override def getName = "Scala parser"

  override def getIdentifier = "scala"

  override def minimumTokenMatch = 8

  override def parse(dir: File, files: Array[String]): TokenList = this.parser.parse(dir, files)

  override def hasErrors: Boolean = this.parser.hasErrors

}
