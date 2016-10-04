package longevity.integration.subdomain.componentWithSet

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.PType

case class WithComponentWithSet(
  id: WithComponentWithSetId,
  component: Component)
extends Root

object WithComponentWithSet extends PType[WithComponentWithSet] {
  object props {
    val id = prop[WithComponentWithSetId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
