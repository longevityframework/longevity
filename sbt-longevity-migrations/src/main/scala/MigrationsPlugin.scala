package longevity

import sbt._
import Keys._

object MigrationsPlugin extends AutoPlugin {

  object autoImport {
    val migrationsModelPackage = settingKey[String]("the package containing your model classes")
    val migrationsModelSourceDirectory = settingKey[File]("the directory containing your model source files")
    val migrationsPackage = settingKey[String]("the package containing migrations for your model")
    val migrationsSourceDirectory = settingKey[File]("the directory containing your migrations source files")
    val migrationsCreateVersionTag = taskKey[Unit]("tags the current version of your model")
  }

  import autoImport._
  override def trigger = allRequirements

  override lazy val projectSettings = Seq(
    migrationsModelSourceDirectory := pathForPackageName(
      (scalaSource in Compile).value,
      migrationsModelPackage.value),
    migrationsSourceDirectory := pathForPackageName(
      (scalaSource in Compile).value,
      migrationsPackage.value),
    migrationsCreateVersionTag := migrationsCreateVersionTask.value)

  lazy val migrationsCreateVersionTask =
    Def.task {
      println(s"migrationsModelPackage         ${migrationsModelPackage.value}")
      println(s"migrationsModelSourceDirectory ${migrationsModelSourceDirectory.value}")
      println(s"migrationsPackage              ${migrationsPackage.value}")
      println(s"migrationsSourceDirectory      ${migrationsSourceDirectory.value}")
    }

  private def pathForPackageName(sourceDir: File, packageName: String): File = {
    val packageParts = packageName.split('.')
    println(s"packageParts ${packageParts.toList}")
    packageParts.foldLeft(sourceDir) { (path, pack) => path / pack }
  }

}
