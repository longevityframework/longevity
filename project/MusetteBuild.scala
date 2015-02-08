import sbt._
import Keys._

trait BuildSettings {

  val githubUrl = "https://github.com/sullivan-/musette"

  val buildSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "net.jsmscs",
    scalaVersion := "2.11.5",

    // compile
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked"),

    // scaladoc
    scalacOptions in (Compile, doc) ++= Seq("-groups", "-implicits"),
    scalacOptions in (Compile, doc) ++= Seq("-doc-title", (name in (Compile, doc)).value + " API"),
    scalacOptions in (Compile, doc) <++= (baseDirectory in LocalProject("root"), version) map { (bd, v) =>
      val tagOrBranch = if (v endsWith "SNAPSHOT") gitHash else ("v" + v)
      Seq("-sourcepath", bd.getAbsolutePath,
          "-doc-source-url", s"$githubUrl/tree/$tagOrBranch€{FILE_PATH}.scala")
    },
    autoAPIMappings := true,

    // test
    logLevel in test := Level.Info, // switch to warn to get less output from scalatest

    // dependencies
    resolvers += "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scalatest" %% "scalatest" % "2.2.1" % "test"))

  private def gitHash = sys.process.Process("git rev-parse HEAD").lines_!.head

}

object MusetteBuild extends Build with BuildSettings {

  lazy val root = Project(
    id = "root",
    base = file("."),
    settings = buildSettings :+ (version := "0.0.0-SNAPSHOT")
  ) aggregate (emblem, longevity, musette)

  lazy val emblem = Project(
    id = "emblem",
    base = file("emblem"),
    settings = buildSettings :+ (version := "0.0.0-SNAPSHOT")
  )

  lazy val longevity = Project(
    id = "longevity",
    base = file("longevity"),
    settings = buildSettings :+
      (version := "0.0.0-SNAPSHOT") :+
      (libraryDependencies ++= Seq(
        "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23",
        "org.scalatest" %% "scalatest" % "2.2.1" % "provided"))
  ) dependsOn (emblem)

  lazy val musette = Project(
    id = "musette",
    base = file("musette"),
    settings = buildSettings :+
      (version := "0.0.0-SNAPSHOT") :+
      (libraryDependencies += "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23")
  ) dependsOn (emblem, longevity)
  // in the future, this dependsOn emblem may go away

}
