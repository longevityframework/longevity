package longevity.integration.subdomain.keyWithAssoc

import longevity.subdomain._

case class Associated(uri: String) extends Root

object Associated extends RootType[Associated] {
  key("uri")
}

