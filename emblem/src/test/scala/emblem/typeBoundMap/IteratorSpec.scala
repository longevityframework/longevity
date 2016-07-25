package emblem.typeBoundMap

import emblem.typeBound.TypeBoundMap
import emblem.typeBound.TypeBoundPair
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeBoundMap.iterator]] */
class IteratorSpec extends FlatSpec with GivenWhenThen with Matchers {

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
 
}
