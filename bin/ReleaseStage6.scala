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

  val longevityDir = new File("/Users/jsmscs/ws/lf/longevity")
  val projectDir =  new File("/Users/jsmscs/ws/lf/longevity/project")

  def run(processBuilder: ProcessBuilder): Unit = {
    val exitCode = processBuilder.!
    if (exitCode != 0) {
      sys.error(processBuilder.toString)
    }
  }
  def run(cmd: String): Unit = run(Process(cmd, longevityDir))
  def run(cmdArgs: String*): Unit = run(Process(cmdArgs, longevityDir))

  // make sure the oldVersion matches whats in the build
  run("grep", "-q", s"""version := "$oldVersion"""", "project/BuildSettings.scala")

  // make sure no outstanding changes
  run("git diff --exit-code")
  run("git diff --cached --exit-code")

  // create branch x.y
  run("git", "checkout", "-b", majorMinor)
  run("git", "push", "-u", "origin", majorMinor)

  // create tag x.y.0
  run("git", "tag", "-s", "-a", oldVersion, "-m", s"create tag $oldVersion")
  run("git", "push", "origin", oldVersion)

  // back to master branch
  run("git", "checkout", "master")

  // update build to snapshot version
  run(
    "sed", "-i", "",
    "-e", s"""s/version := "$oldVersion"/version := "$newVersion"/""",
    "project/BuildSettings.scala")

  // commit and push the new version of the build
  run("git stage project/BuildSettings.scala")
  run("git", "commit", "-m", s"up build version to $newVersion")
  run("git push")

}
