package musette
package repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._

import domain.testUtils._

class InMemUserRepoSpec extends FeatureSpec with GivenWhenThen with Matchers {

  private val repoLayer = new InMemRepoLayer

  // TODO: establish the repo layer
  // TODO: test data generators

  feature("The user repo client can create new persistent users") {

    scenario("create a valid new user") {
      Given("a valid but unpersisted user")
      val user = testEntityGen.user()
      When("we create a persisted user")
      val userP = repoLayer.userRepo.create(user)
      Then("we get back the user persistent state")
      // TODO

      val blog = testEntityGen.blog()
      println(blog)
      val blogP = repoLayer.blogRepo.create(blog)
      println(blogP.get)

    }

    // todo: uri unique constraint

  }

  // TODO: read update delete

}
