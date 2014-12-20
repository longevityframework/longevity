package musette
package repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.InMemRepoSpec
import domain.testUtils._
import domain.User

class InMemUserRepoSpec extends InMemRepoSpec[User] {

  private val repoLayer = new InMemRepoLayer
  val entityTypeName = "user"
  val repo = repoLayer.userRepo
  val testEntityGen = domain.testUtils.testEntityGen.user _
  val persistedShouldMatchUnpersisted = entityMatchers.persistedUserShouldMatchUnpersisted _

}


