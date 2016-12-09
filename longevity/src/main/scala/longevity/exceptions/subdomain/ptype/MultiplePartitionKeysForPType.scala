package longevity.exceptions.subdomain.ptype

import emblem.TypeKey

/** an exception thrown when [[longevity.model.PType persistent type]]
 * contains more than one [[longevity.model.ptype.PartitionKey partition key]]
 */
class MultiplePartitionKeysForPType[P : TypeKey] extends PTypeException(
  s"PType ${implicitly[TypeKey[P]].name} declares more than one partition key")
