package longevity.integration.subdomain.componentSet

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class WithComponentSet(
  id: WithComponentSetId,
  components: Set[Component])
extends Persistent

object WithComponentSet extends PType[WithComponentSet] {
  object props {
    val id = prop[WithComponentSetId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
