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
 * as `CType` itself is an abstract class.
 *
 * if the annotated component is already an object, we create the `CType` as
 * an internal `object ctype`.
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

  private abstract class ComponentImpl {
    val c: Context
    val as: Seq[c.Tree]

    import c.universe._

    def impl = if (as.tail.isEmpty) {
      as.head match {
        case q"""$ms object $n extends {..$eds} with ..$ps { $s => ..$ss }""" =>
             q"""$ms object $n extends {..$eds} with ..$ps { $s =>
                   object ctype extends longevity.subdomain.CType[$termName.type] ;
                   ..$ss
                 }
              """
        case _ => q"{ ${as.head} ; $newCompanion }"
      }
    } else {
      q"{ ${as.head} ; $augmentedCompanion }"
    }

    private lazy val name = as.head match {
      case q"$_ class  $typeName[..$_] $_(...$_) extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case q"$_ object $typeName                 extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case q"$_ trait  $typeName[..$_]           extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case _ => 
        c.error(
          c.enclosingPosition,
          s"@longevity.subdomain.component can only be applied to classes, traits and objects")
        TermName("")
    }

    private lazy val termName = TermName(name.decodedName.toString)
    private lazy val typeName = TypeName(name.decodedName.toString)

    private def newCompanion = q"object $termName extends $ctype"

    private def ctype = tq"longevity.subdomain.CType[$typeName]"

    private def augmentedCompanion = {
      val q"$ms object $n extends {..$eds} with ..$ps                   { $s => ..$ss }" = as.tail.head
      q"    $ms object $n extends {..$eds} with ..${ ctype +: ps.tail } { $s => ..$ss }"
    }

  }

}
