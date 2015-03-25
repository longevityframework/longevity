package longevity.repo

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.repo.testUtil._
import longevity.context._

/** unit tests for the proper construction of [[BoundedContext.repoPool]] and [[BoundedContext.inMemRepoPool]]
 */
@longevity.UnitTest
class BoundedContextRepoPoolSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "a repo pool of an in-memory bounded context with no specializations"

  it should "be full of stock repos" in {
    val repoPool = inMemBoundedContext.repoPool
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe an [InMemRepo[_]]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Post].value shouldBe an [InMemRepo[_]]
    repoPool.get[Post].value.entityType should equal (PostType)
  }

  behavior of "a repo pool of a mongo bounded context with no specializations"

  it should "be full of stock repos" in {
    val repoPool = boundedContext.repoPool
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe a [MongoRepo[_]]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Post].value shouldBe a [MongoRepo[_]]
    repoPool.get[Post].value.entityType should equal (PostType)
  }

  behavior of "a test in-memory repo pool of a bounded context"

  it should "be full of stock repos" in {
    Seq(
      boundedContext.inMemRepoPool,
      inMemBoundedContext.inMemRepoPool
    ) foreach { repoPool =>
      repoPool.size should equal (2)
      repoPool.get[Friend].value shouldBe an [InMemRepo[_]]
      repoPool.get[Friend].value.entityType should equal (FriendType)
      repoPool.get[Post].value shouldBe an [InMemRepo[_]]
      repoPool.get[Post].value.entityType should equal (PostType)
    }
  }

  behavior of "a repo pool of an in-memory bounded context with specializations"

  it should "contain the specialized repos" in {
    class SpecializedFriendRepo extends InMemRepo[Friend](FriendType) {
      def numHolesItTakesToFillTheAlbertHall: Int = ???
    }
    val boundedContext = BoundedContext(
      InMem,
      subdomain,
      specializations = emptyProvisionalRepoPool + new SpecializedFriendRepo
    )
    val repoPool = boundedContext.repoPool
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe a [SpecializedFriendRepo]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Post].value shouldBe an [InMemRepo[_]]
    repoPool.get[Post].value.entityType should equal (PostType)
  }

  behavior of "a repo pool of a mongo bounded context with specializations"

  it should "contain the specialized repos" in {
    class SpecializedFriendRepo extends MongoRepo[Friend](FriendType) {
      def numHolesItTakesToFillTheAlbertHall: Int = ???
    }
    val boundedContext = BoundedContext(
      Mongo,
      subdomain,
      specializations = emptyProvisionalRepoPool + new SpecializedFriendRepo
    )
    val repoPool = boundedContext.repoPool
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe a [SpecializedFriendRepo]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Post].value shouldBe an [MongoRepo[_]]
    repoPool.get[Post].value.entityType should equal (PostType)
  }

}
