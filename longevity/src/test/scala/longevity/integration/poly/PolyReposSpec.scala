package longevity.integration.poly

import longevity.effect.Effect
import longevity.context.LongevityContext
import longevity.exceptions.persistence.PStateChangesDerivedPTypeException
import longevity.integration.model.derived
import longevity.model.query.Query
import longevity.model.query.QueryFilter
import longevity.test.LongevityIntegrationSpec
import org.scalatest.FlatSpec

/** base class for testing repos that share tables in the presence of [[PolyCType]] */
abstract class PolyReposSpec[F[_] : Effect](
  protected val longevityContext: LongevityContext[F, derived.DomainModel])
extends FlatSpec with LongevityIntegrationSpec[F, derived.DomainModel] {

  private val repo = longevityContext.testRepo
  private val effect = implicitly[Effect[F]]

  private val testDataGenerator = longevityContext.testDataGenerator

  behavior of "Repo[PolyPersistent].retrieve"

  it should "retrieve by KeyVal a FirstDerivedPersistent persisted by Repo[FirstDerivedPersistent]" in {
    val firstDerivedPersistent = testDataGenerator.generateP[derived.FirstDerivedPersistent]
    val createdPState = effect.run(repo.create(firstDerivedPersistent))

    val retrievedPStateOpt = effect.run(repo.retrieve[derived.PolyPersistent](firstDerivedPersistent.id))
    retrievedPStateOpt should be ('nonEmpty)
    retrievedPStateOpt.get.get should equal (firstDerivedPersistent)
  } 

  behavior of "Repo[FirstDerivedPersistent].retrieve"

  it should "retrieve by KeyVal a FirstDerivedPersistent persisted by Repo[PolyPersistent]" in {
    val firstDerivedPersistent = testDataGenerator.generateP[derived.FirstDerivedPersistent]
    val createdPState = effect.run(repo.create(firstDerivedPersistent))

    val retrievedPStateOpt = effect.run(repo.retrieve[derived.FirstDerivedPersistent](
      firstDerivedPersistent.component.id
    ))
    retrievedPStateOpt should be ('nonEmpty)
    retrievedPStateOpt.get.get should equal (firstDerivedPersistent)
  } 

  it should "not retrieve a SecondDerivedPersistent by KeyVal[FirstDerivedPersistent]" in {
    val secondDerivedPersistent = testDataGenerator.generateP[derived.SecondDerivedPersistent]
    val createdPState = effect.run(repo.create(secondDerivedPersistent))

    val retrievedPStateOpt = effect.run(repo.retrieve[derived.FirstDerivedPersistent](
      secondDerivedPersistent.component.id
    ))
    retrievedPStateOpt should be ('empty)
  } 

  behavior of "Repo[PolyPersistent].queryToVector"

  it should "retrieve a FirstDerivedPersistent persisted by Repo[FirstDerivedPersistent]" in {
    val firstDerivedPersistent = testDataGenerator.generateP[derived.FirstDerivedPersistent]
    val createdPState = effect.run(repo.create(firstDerivedPersistent))

    val query: Query[derived.PolyPersistent] =
      Query(QueryFilter.eqs(derived.PolyPersistent.props.id, firstDerivedPersistent.id))

    val retrievedPStateSeq = effect.run(repo.queryToVector(query))
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedPersistent)
  } 

  behavior of "Repo[FirstDerivedPersistent].queryToVector"

  it should "retrieve a FirstDerivedPersistent persisted by Repo[PolyPersistent]" in {
    val firstDerivedPersistent = testDataGenerator.generateP[derived.FirstDerivedPersistent]
    val createdPState = effect.run(repo.create(firstDerivedPersistent))

    val query: Query[derived.FirstDerivedPersistent] =
      Query(QueryFilter.eqs(
        derived.FirstDerivedPersistent.props.component.id,
        firstDerivedPersistent.component.id))

    val retrievedPStateSeq = effect.run(repo.queryToVector(query))
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedPersistent)
  } 

  it should "not retrieve a SecondDerivedPersistent" in {
    val secondDerivedPersistent = testDataGenerator.generateP[derived.SecondDerivedPersistent]
    val createdPState = effect.run(repo.create(secondDerivedPersistent))

    val query: Query[derived.FirstDerivedPersistent] =
      Query(QueryFilter.eqs(
        derived.FirstDerivedPersistent.props.component.id,
        secondDerivedPersistent.component.id))

    val retrievedPStateSeq = effect.run(repo.queryToVector(query))
    retrievedPStateSeq.size should equal (0)
  } 

  it should "retrieve a FirstDerivedPersistent by Query with mixed props" in {
    val firstDerivedPersistent = testDataGenerator.generateP[derived.FirstDerivedPersistent]
    val createdPState = effect.run(repo.create(firstDerivedPersistent))

    val query: Query[derived.FirstDerivedPersistent] =
      Query(
        QueryFilter.and(
          QueryFilter.eqs(derived.FirstDerivedPersistent.props.component.id, firstDerivedPersistent.component.id),
          QueryFilter.eqs(derived.PolyPersistent.props.id, firstDerivedPersistent.id)))

    val retrievedPStateSeq = effect.run(repo.queryToVector(query))
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedPersistent)
  } 

  it should "retrieve a FirstDerivedPersistent by Query DSL with mixed props" in {
    val firstDerivedPersistent = testDataGenerator.generateP[derived.FirstDerivedPersistent]
    val createdPState = effect.run(repo.create(firstDerivedPersistent))

    import derived.FirstDerivedPersistent.queryDsl._
    val query: Query[derived.FirstDerivedPersistent] =
      derived.FirstDerivedPersistent.props.component.id eqs firstDerivedPersistent.component.id and
      derived.PolyPersistent.props.id eqs firstDerivedPersistent.id

    val retrievedPStateSeq = effect.run(repo.queryToVector(query))
    retrievedPStateSeq.size should equal (1)
    retrievedPStateSeq(0).get should equal (firstDerivedPersistent)
  } 

  behavior of "Repo[PolyPersistent].update"

  it should "throw exception on attempt to change the derived type of the PState" in {
    val firstDerivedPersistent = testDataGenerator.generateP[derived.FirstDerivedPersistent]
    val createdPState = effect.run(repo.create[derived.PolyPersistent](firstDerivedPersistent))

    val secondDerivedPersistent = testDataGenerator.generateP[derived.SecondDerivedPersistent]
    val modifiedPState = createdPState.set(secondDerivedPersistent)

    intercept[PStateChangesDerivedPTypeException] {
      effect.run(repo.update(modifiedPState))
    }
  } 

}
