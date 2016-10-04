package longevity.integration.subdomain.keyWithForeignKey

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.PType

case class KeyWithForeignKey(
  id: KeyWithForeignKeyId,
  secondaryKey: SecondaryKey)
extends Root

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
