package longevity.integration.subdomain.componentSet

import longevity.subdomain.PType

case class WithComponentSet(
  id: WithComponentSetId,
  components: Set[Component])

object WithComponentSet extends PType[WithComponentSet] {
  object props {
    val id = prop[WithComponentSetId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
