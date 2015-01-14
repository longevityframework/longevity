package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[HasEmblemBuilder HasEmblem builder]] specifications */
class HasEmblemBuilderSpec extends FlatSpec with GivenWhenThen with Matchers {

  private case class Point(x: Double, y: Double) extends HasEmblem

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

  private case class Point2(x: Double = 17.0, y: Double = 13.0) extends HasEmblem

  private val xProp2 = new EmblemProp[Point2, Double]("x", _.x, (p, x) => p.copy(x = x))
  private val yProp2 = new EmblemProp[Point2, Double]("y", _.y, (p, y) => p.copy(y = y))

  private object Point2Emblem extends Emblem[Point2](
    "emblem.HasEmblemBuilderSpec",
    "Point2",
    Seq(xProp2, yProp2),
    EmblemPropToValueMap[Point2]() + (xProp2, 17.0) + (yProp2, 13.0),
    { (map: EmblemPropToValueMap[Point2]) => Point2(map.get(xProp2), map.get(yProp2)) }
  )

  it should "use the default values provided" in {
    val builder = Point2Emblem.builder()
    val point = builder.build()
    point should equal (Point2(17.0, 13.0))
    builder.setProp(yProp2, 5.0)
    val point2 = builder.build()
    point2 should equal (Point2(17.0, 5.0))
  }

}
