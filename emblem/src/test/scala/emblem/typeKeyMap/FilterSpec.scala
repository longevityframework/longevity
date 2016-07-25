package emblem.typeKeyMap

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.typeBound.TypeBoundPair
import emblem.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeKeyMap.filter]], [[TypeKeyMap.filterKeys]],
 * [[TypeKeyMap.filterNot]], [[TypeKeyMap.filterTypeBound]], and
 * [[TypeKeyMap.filterValues]].
 */
class FilterSpec extends FlatSpec with GivenWhenThen with Matchers {

  import emblem.testData.computerParts._

  private var partLists = TypeKeyMap[ComputerPart, List]()
  partLists += Memory(2) :: Memory(4) :: Memory(8) :: Nil
  partLists += CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
  partLists += Display(720) :: Display(1080) :: Nil

  private var cpuMemoryOnly = TypeKeyMap[ComputerPart, List]()
  cpuMemoryOnly += Memory(2) :: Memory(4) :: Memory(8) :: Nil
  cpuMemoryOnly += CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil

  private var displayOnly = TypeKeyMap[ComputerPart, List]()
  displayOnly += Display(720) :: Display(1080) :: Nil

  private var noParts = TypeKeyMap[ComputerPart, List]()

  behavior of "TypeKeyMap.filter"

  it should "return a copy of the map filtered by the predicate" in {
    val passAll = (pair: TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]) => true
    partLists.filter(passAll) should equal (partLists)

    val failAll = (pair: TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]) => false
    partLists.filter(failAll) should equal (noParts)

    val displayKey =
      (pair: TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]) => pair._1 == typeKey[Display]
    partLists.filter(displayKey) should equal (displayOnly)

    val size3 = (pair: TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]) => pair._2.size == 3
    partLists.filter(size3) should equal (cpuMemoryOnly)
  }  

  behavior of "TypeKeyMap.filterNot"

  it should "return a copy of the map filtered by the predicate" in {
    val passAll = (pair: TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]) => true
    partLists.filterNot(passAll) should equal (noParts)

    val failAll = (pair: TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]) => false
    partLists.filterNot(failAll) should equal (partLists)

    val displayKey =
      (pair: TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]) => pair._1 == typeKey[Display]
    partLists.filterNot(displayKey) should equal (cpuMemoryOnly)

    val size3 = (pair: TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]) => pair._2.size == 3
    partLists.filterNot(size3) should equal (displayOnly)
  }  


  behavior of "TypeKeyMap.filterKeys"

  it should "return a copy of the map filtered by the predicate" in {
    partLists.filterKeys((key) => true) should equal (partLists)
    partLists.filterKeys((key) => false) should equal (noParts)
    partLists.filterKeys((key) => key == typeKey[ComputerPart]) should equal (noParts)
    partLists.filterKeys((key) => key == typeKey[Display]) should equal (displayOnly)
  }

  behavior of "TypeKeyMap.filterValues"

  it should "return a copy of the map filtered by the predicate" in {
    partLists.filterValues((list) => true) should equal (partLists)
    partLists.filterValues((list) => false) should equal (noParts)
    partLists.filterValues(_.size == 3) should equal (cpuMemoryOnly)
  }

  behavior of "TypeKeyMap.filterTypeBound"

  it should "return a copy of the map filtered by the predicate" in {
    partLists.filterTypeBound[Display] should equal (displayOnly)
  }

}
