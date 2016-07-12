package longevity.exceptions.subdomain.ptype

import emblem.TypeKey
import longevity.subdomain.persistent.Persistent

/** thrown on attempt to create a property with a type that longevity does not
 * now support, such as property paths that contain any collections or
 * [[longevity.subdomain.embeddable.PolyType polymorphic types]].
 */
class UnsupportedPropTypeException[P <: Persistent : TypeKey, U : TypeKey](val path: String)
extends PropException(
  s"longevity doesn't currently support properties with type `${implicitly[TypeKey[U]].name}`, such as " +
  s"`$path` in `${implicitly[TypeKey[P]].name}`")

