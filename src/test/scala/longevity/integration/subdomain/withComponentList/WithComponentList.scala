package longevity.integration.subdomain.withComponentList

import longevity.subdomain._

case class WithComponentList(
  uri: String,
  components: List[Component])
extends Root

object WithComponentList extends RootType[WithComponentList] {
  key("uri")
}

