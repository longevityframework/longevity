package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[TypedMap]] specifications */
class TypedMapSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "the example code in the scaladocs for TypedMap"
  it should "compile and produce the expected values" in {

    import emblem.testData.pets._

    val catStore1 = new PetStore[Cat]
    val catStore2 = new PetStore[Cat]
    val dogStore1 = new PetStore[Dog]

    var inventories = TypedMap[Pet, PetStore, List]
    inventories += (catStore1, Cat("cat11") :: Cat("cat12") :: Cat("cat13") :: Nil)
    inventories += (catStore2, Cat("cat21") :: Nil)
    inventories += (dogStore1, Dog("dog11") :: Dog("dog12") :: Nil)

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

  behavior of "TypedMaps in the face of someone trying to break through my bulletpoof type signatures"
  it should "thwart the attack with a compile time error of unspecified opacity" in {
    import testData.pets._
    val dog = Dog("dog")
    val hound = Hound("hound")
    var petToPetMap = TypedMap[Pet, PetIdentity, PetIdentity]()

    "petToPetMap += dog -> dog" should compile

    petToPetMap += dog -> dog
    petToPetMap(dog) should equal (dog)

    // you are not allowed to map a hound to a dog.
    // otherwise you could then call "petToPetMap(hound)" and get a ClassCastException.
    "petToPetMap += hound -> dog" shouldNot compile

    // you are allowed to map a dog to a hound.
    // in this case petToPetMap(dog) return something of type PetIdentity[Dog] (which =:= Dog), and the
    // something returned is hound. nothing weird, no ClassCastException.
    "petToPetMap += dog -> hound" should compile

    petToPetMap = TypedMap[Pet, PetIdentity, PetIdentity]()
    petToPetMap += dog -> hound
    petToPetMap(dog) should equal (hound)

    var petToPetBoxInvarMap = TypedMap[Pet, PetIdentity, PetBoxInvar]()

    "petToPetBoxInvarMap += dog -> new PetBoxInvar(dog)" should compile

    petToPetBoxInvarMap += dog -> new PetBoxInvar(dog)
    petToPetBoxInvarMap[Pet](dog).p should equal (dog)

    // you are not allowed to map a hound to a PetBoxInvar[Dog].
    // otherwise you could then call "petToPetBoxInvarMap(hound)" and get a ClassCastException.
    "petToPetBoxInvarMap += hound -> new PetBoxInvar(dog)" shouldNot compile

    // you are not allowed to map a dog to a PetBoxInvar[Hound].
    // otherwise you could then call "petToPetBoxInvarMap(dog)" and get a ClassCastException.
    "petToPetBoxInvarMap += dog -> new PetBoxInvar(hound)" shouldNot compile

    var petToPetBoxCovarMap = TypedMap[Pet, PetIdentity, PetBoxCovar]()

    "petToPetBoxCovarMap += dog -> new PetBoxCovar(dog)" should compile

    petToPetBoxCovarMap += dog -> new PetBoxCovar(dog)
    petToPetBoxCovarMap[Pet](dog).p should equal (dog)

    // you are not allowed to map a hound to a PetBoxCovar[Dog].
    // otherwise you could then call "petToPetBoxCovarMap(hound)" and get a ClassCastException.
    "petToPetBoxCovarMap += hound -> new PetBoxCovar(dog)" shouldNot compile

    // you are allowed to map a dog to a PetBoxCovar[Hound].
    // in this case petToPetMap(dog) return something of type PetBoxCovar[Dog], which is >:> PetBoxCovar[Hound]
    "petToPetBoxCovarMap += dog -> new PetBoxCovar(hound)" should compile

    petToPetBoxCovarMap = TypedMap[Pet, PetIdentity, PetBoxCovar]()
    petToPetBoxCovarMap += dog -> new PetBoxCovar(hound)
    petToPetBoxCovarMap(dog).p should equal (hound)

    var petToPetBoxContravarMap = TypedMap[Pet, PetIdentity, PetBoxContravar]()

    "petToPetBoxContravarMap += dog -> new PetBoxContravar[Dog]" should compile

    petToPetBoxContravarMap += dog -> new PetBoxContravar[Dog]
    "val contravar: PetBoxContravar[Dog] = petToPetBoxContravarMap(dog)" should compile
    "val contravar: PetBoxContravar[Hound] = petToPetBoxContravarMap(dog)" should compile

    // you are allowed to map a hound to a PetBoxContravar[Dog].
    // in this case petToPetMap(hound) return something of type PetBoxContravar[Hound],
    // which is >:> PetBoxContravar[Dog]
    "petToPetBoxContravarMap += hound -> new PetBoxContravar[Dog]" should compile

    petToPetBoxContravarMap = TypedMap[Pet, PetIdentity, PetBoxContravar]()
    petToPetBoxContravarMap += hound -> new PetBoxContravar[Dog]
    "val contravar: PetBoxContravar[Dog] = petToPetBoxContravarMap(hound)" should compile
    "val contravar: PetBoxContravar[Hound] = petToPetBoxContravarMap(hound)" should compile

    // you are not allowed to map a dog to a PetBoxContravar[Hound].
    // otherwise you could then call "petToPetBoxContravarMap(dog)" and get a ClassCastException.
    "petToPetBoxContravarMap += dog -> new PetBoxContravar[Hound]" shouldNot compile
    
  }

  behavior of "a TypedMap where the type bound, key, and value types are all the same"

  import emblem.testData.computerParts._
  type Identity[Part <: ComputerPart] = Part

  it should "only allow key/value pairs with matching type param" in {
    var partsUpgradeMap = TypedMap[ComputerPart, Identity, Identity]()
    "partsUpgradeMap += Memory(4) -> Memory(8)" should compile
    "partsUpgradeMap += Memory(4) -> CPU(2.4)" shouldNot compile
    "partsUpgradeMap += CPU(2.4) -> Memory(4)" shouldNot compile
    "partsUpgradeMap += (Memory(4), Memory(8))" should compile
    "partsUpgradeMap += (Memory(4), CPU(2.4))" shouldNot compile
    "partsUpgradeMap += (CPU(2.4), Memory(4))" shouldNot compile
  }

  it should "store multiple key/value pairs for a given type param" in {
    var partsUpgradeMap = TypedMap[ComputerPart, Identity, Identity]()
    partsUpgradeMap += Memory(2) -> Memory(0)
    partsUpgradeMap += Memory(2) -> Memory(4) // overwrites value Memory(0)
    partsUpgradeMap += Memory(4) -> Memory(8)
    partsUpgradeMap(Memory(2)) should equal (Memory(4))
    partsUpgradeMap(Memory(4)) should equal (Memory(8))
    partsUpgradeMap += CPU(2.2) -> CPU(2.6)
    partsUpgradeMap += CPU(2.4) -> CPU(2.6)
    partsUpgradeMap += CPU(2.6) -> CPU(2.8)
    partsUpgradeMap += Display(720) -> Display(1080)

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

  import emblem.testData.blogs._
  val userRepo = new UserRepo
  val blogRepo = new BlogRepo

  it should "only allow key/value pairs with matching type param" in {
    var entityTypeToRepoMap = TypedMap[Entity, EntityType, Repo]()

    "entityTypeToRepoMap += (userType, userRepo)" should compile
    "entityTypeToRepoMap += (userType, blogRepo)" shouldNot compile
    "entityTypeToRepoMap += (blogType, userRepo)" shouldNot compile

    "entityTypeToRepoMap += userType -> userRepo" should compile
    "entityTypeToRepoMap += userType -> blogRepo" shouldNot compile
    "entityTypeToRepoMap += blogType -> userRepo" shouldNot compile
  }

  it should "store multiple key/value pairs for a given type param" in {
    val entityTypeSet = Set(userType, blogType)

    var localEntityStore = TypedMap[Entity, EntityType, Seq]()
    localEntityStore += (userType, Seq(User("user1"), User("user2"), User("user3")))
    localEntityStore += (blogType, Seq(Blog("blog1"), Blog("blog2")))

    var entityTypeToRepoMap = TypedMap[Entity, EntityType, Repo]()
    entityTypeToRepoMap += (userType, userRepo)
    entityTypeToRepoMap += (blogType, blogRepo)

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
