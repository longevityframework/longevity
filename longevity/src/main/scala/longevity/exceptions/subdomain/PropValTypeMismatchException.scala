package longevity.exceptions.subdomain

import longevity.subdomain.Prop
import longevity.subdomain.RootEntity

/** an exception indicating that the property value passed to either of the `setProp` methods in
 * [[longevity.subdomain.Key#ValBuilder]] does not match the type of the
 * [[longevity.subdomain.Prop natural key property]].
 *
 * @param prop the nat key prop
 * @param propVal the nat key prop value
 */
class PropValTypeMismatchException[E <: RootEntity](prop: Prop[E, _], propVal: Any)
extends KeyValBuilderException(
  s"prop val $propVal does not match type of nat key prop $prop")
