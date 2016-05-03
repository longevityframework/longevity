package longevity.subdomain

import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.PType

/** the base type for a family of persistent types. mix this in to your
 * [[longevity.subdomain.ptype.PType persistent type]] when it represents an
 * abstract persistent type with concrete subtypes.
 */
trait PolyPType[Poly <: Persistent] extends PType[Poly]
