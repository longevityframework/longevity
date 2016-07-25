package emblem.typeBoundMap

import emblem.typeBound.TypeBoundMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeBoundMap.keys]] */
class KeysSpec extends FlatSpec with GivenWhenThen with Matchers {

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
 
}
