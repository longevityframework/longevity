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

}
