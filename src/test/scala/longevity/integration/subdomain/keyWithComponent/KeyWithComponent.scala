package longevity.integration.subdomain.keyWithComponent

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.PType

case class KeyWithComponent(
  id: KeyWithComponentId,
  secondaryKey: SecondaryKey)
extends Root

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
