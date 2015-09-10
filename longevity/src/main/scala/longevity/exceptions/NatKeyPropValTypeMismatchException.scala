package longevity.exceptions

import longevity.subdomain.RootEntity
import longevity.subdomain.RootEntityType

/** an exception indicating that the property value passed to
 * [[longevity.subdomain.RootEntityType#NatKey#Builder.setProp]] does not match the type of the
 * [[longevity.subdomain.RootEntityType#NatKeyProp natural key property]].
 *
 * @param prop the nat key prop
 * @param propVal the nat key prop value
 */
class NatKeyPropValTypeMismatchException[E <: RootEntity](
  prop: RootEntityType[E]#NatKeyProp,
  propVal: Any)
extends NatKeyValBuilderException(
  s"prop val $propVal does not match type of nat key prop $prop")
