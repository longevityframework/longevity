package longevity.integration.model.primaryKeyWithPartialPartition

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class PrimaryKeyWithPartialPartition(key: Key)

object PrimaryKeyWithPartialPartition {
  implicit val idKey = primaryKey(props.key, partition(props.key.prop1))
}
