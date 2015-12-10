package longevity.integration.subdomain.keyWithShorthand

import longevity.subdomain._

case class KeyWithShorthand(id: String, uri: Uri) extends RootEntity

object KeyWithShorthand extends RootEntityType[KeyWithShorthand] {
  key("id")
  key("uri")
}

