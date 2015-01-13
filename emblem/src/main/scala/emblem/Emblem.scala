package emblem

import scala.reflect.runtime.universe.TypeTag
import stringUtil._

/** A reflective signature for a type. Provides name information, [[EmblemProp properties]],
 * and a degenerate instance. The underlying type is treated as immutable, so each property provides
 * a setter that returns a new instance. The degenerate instance can be used in conjunction with the
 * properties to construct new instances.
 *
 * @tparam T the type that this emblem reflects upon
 * @param namePrefix a dot-separated identifier of the enclosing scope of the type
 * @param name the unqualified type name
 * @param props the [[EmblemProp emblem properties]]
 * @param nullInstance a degenerate instance of type T
 */
class Emblem[T <: HasEmblem : TypeKey](
  val namePrefix: String,
  val name: String,
  val props: Seq[EmblemProp[T, _]],
  val nullInstance: T
) {

  /** A [[TypeKey type key]] for the type that this emblem reflects upon */
  lazy val typeKey: TypeKey[T] = implicitly[TypeKey[T]]

  /** the fully qualified type name */
  lazy val fullname = s"$namePrefix.$name"

  private val propMap: Map[String, EmblemProp[T, _]] = props.view.map(prop => prop.name -> prop).toMap

  /** retrieves an [[EmblemProp]] by name */
  def apply[U](name: String) = propMap(name).asInstanceOf[EmblemProp[T, U]]

  /** A string describing the emblem in full detail */
  lazy val debugInfo = {
    val builder = new StringBuilder()
    builder ++= s"$fullname {\n"
    props.foreach {
      prop => builder ++= s"  ${prop}\n"
    }
    builder ++= s"}"
    builder.toString
  }

  override def toString = fullname

}
