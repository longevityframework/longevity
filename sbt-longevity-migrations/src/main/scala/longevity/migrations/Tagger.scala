package longevity.migrations

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import scala.collection.JavaConverters.asJavaIterableConverter
import scala.collection.JavaConverters.iterableAsScalaIterableConverter

object Tagger {

  def tag(modelDir: Path, modelPackage: String, tagDir: Path, tagPackage: String): Unit = {
    def tagDirParent = tagDir.getParent
    require(Files.exists(modelDir),          s"model directory $modelDir does not exist!")
    require(Files.isDirectory(modelDir),     s"model directory $modelDir is not a directory!")
    require(Files.notExists(tagDir),         s"tag directory $tagDir already exists!")
    require(Files.exists(tagDirParent),      s"tag directory parent $tagDirParent does not exist!")
    require(Files.isDirectory(tagDirParent), s"tag directory parent $tagDirParent is not a directory!")

    Files.createDirectory(tagDir)
    DirTagger(modelPackage, tagPackage).tag(modelDir, tagDir)
  }

  private case class DirTagger(sourcePackage: String, targetPackage: String) {

    // what's the right way to select a charset here??
    private val charset = Charset.defaultCharset

    // assumes sourceDir and targetDir both already exist
    final def tag(sourceDir: Path, targetDir: Path): Unit = {
      Files.newDirectoryStream(sourceDir).asScala.foreach { sourceFile =>
        val targetFile = mapFileToDir(sourceFile, targetDir)
        if (Files.isDirectory(sourceFile)) {
          Files.createDirectory(targetFile)
          tag(sourceFile, targetFile)
        } else {
          tagFile(sourceFile, targetFile)
        }
      }
    }

    private def mapFileToDir(file: Path, dir: Path) = dir.resolve(file.getName(file.getNameCount - 1).toString)

    private val lineTagger = LineTagger(sourcePackage, targetPackage)

    // please replace this with some more sophisticated parsing
    private def tagFile(source: Path, target: Path): Unit = {
      val sourceLines = Files.readAllLines(source, charset).asScala
      val targetLines = sourceLines.map(lineTagger.tagLine).asJava
      Files.write(target, targetLines, charset)
    }

  }  
}
