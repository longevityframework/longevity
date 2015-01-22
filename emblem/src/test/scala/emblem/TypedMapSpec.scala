package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[TypedMap]] specifications */
class TypedMapSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "the example code in the scaladocs for TypedMap"

  trait Pet
  case class Cat(name: String) extends Pet
  case class Dog(name: String) extends Pet
  class PetStore[P <: Pet]

  it should "compile and produce the expected values" in {
    val catStore1 = new PetStore[Cat]
    val catStore2 = new PetStore[Cat]
    val dogStore1 = new PetStore[Dog]

    var inventories = TypedMap[Pet, PetStore, List]
    inventories += (typeKey[Cat], catStore1, Cat("cat11") :: Cat("cat12") :: Cat("cat13") :: Nil)
    inventories += (typeKey[Cat], catStore2, Cat("cat21") :: Nil)
    inventories += (typeKey[Dog], dogStore1, Dog("dog11") :: Dog("dog12") :: Nil)

    val cats1: List[Cat] = inventories(catStore1)
    cats1.size should be (3)
    val cats2: List[Cat] = inventories(catStore2)
    cats2.size should be (1)
    val dogs1: List[Dog] = inventories(dogStore1)
    dogs1.size should be (2)

    val cat: Cat = inventories(catStore1).head
    cat should equal (Cat("cat11"))
    val dog: Dog = inventories(dogStore1).head
    dog should equal (Dog("dog11"))
  }

  behavior of "a TypedMap where the type bound, key, and value types are all the same"

  private sealed trait ComputerPart
  private case class Memory(gb: Int) extends ComputerPart
  private case class CPU(mhz: Double) extends ComputerPart
  private case class Display(resolution: Int) extends ComputerPart
  private type Identity[Part <: ComputerPart] = Part
  private case class Computer(memory: Memory, cpu: CPU, display: Display)

  it should "only allow key/value pairs with matching type param" in {
    var partsUpgradeMap = TypedMap[ComputerPart, Identity, Identity]()
    "partsUpgradeMap += typeKey[Memory] -> Memory(4) -> Memory(8)" should compile
    "partsUpgradeMap += typeKey[Memory] -> Memory(4) -> CPU(2.4)" shouldNot compile
  }

  it should "store multiple key/value pairs for a given type param" in {
    var partsUpgradeMap = TypedMap[ComputerPart, Identity, Identity]()
    partsUpgradeMap += typeKey[Memory] -> Memory(2) -> Memory(0)
    partsUpgradeMap += typeKey[Memory] -> Memory(2) -> Memory(4) // overwrites value Memory(0)
    partsUpgradeMap += typeKey[Memory] -> Memory(4) -> Memory(8)
    partsUpgradeMap(Memory(2)) should equal (Memory(4))
    partsUpgradeMap(Memory(4)) should equal (Memory(8))
    partsUpgradeMap += typeKey[CPU] -> CPU(2.2) -> CPU(2.6)
    partsUpgradeMap += typeKey[CPU] -> CPU(2.4) -> CPU(2.6)
    partsUpgradeMap += typeKey[CPU] -> CPU(2.6) -> CPU(2.8)
    partsUpgradeMap += typeKey[Display] -> Display(720) -> Display(1080)

    def upgradeComputer(computer: Computer): Computer = Computer(
      partsUpgradeMap.getOrElse(computer.memory, computer.memory),
      partsUpgradeMap.getOrElse(computer.cpu, computer.cpu),
      partsUpgradeMap.getOrElse(computer.display, computer.display))

    val superComputer = Computer(Memory(32), CPU(5.0), Display(7000))
    upgradeComputer(superComputer) should equal (superComputer)

    val decentComputer = Computer(Memory(4), CPU(2.4), Display(1040))
    upgradeComputer(decentComputer) should equal {
      decentComputer.copy(memory = Memory(8), cpu = CPU(2.6))
    }

    val lousyComputer = Computer(Memory(2), CPU(2.0), Display(576))
    upgradeComputer(lousyComputer) should equal {
      lousyComputer.copy(memory = Memory(4))
    }
  }

  behavior of "a TypedMap where both key and value have a single shared type parameter"

  trait Entity
  case class User(uri: String) extends Entity
  case class Blog(uri: String) extends Entity

  trait EntityType[E <: Entity]
  object userType extends EntityType[User]
  object blogType extends EntityType[Blog]

  trait Repo[E <: Entity] {
    var saveCount = 0
    def save(entity: E): Unit = saveCount += 1
  }
  class UserRepo extends Repo[User]
  val userRepo = new UserRepo
  class BlogRepo extends Repo[Blog]
  val blogRepo = new BlogRepo

  it should "only allow key/value pairs with matching type param" in {
    var entityTypeToRepoMap = TypedMap[Entity, EntityType, Repo]()

    "entityTypeToRepoMap += (typeKey[User], userType, userRepo)" should compile
    "entityTypeToRepoMap += (typeKey[User], userType, blogRepo)" shouldNot compile
    "entityTypeToRepoMap += (typeKey[User], blogType, userRepo)" shouldNot compile

    "entityTypeToRepoMap += typeKey[User] -> userType -> userRepo" should compile
    "entityTypeToRepoMap += typeKey[User] -> userType -> blogRepo" shouldNot compile
    "entityTypeToRepoMap += typeKey[User] -> blogType -> userRepo" shouldNot compile
  }

  it should "store multiple key/value pairs for a given type param" in {
    val entityTypeSet = Set(userType, blogType)

    var localEntityStore = TypedMap[Entity, EntityType, Seq]()
    localEntityStore += (typeKey[User], userType, Seq(User("user1"), User("user2"), User("user3")))
    localEntityStore += (typeKey[Blog], blogType, Seq(Blog("blog1"), Blog("blog2")))

    var entityTypeToRepoMap = TypedMap[Entity, EntityType, Repo]()
    entityTypeToRepoMap += (typeKey[User], userType, userRepo)
    entityTypeToRepoMap += (typeKey[Blog], blogType, blogRepo)

    def saveEntities[E <: Entity : TypeKey](entityType: EntityType[E]): Unit = {
      val entities = localEntityStore(entityType)
      val repo = entityTypeToRepoMap(entityType)
      entities.foreach { entity => repo.save(entity) }
    }

    entityTypeSet.foreach { entityType => saveEntities(entityType) }

    userRepo.saveCount should equal (3)
    blogRepo.saveCount should equal (2)
  }

  // TODO: single-TP key type, double-TP value type
  // TODO: double-TP key type, single-TP value type

}
