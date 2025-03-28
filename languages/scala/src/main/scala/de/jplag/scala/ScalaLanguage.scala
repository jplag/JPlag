package de.jplag.scala

import de.jplag.Token

import java.io.File
import org.kohsuke.MetaInfServices

import java.util
import scala.jdk.CollectionConverters.{SeqHasAsJava, SetHasAsScala}

class ScalaLanguage extends de.jplag.Language {
  private final val fileExtensions = Array(".scala", ".sc")

  override def suffixes: Array[String] = fileExtensions

  override def getName = "Scala"

  override def getIdentifier = "scala"

  override def minimumTokenMatch = 8

  override def parse(files: util.Set[File], normalize: Boolean): java.util.List[Token] = new Parser().parse(files.asScala.toSet).asJava
}
