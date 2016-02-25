package longevity.subdomain.root

import longevity.exceptions.subdomain.root.NumPropValsException
import longevity.exceptions.subdomain.root.PropValTypeException
import longevity.subdomain._

/** a key value
 *
 * @param key the key that this is a value for
 * @param propVals a map from the properties in the key to the values
 */
case class KeyVal[R <: Root] private[root] (
  val key: Key[R],
  val propVals: Map[Prop[R, _], Any]) {

  /** the property values in the same order as the properties in the key */
  lazy val propValSeq = key.props.map(propVals(_).asInstanceOf[AnyRef])

  /** gets the value for the specified prop
   * 
   * throws java.util.NoSuchElementException if the prop is not part of the key
   * @param the prop to look up a value for
   */
  def apply[A](prop: Prop[R, A]): A = propVals(prop).asInstanceOf[A]

}

object KeyVal {

  /** builds a [[KeyVal]] from a [[Key]] and a series of [[KeyValArg key val args]]
   * 
   * @throws longevity.exceptions.subdomain.root.NumPropValsException if the number of key val args does not
   * match the number of properties in the key
   * 
   * @throws longevity.exceptions.subdomain.root.PropValTypeException if the type of the key val arg does not
   * not match the type of the corresponding property in the key
   */
  def apply[R <: Root](key: Key[R], keyValArgs: KeyValArg[_]*): KeyVal[R] = {
    if (key.props.size != keyValArgs.size) throw new NumPropValsException(key, key.props.size, keyValArgs.size)
    val propVals = key.props.zip(keyValArgs).map {
      case (prop, keyValArg) =>
        if (! (keyValArg.typeKey <:< prop.typeKey)) throw new PropValTypeException(prop, keyValArg.value)
        prop -> keyValArg.value
    }
    KeyVal(key, propVals.toMap[Prop[R, _], Any])
  }

}
