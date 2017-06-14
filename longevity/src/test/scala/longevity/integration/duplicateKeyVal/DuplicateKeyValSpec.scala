package longevity.integration.duplicateKeyVal

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.integration.model.basics.Basics
import longevity.integration.model.basics.BasicsId
import longevity.integration.model.basics.DomainModel
import longevity.persistence.Repo
import longevity.test.LongevityFuturesSpec
import org.joda.time.DateTime
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import scala.concurrent.ExecutionContext.{ global => globalExecutionContext }

/** expect InMem, MongoDB, and SQLite back ends to throw
 * DuplicateKeyValException in non-partitioned database setup such as
 * our test database.
 *
 * we do not necessarily expect the same behavior out of other
 * back ends. we provide no guarantee that duplicate key vals will be
 * caught, as our underlying back ends do not necessarily provide any
 * such guarantee. but Mongo does guarantee to catch this in a single
 * partition database.
 */
class DuplicateKeyValSpec extends FlatSpec with LongevityFuturesSpec
with BeforeAndAfterAll
with GivenWhenThen {

  override protected implicit val executionContext = globalExecutionContext

  val inMemContext = new LongevityContext[DomainModel](TestLongevityConfigs.inMemConfig)
  val mongoContext = new LongevityContext[DomainModel](TestLongevityConfigs.mongoConfig)
  val sqliteContext = new LongevityContext[DomainModel](TestLongevityConfigs.sqliteConfig)

  override def beforeAll() = {
    inMemContext.testRepo.createSchema().futureValue
    mongoContext.testRepo.createSchema().futureValue
    sqliteContext.testRepo.createSchema().futureValue
  }

  assertDuplicateKeyValBehavior(inMemContext.testRepo, "InMemPRepo")
  assertDuplicateKeyValBehavior(mongoContext.testRepo, "MongoPRepo")
  assertDuplicateKeyValBehavior(sqliteContext.testRepo, "SQLitePRepo")

  def assertDuplicateKeyValBehavior(repo: Repo[DomainModel], repoName: String): Unit = {

    behavior of s"$repoName with a single partitioned database"

    it should "throw exception on attempt to insert duplicate key val" in {

      val id = BasicsId("id must be unique")
      val p1 = Basics(id, true, 'c', 5.7d, 4.5f, 3, 77l, "stringy", DateTime.now)
      val p2 = Basics(id, false, 'd', 6.7d, 5.5f, 4, 78l, "stingy", DateTime.now)
      val s1 = repo.create(p1).futureValue

      try {
        val exception = repo.create(p2).failed.futureValue
        if (!exception.isInstanceOf[DuplicateKeyValException[_, _]]) {
          exception.printStackTrace
        }
        exception shouldBe a [DuplicateKeyValException[_, _]]

        val dkve = exception.asInstanceOf[DuplicateKeyValException[DomainModel, Basics]]
        dkve.p should equal (p2)
        (dkve.key: AnyRef) should equal (Basics.keySet.head)
      } finally {
        repo.delete(s1).futureValue
      }
    }

    it should "throw exception on attempt to update to a duplicate key val" in {

      val id = BasicsId("id must be unique 2")
      val p1 = Basics(id, true, 'c', 5.7d, 4.5f, 3, 77l, "stringy", DateTime.now)
      val newId = BasicsId("this one is unique 2")
      val p2 = Basics(newId, false, 'd', 6.7d, 5.5f, 4, 78l, "stingy", DateTime.now)
      val s1 = repo.create(p1).futureValue
      val s2 = repo.create(p2).futureValue

      try {
        val s2_update = s2.map(_.copy(id = id))
        val exception = repo.update(s2_update).failed.futureValue

        if (!exception.isInstanceOf[DuplicateKeyValException[_, _]]) {
          exception.printStackTrace
        }
        exception shouldBe a [DuplicateKeyValException[_, _]]
        val dkve = exception.asInstanceOf[DuplicateKeyValException[DomainModel, Basics]]
        dkve.p should equal (s2_update.get)
        (dkve.key: AnyRef) should equal (Basics.keySet.head)
      } finally {
        repo.delete(s1).futureValue
        repo.delete(s2).futureValue
      }
    }

  }

}
