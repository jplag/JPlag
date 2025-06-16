package de.jplag.scala

import de.jplag.Token
import org.kohsuke.MetaInfServices

import java.io.File
import java.util
import scala.jdk.CollectionConverters._

class ScalaLanguage extends de.jplag.Language {

private final val fileExtensionsList = List(".scala", ".sc")

override def fileExtensions: java.util.List[String] = fileExtensionsList.asJava


  override def getName = "Scala"

  override def getIdentifier = "scala"

  override def minimumTokenMatch = 8

  override def parse(files: util.Set[File], normalize: Boolean): java.util.List[Token] = new Parser().parse(files.asScala.toSet).asJava
}