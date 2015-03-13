package emblem

import org.scalatest._

/** [[TypeBoundMap]] specifications */
class TypeBoundMapSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "the example code in the scaladocs for TypeBoundMap"
  it should "compile and produce the expected values" in {

    import emblem.testData.pets._

    val catStore1 = new PetStore[Cat]
    val catStore2 = new PetStore[Cat]
    val dogStore1 = new PetStore[Dog]

    var inventories = TypeBoundMap[Pet, PetStore, List]
    inventories += (catStore1 -> List(Cat("cat11"), Cat("cat12"), Cat("cat13")))
    inventories += (catStore2 -> List(Cat("cat21")))
    inventories += (dogStore1 -> List(Dog("dog11"), Dog("dog12")))

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

  behavior of "a TypeBoundMap where the type bound, key, and value types are all the same"

  import emblem.testData.computerParts._
  type Identity[Part <: ComputerPart] = Part

  it should "only allow key/value pairs with matching type param" in {
    var partsUpgradeMap = TypeBoundMap[ComputerPart, Identity, Identity]()
    "partsUpgradeMap += Memory(4) -> Memory(8)" should compile
    "partsUpgradeMap += Memory(4) -> CPU(2.4)" shouldNot compile
    "partsUpgradeMap += CPU(2.4) -> Memory(4)" shouldNot compile
  }

  it should "store multiple key/value pairs for a given type param" in {
    var partsUpgradeMap = TypeBoundMap[ComputerPart, Identity, Identity]()
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

  behavior of "a TypeBoundMap where both key and value have a single shared type parameter"

  import emblem.testData.blogs._
  val userRepo = new CrmUserRepo
  val blogRepo = new CrmBlogRepo

  it should "only allow key/value pairs with matching type param" in {
    var entityTypeToRepoMap = TypeBoundMap[CrmEntity, CrmEntityType, CrmRepo]()
    "entityTypeToRepoMap += userType -> userRepo" should compile
    "entityTypeToRepoMap += userType -> blogRepo" shouldNot compile
    "entityTypeToRepoMap += blogType -> userRepo" shouldNot compile
  }

  it should "store multiple key/value pairs for a given type param" in {
    val entityTypeSet = Set(userType, blogType)

    var localEntityStore = TypeBoundMap[CrmEntity, CrmEntityType, Seq]()
    localEntityStore += (userType -> Seq(CrmUser("user1"), CrmUser("user2"), CrmUser("user3")))
    localEntityStore += (blogType -> Seq(CrmBlog("blog1"), CrmBlog("blog2")))

    var entityTypeToRepoMap = TypeBoundMap[CrmEntity, CrmEntityType, CrmRepo]()
    entityTypeToRepoMap += userType -> userRepo
    entityTypeToRepoMap += blogType -> blogRepo

    // http://scabl.blogspot.com/2015/01/introduce-type-param-pattern.html
    def saveEntities[E <: CrmEntity : TypeKey](entityType: CrmEntityType[E]): Unit = {
      val entities = localEntityStore(entityType)
      val repo = entityTypeToRepoMap(entityType)
      entities.foreach { entity => repo.save(entity) }
    }

    entityTypeSet.foreach { entityType => saveEntities(entityType) }

    userRepo.saveCount should equal (3)
    blogRepo.saveCount should equal (2)
  }

  // TODO pt 86950990:
  // - single-TP key type, double-TP value type
  // - double-TP key type, single-TP value type

  behavior of "TypeBoundMaps in the face of someone trying to break through my bulletpoof type signatures"
  it should "thwart the attack with a compile time error of unspecified opacity" in {
    import testData.pets._
    val dog = Dog("dog")
    val anotherDog = Dog("anotherDog")
    val hound = Hound("hound")
    var petToPetMap = TypeBoundMap[Pet, PetIdentity, PetIdentity]()

    // standard usage is to map dogs to dogs and hounds to hounds
    petToPetMap += dog -> anotherDog
    petToPetMap(dog) should equal (anotherDog)

    // you are not allowed to map a hound to a dog.
    // otherwise you could then call "petToPetMap(hound)" and get a ClassCastException.
    "petToPetMap += hound -> dog" shouldNot compile

    // you are allowed to map a dog to a hound.
    // in this case petToPetMap(dog) return something of type PetIdentity[Dog] (which =:= Dog), and the
    // something returned is hound. nothing weird, no ClassCastException.
    petToPetMap = TypeBoundMap[Pet, PetIdentity, PetIdentity]()
    petToPetMap += dog -> hound
    petToPetMap(dog) should equal (hound)

    var petToPetBoxInvarMap = TypeBoundMap[Pet, PetIdentity, PetBoxInvar]()

    // standard usage is to map dogs to dogs and hounds to hounds
    petToPetBoxInvarMap += dog -> new PetBoxInvar(dog)
    petToPetBoxInvarMap[Pet](dog).p should equal (dog)

    // you are not allowed to map a hound to a PetBoxInvar[Dog].
    // otherwise you could then call "petToPetBoxInvarMap(hound)" and get a ClassCastException.
    "petToPetBoxInvarMap += hound -> new PetBoxInvar(dog)" shouldNot compile

    // you are not allowed to map a dog to a PetBoxInvar[Hound].
    // otherwise you could then call "petToPetBoxInvarMap(dog)" and get a ClassCastException.
    "petToPetBoxInvarMap += dog -> new PetBoxInvar(hound)" shouldNot compile

    var petToPetBoxCovarMap = TypeBoundMap[Pet, PetIdentity, PetBoxCovar]()

    // standard usage is to map dogs to dogs and hounds to hounds
    petToPetBoxCovarMap += dog -> new PetBoxCovar(dog)
    petToPetBoxCovarMap[Pet](dog).p should equal (dog)

    // you are not allowed to map a hound to a PetBoxCovar[Dog].
    // otherwise you could then call "petToPetBoxCovarMap(hound)" and get a ClassCastException.
    "petToPetBoxCovarMap += hound -> new PetBoxCovar(dog)" shouldNot compile

    // you are allowed to map a dog to a PetBoxCovar[Hound].
    // in this case petToPetMap(dog) return something of type PetBoxCovar[Dog], which is >:> PetBoxCovar[Hound]
    petToPetBoxCovarMap = TypeBoundMap[Pet, PetIdentity, PetBoxCovar]()
    petToPetBoxCovarMap += dog -> new PetBoxCovar(hound)
    petToPetBoxCovarMap(dog).p should equal (hound)

    val dogContravar = new PetBoxContravar
    val houndContravar = new PetBoxContravar
    var petToPetBoxContravarMap = TypeBoundMap[Pet, PetIdentity, PetBoxContravar]()

    "petToPetBoxContravarMap += dog -> dogContravar" should compile

    petToPetBoxContravarMap += dog -> dogContravar
    "val contravar: PetBoxContravar[Dog] = petToPetBoxContravarMap(dog)" should compile
    "val contravar: PetBoxContravar[Hound] = petToPetBoxContravarMap(dog)" should compile
    petToPetBoxContravarMap(dog) should be theSameInstanceAs dogContravar

    // you are allowed to map a hound to a PetBoxContravar[Dog].
    // in this case petToPetMap(hound) return something of type PetBoxContravar[Hound],
    // which is >:> PetBoxContravar[Dog]
    petToPetBoxContravarMap = TypeBoundMap[Pet, PetIdentity, PetBoxContravar]()
    petToPetBoxContravarMap += hound -> dogContravar
    "val contravar: PetBoxContravar[Dog] = petToPetBoxContravarMap(hound)" should compile
    "val contravar: PetBoxContravar[Hound] = petToPetBoxContravarMap(hound)" should compile
    petToPetBoxContravarMap(hound) should be theSameInstanceAs dogContravar

    // you are not allowed to map a dog to a PetBoxContravar[Hound].
    // otherwise you could then call "petToPetBoxContravarMap(dog)" and get a ClassCastException.
    "petToPetBoxContravarMap += dog -> new PetBoxContravar[Hound]" shouldNot compile    
  }
 
}
