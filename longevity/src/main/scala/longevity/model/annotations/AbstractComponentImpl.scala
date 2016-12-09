package longevity.model.annotations

import scala.reflect.macros.whitebox.Context

/** shared code for ComponentImpl, PolyComponentImpl, and DerivedComponentImpl */
private[annotations] trait AbstractComponentImpl {
  val c: Context
  val as: Seq[c.Tree]

  import c.universe._

  def impl = if (as.tail.isEmpty) {
    as.head match {
      case q"$ms object $n extends {..$eds} with ..$ps { $s =>               ..$ss }" =>
           q"$ms object $n extends {..$eds} with ..$ps { $s => $innerCType ; ..$ss }"
      case _ => q"{ ${as.head} ; $newCompanion }"
    }
  } else {
    q"{ ${as.head} ; $augmentedCompanion }"
  }

  protected def name: Name

  protected lazy val termName = TermName(name.decodedName.toString)
  protected lazy val typeName = TypeName(name.decodedName.toString)

  protected def ctype: Tree

  protected def innerCType: Tree

  protected def newCompanion = q"object $termName extends $ctype"

  protected def augmentedCompanion = {
    val q"$ms object $n extends {..$eds} with ..$ps { $s => ..$ss }" = as.tail.head
    val newPs = if (ps.isEmpty) {
      ctype :: Nil
    } else if (ps.head.equalsStructure(tq"scala.AnyRef")) {
      ctype +: ps.tail
    } else {
      ctype +: ps
    }
    q"$ms object $n extends {..$eds} with ..$newPs { $s => ..$ss }"
  }

}
