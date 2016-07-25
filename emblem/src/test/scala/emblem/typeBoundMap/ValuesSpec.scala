package emblem.typeBoundMap

import emblem.typeBound.TypeBoundMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeBoundMap.values]] */
class ValuesSpec extends FlatSpec with GivenWhenThen with Matchers {

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
