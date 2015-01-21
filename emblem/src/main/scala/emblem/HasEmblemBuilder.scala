package emblem

/** A builder of objects that [[HasEmblem have an emblem]].
 *
 * @tparam T the type of the object to build
 */
class HasEmblemBuilder[T <: HasEmblem : TypeKey] private[emblem] (creator: Map[String, Any] => T) {

  private var map = Map[String, Any]()

  /** specifies the value to use for the given property */
  def setProp[U](prop: EmblemProp[T, U], value: U): Unit = map += (prop.name -> value)

  /** builds and returns the [[HasEmblem]] object */
  def build(): T = creator(map)

}
