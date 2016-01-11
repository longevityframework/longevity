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

/** a simple fixture to test a [[longevity.persistence.RepoPool]]. all you have to do is extend this
 * class and provide the necessary inputs to the constructor.
 *
 * the repo CRUD spec exercises create/retrieve/update/delete for all the repos in your repo pool.
 *
 * @param context the longevity context
 * 
 * @param repoPool the repo pool under test. this may be different than the `longevityContext.repoPool`,
 * as users may want to test against other repo pools. (for instance, they may want a spec for in-memory
 * repo pools if other parts of their test suite rely on them.)
 * 
 * @param suiteNameSuffix a short string to add to the suite name, to help differentiate between suites
 * for longevity contexts with the same name, when reading scalatest output
 */
private[longevity] class RepoCrudSpec(
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

  override val suiteName = s"RepoCrudSpec for ${longevityContext.subdomain.name}${suiteNameSuffix match {
    case Some(suffix) => s" $suffix"
    case None => ""
  }}"

  repoPool.baseRepoMap.foreach { pair =>
    def repoSpec[R <: Root](pair: TypeBoundPair[Root, TypeKey, BaseRepo, R]): Unit = {
      new RepoSpec(pair._2)(pair._1)
    }
    repoSpec(pair)
  }

  private class RepoSpec[R <: Root : TypeKey](private val repo: BaseRepo[R]) {

    private val rootName = repo.rootType.emblem.name
    private val representativeKeyOption = repo.rootType.keys.headOption

    object Create extends Tag("Create")
    object Retrieve extends Tag("Retrieve")
    object Update extends Tag("Update")
    object Delete extends Tag("Delete")

    feature(s"${rootName}Repo.create") {
      scenario(s"should produce a persisted $rootName", Create) {

        representativeKeyOption should be ('nonEmpty)

        Given(s"an unpersisted $rootName")
        val root: R = randomRoot()

        When(s"we create the $rootName")
        Then(s"we get back the $rootName persistent state")
        val created: PState[R] = repo.create(root).futureValue

        And(s"the persisted $rootName should should match the original, unpersisted $rootName")
        created.get should equal (root)

        // i cant figure out if this and clause is a sensible part of this test or not. opinions?
        And(s"further retrieval operations should retrieve the same $rootName")
        representativeKeyOption.foreach { key =>
          val keyValForRoot = key.keyValForRoot(created.get)
          val retrieved: PState[R] = repo.retrieve(keyValForRoot).futureValue.value
          retrieved.get should equal (root)
        }

      }
    }

    feature(s"${rootName}Repo.retrieve") {
      scenario(s"should produce the same persisted $rootName", Retrieve) {

        Given(s"a persisted $rootName")
        val root: R = randomRoot()
        val created = repo.create(root).futureValue

        When(s"we retrieve the $rootName by any of its keys")
        Then(s"we get back the same $rootName persistent state")
        repo.rootType.keys.foreach { key =>
          val keyValForRoot = key.keyValForRoot(created.get)
          val retrieved: PState[R] = repo.retrieve(keyValForRoot).futureValue.value
          retrieved.get should equal (root)
        }
      }
    }

    feature(s"${rootName}Repo.update") {
      scenario(s"should produce an updated persisted $rootName", Update) {

        Given(s"a persisted $rootName")
        val originalRoot: R = randomRoot()
        val modifiedRoot: R = randomRoot()
        val created: PState[R] = repo.create(originalRoot).futureValue

        When(s"we update the persisted $rootName")
        val modified: PState[R] = created.map(e => modifiedRoot)
        val updated: PState[R] = repo.update(modified).futureValue

        Then(s"we get back the updated $rootName persistent state")
        updated.get should equal (modifiedRoot)

        And(s"further retrieval operations should retrieve the updated copy")
        representativeKeyOption.foreach { key =>
          val keyValForRoot = key.keyValForRoot(updated.get)
          val retrieved: PState[R] = repo.retrieve(keyValForRoot).futureValue.value
          retrieved.get should equal (modifiedRoot)
        }

        And(s"further retrieval operations based on the original version should retrieve nothing")
        representativeKeyOption.foreach { key =>
          val keyValForRoot = key.keyValForRoot(created.get)
          repo.retrieve(keyValForRoot).futureValue should be (None)
        }

      }
    }

    feature(s"${rootName}Repo.delete") {
      scenario(s"should delete a persisted $rootName", Delete) {
        Given(s"a persisted $rootName")
        val root: R = randomRoot()
        val created: PState[R] = repo.create(root).futureValue

        When(s"we delete the persisted $rootName")
        val deleted: Deleted[R] = repo.delete(created).futureValue

        Then(s"we get back a Deleted persistent state")
        deleted.root should equal (root)

        And(s"we should no longer be able to retrieve the $rootName")
        representativeKeyOption.foreach { key =>
          val keyValForRoot = key.keyValForRoot(created.get)
          val retrieved: Option[PState[R]] = repo.retrieve(keyValForRoot).futureValue
          retrieved.isEmpty should be (true)
        }
      }
    }

    private def randomRoot(): R = {
      val root: R = testDataGenerator.generate[R]
      repo.patchUnpersistedAssocs(root, CreatedCache()).futureValue._1
    }

  }
 
}
