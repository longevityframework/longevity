package longevity.integration.subdomain.multipleNatKeys

import longevity.subdomain._

case class MultipleNatKeys(
  uri: String,
  username: String)
extends RootEntity

object MultipleNatKeys extends RootEntityType[MultipleNatKeys] {
  natKey("uri")
  natKey("username")
}

