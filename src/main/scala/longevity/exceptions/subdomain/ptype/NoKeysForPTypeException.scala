package longevity.exceptions.subdomain.ptype

import emblem.imports._
import longevity.subdomain.persistent.Persistent

/** an exception thrown when [[longevity.subdomain.ptype.PType persistent type]]
 * neither overrides `keySet`, nor defines an inner object `keys`
 */
class NoKeysForPTypeException[P <: Persistent : TypeKey] extends PTypeException(
  "PType ${typeKey[P].name} must either override `keySet`, or define an inner object `keys`")
