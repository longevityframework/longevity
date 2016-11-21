package longevity.integration.subdomain.multipleKeys

import longevity.subdomain.PType
import longevity.subdomain.annotations.mprops

case class MultipleKeys(
  id: MultipleKeysId,
  username: Username)

@mprops object MultipleKeys extends PType[MultipleKeys] {
  object keys {
    val id = key(props.id)
    val username = key(props.username)
  }
}
