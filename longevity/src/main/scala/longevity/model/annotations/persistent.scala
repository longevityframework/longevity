package longevity.model.annotations

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import longevity.model.ptype.Key
import longevity.model.ptype.Index

/** macro annotation to mark a class as a persistent component. creates a
 * companion object for the class that extends [[longevity.model.PType
 * PType]]. if the class already has a companion object, then adds a parent
 * class `PType` to the existing companion object. Note that
 * this will not work if your companion object already extends an abstract
 * or concrete class, as `PType` itself is an abstract class. if this
 * happens, you will see a compiler error such as "class Foo needs to be a trait
 * to be mixed in".
 *
 * @tparam M the model
 *
 * @param keySet the set of keys for the persistent type. defaults to the empty set. if omitted, you
 * will need to define the keySet yourself in the companion object
 *
 * @param indexSet the set of indexes for the persistent type. defaults to the empty set. if
 * omitted, you can still define the indexSet yourself in the companion object
 */
@compileTimeOnly("you must enable macro paradise for @persistent to work")
class persistent[M](
  keySet: Set[Key[M, _]] = Set.empty[Key[M, _]],
  indexSet: Set[Index[_]] = Set.empty[Index[_]])
extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro persistent.impl

}

private object persistent {

  def impl(c0: Context)(annottees: c0.Tree*): c0.Tree = new PersistentImpl {
    val c: c0.type = c0
    val as = annottees
  } .impl

  private abstract class PersistentImpl extends AbstractPersistentImpl {
    import c.universe._

    protected def name = name0

    private lazy val name0 = as.head match {
      case q"$_ class $typeName[..$_] $_(...$_) extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case _ =>
        c.abort(
          c.enclosingPosition,
          s"@longevity.model.annotations.persistent can only be applied to classes")
    }

    protected def mtype = c.prefix.tree match {
      case q"new $persistent[$mtype](..$args)" => mtype
      case q"new $persistent[$mtype]" => mtype
      case _ => c.abort(
        c.enclosingPosition,
        s"@longevity.model.annotations.persistent requires a single type parameter for the domain model")
    }

    protected def ptype = tq"longevity.model.PType[$mtype, $typeName]"

  }

}
