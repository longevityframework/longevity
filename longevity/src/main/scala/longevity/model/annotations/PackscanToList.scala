package longevity.model.annotations

import scala.reflect.macros.whitebox.Context

private[annotations] object PackscanToList {

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
