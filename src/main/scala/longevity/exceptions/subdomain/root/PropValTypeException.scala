package longevity.exceptions.subdomain.root

import longevity.subdomain.Persistent
import longevity.subdomain.root.Prop

/** an exception indicating an attempt to set a property with a value of the
 * wrong type.
 *
 * @param prop the key prop
 * @param propVal the key prop value
 */
class PropValTypeException[P <: Persistent](prop: Prop[P, _], propVal: Any)
extends KeyValException(
  s"value $propVal does not match type of property $prop")
