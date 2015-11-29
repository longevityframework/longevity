package longevity.test

import emblem.TypeBoundPair
import emblem.imports._
import emblem.traversors.sync.CustomGenerator
import emblem.traversors.sync.CustomGeneratorPool
import emblem.traversors.sync.Differ
import emblem.traversors.sync.Generator
import emblem.traversors.sync.TestDataGenerator
import longevity.context.LongevityContext
import longevity.persistence._
import longevity.subdomain._
import org.scalatest.OptionValues._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScaledTimeSpans
import org.scalatest.time.SpanSugar._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
  context: LongevityContext,
  repoPool: RepoPool,
  suiteNameSuffix: Option[String] = None)
extends {
  protected val longevityContext = context
}
with FeatureSpec
with GivenWhenThen
with Matchers
with ScalaFutures
with ScaledTimeSpans
with TestDataGeneration {

  override implicit def patienceConfig = PatienceConfig(
    timeout = scaled(4000 millis),
    interval = scaled(50 millis))

  override val suiteName = s"RepoPoolSpec for ${longevityContext.subdomain.name}${suiteNameSuffix match {
    case Some(suffix) => s" $suffix"
    case None => ""
  }}"

  repoPool.foreach { pair =>
    def repoSpec[R <: RootEntity](pair: TypeBoundPair[RootEntity, TypeKey, Repo, R]): Unit = {
      new RepoSpec(pair._2)(pair._1)
    }
    repoSpec(pair)
  }

  private class RepoSpec[R <: RootEntity : TypeKey](private val repo: Repo[R]) {

    private val rootName = repo.entityType.emblem.name
    private val representativeKeyOption = repo.entityType.keys.headOption

    object Create extends Tag("Create")
    object Retrieve extends Tag("Retrieve")
    object Update extends Tag("Update")
    object Delete extends Tag("Delete")

    feature(s"${rootName}Repo.create") {
      scenario(s"should produce a persisted $rootName", Create) {

        representativeKeyOption should be ('nonEmpty)

        Given(s"an unpersisted $rootName")
        val root: R = testDataGenerator.generate[R]

        When(s"we create the $rootName")
        Then(s"we get back the $rootName persistent state")
        val created: Persisted[R] = repo.create(root).futureValue

        And(s"the persisted $rootName should should match the original, unpersisted $rootName")
        persistedShouldMatchUnpersisted(created.get, root)

        // i cant figure out if this and clause is a sensible part of this test or not. opinions?
        And(s"further retrieval operations should retrieve the same $rootName")
        representativeKeyOption.foreach { key =>
          val keyVal = key.keyVal(created.get)
          val retrieved: Persisted[R] = repo.retrieve(key)(keyVal).futureValue.value
          persistedShouldMatchUnpersisted(retrieved.get, root)
        }

      }
    }

    feature(s"${rootName}Repo.retrieve") {
      scenario(s"should produce the same persisted $rootName", Retrieve) {

        Given(s"a persisted $rootName")
        val root: R = testDataGenerator.generate[R]
        val created = repo.create(root).futureValue

        When(s"we retrieve the $rootName by any of its natural keys")
        Then(s"we get back the same $rootName persistent state")
        repo.entityType.keys.foreach { key =>
          val keyVal = key.keyVal(created.get)
          val retrieved: Persisted[R] = repo.retrieve(key)(keyVal).futureValue.value
          persistedShouldMatchUnpersisted(retrieved.get, root)
        }
      }
    }

    feature(s"${rootName}Repo.update") {
      scenario(s"should produce an updated persisted $rootName", Update) {

        Given(s"a persisted $rootName")
        val originalRoot: R = testDataGenerator.generate[R]
        val modifiedRoot: R = testDataGenerator.generate[R]
        val created: Persisted[R] = repo.create(originalRoot).futureValue

        When(s"we update the persisted $rootName")
        val modified: Persisted[R] = created.map(e => modifiedRoot)
        val updated: Persisted[R] = repo.update(modified).futureValue

        Then(s"we get back the updated $rootName persistent state")
        persistedShouldMatchUnpersisted(updated.get, modifiedRoot)

        And(s"further retrieval operations should retrieve the updated copy")
        representativeKeyOption.foreach { key =>
          val keyVal = key.keyVal(updated.get)
          val retrieved: Persisted[R] = repo.retrieve(key)(keyVal).futureValue.value
          persistedShouldMatchUnpersisted(retrieved.get, modifiedRoot)
        }

        And(s"further retrieval operations based on the original version should retrieve nothing")
        representativeKeyOption.foreach { key =>
          val keyVal = key.keyVal(created.get)
          repo.retrieve(key)(keyVal).futureValue should be (None)
        }

      }
    }

    feature(s"${rootName}Repo.delete") {
      scenario(s"should delete a persisted $rootName", Delete) {
        Given(s"a persisted $rootName")
        val root: R = testDataGenerator.generate[R]
        val created: Persisted[R] = repo.create(root).futureValue

        When(s"we delete the persisted $rootName")
        val deleted: Deleted[R] = repo.delete(created).futureValue

        Then(s"we get back a Deleted persistent state")
        persistedShouldMatchUnpersisted(deleted.get, root)

        And(s"we should no longer be able to retrieve the $rootName")
        representativeKeyOption.foreach { key =>
          val keyVal = key.keyVal(created.get)
          val retrieved: Option[Persisted[R]] = repo.retrieve(key)(keyVal).futureValue
          retrieved.isEmpty should be (true)
        }
      }
    }

  }

  private val subdomain = longevityContext.subdomain
  private val emblemPool = subdomain.entityEmblemPool
  private val extractorPool = shorthandPoolToExtractorPool(subdomain.shorthandPool)
  private val unpersistor = new PersistedToUnpersistedTransformer(emblemPool, extractorPool)
  private val differ = new Differ(emblemPool, extractorPool)

  private def persistedShouldMatchUnpersisted[R <: RootEntity : TypeKey](persisted: R, unpersisted: R): Unit = {
    val unpersistorated = unpersistor.transform(Future(persisted))
    if (unpersistorated.futureValue != unpersisted) {
      val diffs = differ.diff(unpersistorated, unpersisted)
      fail (Differ.explainDiffs(diffs, true))
    }
  }
 
}
