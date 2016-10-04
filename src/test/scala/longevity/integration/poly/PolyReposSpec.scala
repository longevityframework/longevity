package longevity.integration.poly

import longevity.context.LongevityContext
import longevity.exceptions.persistence.NotInSubdomainTranslationException
import longevity.exceptions.persistence.PStateChangesDerivedPTypeException
import longevity.integration.subdomain.derived
import longevity.persistence.RepoPool
import longevity.subdomain.ptype.Query
import longevity.test.LongevityIntegrationSpec
import org.scalatest.FlatSpec
import scala.concurrent.ExecutionContext.{ global => globalExecutionContext }

object PolyReposSpec {

  case class DerivedNotInSubdomain(
    id: derived.PolyRootId,
    component: derived.PolyEmbeddable)
  extends derived.PolyRoot

}

/** base class for testing repos that share tables in the presence of [[PolyEType]] */
abstract class PolyReposSpec(
  protected val longevityContext: LongevityContext)
extends FlatSpec with LongevityIntegrationSpec {

  private val repoPool: RepoPool = longevityContext.testRepoPool

  override protected implicit val executionContext = globalExecutionContext

  private val testDataGenerator = longevityContext.testDataGenerator

  behavior of "Repo[PolyRoot].retrieve"

  it should "retrieve by KeyVal a FirstDerivedRoot persisted by Repo[FirstDerivedRoot]" in {
    val firstDerivedRoot = testDataGenerator.generate[derived.FirstDerivedRoot]
    val createdPState = repoPool[derived.FirstDerivedRoot].create(firstDerivedRoot).futureValue

    val retrievedPStateOpt = repoPool[derived.PolyRoot].retrieve(firstDerivedRoot.id).futureValue
    retrievedPStateOpt should be ('nonEmpty)
    retrievedPStateOpt.get.get should equal (firstDerivedRoot)
  } 

  behavior of "Repo[FirstDerivedRoot].retrieve"

  it should "retrieve by KeyVal a FirstDerivedRoot persisted by Repo[PolyRoot]" in {
    val firstDerivedRoot = testDataGenerator.generate[derived.FirstDerivedRoot]
    val createdPState = repoPool[derived.PolyRoot].create(firstDerivedRoot).futureValue

    val retrievedPStateOpt = repoPool[derived.FirstDerivedRoot].retrieve(
      firstDerivedRoot.component.id
    ).futureValue
    retrievedPStateOpt should be ('nonEmpty)
    retrievedPStateOpt.get.get should equal (firstDerivedRoot)
  } 

  it should "not retrieve a SecondDerivedRoot by KeyVal[FirstDerivedRoot]" in {
    val secondDerivedRoot = testDataGenerator.generate[derived.SecondDerivedRoot]
    val createdPState = repoPool[derived.SecondDerivedRoot].create(secondDerivedRoot).futureValue

    val retrievedPStateOpt = repoPool[derived.FirstDerivedRoot].retrieve(
      secondDerivedRoot.component.id
    ).futureValue
    retrievedPStateOpt should be ('empty)
  } 

  behavior of "Repo[PolyRoot].retrieveByQuery"

  it should "retrieve a FirstDerivedRoot persisted by Repo[FirstDerivedRoot]" in {
    val firstDerivedRoot = testDataGenerator.generate[derived.FirstDerivedRoot]
    val createdPState = repoPool[derived.FirstDerivedRoot].create(firstDerivedRoot).futureValue

    val query: Query[derived.PolyRoot] =
      Query.eqs(derived.PolyRoot.props.id, firstDerivedRoot.id)

    val retrievedPStateSeq = repoPool[derived.PolyRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedRoot)
  } 

  behavior of "Repo[FirstDerivedRoot].retrieveByQuery"

  it should "retrieve a FirstDerivedRoot persisted by Repo[PolyRoot]" in {
    val firstDerivedRoot = testDataGenerator.generate[derived.FirstDerivedRoot]
    val createdPState = repoPool[derived.PolyRoot].create(firstDerivedRoot).futureValue

    val query: Query[derived.FirstDerivedRoot] =
      Query.eqs(derived.FirstDerivedRoot.props.componentId, firstDerivedRoot.component.id)

    val retrievedPStateSeq = repoPool[derived.FirstDerivedRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedRoot)
  } 

  it should "not retrieve a SecondDerivedRoot" in {
    val secondDerivedRoot = testDataGenerator.generate[derived.SecondDerivedRoot]
    val createdPState = repoPool[derived.PolyRoot].create(secondDerivedRoot).futureValue

    val query: Query[derived.FirstDerivedRoot] =
      Query.eqs(derived.FirstDerivedRoot.props.componentId, secondDerivedRoot.component.id)

    val retrievedPStateSeq = repoPool[derived.FirstDerivedRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (0)
  } 

  it should "retrieve a FirstDerivedRoot by Query with mixed props" in {
    val firstDerivedRoot = testDataGenerator.generate[derived.FirstDerivedRoot]
    val createdPState = repoPool[derived.PolyRoot].create(firstDerivedRoot).futureValue

    val query: Query[derived.FirstDerivedRoot] =
      Query.and(
        Query.eqs(derived.FirstDerivedRoot.props.componentId, firstDerivedRoot.component.id),
        Query.eqs(derived.PolyRoot.props.id, firstDerivedRoot.id))

    val retrievedPStateSeq = repoPool[derived.FirstDerivedRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedRoot)
  } 

  it should "retrieve a FirstDerivedRoot by Query DSL with mixed props" in {
    val firstDerivedRoot = testDataGenerator.generate[derived.FirstDerivedRoot]
    val createdPState = repoPool[derived.PolyRoot].create(firstDerivedRoot).futureValue

    import derived.FirstDerivedRoot.queryDsl._
    val query: Query[derived.FirstDerivedRoot] =
      derived.FirstDerivedRoot.props.componentId eqs firstDerivedRoot.component.id and
      derived.PolyRoot.props.id eqs firstDerivedRoot.id

    val retrievedPStateSeq = repoPool[derived.FirstDerivedRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedRoot)
  } 

  behavior of "Repo[PolyRoot].create"

  it should "throw exception on a subclass of PolyRoot that is not in the subdomain" in {
    val derivedNotInSubdomain = generateDerivedNotInSubdomain

    intercept[NotInSubdomainTranslationException] {
      repoPool[derived.PolyRoot].create(derivedNotInSubdomain)
    }
  } 

  behavior of "Repo[PolyRoot].update"

  it should "throw exception on attempt to change the derived type of the PState" in {
    val firstDerivedRoot = testDataGenerator.generate[derived.FirstDerivedRoot]
    val createdPState = repoPool[derived.PolyRoot].create(firstDerivedRoot).futureValue

    val secondDerivedRoot = testDataGenerator.generate[derived.SecondDerivedRoot]
    val modifiedPState = createdPState.set(secondDerivedRoot)

    intercept[PStateChangesDerivedPTypeException] {
      repoPool[derived.PolyRoot].update(modifiedPState)
    }
  } 

  private def generateDerivedNotInSubdomain =
    PolyReposSpec.DerivedNotInSubdomain(
      testDataGenerator.generate[derived.PolyRootId],
      testDataGenerator.generate[derived.PolyEmbeddable])

}
