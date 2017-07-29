package longevity.model.annotations

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

/** macro annotation to mark a class as a persistent component. creates a companion object for the
 * class that extends [[longevity.model.PType PType]]. if the class already has a companion object,
 * then adds a parent class `PType` to the existing companion object.
 *
 * Note that, when using this annotation, an existing companion object cannot already extend a class
 * other than `PType` or `scala.AnyRef`.
 *
 * @tparam M the model
 */
@compileTimeOnly("you must enable macro paradise for @persistent to work")
class persistent[M] extends StaticAnnotation {

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
      case q"new $_[$mtype]" => mtype
      case _ => c.abort(
        c.enclosingPosition,
        s"@longevity.model.annotations.persistent requires a single type parameter for the domain model")
    }

    protected lazy val ptype = tq"longevity.model.PType[$mtype, $typeName]"

  }

}
