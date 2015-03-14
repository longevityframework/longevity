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
import org.scalatest.OptionValues._

/** a simple fixture to test your [[longevity.repo.RepoPool]]. all you have to do is extend this class and
 * provide the necessary inputs to the constructor.
 *
 * the repo pool spec exercises create/retrieve/update/delete for all the repos in your repo pool.
 *
 * @param boundedContext the bounded context
 * @param repoPool the repo pool under test
 * @param customGenerators a collection of custom generators to use when generating test data. defaults to an
 * empty collection.
 * @param suiteNameSuffix a short string to add to the suite name, to help differentiate between suites for
 * bounded contexts with the same name, when reading scalatest output
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
    def repoSpec[E <: RootEntity](pair: TypeBoundPair[RootEntity, TypeKey, Repo, E]): Unit = {
      new RepoSpec(pair._2)(pair._1)
    }
    repoSpec(pair)
  }

  private class RepoSpec[E <: RootEntity : TypeKey](private val repo: Repo[E]) {

    private val entityName = repo.entityType.emblem.name

    feature(s"${entityName}Repo.create") {
      scenario(s"should produce a persisted $entityName") {

        Given(s"an unpersisted $entityName")
        val unpersisted: E = testDataGenerator.generate[E]

        When(s"we create the $entityName") 
        Then(s"we get back the $entityName persistent state")
        val created: Persisted[E] = repo.create(unpersisted)

        And(s"the persisted $entityName should should match the original, unpersisted $entityName")
        persistedShouldMatchUnpersisted(created.get, unpersisted)

        And(s"further retrieval operations should retrieve the same $entityName")
        val retrieved: Persisted[E] = repo.retrieve(created.id).value
        persistedShouldMatchUnpersisted(retrieved.get, unpersisted)
      }
    }

    feature(s"${entityName}Repo.retrieve") {
      scenario(s"should produce the same persisted $entityName") {

        Given(s"a persisted $entityName")
        val unpersisted: E = testDataGenerator.generate[E]
        val created = repo.create(unpersisted)

        When(s"we retrieve the $entityName by id")
        val retrieved: Persisted[E] = repo.retrieve(created.id).value

        Then(s"we get back the same $entityName persistent state")
        persistedShouldMatchUnpersisted(retrieved.get, unpersisted)
      }
    }

    feature(s"${entityName}Repo.update") {
      scenario(s"should produce an updated persisted $entityName") {

        Given(s"a persisted $entityName")
        val unpersistedOriginal: E = testDataGenerator.generate[E]
        val unpersistedModified: E = testDataGenerator.generate[E]
        val created: Persisted[E] = repo.create(unpersistedOriginal)

        When(s"we update the persisted $entityName")
        val modified: Persisted[E] = created.map(e => unpersistedModified)
        val updated: Persisted[E] = repo.update(modified)

        Then(s"we get back the updated $entityName persistent state")
        persistedShouldMatchUnpersisted(updated.get, unpersistedModified)

        And(s"further retrieval operations should retrieve the updated copy")
        val retrieved: Persisted[E] = repo.retrieve(updated.id).value
        persistedShouldMatchUnpersisted(retrieved.get, unpersistedModified)
      }
    }

    feature(s"${entityName}Repo.delete") {
      scenario(s"should deleted persisted $entityName") {

        Given(s"a persisted $entityName")
        val unpersisted: E = testDataGenerator.generate[E]
        val created: Persisted[E] = repo.create(unpersisted)
        created shouldBe a [Persisted[_]]

        When(s"we delete the persisted $entityName")
        val deleted: Deleted[E] = repo.delete(created)

        Then(s"we get back a Deleted persistent state")
        persistedShouldMatchUnpersisted(deleted.get, unpersisted)

        And(s"we should no longer be able to retrieve the $entityName")
        val retrieved: Option[Persisted[E]] = repo.retrieve(created.id)
        retrieved.isEmpty should be (true)
      }
    }

  }

  private val assocGenerator: CustomGenerator[Assoc[_ <: RootEntity]] =
    new CustomGenerator[Assoc[_ <: RootEntity]] {
      def apply[B <: Assoc[_ <: RootEntity] : TypeKey](generator: Generator): B = {
        val entityTypeKey: TypeKey[_ <: RootEntity] =
          typeKey[B].typeArgs.head.castToUpperBound[RootEntity].get
        def genAssoc[Associatee <: RootEntity : TypeKey] =
            Assoc[Associatee](generator.generate[Associatee])
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
