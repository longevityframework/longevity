// longevity/bin/ReleaseStage6.scala

// covers step 6 in this doc:
// https://docs.google.com/document/d/1RisEp9o0825YJaYjKu4AM9W8wIYNgEO2jt0RxmioJMo/edit?usp=sharing

// - create branch x.y
// - create tag x.y.0
// - update version in build to x.y+1-SNAPSHOT

import scala.sys.process._
import java.io.File

object ReleaseStage6 extends App {

  def usage() = sys.error("usage: ReleaseStage6 x.y")

  if (args.length != 1) usage()

  val majorMinor = args(0)
  val mmMatcher = """(\d+)\.(\d+)""".r.pattern.matcher(majorMinor)
  if (!mmMatcher.matches) usage()

  val major = mmMatcher.group(1).toInt
  val minor = mmMatcher.group(2).toInt
  val oldVersion = majorMinor + ".0"
  val newVersion = s"$major.${minor+1}-SNAPSHOT"

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

  // create branch x.y
  run(Process(Seq("git", "checkout", "-b", majorMinor), longevityDir))
  run(Process(Seq("git", "push", "-u", "origin", majorMinor), longevityDir))

  // create tag x.y.0
  run(Process(Seq("git", "tag", "-a", oldVersion, "-m", s"create tag $oldVersion"), longevityDir))
  run(Process(Seq("git", "push", "origin", oldVersion), longevityDir))

  // back to master branch
  run(Process(Seq("git", "checkout", "master"), longevityDir))

  // update build to snapshot version
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

}
