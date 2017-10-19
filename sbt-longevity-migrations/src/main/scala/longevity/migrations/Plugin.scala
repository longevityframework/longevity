package longevity.migrations

import sbt._
import sbt.Keys._
import sbt.complete.DefaultParsers._

object Plugin extends AutoPlugin {

  object autoImport {
    val modelPackage        = settingKey[String]("the package containing your model classes")
    val modelSourceDir      = settingKey[File]("the directory containing your model source files")
    val migrationsPackage   = settingKey[String]("the package containing migrations for your domain model")
    val migrationsSourceDir = settingKey[File]("the directory containing your migrations source files")
    val createVersionTag    = inputKey[Unit]("tags the current version of your domain model")
    val migrateSchema       = inputKey[Unit]("migrates data to the latest version of your domain model")
  }

  import autoImport._
  override def trigger = allRequirements

  override lazy val projectSettings = Seq(
    modelSourceDir      := pathForPackageName((scalaSource in Compile).value, modelPackage.value),
    migrationsSourceDir := pathForPackageName((scalaSource in Compile).value, migrationsPackage.value),
    createVersionTask,
    migrateSchemaTask)

  lazy val createVersionTask = {
    val versionParser = OptSpace ~> NotSpace
    createVersionTag := {
      val version = versionParser.parsed
      Tagger.tag(
        modelSourceDir.value.toPath,
        modelPackage.value,
        (migrationsSourceDir.value / version).toPath,
        s"${migrationsPackage.value}.$version")
    }
  }

  private def pathForPackageName(sourceDir: File, packageName: String): File = {
    val packageParts = packageName.split('.')
    packageParts.foldLeft(sourceDir) { (path, pack) => path / pack }
  }

  val rawParser = any.*.map(_.mkString)
  lazy val migrateSchemaTask = migrateSchema := Def.inputTaskDyn {
    val args = s" longevity.migrations.Migrator ${migrationsPackage.value} ${rawParser.parsed}"
    Def.taskDyn((runMain in Compile).toTask(args))
  }.evaluated

}
