package longevity.subdomain.ptype

import emblem.TypeKey
import longevity.subdomain.Persistent

/** one of the derived types in a family of persistent types. mix this in to
 * youe [[PType persistent type]] when it represents a concrete subtype of a
 * [[PolyPType]].
 */
abstract class DerivedPType[P <: Persistent : TypeKey, Poly >: P <: Persistent : TypeKey] extends PType[P] {

  private[longevity] val polyPTypeKey: TypeKey[Poly] = implicitly[TypeKey[Poly]]

}
