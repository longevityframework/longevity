package emblem

object withImplicit {

  case class FooWithImplicit(implicitBar: ImplicitBar, point: Point) extends HasEmblem

  implicit class ImplicitBar(private val implicitBar: String) extends AnyVal {
    override def toString = implicitBar
  }

}
