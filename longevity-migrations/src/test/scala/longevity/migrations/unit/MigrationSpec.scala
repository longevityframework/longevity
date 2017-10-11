package longevity.migrations.unit

import longevity.migrations.{ FinalPersistentMissing, InitialPersistentMissing, Migration }
import longevity.migrations.InitialDerivedPresent
import longevity.migrations.FinalDerivedPresent
import org.scalatest.FlatSpec
import org.scalatest.Matchers

/** unit tests for the proper behavior of [[Migration.builder]] and [[Migration.validate]] */
class MigrationSpec extends FlatSpec with Matchers {

  behavior of "Migration.validate for models without subtype polymorphism"

  it should "produce no errors when all persistents from both models are accounted for" in {
    val migration1 = Migration.builder[basic.DomainModel, basic.DomainModel](None, "v1")
      .update[basic.User, basic.User](identity)
      .build
    migration1.validate.isValid should be (true)

    val migration2 = Migration.builder[basic.DomainModel, basic.DomainModel](None, "v1")
      .drop[basic.User]
      .create[basic.User]
      .build
    migration2.validate.isValid should be (true)
  }

  it should "produce an error when a persistent in the initial model is not accounted for" in {
    val migration1 = Migration.builder[basic.DomainModel, basic.DomainModel](None, "v1")
      .create[basic.User]
      .build
    val result = migration1.validate
    result.isValid should be (false)
    result.errors.size should be (1)
    val error = result.errors(0)
    error shouldBe a [InitialPersistentMissing]
    error.asInstanceOf[InitialPersistentMissing].name should equal (basic.User.name)
  }

  it should "produce an error when a persistent in the final model is not accounted for" in {
    val migration1 = Migration.builder[basic.DomainModel, basic.DomainModel](None, "v1")
      .drop[basic.User]
      .build
    val result = migration1.validate
    result.isValid should be (false)
    result.errors.size should be (1)
    val error = result.errors(0)
    error shouldBe a [FinalPersistentMissing]
    error.asInstanceOf[FinalPersistentMissing].name should equal (basic.User.name)
  }

  behavior of "Migration.validate for models with subtype polymorphism"

  it should "produce no errors when all non-derived persistents from both models are accounted for" in {
    val migration1 = Migration.builder[poly.DomainModel, poly.DomainModel](None, "v1")
      .update[poly.User, poly.User](identity)
      .build
    migration1.validate.isValid should be (true)

    val migration2 = Migration.builder[poly.DomainModel, poly.DomainModel](None, "v1")
      .drop[poly.User]
      .create[poly.User]
      .build
    migration2.validate.isValid should be (true)
  }

  it should "produce errors when derived persistents occur in the migration steps" in {
    val migration1 = Migration.builder[poly.DomainModel, poly.DomainModel](None, "v1")
      .update[poly.User, poly.User](identity)
      .drop[poly.Member]
      .build
    val result1 = migration1.validate
    result1.isValid should be (false)
    result1.errors.size should be (1)
    val error1 = result1.errors(0)
    error1 shouldBe a [InitialDerivedPresent]
    error1.asInstanceOf[InitialDerivedPresent].name should equal (poly.Member.name)

    val migration2 = Migration.builder[poly.DomainModel, poly.DomainModel](None, "v1")
      .update[poly.User, poly.User](identity)
      .create[poly.Member]
      .build
    val result2 = migration2.validate
    result2.isValid should be (false)
    result2.errors.size should be (1)
    val error2 = result2.errors(0)
    error2 shouldBe a [FinalDerivedPresent]
    error2.asInstanceOf[FinalDerivedPresent].name should equal (poly.Member.name)
  }

}
