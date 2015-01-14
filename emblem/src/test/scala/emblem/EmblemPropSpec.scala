package emblem

import scala.reflect.runtime.universe.TypeRef
import scala.reflect.runtime.universe.typeOf
import scala.reflect.runtime.universe.typeTag
import org.scalatest._
import org.scalatest.OptionValues._

/** emblem property specifications */
class EmblemPropSpec extends FlatSpec with GivenWhenThen with Matchers {

  case class Point(x: Double, y: Double) extends HasEmblem

  private val xProp = new EmblemProp[Point, Double]("x", _.x, (p, x) => p.copy(x = x))
  private val yProp = new EmblemProp[Point, Double]("y", _.y, (p, y) => p.copy(y = y))
  object PointEmblem extends Emblem[Point](
    "emblem.EmblemSpec",
    "Point",
    Seq(xProp, yProp),
    EmblemPropToValueMap[Point](),
    { (map: EmblemPropToValueMap[Point]) => Point(map.get(xProp), map.get(yProp)) }
  ) {

    lazy val x = apply[Double]("x")
    lazy val y = apply[Double]("y")
  }

  behavior of "an emblem prop"

  it should "retain type information" in {
    PointEmblem.x.typeKey should equal (TypeKey(typeTag[Double]))
    PointEmblem.y.typeKey should equal (TypeKey(typeTag[Double]))
  }

  val point = Point(3.0, 4.0)

  it should "allow getter access through the props" in {
    PointEmblem[Double]("x").get(point) should equal (3.0)
    PointEmblem[Double]("y").get(point) should equal (4.0)
  }

  it should "allow setter access through the props" in {
    PointEmblem[Double]("x").set(point, 5.0) should equal (Point(5.0, 4.0))
    PointEmblem[Double]("y").set(point, 5.0) should equal (Point(3.0, 5.0))
  }

  it should "allow getter access through accessors" in {
    PointEmblem.x.get(point) should equal (3.0)
    PointEmblem.y.get(point) should equal (4.0)
  }

  it should "allow setter access through accessors" in {
    PointEmblem.x.set(point, 5.0) should equal (Point(5.0, 4.0))
    PointEmblem.y.set(point, 5.0) should equal (Point(3.0, 5.0))
  }

  case class Polygon(corners: Set[Point]) extends HasEmblem

  val cornersProp = new EmblemProp[Polygon, Set[Point]](
    "corners", _.corners, (p, corners) => p.copy(corners = corners))
  object PolygonEmblem extends Emblem[Polygon](
    "emblem.EmblemPropSpec",
    "Polygon",
    Seq(cornersProp),
    EmblemPropToValueMap[Polygon](),
    { (map: EmblemPropToValueMap[Polygon]) => Polygon(map.get(cornersProp)) }
  ) {

    lazy val corners = apply[Set[Point]]("corners")
  }

  it should "retain embedded type information" in {
    PolygonEmblem.corners.typeKey should equal (typeKey[Set[Point]])

    val typeArgs = PolygonEmblem.corners.typeKey.tag.tpe.typeArgs
    typeArgs.size should equal (1)
    typeArgs.head should equal (typeOf[Point])

    PolygonEmblem.corners.toString should equal ("corners: Set[EmblemPropSpec.this.Point]")
  }

}
