package longevity.testUtil

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.repo._
import longevity.domain._
import emblem._
import emblem.generators.TestDataGenerator
import emblem.generators.TestDataGenerator.emptyCustomGenerators
import emblem.generators.TestDataGenerator.GeneratorFunction

/** A simple fixture to test your [[Repo]]. all you have to do is extend this class and implement the five
 * abstract methods. */
abstract class RepoSpec[E <: Entity : TypeKey] extends FeatureSpec with GivenWhenThen with Matchers {

  /** the name of the entity type. to be used in test descriptions */
  protected def ename: String

  /** the repository under test */
  protected def repo: Repo[E]

  /** the application domain specification. to help us generate test data. */
  protected def domainConfig: DomainConfig

  /** a function that uses ScalaTest matchers to check that two versions of the entity match. the first
   * entity is the persisted, actual, version, and the second entity is the unpersisted, expected, version. */
  protected def persistedShouldMatchUnpersisted: (E, E) => Unit

  // TODO scaladoc
  // TODO override with gens for uri/email etc
  protected def customGenerators = emptyCustomGenerators

  // TODO
  // TODO another case to fix in TypeKeyMap
  private val assocGenerator: GeneratorFunction[Assoc[_ <: Entity]] = new GeneratorFunction[Assoc[_ <: Entity]] {
    def apply[B <: Assoc[_ <: Entity] : TypeKey](generator: TestDataGenerator): B = {
      val entityTypeKey = typeKey[B].typeArgs.head.asInstanceOf[TypeKey[_ <: Entity]]
      applyTightly(generator)(entityTypeKey, typeKey[B])
    }
    private def applyTightly[
      E <: Entity : TypeKey,
      B <: Assoc[_ <: Entity] : TypeKey](
      generator: TestDataGenerator): B = {
      (Assoc[E](generator.any[E])).asInstanceOf[B]
    }
  }

  private val testDataGenerator = new TestDataGenerator(
    domainConfig.shorthandPool,
    domainConfig.entityEmblemPool,
    customGenerators + assocGenerator)

  feature(s"${ename}Repo.create") {
    scenario(s"should produce a persisted $ename") {
      Given(s"an unpersisted $ename")
      val unpersisted = testDataGenerator.emblem[E]
      When(s"we create the $ename")
      val created = repo.create(unpersisted)
      Then(s"we get back the $ename persistent state")
      And(s"the persistent state should be `Persisted`")
      created.isError should be (false)
      created shouldBe a [Persisted[_]]
      And(s"the persisted $ename should should match the original, unpersisted $ename")
      persistedShouldMatchUnpersisted(created.get, unpersisted)
      And(s"further retrieval operations should retrieve the same $ename")
      val retrieved = repo.retrieve(created.id)
      retrieved shouldBe a [Persisted[_]]
      persistedShouldMatchUnpersisted(retrieved.get, unpersisted)
    }
  }

  feature(s"${ename}Repo.retrieve") {
    scenario(s"should produce the same persisted $ename") {
      Given(s"a persisted $ename")
      val unpersisted = testDataGenerator.emblem[E]
      val created = repo.create(unpersisted)
      When(s"we retrieve the $ename by id")
      val retrieved = repo.retrieve(created.id)
      Then(s"we get back the same $ename persistent state")
      retrieved.isError should be (false)
      retrieved shouldBe a [Persisted[_]]
      persistedShouldMatchUnpersisted(retrieved.get, unpersisted)
    }
  }

  feature(s"${ename}Repo.update") {
    scenario(s"should produce an updated persisted $ename") {
      Given(s"a persisted $ename")
      val unpersistedOriginal = testDataGenerator.emblem[E]
      val unpersistedModified = testDataGenerator.emblem[E]
      val created = repo.create(unpersistedOriginal).asPersisted
      When(s"we update the persisted $ename")
      val modified = created.copy(e => unpersistedModified)
      val updated = repo.update(modified)
      Then(s"we get back the updated $ename persistent state")
      updated.isError should be (false)
      updated shouldBe a [Persisted[_]]
      persistedShouldMatchUnpersisted(updated.get, unpersistedModified)
      And(s"further retrieval operations should retrieve the updated copy")
      val retrieved = repo.retrieve(updated.id)
      retrieved shouldBe a [Persisted[_]]
      persistedShouldMatchUnpersisted(retrieved.get, unpersistedModified)
    }
  }

  feature(s"${ename}Repo.delete") {
    scenario(s"should deleted persisted $ename") {
      Given(s"a persisted $ename")
      val unpersisted = testDataGenerator.emblem[E]
      val created = repo.create(unpersisted)
      created shouldBe a [Persisted[_]]
      When(s"we delete the persisted $ename")
      val deleted = repo.delete(created)
      Then(s"we get back a Deleted persistent state")
      deleted shouldBe a [Deleted[_]]
      persistedShouldMatchUnpersisted(deleted.get, unpersisted)
      And(s"we should no longer be able to retrieve the $ename")
      val retrieved = repo.retrieve(created.id)
      retrieved shouldBe a [NotFound[_]]
    }
  }
 
}
