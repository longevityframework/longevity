package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** specifications for methods common to [[TypeKeyMap]] and [[TypeBoundMap]] found in [[BaseTypeBoundMap]]. */
class BaseTypeBoundMapSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TypeKeyMap.isEmpty"
  it should "produce true iff the map is empty" in {
    import emblem.testData.computerParts._
    var partLists = TypeKeyMap[ComputerPart, List]()
    partLists.isEmpty should be (true)
    partLists += Memory(2) :: Memory(4) :: Memory(8) :: Nil
    partLists.isEmpty should be (false)
    partLists += CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    partLists.isEmpty should be (false)
    partLists += Display(720) :: Display(1080) :: Nil
    partLists.isEmpty should be (false)
  }  

  behavior of "TypeBoundMap.isEmpty"
  it should "produce true iff the map is empty" in {
    import emblem.testData.pets._
    val catStore1 = new PetStore[Cat]
    val catStore2 = new PetStore[Cat]
    val dogStore1 = new PetStore[Dog]

    var inventories = TypeBoundMap[Pet, PetStore, List]
    inventories.isEmpty should be (true)
    inventories += (catStore1, Cat("cat11") :: Cat("cat12") :: Cat("cat13") :: Nil)
    inventories.isEmpty should be (false)
    inventories += (catStore2, Cat("cat21") :: Nil)
    inventories.isEmpty should be (false)
    inventories += (dogStore1, Dog("dog11") :: Dog("dog12") :: Nil)
    inventories.isEmpty should be (false)
  }  

  behavior of "TypeKeyMap.keys"
  it should "return the keys of this map as an iterable" in {
    import emblem.testData.computerParts._

    var partLists = TypeKeyMap[ComputerPart, List]()
    var keys: Iterable[TypeKey[_ <: ComputerPart]] = partLists.keys
    keys.toSet should equal (Set())

    partLists += Memory(2) :: Memory(4) :: Memory(8) :: Nil
    keys = partLists.keys
    keys.toSet should equal (Set(typeKey[Memory]))

    partLists += CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    keys = partLists.keys
    keys.toSet should equal (Set(typeKey[Memory], typeKey[CPU]))

    partLists += Display(720) :: Display(1080) :: Nil
    keys = partLists.keys
    keys.toSet should equal (Set(typeKey[Memory], typeKey[CPU], typeKey[Display]))
  }  

  behavior of "TypeBoundMap.keys"
  it should "return the keys of this map as an iterable" in {
    import emblem.testData.pets._
    val catStore1 = new PetStore[Cat]
    val catStore2 = new PetStore[Cat]
    val dogStore1 = new PetStore[Dog]

    var inventories = TypeBoundMap[Pet, PetStore, List]
    val keys: Iterable[PetStore[_ <: Pet]] = inventories.keys
    keys.toSet should equal (Set())

    inventories += (catStore1, Cat("cat11") :: Cat("cat12") :: Cat("cat13") :: Nil)
    inventories.keys.toSet should equal (Set(catStore1))

    inventories += (catStore2, Cat("cat21") :: Nil)
    inventories.keys.toSet should equal (Set(catStore1, catStore2))

    inventories += (dogStore1, Dog("dog11") :: Dog("dog12") :: Nil)
    inventories.keys.toSet should equal (Set(catStore1, catStore2, dogStore1))
  }

  behavior of "TypeKeyMap.size"
  it should "return the number of typekey/value bindings in the map" in {
    import emblem.testData.computerParts._
    var partLists = TypeKeyMap[ComputerPart, List]()
    partLists.size should be (0)
    partLists += Memory(2) :: Memory(4) :: Memory(8) :: Nil
    partLists.size should be (1)
    partLists += CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    partLists.size should be (2)
    partLists += Display(720) :: Display(1080) :: Nil
    partLists.size should be (3)
  }  

  behavior of "TypeBoundMap.size"
  it should "return the number of key/value bindings in the map" in {
    import emblem.testData.pets._
    val catStore1 = new PetStore[Cat]
    val catStore2 = new PetStore[Cat]
    val dogStore1 = new PetStore[Dog]

    var inventories = TypeBoundMap[Pet, PetStore, List]
    inventories.size should be (0)
    inventories += (catStore1, Cat("cat11") :: Cat("cat12") :: Cat("cat13") :: Nil)
    inventories.size should be (1)
    inventories += (catStore2, Cat("cat21") :: Nil)
    inventories.size should be (2)
    inventories += (dogStore1, Dog("dog11") :: Dog("dog12") :: Nil)
    inventories.size should be (3)
  }
 
}