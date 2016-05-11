package emblem.typeBound

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

// TODO pt-92300898 reorg specs for TypeKeyMap and TypeBoundMap

/** specifications for methods common to [[TypeKeyMap]] and [[TypeBoundMap]] found in [[BaseTypeBoundMap]]. */
class BaseTypeBoundMapSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TypeKeyMap.contains"
  it should "return true iff the map contains the given key" in {
    import emblem.testData.computerParts._
    val memoryList = Memory(2) :: Memory(4) :: Memory(8) :: Nil
    val cpuList = CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    val displayList = Display(720) :: Display(1080) :: Nil

    var partLists = TypeKeyMap[ComputerPart, List]()
    partLists.contains[Memory] should be (false)
    partLists.contains[CPU] should be (false)
    partLists.contains[Display] should be (false)

    partLists += memoryList
    partLists.contains[Memory] should be (true)
    partLists.contains[CPU] should be (false)
    partLists.contains[Display] should be (false)

    partLists += cpuList
    partLists.contains[Memory] should be (true)
    partLists.contains[CPU] should be (true)
    partLists.contains[Display] should be (false)

    partLists += displayList
    partLists.contains[Memory] should be (true)
    partLists.contains[CPU] should be (true)
    partLists.contains[Display] should be (true)
  }  

  behavior of "TypeBoundMap.contains"
  it should "return true iff the map contains the given key" in {
    import emblem.testData.pets._
    val catStore1 = new PetStore[Cat]
    val catStore2 = new PetStore[Cat]
    val dogStore1 = new PetStore[Dog]

    var inventories = TypeBoundMap[Pet, PetStore, List]
    inventories.contains(catStore1) should equal (false)
    inventories.contains(catStore2) should equal (false)
    inventories.contains(dogStore1) should equal (false)

    inventories += (catStore1 -> List(Cat("cat11"), Cat("cat12"), Cat("cat13")))
    inventories.contains(catStore1) should equal (true)
    inventories.contains(catStore2) should equal (false)
    inventories.contains(dogStore1) should equal (false)

    inventories += (catStore2 -> List(Cat("cat21"))) 
    inventories.contains(catStore1) should equal (true)
    inventories.contains(catStore2) should equal (true)
    inventories.contains(dogStore1) should equal (false)

    inventories += (dogStore1 -> List(Dog("dog11"), Dog("dog12")))
    inventories.contains(catStore1) should equal (true)
    inventories.contains(catStore2) should equal (true)
    inventories.contains(dogStore1) should equal (true)
  }

  behavior of "TypeKeyMap.foreach"
  it should "iterator over all the TypeBoundPairs in the map" in {
    import emblem.testData.computerParts._
    val memoryList = Memory(2) :: Memory(4) :: Memory(8) :: Nil
    val cpuList = CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    val displayList = Display(720) :: Display(1080) :: Nil

    var range = Set[TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]]()

    var partLists = TypeKeyMap[ComputerPart, List]()
    partLists.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Memory](typeKey[Memory], memoryList))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, CPU](typeKey[CPU], cpuList))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Display](typeKey[Display], displayList))
    } should be {
      false
    }

    range = Set[TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]]()
    partLists += memoryList
    partLists.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Memory](typeKey[Memory], memoryList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, CPU](typeKey[CPU], cpuList))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Display](typeKey[Display], displayList))
    } should be {
      false
    }

    range = Set[TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]]()
    partLists += cpuList
    partLists.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Memory](typeKey[Memory], memoryList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, CPU](typeKey[CPU], cpuList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Display](typeKey[Display], displayList))
    } should be {
      false
    }

    range = Set[TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]]()
    partLists += displayList
    partLists.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Memory](typeKey[Memory], memoryList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, CPU](typeKey[CPU], cpuList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Display](typeKey[Display], displayList))
    } should be {
      true
    }
  }  

  behavior of "TypeBoundMap.foreach"
  it should "return true iff the map contains the given key" in {
    import emblem.testData.pets._
    val catStore1 = new PetStore[Cat]
    val catStore2 = new PetStore[Cat]
    val dogStore1 = new PetStore[Dog]

    val catList1 = List(Cat("cat11"), Cat("cat12"), Cat("cat13"))
    val catList2 = List(Cat("cat21"))
    val dogList1 = List(Dog("dog11"), Dog("dog12"))

    var range = Set[TypeBoundPair[Pet, PetStore, List, _ <: Pet]]()
    var inventories = TypeBoundMap[Pet, PetStore, List]

    inventories.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore1, catList1))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore2, catList2))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Dog](dogStore1, dogList1))
    } should be {
      false
    }

    inventories += (catStore1 -> catList1)
    range = Set[TypeBoundPair[Pet, PetStore, List, _ <: Pet]]()
    inventories.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore1, catList1))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore2, catList2))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Dog](dogStore1, dogList1))
    } should be {
      false
    }

    inventories += (catStore2 -> catList2)
    range = Set[TypeBoundPair[Pet, PetStore, List, _ <: Pet]]()
    inventories.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore1, catList1))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore2, catList2))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Dog](dogStore1, dogList1))
    } should be {
      false
    }

    inventories += (dogStore1 -> dogList1)
    range = Set[TypeBoundPair[Pet, PetStore, List, _ <: Pet]]()
    inventories.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore1, catList1))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore2, catList2))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Dog](dogStore1, dogList1))
    } should be {
      true
    }
  }

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
    inventories += (catStore1 -> List(Cat("cat11"), Cat("cat12"), Cat("cat13")))
    inventories.isEmpty should be (false)
    inventories += (catStore2 -> List(Cat("cat21")))
    inventories.isEmpty should be (false)
    inventories += (dogStore1 -> List(Dog("dog11"), Dog("dog12")))
    inventories.isEmpty should be (false)
  }  

  behavior of "TypeKeyMap.iterator"
  it should "iterator over all the TypeBoundPairs in the map" in {
    import emblem.testData.computerParts._
    val memoryList = Memory(2) :: Memory(4) :: Memory(8) :: Nil
    val cpuList = CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    val displayList = Display(720) :: Display(1080) :: Nil

    var range = Set[TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]]()

    var partLists = TypeKeyMap[ComputerPart, List]()
    partLists.iterator.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Memory](typeKey[Memory], memoryList))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, CPU](typeKey[CPU], cpuList))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Display](typeKey[Display], displayList))
    } should be {
      false
    }

    range = Set[TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]]()
    partLists += memoryList
    partLists.iterator.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Memory](typeKey[Memory], memoryList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, CPU](typeKey[CPU], cpuList))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Display](typeKey[Display], displayList))
    } should be {
      false
    }

    range = Set[TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]]()
    partLists += cpuList
    partLists.iterator.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Memory](typeKey[Memory], memoryList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, CPU](typeKey[CPU], cpuList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Display](typeKey[Display], displayList))
    } should be {
      false
    }

    range = Set[TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]]()
    partLists += displayList
    partLists.iterator.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Memory](typeKey[Memory], memoryList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, CPU](typeKey[CPU], cpuList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Display](typeKey[Display], displayList))
    } should be {
      true
    }
  }  

  behavior of "TypeBoundMap.iterator"
  it should "return true iff the map contains the given key" in {
    import emblem.testData.pets._
    val catStore1 = new PetStore[Cat]
    val catStore2 = new PetStore[Cat]
    val dogStore1 = new PetStore[Dog]

    val catList1 = List(Cat("cat11"), Cat("cat12"), Cat("cat13"))
    val catList2 = List(Cat("cat21"))
    val dogList1 = List(Dog("dog11"), Dog("dog12"))

    var range = Set[TypeBoundPair[Pet, PetStore, List, _ <: Pet]]()
    var inventories = TypeBoundMap[Pet, PetStore, List]

    inventories.iterator.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore1, catList1))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore2, catList2))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Dog](dogStore1, dogList1))
    } should be {
      false
    }

    inventories += (catStore1 -> catList1)
    range = Set[TypeBoundPair[Pet, PetStore, List, _ <: Pet]]()
    inventories.iterator.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore1, catList1))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore2, catList2))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Dog](dogStore1, dogList1))
    } should be {
      false
    }

    inventories += (catStore2 -> catList2)
    range = Set[TypeBoundPair[Pet, PetStore, List, _ <: Pet]]()
    inventories.iterator.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore1, catList1))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore2, catList2))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Dog](dogStore1, dogList1))
    } should be {
      false
    }

    inventories += (dogStore1 -> dogList1)
    range = Set[TypeBoundPair[Pet, PetStore, List, _ <: Pet]]()
    inventories.iterator.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore1, catList1))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Cat](catStore2, catList2))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[Pet, PetStore, List, Dog](dogStore1, dogList1))
    } should be {
      true
    }
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

    inventories += (catStore1 -> List(Cat("cat11"), Cat("cat12"), Cat("cat13")))
    inventories.keys.toSet should equal (Set(catStore1))

    inventories += (catStore2 -> List(Cat("cat21")))
    inventories.keys.toSet should equal (Set(catStore1, catStore2))

    inventories += (dogStore1 -> List(Dog("dog11"), Dog("dog12")))
    inventories.keys.toSet should equal (Set(catStore1, catStore2, dogStore1))
  }

  behavior of "TypeKeyMap.mapValues"
  it should "return the TypeKeyMap obtained by applying the TypeBoundFunction to each of the values" in {
    import emblem.testData.computerParts._
    val toHeadOption = new TypeBoundFunction[ComputerPart, List, Option] {
      def apply[P <: ComputerPart](parts: List[P]): Option[P] = parts.headOption
    }
    var partLists = TypeKeyMap[ComputerPart, List]()

    // make sure the the method returns the right type
    val partOptions: TypeKeyMap[ComputerPart, Option] = partLists.mapValues[Option](toHeadOption)

    partLists.mapValues[Option](toHeadOption) should be (TypeKeyMap[ComputerPart, Option]())
    val memoryList = Memory(2) :: Memory(4) :: Memory(8) :: Nil

    partLists += memoryList
    partLists.mapValues[Option](toHeadOption) should be (
      TypeKeyMap[ComputerPart, Option]() + memoryList.headOption)

    val cpuList = CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    partLists += cpuList
    partLists.mapValues[Option](toHeadOption) should be (
      TypeKeyMap[ComputerPart, Option]() + memoryList.headOption + cpuList.headOption)

    val displayList = Display(720) :: Display(1080) :: Nil
    partLists += displayList
    partLists.mapValues[Option](toHeadOption) should be (
      TypeKeyMap[ComputerPart, Option]() + memoryList.headOption + cpuList.headOption + displayList.headOption)
  }  

  // a good example of the utility of mapValuesWiden is in longevity.subdomain.Subdomain
  behavior of "TypeKeyMap.mapValuesWiden"
  it should "return a TypeKeyMap with a wider type bound than the original" in {
    import emblem.testData.pets._
    val toSponsor = new WideningTypeBoundFunction[Dog, Pet, DogKennel, PetStore] {
      def apply[D <: Dog](kennel: DogKennel[D]): PetStore[D] = kennel.sponsor
    }

    var kennels = TypeKeyMap[Dog, DogKennel]

    // make sure the the method returns the right type
    val stores: TypeKeyMap[Pet, PetStore] = kennels.mapValuesWiden[Pet, PetStore](toSponsor)

    kennels.mapValuesWiden[Pet, PetStore](toSponsor) should be (TypeKeyMap[Pet, PetStore])

    val houndKennel = new DogKennel[Hound](new PetStore[Hound])
    kennels += houndKennel
    kennels.mapValuesWiden[Pet, PetStore](toSponsor) should be (
      TypeKeyMap[Pet, PetStore] + houndKennel.sponsor)

    val shepherdKennel = new DogKennel[Shepherd](new PetStore[Shepherd])
    kennels += shepherdKennel
    kennels.mapValuesWiden[Pet, PetStore](toSponsor) should be (
      TypeKeyMap[Pet, PetStore] + houndKennel.sponsor + shepherdKennel.sponsor)

    val germanShepherdKennel = new DogKennel[GermanShepherd](new PetStore[GermanShepherd])
    kennels += germanShepherdKennel
    kennels.mapValuesWiden[Pet, PetStore](toSponsor) should be (
      TypeKeyMap[Pet, PetStore] + houndKennel.sponsor + shepherdKennel.sponsor + germanShepherdKennel.sponsor)
  }  

  behavior of "TypeBoundMap.mapValues"
  it should "return the TypeKeyMap obtained by applying the TypeBoundFunction to each of the values" in {
    import emblem.testData.pets._
    val catStore1 = new PetStore[Cat]
    val catStore2 = new PetStore[Cat]
    val dogStore1 = new PetStore[Dog]

    val toHeadOption = new TypeBoundFunction[Pet, List, Option] {
      def apply[P <: Pet](pets: List[P]): Option[P] = pets.headOption
    }

    var inventories = TypeBoundMap[Pet, PetStore, List]
    inventories.mapValues[Option](toHeadOption) should equal (TypeBoundMap[Pet, PetStore, Option])

    val catList1 = Cat("cat11") :: Cat("cat12") :: Cat("cat13") :: Nil
    inventories += (catStore1 -> catList1)
    inventories.mapValues[Option](toHeadOption) should equal (
      TypeBoundMap[Pet, PetStore, Option] + (catStore1 -> catList1.headOption))

    val catList2 = Cat("cat21") :: Nil
    inventories += (catStore2 -> catList2)
    inventories.mapValues[Option](toHeadOption) should equal (
      TypeBoundMap[Pet, PetStore, Option] +
      (catStore1 -> catList1.headOption) +
      (catStore2 -> catList2.headOption))

    val dogList1 = Dog("dog11") :: Dog("dog12") :: Nil
    inventories += (dogStore1 -> dogList1)
    inventories.mapValues[Option](toHeadOption) should equal (
      TypeBoundMap[Pet, PetStore, Option] +
      (catStore1 -> catList1.headOption) +
      (catStore2 -> catList2.headOption) +
      (dogStore1 -> dogList1.headOption))
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
    inventories += (catStore1 -> List(Cat("cat11"), Cat("cat12"), Cat("cat13")))
    inventories.size should be (1)
    inventories += (catStore2 -> List(Cat("cat21")))
    inventories.size should be (2)
    inventories += (dogStore1 -> List(Dog("dog11"), Dog("dog12")))
    inventories.size should be (3)
  }

  behavior of "TypeKeyMap.values"
  it should "return the values of this map as an iterable" in {
    import emblem.testData.computerParts._
    var partLists = TypeKeyMap[ComputerPart, List]()
    partLists.values.toSet should be (Set())
    val memoryList = Memory(2) :: Memory(4) :: Memory(8) :: Nil
    partLists += memoryList
    partLists.values.toSet should be (Set(memoryList))
    val cpuList = CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    partLists += cpuList
    partLists.values.toSet should be (Set(memoryList, cpuList))
    val displayList = Display(720) :: Display(1080) :: Nil
    partLists += displayList
    partLists.values.toSet should be (Set(memoryList, cpuList, displayList))
  }  

  behavior of "TypeBoundMap.values"
  it should "return the values of this map as an iterable" in {
    import emblem.testData.pets._
    val catStore1 = new PetStore[Cat]
    val catStore2 = new PetStore[Cat]
    val dogStore1 = new PetStore[Dog]

    var inventories = TypeBoundMap[Pet, PetStore, List]
    val values: Iterable[List[_ <: Pet]] = inventories.values
    values.toSet should equal (Set())

    val catList1 = Cat("cat11") :: Cat("cat12") :: Cat("cat13") :: Nil
    inventories += (catStore1 -> catList1)
    inventories.values.toSet should equal (Set(catList1))

    val catList2 = Cat("cat21") :: Nil
    inventories += (catStore2 -> catList2)
    inventories.values.toSet should equal (Set(catList1, catList2))

    val dogList1 = Dog("dog11") :: Dog("dog12") :: Nil
    inventories += (dogStore1 -> dogList1)
    inventories.values.toSet should equal (Set(catList1, catList2, dogList1))
  }
 
}
