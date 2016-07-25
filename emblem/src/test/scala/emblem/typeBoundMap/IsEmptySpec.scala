package emblem.typeBoundMap

import emblem.typeBound.TypeBoundMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeBoundMap.isEmpty]] */
class IsEmptySpec extends FlatSpec with GivenWhenThen with Matchers {

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
 
}
