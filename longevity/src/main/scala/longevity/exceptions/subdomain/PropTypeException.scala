package longevity.exceptions.subdomain

import emblem.TypeKey
import longevity.subdomain.Persistent

/** an exception indicating an attempt to create a property with the wrong type.
 *
 * @param prop the key prop
 * @param propVal the key prop value
 */
class PropTypeException(path: String, pTypeKey: TypeKey[_ <: Persistent], propType: TypeKey[_])
extends SubdomainException(
  s"property '$path' for root ${pTypeKey.name} does not match type ${propType.name}")
