package longevity.subdomain.ptype

import longevity.subdomain.persistent.Persistent

/** a natural key for this persistent type. a set of properties for which,
 * given specific property values for each of the properties, will match no more
 * than one persistent instance.
 * 
 * @tparam R the root type
 * @param props the set of properties that make up this key
 */
case class Key[P <: Persistent] private [subdomain] (val props: Seq[Prop[P, _]]) {

  /** builds a [[KeyVal]] for this key from a series of [[KeyValArg key val args]]
   * 
   * @throws longevity.exceptions.subdomain.ptype.NumPropValsException if the
   * number of key val args does not match the number of properties in the key
   * 
   * @throws longevity.exceptions.subdomain.ptype.PropValTypeException if the
   * type of the key val arg does not not match the type of the corresponding
   * property in the key
   */
  def apply(keyValArgs: KeyValArg[_]*): KeyVal[P] = KeyVal(this, keyValArgs: _*)

  /** returns the [[KeyVal]] for the supplied persistent
   * @param p the persistent
   */
  def keyValForP(p: P): KeyVal[P] = {
    val propVals = props.map { prop => prop -> prop.propVal(p) }
    KeyVal(this, propVals.toMap[Prop[P, _], Any])
  }

  override def toString = s"Key(${props.mkString(",")})"

}
