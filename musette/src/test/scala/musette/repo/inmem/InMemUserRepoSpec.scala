package musette
package repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.InMemRepoSpec
import domain.testUtils._
import domain.User

class InMemUserRepoSpec extends InMemRepoSpec[User] {

  private val repoLayer = new InMemRepoLayer
  def entityTypeName = "user"
  def repo = repoLayer.userRepo
  def testEntityGen = domain.testUtils.testEntityGen.user _
  def persistedShouldMatchUnpersisted = entityMatchers.persistedUserShouldMatchUnpersisted _

}


