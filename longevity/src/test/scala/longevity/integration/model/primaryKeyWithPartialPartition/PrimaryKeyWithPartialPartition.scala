package longevity.integration.model.primaryKeyWithPartialPartition

import longevity.model.annotations.persistent

@persistent(keySet = Set(primaryKey(props.key, partition(props.key.prop1))))
case class PrimaryKeyWithPartialPartition(
  key: Key)
