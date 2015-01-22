package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[TypeKeyMap]] specifications */
class TypeKeyMapSpec extends FlatSpec with GivenWhenThen with Matchers {

  // TODO identity example

  behavior of "a TypeKeyMap where the value type has a single type parameter"

  trait Entity
  case class User(uri: String) extends Entity
  case class Blog(uri: String) extends Entity

  trait Repo[E <: Entity] {
    var saveCount = 0
    def save(entity: E): Unit = saveCount += 1
  }
  class UserRepo extends Repo[User]
  val userRepo = new UserRepo
  class BlogRepo extends Repo[Blog]
  val blogRepo = new BlogRepo

  it should "only allow key/value pairs with matching type param" in {
    var entityTypeToRepoMap = TypeKeyMap[Entity, Repo]()

    "entityTypeToRepoMap += (typeKey[User], userRepo)" should compile
    "entityTypeToRepoMap += (typeKey[User], blogRepo)" shouldNot compile
    "entityTypeToRepoMap += (typeKey[Blog], userRepo)" shouldNot compile

    "entityTypeToRepoMap += typeKey[User] -> userRepo" should compile
    "entityTypeToRepoMap += typeKey[User] -> blogRepo" shouldNot compile
    "entityTypeToRepoMap += typeKey[Blog] -> userRepo" shouldNot compile
  }

  it should "store multiple key/value pairs for a given type param" in {
    val typeKeySet = Set(typeKey[User], typeKey[Blog])

    var localEntityStore = TypeKeyMap[Entity, Seq]()
    localEntityStore += (typeKey[User], Seq(User("user1"), User("user2"), User("user3")))
    localEntityStore += (typeKey[Blog], Seq(Blog("blog1"), Blog("blog2")))

    var entityTypeToRepoMap = TypeKeyMap[Entity, Repo]()
    entityTypeToRepoMap += (typeKey[User], userRepo)
    entityTypeToRepoMap += (typeKey[Blog], blogRepo)

    def saveEntities[E <: Entity : TypeKey]: Unit = {
      val entitySeq = localEntityStore(typeKey)
      val repo = entityTypeToRepoMap(typeKey)
      entitySeq.foreach { entity => repo.save(entity) }
    }

    typeKeySet.foreach { implicit typeKey => saveEntities }

    userRepo.saveCount should equal (3)
    blogRepo.saveCount should equal (2)
  }

  // TODO: double-TP value type
  // TODO: specs to exercise api in BaseTypedKeyMapSpec

}
