package longevity.integration.model.primaryKeyWithPartialPartition

import longevity.model.annotations.keyVal

@keyVal[DomainModel, PrimaryKeyWithPartialPartition]
case class Key(prop1: String, prop2: String)
