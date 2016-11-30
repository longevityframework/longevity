package longevity.subdomain.annotations

import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import scala.meta._

/** macro annotation to mark a class as a key value. extends the class with
 * `longevity.subdomain.KeyVal[P]`.
 *
 * @tparam P the persistent object that this type serves as a key value for
 */
@compileTimeOnly("you must enable macro paradise for @keyVal to work")
class keyVal[P] extends StaticAnnotation {
  inline def apply(defn: Any): Any = meta {

    def newPs(ps: scala.collection.immutable.Seq[Ctor.Call]) =
      ps :+ (t"longevity.subdomain.KeyVal[$ptype]" : Type).ctorRef(Ctor.Name("this"))

    def ptype = this match {
      case q"new $keyVal[$ptype]()" => ptype
      case _ => 
        abort(s"@longevity.subdomain.keyVal must take a single type parameter")
    }

    defn match {
      case q"..$ms class $n[..$tps] ..$cms(...$pss) extends {..$eds} with ..$ps          { $s => ..$ss }" =>
           q"..$ms class $n[..$tps] ..$cms(...$pss) extends {..$eds} with ..${newPs(ps)} { $s => ..$ss }"
      case _ => 
        abort(s"@longevity.subdomain.keyVal can only be applied to a class")
    }
  }
}
