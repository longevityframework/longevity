package musette.repo.mongo

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.RepoSpec
import musette.domain.testUtils._
import musette.domain.User

class MongoUserRepoSpec extends RepoSpec[User] {

  private val repoLayer = new MongoRepoLayer
  def ename = "user"
  def repo = repoLayer.userRepo
  def genTestEntity = testEntityGen.user _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedUserShouldMatchUnpersisted _

}


