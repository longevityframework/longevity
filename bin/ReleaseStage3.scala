// longevity/bin/ReleaseStage3.scala

// covers step 3 in this doc:
// https://docs.google.com/document/d/1RisEp9o0825YJaYjKu4AM9W8wIYNgEO2jt0RxmioJMo/edit?usp=sharing

// - pass `sbt clean test doc`
// - update version in build to x.y.0
// - run `sbt publish-signed`

import scala.sys.process._
import java.io.File

object ReleaseStage3 extends App {

  if (args.length != 1) {
    sys.error("usage: ReleaseStage3 x.y")
  }

  val majorMinor = args(0)
  val oldVersion = majorMinor + "-SNAPSHOT"
  val newVersion = majorMinor + ".0"

  def run(processBuilder: ProcessBuilder): Unit = {
    val exitCode = processBuilder.!
    if (exitCode != 0) {
      sys.error(processBuilder.toString)
    }
  }

  val longevityDir = new File("/Users/jsmscs/ws/lf/longevity")
  val projectDir =  new File("/Users/jsmscs/ws/lf/longevity/project")

  // make sure no outstanding changes
  run("git diff --exit-code")
  run("git diff --cached --exit-code")

  // make sure the build is clean
  run(Process("sbt clean test doc", longevityDir))

  // make sure the oldVersion matches whats in the build
  run(Process(
    Seq("grep", "-q", s"""version := "$oldVersion"""", "BuildSettings.scala"),
    projectDir))

  // update to newVersion in the build
  run(Process(
    Seq(
      "sed", "-i", "",
      "-e", s"""s/version := "$oldVersion"/version := "$newVersion"/""",
      "BuildSettings.scala"),
    projectDir))

  // commit and push the new version of the build
  run(Process("git stage BuildSettings.scala", projectDir))
  run(Process(Seq("git", "commit", "-m", s"up build version to $newVersion"), longevityDir))
  run(Process("git push", longevityDir))

  // publish signed
  run(Process("sbt publish-signed", longevityDir) #< java.lang.System.in)

}
