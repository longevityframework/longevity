package longevity.integration.subdomain.componentWithOption

import longevity.ddd.subdomain.Root
import longevity.subdomain.PType

case class WithComponentWithOption(
  id: WithComponentWithOptionId,
  component: Component)
extends Root

object WithComponentWithOption extends PType[WithComponentWithOption] {
  object props {
    val id = prop[WithComponentWithOptionId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
