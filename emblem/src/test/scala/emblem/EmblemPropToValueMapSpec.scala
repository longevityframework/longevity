package emblem

import org.scalatest._
import org.scalatest.OptionValues._
import emblem.testData._

/** [[EmblemPropToValueMap]] specifications */
class EmblemPropToValueMapSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "the EmblemPropToValueMap constructor"
  it should "return an empty map" in {
    val map = EmblemPropToValueMap[Point]()
    map.size should equal (0)
    map.isEmpty should be (true)
    intercept[EmblemPropToValueMap.NoValueForPropName] {
      map.get(pointEmblem("x"))
    }
    intercept[EmblemPropToValueMap.NoValueForPropName] {
      map.get(pointEmblem("y"))
    }
  }

  behavior of "EmblemPropToValueMap.+"
  it should "return a map with the specified mapping added" in {
    val map = EmblemPropToValueMap[Point]() + (pointEmblem.prop[Double]("x") -> 7.0)
    map.size should equal (1)
    map.isEmpty should be (false)
    map.get(pointEmblem("x")) should equal (7.0)
    intercept[EmblemPropToValueMap.NoValueForPropName] {
      map.get(pointEmblem("y"))
    }
  }

}
