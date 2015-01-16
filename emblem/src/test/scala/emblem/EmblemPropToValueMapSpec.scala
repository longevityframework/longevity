package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[EmblemPropToValueMap]] specifications */
class EmblemPropToValueMapSpec extends FlatSpec with GivenWhenThen with Matchers {

  private case class Point(x: Double, y: Double) extends HasEmblem
  private val pointEmblem = emblemFor[Point]

  behavior of "the EmblemPropToValueMap constructor"
  it should "return an empty map" in {
    val map = EmblemPropToValueMap[Point]()
    map.size should equal (0)
    map.isEmpty should be (true)
    intercept[EmblemPropToValueMap.NoValueForEmblemProp] {
      map.get(pointEmblem("x"))
    }
    intercept[EmblemPropToValueMap.NoValueForEmblemProp] {
      map.get(pointEmblem("y"))
    }
  }

  behavior of "EmblemPropToValueMap.+"
  it should "return a map with the specified mapping added" in {
    val map = EmblemPropToValueMap[Point]() + (pointEmblem.prop[Double]("x") -> 7.0)
    map.size should equal (1)
    map.isEmpty should be (false)
    map.get(pointEmblem("x")) should equal (7.0)
    intercept[EmblemPropToValueMap.NoValueForEmblemProp] {
      map.get(pointEmblem("y"))
    }
  }

}
