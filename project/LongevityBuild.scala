import sbt._
import Keys._

trait BuildSettings {

  val githubUrl = "https://github.com/sullivan-/longevity"

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

  val buildSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "net.jsmscs",
    scalaVersion := "2.11.7",

    // compile
    scalacOptions ++= nonConsoleScalacOptions ++ otherScalacOptions,

    // console
    scalacOptions in (Compile, console) ~= (_ filterNot (nonConsoleScalacOptions.contains(_))),
    scalacOptions in (Test, console) ~= (_ filterNot (nonConsoleScalacOptions.contains(_))),

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
    resolvers += Resolver.typesafeRepo("releases"),
    libraryDependencies += ("org.scala-lang" % "scala-reflect" % scalaVersion.value),
    libraryDependencies += ("org.scala-lang.modules" %% "scala-async" % "0.9.2"),
    libraryDependencies += ("com.github.nscala-time" %% "nscala-time" % "1.0.0"),
    libraryDependencies += ("org.scalatest" %% "scalatest" % "2.2.1" % "test"),

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

  lazy val root = Project(
    id = "root",
    base = file("."),
    settings = buildSettings :+ (version := "0.0-SNAPSHOT")
  ) aggregate (emblem, longevity, musette)

  lazy val emblem = Project(
    id = "emblem",
    base = file("emblem"),
    settings = buildSettings ++ Seq(
      version := "0.1-SNAPSHOT",
      homepage := Some(url("https://github.com/sullivan-/emblem")),
      pomExtra := (
        <scm>
          <url>git@github.com:sullivan-/emblem.git</url>
          <connection>scm:git:git@github.com:sullivan-/emblem.git</connection>
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

  lazy val longevity = Project(
    id = "longevity",
    base = file("longevity"),
    settings = buildSettings ++ Seq(
      version := "0.2-SNAPSHOT",
      libraryDependencies += "org.mongodb" %% "casbah" % "2.8.2",
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % Optional,
      homepage := Some(url("https://github.com/sullivan-/emblem")),
      pomExtra := (
        <scm>
          <url>git@github.com:sullivan-/longevity.git</url>
          <connection>scm:git:git@github.com:sullivan-/longevity.git</connection>
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
  .dependsOn(emblem)

  lazy val musette = Project(
    id = "musette",
    base = file("musette"),
    settings = buildSettings :+ (version := "0.0-SNAPSHOT")
  ) dependsOn (emblem, longevity)
  // in the future, this dependsOn emblem may go away

}
