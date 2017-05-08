package longevity.integration.unstablePrimaryKey

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.exceptions.persistence.UnstablePrimaryKeyException
import longevity.integration.model.primaryKey.Key
import longevity.integration.model.primaryKey.PrimaryKey
import longevity.integration.model.primaryKey.domainModel
import longevity.persistence.RepoPool
import longevity.test.LongevityFuturesSpec
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import scala.concurrent.ExecutionContext.{ global => globalExecutionContext }
import scala.concurrent.Future

/** expect UnstablePrimaryKeyException when primary key changes */
class UnstablePrimaryKeySpec extends FlatSpec with LongevityFuturesSpec
with BeforeAndAfterAll
with GivenWhenThen {

  override protected implicit val executionContext = globalExecutionContext

  val cassandraContext = new LongevityContext(domainModel, TestLongevityConfigs.cassandraConfig)
  val inmemContext = new LongevityContext(domainModel, TestLongevityConfigs.inMemConfig)
  val mongoContext = new LongevityContext(domainModel, TestLongevityConfigs.mongoConfig)
  val sqliteContext = new LongevityContext(domainModel, TestLongevityConfigs.sqliteConfig)

  override def beforeAll() = {
    cassandraContext.testRepoPool.createSchema().futureValue
    inmemContext.testRepoPool.createSchema().futureValue
    mongoContext.testRepoPool.createSchema().futureValue
    sqliteContext.testRepoPool.createSchema().futureValue
  }

  assertUnstablePrimaryKeyBehavior(cassandraContext.testRepoPool, "CassandraRepo")
  assertUnstablePrimaryKeyBehavior(inmemContext.testRepoPool, "InMemRepo")
  assertUnstablePrimaryKeyBehavior(mongoContext.testRepoPool, "MongoRepo")
  assertUnstablePrimaryKeyBehavior(sqliteContext.testRepoPool, "SQLiteRepo")

  def assertUnstablePrimaryKeyBehavior(repo: RepoPool, repoName: String): Unit = {

    behavior of s"$repoName.create"

    it should "throw exception on update with modified primary key" in {

      val origKey = Key("orig")
      val modifiedKey = Key("modified")
      val p1 = PrimaryKey(origKey)
      val p2 = PrimaryKey(modifiedKey)
      val s1 = repo.create(p1).futureValue

      def expectUnstable(future: Future[_]) = {
        val exception = future.failed.futureValue
        if (!exception.isInstanceOf[UnstablePrimaryKeyException[_]]) {
          exception.printStackTrace
        }
        exception shouldBe a [UnstablePrimaryKeyException[_]]

        val e = exception.asInstanceOf[UnstablePrimaryKeyException[PrimaryKey]]
        e.orig should equal (p1)
        e.origKeyVal should equal (origKey)
        e.newKeyVal should equal (modifiedKey)
      }

      try {
        val s2 = s1.set(p2)
        expectUnstable(repo.update(s2))
        expectUnstable(repo.delete(s2))
      } finally {
        repo.delete(s1).futureValue
      }
    }

  }

}
