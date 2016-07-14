package longevity.subdomain.ptype

import emblem.TypeKey
import emblem.emblematic.Emblem
import emblem.typeKey
import longevity.subdomain.KeyVal
import longevity.subdomain.persistent.Persistent

/** a natural key for this persistent type. wraps a [[Prop property]] that,
 * given specific a property value, will match no more than one persistent
 * object.
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
