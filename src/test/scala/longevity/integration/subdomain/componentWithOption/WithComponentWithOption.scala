package longevity.integration.subdomain.componentWithOption

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class WithComponentWithOption(
  id: WithComponentWithOptionId,
  component: Component)
extends Persistent

object WithComponentWithOption extends PType[WithComponentWithOption] {
  object props {
    val id = prop[WithComponentWithOptionId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
