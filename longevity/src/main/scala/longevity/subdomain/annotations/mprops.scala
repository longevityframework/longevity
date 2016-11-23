package longevity.subdomain.annotations

import org.joda.time.DateTime
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
 * TODO example here. or maybe just a link to the manual page
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
// TODO better error messages for no MP:
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

    def impl = if (as.tail.isEmpty) expanded else q"{ $expanded ; ${as.tail.head} }"

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
        s"@longevity.subdomain.mprops can only be applied to a longevity.subdomain.PType")
      as.head
    }

    private def defObjectProps(parent: c.Tree) = {
      c.typecheck(parent, c.TYPEmode) match {
        case tq"longevity.subdomain.PType[$p]" => q"object props { ..${propsForP(p)} }"
        case _                                 => misapplied()
      }
    }

    private def propsForP(p: c.Tree) = propsForType(p, "", p.tpe).trees

    private case class PropsForType(trees: Seq[c.Tree], hasNonPropMembers: Boolean)

    // TODO refactor
    private def propsForType(p: c.Tree, pathPrefix: String, tpe: c.Type): PropsForType = {
      val symbol = tpe.typeSymbol.asClass
      if (isCaseClass(symbol)) {
        val constructorSymbol = symbol.primaryConstructor.asMethod
        val params: List[TermSymbol] = constructorSymbol.paramLists.head.map(_.asTerm)
        params.foldLeft(PropsForType(Seq.empty, false)) {
          case (acc, param) =>
            val paramTpe = param.typeSignature.etaExpand
            val paramPath = s"$pathPrefix${param.name.toString}"
            val PropsForType(stats, hasNonPropMembers) = propsForType(p, s"$paramPath.", paramTpe)

            val tree = (shouldExtendProp(param.typeSignature, hasNonPropMembers), stats.nonEmpty) match {
              case (true, true) => q"""
                object ${param.name} extends longevity.subdomain.ptype.Prop[$p, $paramTpe]($paramPath) {
                  ..$stats
                }
                """
              case (true, false) => q"""
                object ${param.name} extends longevity.subdomain.ptype.Prop[$p, $paramTpe]($paramPath)
                """
              case (false, true) => q"""
                object ${param.name} { ..$stats }
                """
              case (false, false) => EmptyTree
            }

            PropsForType(acc.trees :+ tree, acc.hasNonPropMembers || hasNonPropMembers)
        }
      } else {
        // TODO
        PropsForType(Seq(), !isBasicType(tpe))
      }
    }

    private def isCaseClass(symbol: Symbol) = symbol.isClass && symbol.asClass.isCaseClass

    private def shouldExtendProp(tpe: c.Type, hasNonPropMembers: Boolean) = {
      !hasNonPropMembers && ( isCaseClass(tpe.typeSymbol) || isBasicType(tpe) )
    }

    private def isBasicType(tpe: c.Type) = {
      tpe =:= c.typeOf[Boolean  ] ||
      tpe =:= c.typeOf[Char     ] ||
      tpe =:= c.typeOf[DateTime ] ||
      tpe =:= c.typeOf[Double   ] ||
      tpe =:= c.typeOf[Float    ] ||
      tpe =:= c.typeOf[Int      ] ||
      tpe =:= c.typeOf[Long     ] ||
      tpe =:= c.typeOf[String   ]
    }

  }

}
