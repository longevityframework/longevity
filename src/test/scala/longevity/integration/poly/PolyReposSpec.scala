package longevity.integration.poly

import longevity.context.LongevityContext
import longevity.integration.subdomain.derivedEntities
import longevity.persistence.RepoPool
import longevity.test.PersistedToUnpersistedMatcher
import longevity.test.TestDataGeneration
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.ExecutionContext

// TODO InMem and Mongo kids

/** base class for test repos that share tables in the presence of [[PolyType]] */
abstract class PolyReposSpec(
  protected val longevityContext: LongevityContext,
  protected val repoPool: RepoPool)
extends {
  protected implicit val executionContext = ExecutionContext.global
}
with FlatSpec
with GivenWhenThen
with Matchers
with ScalaFutures
with TestDataGeneration
with PersistedToUnpersistedMatcher {

  "setting up the test" should "trigger schema creation" in {

  }

  behavior of "Repo[PolyRoot].retrieve"

  it should "pull up a DerivedRoot persisted by a different repository" ignore {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.FirstDerivedRoot].create(firstDerivedRoot).futureValue
    val retrievedPStateOpt = repoPool[derivedEntities.PolyRoot].retrieve(createdPState.assoc).futureValue
    retrievedPStateOpt should be ('nonEmpty)
    retrievedPStateOpt.get.get should equal (firstDerivedRoot)
  } 

}
