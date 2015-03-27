package longevity.persistence

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.persistence.messageFriend._
import longevity.context._

/** unit tests for the proper construction of [[LongevityContext.repoPool]] and [[LongevityContext.inMemRepoPool]]
 */
@longevity.UnitTest
class LongevityContextRepoPoolSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "a repo pool of an in-memory longevity context with no specializations"

  it should "be full of stock repos" in {
    val repoPool = inMemLongevityContext.repoPool
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe an [InMemRepo[_]]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Message].value shouldBe an [InMemRepo[_]]
    repoPool.get[Message].value.entityType should equal (MessageType)
  }

  behavior of "a repo pool of a mongo longevity context with no specializations"

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

  behavior of "a repo pool of an in-memory longevity context with specializations"

  it should "contain the specialized repos" in {
    class SpecializedFriendRepo(longevityContext: LongevityContext)
    extends InMemRepo[Friend](FriendType, longevityContext) {
      def numHolesItTakesToFillTheAlbertHall: Int = ???
    }
    def factory = (longevityContext: LongevityContext) => new SpecializedFriendRepo(longevityContext)
    val longevityContext = LongevityContext(
      InMem,
      subdomain,
      specializations = emptySpecializedRepoFactoryPool + factory
    )
    val repoPool = longevityContext.repoPool
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe a [SpecializedFriendRepo]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Message].value shouldBe an [InMemRepo[_]]
    repoPool.get[Message].value.entityType should equal (MessageType)
  }

  behavior of "a repo pool of a mongo longevity context with specializations"

  it should "contain the specialized repos" in {
    class SpecializedFriendRepo(longevityContext: LongevityContext)
    extends MongoRepo[Friend](FriendType, longevityContext) {
      def numHolesItTakesToFillTheAlbertHall: Int = ???
    }
    def factory = (longevityContext: LongevityContext) => new SpecializedFriendRepo(longevityContext)
    val longevityContext = LongevityContext(
      Mongo,
      subdomain,
      specializations = emptySpecializedRepoFactoryPool + factory
    )
    val repoPool = longevityContext.repoPool
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe a [SpecializedFriendRepo]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Message].value shouldBe a [MongoRepo[_]]
    repoPool.get[Message].value.entityType should equal (MessageType)
  }

}
