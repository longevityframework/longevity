package longevity.integration.model.primaryKeyWithComplexPartialPartition

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class PrimaryKeyWithComplexPartialPartition(key: Key)

object PrimaryKeyWithComplexPartialPartition {
  implicit val idKey = primaryKey(props.key, partition(props.key.prop1, props.key.subKey.prop1))
}
