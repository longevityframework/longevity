package emblem.typeKeyMap

import emblem.TypeKeyMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeKeyMap.++]] */
class UnionSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TypeKeyMap.++"

  it should "produce the union of the two type key maps" in {
    import emblem.testData.computerParts._

    val memoryList = Memory(2) :: Memory(4) :: Memory(8) :: Nil
    val cpuList = CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    val displayList = Display(720) :: Display(1080) :: Nil

    val emptyPartLists = TypeKeyMap[ComputerPart, List]()
    val partListsM = emptyPartLists + memoryList
    val partListsC = emptyPartLists + cpuList
    val partListsD = emptyPartLists + displayList
    val partListsMC = partListsM + cpuList
    val partListsMD = partListsM + displayList
    val partListsCD = partListsC + displayList
    val partListsMCD = partListsMC + displayList

    (emptyPartLists ++ emptyPartLists) should equal (emptyPartLists)
    (emptyPartLists ++ partListsM) should equal (partListsM)
    (emptyPartLists ++ partListsC) should equal (partListsC)
    (emptyPartLists ++ partListsD) should equal (partListsD)
    (emptyPartLists ++ partListsMC) should equal (partListsMC)
    (emptyPartLists ++ partListsMD) should equal (partListsMD)
    (emptyPartLists ++ partListsCD) should equal (partListsCD)
    (emptyPartLists ++ partListsMCD) should equal (partListsMCD)

    (partListsM ++ emptyPartLists) should equal (partListsM)
    (partListsM ++ partListsM) should equal (partListsM)
    (partListsM ++ partListsC) should equal (partListsMC)
    (partListsM ++ partListsD) should equal (partListsMD)
    (partListsM ++ partListsMC) should equal (partListsMC)
    (partListsM ++ partListsMD) should equal (partListsMD)
    (partListsM ++ partListsCD) should equal (partListsMCD)
    (partListsM ++ partListsMCD) should equal (partListsMCD)

    (partListsMC ++ emptyPartLists) should equal (partListsMC)
    (partListsMC ++ partListsM) should equal (partListsMC)
    (partListsMC ++ partListsC) should equal (partListsMC)
    (partListsMC ++ partListsD) should equal (partListsMCD)
    (partListsMC ++ partListsMC) should equal (partListsMC)
    (partListsMC ++ partListsMD) should equal (partListsMCD)
    (partListsMC ++ partListsCD) should equal (partListsMCD)
    (partListsMC ++ partListsMCD) should equal (partListsMCD)

    (partListsMCD ++ emptyPartLists) should equal (partListsMCD)
    (partListsMCD ++ partListsM) should equal (partListsMCD)
    (partListsMCD ++ partListsC) should equal (partListsMCD)
    (partListsMCD ++ partListsD) should equal (partListsMCD)
    (partListsMCD ++ partListsMC) should equal (partListsMCD)
    (partListsMCD ++ partListsMD) should equal (partListsMCD)
    (partListsMCD ++ partListsCD) should equal (partListsMCD)
    (partListsMCD ++ partListsMCD) should equal (partListsMCD)
  }

}
