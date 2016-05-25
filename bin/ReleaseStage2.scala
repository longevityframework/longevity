// longevity/bin/ReleaseStage1.scala

// covers steps 2-4 (`sbt clean test doc` through `sbt publish-signed`) in this doc:
// https://docs.google.com/document/d/1RisEp9o0825YJaYjKu4AM9W8wIYNgEO2jt0RxmioJMo/edit?usp=sharing

import scala.sys.process._
import java.io.File

object ReleaseStage2 extends App {

  if (args.length != 2) {
    sys.error("usage: ReleaseStage2 oldVersion newVersion")
  }

  val oldVersion = args(0)
  val newVersion = args(1)

  def run(processBuilder: ProcessBuilder): Unit = {
    val exitCode = processBuilder.!
    if (exitCode != 0) {
      sys.error(processBuilder.toString)
    }
  }

  val isLive = false

  val longevityDir = new File("/Users/jsmscs/ws/longevity")
  val projectDir =  new File("/Users/jsmscs/ws/longevity/project")

  // make sure no outstanding changes
  run("git diff --exit-code")
  run("git diff --cached --exit-code")

  // make sure the build is clean
  if (isLive) {
    run(Process("sbt clean test doc", longevityDir))
  }

  // make sure the oldVersion matches whats in the build
  if (isLive) {
    run(Process(
      Seq("grep", "-q", s"""version := "$oldVersion"""", "LongevityBuild.scala"),
      projectDir))
  }

  // update to newVersion in the build
  if (isLive) {
    run(Process(
      Seq(
        "sed", "-i", "",
        "-e", s"""s/version := "$oldVersion"/version := "$newVersion"/""",
        "LongevityBuild.scala"),
      projectDir))
  }

  // commit and push the new version of the build
  if (isLive) {
    run(Process("git stage LongevityBuild.scala", projectDir))
    run(Process(Seq("git", "commit", "-m", s"up build version to $newVersion"), longevityDir))
    run(Process("git push", longevityDir))
  }

  // publish signed
  if (isLive) {
    run(Process("sbt publish-signed", longevityDir) #< java.lang.System.in)
  }

}
