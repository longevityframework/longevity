package longevity.integration.poly

import longevity.context.LongevityContext
import longevity.exceptions.persistence.NotInSubdomainTranslationException
import longevity.exceptions.persistence.PStateChangesDerivedPTypeException
import longevity.integration.subdomain.derivedEntities
import longevity.persistence.RepoPool
import longevity.subdomain.ptype.Query
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._
import scala.concurrent.ExecutionContext.Implicits.global

object PolyReposSpec {

  case class DerivedNotInSubdomain(
    id: derivedEntities.PolyRootId,
    component: derivedEntities.PolyEntity)
  extends derivedEntities.PolyRoot

}

/** base class for testing repos that share tables in the presence of [[PolyType]] */
abstract class PolyReposSpec(
  protected val longevityContext: LongevityContext,
  protected val repoPool: RepoPool)
extends FlatSpec
with GivenWhenThen
with Matchers
with ScalaFutures {

  override implicit def patienceConfig = PatienceConfig(
    timeout = scaled(4000.millis),
    interval = scaled(50.millis))

  private val testDataGenerator = longevityContext.testDataGenerator

  behavior of "Repo[PolyRoot].retrieve"

  it should "retrieve by KeyVal a FirstDerivedRoot persisted by Repo[FirstDerivedRoot]" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.FirstDerivedRoot].create(firstDerivedRoot).futureValue

    val retrievedPStateOpt = repoPool[derivedEntities.PolyRoot].retrieve(firstDerivedRoot.id).futureValue
    retrievedPStateOpt should be ('nonEmpty)
    retrievedPStateOpt.get.get should equal (firstDerivedRoot)
  } 

  behavior of "Repo[FirstDerivedRoot].retrieve"

  it should "retrieve by KeyVal a FirstDerivedRoot persisted by Repo[PolyRoot]" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.PolyRoot].create(firstDerivedRoot).futureValue

    val retrievedPStateOpt = repoPool[derivedEntities.FirstDerivedRoot].retrieve(
      firstDerivedRoot.component.id
    ).futureValue
    retrievedPStateOpt should be ('nonEmpty)
    retrievedPStateOpt.get.get should equal (firstDerivedRoot)
  } 

  it should "not retrieve a SecondDerivedRoot by KeyVal[FirstDerivedRoot]" in {
    val secondDerivedRoot = testDataGenerator.generate[derivedEntities.SecondDerivedRoot]
    val createdPState = repoPool[derivedEntities.SecondDerivedRoot].create(secondDerivedRoot).futureValue

    val retrievedPStateOpt = repoPool[derivedEntities.FirstDerivedRoot].retrieve(
      secondDerivedRoot.component.id
    ).futureValue
    retrievedPStateOpt should be ('empty)
  } 

  behavior of "Repo[PolyRoot].retrieveByQuery"

  it should "retrieve a FirstDerivedRoot persisted by Repo[FirstDerivedRoot]" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.FirstDerivedRoot].create(firstDerivedRoot).futureValue

    val query: Query[derivedEntities.PolyRoot] =
      Query.eqs(derivedEntities.PolyRoot.props.id, firstDerivedRoot.id)

    val retrievedPStateSeq = repoPool[derivedEntities.PolyRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedRoot)
  } 

  behavior of "Repo[FirstDerivedRoot].retrieveByQuery"

  it should "retrieve a FirstDerivedRoot persisted by Repo[PolyRoot]" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.PolyRoot].create(firstDerivedRoot).futureValue

    val query: Query[derivedEntities.FirstDerivedRoot] =
      Query.eqs(derivedEntities.FirstDerivedRoot.props.componentId, firstDerivedRoot.component.id)

    val retrievedPStateSeq = repoPool[derivedEntities.FirstDerivedRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedRoot)
  } 

  it should "not retrieve a SecondDerivedRoot" in {
    val secondDerivedRoot = testDataGenerator.generate[derivedEntities.SecondDerivedRoot]
    val createdPState = repoPool[derivedEntities.PolyRoot].create(secondDerivedRoot).futureValue

    val query: Query[derivedEntities.FirstDerivedRoot] =
      Query.eqs(derivedEntities.FirstDerivedRoot.props.componentId, secondDerivedRoot.component.id)

    val retrievedPStateSeq = repoPool[derivedEntities.FirstDerivedRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (0)
  } 

  it should "retrieve a FirstDerivedRoot by Query with mixed props" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.PolyRoot].create(firstDerivedRoot).futureValue

    val query: Query[derivedEntities.FirstDerivedRoot] =
      Query.and(
        Query.eqs(derivedEntities.FirstDerivedRoot.props.componentId, firstDerivedRoot.component.id),
        Query.eqs(derivedEntities.PolyRoot.props.id, firstDerivedRoot.id))

    val retrievedPStateSeq = repoPool[derivedEntities.FirstDerivedRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedRoot)
  } 

  it should "retrieve a FirstDerivedRoot by Query DSL with mixed props" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.PolyRoot].create(firstDerivedRoot).futureValue

    import derivedEntities.FirstDerivedRoot.queryDsl._
    val query: Query[derivedEntities.FirstDerivedRoot] =
      derivedEntities.FirstDerivedRoot.props.componentId eqs firstDerivedRoot.component.id and
      derivedEntities.PolyRoot.props.id eqs firstDerivedRoot.id

    val retrievedPStateSeq = repoPool[derivedEntities.FirstDerivedRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedRoot)
  } 

  behavior of "Repo[PolyRoot].create"

  it should "throw exception on a subclass of PolyRoot that is not in the subdomain" in {
    val derivedNotInSubdomain = generateDerivedNotInSubdomain

    intercept[NotInSubdomainTranslationException] {
      repoPool[derivedEntities.PolyRoot].create(derivedNotInSubdomain)
    }
  } 

  behavior of "Repo[PolyRoot].update"

  it should "throw exception on attempt to change the derived type of the PState" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.PolyRoot].create(firstDerivedRoot).futureValue

    val secondDerivedRoot = testDataGenerator.generate[derivedEntities.SecondDerivedRoot]
    val modifiedPState = createdPState.set(secondDerivedRoot)

    intercept[PStateChangesDerivedPTypeException] {
      repoPool[derivedEntities.PolyRoot].update(modifiedPState)
    }
  } 

  private def generateDerivedNotInSubdomain =
    PolyReposSpec.DerivedNotInSubdomain(
      testDataGenerator.generate[derivedEntities.PolyRootId],
      testDataGenerator.generate[derivedEntities.PolyEntity])

}
