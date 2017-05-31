package longevity.integration.model.primaryKeyWithComplexPartialPartition

import longevity.model.annotations.keyVal

@keyVal[DomainModel, PrimaryKeyWithComplexPartialPartition]
case class Key(
  prop1: String,
  subKey: SubKey,
  prop2: String)
