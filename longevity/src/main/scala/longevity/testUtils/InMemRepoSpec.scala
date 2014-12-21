package longevity.testUtils

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.repo._
import longevity.domain._

trait InMemRepoSpec[E <: Entity] extends FeatureSpec with GivenWhenThen with Matchers {

  def entityTypeName: String
  def repo: Repo[E]
  def testEntityGen: () => E
  def persistedShouldMatchUnpersisted: (E, E) => Unit

  feature(s"${entityTypeName}Repo.create") {
    scenario(s"should produce a persisted $entityTypeName") {
      Given(s"an unpersisted $entityTypeName")
      val unpersisted = testEntityGen()
      When(s"we persist the $entityTypeName")
      val persistentState = repo.create(unpersisted)
      Then(s"we get back the $entityTypeName persistent state")
      persistentState.isError should be (false)
      persistentState shouldBe a [Persisted[_]]
      persistedShouldMatchUnpersisted(persistentState.get, unpersisted)
    }
  }

  // TODO: read update delete

}
