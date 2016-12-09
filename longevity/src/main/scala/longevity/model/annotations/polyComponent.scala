package longevity.model.annotations

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

/** macro annotation to mark a trait as a polymorphic persistent component.
 * creates a companion object for the trait that extends
 * [[longevity.model.PolyCType PolyCType]]. if the trait already has a
 * companion object, then adds a parent class `PolyCType` to the existing
 * companion object. Note that this will not work if your companion object
 * already extends an abstract or concrete class, as `PolyCType` itself is an
 * abstract class.
 */
@compileTimeOnly("you must enable macro paradise for @polyComponent to work")
class polyComponent extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro polyComponent.impl

}

private object polyComponent {

  def impl(c0: Context)(annottees: c0.Tree*): c0.Tree = new PolyComponentImpl {
    val c: c0.type = c0
    val as = annottees
  } .impl

  private abstract class PolyComponentImpl extends AbstractComponentImpl {
    import c.universe._

    protected def name = name0

    private lazy val name0 = as.head match {
      case q"$_ trait $typeName[..$_] extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case _ => 
        c.abort(c.enclosingPosition, s"@longevity.model.annotations.polyComponent can only be applied to traits")
    }

    protected def ctype = tq"longevity.model.PolyCType[$typeName]"

    // this only gets called when as.head is an object
    protected def innerCType =
      c.abort(c.enclosingPosition, s"@longevity.model.annotations.polyComponent can only be applied to traits")

  }

}
