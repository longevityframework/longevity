package emblem.typeKeyMap

import emblem.TypeKeyMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeKeyMap.size]] */
class SizeSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TypeKeyMap.size"

  it should "return the number of typekey/value bindings in the map" in {
    import emblem.testData.computerParts._
    var partLists = TypeKeyMap[ComputerPart, List]()
    partLists.size should be (0)
    partLists += Memory(2) :: Memory(4) :: Memory(8) :: Nil
    partLists.size should be (1)
    partLists += CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    partLists.size should be (2)
    partLists += Display(720) :: Display(1080) :: Nil
    partLists.size should be (3)
  }  

}
