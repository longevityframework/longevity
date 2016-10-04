package longevity.exceptions.subdomain

import emblem.TypeKey
import longevity.subdomain.Persistent

/** thrown on attempt to create a property with a type that longevity does not
 * now support, such as property paths that contain any collections or terminate
 * with a [[longevity.subdomain.embeddable.PolyEType polymorphic type]].
 */
class UnsupportedPropTypeException[P <: Persistent : TypeKey, U : TypeKey](val path: String)
extends SubdomainException(
  s"longevity doesn't currently support properties with type `${implicitly[TypeKey[U]].name}`, such as " +
  s"`$path` in `${implicitly[TypeKey[P]].name}`")

