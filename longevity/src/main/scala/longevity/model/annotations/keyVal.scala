package longevity.model.annotations

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

/** macro annotation to mark a class as a key value. extends the class with
 * `longevity.model.KeyVal[P]`.
 *
 * @tparam P the persistent object that this type serves as a key value for
 */
@compileTimeOnly("you must enable macro paradise for @keyVal to work")
class keyVal[P] extends StaticAnnotation {

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

    def impl = if (as.tail.isEmpty) extended else q"{ $extended ; ${as.tail.head} }"

    private def extended: c.Tree = {
      def newPs(ps: Seq[c.Tree]) = ps :+ tq"longevity.model.KeyVal[$ptype]"
      as.head match {
        case q"$ms class $n[..$tps] $cms(...$pss) extends {..$eds} with ..$ps          { $s => ..$ss }" =>
             q"$ms class $n[..$tps] $cms(...$pss) extends {..$eds} with ..${newPs(ps)} { $s => ..$ss }"
        case _ =>
          c.abort(c.enclosingPosition, s"@longevity.model.annotations.keyVal can only be applied to a class")
      }
    }

    private def ptype = c.prefix.tree match {
      case q"new $keyVal[$ptype]()" => ptype
      case _ => 
          c.abort(c.enclosingPosition, s"@longevity.model.annotations.keyVal must take a single type parameter")
    }

  }

}
