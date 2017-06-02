package longevity.model.annotations

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

/** macro annotation to mark a class or object as a derived persistent component. creates a
 * companion object for the class that extends [[longevity.model.DerivedCType
 * DerivedCType]]. if the class already has a companion object, then adds a parent
 * class `DerivedCType` to the existing companion object. Note that
 * this will not work if your companion object already extends an abstract
 * or concrete class, as `DerivedCType` itself is an abstract class. if this
 * happens, you will see a compiler error such as "class Foo needs to be a trait
 * to be mixed in".
 *
 * if the annotated component is already an object, we create the `DerivedCType` as
 * an internal `object ctype`.
 *
 * @tparam M the model
 *
 * @tparam Poly the type of the polymorphic component that this component is
 * derived from
 */
@compileTimeOnly("you must enable macro paradise for @derivedComponent to work")
class derivedComponent[M, Poly] extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro derivedComponent.impl

}

private object derivedComponent {

  def impl(c0: Context)(annottees: c0.Tree*): c0.Tree = new DerivedComponentImpl {
    val c: c0.type = c0
    val as = annottees
  } .impl

  private abstract class DerivedComponentImpl extends AbstractComponentImpl {
    import c.universe._

    protected def name = name0

    private lazy val name0 = as.head match {
      case q"$_ class  $typeName[..$_] $_(...$_) extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case q"$_ object $typeName                 extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case _ => 
        c.abort(
          c.enclosingPosition,
          s"@longevity.model.annotations.derivedComponent can only be applied to objects and classes")
    }

    protected def ctype = tq"longevity.model.DerivedCType[$mtype, $typeName, $polyTypeName]"

    protected def innerCType =
      q"object ctype extends longevity.model.DerivedCType[$mtype, $termName.type, $polyTypeName]"

    protected lazy val (mtype, polyTypeName) = c.prefix.tree match {
      case q"new $derivedComponent[$mtype, $poly]" => (mtype, poly)
      case q"new $derivedComponent[$mtype, $poly](..$params)" => (mtype, poly)
      case _ =>
        c.abort(
          c.enclosingPosition,
          s"@longevity.model.annotations.derivedComponent must take exactly two type arguments")
    }

  }

}
