package longevity.integration.model.primaryKeyInComponentWithPartialPartition

import longevity.model.annotations.keyVal

@keyVal[DomainModel, PKInComponentWithPartialPartition]
case class Key(part1: String, part2: String)
