package longevity.integration.subdomain.multipleKeys

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class MultipleKeys(
  uri: String,
  username: String)
extends Root

object MultipleKeys extends RootType[MultipleKeys] {
  object props {
    val uri = prop[String]("uri")
    val username = prop[String]("username")
  }
  object keys {
    val uri = key(props.uri)
    val username = key(props.username)
  }
  object indexes {
  }
}
