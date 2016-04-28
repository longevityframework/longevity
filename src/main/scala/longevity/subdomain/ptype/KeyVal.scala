package longevity.subdomain.ptype

import longevity.exceptions.subdomain.ptype.NumPropValsException
import longevity.exceptions.subdomain.ptype.PropValTypeException
import longevity.subdomain.PRef
import longevity.subdomain.persistent.Persistent

/** a key value
 *
 * @param key the key that this is a value for
 * @param propVals a map from the properties in the key to the values
 */
case class KeyVal[P <: Persistent] private[ptype] (
  val key: Key[P],
  val propVals: Map[Prop[P, _], Any])
extends PRef[P] {
  private[longevity] val _lock = 0

  /** the property values in the same order as the properties in the key */
  lazy val propValSeq = key.props.map(propVals(_).asInstanceOf[AnyRef])

  /** gets the value for the specified prop
   * 
   * throws java.util.NoSuchElementException if the prop is not part of the key
   * @param the prop to look up a value for
   */
  def apply[A](prop: Prop[P, A]): A = propVals(prop).asInstanceOf[A]

}

object KeyVal {

  /** builds a [[KeyVal]] from a [[Key]] and a series of [[KeyValArg key val args]]
   * 
   * @throws longevity.exceptions.subdomain.ptype.NumPropValsException if the
   * number of key val args does not match the number of properties in the key
   * 
   * @throws longevity.exceptions.subdomain.ptype.PropValTypeException if the
   * type of the key val arg does not not match the type of the corresponding
   * property in the key
   */
  def apply[P <: Persistent](key: Key[P], keyValArgs: KeyValArg[_]*): KeyVal[P] = {
    if (key.props.size != keyValArgs.size)
      throw new NumPropValsException(key, key.props.size, keyValArgs.size)
    val propVals = key.props.zip(keyValArgs).map {
      case (prop, keyValArg) =>
        if (! (keyValArg.typeKey <:< prop.propTypeKey))
          throw new PropValTypeException(prop, keyValArg.value)
        prop -> keyValArg.value
    }
    KeyVal(key, propVals.toMap[Prop[P, _], Any])
  }

}
