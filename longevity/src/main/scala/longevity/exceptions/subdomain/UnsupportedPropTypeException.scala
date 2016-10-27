package longevity.exceptions.subdomain

import emblem.TypeKey
import longevity.subdomain.Persistent

/** thrown on attempt to create a property with a type that longevity does not
 * now support, such as:
 *
 * - properties with non-basic, non-embeddable, non-key-val types
 * - property paths that contain any collections
 * - property paths that terminate with a [[longevity.subdomain.PolyEType polymorphic type]].
 */
class UnsupportedPropTypeException[P <: Persistent : TypeKey, U : TypeKey](val path: String)
extends SubdomainException(
  s"longevity doesn't currently support properties with type `${implicitly[TypeKey[U]].name}`, such as " +
  s"`$path` in `${implicitly[TypeKey[P]].name}`")

