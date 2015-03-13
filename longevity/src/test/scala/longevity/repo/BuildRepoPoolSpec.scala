package longevity.repo

import org.scalatest._
import org.scalatest.OptionValues._
import testUtil._

/** unit tests for builder methods [[longevity.repo.inMemRepoPool]] and [[longevity.repo.mongoRepoPool]]
 * found in the [[longevity.repo]] package object
 */
@longevity.UnitTest
class BuildRepoPoolSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "longevity.repo.inMemRepoPool"

  it should "build repo pools full of stock repos" in {
    val repoPool = inMemRepoPool(boundedContext)
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe an [InMemRepo[_]]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Post].value shouldBe an [InMemRepo[_]]
    repoPool.get[Post].value.entityType should equal (PostType)
  }

  it should "build repo pools with specialized repos when requested" in {
    class SpecializedFriendRepo extends InMemRepo[Friend](FriendType) {
      def numHolesItTakesToFillTheAlbertHall: Int = ???
    }
    val repoPool = inMemRepoPool(
      boundedContext,
      emptyProvisionalRepoPool + new SpecializedFriendRepo
    )
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe a [SpecializedFriendRepo]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Post].value shouldBe an [InMemRepo[_]]
    repoPool.get[Post].value.entityType should equal (PostType)
  }

  behavior of "longevity.repo.mongoRepoPool"

  it should "build repo pools full of stock repos" in {
    val repoPool = mongoRepoPool(boundedContext)
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe an [MongoRepo[_]]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Post].value shouldBe an [MongoRepo[_]]
    repoPool.get[Post].value.entityType should equal (PostType)
  }

  it should "build repo pools with specialized repos when requested" in {
    class SpecializedFriendRepo extends MongoRepo[Friend](FriendType) {
      def numHolesItTakesToFillTheAlbertHall: Int = ???
    }
    val repoPool = mongoRepoPool(
      boundedContext,
      emptyProvisionalRepoPool + new SpecializedFriendRepo
    )
    repoPool.size should equal (2)
    repoPool.get[Friend].value shouldBe a [SpecializedFriendRepo]
    repoPool.get[Friend].value.entityType should equal (FriendType)
    repoPool.get[Post].value shouldBe an [MongoRepo[_]]
    repoPool.get[Post].value.entityType should equal (PostType)
  }

}
