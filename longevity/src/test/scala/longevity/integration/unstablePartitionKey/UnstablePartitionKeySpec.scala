package longevity.integration.unstablePartitionKey

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.exceptions.persistence.UnstablePartitionKeyException
import longevity.integration.model.partitionKey.Key
import longevity.integration.model.partitionKey.PartitionKey
import longevity.integration.model.partitionKey.domainModel
import longevity.persistence.Repo
import longevity.test.LongevityFuturesSpec
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import scala.concurrent.ExecutionContext.{ global => globalExecutionContext }
import scala.concurrent.Future

/** expect UnstablePartitionKeyException when partition key changes */
class UnstablePartitionKeySpec extends FlatSpec with LongevityFuturesSpec
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

  assertUnstablePartitionKeyBehavior(cassandraContext.testRepoPool[PartitionKey], "CassandraRepo")
  assertUnstablePartitionKeyBehavior(inmemContext.testRepoPool[PartitionKey], "InMemRepo")
  assertUnstablePartitionKeyBehavior(mongoContext.testRepoPool[PartitionKey], "MongoRepo")
  assertUnstablePartitionKeyBehavior(sqliteContext.testRepoPool[PartitionKey], "SQLiteRepo")

  def assertUnstablePartitionKeyBehavior(repo: Repo[PartitionKey], repoName: String): Unit = {

    behavior of s"$repoName.create"

    it should "throw exception on update with modified partition key" in {

      val origKey = Key("orig")
      val modifiedKey = Key("modified")
      val p1 = PartitionKey(origKey)
      val p2 = PartitionKey(modifiedKey)
      val s1 = repo.create(p1).futureValue

      def expectUnstable(future: Future[_]) = {
        val exception = future.failed.futureValue
        if (!exception.isInstanceOf[UnstablePartitionKeyException[_]]) {
          exception.printStackTrace
        }
        exception shouldBe a [UnstablePartitionKeyException[_]]

        val e = exception.asInstanceOf[UnstablePartitionKeyException[PartitionKey]]
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
