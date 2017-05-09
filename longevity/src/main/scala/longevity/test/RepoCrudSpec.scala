package longevity.test

import emblem.TypeKey
import longevity.context.LongevityContext
import longevity.config.BackEnd
import longevity.persistence.Deleted
import longevity.persistence.PState
import longevity.persistence.Repo
import longevity.model.KeyVal
import longevity.model.PolyPType
import longevity.model.PType
import longevity.model.realized.RealizedKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.Tag
import scala.concurrent.ExecutionContext

/** a [[http://www.scalatest.org/ ScalaTest]] fixture to test a [[longevity.persistence.Repo Repo]].
 * instances of this test are provided in your [[longevity.context.LongevityContext
 * LongevityContext]] via methods `repoCrudSpec` and `inMemRepoCrudSpec`. these methods are added by
 * an implicit conversion from `LongevityContext` to [[longevity.context.TestContext.ScalaTestSpecs
 * ScalaTestSpecs]].
 *
 * the repo CRUD spec exercises create/retrieve/update/delete for all the persistent types in your
 * repo.
 *
 * pardon the nasty ScalaDocs for this class. we haven't figured out how to remove the methods
 * inherited from ScalaTest classes yet.
 * 
 * @param context the longevity context
 * 
 * @param repo the repo under test. this may be different than the `longevityContext.repo`, as users
 * may want to test against other repo. (for instance, they may want a spec for in-memory repo if
 * other parts of their test suite rely on them.)
 * 
 * @param backEnd the back end we are running against. used to name tests so we can distinguish
 * different back ends in test output
 *
 * @param executionContext the execution context
 */
class RepoCrudSpec private[longevity] (
  protected val longevityContext: LongevityContext,
  protected val repo: Repo,
  private val backEnd: BackEnd)(
  protected implicit val executionContext: ExecutionContext)
extends FlatSpec with LongevityIntegrationSpec with GivenWhenThen {

  private val suiteNameSuffix = s"- $backEnd - optimisticLocking: ${longevityContext.config.optimisticLocking}"

  override val suiteName = s"RepoCrudSpec $suiteNameSuffix"

  override def beforeAll = repo.createSchema().recover({
    case t: Throwable =>
      logger.error("failed to create schema", t)
      throw t
  }).futureValue

  override def afterAll = repo.closeSession().futureValue

  longevityContext.modelType.pTypePool.values.foreach(new RepoSpec(_))

  private class RepoSpec[P](val pType: PType[P]) {
    private implicit val pTypeKey = pType.pTypeKey
    private val realizedPType = longevityContext.modelType.realizedPTypes(pType)
    private val pName = pTypeKey.name

    object Create extends Tag("Create")
    object Retrieve extends Tag("Retrieve")
    object Update extends Tag("Update")
    object Delete extends Tag("Delete")

    behavior of s"Repo.create[${pName}] $suiteNameSuffix"

    it should s"persist an unpersisted $pName" taggedAs(Create) in {
      val p = randomP()
      val created: PState[P] = repo.create(p).futureValue
      created.get should equal (p)

      realizedPType.keySet.foreach { key =>
        val retrieved: PState[P] = retrieveByKey(key, created.get).value
        retrieved.get should equal (p)
      }
    }

    behavior of s"Repo.retrieve[${pName}] $suiteNameSuffix"

    it should s"retrieve a persisted $pName" taggedAs(Retrieve) in {
      val p = randomP()
      val created = repo.create(p).futureValue

      realizedPType.keySet.foreach { key =>
        val retrieved: PState[P] = retrieveByKey(key, created.get).value
        retrieved.get should equal (p)
      }
    }

    behavior of s"Repo.update[${pName}] $suiteNameSuffix"

    it should s"persist updates to a persisted $pName" taggedAs(Update) in {
      val key = randomPTypeKey
      val originalP = randomP(key)
      val modifiedP = realizedPType.keySet.foldLeft(randomP(key)) { (modified, key) =>
        def updateByOriginalKeyVal[V <: KeyVal[P]](key: RealizedKey[P, V]) = {
          val originalKeyVal = key.keyValForP(originalP)
          key.updateKeyVal(modified, originalKeyVal)
        }
        updateByOriginalKeyVal(key)
      }

      val created: PState[P] = repo.create(originalP).futureValue

      val modified: PState[P] = created.map(e => modifiedP)
      val updated: PState[P] = repo.update(modified).futureValue

      updated.get should equal (modifiedP)

      realizedPType.keySet.foreach { key =>
        val retrieved: PState[P] = retrieveByKey(key, modifiedP).value
        retrieved.get should equal (modifiedP)
      }
    }

    behavior of s"Repo.delete[${pName}] $suiteNameSuffix"

    it should s"delete a persisted $pName" taggedAs(Delete) in {
      val p = randomP()
      val created: PState[P] = repo.create(p).futureValue

      val deleted: Deleted[P] = repo.delete(created).futureValue
      deleted.get should equal (p)

      realizedPType.keySet.foreach { key =>
        val retrieved: Option[PState[P]] = retrieveByKey(key, created.get)
        retrieved.isEmpty should be (true)
      }
    }

    private def randomPTypeKey(): TypeKey[_ <: P] = {
      pType match {
        case polyPType: PolyPType[P] =>
          val union = longevityContext.modelType.emblematic.unions(pTypeKey)
          val derivedTypeKeys = union.constituentKeys.toSeq
          val randomIndex = math.abs(longevityContext.testDataGenerator.generate[Int]) % derivedTypeKeys.size
          derivedTypeKeys(randomIndex)
        case _ =>
          pTypeKey
      }
    }

    private def randomP(key: TypeKey[_ <: P] = pTypeKey): P = longevityContext.testDataGenerator.generate(key)

    private def retrieveByKey[V <: KeyVal[P]](key: RealizedKey[P, V], p: P): Option[PState[P]] = {
      val keyValForP = key.keyValForP(p)
      implicit val keyValTypeKey = key.keyValTypeKey
      repo.retrieve[P, V](key.keyValForP(p)).futureValue
    }

  }
 
}
