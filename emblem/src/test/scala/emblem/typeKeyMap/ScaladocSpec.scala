package emblem.typeKeyMap

import emblem.TypeKeyMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** testing the correctness of the scaladoc examples for [[TypeKeyMap]] */
class ScaladocSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "the example code in the scaladocs for TypeKeyMap"

  it should "compile and produce the expected values" in {

    import emblem.testData.computerParts._

    var partLists = TypeKeyMap[ComputerPart, List]()
    partLists += Memory(2) :: Memory(4) :: Memory(8) :: Nil
    partLists += CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    partLists += Display(720) :: Display(1080) :: Nil

    val memories: List[Memory] = partLists[Memory]
    memories.size should be (3)
    val cpus: List[CPU] = partLists[CPU]
    cpus.size should be (3)
    val displays: List[Display] = partLists[Display]
    displays.size should be (2)

    val cpu: CPU = partLists[CPU].head
    cpu should equal (CPU(2.2))
    val display: Display = partLists[Display].tail.head
    display should equal (Display(1080))
  }

}
