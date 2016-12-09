package longevity.integration.poly

import longevity.context.LongevityContext
import longevity.exceptions.persistence.NotInSubdomainTranslationException
import longevity.exceptions.persistence.PStateChangesDerivedPTypeException
import longevity.integration.model.derived
import longevity.persistence.RepoPool
import longevity.model.query.Query
import longevity.model.query.QueryFilter
import longevity.test.LongevityIntegrationSpec
import org.scalatest.FlatSpec
import scala.concurrent.ExecutionContext.{ global => globalExecutionContext }

object PolyReposSpec {

  case class DerivedNotInSubdomain(
    id: derived.PolyPersistentId,
    component: derived.PolyComponent)
  extends derived.PolyPersistent

}

/** base class for testing repos that share tables in the presence of [[PolyCType]] */
abstract class PolyReposSpec(
  protected val longevityContext: LongevityContext)
extends FlatSpec with LongevityIntegrationSpec {

  private val repoPool: RepoPool = longevityContext.testRepoPool

  override protected implicit val executionContext = globalExecutionContext

  private val testDataGenerator = longevityContext.testDataGenerator

  behavior of "Repo[PolyPersistent].retrieve"

  it should "retrieve by KeyVal a FirstDerivedPersistent persisted by Repo[FirstDerivedPersistent]" in {
    val firstDerivedPersistent = testDataGenerator.generate[derived.FirstDerivedPersistent]
    val createdPState = repoPool[derived.FirstDerivedPersistent].create(firstDerivedPersistent).futureValue

    val retrievedPStateOpt = repoPool[derived.PolyPersistent].retrieve(firstDerivedPersistent.id).futureValue
    retrievedPStateOpt should be ('nonEmpty)
    retrievedPStateOpt.get.get should equal (firstDerivedPersistent)
  } 

  behavior of "Repo[FirstDerivedPersistent].retrieve"

  it should "retrieve by KeyVal a FirstDerivedPersistent persisted by Repo[PolyPersistent]" in {
    val firstDerivedPersistent = testDataGenerator.generate[derived.FirstDerivedPersistent]
    val createdPState = repoPool[derived.PolyPersistent].create(firstDerivedPersistent).futureValue

    val retrievedPStateOpt = repoPool[derived.FirstDerivedPersistent].retrieve(
      firstDerivedPersistent.component.id
    ).futureValue
    retrievedPStateOpt should be ('nonEmpty)
    retrievedPStateOpt.get.get should equal (firstDerivedPersistent)
  } 

  it should "not retrieve a SecondDerivedPersistent by KeyVal[FirstDerivedPersistent]" in {
    val secondDerivedPersistent = testDataGenerator.generate[derived.SecondDerivedPersistent]
    val createdPState = repoPool[derived.SecondDerivedPersistent].create(secondDerivedPersistent).futureValue

    val retrievedPStateOpt = repoPool[derived.FirstDerivedPersistent].retrieve(
      secondDerivedPersistent.component.id
    ).futureValue
    retrievedPStateOpt should be ('empty)
  } 

  behavior of "Repo[PolyPersistent].retrieveByQuery"

  it should "retrieve a FirstDerivedPersistent persisted by Repo[FirstDerivedPersistent]" in {
    val firstDerivedPersistent = testDataGenerator.generate[derived.FirstDerivedPersistent]
    val createdPState = repoPool[derived.FirstDerivedPersistent].create(firstDerivedPersistent).futureValue

    val query: Query[derived.PolyPersistent] =
      Query(QueryFilter.eqs(derived.PolyPersistent.props.id, firstDerivedPersistent.id))

    val retrievedPStateSeq = repoPool[derived.PolyPersistent].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedPersistent)
  } 

  behavior of "Repo[FirstDerivedPersistent].retrieveByQuery"

  it should "retrieve a FirstDerivedPersistent persisted by Repo[PolyPersistent]" in {
    val firstDerivedPersistent = testDataGenerator.generate[derived.FirstDerivedPersistent]
    val createdPState = repoPool[derived.PolyPersistent].create(firstDerivedPersistent).futureValue

    val query: Query[derived.FirstDerivedPersistent] =
      Query(QueryFilter.eqs(
        derived.FirstDerivedPersistent.props.component.id,
        firstDerivedPersistent.component.id))

    val retrievedPStateSeq = repoPool[derived.FirstDerivedPersistent].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedPersistent)
  } 

  it should "not retrieve a SecondDerivedPersistent" in {
    val secondDerivedPersistent = testDataGenerator.generate[derived.SecondDerivedPersistent]
    val createdPState = repoPool[derived.PolyPersistent].create(secondDerivedPersistent).futureValue

    val query: Query[derived.FirstDerivedPersistent] =
      Query(QueryFilter.eqs(
        derived.FirstDerivedPersistent.props.component.id,
        secondDerivedPersistent.component.id))

    val retrievedPStateSeq = repoPool[derived.FirstDerivedPersistent].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (0)
  } 

  it should "retrieve a FirstDerivedPersistent by Query with mixed props" in {
    val firstDerivedPersistent = testDataGenerator.generate[derived.FirstDerivedPersistent]
    val createdPState = repoPool[derived.PolyPersistent].create(firstDerivedPersistent).futureValue

    val query: Query[derived.FirstDerivedPersistent] =
      Query(
        QueryFilter.and(
          QueryFilter.eqs(derived.FirstDerivedPersistent.props.component.id, firstDerivedPersistent.component.id),
          QueryFilter.eqs(derived.PolyPersistent.props.id, firstDerivedPersistent.id)))

    val retrievedPStateSeq = repoPool[derived.FirstDerivedPersistent].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedPersistent)
  } 

  it should "retrieve a FirstDerivedPersistent by Query DSL with mixed props" in {
    val firstDerivedPersistent = testDataGenerator.generate[derived.FirstDerivedPersistent]
    val createdPState = repoPool[derived.PolyPersistent].create(firstDerivedPersistent).futureValue

    import derived.FirstDerivedPersistent.queryDsl._
    val query: Query[derived.FirstDerivedPersistent] =
      derived.FirstDerivedPersistent.props.component.id eqs firstDerivedPersistent.component.id and
      derived.PolyPersistent.props.id eqs firstDerivedPersistent.id

    val retrievedPStateSeq = repoPool[derived.FirstDerivedPersistent].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedPersistent)
  } 

  behavior of "Repo[PolyPersistent].create"

  it should "throw exception on a subclass of PolyPersistent that is not in the domain model" in {
    val derivedNotInSubdomain = generateDerivedNotInSubdomain

    intercept[NotInSubdomainTranslationException] {
      repoPool[derived.PolyPersistent].create(derivedNotInSubdomain)
    }
  } 

  behavior of "Repo[PolyPersistent].update"

  it should "throw exception on attempt to change the derived type of the PState" in {
    val firstDerivedPersistent = testDataGenerator.generate[derived.FirstDerivedPersistent]
    val createdPState = repoPool[derived.PolyPersistent].create(firstDerivedPersistent).futureValue

    val secondDerivedPersistent = testDataGenerator.generate[derived.SecondDerivedPersistent]
    val modifiedPState = createdPState.set(secondDerivedPersistent)

    intercept[PStateChangesDerivedPTypeException] {
      repoPool[derived.PolyPersistent].update(modifiedPState)
    }
  } 

  private def generateDerivedNotInSubdomain =
    PolyReposSpec.DerivedNotInSubdomain(
      testDataGenerator.generate[derived.PolyPersistentId],
      testDataGenerator.generate[derived.PolyComponent])

}
