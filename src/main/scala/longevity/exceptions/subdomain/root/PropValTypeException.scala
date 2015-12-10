package longevity.exceptions.subdomain.root

import longevity.subdomain.RootEntity
import longevity.subdomain.root.Prop

/** an exception indicating an attempt to set a property with a value of the wrong type.
 *
 * @param prop the key prop
 * @param propVal the key prop value
 */
class PropValTypeException[R <: RootEntity](prop: Prop[R, _], propVal: Any)
extends KeyValException(
  s"value $propVal does not match type of property $prop")
