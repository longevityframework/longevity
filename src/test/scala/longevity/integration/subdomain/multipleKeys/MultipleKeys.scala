package longevity.integration.subdomain.multipleKeys

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class MultipleKeys(
  id: MultipleKeysId,
  username: Username)
extends Root

object MultipleKeys extends RootType[MultipleKeys] {
  object props {
    val id = prop[MultipleKeysId]("id")
    val username = prop[Username]("username")
  }
  object keys {
    val id = key(props.id)
    val username = key(props.username)
  }
}
