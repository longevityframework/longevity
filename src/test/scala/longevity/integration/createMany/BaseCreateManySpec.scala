package longevity.integration.createMany

import longevity.context.LongevityContext
import longevity.persistence.RepoPool
import longevity.test.PersistedToUnpersistedMatcher
import longevity.test.TestDataGeneration
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

/** unit tests for [[RepoPoolSpec]] methods */
abstract class BaseCreateManySpec(
  protected val longevityContext: LongevityContext,
  protected val repoPool: RepoPool)
extends FlatSpec
with GivenWhenThen
with Matchers
with ScalaFutures
with TestDataGeneration
with PersistedToUnpersistedMatcher
