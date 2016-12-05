package longevity.subdomain.annotations

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

/** macro annotation to mark an object as a subdomain. extends the class with
 * `longevity.subdomain.Subdomain(currentPackage)`, where `currentPackage` is
 * the name of the package in which this annotation was applied.
 */
@compileTimeOnly("you must enable macro paradise for @subdomain to work")
class subdomain[P] extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro subdomain.impl

}

private object subdomain {

  def impl(c0: Context)(annottees: c0.Tree*): c0.Tree = new SubdomainImpl {
    val c: c0.type = c0
    val as = annottees
  } .impl

  private abstract class SubdomainImpl {
    val c: Context
    val as: Seq[c.Tree]

    import c.universe._

    def impl = {
      as.head match {
        case q"$ms object $n extends {..$eds} with ..$ps          { $s => ..$ss }" =>
             q"$ms object $n extends {..$eds} with ..${newPs(ps)} { $s => ..$ss }"
        case _ =>
          c.abort(c.enclosingPosition, s"@longevity.subdomain.subdomain can only be applied to objects")
      }
    }

    def newPs(ps: Seq[c.Tree]) = {
      def owningPackage(s: c.Symbol): c.Symbol = if (s.isPackage) s else owningPackage(s.owner)
      val p = owningPackage(c.internal.enclosingOwner)
      val subdomain =
        Apply(
          Select(Select(Ident(TermName("longevity")), TermName("subdomain")),
                 TypeName("Subdomain")),
          List(Literal(Constant(p.fullName))))
      subdomain +: ps.tail
    }

  }

}
