package longevity.integration.subdomain.keyWithComponent

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class KeyWithComponent(
  id: KeyWithComponentId,
  secondaryKey: SecondaryKey)
extends Root

object KeyWithComponent extends RootType[KeyWithComponent] {
  object props {
    val id = prop[KeyWithComponentId]("id")
    val secondaryKey = prop[SecondaryKey]("secondaryKey")
  }
  object keys {
    val id = key(props.id)
    val secondaryKey = key(props.secondaryKey)
  }
  object indexes {
  }
}
