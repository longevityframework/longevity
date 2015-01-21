package emblem

package object testData {

  import emblem._

  // for testing emblem success cases:

  case class Point(x: Double, y: Double) extends HasEmblem
  lazy val pointEmblem = emblemFor[Point]
  lazy val xProp = pointEmblem.prop[Double]("x")
  lazy val yProp = pointEmblem.prop[Double]("y")

  case class Polygon(corners: Set[Point]) extends HasEmblem
  lazy val polygonEmblem = emblemFor[Polygon]
  lazy val cornersProp = polygonEmblem.prop[Set[Point]]("corners")

  case class PointWithDefaults(x: Double = 17.0, y: Double = 13.0) extends HasEmblem
  lazy val pointWithDefaultsEmblem = emblemFor[PointWithDefaults]
  lazy val xPropWithDefaults = pointWithDefaultsEmblem.prop[Double]("x")
  lazy val yPropWithDefaults = pointWithDefaultsEmblem.prop[Double]("y")

  implicit class ImplicitBar(private val implicitBar: String) extends AnyVal {
    override def toString = implicitBar
  }
  case class FooWithImplicit(implicitBar: ImplicitBar, point: Point) extends HasEmblem
  lazy val fooWithImplicitEmblem = emblemFor[FooWithImplicit]
  lazy val implicitBarProp = fooWithImplicitEmblem.prop[ImplicitBar]("implicitBar")
  lazy val pointProp = fooWithImplicitEmblem.prop[Point]("point")

  // for shorthand happy cases:

  case class Email(email: String)
  lazy val emailShorthand = shorthandFor[Email, String]

  case class Markdown(markdown: String)
  lazy val markdownShorthand = shorthandFor[Markdown, String]

  case class Uri(uri: String)
  lazy val uriShorthand = shorthandFor[Uri, String]

  // for emblem and shorthand failure cases:

  trait NotACaseClass extends HasEmblem

  case class MultipleParamLists(i: Int)(j: Int) extends HasEmblem

  class HasInnerClass {
    case class IsInnerCaseClass(i: Int) extends HasEmblem
  }

  case class MultipleParams(i: Int, j: Int)

}
