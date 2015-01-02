package longevity.testUtil

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.repo._
import longevity.domain._

/** A simple fixture to test your [[Repo]]. all you have to do is extend this class and implement the five
 * abstract methods. */
trait RepoSpec[E <: Entity] extends FeatureSpec with GivenWhenThen with Matchers {

  /** the name of the entity type. to be used in test descriptions */
  def ename: String

  /** the repository under test */
  def repo: Repo[E]

  /** generates an entity suitable for use in testing */
  def genTestEntity: () => E

  /** make an update to an entity that is suitable for use in testing. this means that the change should be
   * detectable by [[InMemRepoSpec.persistedShouldMatchUnpersisted]]. it also means that the change should be
   * idempotent, in the sense that calling the function twice with the same input should produce the same
   * result. */
  def updateTestEntity: (E) => E

  /** a function that uses ScalaTest matchers to check that two versions of the entity match. the first
   * entity is the persisted, actual, version, and the second entity is the unpersisted, expected, version. */
  def persistedShouldMatchUnpersisted: (E, E) => Unit

  feature(s"${ename}Repo.create") {
    scenario(s"should produce a persisted $ename") {
      Given(s"an unpersisted $ename")
      val unpersisted = genTestEntity()
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
      val unpersisted = genTestEntity()
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
      val unpersistedOriginal = genTestEntity()
      val unpersistedModified = updateTestEntity(unpersistedOriginal)
      val created = repo.create(unpersistedOriginal).asPersisted
      When(s"we update the persisted $ename")
      val modified = created.copy(updateTestEntity)
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
      val unpersisted = genTestEntity()
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
