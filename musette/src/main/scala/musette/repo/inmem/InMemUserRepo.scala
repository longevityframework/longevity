package musette.repo
package inmem

import longevity.repo._
import longevity.domain.SimpleAssoc
import musette.domain._

class InMemUserRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[User](User)
with UserRepo {

  // override protected def handleAssocs(user: User): User = user.site match {
  //   case SimpleAssoc(site) => {
  //     val siteRepo = repoPool.repoForEntityClassTag(scala.reflect.classTag[Site])
  //     user.copy(site = siteRepo.create(site).id)
  //   }
  //   case _: Id[_] => user

  //   // TODO default case should drop into error state
  //   case _ => user
  // }

}
