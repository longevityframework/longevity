package longevity.exceptions

import longevity.subdomain.NatKeyProp
import longevity.subdomain.RootEntity

/** an exception indicating that the property value passed to
 * [[longevity.subdomain.NatKey#Builder.setProp]] does not match the type of the
 * [[longevity.subdomain.NatKeyProp natural key property]].
 *
 * @param prop the nat key prop
 * @param propVal the nat key prop value
 */
class NatKeyPropValTypeMismatchException[E <: RootEntity](prop: NatKeyProp[E], propVal: Any)
extends NatKeyValBuilderException(
  s"prop val $propVal does not match type of nat key prop $prop")
