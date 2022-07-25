package de.jplag.scala

import de.jplag.{ErrorConsumer, TokenList}

import java.io.File


class Language(val consumer: ErrorConsumer) extends de.jplag.Language {
  private val parser = new Parser(consumer)

  override def suffixes: Array[String] = {
    Array(".scala")
  }

  override def getName = "Scala parser"

  override def getShortName = "scala"

  override def minimumTokenMatch = 8

  override def parse(dir: File, files: Array[String]): TokenList = this.parser.parse(dir, files)

  override def hasErrors: Boolean = this.parser.hasErrors

  override def supportsColumns = true

  override def isPreformatted = true

  override def usesIndex = false

  override def numberOfTokens: Int = ScalaTokenConstants.maxId
}
