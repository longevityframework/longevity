package longevity.integration.model.primaryKeyWithComplexPartialPartition

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = Set(
  primaryKey(props.key, partition(props.key.prop1, props.key.subKey.prop1))))
case class PrimaryKeyWithComplexPartialPartition(key: Key)
