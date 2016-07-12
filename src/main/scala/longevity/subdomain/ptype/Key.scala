package longevity.subdomain.ptype

import emblem.TypeKey
import emblem.emblematic.Emblem
import emblem.typeKey
import longevity.subdomain.KeyVal
import longevity.subdomain.persistent.Persistent

// TODO revisit this scaladoc
/** a natural key for this persistent type. a set of properties for which,
 * given specific property values for each of the properties, will match no more
 * than one persistent instance.
 * 
 * @tparam P the persistent type
 * @tparam V the key value type
 * @param keyValProp a property for the key
 */
case class Key[P <: Persistent : TypeKey, V <: KeyVal[P, V] : TypeKey] private [subdomain] (
  val keyValProp: Prop[P, V]) {

  private[subdomain] val keyValEmblem = Emblem[V]

  override def toString = s"Key[${typeKey[P].name},${typeKey[V].name}]"

}
