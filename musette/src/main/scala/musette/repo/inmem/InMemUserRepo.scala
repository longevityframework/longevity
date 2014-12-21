package musette.repo.inmem

import longevity.repo._
import longevity.domain.AssocWithUnpersisted
import musette.domain._
import musette.repo.UserRepo

class InMemUserRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[User](User)
with UserRepo
