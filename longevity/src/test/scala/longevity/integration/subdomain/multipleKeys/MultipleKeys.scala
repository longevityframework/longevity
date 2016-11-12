package longevity.integration.subdomain.multipleKeys

import longevity.subdomain.PType

case class MultipleKeys(
  id: MultipleKeysId,
  username: Username)

object MultipleKeys extends PType[MultipleKeys] {
  object props {
    val id = prop[MultipleKeysId]("id")
    val username = prop[Username]("username")
  }
  object keys {
    val id = key(props.id)
    val username = key(props.username)
  }
}
