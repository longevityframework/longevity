package longevity.test

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import emblem.imports._
import emblem.TypeBoundPair
import emblem.traversors.sync.CustomGeneratorPool
import emblem.traversors.sync.CustomGenerator
import emblem.traversors.sync.Differ
import emblem.traversors.sync.Generator
import emblem.traversors.sync.TestDataGenerator
import longevity.subdomain._
import longevity.persistence._
import org.scalatest.OptionValues._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScaledTimeSpans
import org.scalatest.time.SpanSugar._

/** a simple fixture to test a [[longevity.persistence.RepoPool]]. all you have to do is extend this class and
 * provide the necessary inputs to the constructor.
 *
 * the repo pool spec exercises create/retrieve/update/delete for all the repos in your repo pool.
 *
 * @param subdomain the subdomain
 * @param customGeneratorPool a collection of custom generators to use when generating test data
 * @param repoPool the repo pool under test. this may be different than the `longevityContext.repoPool`, as
 * users may want to test against other repo pools. (for instance, they may want a spec for in-memory repo
 * pools if other parts of their test suite rely on them.)
 * @param suiteNameSuffix a short string to add to the suite name, to help differentiate between suites for
 * longevity contexts with the same name, when reading scalatest output
 */
private[longevity] class RepoPoolSpec(
  subdomain: Subdomain,
  customGeneratorPool: CustomGeneratorPool,
  repoPool: RepoPool,
  suiteNameSuffix: Option[String] = None)
extends FeatureSpec with GivenWhenThen with Matchers with ScalaFutures with ScaledTimeSpans {

  override implicit def patienceConfig = PatienceConfig(
    timeout = scaled(4000 millis),
    interval = scaled(50 millis))

  override val suiteName = s"RepoPoolSpec for ${subdomain.name}${suiteNameSuffix match {
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
    private val representativeNatKeyOption = repo.entityType.natKeys.headOption

    feature(s"${entityName}Repo.create") {
      scenario(s"should produce a persisted $entityName") {

        representativeNatKeyOption should be ('nonEmpty)

        Given(s"an unpersisted $entityName")
        val entity: E = testDataGenerator.generate[E]

        When(s"we create the $entityName") 
        Then(s"we get back the $entityName persistent state")
        val created: Persisted[E] = repo.create(entity).futureValue

        And(s"the persisted $entityName should should match the original, unpersisted $entityName")
        persistedShouldMatchUnpersisted(created.get, entity)

        // i cant figure out if this and clause is a sensible part of this test or not. opinions?
        And(s"further retrieval operations should retrieve the same $entityName")
        representativeNatKeyOption.foreach { natKey =>
          val natKeyVal = natKey.natKeyVal(entity)
          val retrieved: Persisted[E] = repo.retrieve(natKey)(natKeyVal).futureValue.value
          persistedShouldMatchUnpersisted(retrieved.get, entity)
        }

      }
    }

    feature(s"${entityName}Repo.retrieve") {
      scenario(s"should produce the same persisted $entityName") {

        Given(s"a persisted $entityName")
        val entity: E = testDataGenerator.generate[E]
        val created = repo.create(entity).futureValue

        When(s"we retrieve the $entityName by any of its natural keys")
        Then(s"we get back the same $entityName persistent state")
        repo.entityType.natKeys.foreach { natKey =>
          val natKeyVal = natKey.natKeyVal(created.get)
          val retrieved: Persisted[E] = repo.retrieve(natKey)(natKeyVal).futureValue.value
          persistedShouldMatchUnpersisted(retrieved.get, entity)
        }
      }
    }

    feature(s"${entityName}Repo.update") {
      scenario(s"should produce an updated persisted $entityName") {

        Given(s"a persisted $entityName")
        val originalEntity: E = testDataGenerator.generate[E]
        val modifiedEntity: E = testDataGenerator.generate[E]
        val created: Persisted[E] = repo.create(originalEntity).futureValue

        When(s"we update the persisted $entityName")
        val modified: Persisted[E] = created.map(e => modifiedEntity)
        val updated: Persisted[E] = repo.update(modified).futureValue

        Then(s"we get back the updated $entityName persistent state")
        persistedShouldMatchUnpersisted(updated.get, modifiedEntity)

        And(s"further retrieval operations should retrieve the updated copy")
        representativeNatKeyOption.foreach { natKey =>
          val natKeyVal = natKey.natKeyVal(modifiedEntity)
          val retrieved: Persisted[E] = repo.retrieve(natKey)(natKeyVal).futureValue.value
          persistedShouldMatchUnpersisted(retrieved.get, modifiedEntity)
        }

        And(s"further retrieval operations based on the original version should retrieve nothing")
        representativeNatKeyOption.foreach { natKey =>
          val natKeyVal = natKey.natKeyVal(originalEntity)
          repo.retrieve(natKey)(natKeyVal).futureValue should be (None)
        }

      }
    }

    feature(s"${entityName}Repo.delete") {
      scenario(s"should delete a persisted $entityName") {
        Given(s"a persisted $entityName")
        val entity: E = testDataGenerator.generate[E]
        val created: Persisted[E] = repo.create(entity).futureValue

        When(s"we delete the persisted $entityName")
        val deleted: Deleted[E] = repo.delete(created).futureValue

        Then(s"we get back a Deleted persistent state")
        persistedShouldMatchUnpersisted(deleted.get, entity)

        And(s"we should no longer be able to retrieve the $entityName")
        representativeNatKeyOption.foreach { natKey =>
          val natKeyVal = natKey.natKeyVal(entity)
          val retrieved: Option[Persisted[E]] = repo.retrieve(natKey)(natKeyVal).futureValue
          retrieved.isEmpty should be (true)
        }
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

  private val emblemPool = subdomain.entityEmblemPool
  private val extractorPool = shorthandPoolToExtractorPool(subdomain.shorthandPool)
  private val generators = customGeneratorPool + assocGenerator
  private val testDataGenerator = new TestDataGenerator(emblemPool, extractorPool, generators)
  private val unpersistor = new PersistedToUnpersistedTransformer(emblemPool, extractorPool)
  private val differ = new Differ(emblemPool, extractorPool)

  private def persistedShouldMatchUnpersisted[E <: Entity : TypeKey](persisted: E, unpersisted: E): Unit = {
    val unpersistorated = unpersistor.transform(Future(persisted))
    if (unpersistorated.futureValue != unpersisted) {
      val diffs = differ.diff(unpersistorated, unpersisted)
      fail (Differ.explainDiffs(diffs, true))
    }
  }
 
}
