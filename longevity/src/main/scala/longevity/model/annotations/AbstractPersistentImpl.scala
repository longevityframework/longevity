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

  private def newCompanion = q"""
    @longevity.model.annotations.mprops @longevity.model.annotations.pEv object $termName extends $ptype
  """

  protected def mtype: c.Tree

  protected def ptype: Tree

  private def augmentedCompanion = {
    val q"$origMods object $n extends {..$eds} with ..$ps { $s => ..$ss }" = as.tail.head
    val newPs = if (ps.head.equalsStructure(tq"scala.AnyRef")) {
      ptype +: ps.tail
    } else if (ps.head.equalsStructure(ptype)) {
      ps
    } else {
      c.abort(
        c.enclosingPosition,
        s"companion object of $name class cannot extend ${ps.head}")
    }
    val q"$mpropsMods object $_" = q"@longevity.model.annotations.mprops object $n"
    val q"$pEvMods object $_" = q"@longevity.model.annotations.pEv object $n"
    val mergedMods = Modifiers(
      origMods.flags,
      origMods.privateWithin,
      mpropsMods.annotations.head :: pEvMods.annotations.head :: origMods.annotations)
    q"$mergedMods object $n extends {..$eds} with ..$newPs { $s => ..$ss }"
  }

}
