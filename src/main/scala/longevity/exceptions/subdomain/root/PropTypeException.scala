package longevity.exceptions.subdomain.root

import emblem.TypeKey
import longevity.subdomain.Root

/** an exception indicating an attempt to create a property with the wrong type.
 *
 * @param prop the key prop
 * @param propVal the key prop value
 */
class PropTypeException(path: String, rootType: TypeKey[_ <: Root], propType: TypeKey[_])
extends PropException(
  s"property '$path' for root ${rootType.name} does not match type ${propType.name}")
