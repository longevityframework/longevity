package longevity.subdomain.root

import longevity.subdomain._

/** a natural key for this root entity type. a set of properties for which, given specific
 * property values for each of the properties, will match no more than one root instance.
 * 
 * @tparam R the root entity type
 * @param props the set of properties that make up this key
 */
case class Key[R <: RootEntity] private [subdomain] (val props: Seq[Prop[R, _]]) {

  // TODO scaladocs
  def apply(keyValArgs: KeyValArg[_]*): KeyVal[R] = KeyVal(this, keyValArgs: _*)

  /** returns the key val for the supplied root entity
   * @param e the root entity
   */
  def keyVal(root: R): KeyVal[R] = {
    val propVals = props.map { prop => prop -> prop.propVal(root) }
    KeyVal(this, propVals.toMap[Prop[R, _], Any])
  }

}
