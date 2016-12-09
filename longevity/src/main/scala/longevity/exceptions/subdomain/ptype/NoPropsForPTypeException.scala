package longevity.exceptions.subdomain.ptype

import emblem.TypeKey

/** an exception thrown when [[longevity.model.PType persistent type]]
 * neither overrides `propSet`, nor defines an inner object `props`
 */
class NoPropsForPTypeException[P : TypeKey] extends PTypeException(
  s"PType ${implicitly[TypeKey[P]].name} must either override `propSet`, or define an inner object `props`")
