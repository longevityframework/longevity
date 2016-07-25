package emblem.typeBoundMap

import emblem.typeBound.TypeBoundMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeBoundMap.contains]] */
class ContainsSpec extends FlatSpec with GivenWhenThen with Matchers {

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
 
}
