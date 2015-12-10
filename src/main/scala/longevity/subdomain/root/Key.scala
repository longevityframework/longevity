package longevity.subdomain.root

import longevity.subdomain._

/** a natural key for this root entity type. a set of properties for which, given specific
 * property values for each of the properties, will match no more than one root instance.
 * 
 * @tparam R the root entity type
 * @param props the set of properties that make up this key
 */
case class Key[R <: RootEntity] private [subdomain] (val props: Seq[Prop[R, _]]) {

  /** builds a [[KeyVal]] for this key from a series of [[KeyValArg key val args]]
   * 
   * @throws longevity.exceptions.subdomain.root.NumPropValsException if the number of key val args does not
   * match the number of properties in the key
   * 
   * @throws longevity.exceptions.subdomain.root.PropValTypeException if the type of the key val arg does not
   * not match the type of the corresponding property in the key
   */
  def apply(keyValArgs: KeyValArg[_]*): KeyVal[R] = KeyVal(this, keyValArgs: _*)

  /** returns the [[KeyVal]] for the supplied root entity
   * @param root the root entity
   */
  def keyVal(root: R): KeyVal[R] = {
    val propVals = props.map { prop => prop -> prop.propVal(root) }
    KeyVal(this, propVals.toMap[Prop[R, _], Any])
  }

}
