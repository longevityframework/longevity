package longevity.integration.natKeyWithShorthand

import longevity.subdomain._

case class NatKeyWithShorthand(id: String, uri: Uri) extends RootEntity

object NatKeyWithShorthand extends RootEntityType[NatKeyWithShorthand] {
  natKey("id")
  natKey("uri")
}

