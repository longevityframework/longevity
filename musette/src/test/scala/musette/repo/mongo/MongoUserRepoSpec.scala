package musette.repo.mongo

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtil.RepoSpec
import musette.domain.testUtil._
import musette.domain.User

class MongoUserRepoSpec extends RepoSpec[User] {

  private val repoLayer = new MongoRepoLayer
  def ename = "user"
  def repo = repoLayer.userRepo
  def domainConfig = musette.domain.domainConfig
  def persistedShouldMatchUnpersisted = entityMatchers.persistedUserShouldMatchUnpersisted _

}


