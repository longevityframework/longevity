import sbt._
import Keys._

trait BuildSettings {

  val buildSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "net.jsmscs",
    version := "0.0.0-SNAPSHOT",
    logLevel in test := Level.Info,
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked"),
    scalaVersion := "2.11.5",
    resolvers += "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scalatest" %% "scalatest" % "2.2.1" % "test"))

}

object MusetteBuild extends Build with BuildSettings {

  lazy val root = Project(
    id = "root",
    base = file("."),
    settings = buildSettings
  ) aggregate (emblem, longevity, musette)

  lazy val emblem = Project(
    id = "emblem",
    base = file("emblem"),
    settings = buildSettings :+ (
      libraryDependencies ++= Seq(
        "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23" % "provided")))

  lazy val longevity = Project(
    id = "longevity",
    base = file("longevity"),
    settings = buildSettings :+ (
      libraryDependencies ++= Seq(
        "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23",
        "org.scalatest" %% "scalatest" % "2.2.1" % "provided"))
  ) dependsOn (emblem)

  lazy val musette = Project(
    id = "musette",
    base = file("musette"),
    settings = buildSettings :+ (
      libraryDependencies ++= Seq(
        "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23"))
  ) dependsOn (emblem, longevity)
  // in the future, this dependsOn emblem may go away

}
