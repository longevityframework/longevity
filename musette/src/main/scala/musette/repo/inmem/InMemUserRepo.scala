package musette.repo.inmem

import longevity.repo._
import musette.domain._
import musette.repo.UserRepo

class InMemUserRepo(
  implicit repoPool: OldRepoPool
)
extends InMemRepo[User](UserType)
with UserRepo
