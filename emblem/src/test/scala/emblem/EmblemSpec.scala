package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[Emblem emblem]] specifications */
class EmblemSpec extends FlatSpec with GivenWhenThen with Matchers {

  trait Foo extends HasEmblem

  behavior of "emblem generator"
  it should "throw exception on non case class types" in {
    intercept[TypeIsNotCaseClassException[_]] {
      emblemFor[Foo]
    }
  }

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

  val pointEmblem = emblemFor[Point]

  behavior of "an emblem"

  it should "retain name information" in {
    pointEmblem.namePrefix should equal ("emblem.EmblemSpec")
    pointEmblem.name should equal ("Point")
    pointEmblem.fullname should equal ("emblem.EmblemSpec.Point")
  }

  it should "retain type information" in {
    pointEmblem.typeKey should equal (typeKey[Point])
  }

  it should "dump helpful debug info" in {
    pointEmblem.debugInfo should equal (
      """|emblem.EmblemSpec.Point {
         |  x: scala.Double
         |  y: scala.Double
         |}""".stripMargin)
  }

}
