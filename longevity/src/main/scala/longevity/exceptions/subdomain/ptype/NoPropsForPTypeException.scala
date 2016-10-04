package longevity.exceptions.subdomain.ptype

import emblem.TypeKey
import longevity.subdomain.Persistent

/** an exception thrown when [[longevity.subdomain.PType persistent type]]
 * neither overrides `propSet`, nor defines an inner object `props`
 */
class NoPropsForPTypeException[P <: Persistent : TypeKey] extends PTypeException(
  s"PType ${implicitly[TypeKey[P]].name} must either override `propSet`, or define an inner object `props`")
