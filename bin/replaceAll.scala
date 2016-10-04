import scala.sys.process._

object replaceAll extends App {

  if (args.size != 2) {
    println("usage: replaceAll original replacement")
  }

  // settings
  val baseDir = new java.io.File(System.getProperty("user.dir"))
  val original = args(0)
  val replacement = args(1)

  // TODO rename dirs

  val fileExtensions = "scala" :: "conf" :: "md" :: Nil

  val filenameMatch = s"""(.*?)$original(.*?)\\.(.*)""".r

  val findCommandArgs = {
    def mapexts = fileExtensions.foldLeft(List[String]()) { (acc, ext) =>
      val x = "-name" :: s"*.$ext" :: Nil
      if (acc.isEmpty) x else acc ::: ("-o" :: x)
    }
    "find" :: "." :: "(" :: Nil ::: mapexts ::: ")" :: "-type" :: "f" :: Nil
  }

  val findCmd = Process(findCommandArgs, baseDir)

  findCmd.lineStream.foreach { sourceFile =>

    val exitCode = Process(
      Seq("sed", "-e", s"s/$original/$replacement/g", "-i", "", sourceFile),
      baseDir).!
    if (exitCode != 0) {
      println(s"non-zero exit code for sed s/$original/$replacement/g $sourceFile")
    }

    sourceFile match {
      case filenameMatch(prefix, suffix, ext) => {
        val exitCode = Process(
          Seq("mv", sourceFile, s"$prefix$replacement$suffix.$ext"),
          baseDir).!
        if (exitCode != 0) {
          println(s"non-zero exit code for mv $sourceFile")
        }
      }
      case _ =>
    }
  }
  
}
