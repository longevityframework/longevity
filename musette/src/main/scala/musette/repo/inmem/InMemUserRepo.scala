package musette.repo
package inmem

import longevity.repo._
import longevity.domain.AssocWithUnpersisted
import musette.domain._

class InMemUserRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[User](User)
with UserRepo {

  // override protected def handleAssocs(user: User): User = user.site match {
  //   case AssocWithUnpersisted(site) => {
  //     val siteRepo = repoPool.repoForEntityTypeTag(scala.reflect.typeTag[Site])
  //     user.copy(site = siteRepo.create(site).id)
  //   }
  //   case _: Id[_] => user

  //   // TODO default case should drop into error state
  //   case _ => user
  // }
  
  import scala.reflect.ClassTag
  import scala.reflect.runtime.universe.TypeTag
  override protected def handleAssocs[Q](q: Q)(implicit qTypeTag: TypeTag[Q]): Q = {
    println(s"q === $q")

    import scala.reflect.runtime.universe.runtimeMirror
    import scala.reflect.runtime.universe.newTermName
    import scala.reflect.runtime.universe.Type
    import scala.reflect.runtime.universe.TermName
    import scala.reflect.runtime.universe.typeTag

    val qType: Type = qTypeTag.tpe
    println(s"entityTypeTag $entityTypeTag")
    println(s"qType $qType")

    val mirror = runtimeMirror(q.getClass.getClassLoader)

    val siteTermSymbol = qType.decl(TermName("site")).asTerm
    println(s"siteTermSymbol $siteTermSymbol")

    val qClassTag = ClassTag[Q](qTypeTag.mirror.runtimeClass(qType))
    val instanceMirror = mirror.reflect(q)(qClassTag)
    val siteFieldMirror = instanceMirror.reflectField(siteTermSymbol)

    println(s"siteFieldMirror $siteFieldMirror")

    val siteAssoc = siteFieldMirror.get.asInstanceOf[AssocWithUnpersisted[musette.domain.Site]]
    val site = siteAssoc.unpersisted

    

    val repo = repoPool.repoForEntity(site)
    val created = repo.create(site)
    val id = created.id

    siteFieldMirror.set(id)

    // snope out all the AssocWithUnpersisted
    

    q
  }

}
