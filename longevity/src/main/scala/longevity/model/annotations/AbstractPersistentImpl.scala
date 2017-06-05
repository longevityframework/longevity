package longevity.model.annotations

import scala.reflect.macros.whitebox.Context

private[annotations] abstract class AbstractPersistentImpl {
  val c: Context
  val as: Seq[c.Tree]

  import c.universe._

  def impl = if (as.tail.isEmpty) {
    q"{ ${as.head} ; $newCompanion }"
  } else {
    q"{ ${as.head} ; $augmentedCompanion }"
  }

  protected def name: Name

  protected lazy val termName = TermName(name.decodedName.toString)
  protected lazy val typeName = TypeName(name.decodedName.toString)

  private def newCompanion = q"@longevity.model.annotations.mprops object $termName extends $ptype"

  protected def mtype: c.Tree

  protected def ptype: Tree

  private def augmentedCompanion = {
    val q"$origMods object $n extends {..$eds} with ..$ps { $s => ..$ss }" = as.tail.head
    val q"$mpropsAnnMods object $_" = q"@longevity.model.annotations.mprops object $n"
    val mergedMods = Modifiers(
      origMods.flags,
      origMods.privateWithin,
      mpropsAnnMods.annotations.head :: origMods.annotations)
    q"$mergedMods object $n extends {..$eds} with ..${ ptype +: ps.tail } { $s => ..$ss }"
  }

}
