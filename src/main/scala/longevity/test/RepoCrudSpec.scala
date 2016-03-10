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
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** a fixture to test a [[longevity.persistence.RepoPool]]. all you have
 * to do is extend this class and provide the necessary inputs to the
 * constructor.
 *
 * the repo CRUD spec exercises create/retrieve/update/delete for all the repos
 * in your repo pool.
 *
 * @param context the longevity context
 * 
 * @param repoPool the repo pool under test. this may be different than the
 * `longevityContext.repoPool`, as users may want to test against other repo
 * pools. (for instance, they may want a spec for in-memory repo pools if other
 * parts of their test suite rely on them.)
 * 
 * @param suiteNameSuffix a short string to add to the suite name, to help
 * differentiate between suites for longevity contexts with the same name, when
 * reading scalatest output
 *
 * @param executionContext the execution context
 */
private[longevity] class RepoCrudSpec(
  context: LongevityContext,
  repoPool: RepoPool,
  suiteNameSuffix: Option[String] = None)(
  implicit executionContext: ExecutionContext)
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

  private def subdomainName = longevityContext.subdomain.name
  override val suiteName = s"RepoCrudSpec for ${subdomainName}${suiteNameSuffix match {
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
    private val representativeKeyOption = repo.rootType.keySet.headOption

    object Create extends Tag("Create")
    object RetrieveAssoc extends Tag("RetrieveAssoc")
    object RetrieveNatKey extends Tag("RetrieveNatKey")
    object Update extends Tag("Update")
    object Delete extends Tag("Delete")

    feature(s"${rootName}Repo.create") {
      scenario(s"should produce a persisted $rootName", Create) {

        Given(s"an unpersisted $rootName")
        val root: R = randomRoot()

        When(s"we create the $rootName")
        Then(s"we get back the $rootName persistent state")
        val created: PState[R] = repo.create(root).futureValue

        And(s"the persisted $rootName should should match the original, unpersisted $rootName")
        created.get should equal (root)

        And(s"further retrieval operations should retrieve the same $rootName")
        representativeKeyOption.foreach { key =>
          val retrieved: PState[R] = repo.retrieveOne(created.assoc).futureValue
          retrieved.get should equal (root)
        }

      }
    }

    feature(s"${rootName}Repo.retrieve(Assoc)") {
      scenario(s"should produce the same persisted $rootName", RetrieveAssoc) {

        Given(s"a persisted $rootName")
        val root: R = randomRoot()
        val created = repo.create(root).futureValue

        When(s"we retrieve the $rootName by its Assoc")
        Then(s"we get back the same $rootName persistent state")
        repo.rootType.keySet.foreach { key =>
          val retrieved: PState[R] = repo.retrieve(created.assoc).futureValue.value
          retrieved.get should equal (root)
        }
      }
    }

    feature(s"${rootName}Repo.retrieve(NatKey)") {
      scenario(s"should produce the same persisted $rootName", RetrieveNatKey) {

        Given(s"a persisted $rootName")
        val root: R = randomRoot()
        val created = repo.create(root).futureValue

        When(s"we retrieve the $rootName by any of its keys")
        Then(s"we get back the same $rootName persistent state")
        repo.rootType.keySet.foreach { key =>
          val keyValForP = key.keyValForP(created.get)
          val retrieved: PState[R] = repo.retrieve(keyValForP).futureValue.value
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
        val retrieved: PState[R] = repo.retrieveOne(updated.assoc).futureValue
        retrieved.get should equal (modifiedRoot)
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
        val retrieved: Option[PState[R]] = repo.retrieve(deleted.assoc).futureValue
        retrieved.isEmpty should be (true)
      }
    }

    private def randomRoot(): R = {
      val root: R = testDataGenerator.generate[R]
      repo.patchUnpersistedAssocs(root, CreatedCache()).futureValue._1
    }

  }
 
}
