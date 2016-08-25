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

  val publishSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "org.longevityframework",
    version := "0.10-SNAPSHOT",
    scalaVersion := "2.11.8",

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
    scalacOptions in (Compile, doc) <++= (baseDirectory in LocalProject("longevity"), version) map {
      (bd, v) =>
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
    libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.12.0",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % Test
    
  )

  private def gitHash = sys.process.Process("git rev-parse HEAD").lines_!.head

}

object LongevityBuild extends Build with BuildSettings {

  val akkaStreamDep: ModuleID = "com.typesafe.akka" %% "akka-stream" % "2.4.9"
  val casbahDep: ModuleID = "org.mongodb" %% "casbah" % "3.1.1"
  val cassandraDep: ModuleID = "com.datastax.cassandra" % "cassandra-driver-core" % "3.1.0"
  val json4sDep: ModuleID = "org.json4s" %% "json4s-native" % "3.4.0"

  lazy val longevity = Project(
    id = "longevity",
    base = file("."),
    settings = buildSettings ++ Seq(
      libraryDependencies += "com.typesafe" % "config" % "1.3.0",
      libraryDependencies += "com.github.kxbmap" %% "configs" % "0.4.2",
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % Optional,

      // for streaming api:

      libraryDependencies += akkaStreamDep % Optional,
      libraryDependencies += akkaStreamDep % Test,

      // for mongo:

      libraryDependencies += casbahDep % Optional,
      libraryDependencies += casbahDep % Test,

      // for cassandra:

      libraryDependencies += cassandraDep % Optional,
      libraryDependencies += cassandraDep % Test,
      libraryDependencies += json4sDep % Optional,
      libraryDependencies += json4sDep % Test,

      homepage := longevityHomepage,
      pomExtra := longevityPomExtra
    )
  )
  .aggregate(emblem, longevityMongoDeps, longevityCassandraDeps)
  .dependsOn(emblem)

  lazy val emblem = Project(
    id = "emblem",
    base = file("emblem"),
    settings = buildSettings ++ Seq(
      libraryDependencies += "org.json4s" %% "json4s-native" % "3.3.0" % Optional,
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
        </developers>),
      libraryDependencies += "org.json4s" %% "json4s-native" % "3.3.0" % Optional
    )
  )

  lazy val longevityMongoDeps = Project(
    id = "longevity-mongo-deps",
    base = file("longevity-mongo-deps"),
    settings = publishSettings ++ Seq(
      libraryDependencies += casbahDep,
      homepage := longevityHomepage,
      pomExtra := longevityPomExtra))

  lazy val longevityCassandraDeps = Project(
    id = "longevity-cassandra-deps",
    base = file("longevity-cassandra-deps"),
    settings = publishSettings ++ Seq(
      libraryDependencies += cassandraDep,
      libraryDependencies += json4sDep,
      homepage := longevityHomepage,
      pomExtra := longevityPomExtra))

}
