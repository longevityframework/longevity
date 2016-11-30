package longevity.subdomain.annotations

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import longevity.subdomain.ptype.Key
import longevity.subdomain.ptype.Index

/** macro annotation to mark a trait as a polymorphic persistent component. creates a
 * companion object for the class that extends [[longevity.subdomain.PolyPType
 * PolyPType]]. if the class already has a companion object, then adds a parent
 * class `PolyPType` to the existing companion object. Note that
 * this will not work if your companion object already extends an abstract
 * or concrete class, as `PolyPType` itself is an abstract class. if this
 * happens, you will see a compiler error such as "class Foo needs to be a trait
 * to be mixed in".
 *
 * @param keySet the set of keys for the persistent type
 * @param indexSet the set of indexes for the persistent type. defaults to the empty set
 */
@compileTimeOnly("you must enable macro paradise for @polyPersistent to work")
class polyPersistent(keySet: Set[Key[_]] = null, indexSet: Set[Index[_]] = null) extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro polyPersistent.impl

}

object polyPersistent {

  def impl(c0: Context)(annottees: c0.Tree*): c0.Tree = new PolyPersistentImpl {
    val c: c0.type = c0
    val as = annottees
  } .impl

  private abstract class PolyPersistentImpl extends AbstractPersistentImpl {
    import c.universe._

    protected def name = name0

    private lazy val name0 = as.head match {
      case q"$_ trait $typeName[..$_] extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case _ =>
        c.abort(
          c.enclosingPosition,
          s"@longevity.subdomain.polyPersistent can only be applied to traits")
    }

    protected def ptype = tq"longevity.subdomain.PolyPType[$typeName]"

  }

}
