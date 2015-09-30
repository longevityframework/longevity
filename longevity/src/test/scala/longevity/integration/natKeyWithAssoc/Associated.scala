package longevity.integration.natKeyWithAssoc

import longevity.subdomain._

case class Associated(uri: String) extends RootEntity

object Associated extends RootEntityType[Associated] {
  natKey("uri")
}

