package longevity.testUtil

import emblem._
import emblem.traversors.CustomGenerator
import emblem.traversors.Differ
import emblem.traversors.Generator
import emblem.traversors.Generator.emptyCustomGenerators
import emblem.traversors.Generator.CustomGenerators
import emblem.traversors.TestDataGenerator
import longevity.domain._
import longevity.repo._
import org.scalatest._

// TODO scaladoc
/** A simple fixture to test your [[longevity.repo.Repo]]. all you have to do is extend this class and implement
 * the four abstract methods.
 *
 * @param boundedContext TODO
 * @param repoPool TODO
 * @param customGenerators a collection of custom generators to use when generating test data. defaults to an
 * empty collection.
 */
class RepoPoolSpec(
  private val boundedContext: BoundedContext,
  private val repoPool: RepoPool,
  private val customGenerators: CustomGenerators = emptyCustomGenerators,
  private val suiteNameSuffix: Option[String] = None)
extends FeatureSpec with GivenWhenThen with Matchers {

  override val suiteName = s"RepoPoolSpec for ${boundedContext.name}${suiteNameSuffix match {
    case Some(suffix) => s" $suffix"
    case None => ""
  }}"

  repoPool.foreach { pair =>
    def repoSpec[E <: Entity](pair: TypeBoundPair[Entity, TypeKey, Repo, E]): Unit = {
      new RepoSpec(pair._2)(pair._1)
    }
    repoSpec(pair)
  }

  private class RepoSpec[E <: Entity : TypeKey](private val repo: Repo[E]) {

    private val entityName = repo.entityType.emblem.name

    feature(s"${entityName}Repo.create") {
      scenario(s"should produce a persisted $entityName") {

        Given(s"an unpersisted $entityName")
        val unpersisted = testDataGenerator.generate[E]

        When(s"we create the $entityName")
        val created = repo.create(unpersisted)

        Then(s"we get back the $entityName persistent state")
        And(s"the persistent state should be `Persisted`")
        created.isError should be (false)
        created shouldBe a [Persisted[_]]

        And(s"the persisted $entityName should should match the original, unpersisted $entityName")
        persistedShouldMatchUnpersisted(created.get, unpersisted)

        And(s"further retrieval operations should retrieve the same $entityName")
        val retrieved = repo.retrieve(created.id)
        retrieved shouldBe a [Persisted[_]]
        persistedShouldMatchUnpersisted(retrieved.get, unpersisted)
      }
    }

    feature(s"${entityName}Repo.retrieve") {
      scenario(s"should produce the same persisted $entityName") {

        Given(s"a persisted $entityName")
        val unpersisted = testDataGenerator.generate[E]
        val created = repo.create(unpersisted)

        When(s"we retrieve the $entityName by id")
        val retrieved = repo.retrieve(created.id)

        Then(s"we get back the same $entityName persistent state")
        retrieved.isError should be (false)
        retrieved shouldBe a [Persisted[_]]
        persistedShouldMatchUnpersisted(retrieved.get, unpersisted)
      }
    }

    feature(s"${entityName}Repo.update") {
      scenario(s"should produce an updated persisted $entityName") {

        Given(s"a persisted $entityName")
        val unpersistedOriginal = testDataGenerator.generate[E]
        val unpersistedModified = testDataGenerator.generate[E]
        val created = repo.create(unpersistedOriginal).asPersisted

        When(s"we update the persisted $entityName")
        val modified = created.copy(e => unpersistedModified)
        val updated = repo.update(modified)

        Then(s"we get back the updated $entityName persistent state")
        updated.isError should be (false)
        updated shouldBe a [Persisted[_]]
        persistedShouldMatchUnpersisted(updated.get, unpersistedModified)

        And(s"further retrieval operations should retrieve the updated copy")
        val retrieved = repo.retrieve(updated.id)
        retrieved shouldBe a [Persisted[_]]
        persistedShouldMatchUnpersisted(retrieved.get, unpersistedModified)
      }
    }

    feature(s"${entityName}Repo.delete") {
      scenario(s"should deleted persisted $entityName") {

        Given(s"a persisted $entityName")
        val unpersisted = testDataGenerator.generate[E]
        val created = repo.create(unpersisted)
        created shouldBe a [Persisted[_]]

        When(s"we delete the persisted $entityName")
        val deleted = repo.delete(created)

        Then(s"we get back a Deleted persistent state")
        deleted shouldBe a [Deleted[_]]
        persistedShouldMatchUnpersisted(deleted.get, unpersisted)

        And(s"we should no longer be able to retrieve the $entityName")
        val retrieved = repo.retrieve(created.id)
        retrieved shouldBe a [NotFound[_]]
      }
    }

  }

  private val assocGenerator: CustomGenerator[Assoc[_ <: Entity]] = new CustomGenerator[Assoc[_ <: Entity]] {
    def apply[B <: Assoc[_ <: Entity] : TypeKey](generator: Generator): B = {
      val entityTypeKey: TypeKey[_ <: Entity] = typeKey[B].typeArgs.head.castToUpperBound[Entity].get
      def genAssoc[Associatee <: Entity : TypeKey] = Assoc[Associatee](generator.generate[Associatee])
      genAssoc(entityTypeKey).asInstanceOf[B]
    }
  }

  private val testDataGenerator = new TestDataGenerator(
    boundedContext.entityEmblemPool,
    boundedContext.shorthandPool,
    customGenerators + assocGenerator)

  private val unpersistor = new PersistedToUnpersistedTransformer(boundedContext)
  private lazy val differ = new Differ(boundedContext.entityEmblemPool, boundedContext.shorthandPool)

  private def persistedShouldMatchUnpersisted[E <: Entity : TypeKey](persisted: E, unpersisted: E): Unit = {
    val unpersistorated = unpersistor.transform(persisted)
    if (unpersistorated != unpersisted) {
      val diffs = differ.diff(unpersistorated, unpersisted)
      fail (Differ.explainDiffs(diffs, true))
    }
  }
 
}
