package longevity.integration.poly

import longevity.context.LongevityContext
import longevity.integration.subdomain.derivedEntities
import longevity.persistence.PState
import longevity.persistence.RepoPool
import longevity.subdomain.Assoc
import longevity.subdomain.ptype.Query
import longevity.test.PersistedToUnpersistedMatcher
import longevity.test.TestDataGeneration
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.ExecutionContext

// TODO InMem and Mongo kids

/** base class for testing repos that share tables in the presence of [[PolyType]] */
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

  behavior of "Repo[PolyRoot].retrieve(PRef)"

  it should "retrieve by Assoc a FirstDerivedRoot persisted by Repo[FirstDerivedRoot]" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.FirstDerivedRoot].create(firstDerivedRoot).futureValue
    val assoc = createdPState.assoc.asInstanceOf[Assoc[derivedEntities.PolyRoot]]

    val retrievedPStateOpt = repoPool[derivedEntities.PolyRoot].retrieve(assoc).futureValue
    retrievedPStateOpt should be ('nonEmpty)
    retrievedPStateOpt.get.get should equal (firstDerivedRoot)
  } 

  it should "retrieve by KeyVal a FirstDerivedRoot persisted by Repo[FirstDerivedRoot]" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.FirstDerivedRoot].create(firstDerivedRoot).futureValue
    val uriKeyVal = derivedEntities.PolyRoot.keys.uri.keyValForP(createdPState.get)

    val retrievedPStateOpt = repoPool[derivedEntities.PolyRoot].retrieve(uriKeyVal).futureValue
    retrievedPStateOpt should be ('nonEmpty)
    retrievedPStateOpt.get.get should equal (firstDerivedRoot)
  } 

  behavior of "Repo[FirstDerivedRoot].retrieve(PRef)"

  it should "retrieve by Assoc a FirstDerivedRoot persisted by Repo[PolyRoot]" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.PolyRoot].create(firstDerivedRoot).futureValue
    val castPState = createdPState.asInstanceOf[PState[derivedEntities.FirstDerivedRoot]]
    val assoc = castPState.assoc

    val retrievedPStateOpt = repoPool[derivedEntities.FirstDerivedRoot].retrieve(assoc).futureValue
    retrievedPStateOpt should be ('nonEmpty)
    retrievedPStateOpt.get.get should equal (firstDerivedRoot)
  } 

  it should "retrieve by KeyVal a FirstDerivedRoot persisted by Repo[PolyRoot]" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.PolyRoot].create(firstDerivedRoot).futureValue
    val castPState = createdPState.asInstanceOf[PState[derivedEntities.FirstDerivedRoot]]
    val componentUriKeyVal = derivedEntities.FirstDerivedRoot.keys.componentUri.keyValForP(castPState.get)

    val retrievedPStateOpt = repoPool[derivedEntities.FirstDerivedRoot].retrieve(componentUriKeyVal).futureValue
    retrievedPStateOpt should be ('nonEmpty)
    retrievedPStateOpt.get.get should equal (firstDerivedRoot)
  } 

  it should "not retrieve a SecondDerivedRoot by KeyVal[FirstDerivedRoot]" in {
    val secondDerivedRoot = testDataGenerator.generate[derivedEntities.SecondDerivedRoot]
    val createdPState = repoPool[derivedEntities.SecondDerivedRoot].create(secondDerivedRoot).futureValue
    val componentUriKeyVal = derivedEntities.FirstDerivedRoot.keys.componentUri(createdPState.get.component.uri)

    val retrievedPStateOpt = repoPool[derivedEntities.FirstDerivedRoot].retrieve(componentUriKeyVal).futureValue
    retrievedPStateOpt should be ('empty)
  } 

  behavior of "Repo[PolyRoot].retrieveByQuery"

  it should "retrieve a FirstDerivedRoot persisted by Repo[FirstDerivedRoot]" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.FirstDerivedRoot].create(firstDerivedRoot).futureValue

    val query: Query[derivedEntities.PolyRoot] =
      Query.eqs(derivedEntities.PolyRoot.props.uri, firstDerivedRoot.uri)

    val retrievedPStateSeq = repoPool[derivedEntities.PolyRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedRoot)
  } 

  behavior of "Repo[FirstDerivedRoot].retrieveByQuery"

  it should "retrieve a FirstDerivedRoot persisted by Repo[PolyRoot]" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.PolyRoot].create(firstDerivedRoot).futureValue

    val query: Query[derivedEntities.FirstDerivedRoot] =
      Query.eqs(derivedEntities.FirstDerivedRoot.props.componentUri, firstDerivedRoot.component.uri)

    val retrievedPStateSeq = repoPool[derivedEntities.FirstDerivedRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedRoot)
  } 

  it should "not retrieve a SecondDerivedRoot" in {
    val secondDerivedRoot = testDataGenerator.generate[derivedEntities.SecondDerivedRoot]
    val createdPState = repoPool[derivedEntities.PolyRoot].create(secondDerivedRoot).futureValue

    val query: Query[derivedEntities.FirstDerivedRoot] =
      Query.eqs(derivedEntities.FirstDerivedRoot.props.componentUri, secondDerivedRoot.component.uri)

    val retrievedPStateSeq = repoPool[derivedEntities.FirstDerivedRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (0)
  } 

  it should "retrieve a FirstDerivedRoot by Query with mixed props" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.PolyRoot].create(firstDerivedRoot).futureValue

    val query: Query[derivedEntities.FirstDerivedRoot] =
      Query.and(
        Query.eqs(derivedEntities.FirstDerivedRoot.props.componentUri, firstDerivedRoot.component.uri),
        Query.eqs(derivedEntities.PolyRoot.props.uri, firstDerivedRoot.uri))

    val retrievedPStateSeq = repoPool[derivedEntities.FirstDerivedRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedRoot)
  } 

  it should "retrieve a FirstDerivedRoot by Query DSL with mixed props" in {
    val firstDerivedRoot = testDataGenerator.generate[derivedEntities.FirstDerivedRoot]
    val createdPState = repoPool[derivedEntities.PolyRoot].create(firstDerivedRoot).futureValue

    // TODO: consider moving this DSL test into QueryDslSpec

    import derivedEntities.FirstDerivedRoot.queryDsl._
    val query: Query[derivedEntities.FirstDerivedRoot] =
      derivedEntities.FirstDerivedRoot.props.componentUri eqs firstDerivedRoot.component.uri and
      derivedEntities.PolyRoot.props.uri eqs firstDerivedRoot.uri

    val retrievedPStateSeq = repoPool[derivedEntities.FirstDerivedRoot].retrieveByQuery(query).futureValue
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedRoot)
  } 

}
