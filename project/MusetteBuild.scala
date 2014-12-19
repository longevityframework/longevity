import sbt._
import Keys._

trait BuildSettings {

  val buildSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "net.jsmscs",
    version := "0.0.0-SNAPSHOT",
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked"),
    scalaVersion := "2.11.4",
    libraryDependencies ++= Seq(
      // TODO: reflect probably not used right now
      "org.scala-lang" % "scala-reflect" % scalaVersion.value withSources() withJavadoc(),
      "org.scalatest" %% "scalatest" % "2.2.1" % "test",
      "org.easymock" % "easymockclassextension" % "3.2" % "test"))

}

object MusetteBuild extends Build with BuildSettings {

  lazy val root = Project(
    id = "root",
    base = file("."),
    settings = buildSettings
  ) aggregate (longevity, musette)

  lazy val longevity = Project(
    id = "longevity",
    base = file("longevity"),
    settings = buildSettings :+ (
      // TODO not currently used
      libraryDependencies += "com.chuusai" %% "shapeless" % "2.0.0" withSources() withJavadoc()))

  lazy val musette = Project(
    id = "musette",
    base = file("musette"),
    settings = buildSettings
  ) dependsOn (longevity)

}
