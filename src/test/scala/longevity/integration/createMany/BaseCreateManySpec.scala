package longevity.integration.createMany

import longevity.context.LongevityContext
import longevity.exceptions.persistence.AssocIsUnpersistedException
import longevity.integration.subdomain.withAssoc
import longevity.persistence.RepoPool
import longevity.test.PersistedToUnpersistedMatcher
import longevity.test.TestDataGeneration
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.ExecutionContext

/** base class for unit tests for the [[RepoPool.createMany]] method */
abstract class BaseCreateManySpec(
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

  def uri = testDataGenerator.generate[String]

  behavior of "RepoPool.createMany"

  it should "persist networks with unpersisted associations" in {
    val associated1 = withAssoc.Associated(uri)
    val withAssoc1 = withAssoc.WithAssoc(uri, associated1)
    val associated2 = withAssoc.Associated(uri)
    val withAssoc2 = withAssoc.WithAssoc(uri, associated2)

    val result = repoPool.createMany(associated1, withAssoc1, associated2, withAssoc2)
    val pstates = result.futureValue
    pstates.size should equal(4)
    persistedShouldMatchUnpersisted(pstates(0).get.asInstanceOf[withAssoc.Associated], associated1)
    persistedShouldMatchUnpersisted(pstates(1).get.asInstanceOf[withAssoc.WithAssoc], withAssoc1)
    persistedShouldMatchUnpersisted(pstates(2).get.asInstanceOf[withAssoc.Associated], associated2)
    persistedShouldMatchUnpersisted(pstates(3).get.asInstanceOf[withAssoc.WithAssoc], withAssoc2)
  }

  behavior of "Repo.create"

  it should "throw AssocIsUnpersistedException when the aggregate contains unpersisted assocs" in {
    val associated1 = withAssoc.Associated(uri)
    val withAssoc1 = withAssoc.WithAssoc(uri, associated1)
    val withAssocRepo = repoPool[withAssoc.WithAssoc]
    withAssocRepo.create(withAssoc1).failed.futureValue shouldBe an [AssocIsUnpersistedException]
  }

}
