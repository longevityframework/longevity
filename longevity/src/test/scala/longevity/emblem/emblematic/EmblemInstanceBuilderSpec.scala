package longevity.emblem.emblematic

import org.scalatest._
import longevity.emblem.exceptions.RequiredPropertyNotSetException
import longevity.emblem.testData.geometry._
import longevity.emblem.testData.withImplicits._

/** [[Emblem.InstanceBuilder]] specifications */
class EmblemInstanceBuilderSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "an Emblem.InstanceBuilder"

  it should "build new objects of the emblemized type" in {
    val builder = pointEmblem.builder()
    builder.setProp(xProp, 3.0)
    builder.setProp(yProp, 4.0)
    val point = builder.build()
    point should equal (Point(3.0, 4.0))
    builder.setProp(yProp, 5.0)
    val point2 = builder.build()
    point2 should equal (Point(3.0, 5.0))
  }

  it should "fail to build a new object when not all properties are set" in {
    val builder = pointEmblem.builder()
    intercept[RequiredPropertyNotSetException] {
      builder.build()
    }
    builder.setProp(xProp, 3.0)
    intercept[RequiredPropertyNotSetException] {
      builder.build()
    }
    builder.setProp(yProp, 4.0)
    val point = builder.build()
    point should equal (Point(3.0, 4.0))
  }

  it should "work with set props" in {
    val builder = polygonEmblem.builder()
    builder.setProp(cornersProp, Set(Point(0.0, 0.0)))
    val polygon = builder.build()
    polygon should equal (Polygon(Set(Point(0.0, 0.0))))
    cornersProp.set(polygon, Set[Point]())
  }

  // this exposes a bug in scala-reflect: https://issues.scala-lang.org/browse/SI-9102
  ignore should "work with implicit props" in {
    val builder = fooWithImplicitEmblem.builder()
    val funnyString = "please implicitly transform me (this String) into an ImplicitBar"
    builder.setProp[ImplicitBar](implicitBarProp, funnyString)
    builder.setProp(pointProp, Point(0.0, 0.0))
    val fooWithImplicit = builder.build()
    fooWithImplicit should equal (FooWithImplicit(funnyString, Point(0.0, 0.0)))
  }

  it should "use the default values provided" in {
    val builder = pointWithDefaultsEmblem.builder()
    val point = builder.build()
    point should equal (PointWithDefaults(17.0, 13.0))
    builder.setProp(yPropWithDefaults, 5.0)
    val point2 = builder.build()
    point2 should equal (PointWithDefaults(17.0, 5.0))
  }

}
