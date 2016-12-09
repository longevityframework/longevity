package longevity.integration.model.partitionKeyInComponentWithPartialPartition

import longevity.model.annotations.keyVal

@keyVal[PKInComponentWithPartialPartition]
case class Key(part1: String, part2: String)
