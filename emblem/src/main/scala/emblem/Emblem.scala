package emblem

import scala.reflect.runtime.universe.TypeTag
import stringUtil._

/** A reflective signature for a type. Provides name information, [[EmblemProp properties]],
 * and a tools used to build new instances. The underlying type is treated as immutable, so each property
 * provides a setter that returns a new instance. New instances can be built using a [[HasEmblemBuilder]]
 * returned by method [[builder]].
 *
 * @tparam T the type that this emblem reflects upon
 * @param namePrefix a dot-separated identifier of the enclosing scope of the type
 * @param name the unqualified type name
 * @param props the [[EmblemProp emblem properties]]
 * @param propDefaults default property values used by the builder
 * @param creator a function used by the builder to instantiate the new object
 */
case class Emblem[T <: HasEmblem : TypeKey] private[emblem] (
  val namePrefix: String,
  val name: String,
  val props: Seq[EmblemProp[T, _]],
  val creator: Map[String, Any] => T
) {

  /** A [[TypeKey type key]] for the type that this emblem reflects upon */
  lazy val typeKey: TypeKey[T] = implicitly[TypeKey[T]]

  /** the fully qualified type name */
  lazy val fullname = s"$namePrefix.$name"

  private val propMap: Map[String, EmblemProp[T, _]] = props.view.map(prop => prop.name -> prop).toMap

  /** retrieves an [[EmblemProp]] by name */
  def apply(name: String) = propMap(name)

  /** retrieves an [[EmblemProp]] with the specified property type by name */
  def prop[U : TypeKey](name: String) = {
    val typeKey = implicitly[TypeKey[U]]
    val prop = propMap(name)
    if (typeKey != prop.typeKey) {
      throw new ClassCastException(
        s"requested property $name with type ${typeKey.tpe}, but this property has type ${prop.typeKey.tpe}")
    }
    prop.asInstanceOf[EmblemProp[T, U]]
  }

  /** creates and returns a new builder for constructing new instances */
  def builder(): HasEmblemBuilder[T] = new HasEmblemBuilder[T](creator)

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
