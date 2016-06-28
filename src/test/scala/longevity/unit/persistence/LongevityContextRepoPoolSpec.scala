package longevity.unit.persistence

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.OptionValues.convertOptionToValuable
import longevity.persistence.inmem.InMemRepo
import longevity.persistence.cassandra.CassandraRepo
import longevity.persistence.mongo.MongoRepo

/** unit tests for the proper construction of [[LongevityContext.repoPool]] and
 * [[LongevityContext.inMemRepoPool]]
 */
class LongevityContextRepoPoolSpec extends FlatSpec with GivenWhenThen with Matchers {

  import longevity.unit.persistence.messageFriend._

  behavior of "LongevityContext.repoPool for an in-memory longevity context"

  it should "be full of InMemRepos" in {
    val repoPool = inMemLongevityContext.repoPool
    repoPool.typeKeyMap.size should equal (2)
    repoPool.typeKeyMap.get[Friend].value shouldBe an [InMemRepo[_]]
    repoPool.baseRepoMap.get[Friend].value.pType should equal (Friend)
    repoPool.typeKeyMap.get[Message].value shouldBe an [InMemRepo[_]]
    repoPool.baseRepoMap.get[Message].value.pType should equal (Message)
  }

  behavior of "LongevityContext.repoPool for a mongo longevity context"

  it should "be full of MongoRepos" in {
    val repoPool = mongoLongevityContext.repoPool
    repoPool.typeKeyMap.size should equal (2)
    repoPool.typeKeyMap.get[Friend].value shouldBe a [MongoRepo[_]]
    repoPool.baseRepoMap.get[Friend].value.pType should equal (Friend)
    repoPool.typeKeyMap.get[Message].value shouldBe a [MongoRepo[_]]
    repoPool.baseRepoMap.get[Message].value.pType should equal (Message)
  }

  behavior of "LongevityContext.repoPool for a cassandra longevity context"

  it should "be full of CassandraRepos" in {
    val repoPool = cassandraLongevityContext.repoPool
    repoPool.typeKeyMap.size should equal (2)
    repoPool.typeKeyMap.get[Friend].value shouldBe a [CassandraRepo[_]]
    repoPool.baseRepoMap.get[Friend].value.pType should equal (Friend)
    repoPool.typeKeyMap.get[Message].value shouldBe a [CassandraRepo[_]]
    repoPool.baseRepoMap.get[Message].value.pType should equal (Message)
  }

  behavior of "LongevityContext.testRepoPool of an in-memory longevity context"

  it should "be full of InMemRepos" in {
    val testRepoPool = inMemLongevityContext.testRepoPool
    testRepoPool.typeKeyMap.size should equal (2)
    testRepoPool.typeKeyMap.get[Friend].value shouldBe an [InMemRepo[_]]
    testRepoPool.baseRepoMap.get[Friend].value.pType should equal (Friend)
    testRepoPool.typeKeyMap.get[Message].value shouldBe an [InMemRepo[_]]
    testRepoPool.baseRepoMap.get[Message].value.pType should equal (Message)
  }

  behavior of "LongevityContext.testRepoPool of a mongo longevity context"

  it should "be full of MongoRepos" in {
    val testRepoPool = mongoLongevityContext.testRepoPool
    testRepoPool.typeKeyMap.size should equal (2)
    testRepoPool.typeKeyMap.get[Friend].value shouldBe a [MongoRepo[_]]
    testRepoPool.baseRepoMap.get[Friend].value.pType should equal (Friend)
    testRepoPool.typeKeyMap.get[Message].value shouldBe a [MongoRepo[_]]
    testRepoPool.baseRepoMap.get[Message].value.pType should equal (Message)
  }

  behavior of "LongevityContext.repoPool of a cassandra longevity context"

  it should "be full of CassandraRepos" in {
    val repoPool = cassandraLongevityContext.testRepoPool
    repoPool.typeKeyMap.size should equal (2)
    repoPool.typeKeyMap.get[Friend].value shouldBe a [CassandraRepo[_]]
    repoPool.baseRepoMap.get[Friend].value.pType should equal (Friend)
    repoPool.typeKeyMap.get[Message].value shouldBe a [CassandraRepo[_]]
    repoPool.baseRepoMap.get[Message].value.pType should equal (Message)
  }

  behavior of "LongevityContext.inMemTestRepoPool"

  it should "be full of InMemRepos" in {
    Seq(
      inMemLongevityContext.inMemTestRepoPool,
      mongoLongevityContext.inMemTestRepoPool,
      cassandraLongevityContext.inMemTestRepoPool
    ) foreach { repoPool =>
      repoPool.typeKeyMap.size should equal (2)
      repoPool.typeKeyMap.get[Friend].value shouldBe an [InMemRepo[_]]
      repoPool.baseRepoMap.get[Friend].value.pType should equal (Friend)
      repoPool.typeKeyMap.get[Message].value shouldBe an [InMemRepo[_]]
      repoPool.baseRepoMap.get[Message].value.pType should equal (Message)
    }
  }

}
