package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[Emblem emblem]] specifications */
class EmblemSpec extends FlatSpec with GivenWhenThen with Matchers {

  case class Point(x: Double, y: Double) extends HasEmblem

  private val xProp = new EmblemProp[Point, Double]("x", _.x, (p, x) => p.copy(x = x))
  private val yProp = new EmblemProp[Point, Double]("y", _.y, (p, y) => p.copy(y = y))
  object PointEmblem extends Emblem[Point](
    "emblem.EmblemSpec",
    "Point",
    Seq(xProp, yProp),
    EmblemPropToValueMap[Point](),
    { (map: EmblemPropToValueMap[Point]) => Point(map.get(xProp), map.get(yProp)) }
  )

  behavior of "an emblem"

  it should "retain name information" in {
    PointEmblem.namePrefix should equal ("emblem.EmblemSpec")
    PointEmblem.name should equal ("Point")
    PointEmblem.fullname should equal ("emblem.EmblemSpec.Point")
  }

  it should "retain type information" in {
    PointEmblem.typeKey should equal (typeKey[Point])
  }

  it should "dump helpful debug info" in {
    PointEmblem.debugInfo should equal (
      """|emblem.EmblemSpec.Point {
         |  x: Double
         |  y: Double
         |}""".stripMargin)
  }

}
