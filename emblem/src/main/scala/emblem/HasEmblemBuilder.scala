package emblem

/** A builder of objects that [[HasEmblem have an emblem]].
 *
 * @tparam T the type of the object to build
 * @param defaults a set of default property values for the builder
 * @param creator a function to build the object from a [[EmblemPropToValueMap]]
 */
class HasEmblemBuilder[T <: HasEmblem](
  defaults: EmblemPropToValueMap[T],
  creator: EmblemPropToValueMap[T] => T) {

  private var map: EmblemPropToValueMap[T] = defaults

  /** specifies the value to use for the given property */
  def setProp[U](prop: EmblemProp[T, U], value: U): Unit = map += (prop -> value)

  /** builds and returns the [[HasEmblem]] object */
  def build(): T = creator(map)

}
