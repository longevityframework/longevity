package emblem

package object testData {

  import emblem._

  case class Point(x: Double, y: Double) extends HasEmblem
  val pointEmblem = emblemFor[Point]
  val xProp = pointEmblem.prop[Double]("x")
  val yProp = pointEmblem.prop[Double]("y")

  case class Polygon(corners: Set[Point]) extends HasEmblem
  val polygonEmblem = emblemFor[Polygon]
  val cornersProp = polygonEmblem.prop[Set[Point]]("corners")

  case class PointWithDefaults(x: Double = 17.0, y: Double = 13.0) extends HasEmblem
  val pointWithDefaultsEmblem = emblemFor[PointWithDefaults]
  val xPropWithDefaults = pointWithDefaultsEmblem.prop[Double]("x")
  val yPropWithDefaults = pointWithDefaultsEmblem.prop[Double]("y")

  implicit class ImplicitBar(private val implicitBar: String) extends AnyVal {
    override def toString = implicitBar
  }
  case class FooWithImplicit(implicitBar: ImplicitBar, point: Point) extends HasEmblem
  val fooWithImplicitEmblem = emblemFor[FooWithImplicit]
  val implicitBarProp = fooWithImplicitEmblem.prop[ImplicitBar]("implicitBar")
  val pointProp = fooWithImplicitEmblem.prop[Point]("point")

  trait NotACaseClass extends HasEmblem

  case class MultipleParamLists(i: Int)(j: Int) extends HasEmblem

  class HasInnerClass {
    case class IsInnerCaseClass(i: Int) extends HasEmblem
  }

}
