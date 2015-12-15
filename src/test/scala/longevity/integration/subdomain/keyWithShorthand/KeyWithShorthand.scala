package longevity.integration.subdomain.keyWithShorthand

import longevity.subdomain._

case class KeyWithShorthand(id: String, uri: Uri) extends Root

object KeyWithShorthand extends RootType[KeyWithShorthand] {
  key("id")
  key("uri")
}

