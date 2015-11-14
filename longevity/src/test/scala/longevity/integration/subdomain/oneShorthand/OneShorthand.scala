package longevity.integration.subdomain.oneShorthand

import longevity.subdomain._

case class OneShorthand(id: String, uri: Uri) extends RootEntity

object OneShorthand extends RootEntityType[OneShorthand] {
  natKey("id")
}

