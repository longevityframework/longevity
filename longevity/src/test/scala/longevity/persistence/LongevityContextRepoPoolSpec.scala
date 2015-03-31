package longevity.persistence

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.persistence.messageFriend._
import longevity.context._
import emblem.imports._

/** unit tests for the proper construction of [[LongevityContext.repoPool]] and
 * [[LongevityContext.inMemRepoPool]]
 */
@longevity.UnitTest
class LongevityContextRepoPoolSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "a repo pool of an in-memory longevity context with no specializedRepoFactoryPool"

  it should "be full of stock repos" in {
    val repoPool = inMemLongevityContext.repoPool
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe an [InMemRepo[_]]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Message].value shouldBe an [InMemRepo[_]]
    repoPool.get[Message].value.entityType should equal (MessageType)
  }

  behavior of "a repo pool of a mongo longevity context with no specializedRepoFactoryPool"

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

  behavior of "a repo pool of an in-memory longevity context with specializedRepoFactoryPool"

  it should "contain the specialized repos" in {
    class SpecializedFriendRepo extends InMemRepo[Friend](FriendType) {
      def numHolesItTakesToFillTheAlbertHall: Int = ???
    }
    def factory = (emblemPool: EmblemPool, shorthandPool: ShorthandPool) => new SpecializedFriendRepo
    val longevityContext = LongevityContext(
      subdomain,
      ShorthandPool.empty,
      InMem,
      specializedRepoFactoryPool = SpecializedRepoFactoryPool.empty + factory
    )
    val repoPool = longevityContext.repoPool
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe a [SpecializedFriendRepo]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Message].value shouldBe an [InMemRepo[_]]
    repoPool.get[Message].value.entityType should equal (MessageType)
  }

  behavior of "a repo pool of a mongo longevity context with specializedRepoFactoryPool"

  it should "contain the specialized repos" in {
    class SpecializedFriendRepo(emblemPool: EmblemPool, shorthandPool: ShorthandPool)
    extends MongoRepo[Friend](FriendType, emblemPool, shorthandPool) {
      def numHolesItTakesToFillTheAlbertHall: Int = ???
    }
    def factory = { (emblemPool: EmblemPool, shorthandPool: ShorthandPool) =>
      new SpecializedFriendRepo(emblemPool, shorthandPool)
    }
    val longevityContext = LongevityContext(
      subdomain,
      ShorthandPool.empty,
      Mongo,
      specializedRepoFactoryPool = SpecializedRepoFactoryPool.empty + factory
    )
    val repoPool = longevityContext.repoPool
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe a [SpecializedFriendRepo]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Message].value shouldBe a [MongoRepo[_]]
    repoPool.get[Message].value.entityType should equal (MessageType)
  }

}
