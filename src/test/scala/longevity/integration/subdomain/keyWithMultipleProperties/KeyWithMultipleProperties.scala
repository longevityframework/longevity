package longevity.integration.subdomain.keyWithMultipleProperties

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class KeyWithMultipleProperties(
  id: KeyWithMultiplePropertiesId,
  secondaryKey: SecondaryKey)
extends Root

object KeyWithMultipleProperties extends RootType[KeyWithMultipleProperties] {
  object props {
    val id = prop[KeyWithMultiplePropertiesId]("id")
    val secondaryKey = prop[SecondaryKey]("secondaryKey")
  }
  object keys {
    val id = key(props.id)
    val secondaryKey = key(props.secondaryKey)
  }
  object indexes {
  }
}
