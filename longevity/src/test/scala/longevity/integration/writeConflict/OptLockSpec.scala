package longevity.integration.writeConflict

import longevity.context.LongevityContext
import longevity.exceptions.persistence.WriteConflictException
import longevity.integration.model.basics
import longevity.test.LongevityIntegrationSpec
import org.scalatest.FlatSpec
import scala.concurrent.Future

/** base class for testing optimistic locking */
abstract class OptLockSpec(
  protected val longevityContext: LongevityContext[Future, basics.DomainModel])
extends FlatSpec with LongevityIntegrationSpec[Future, basics.DomainModel] {

  private val generator = longevityContext.testDataGenerator
  private val repo = longevityContext.testRepo
  private val effect = longevityContext.effect

  behavior of "Repo.{update,delete} when the original PState comes from a create"

  it should "throw exception when an update beats an update" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = effect.run(repo.create(basic))

    val modified1 = generator.generate[basics.Basics]
    val modifiedPState1 = createdPState.set(modified1)
    val updated1 = effect.run(repo.update(modifiedPState1))

    val modified2 = generator.generate[basics.Basics]
    val modifiedPState2 = createdPState.set(modified2)

    intercept[WriteConflictException[_]] {
      effect.run(repo.update(modifiedPState2))
    }
  } 

  it should "throw exception when a delete beats an update" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = effect.run(repo.create(basic))

    val deleted1 = effect.run(repo.delete(createdPState))

    val modified2 = generator.generate[basics.Basics]
    val modifiedPState2 = createdPState.set(modified2)
    intercept[WriteConflictException[_]] {
      effect.run(repo.update(modifiedPState2))
    }
  } 

  it should "throw exception when an update beats a delete" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = effect.run(repo.create(basic))

    val modified1 = generator.generate[basics.Basics]
    val modifiedPState1 = createdPState.set(modified1)
    val updated1 = effect.run(repo.update(modifiedPState1))

    intercept[WriteConflictException[_]] {
      effect.run(repo.delete(createdPState))
    }
  } 

  it should "throw exception when a delete beats a delete" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = effect.run(repo.create(basic))

    val deleted1 = effect.run(repo.delete(createdPState))

    intercept[WriteConflictException[_]] {
      effect.run(repo.delete(createdPState))
    }
  } 

  behavior of "Repo.{update,delete} when the original PState comes from a retrieve"

  it should "throw exception when an update beats an update" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = effect.run(repo.create(basic))
    val retrievedPState = effect.run(repo.retrieveOne[basics.Basics](basic.id))

    val modified1 = generator.generate[basics.Basics]
    val modifiedPState1 = retrievedPState.set(modified1)
    val updated1 = effect.run(repo.update(modifiedPState1))

    val modified2 = generator.generate[basics.Basics]
    val modifiedPState2 = retrievedPState.set(modified2)

    intercept[WriteConflictException[_]] {
      effect.run(repo.update(modifiedPState2))
    }
  } 

  it should "throw exception when a delete beats an update" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = effect.run(repo.create(basic))
    val retrievedPState = effect.run(repo.retrieveOne[basics.Basics](basic.id))

    val deleted1 = effect.run(repo.delete(retrievedPState))

    val modified2 = generator.generate[basics.Basics]
    val modifiedPState2 = retrievedPState.set(modified2)
    intercept[WriteConflictException[_]] {
      effect.run(repo.update(modifiedPState2))
    }
  } 

  it should "throw exception when an update beats a delete" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = effect.run(repo.create(basic))
    val retrievedPState = effect.run(repo.retrieveOne[basics.Basics](basic.id))

    val modified1 = generator.generate[basics.Basics]
    val modifiedPState1 = retrievedPState.set(modified1)
    val updated1 = effect.run(repo.update(modifiedPState1))

    intercept[WriteConflictException[_]] {
      effect.run(repo.delete(retrievedPState))
    }
  } 

  it should "throw exception when a delete beats a delete" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = effect.run(repo.create(basic))
    val retrievedPState = effect.run(repo.retrieveOne[basics.Basics](basic.id))

    val deleted1 = effect.run(repo.delete(retrievedPState))

    intercept[WriteConflictException[_]] {
      effect.run(repo.delete(retrievedPState))
    }
  } 

  behavior of "Repo.{update,delete} when the original PState comes from a update"

  it should "throw exception when an update beats an update" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = effect.run(repo.create(basic))
    val updatedPState = effect.run(repo.update(createdPState.set(generator.generate[basics.Basics])))

    val modified1 = generator.generate[basics.Basics]
    val modifiedPState1 = updatedPState.set(modified1)
    val updated1 = effect.run(repo.update(modifiedPState1))

    val modified2 = generator.generate[basics.Basics]
    val modifiedPState2 = updatedPState.set(modified2)

    intercept[WriteConflictException[_]] {
      effect.run(repo.update(modifiedPState2))
    }
  } 

  it should "throw exception when a delete beats an update" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = effect.run(repo.create(basic))
    val updatedPState = effect.run(repo.update(createdPState.set(generator.generate[basics.Basics])))

    val deleted1 = effect.run(repo.delete(updatedPState))

    val modified2 = generator.generate[basics.Basics]
    val modifiedPState2 = updatedPState.set(modified2)
    intercept[WriteConflictException[_]] {
      effect.run(repo.update(modifiedPState2))
    }
  } 

  it should "throw exception when an update beats a delete" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = effect.run(repo.create(basic))
    val updatedPState = effect.run(repo.update(createdPState.set(generator.generate[basics.Basics])))

    val modified1 = generator.generate[basics.Basics]
    val modifiedPState1 = updatedPState.set(modified1)
    val updated1 = effect.run(repo.update(modifiedPState1))

    intercept[WriteConflictException[_]] {
      effect.run(repo.delete(updatedPState))
    }
  } 

  it should "throw exception when a delete beats a delete" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = effect.run(repo.create(basic))
    val updatedPState = effect.run(repo.update(createdPState.set(generator.generate[basics.Basics])))

    val deleted1 = effect.run(repo.delete(updatedPState))

    intercept[WriteConflictException[_]] {
      effect.run(repo.delete(updatedPState))
    }
  } 

}
