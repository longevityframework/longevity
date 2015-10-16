package longevity.unit.persistence

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.unit.persistence.messageFriend._
import longevity.unit.persistence.messageFriend.context._
import emblem.imports._
import longevity.persistence._

/** unit tests for the proper construction of [[LongevityContext.repoPool]] and
 * [[LongevityContext.inMemRepoPool]]
 */
class LongevityContextRepoPoolSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "a repo pool of an in-memory longevity context"

  it should "be full of stock repos" in {
    val repoPool = inMemLongevityContext.repoPool
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe an [InMemRepo[_]]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Message].value shouldBe an [InMemRepo[_]]
    repoPool.get[Message].value.entityType should equal (MessageType)
  }

  behavior of "a repo pool of a mongo longevity context"

  it should "be full of stock repos" in {
    val repoPool = longevityContext.repoPool
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe a [MongoRepo[_]]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Message].value shouldBe a [MongoRepo[_]]
    repoPool.get[Message].value.entityType should equal (MessageType)
  }

  behavior of "a test in-memory repo pool of a longevity context"

  it should "be full of stock repos" in {
    Seq(
      longevityContext.inMemRepoPool,
      inMemLongevityContext.inMemRepoPool
    ) foreach { repoPool =>
      repoPool.size should equal (2)
      repoPool.get[Friend].value shouldBe an [InMemRepo[_]]
      repoPool.get[Friend].value.entityType should equal (FriendType)
      repoPool.get[Message].value shouldBe an [InMemRepo[_]]
      repoPool.get[Message].value.entityType should equal (MessageType)
    }
  }

}
