package longevity.integration.unstablePartitionKey

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.exceptions.persistence.UnstablePartitionKeyException
import longevity.integration.subdomain.partitionKey.Key
import longevity.integration.subdomain.partitionKey.PartitionKey
import longevity.integration.subdomain.partitionKey.subdomain
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

  val mongoContext = new LongevityContext(subdomain, TestLongevityConfigs.mongoConfig)
  val cassandraContext = new LongevityContext(subdomain, TestLongevityConfigs.cassandraConfig)

  override def beforeAll() = {
    mongoContext.testRepoPool.createSchema().futureValue
    cassandraContext.testRepoPool.createSchema().futureValue
  }

  assertUnstablePartitionKeyBehavior(mongoContext.inMemTestRepoPool[PartitionKey], "InMemRepo")
  assertUnstablePartitionKeyBehavior(mongoContext.testRepoPool[PartitionKey], "MongoRepo")
  assertUnstablePartitionKeyBehavior(cassandraContext.testRepoPool[PartitionKey], "CassandraRepo")

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
