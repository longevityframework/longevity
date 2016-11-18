package longevity.subdomain

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

/** macro annotation to generate a inner object `props` for your `PType` for
 * you. Recursive members of the type `P` in `PType[P]` are mirrored in a
 * structure of nested objects with type `Prop[P, A](path)` for appropriate
 * values `A` and `path`.
 *
 * - case classes are traversed according to the parameters of their primary
 *   constructor
 * - collection properties (`Options`, `Sets`, and `Lists`) are skipped
 * - traits are traversed according to the abstract public vals they define.
 *   the traits themselves, however, are not mirrored as a `Prop[P, A]`
 *
 * TODO example here
 *
 * NOTE: this traversal process will collect all valid properties for your `PType`.
 * but be aware that the traversal can collect properties that are not actually
 * valid. for example, it will freely traverse case class elements that appear
 * to be persistent components, but are not included in your [[Subdomain]]. it
 * will also traverse persistent components that are otherwise illegal, such as
 * those whose primary constructor has more than one parameter list. in all
 * these cases, the property created by this macro will cause an exception to be
 * thrown on subdomain construction. but all these cases, which represent a
 * malformed subdomain, would have otherwise produced exceptions on subdomain
 * construction.
 */
// TODO better error message here for no MP:
@compileTimeOnly("you must enable macro paradise for @mprops to work")
class mprops extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro mprops.impl

}

object mprops {

  def impl(c0: Context)(annottees: c0.Tree*): c0.Tree = new MPropsImpl {
    val c: c0.type = c0
    val as = annottees
  } .impl

  private abstract class MPropsImpl {
    val c: Context
    val as: Seq[c.Tree]

    import c.universe._

    def impl = if (as.tail.isEmpty) {
      expanded
    } else {
      q"{ $expanded ; ${as.tail.head} }"
    }

    private def expanded: c.Tree = {
      def props(ps: Seq[c.Tree]) = defObjectProps(ps.head)
      as.head match {
        case q"$ms object $n                       extends {..$eds} with ..$ps { $s =>                ..$ss }" =>
             q"$ms object $n                       extends {..$eds} with ..$ps { $s => ${props(ps)} ; ..$ss }"
        case q"$ms class  $n[..$tps] $cms(...$pss) extends {..$eds} with ..$ps { $s =>                ..$ss }" =>
             q"$ms class  $n[..$tps] $cms(...$pss) extends {..$eds} with ..$ps { $s => ${props(ps)} ; ..$ss }"
        case _ => misapplied()
      }
    }

    private def misapplied() = {
      c.error(
        c.enclosingPosition,
        s"@longevity.subdomain.mprop can only be applied to a longevity.subdomain.PType")
      as.head
    }

    private def defObjectProps(parent: c.Tree) = {
      c.typecheck(parent, c.TYPEmode) match {
        case tq"longevity.subdomain.PType[$p]" => q"""object props { ..${propsForType(p)} }"""
        case _                                 => misapplied()
      }
    }

    private def propsForType(ptype: c.Tree) = {
      // TODO this method is WIP. we currently only handle simple cases like `Basics`
      val tpe = ptype.tpe
      val symbol = tpe.typeSymbol.asClass
      if (isCaseClass(symbol)) {
        val constructorSymbol = symbol.primaryConstructor.asMethod
        val params: List[c.universe.TermSymbol] = constructorSymbol.paramLists.head.map(_.asTerm)
        params.map { param =>
          q"""object ${param.name}
              extends longevity.subdomain.ptype.Prop[$ptype, ${param.typeSignature}](${param.name.toString})
           """
        }
      } else {
        // TODO
        misapplied()
        Seq()
      }
    }

    private def isCaseClass(symbol: ClassSymbol) = symbol.isClass && symbol.asClass.isCaseClass

  }

}
