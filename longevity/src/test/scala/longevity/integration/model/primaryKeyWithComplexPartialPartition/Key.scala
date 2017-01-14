package longevity.integration.model.primaryKeyWithComplexPartialPartition

import longevity.model.annotations.keyVal

@keyVal[PrimaryKeyWithComplexPartialPartition]
case class Key(
  prop1: String,
  subKey: SubKey,
  prop2: String)
