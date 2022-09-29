package de.jplag.scala

import de.jplag.Token

import java.io.File
import org.kohsuke.MetaInfServices

import java.util
import scala.jdk.CollectionConverters.{SeqHasAsJava, SetHasAsScala}

class Language extends de.jplag.Language {
  private val parser = new Parser
  private final val fileExtensions = Array(".scala", ".sc")

  override def suffixes: Array[String] = fileExtensions

  override def getName = "Scala parser"

  override def getIdentifier = "scala"

  override def minimumTokenMatch = 8

  override def parse(files: util.Set[File]): java.util.List[Token] = this.parser.parse(files.asScala.toSet).asJava
}
