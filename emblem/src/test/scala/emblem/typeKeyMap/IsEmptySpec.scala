package emblem.typeKeyMap

import emblem.TypeKeyMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeKeyMap.isEmpty]] */
class IsEmptySpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TypeKeyMap.isEmpty"

  it should "produce true iff the map is empty" in {
    import emblem.testData.computerParts._
    var partLists = TypeKeyMap[ComputerPart, List]()
    partLists.isEmpty should be (true)
    partLists += Memory(2) :: Memory(4) :: Memory(8) :: Nil
    partLists.isEmpty should be (false)
    partLists += CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    partLists.isEmpty should be (false)
    partLists += Display(720) :: Display(1080) :: Nil
    partLists.isEmpty should be (false)
  }  
 
}
