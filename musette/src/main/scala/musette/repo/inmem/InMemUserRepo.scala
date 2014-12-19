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
  import scala.reflect.runtime.universe
  override protected def handleAssocs[Q](q: Q)(implicit qTypeTag: universe.TypeTag[Q]): Q = {
    println(s"q === $q")

    val qType: universe.Type = qTypeTag.tpe
    println(s"entityTypeTag $entityTypeTag")
    println(s"qType $qType")

    val siteTermSymbol = qType.decl(universe.TermName("site")).asTerm
    println(s"siteTermSymbol $siteTermSymbol")

    val siteType: universe.Type = siteTermSymbol.typeSignatureIn(qType)
    val unpersistedTermSymbol = siteType.decl(universe.TermName("unpersisted")).asTerm
    
    val qClassTag = ClassTag[Q](qTypeTag.mirror.runtimeClass(qType))
    val mirror = universe.runtimeMirror(q.getClass.getClassLoader)
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
