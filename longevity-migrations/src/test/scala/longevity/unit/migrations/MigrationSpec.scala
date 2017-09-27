package longevity.unit.migrations

import longevity.migrations.{ FinalPersistentMissing, InitialPersistentMissing, Migration }
import org.scalatest.FlatSpec
import org.scalatest.Matchers

/** unit tests for the proper behavior of [[Migration.builder]] and [[Migration.validate]] */
class MigrationSpec extends FlatSpec with Matchers {

  behavior of "Migration.validate"

  it should "produce no errors when all persistents from both models are accounted for" in {
    val migration1 = Migration.builder[model1.DomainModel, model1.DomainModel]()
      .update[model1.User, model1.User](identity)
      .build
    migration1.validate.isValid should be (true)

    val migration2 = Migration.builder[model1.DomainModel, model1.DomainModel]()
      .drop[model1.User]
      .create[model1.User]
      .build
    migration2.validate.isValid should be (true)
  }

  it should "produce an error when a persistent in the initial model is not accounted for" in {
    val migration1 = Migration.builder[model1.DomainModel, model1.DomainModel]()
      .create[model1.User]
      .build
    val result = migration1.validate
    result.isValid should be (false)
    result.errors.size should be (1)
    val error = result.errors(0)
    error shouldBe a [InitialPersistentMissing]
    error.asInstanceOf[InitialPersistentMissing].name should equal (model1.User.name)
  }

  it should "produce an error when a persistent in the final model is not accounted for" in {
    val migration1 = Migration.builder[model1.DomainModel, model1.DomainModel]()
      .drop[model1.User]
      .build
    val result = migration1.validate
    result.isValid should be (false)
    result.errors.size should be (1)
    val error = result.errors(0)
    error shouldBe a [FinalPersistentMissing]
    error.asInstanceOf[FinalPersistentMissing].name should equal (model1.User.name)
  }

}
