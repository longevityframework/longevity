package emblem.typeBoundMap

import emblem.typeBound.TypeBoundMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeBoundMap.size]] */
class SizeSpec extends FlatSpec with GivenWhenThen with Matchers {

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
 
}
