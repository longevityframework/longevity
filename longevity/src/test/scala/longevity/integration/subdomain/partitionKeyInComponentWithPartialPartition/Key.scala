package longevity.integration.subdomain.partitionKeyInComponentWithPartialPartition

import longevity.model.annotations.keyVal

@keyVal[PKInComponentWithPartialPartition]
case class Key(part1: String, part2: String)
