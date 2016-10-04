import scala.sys.process._

// specialized script for multi-imports such as:
//     import longevity.subdomain.{ Root, RootType }
// that replaceAll cannot handle
object repackageImports extends App {

  if (args.size != 3) {
    println("usage: replaceAll <classname> <original package> <replacement package>")
  }

  // settings
  val baseDir = new java.io.File(System.getProperty("user.dir"))
  val classname = args(0)
  val original = args(1)
  val replacement = args(2)

  val findCmd = Process(
    Seq("find", ".", "-name", "*.scala", "-type", "f"),
    baseDir)
  findCmd.lineStream.foreach { scalaFile =>

    def runSed(script: String): Unit = {
      val exitCode = sys.process.Process(Seq("sed", "-E", "-e", script, "-i", "", scalaFile), baseDir).!
      if (exitCode != 0) {
        println(s"non-zero exit code for sed '$script' in $scalaFile")
      }
    }

    runSed(s"s/import $original\\.({ *)?$classname( *})?/import $replacement.$classname/g")

    runSed(s"""|/import $original\\.{.*$classname.*}/ {
               | s/, *$classname//
               | s/$classname, *//
               | p
               | s/.*/import $replacement.$classname/
               |}""".stripMargin)

  }
}
