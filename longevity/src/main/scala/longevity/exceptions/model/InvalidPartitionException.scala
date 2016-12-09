package longevity.exceptions.model

import emblem.TypeKey
import longevity.model.ptype.PartitionKey

/** an exception indicating an attempt to create a property with a prop path
 * that does not exist
 *
 * @param path the requested property path
 * @param pTypeKey the type of the persistent with the problematic property
 */
class InvalidPartitionException[P : TypeKey] (val key: PartitionKey[P])
extends DomainModelException(
  s"partition key $key for ${implicitly[TypeKey[P]].name} has an invalid partition. " +
  s"the properties in the partition must form a prefix of the key value property.")

