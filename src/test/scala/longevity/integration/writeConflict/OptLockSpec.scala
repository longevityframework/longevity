package longevity.integration.writeConflict

import longevity.context.LongevityContext
import longevity.exceptions.persistence.WriteConflictException
import longevity.integration.subdomain.basics
import longevity.persistence.RepoPool
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._
import scala.concurrent.ExecutionContext.Implicits.global

/** base class for testing optimistic locking */
abstract class OptLockSpec(
  protected val longevityContext: LongevityContext,
  protected val repoPool: RepoPool)
extends FlatSpec
with GivenWhenThen
with Matchers
with ScalaFutures {

  private val generator = longevityContext.testDataGenerator
  private val repo = repoPool[basics.Basics]

  override implicit def patienceConfig = PatienceConfig(
    timeout = scaled(4000.millis),
    interval = scaled(50.millis))

  behavior of "Repo[Basics].update"

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

}
