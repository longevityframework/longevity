package longevity.unit.persistence

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import longevity.persistence.inmem.InMemRepo
import longevity.persistence.cassandra.CassandraRepo
import longevity.persistence.mongo.MongoRepo

/** unit tests for the proper construction of [[LongevityContext.repoPool]] and
 * [[LongevityContext.inMemTestRepoPool]]
 */
class LongevityContextRepoPoolSpec extends FlatSpec with GivenWhenThen with Matchers {

  import longevity.unit.persistence.messageFriend._

  behavior of "LongevityContext.repoPool for an in-memory longevity context"

  it should "be full of InMemRepos" in {
    val repoPool = inMemLongevityContext.repoPool
    repoPool.values.size should equal (2)
    repoPool[Friend] shouldBe an [InMemRepo[_]]
    repoPool.baseRepoMap[Friend].pType should equal (Friend)
    repoPool[Message] shouldBe an [InMemRepo[_]]
    repoPool.baseRepoMap[Message].pType should equal (Message)
  }

  behavior of "LongevityContext.repoPool for a mongo longevity context"

  it should "be full of MongoRepos" in {
    val repoPool = mongoLongevityContext.repoPool
    repoPool.values.size should equal (2)
    repoPool[Friend] shouldBe a [MongoRepo[_]]
    repoPool.baseRepoMap[Friend].pType should equal (Friend)
    repoPool[Message] shouldBe a [MongoRepo[_]]
    repoPool.baseRepoMap[Message].pType should equal (Message)
  }

  behavior of "LongevityContext.repoPool for a cassandra longevity context"

  it should "be full of CassandraRepos" in {
    val repoPool = cassandraLongevityContext.repoPool
    repoPool.values.size should equal (2)
    repoPool[Friend] shouldBe a [CassandraRepo[_]]
    repoPool.baseRepoMap[Friend].pType should equal (Friend)
    repoPool[Message] shouldBe a [CassandraRepo[_]]
    repoPool.baseRepoMap[Message].pType should equal (Message)
  }

  behavior of "LongevityContext.testRepoPool of an in-memory longevity context"

  it should "be full of InMemRepos" in {
    val testRepoPool = inMemLongevityContext.testRepoPool
    testRepoPool.values.size should equal (2)
    testRepoPool[Friend] shouldBe an [InMemRepo[_]]
    testRepoPool.baseRepoMap[Friend].pType should equal (Friend)
    testRepoPool[Message] shouldBe an [InMemRepo[_]]
    testRepoPool.baseRepoMap[Message].pType should equal (Message)
  }

  behavior of "LongevityContext.testRepoPool of a mongo longevity context"

  it should "be full of MongoRepos" in {
    val testRepoPool = mongoLongevityContext.testRepoPool
    testRepoPool.values.size should equal (2)
    testRepoPool[Friend] shouldBe a [MongoRepo[_]]
    testRepoPool.baseRepoMap[Friend].pType should equal (Friend)
    testRepoPool[Message] shouldBe a [MongoRepo[_]]
    testRepoPool.baseRepoMap[Message].pType should equal (Message)
  }

  behavior of "LongevityContext.repoPool of a cassandra longevity context"

  it should "be full of CassandraRepos" in {
    val repoPool = cassandraLongevityContext.testRepoPool
    repoPool.values.size should equal (2)
    repoPool[Friend] shouldBe a [CassandraRepo[_]]
    repoPool.baseRepoMap[Friend].pType should equal (Friend)
    repoPool[Message] shouldBe a [CassandraRepo[_]]
    repoPool.baseRepoMap[Message].pType should equal (Message)
  }

  behavior of "LongevityContext.inMemTestRepoPool"

  it should "be full of InMemRepos" in {
    Seq(
      inMemLongevityContext.inMemTestRepoPool,
      mongoLongevityContext.inMemTestRepoPool,
      cassandraLongevityContext.inMemTestRepoPool
    ) foreach { repoPool =>
      repoPool.values.size should equal (2)
      repoPool[Friend] shouldBe an [InMemRepo[_]]
      repoPool.baseRepoMap[Friend].pType should equal (Friend)
      repoPool[Message] shouldBe an [InMemRepo[_]]
      repoPool.baseRepoMap[Message].pType should equal (Message)
    }
  }

}
