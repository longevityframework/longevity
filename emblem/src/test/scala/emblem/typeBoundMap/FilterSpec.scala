package emblem.typeBoundMap

import emblem.typeBound.TypeBoundPair
import emblem.typeBound.TypeBoundMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeBoundMap.filter]], [[TypeBoundMap.filterKeys]],
 * [[TypeBoundMap.filterNot]], and [[TypeBoundMap.filterValues]].
 */
class FilterSpec extends FlatSpec with GivenWhenThen with Matchers {

  import emblem.testData.pets._
  private val catStore1 = new PetStore[Cat]
  private val catStore2 = new PetStore[Cat]
  private val dogStore1 = new PetStore[Dog]

  private var inventories = TypeBoundMap[Pet, PetStore, List]
  inventories += (catStore1 -> List(Cat("cat11"), Cat("cat12"), Cat("cat13")))
  inventories += (catStore2 -> List(Cat("cat21"), Cat("cat22")))
  inventories += (dogStore1 -> List(Dog("dog11"), Dog("dog12")))

  private var catsOnly = TypeBoundMap[Pet, PetStore, List]
  catsOnly += (catStore1 -> List(Cat("cat11"), Cat("cat12"), Cat("cat13")))
  catsOnly += (catStore2 -> List(Cat("cat21"), Cat("cat22")))

  private var noCats = TypeBoundMap[Pet, PetStore, List]
  noCats += (dogStore1 -> List(Dog("dog11"), Dog("dog12")))

  private var size2Only = TypeBoundMap[Pet, PetStore, List]
  size2Only += (catStore2 -> List(Cat("cat21"), Cat("cat22")))
  size2Only += (dogStore1 -> List(Dog("dog11"), Dog("dog12")))

  private var notSize2 = TypeBoundMap[Pet, PetStore, List]
  notSize2 += (catStore1 -> List(Cat("cat11"), Cat("cat12"), Cat("cat13")))

  private var noStores = TypeBoundMap[Pet, PetStore, List]

  behavior of "TypeBoundMap.filter"

  it should "return a copy of the map filtered by the predicate" in {
    val passAll = (pair: TypeBoundPair[Pet, PetStore, List, _ <: Pet]) => true
    inventories.filter(passAll) should equal (inventories)

    val failAll = (pair: TypeBoundPair[Pet, PetStore, List, _ <: Pet]) => false
    inventories.filter(failAll) should equal (noStores)

    val hasCatStore =
      (pair: TypeBoundPair[Pet, PetStore, List, _ <: Pet]) =>
      Set[PetStore[_ <: Pet]](catStore1, catStore2).contains(pair._1)
    inventories.filter(hasCatStore) should equal (catsOnly)

    val size2 = (pair: TypeBoundPair[Pet, PetStore, List, _ <: Pet]) => pair._2.size == 2
    inventories.filter(size2) should equal (size2Only)
  }  

  behavior of "TypeBoundMap.filterNot"

  it should "return a copy of the map filtered by the predicate" in {
    val passAll = (pair: TypeBoundPair[Pet, PetStore, List, _ <: Pet]) => true
    inventories.filterNot(passAll) should equal (noStores)

    val failAll = (pair: TypeBoundPair[Pet, PetStore, List, _ <: Pet]) => false
    inventories.filterNot(failAll) should equal (inventories)

    val hasCatStore =
      (pair: TypeBoundPair[Pet, PetStore, List, _ <: Pet]) =>
      Set[PetStore[_ <: Pet]](catStore1, catStore2).contains(pair._1)
    inventories.filterNot(hasCatStore) should equal (noCats)

    val size2 = (pair: TypeBoundPair[Pet, PetStore, List, _ <: Pet]) => pair._2.size == 2
    inventories.filterNot(size2) should equal (notSize2)
  }

  behavior of "TypeBoundMap.filterKeys"

  it should "return a copy of the map filtered by the predicate" in {
    inventories.filterKeys((key) => true) should equal (inventories)
    inventories.filterKeys((key) => false) should equal (noStores)

    { inventories.filterKeys((key) => Set[PetStore[_ <: Pet]](catStore1, catStore2).contains(key))
    } should equal (catsOnly)
  }

  behavior of "TypeBoundMap.filterValues"

  it should "return a copy of the map filtered by the predicate" in {
    inventories.filterValues((list) => true) should equal (inventories)
    inventories.filterValues((list) => false) should equal (noStores)
    inventories.filterValues(_.size == 2) should equal (size2Only)
  }

}
