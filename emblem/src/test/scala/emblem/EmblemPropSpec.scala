package emblem

import scala.reflect.runtime.universe.TypeRef
import scala.reflect.runtime.universe.typeOf
import scala.reflect.runtime.universe.typeTag
import org.scalatest._
import org.scalatest.OptionValues._

/** [[EmblemProp emblem property]] specifications */
class EmblemPropSpec extends FlatSpec with GivenWhenThen with Matchers {

  case class Point(x: Double, y: Double) extends HasEmblem

  private val pointEmblem = emblemFor[Point]

  private val xProp = new EmblemProp[Point, Double]("x", _.x, (p, x) => p.copy(x = x))
  private val yProp = new EmblemProp[Point, Double]("y", _.y, (p, y) => p.copy(y = y))
  object PointEmblem extends Emblem[Point](
    "emblem.EmblemSpec",
    "Point",
    Seq(xProp, yProp),
    EmblemPropToValueMap[Point](),
    { (map: EmblemPropToValueMap[Point]) => Point(map.get(xProp), map.get(yProp)) }
  ) {

    lazy val x = this.prop[Double]("x")
    lazy val y = this.prop[Double]("y")
  }

  behavior of "an emblem prop"

  it should "retain type information" in {
    pointEmblem("x").typeKey should equal (TypeKey(typeTag[Double]))
    pointEmblem("y").typeKey should equal (TypeKey(typeTag[Double]))
  }

  val point = Point(3.0, 4.0)

  it should "allow getter access through the props" in {
    PointEmblem("x").get(point) should equal (3.0)
    PointEmblem("y").get(point) should equal (4.0)
  }

  it should "allow setter access through the props" in {
    PointEmblem.prop[Double]("x").set(point, 5.0) should equal (Point(5.0, 4.0))
    PointEmblem.prop[Double]("y").set(point, 5.0) should equal (Point(3.0, 5.0))
  }

  private case class Polygon(corners: Set[Point]) extends HasEmblem
  private val polygonEmblem = emblemFor[Polygon]

  it should "retain embedded type information" in {
    polygonEmblem("corners").typeKey should equal (typeKey[Set[Point]])

    val typeArgs = polygonEmblem("corners").typeKey.tag.tpe.typeArgs
    typeArgs.size should equal (1)
    typeArgs.head should equal (typeOf[Point])

    polygonEmblem("corners").toString should equal ("corners: Set[EmblemPropSpec.this.Point]")
  }

}
