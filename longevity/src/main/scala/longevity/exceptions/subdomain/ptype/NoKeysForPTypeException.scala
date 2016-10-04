package longevity.exceptions.subdomain.ptype

import emblem.TypeKey
import longevity.subdomain.Persistent

/** an exception thrown when [[longevity.subdomain.PType persistent type]]
 * neither overrides `keySet`, nor defines an inner object `keys`
 */
class NoKeysForPTypeException[P <: Persistent : TypeKey] extends PTypeException(
  s"PType ${implicitly[TypeKey[P]].name} must either override `keySet`, or define an inner object `keys`")
