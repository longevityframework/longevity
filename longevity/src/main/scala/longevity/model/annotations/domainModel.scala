package longevity.model.annotations

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

/** macro annotation to mark an object as a domain model. extends the class with
 * `longevity.model.ModelType(currentPackage)`, where `currentPackage` is
 * the name of the package in which this annotation was applied.
 */
@compileTimeOnly("you must enable macro paradise for @domainModel to work")
class domainModel extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro domainModel.impl

}

// TODO learn what @macrocompat is, see if you can make use of it here
private object domainModel {

  def impl(c0: Context)(annottees: c0.Tree*): c0.Tree = new ModelTypeImpl {
    val c: c0.type = c0
    val as = annottees
  } .impl

  private abstract class ModelTypeImpl {
    val c: Context
    val as: Seq[c.Tree]

    import c.universe._

    def impl = if (as.tail.isEmpty) {
      q"{ ${as.head} ; $newCompanion }"
    } else {
      q"{ ${as.head} ; $augmentedCompanion }"
    }

    private def newCompanion = q"object $termName { $modelType }"

    private def augmentedCompanion = {
      val q"$mods object $n extends {..$eds} with ..$ps { $s => ..$ss }" = as.tail.head
      q"$mods object $n extends {..$eds} with ..$ps { $s => $modelType ; ..$ss }"
    }

    private def modelType =
      q"implicit object modelType extends longevity.model.ModelType[$typeName]($owningPackage)"

    private lazy val name = as.head match {
      case q"$_ class $typeName[..$_] $_(...$_) extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case q"$_ trait $typeName[..$_]           extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case _ =>
        c.abort(
          c.enclosingPosition,
          s"@longevity.model.annotations.domainModel can only be applied to classes and traits")
    }

    private def termName = TermName(name.decodedName.toString)
    private def typeName = TypeName(name.decodedName.toString)
    
    def owningPackage = {
      def owningPackage0(s: c.Symbol): c.Symbol = if (s.isPackage) s else owningPackage0(s.owner)
      val p = owningPackage0(c.internal.enclosingOwner)
      Constant(p.fullName)
    }

  }

}
