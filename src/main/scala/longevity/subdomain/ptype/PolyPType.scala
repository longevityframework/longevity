package longevity.subdomain.ptype

import longevity.subdomain.Persistent

/** the base type for a family of persistent types. mix this in to your
 * [[PType persistent type]] when it represents an abstract persistent
 * type with concrete subtypes.
 */
trait PolyPType[P <: Persistent] extends PType[P]
