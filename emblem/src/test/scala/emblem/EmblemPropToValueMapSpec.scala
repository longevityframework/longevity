package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[EmblemPropToValueMap]] specifications */
class EmblemPropToValueMapSpec extends FlatSpec with GivenWhenThen with Matchers {

  private case class Point(x: Double, y: Double) extends HasEmblem

  private val xProp = new EmblemProp[Point, Double]("x", _.x, (p, x) => p.copy(x = x))
  private val yProp = new EmblemProp[Point, Double]("y", _.y, (p, y) => p.copy(y = y))
  private object PointEmblem extends Emblem[Point](
    "emblem.EmblemPropToValueMapSpec",
    "Point",
    Seq(xProp, yProp),
    EmblemPropToValueMap[Point](),
    { (map: EmblemPropToValueMap[Point]) => Point(map.get(xProp), map.get(yProp)) }
  )

  behavior of "the EmblemPropToValueMap constructor"
  it should "return an empty map" in {
    val map = EmblemPropToValueMap[Point]()
    map.size should equal (0)
    map.isEmpty should be (true)
    intercept[EmblemPropToValueMap.NoValueForEmblemProp] {
      map.get(xProp)
    }
    intercept[EmblemPropToValueMap.NoValueForEmblemProp] {
      map.get(yProp)
    }
  }

  behavior of "EmblemPropToValueMap.+"
  it should "return a map with the specified mapping added" in {
    val map = EmblemPropToValueMap[Point]() + (xProp -> 7.0)
    map.size should equal (1)
    map.isEmpty should be (false)
    map.get(xProp) should equal (7.0)
    intercept[EmblemPropToValueMap.NoValueForEmblemProp] {
      map.get(yProp)
    }    
  }

}
