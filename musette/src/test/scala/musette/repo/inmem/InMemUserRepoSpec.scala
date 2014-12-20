package musette
package repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.repo.Persisted
import domain.User
import domain.testUtils._

class InMemUserRepoSpec extends FeatureSpec with GivenWhenThen with Matchers {

  private val repoLayer = new InMemRepoLayer

  // TODO: establish the repo layer
  // TODO: test data generators

  feature("The user repo client can create new persistent users") {

    scenario("create a valid new user") {
      Given("an unpersisted user")
      val user = testEntityGen.user()
      When("we persist the  user")
      val userP = repoLayer.userRepo.create(user)
      Then("we get back the user persistent state")
      userP shouldBe a [Persisted[_]]
      entityMatchers.persistedShouldMatchUnpersisted(userP.get, user)
      userP.isError should be (false)
    }

    // todo: uri unique constraint

  }

  // TODO: read update delete

}
