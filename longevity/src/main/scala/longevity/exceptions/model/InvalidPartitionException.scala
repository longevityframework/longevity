package longevity.exceptions.model

import emblem.TypeKey
import longevity.model.ptype.PrimaryKey

/** an exception indicating an attempt to create a primary key with a
  * partition that does not form a prefix of the key value property
  *
  * @param path the requested property path
  * @param pTypeKey the type of the persistent with the problematic property
  */
class InvalidPartitionException[P : TypeKey] (val key: PrimaryKey[P])
extends ModelTypeException(
  s"primary key $key for ${implicitly[TypeKey[P]].name} has an invalid partition. " +
  s"the properties in the partition must form a prefix of the key value property.")

