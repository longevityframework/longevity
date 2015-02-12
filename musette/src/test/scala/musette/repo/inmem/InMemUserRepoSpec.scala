package musette.repo.inmem

import musette.domain.User
import musette.repo.MusetteRepoSpec

class InMemUserRepoSpec extends MusetteRepoSpec[User] {

  private val repoLayer = new InMemRepoLayer
  def ename = "user"
  def repo = repoLayer.userRepo

}


