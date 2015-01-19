package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[HasEmblemBuilder HasEmblem builder]] specifications */
class HasEmblemBuilderSpec extends FlatSpec with GivenWhenThen with Matchers {

  private val pointEmblem = emblemFor[Point]
  private val xProp = pointEmblem.prop[Double]("x")
  private val yProp = pointEmblem.prop[Double]("y")

  behavior of "a HasEmblem builder"

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
    intercept[EmblemPropToValueMap.NoValueForPropName] {
      builder.build()
    }
    builder.setProp(xProp, 3.0)
    intercept[EmblemPropToValueMap.NoValueForPropName] {
      builder.build()
    }
    builder.setProp(yProp, 4.0)
    val point = builder.build()
    point should equal (Point(3.0, 4.0))
  }

  private val polygonEmblem = emblemFor[Polygon]
  private val cornersProp = polygonEmblem.prop[Set[Point]]("corners")

  it should "work with set props" in {
    val builder = polygonEmblem.builder()
    builder.setProp(cornersProp, Set(Point(0.0, 0.0)))
    val polygon = builder.build()
    polygon should equal (Polygon(Set(Point(0.0, 0.0))))
    cornersProp.set(polygon, Set[Point]())
  }

  // TODO: submit as bug to scala
  // this exposes a bug in scala-reflect!
  ignore should "work with implicit props" in {
    import withImplicit._
    val fooWithImplicitEmblem = emblemFor[FooWithImplicit]
    val implicitBarProp = fooWithImplicitEmblem.prop[ImplicitBar]("implicitBar")
    val pointProp = fooWithImplicitEmblem.prop[Point]("point")

    val builder = fooWithImplicitEmblem.builder()
    val funnyString = "please implicitly transform me (this String) into an ImplicitBar"
    builder.setProp[ImplicitBar](implicitBarProp, funnyString)
    builder.setProp(pointProp, Point(0.0, 0.0))
    val fooWithImplicit = builder.build()
    fooWithImplicit should equal (FooWithImplicit(funnyString, Point(0.0, 0.0)))
  }

  it should "use the default values provided" in {
    val pointWithDefaultsEmblem = emblemFor[PointWithDefaults]
    val xPropWithDefaults = pointWithDefaultsEmblem.prop[Double]("x")
    val yPropWithDefaults = pointWithDefaultsEmblem.prop[Double]("y")

    val builder = pointWithDefaultsEmblem.builder()
    val point = builder.build()
    point should equal (PointWithDefaults(17.0, 13.0))
    builder.setProp(yPropWithDefaults, 5.0)
    val point2 = builder.build()
    point2 should equal (PointWithDefaults(17.0, 5.0))
  }

}
