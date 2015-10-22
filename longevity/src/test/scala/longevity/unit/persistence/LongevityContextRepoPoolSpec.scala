package longevity.unit.persistence

import org.scalatest._
import org.scalatest.OptionValues._
import emblem.imports._
import longevity.persistence._

/** unit tests for the proper construction of [[LongevityContext.repoPool]] and
 * [[LongevityContext.inMemRepoPool]]
 */
class LongevityContextRepoPoolSpec extends FlatSpec with GivenWhenThen with Matchers {

  import longevity.unit.persistence.messageFriend._
  import longevity.unit.persistence.messageFriend.context._

  behavior of "LongevityContext.repoPool of an in-memory longevity context"

  it should "be full of InMemRepos" in {
    val repoPool = inMemLongevityContext.repoPool
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe an [InMemRepo[_]]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Message].value shouldBe an [InMemRepo[_]]
    repoPool.get[Message].value.entityType should equal (MessageType)
  }

  behavior of "LongevityContext.repoPool of a mongo longevity context"

  it should "be full of MongoRepos" in {
    val repoPool = longevityContext.repoPool
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe a [MongoRepo[_]]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Message].value shouldBe a [MongoRepo[_]]
    repoPool.get[Message].value.entityType should equal (MessageType)
  }

  behavior of "LongevityContext.testRepoPool of an in-memory longevity context"

  it should "be full of InMemRepos" in {
    val testRepoPool = inMemLongevityContext.testRepoPool
    testRepoPool.size should equal (2)
    testRepoPool.get[Friend].value shouldBe an [InMemRepo[_]]
    testRepoPool.get[Friend].value.entityType should equal (FriendType)
    testRepoPool.get[Message].value shouldBe an [InMemRepo[_]]
    testRepoPool.get[Message].value.entityType should equal (MessageType)
  }

  behavior of "LongevityContext.testRepoPool of a mongo longevity context"

  it should "be full of MongoRepos" in {
    val testRepoPool = longevityContext.testRepoPool
    testRepoPool.size should equal (2)
    testRepoPool.get[Friend].value shouldBe a [MongoRepo[_]]
    testRepoPool.get[Friend].value.entityType should equal (FriendType)
    testRepoPool.get[Message].value shouldBe a [MongoRepo[_]]
    testRepoPool.get[Message].value.entityType should equal (MessageType)
  }

  behavior of "LongevityContext.inMemTestRepoPool"

  it should "be full of InMemRepos" in {
    Seq(
      longevityContext.inMemTestRepoPool,
      inMemLongevityContext.inMemTestRepoPool
    ) foreach { repoPool =>
      repoPool.size should equal (2)
      repoPool.get[Friend].value shouldBe an [InMemRepo[_]]
      repoPool.get[Friend].value.entityType should equal (FriendType)
      repoPool.get[Message].value shouldBe an [InMemRepo[_]]
      repoPool.get[Message].value.entityType should equal (MessageType)
    }
  }

}
