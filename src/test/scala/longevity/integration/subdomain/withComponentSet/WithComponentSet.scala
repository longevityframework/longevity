package longevity.integration.subdomain.withComponentSet

import longevity.subdomain._

case class WithComponentSet(
  uri: String,
  components: Set[Component])
extends Root

object WithComponentSet extends RootType[WithComponentSet] {
  key("uri")
}

