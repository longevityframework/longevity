package longevity.unit.model.annotations

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class PackscanToListSpec extends FlatSpec with Matchers {

  behavior of "longevity.model.annotations.packscanToList[A]"

  it should "gather all the ATypes in the same package into a List" in {
    import packscanExample._
    aTypes.size should equal (3)
    aTypes should contain (A1)
    aTypes should contain (A2)
    aTypes should contain (subpackage.A3)
  }

}
