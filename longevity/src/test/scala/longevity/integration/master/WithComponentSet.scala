package longevity.integration.master

import longevity.subdomain._

case class WithComponentSet(
  uri: String,
  components: Set[Component])
extends RootEntity

object WithComponentSet extends RootEntityType[WithComponentSet] {
  natKey("uri")
}

