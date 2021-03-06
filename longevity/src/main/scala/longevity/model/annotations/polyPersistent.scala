package longevity.model.annotations

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

/** macro annotation to mark a trait as a polymorphic persistent component. creates a companion
 * object for the class that extends [[longevity.model.PolyPType PolyPType]]. if the class already
 * has a companion object, then adds a parent class `PolyPType` to the existing companion object.
 *
 * Note that, when using this annotation, an existing companion object cannot already extend a class
 * other than `PolyPType` or `scala.AnyRef`.
 *
 * @tparam M the model
 */
@compileTimeOnly("you must enable macro paradise for @polyPersistent to work")
class polyPersistent[M] extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro polyPersistent.impl

}

private object polyPersistent {

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
          s"@longevity.model.annotations.polyPersistent can only be applied to traits")
    }

    protected def mtype = c.prefix.tree match {
      case q"new $_[$mtype]" => mtype
      case _ => c.abort(
        c.enclosingPosition,
        s"@longevity.model.annotations.polyPersistent requires a single type parameter for the domain model")
    }    

    protected lazy val ptype = tq"longevity.model.PolyPType[$mtype, $typeName]"

  }

}
