package longevity.test

import emblem.typeBound.TypeBoundPair
import emblem.TypeKey
import longevity.context.LongevityContext
import longevity.persistence.BaseRepo
import longevity.persistence.CreatedCache
import longevity.persistence.Deleted
import longevity.persistence.PState
import longevity.persistence.RepoPool
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.PolyPType
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.Tag
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScaledTimeSpans
import org.scalatest.time.SpanSugar._
import scala.concurrent.ExecutionContext

/** a fixture to test a [[longevity.persistence.RepoPool]]. all you have
 * to do is extend this class and provide the necessary inputs to the
 * constructor.
 *
 * the repo CRUD spec exercises create/retrieve/update/delete for all the repos
 * in your repo pool.
 *
 * pardon the nasty ScalaDocs for this class. we haven't figured out how to
 * remove the methods inherited from ScalaTest classes yet.
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
class RepoCrudSpec private[longevity] (
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
    def repoSpec[P <: Persistent](pair: TypeBoundPair[Persistent, TypeKey, BaseRepo, P]): Unit = {
      new RepoSpec(pair._2, pair._1)
    }
    repoSpec(pair)
  }

  private class RepoSpec[P <: Persistent](
    private val repo: BaseRepo[P],
    private val pTypeKey: TypeKey[P]) {

    private val pName = pTypeKey.name
    private val representativeKeyOption = repo.pType.keySet.headOption

    object Create extends Tag("Create")
    object RetrieveAssoc extends Tag("RetrieveAssoc")
    object RetrieveNatKey extends Tag("RetrieveNatKey")
    object Update extends Tag("Update")
    object Delete extends Tag("Delete")

    feature(s"${pName}Repo.create") {
      scenario(s"should produce a persisted $pName", Create) {

        Given(s"an unpersisted $pName")
        val p = randomP()

        When(s"we create the $pName")
        Then(s"we get back the $pName persistent state")
        val created: PState[P] = repo.create(p).futureValue

        And(s"the persisted $pName should should match the original, unpersisted $pName")
        created.get should equal (p)

        And(s"further retrieval operations should retrieve the same $pName")
        val retrieved: PState[P] = repo.retrieveOne(created.assoc).futureValue
        retrieved.get should equal (p)

      }
    }

    feature(s"${pName}Repo.retrieve(Assoc)") {
      scenario(s"should produce the same persisted $pName", RetrieveAssoc) {

        Given(s"a persisted $pName")
        val p = randomP()
        val created = repo.create(p).futureValue

        When(s"we retrieve the $pName by its Assoc")
        Then(s"we get back the same $pName persistent state")
        val retrieved: PState[P] = repo.retrieve(created.assoc).futureValue.value
        retrieved.get should equal (p)
      }
    }

    feature(s"${pName}Repo.retrieve(NatKey)") {
      scenario(s"should produce the same persisted $pName", RetrieveNatKey) {

        Given(s"a persisted $pName")
        val p = randomP()
        val created = repo.create(p).futureValue

        When(s"we retrieve the $pName by any of its keys")
        Then(s"we get back the same $pName persistent state")
        repo.pType.keySet.foreach { key =>
          val keyValForP = key.keyValForP(created.get)
          val retrieved: PState[P] = repo.retrieve(keyValForP).futureValue.value
          retrieved.get should equal (p)
        }
      }
    }

    feature(s"${pName}Repo.update") {
      scenario(s"should produce an updated persisted $pName", Update) {

        Given(s"a persisted $pName")
        val key = randomPTypeKey
        val originalP = randomP(key)
        val modifiedP = randomP(key)
        val created: PState[P] = repo.create(originalP).futureValue

        When(s"we update the persisted $pName")
        val modified: PState[P] = created.map(e => modifiedP)
        val updated: PState[P] = repo.update(modified).futureValue

        Then(s"we get back the updated $pName persistent state")
        updated.get should equal (modifiedP)

        And(s"further retrieval operations should retrieve the updated copy")
        val retrieved: PState[P] = repo.retrieveOne(updated.assoc).futureValue
        retrieved.get should equal (modifiedP)
      }
    }

    feature(s"${pName}Repo.delete") {
      scenario(s"should delete a persisted $pName", Delete) {
        Given(s"a persisted $pName")
        val p = randomP()
        val created: PState[P] = repo.create(p).futureValue

        When(s"we delete the persisted $pName")
        val deleted: Deleted[P] = repo.delete(created).futureValue

        Then(s"we get back a Deleted persistent state")
        deleted.p should equal (p)

        And(s"we should no longer be able to retrieve the $pName")
        val retrieved: Option[PState[P]] = repo.retrieve(deleted.assoc).futureValue
        retrieved.isEmpty should be (true)
      }
    }

    private def randomPTypeKey(): TypeKey[_ <: P] = {
      repo.pType match {
        case polyPType: PolyPType[P] =>
          val union = longevityContext.subdomain.emblematic.unions(pTypeKey)
          val derivedTypeKeys = union.constituentKeys.toSeq
          val randomIndex = math.abs(testDataGenerator.generate[Int]) % derivedTypeKeys.size
          derivedTypeKeys(randomIndex)
        case _ =>
          pTypeKey
      }
    }

    private def randomP(key: TypeKey[_ <: P] = pTypeKey): P = {
      val p = testDataGenerator.generate(key)
      repo.patchUnpersistedAssocs(p, CreatedCache()).futureValue._1
    }

  }
 
}
