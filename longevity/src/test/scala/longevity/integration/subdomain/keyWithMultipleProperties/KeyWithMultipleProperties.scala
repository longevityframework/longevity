package longevity.integration.subdomain.keyWithMultipleProperties

import longevity.subdomain.PType

case class KeyWithMultipleProperties(
  id: KeyWithMultiplePropertiesId,
  secondaryKey: SecondaryKey)

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
