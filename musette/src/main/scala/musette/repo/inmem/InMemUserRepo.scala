package musette.repo
package inmem

import longevity.repo._
import longevity.domain.AssocWithUnpersisted
import musette.domain._

class InMemUserRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[User](User)
with UserRepo
