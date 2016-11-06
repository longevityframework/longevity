package longevity.integration.subdomain.partitionKeyInComponentWithPartialPartition

import longevity.subdomain.KeyVal

case class Key(
  part1: String,
  part2: String)
extends KeyVal[PKInComponentWithPartialPartition, Key]
