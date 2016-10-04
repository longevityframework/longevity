package longevity.subdomain.ptype

import emblem.TypeKey
import longevity.subdomain.Persistent

/** a property for this persistent type. properties map to underlying members
 * within the [Persistent persistent object], at any depth.
 *
 * properties can be used to define [[Key keys]] and [[Index indexes]], as well
 * as for building [[Query queries]]. a property can descend from the root into
 * child entities at any depth.
 *
 * at present, a property cannot pass through any collections or terminate with
 * a [[longevity.subdomain.embeddable.PolyEType polymorphic embeddable]].
 * violations will cause an exception to be thrown on
 * [[longevity.subdomain.Subdomain Subdomain construction]].
 * 
 * @param path a dot-separated path of the persistent object member descending
 * from the root
 * @param pTypeKey the `TypeKey` for the enclosing [[PType persistent type]]
 * @param propTypeKey the `TypeKey` for the property value type
 */
case class Prop[P <: Persistent, A] private[ptype] (
  path: String,
  pTypeKey: TypeKey[P],
  propTypeKey: TypeKey[A]) {

  override def toString: String = path

}
