package longevity.integration.master

import longevity.subdomain._

case class WithComponentList(
  uri: String,
  components: List[Component])
extends RootEntity

object WithComponentList extends RootEntityType[WithComponentList] {
  natKey("uri")
}

