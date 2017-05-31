package longevity.model.annotations

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

/** macro annotation to mark a class as a key value. extends the class with
 * `longevity.model.KeyVal[P]`.
 *
 * @tparam M the domain model
 * @tparam P the persistent object that this type serves as a key value for
 */
@compileTimeOnly("you must enable macro paradise for @keyVal to work")
class keyVal[M, P] extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro keyVal.impl

}

private object keyVal {

  def impl(c0: Context)(annottees: c0.Tree*): c0.Tree = new KeyValImpl {
    val c: c0.type = c0
    val as = annottees
  } .impl

  private abstract class KeyValImpl {
    val c: Context
    val as: Seq[c.Tree]

    import c.universe._

    def impl = if (as.tail.isEmpty) {
      q"{ ${as.head} ; $newCompanion }"
    } else {
      q"{ ${as.head} ; $augmentedCompanion }"
    }

    private def newCompanion = q"object $termName extends $kvtype"

    private def augmentedCompanion = {
      val q"$mods object $n extends {..$eds} with ..$ps { $s => ..$ss }" = as.tail.head
      val newPs = if (ps.isEmpty) {
        kvtype :: Nil
      } else if (ps.head.equalsStructure(tq"scala.AnyRef")) {
        kvtype +: ps.tail
      } else {
        kvtype +: ps
      }
      q"$mods object $n extends {..$eds} with ..$newPs { $s => ..$ss }"
    }

    private lazy val name = as.head match {
      case q"$_ class $typeName[..$_] $_(...$_) extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case q"$_ trait $typeName[..$_]           extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case _ =>
        c.abort(c.enclosingPosition, s"@longevity.model.annotations.keyVal can only be applied to classes")
    }

    private lazy val termName = TermName(name.decodedName.toString)
    private lazy val typeName = TypeName(name.decodedName.toString)

    private def kvtype = tq"longevity.model.KVType[$mtype, $ptype, $typeName]"

    private lazy val (mtype, ptype) = c.prefix.tree match {
      case q"new $persistent[$mtype, $ptype]" => (mtype, ptype)
      case _ => c.abort(
        c.enclosingPosition,
        s"@longevity.model.annotations.keyVal requires type parameters for the model and the persistent type that holds the key")
    }

  }

}
