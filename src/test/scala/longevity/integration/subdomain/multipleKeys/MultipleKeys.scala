package longevity.integration.subdomain.multipleKeys

import longevity.subdomain._

case class MultipleKeys(
  uri: String,
  username: String)
extends RootEntity

object MultipleKeys extends RootEntityType[MultipleKeys] {
  key("uri")
  key("username")
}

