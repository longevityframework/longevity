package longevity.integration.subdomain.componentSet

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithComponentSet(
  id: WithComponentSetId,
  components: Set[Component])
extends Root

object WithComponentSet extends RootType[WithComponentSet] {
  object props {
    val id = prop[WithComponentSetId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
