package longevity.integration.subdomain.componentSet

import longevity.subdomain.PType
import longevity.subdomain.annotations.mprops

case class WithComponentSet(
  id: WithComponentSetId,
  components: Set[Component])

@mprops object WithComponentSet extends PType[WithComponentSet] {
  object keys {
    val id = key(props.id)
  }
}
