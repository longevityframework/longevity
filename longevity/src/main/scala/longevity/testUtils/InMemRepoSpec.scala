package longevity.testUtils

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.repo._
import longevity.domain._

trait InMemRepoSpec[E <: Entity] extends FeatureSpec with GivenWhenThen with Matchers {

  def entityTypeName: String
  def repo: Repo[E]
  def genTestEntity: () => E
  def updateTestEntity: (E) => E // needs to be idempotent
  def persistedShouldMatchUnpersisted: (E, E) => Unit

  feature(s"${entityTypeName}Repo.create") {
    scenario(s"should produce a persisted $entityTypeName") {
      Given(s"an unpersisted $entityTypeName")
      val unpersisted = genTestEntity()
      When(s"we persist the $entityTypeName")
      val persistentState = repo.create(unpersisted)
      Then(s"we get back the $entityTypeName persistent state")
      persistentState.isError should be (false)
      persistentState shouldBe a [Persisted[_]]
      persistedShouldMatchUnpersisted(persistentState.get, unpersisted)
      And(s"further retrieval operations should retrieve the $entityTypeName")
      val retrieved = repo.retrieve(persistentState.id)
      retrieved shouldBe a [Persisted[_]]
      persistedShouldMatchUnpersisted(retrieved.get, unpersisted)
    }
  }

  feature(s"${entityTypeName}Repo.retrieve") {
    scenario(s"should produce the same persisted $entityTypeName") {
      Given(s"a persisted $entityTypeName")
      val unpersisted = genTestEntity()
      val created = repo.create(unpersisted)
      When(s"we retrieve the $entityTypeName by id")
      val retrieved = repo.retrieve(created.id)
      Then(s"we get back the same $entityTypeName persistent state")
      retrieved.isError should be (false)
      retrieved shouldBe a [Persisted[_]]
      persistedShouldMatchUnpersisted(retrieved.get, unpersisted)
    }
  }

  // TODO clean up the confusing language somehow
  feature(s"${entityTypeName}Repo.update") {
    scenario(s"should produce an updated persisted $entityTypeName") {
      Given(s"a persisted $entityTypeName")
      val originalUnpersisted = genTestEntity()
      val updatedUnpersisted = updateTestEntity(originalUnpersisted)
      val originalPersisted = repo.create(originalUnpersisted).asPersisted
      When(s"we update the persisted $entityTypeName")
      val updatedPersisted = originalPersisted.copy(updateTestEntity)
      val updateResult = repo.update(updatedPersisted)
      Then(s"we get back the updated $entityTypeName persistent state")
      updateResult.isError should be (false)
      updateResult shouldBe a [Persisted[_]]
      persistedShouldMatchUnpersisted(updateResult.get, updatedUnpersisted)
      And(s"further retrieval operations should retrieve the updated copy")
      val retrieved = repo.retrieve(updateResult.id)
      retrieved shouldBe a [Persisted[_]]
      persistedShouldMatchUnpersisted(retrieved.get, updatedUnpersisted)
    }
  }

  feature(s"${entityTypeName}Repo.delete") {
    scenario(s"should deleted persisted $entityTypeName") {
      Given(s"a persisted $entityTypeName")
      val unpersisted = genTestEntity()
      val created = repo.create(unpersisted)
      created shouldBe a [Persisted[_]]
      When(s"we delete the persisted $entityTypeName")
      val deleted = repo.delete(created)
      Then(s"we get back a Deleted persistent state")
      deleted shouldBe a [Deleted[_]]
      persistedShouldMatchUnpersisted(deleted.get, unpersisted)
      And(s"we should no longer be able to retrieve the $entityTypeName")
      val retrieved = repo.retrieve(created.id)
      retrieved shouldBe a [NotFound[_]]
    }
  }
}
