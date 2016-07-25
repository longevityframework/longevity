package emblem.typeKeyMap

import emblem.TypeKeyMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeKeyMap.values]] */
class ValuesSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TypeKeyMap.values"

  it should "return the values of this map as an iterable" in {
    import emblem.testData.computerParts._
    var partLists = TypeKeyMap[ComputerPart, List]()
    partLists.values.toSet should be (Set())
    val memoryList = Memory(2) :: Memory(4) :: Memory(8) :: Nil
    partLists += memoryList
    partLists.values.toSet should be (Set(memoryList))
    val cpuList = CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    partLists += cpuList
    partLists.values.toSet should be (Set(memoryList, cpuList))
    val displayList = Display(720) :: Display(1080) :: Nil
    partLists += displayList
    partLists.values.toSet should be (Set(memoryList, cpuList, displayList))
  }  
 
}
