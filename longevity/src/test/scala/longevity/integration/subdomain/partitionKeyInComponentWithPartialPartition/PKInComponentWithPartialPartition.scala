package longevity.integration.subdomain.partitionKeyInComponentWithPartialPartition

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class PKInComponentWithPartialPartition(
  filler: String,
  component: Component)
extends Persistent

object PKInComponentWithPartialPartition extends PType[PKInComponentWithPartialPartition] {
  object props {
    val key = prop[Key]("component.key")
    val partition = prop[String]("component.key.part1")
  }
  object keys {
    val primaryKey = partitionKey(props.key, partition(props.partition))
  }
}
