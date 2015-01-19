package emblem

import scala.reflect.runtime.universe.TypeRef
import scala.reflect.runtime.universe.typeOf
import scala.reflect.runtime.universe.typeTag
import org.scalatest._
import org.scalatest.OptionValues._

/** [[EmblemProp emblem property]] specifications */
class EmblemPropSpec extends FlatSpec with GivenWhenThen with Matchers {

  import testData._

  behavior of "an emblem prop"

  it should "retain type information" in {
    pointEmblem("x").typeKey should equal (TypeKey(typeTag[Double]))
    pointEmblem("y").typeKey should equal (TypeKey(typeTag[Double]))
  }

  it should "allow getter access through the props" in {
    val point = Point(3.0, 4.0)
    pointEmblem("x").get(point) should equal (3.0)
    pointEmblem("y").get(point) should equal (4.0)
  }

  it should "allow setter access through the props" in {
    val point = Point(3.0, 4.0)
    pointEmblem.prop[Double]("x").set(point, 5.0) should equal (Point(5.0, 4.0))
    pointEmblem.prop[Double]("y").set(point, 5.0) should equal (Point(3.0, 5.0))
  }

  it should "retain embedded type information" in {
    polygonEmblem("corners").typeKey should equal (typeKey[Set[Point]])

    val typeArgs = polygonEmblem("corners").typeKey.tag.tpe.typeArgs
    typeArgs.size should equal (1)
    typeArgs.head =:= typeOf[Point] should be (true)

    polygonEmblem("corners").toString should equal ("corners: Set[emblem.testData.package.Point]")
  }

}
