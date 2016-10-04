package longevity.integration.subdomain.keyWithComponent

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class KeyWithComponent(
  id: KeyWithComponentId,
  secondaryKey: SecondaryKey)
extends Persistent

object KeyWithComponent extends PType[KeyWithComponent] {
  object props {
    val id = prop[KeyWithComponentId]("id")
    val secondaryKey = prop[SecondaryKey]("secondaryKey")
  }
  object keys {
    val id = key(props.id)
    val secondaryKey = key(props.secondaryKey)
  }
}
