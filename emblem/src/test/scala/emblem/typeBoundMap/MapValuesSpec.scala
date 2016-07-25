package emblem.typeBoundMap

import emblem.typeBound.TypeBoundFunction
import emblem.typeBound.TypeBoundMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeBoundMap.mapValues]] */
class MapValuesSpec extends FlatSpec with GivenWhenThen with Matchers {

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
 
}
