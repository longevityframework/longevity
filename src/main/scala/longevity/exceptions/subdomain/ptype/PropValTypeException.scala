package longevity.exceptions.subdomain.ptype

import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Prop

/** an exception indicating an attempt to set a property with a value of the
 * wrong type.
 *
 * @param prop the key prop
 * @param propVal the key prop value
 */
class PropValTypeException[P <: Persistent](prop: Prop[P, _], propVal: Any)
extends KeyValException(
  s"value $propVal does not match type of property $prop")
