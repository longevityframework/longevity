import scala.sys.process._

object killLine extends App {

  if (args.size != 1) {
    println("usage: killLine line")
  }

  val baseDir = new java.io.File(System.getProperty("user.dir"))
  val lineToKill = args(0)
  val fileExtensions = "scala" :: "conf" :: "md" :: Nil

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
      Seq("sed", "-e", s"/^$lineToKill$$/d", "-i", "", sourceFile),
      baseDir).!
    if (exitCode != 0) {
      println(s"non-zero exit code for sed /$lineToKill/d $sourceFile")
    }
  }
  
}
