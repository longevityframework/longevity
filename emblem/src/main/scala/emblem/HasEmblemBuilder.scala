package emblem

// TODO scaladoc
/** A builder of objects that [[HasEmblem have an emblem]].
 *
 * @tparam T
 * @param emblem
 */
class HasEmblemBuilder[T <: HasEmblem](
  defaults: EmblemPropToValueMap[T],
  creator: EmblemPropToValueMap[T] => T) {

  private var map: EmblemPropToValueMap[T] = defaults

  def setProp[U](prop: EmblemProp[T, U], value: U): Unit = map += (prop -> value)

  def build(): T = creator(map)

}
