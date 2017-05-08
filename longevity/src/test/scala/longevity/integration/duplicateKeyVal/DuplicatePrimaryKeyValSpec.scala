package longevity.integration.duplicateKeyVal

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.integration.model.primaryKey.Key
import longevity.integration.model.primaryKey.PrimaryKey
import longevity.integration.model.primaryKey.domainModel
import longevity.persistence.Repo
import longevity.test.LongevityFuturesSpec
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import scala.concurrent.ExecutionContext.{ global => globalExecutionContext }

/** expect all back ends to throw DuplicateKeyValException on a duplicate
 * primary key value. except cassandra of course. in cassandra, if you insert
 * a duplicate primary key, it will simply overwrite the old row.
 */
class DuplicatePrimaryKeyValSpec extends FlatSpec with LongevityFuturesSpec
with BeforeAndAfterAll
with GivenWhenThen {

  override protected implicit val executionContext = globalExecutionContext

  val inMemContext = new LongevityContext(domainModel, TestLongevityConfigs.inMemConfig)
  val mongoContext = new LongevityContext(domainModel, TestLongevityConfigs.mongoConfig)
  val sqliteContext = new LongevityContext(domainModel, TestLongevityConfigs.sqliteConfig)

  override def beforeAll() = {
    inMemContext.testRepo.createSchema().futureValue
    mongoContext.testRepo.createSchema().futureValue
    sqliteContext.testRepo.createSchema().futureValue
  }

  assertDuplicateKeyValBehavior(inMemContext.testRepo, "InMemRepo")
  assertDuplicateKeyValBehavior(mongoContext.testRepo, "MongoRepo")
  assertDuplicateKeyValBehavior(sqliteContext.testRepo, "SQLiteRepo")

  def assertDuplicateKeyValBehavior(repo: Repo, repoName: String): Unit = {

    behavior of s"$repoName.create"

    it should "throw exception on attempt to insert duplicate key val for a primary key" in {

      val origKey = Key("orig")
      val p1 = PrimaryKey(origKey)
      val s1 = repo.create(p1).futureValue

      try {
        val exception = repo.create(p1).failed.futureValue
        if (!exception.isInstanceOf[DuplicateKeyValException[_]]) {
          exception.printStackTrace
        }
        exception shouldBe a [DuplicateKeyValException[_]]

        val dkve = exception.asInstanceOf[DuplicateKeyValException[PrimaryKey]]
        dkve.p should equal (p1)
        (dkve.key: AnyRef) should equal (PrimaryKey.keySet.head)
      } finally {
        repo.delete(s1).futureValue
      }
    }

  }

}
