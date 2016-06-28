package longevity.integration.subdomain.keyWithForeignKey

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class KeyWithForeignKey(
  id: KeyWithForeignKeyId,
  secondaryKey: SecondaryKey)
extends Root

object KeyWithForeignKey extends RootType[KeyWithForeignKey] {
  object props {
    val id = prop[KeyWithForeignKeyId]("id")
    val secondaryKey = prop[SecondaryKey]("secondaryKey")
  }
  object keys {
    val id = key(props.id)
    val secondaryKey = key(props.secondaryKey)
  }
  object indexes {
  }
}
