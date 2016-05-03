package longevity.subdomain

import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.PType

/** one of the derived types in a family of persistent types. mix this in to
 * youe [[longevity.subdomain.ptype.PType persistent type]] when it represents
 * a concrete subtype of a [[PolyPType]].
 */
trait DerivedPType[Poly <: Persistent, Derived <: Poly] extends PType[Derived] {

  /** the poly type that this type is derived from */
  val polyPType: PolyPType[Poly]

}
