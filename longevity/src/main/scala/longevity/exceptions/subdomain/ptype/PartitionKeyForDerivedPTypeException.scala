package longevity.exceptions.subdomain.ptype

import emblem.TypeKey

/** an exception thrown when [[longevity.subdomain.DerivedPType derived
 * persistent type]] contains a [[longevity.subdomain.ptype.PartitionKey
 * partition key]]
 */
class PartitionKeyForDerivedPTypeException[P : TypeKey] extends PTypeException(
  s"DerivedPType ${implicitly[TypeKey[P]].name} declares a partition key")
