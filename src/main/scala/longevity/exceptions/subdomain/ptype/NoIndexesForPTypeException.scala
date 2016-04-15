package longevity.exceptions.subdomain.ptype

import emblem.TypeKey
import longevity.subdomain.persistent.Persistent

/** an exception thrown when [[longevity.subdomain.ptype.PType persistent type]]
 * neither overrides `indexSet` nor defines an inner object `indexes`
 */
class NoIndexesForPTypeException[P <: Persistent : TypeKey] extends PTypeException(
  "PType ${implicitly[TypeKey[P]].name} must either override `indexSet`, or define an inner object `indexes`")
