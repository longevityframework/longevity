package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[HasEmblemBuilder HasEmblem builder]] specifications */
class HasEmblemBuilderSpec extends FlatSpec with GivenWhenThen with Matchers {

  private case class Point(x: Double, y: Double) extends HasEmblem
  private val pointEmblem = emblemFor[Point]

  private val xProp = new EmblemProp[Point, Double]("x", _.x, (p, x) => p.copy(x = x))
  private val yProp = new EmblemProp[Point, Double]("y", _.y, (p, y) => p.copy(y = y))
  private object PointEmblem extends Emblem[Point](
    "emblem.HasEmblemBuilderSpec",
    "Point",
    Seq(xProp, yProp),
    EmblemPropToValueMap[Point](),
    { (map: EmblemPropToValueMap[Point]) => Point(map.get(xProp), map.get(yProp)) }
  )

  behavior of "a HasEmblem builder"

  it should "build new objects of the emblemized type" in {
    val builder = PointEmblem.builder()
    builder.setProp(xProp, 3.0)
    builder.setProp(yProp, 4.0)
    val point = builder.build()
    point should equal (Point(3.0, 4.0))
    builder.setProp(yProp, 5.0)
    val point2 = builder.build()
    point2 should equal (Point(3.0, 5.0))
  }

  it should "fail to build a new object when not all properties are set" in {
    val builder = PointEmblem.builder()
    intercept[EmblemPropToValueMap.NoValueForEmblemProp] {
      builder.build()
    }
    builder.setProp(xProp, 3.0)
    intercept[EmblemPropToValueMap.NoValueForEmblemProp] {
      builder.build()
    }
    builder.setProp(yProp, 4.0)
    val point = builder.build()
    point should equal (Point(3.0, 4.0))
  }

  private case class PointWithDefaults(x: Double = 17.0, y: Double = 13.0) extends HasEmblem
  private val pointWithDefaultsEmblem = emblemFor[PointWithDefaults]

  private val xPropWithDefaults = new EmblemProp[PointWithDefaults, Double](
    "x", _.x, (p, x) => p.copy(x = x))
  private val yPropWithDefaults = new EmblemProp[PointWithDefaults, Double](
    "y", _.y, (p, y) => p.copy(y = y))
  private object PointWithDefaultsEmblem extends Emblem[PointWithDefaults](
    "emblem.HasEmblemBuilderSpec",
    "PointWithDefaults",
    Seq(xPropWithDefaults, yPropWithDefaults),
    EmblemPropToValueMap[PointWithDefaults]() + (xPropWithDefaults, 17.0) + (yPropWithDefaults, 13.0),
    { (map: EmblemPropToValueMap[PointWithDefaults]) =>
      PointWithDefaults(map.get(xPropWithDefaults), map.get(yPropWithDefaults))
    }
  )

  it should "use the default values provided" in {
    val builder = PointWithDefaultsEmblem.builder()
    val point = builder.build()
    point should equal (PointWithDefaults(17.0, 13.0))
    builder.setProp(yPropWithDefaults, 5.0)
    val point2 = builder.build()
    point2 should equal (PointWithDefaults(17.0, 5.0))
  }

}
