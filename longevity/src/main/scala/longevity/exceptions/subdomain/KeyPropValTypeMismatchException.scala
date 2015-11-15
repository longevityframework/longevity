package longevity.exceptions.subdomain

import longevity.subdomain.KeyProp
import longevity.subdomain.RootEntity

// * [[longevity.subdomain.Key#ValBuilder.setProp]] does not match the type of the

/** an exception indicating that the property value passed to either of the `setProp` methods in
 * [[longevity.subdomain.Key#ValBuilder]] does not match the type of the
 * [[longevity.subdomain.KeyProp natural key property]].
 *
 * @param prop the nat key prop
 * @param propVal the nat key prop value
 */
class KeyPropValTypeMismatchException[E <: RootEntity](prop: KeyProp[E], propVal: Any)
extends KeyValBuilderException(
  s"prop val $propVal does not match type of nat key prop $prop")
