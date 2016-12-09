package longevity.exceptions.model

import emblem.TypeKey

/** an exception indicating an attempt to create a property with the wrong type.
 *
 * @param prop the key prop
 * @param propVal the key prop value
 */
class PropTypeException(path: String, pTypeKey: TypeKey[_], propType: TypeKey[_], pathTypeKey: TypeKey[_])
extends DomainModelException(
  s"property '$path' with type ${pathTypeKey.name} for root type ${pTypeKey.name} " +
  s"does not match type ${propType.name}")
