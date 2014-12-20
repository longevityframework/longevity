package longevity.testUtils

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.repo._
import longevity.domain._

class InMemRepoSpec[E <: Entity](
  val entityTypeName: String,
  val repo: Repo[E],
  val testEntityGen: () => E,
  val persistedShouldMatchUnpersisted: (E, E) => Unit
)
extends FeatureSpec with GivenWhenThen with Matchers {

  feature(s"${entityTypeName}Repo.create") {
    scenario(s"should produce a persisted $entityTypeName") {
      Given(s"an unpersisted $entityTypeName")
      val unpersisted = testEntityGen()
      When("we persist the $entityTypeName")
      val persistentState = repo.create(unpersisted)
      Then("we get back the $entityTypeName persistent state")
      persistentState shouldBe a [Persisted[_]]
      persistedShouldMatchUnpersisted(persistentState.get, unpersisted)
      persistentState.isError should be (false)
    }
  }

  // TODO: read update delete

}
