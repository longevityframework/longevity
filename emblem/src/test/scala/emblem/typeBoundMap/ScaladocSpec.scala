package emblem.typeBoundMap

import emblem.typeBound.TypeBoundMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** testing the correctness of the scaladoc examples for [[TypeBoundMap]] */
class ScaladocSpec extends FlatSpec with GivenWhenThen with Matchers {

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
 
}
