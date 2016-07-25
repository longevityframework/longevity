package emblem.typeKeyMap

import emblem.TypeKeyMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeKeyMap.contains]] */
class ContainsSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TypeKeyMap.contains"

  it should "return true iff the map contains the given key" in {
    import emblem.testData.computerParts._
    val memoryList = Memory(2) :: Memory(4) :: Memory(8) :: Nil
    val cpuList = CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    val displayList = Display(720) :: Display(1080) :: Nil

    var partLists = TypeKeyMap[ComputerPart, List]()
    partLists.contains[Memory] should be (false)
    partLists.contains[CPU] should be (false)
    partLists.contains[Display] should be (false)

    partLists += memoryList
    partLists.contains[Memory] should be (true)
    partLists.contains[CPU] should be (false)
    partLists.contains[Display] should be (false)

    partLists += cpuList
    partLists.contains[Memory] should be (true)
    partLists.contains[CPU] should be (true)
    partLists.contains[Display] should be (false)

    partLists += displayList
    partLists.contains[Memory] should be (true)
    partLists.contains[CPU] should be (true)
    partLists.contains[Display] should be (true)
  }  
 
}
