package musette.repo.inmem

import longevity.repo._
import musette.domain.User
import musette.repo.UserRepo

class InMemUserRepo(
  implicit override protected val repoPool: RepoPool
)
extends UserRepo with InMemRepo[User] {
  override val entityType = User
}
