package longevity.exceptions.subdomain.ptype

import emblem.TypeKey
import longevity.subdomain.Persistent

/** an exception thrown when [[longevity.subdomain.DerivedPType derived
 * persistent type]] contains a [[longevity.subdomain.ptype.PartitionKey
 * partition key]]
 */
class PartitionKeyForDerivedPTypeException[P <: Persistent : TypeKey] extends PTypeException(
  s"DerivedPType ${implicitly[TypeKey[P]].name} declares a partition key")
