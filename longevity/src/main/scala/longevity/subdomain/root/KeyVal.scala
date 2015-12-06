package longevity.subdomain.root

import longevity.exceptions.subdomain.root.NumPropValsException
import longevity.exceptions.subdomain.root.PropValTypeException
import longevity.subdomain._

/** a key value
 *
 * @param key the key that this is a value for
 * @param propVals a map from the properties in the key to the values
 */
case class KeyVal[R <: RootEntity] private[root] (
  val key: Key[R],
  val propVals: Map[Prop[R, _], Any]) {

  /** gets the value for the specified prop
   * 
   * throws java.util.NoSuchElementException if the prop is not part of the key
   * @param the prop to look up a value for
   */
  def apply(prop: Prop[R, _]): Any = propVals(prop)

}

object KeyVal {

  def apply[R <: RootEntity](key: Key[R], keyValArgs: KeyValArg[_]*): KeyVal[R] = {
    if (key.props.size != keyValArgs.size) throw new NumPropValsException(key, key.props.size, keyValArgs.size)
    val propVals = key.props.zip(keyValArgs).map {
      case (prop, keyValArg) =>
        if (! (keyValArg.typeKey <:< prop.typeKey)) throw new PropValTypeException(prop, keyValArg.value)
        prop -> keyValArg.value
    }
    KeyVal(key, propVals.toMap[Prop[R, _], Any])
  }

}
