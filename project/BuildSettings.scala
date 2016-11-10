import sbt._
import Keys._
import com.typesafe.sbt.pgp.PgpKeys._

object BuildSettings {

  val githubUrl = "https://github.com/longevityframework/longevity"

  val nonConsoleScalacOptions = Seq(
    "-Xfatal-warnings")

  val otherScalacOptions = Seq(
    "-Xfuture",
    "-Yno-adapted-args",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused-import",
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked")

  val publishSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "org.longevityframework",
    version := "0.16.0",
    scalaVersion := Dependencies.scalaVersionString,

    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    licenses := Seq("Apache License, Version 2.0" ->
                    url("http://www.apache.org/licenses/LICENSE-2.0"))
  )

  val longevityHomepage = Some(url("http://longevityframework.github.io/longevity/"))

  val longevityPomExtra = (
    <scm>
      <url>git@github.com:longevityframework/longevity.git</url>
      <connection>scm:git:git@github.com:longevityframework/longevity.git</connection>
    </scm>
    <developers>
      <developer>
        <id>sullivan-</id>
        <name>John Sullivan</name>
        <url>https://github.com/sullivan-</url>
      </developer>
    </developers>)

  val buildSettings = publishSettings ++ Seq(

    // compile
    scalacOptions ++= nonConsoleScalacOptions ++ otherScalacOptions,

    // console
    scalacOptions in (Compile, console) ~= (_ filterNot (nonConsoleScalacOptions.contains(_))),
    scalacOptions in (Test, console) ~= (_ filterNot (nonConsoleScalacOptions.contains(_))),

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
    libraryDependencies += Dependencies.scalaReflectDep,
    libraryDependencies += Dependencies.nScalaTimeDep,
    libraryDependencies += Dependencies.scalaTestDep % Test
    
  )

  val noPublishSettings = Defaults.coreDefaultSettings ++ Seq(
    scalaVersion := Dependencies.scalaVersionString,
    packagedArtifacts := Map.empty,
    publishLocal := (),
    publishSigned := (),
    publish := ())

  private def gitHash = sys.process.Process("git rev-parse HEAD").lines_!.head

}
