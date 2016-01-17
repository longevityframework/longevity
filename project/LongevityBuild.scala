import sbt._
import Keys._

trait BuildSettings {

  val githubUrl = "https://github.com/longevityframework/longevity"

  val nonConsoleScalacOptions = Seq(
    "-Xfatal-warnings",
    "-Ywarn-unused-import")

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

  // please update these references that contain version number if you up the version:
  //   - https://github.com/longevityframework/emblem/wiki/Setting-up-a-Library-Dependency-on-emblem
  //   - manual/project-setup.md on longevity branch gh-pages
  //   - src/test/scala/longevity/integration/quickStart/QuickStartSpec.scala on longevity master branch

  val buildSettings = Defaults.coreDefaultSettings ++ Seq(

    organization := "org.longevityframework",
    version := "0.5-SNAPSHOT",
    scalaVersion := "2.11.7",

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
    scalacOptions in (Compile, doc) <++= (baseDirectory in LocalProject("longevity"), version) map { (bd, v) =>
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
    resolvers += Resolver.typesafeRepo("releases"),
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    libraryDependencies += "org.scala-lang.modules" %% "scala-async" % "0.9.2",
    libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.0.0",
    libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % Test,

    // publish
    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    licenses := Seq("Apache License, Version 2.0" ->
                    url("http://www.apache.org/licenses/LICENSE-2.0"))
    
  )

  private def gitHash = sys.process.Process("git rev-parse HEAD").lines_!.head

}

object LongevityBuild extends Build with BuildSettings {

  lazy val longevity = Project(
    id = "longevity",
    base = file("."),
    settings = buildSettings ++ Seq(
      libraryDependencies += "com.typesafe" % "config" % "1.3.0",
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % Optional,

      // TODO up dbs & db driver versions

      // for mongo:
      libraryDependencies += "org.mongodb" %% "casbah" % "3.0.0" % Optional,
      libraryDependencies += "org.mongodb" %% "casbah" % "3.0.0" % Test,

      // for cassandra:
      libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "2.2.0-rc3" % Optional,
      libraryDependencies += "org.json4s" %% "json4s-native" % "3.3.0" % Optional,

      homepage := Some(url("http://longevityframework.github.io/longevity/")),
      pomExtra := (
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
    )
  )
  .aggregate(emblem)
  .dependsOn(emblem)

  lazy val emblem = Project(
    id = "emblem",
    base = file("emblem"),
    settings = buildSettings ++ Seq(
      homepage := Some(url("https://github.com/longevityframework/emblem")),
      pomExtra := (
        <scm>
          <url>git@github.com:longevityframework/emblem.git</url>
          <connection>scm:git:git@github.com:longevityframework/emblem.git</connection>
        </scm>
        <developers>
          <developer>
            <id>sullivan-</id>
            <name>John Sullivan</name>
            <url>https://github.com/sullivan-</url>
          </developer>
        </developers>)
    )
  )

}
