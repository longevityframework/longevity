package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[TypeKeyMap]] specifications */
class TypeKeyMapSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "the example code in the scaladocs for TypeKeyMap"
  it should "compile and produce the expected values" in {

    import emblem.testData.computerParts._

    var partLists = TypeKeyMap[ComputerPart, List]()
    partLists += Memory(2) :: Memory(4) :: Memory(8) :: Nil
    partLists += CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    partLists += Display(720) :: Display(1080) :: Nil

    val memories: List[Memory] = partLists[Memory]
    memories.size should be (3)
    val cpus: List[CPU] = partLists[CPU]
    cpus.size should be (3)
    val displays: List[Display] = partLists[Display]
    displays.size should be (2)

    val cpu: CPU = partLists[CPU].head
    cpu should equal (CPU(2.2))
    val display: Display = partLists[Display].tail.head
    display should equal (Display(1080))

  }

  // TODO identity example

  behavior of "a TypeKeyMap where the value type has a single type parameter"

  import emblem.testData.blogs._
  val userRepo = new UserRepo
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
