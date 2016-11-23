package longevity.subdomain.annotations

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

/** macro annotation to mark a class as a persistent component. creates a
 * companion object for the class that extends [[longevity.subdomain.CType
 * CType]]. if the class already has a companion object, then adds a parent
 * class `CType` to the existing companion object. Note that this will not
 * work if your companion object already extends an abstract or concrete class,
 * as `CType` itself is an abstract class. if this
 * happens, you will see a compiler error such as "class Foo needs to be a trait
 * to be mixed in".
 */
@compileTimeOnly("you must enable macro paradise for @component to work")
class component extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro component.impl

}

object component {

  def impl(c0: Context)(annottees: c0.Tree*): c0.Tree = new ComponentImpl {
    val c: c0.type = c0
    val as = annottees
  } .impl

  private abstract class ComponentImpl extends AbstractComponentImpl {
    import c.universe._

    protected def name = name0

    private lazy val name0 = as.head match {
      case q"$_ class $typeName[..$_] $_(...$_) extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case _ => 
        c.abort(c.enclosingPosition, s"@longevity.subdomain.component can only be applied to classes")
    }

    protected def ctype = tq"longevity.subdomain.CType[$typeName]"

    protected def innerCType = q"object ctype extends longevity.subdomain.CType[$termName.type]"

  }

}
