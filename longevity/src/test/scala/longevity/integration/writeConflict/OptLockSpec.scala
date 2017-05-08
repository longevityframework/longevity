package longevity.integration.writeConflict

import longevity.context.LongevityContext
import longevity.exceptions.persistence.WriteConflictException
import longevity.integration.model.basics
import longevity.test.LongevityIntegrationSpec
import org.scalatest.FlatSpec
import scala.concurrent.ExecutionContext.{ global => globalExecutionContext }

/** base class for testing optimistic locking */
abstract class OptLockSpec(
  protected val longevityContext: LongevityContext)
extends FlatSpec with LongevityIntegrationSpec {

  override protected implicit val executionContext = globalExecutionContext

  private val generator = longevityContext.testDataGenerator
  private val repo = longevityContext.testRepo

  behavior of "Repo.{update,delete} when the original PState comes from a create"

  it should "throw exception when an update beats an update" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = repo.create(basic).futureValue

    val modified1 = generator.generate[basics.Basics]
    val modifiedPState1 = createdPState.set(modified1)
    val updated1 = repo.update(modifiedPState1).futureValue

    val modified2 = generator.generate[basics.Basics]
    val modifiedPState2 = createdPState.set(modified2)

    val failure2 = repo.update(modifiedPState2).failed.futureValue
    failure2 shouldBe a [WriteConflictException[_]]
  } 

  it should "throw exception when a delete beats an update" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = repo.create(basic).futureValue

    val deleted1 = repo.delete(createdPState).futureValue

    val modified2 = generator.generate[basics.Basics]
    val modifiedPState2 = createdPState.set(modified2)
    val failure2 = repo.update(modifiedPState2).failed.futureValue
    failure2 shouldBe a [WriteConflictException[_]]
  } 

  it should "throw exception when an update beats a delete" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = repo.create(basic).futureValue

    val modified1 = generator.generate[basics.Basics]
    val modifiedPState1 = createdPState.set(modified1)
    val updated1 = repo.update(modifiedPState1).futureValue

    val failure2 = repo.delete(createdPState).failed.futureValue
    failure2 shouldBe a [WriteConflictException[_]]
  } 

  it should "throw exception when a delete beats a delete" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = repo.create(basic).futureValue

    val deleted1 = repo.delete(createdPState).futureValue

    val failure2 = repo.delete(createdPState).failed.futureValue
    failure2 shouldBe a [WriteConflictException[_]]
  } 

  behavior of "Repo.{update,delete} when the original PState comes from a retrieve"

  it should "throw exception when an update beats an update" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = repo.create(basic).futureValue
    val retrievedPState = repo.retrieveOne[basics.Basics, basics.BasicsId](basic.id).futureValue

    val modified1 = generator.generate[basics.Basics]
    val modifiedPState1 = retrievedPState.set(modified1)
    val updated1 = repo.update(modifiedPState1).futureValue

    val modified2 = generator.generate[basics.Basics]
    val modifiedPState2 = retrievedPState.set(modified2)

    val failure2 = repo.update(modifiedPState2).failed.futureValue
    failure2 shouldBe a [WriteConflictException[_]]
  } 

  it should "throw exception when a delete beats an update" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = repo.create(basic).futureValue
    val retrievedPState = repo.retrieveOne[basics.Basics, basics.BasicsId](basic.id).futureValue

    val deleted1 = repo.delete(retrievedPState).futureValue

    val modified2 = generator.generate[basics.Basics]
    val modifiedPState2 = retrievedPState.set(modified2)
    val failure2 = repo.update(modifiedPState2).failed.futureValue
    failure2 shouldBe a [WriteConflictException[_]]
  } 

  it should "throw exception when an update beats a delete" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = repo.create(basic).futureValue
    val retrievedPState = repo.retrieveOne[basics.Basics, basics.BasicsId](basic.id).futureValue

    val modified1 = generator.generate[basics.Basics]
    val modifiedPState1 = retrievedPState.set(modified1)
    val updated1 = repo.update(modifiedPState1).futureValue

    val failure2 = repo.delete(retrievedPState).failed.futureValue
    failure2 shouldBe a [WriteConflictException[_]]
  } 

  it should "throw exception when a delete beats a delete" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = repo.create(basic).futureValue
    val retrievedPState = repo.retrieveOne[basics.Basics, basics.BasicsId](basic.id).futureValue

    val deleted1 = repo.delete(retrievedPState).futureValue

    val failure2 = repo.delete(retrievedPState).failed.futureValue
    failure2 shouldBe a [WriteConflictException[_]]
  } 

  behavior of "Repo.{update,delete} when the original PState comes from a update"

  it should "throw exception when an update beats an update" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = repo.create(basic).futureValue
    val updatedPState = repo.update(createdPState.set(generator.generate[basics.Basics])).futureValue

    val modified1 = generator.generate[basics.Basics]
    val modifiedPState1 = updatedPState.set(modified1)
    val updated1 = repo.update(modifiedPState1).futureValue

    val modified2 = generator.generate[basics.Basics]
    val modifiedPState2 = updatedPState.set(modified2)

    val failure2 = repo.update(modifiedPState2).failed.futureValue
    failure2 shouldBe a [WriteConflictException[_]]
  } 

  it should "throw exception when a delete beats an update" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = repo.create(basic).futureValue
    val updatedPState = repo.update(createdPState.set(generator.generate[basics.Basics])).futureValue

    val deleted1 = repo.delete(updatedPState).futureValue

    val modified2 = generator.generate[basics.Basics]
    val modifiedPState2 = updatedPState.set(modified2)
    val failure2 = repo.update(modifiedPState2).failed.futureValue
    failure2 shouldBe a [WriteConflictException[_]]
  } 

  it should "throw exception when an update beats a delete" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = repo.create(basic).futureValue
    val updatedPState = repo.update(createdPState.set(generator.generate[basics.Basics])).futureValue

    val modified1 = generator.generate[basics.Basics]
    val modifiedPState1 = updatedPState.set(modified1)
    val updated1 = repo.update(modifiedPState1).futureValue

    val failure2 = repo.delete(updatedPState).failed.futureValue
    failure2 shouldBe a [WriteConflictException[_]]
  } 

  it should "throw exception when a delete beats a delete" in {
    val basic = generator.generate[basics.Basics]
    val createdPState = repo.create(basic).futureValue
    val updatedPState = repo.update(createdPState.set(generator.generate[basics.Basics])).futureValue

    val deleted1 = repo.delete(updatedPState).futureValue

    val failure2 = repo.delete(updatedPState).failed.futureValue
    failure2 shouldBe a [WriteConflictException[_]]
  } 

}
