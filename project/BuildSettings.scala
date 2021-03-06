import sbt._
import Keys._
import com.typesafe.sbt.pgp.PgpKeys._

object BuildSettings {

  val commonSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "org.longevityframework",
    version := "0.28-SNAPSHOT",
    scalaVersion := Dependencies.scalaVersionString,
    crossScalaVersions := Seq("2.11.12", Dependencies.scalaVersionString))

  val publishSettings = commonSettings ++ Seq(
    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    homepage := Some(url("http://longevityframework.org/")),
    scmInfo  := Some(ScmInfo(
      url("https://github.com/longevityframework/longevity.git"),
      "scm:git:git@github.com:longevityframework/longevity.git")),
    developers := List(Developer(
      id    = "sullivan-",
      name  = "John Sullivan",
      email = "John Sullivan MSCS at gmail",
      url   = url("https://github.com/sullivan-"))))

  val buildSettings = publishSettings ++ Seq(
    scalacOptions ++= compileScalacOptions,

    // scaladoc
    scalacOptions in (Compile, doc) ++= Seq("-groups", "-implicits", "-encoding", "UTF-8", "-diagrams"),
    scalacOptions in (Compile, doc) ++= {
      val projectName = (name in (Compile, doc)).value
      val projectVersion = (version in (Compile, doc)).value
      Seq("-doc-title", s"$projectName $projectVersion API")
    },
    scalacOptions in (Compile, doc) ++= {
      val bd = (baseDirectory in LocalProject("longevity")).value
      val v = version.value

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
    // to run a single tag in sbt:
    // test-only longevity.integration.subdomain.allAttributes.AllAttributesSpec -- -n Create

    // common dependencies
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    libraryDependencies += Dependencies.nScalaTimeDep,
    libraryDependencies += Dependencies.scalaTestDep % Test

  )

  val noPublishSettings = commonSettings ++ Seq(
    packagedArtifacts := Map.empty,
    publish := ((): Unit),
    publishSigned := ((): Unit))

  private def githubUrl = "https://github.com/longevityframework/longevity"

  private def compileScalacOptions = Seq(
    "-Xfuture",
    "-Yno-adapted-args",
    "-Ypartial-unification",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused-import",
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked")

  private def gitHash = sys.process.Process("git rev-parse HEAD").lineStream_!.head

}
