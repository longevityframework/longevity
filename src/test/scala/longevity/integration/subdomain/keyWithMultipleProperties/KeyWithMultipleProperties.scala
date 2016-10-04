package longevity.integration.subdomain.keyWithMultipleProperties

import longevity.ddd.subdomain.Root
import longevity.subdomain.PType

case class KeyWithMultipleProperties(
  id: KeyWithMultiplePropertiesId,
  secondaryKey: SecondaryKey)
extends Root

object KeyWithMultipleProperties extends PType[KeyWithMultipleProperties] {
  object props {
    val id = prop[KeyWithMultiplePropertiesId]("id")
    val secondaryKey = prop[SecondaryKey]("secondaryKey")
  }
  object keys {
    val id = key(props.id)
    val secondaryKey = key(props.secondaryKey)
  }
}
