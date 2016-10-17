package longevity.integration.subdomain.key

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class Key(id: KeyId)
extends Persistent

object Key extends PType[Key] {
  object props {
    val id = prop[KeyId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
