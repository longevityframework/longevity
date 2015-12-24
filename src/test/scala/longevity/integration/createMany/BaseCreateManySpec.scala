package longevity.integration.createMany

import longevity.context.LongevityContext
import longevity.test.PersistedToUnpersistedMatcher
import longevity.test.TestDataGeneration
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

/** unit tests for [[RepoPoolSpec]] methods */
abstract class BaseCreateManySpec(val longevityContext: LongevityContext)
extends FlatSpec
with GivenWhenThen
with Matchers
with ScalaFutures
with TestDataGeneration
with PersistedToUnpersistedMatcher
