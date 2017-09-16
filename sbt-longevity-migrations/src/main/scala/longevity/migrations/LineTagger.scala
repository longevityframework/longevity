package longevity.migrations

case class LineTagger(sourcePackage: String, targetPackage: String) {

  private def splitPackage(p: String) = p.lastIndexOf('.') match {
    case i if i < 0 => ("", p)
    case i          => (p.substring(0, i), p.substring(i + 1))
  }

  private val (sourcePrefix, sourceName) = splitPackage(sourcePackage)
  private val (targetPrefix, targetName) = splitPackage(targetPackage)

  private val fullPackage   = s"(.*\\b|\\s*)package\\s+$sourcePackage(\\b.*|\\s*)".r
  private val packagePrefix = s"(.*\\b|\\s*)package\\s+$sourcePrefix(\\b.*|\\s*)".r
  private val packageObject = s"(.*\\b|\\s*)package\\s+object\\s+$sourceName(\\b.*|\\s*)".r
  private val fullImport    = s"(.*\\b|\\s*)import\\s+$sourcePackage(\\b.*|\\s*)".r

  def tagLine(sourceLine: String): String = {
    sourceLine match {
      case fullPackage(l, r)   => s"${l}package ${targetPackage}${r}"
      case packagePrefix(l, r) => s"${l}package ${targetPrefix}${r}"
      case packageObject(l, r) => s"${l}package object ${targetName}${r}"
      case fullImport(l, r)    => s"${l}import ${targetPackage}${r}"
      case line                => line
    }
  }

}
