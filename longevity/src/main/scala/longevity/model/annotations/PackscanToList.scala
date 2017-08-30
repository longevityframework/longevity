package longevity.model.annotations

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros

trait PackscanToList {

  /** gathers all the `A`s in the same package as the macro was called into a list.
   *
   * this def macro is used by the `@domainModel` annotation macro to gather all the `PTypes` and
   * `CTypes` together to construct the `ModelType`. it is a little out of place in a package named
   * `longevity.model.annotations`, but it does not seem appropriate to create a new project or
   * package to contain a single def macro.
   * 
   * @see domainModel
   * @see longevity,model.PType
   * @see longevity,model.CType
   * @see longevity,model.ModelType
   */
  def packscanToList[A]: List[A] = macro PackscanToList.packscanToList[A]

}

private object PackscanToList {

  def packscanToList[A: c0.WeakTypeTag](c0: Context): c0.Tree =
    new Scanner[A] {
      val c: c0.type = c0
      val aTag = implicitly[c0.WeakTypeTag[A]]
    } .packscanToList

  abstract class Scanner[A] {
    val c: Context
    val aTag: c.WeakTypeTag[A]
    import c.universe._

    def packscanToList = {
      val decls = matchingDecls(enclosingPackage.info.decls)
      decls.foldLeft(q"scala.collection.immutable.Nil": Tree) { case (acc, decl) =>
        c.internal.initialize(decl)
        q"scala.collection.immutable.::($decl, $acc)"
      }
    }

    private def enclosingPackage = {
      def loop(s: c.Symbol): c.Symbol = if (s.isPackage || s.isPackageClass) s else loop(s.owner)
      loop(c.internal.enclosingOwner)
    }

    private def matchingDecls(decls: MemberScope): Seq[Symbol] = {
      val localModules  = decls.filter { s => s.isModule }
      val localDecls    = localModules.filter { s => s.info <:< aTag.tpe }
      val localPackages = decls.filter { s => s.isPackage || s.isPackageClass }
      val nestedDecls   = localModules.map(_.info.decls).map(matchingDecls)
      (localDecls :: nestedDecls.toList).flatten
    }

  }

}
