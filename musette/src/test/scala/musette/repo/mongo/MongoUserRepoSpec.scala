package musette.repo.mongo

import musette.domain.testUtil.entityMatchers
import musette.domain.User
import musette.repo.MusetteRepoSpec

class MongoUserRepoSpec extends MusetteRepoSpec[User] {

  private lazy val repoLayer = new MongoRepoLayer
  def ename = "user"
  def repo = repoLayer.userRepo
  def persistedShouldMatchUnpersisted = entityMatchers.persistedUserShouldMatchUnpersisted _

}


