package de.jplag.scala

import de.jplag.Token
import org.kohsuke.MetaInfServices

import java.io.File
import java.util
import scala.jdk.CollectionConverters._

@MetaInfServices
class ScalaLanguage extends de.jplag.Language {

  private final val fileExtensionsList = List(".scala", ".sc")

  override def fileExtensions: util.List[String] =
    fileExtensionsList.asJava

  override def getName: String = "Scala"

  override def getIdentifier: String = "scala"

  override def minimumTokenMatch: Int = 8

  override def parse(files: util.Set[File], normalize: Boolean): util.List[Token] =
    new Parser().parse(files.asScala.toSet).asJava
}
