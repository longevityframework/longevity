package longevity.exceptions.subdomain.ptype

import emblem.imports._
import longevity.subdomain.persistent.Persistent

/** an exception thrown when [[PType persistent type]] neither overrides
 * `indexSet` nor defines an inner object `indexes`
 */
class NoIndexesForPTypeException[P <: Persistent : TypeKey] extends PTypeException(
  "PType ${typeKey[P].name} must either override `indexSet`, or define an inner object `indexes`")
