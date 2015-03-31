package emblem.testData

import emblem.imports._
import emblem.testData.geometry.Point

/** for testing ignored emblem cases */
object withImplicits {

  implicit class ImplicitBar(private val implicitBar: String) extends AnyVal {
    override def toString = implicitBar
  }
  case class FooWithImplicit(implicitBar: ImplicitBar, point: Point) extends HasEmblem
  lazy val fooWithImplicitEmblem = Emblem[FooWithImplicit]
  lazy val implicitBarProp = fooWithImplicitEmblem.prop[ImplicitBar]("implicitBar")
  lazy val pointProp = fooWithImplicitEmblem.prop[Point]("point")

}
