import sbt._
import Keys._

trait BuildSettings {

  val githubUrl = "https://github.com/sullivan-/musette"

  val buildSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "net.jsmscs",
    scalaVersion := "2.11.5",

    // compile
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-unchecked",
      "-Xfatal-warnings",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-numeric-widen",
      "-Xfuture",
      "-Ywarn-unused-import"),

    // scaladoc
    scalacOptions in (Compile, doc) ++= Seq("-groups", "-implicits", "-encoding", "UTF-8"),
    scalacOptions in (Compile, doc) ++= {
      val projectName = (name in (Compile, doc)).value
      val projectVersion = (version in (Compile, doc)).value
      Seq("-doc-title", s"$projectName $projectVersion API")
    },
    scalacOptions in (Compile, doc) <++= (baseDirectory in LocalProject("root"), version) map { (bd, v) =>
      val tagOrBranch = if (v endsWith "SNAPSHOT") gitHash else ("v" + v)
      Seq("-sourcepath", bd.getAbsolutePath,
          "-doc-source-url", s"$githubUrl/tree/$tagOrBranch€{FILE_PATH}.scala")
    },
    autoAPIMappings := true,
    apiMappings += (scalaInstance.value.libraryJar ->
                    url(s"http://www.scala-lang.org/api/${scalaVersion.value}/")),

    // test
    logLevel in test := Level.Info, // switch to warn to get less output from scalatest
    testOptions in Test += Tests.Argument("-oF"),

    // dependencies
    resolvers += "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
    libraryDependencies += ("org.scala-lang" % "scala-reflect" % scalaVersion.value),
    libraryDependencies += ("org.scalatest" %% "scalatest" % "2.2.1" % "test")
  )

  private def gitHash = sys.process.Process("git rev-parse HEAD").lines_!.head

}

object MusetteBuild extends Build with BuildSettings {

  lazy val root = Project(
    id = "root",
    base = file("."),
    settings = buildSettings :+ (version := "0.0-SNAPSHOT")
  ) aggregate (emblem, longevity, musette)

  lazy val emblem = Project(
    id = "emblem",
    base = file("emblem"),
    settings = buildSettings :+ (version := "0.0-SNAPSHOT")
  )

  lazy val longevity = Project(
    id = "longevity",
    base = file("longevity"),
    settings = buildSettings :+
      (version := "0.0-SNAPSHOT") :+
      (libraryDependencies += "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23") :+
      (libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "provided")
  )
  .dependsOn(emblem)
  .configs(UnitTest, IntegrationTest, MasterIntegrationTest)
  .settings(inConfig(UnitTest)(Defaults.testTasks): _*)
  .settings(testOptions in UnitTest := Seq(Tests.Argument("-n", "longevity.UnitTest")))
  .settings(inConfig(IntegrationTest)(Defaults.testTasks): _*)
  .settings(testOptions in IntegrationTest := Seq(Tests.Argument("-n", "longevity.IntegrationTest")))
  .settings(inConfig(MasterIntegrationTest)(Defaults.testTasks): _*)
  .settings(testOptions in MasterIntegrationTest := Seq(Tests.Argument("-n", "longevity.MasterIntegrationTest")))

  lazy val UnitTest = config("unit") extend (Test)
  lazy val IntegrationTest = config("integration") extend (Test)
  lazy val MasterIntegrationTest = config("master") extend (Test)

  lazy val musette = Project(
    id = "musette",
    base = file("musette"),
    settings = buildSettings :+
      (version := "0.0-SNAPSHOT") :+
      (libraryDependencies += "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23")
  ) dependsOn (emblem, longevity)
  // in the future, this dependsOn emblem may go away

}
