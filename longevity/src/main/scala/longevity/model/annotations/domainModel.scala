package longevity.model.annotations

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

/** macro annotation to mark a class or trait as a domain model. adds two elements to the companion
 * object of the annotated class:
 *
 * - `implicit object modelType extends longevity.model.ModelType[M](currentPackage)`, 
 * - `private[currentPackage] implicit object modelEv extends longevity.model.ModelEv[M]`
 *
 * where `M` is the annotated class, and `currentPackage` is the package in which this annotation
 * was applied.
 *
 * @see longevity.model.ModelType
 * @see longevity.model.ModelEv
 */
@compileTimeOnly("you must enable macro paradise for @domainModel to work")
class domainModel extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro domainModel.impl

}

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

    private def newCompanion = q"object $termName { $modelType ; $modelEv }"

    private def augmentedCompanion = {
      val q"$mods object $n extends {..$eds} with ..$ps { $s => ..$ss }" = as.tail.head
      q"$mods object $n extends {..$eds} with ..$ps { $s => $modelType ; $modelEv ; ..$ss }"
    }

    private def modelType = {
      val pTypes = q"longevity.model.annotations.packscanToList[longevity.model.PType[$typeName, _]]"
      val cTypes = q"longevity.model.annotations.packscanToList[longevity.model.CType[_]]"
      q"implicit object modelType extends longevity.model.ModelType[$typeName]($pTypes, $cTypes)"
    }

    private def modelEv = {
      val p = TypeName(owningPackage.name.decodedName.toString)
      q"private[$p] implicit object modelEv extends longevity.model.ModelEv[$typeName]"
    }

    private lazy val typeName = as.head match {
      case q"$_ class $typeName[..$_] $_(...$_) extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case q"$_ trait $typeName[..$_]           extends {..$_} with ..$_ { $_ => ..$_ }" => typeName
      case _ =>
        c.abort(
          c.enclosingPosition,
          s"@longevity.model.annotations.domainModel can only be applied to classes and traits")
    }

    private def termName = TermName(typeName.decodedName.toString)

    def owningPackage = {
      def owningPackage0(s: c.Symbol): c.Symbol = if (s.isPackage) s else owningPackage0(s.owner)
      owningPackage0(c.internal.enclosingOwner)
    }

  }

}
