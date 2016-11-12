package longevity.integration.subdomain.keyWithForeignKey

import longevity.subdomain.PType

case class KeyWithForeignKey(
  id: KeyWithForeignKeyId,
  secondaryKey: SecondaryKey)

object KeyWithForeignKey extends PType[KeyWithForeignKey] {
  object props {
    val id = prop[KeyWithForeignKeyId]("id")
    val secondaryKey = prop[SecondaryKey]("secondaryKey")
  }
  object keys {
    val id = key(props.id)
    val secondaryKey = key(props.secondaryKey)
  }
}
