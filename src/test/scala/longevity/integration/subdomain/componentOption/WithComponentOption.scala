package longevity.integration.subdomain.componentOption

import longevity.ddd.subdomain.Root
import longevity.subdomain.PType

case class WithComponentOption(
  id: WithComponentOptionId,
  component: Option[Component])
extends Root

object WithComponentOption extends PType[WithComponentOption] {
  object props {
    val id = prop[WithComponentOptionId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
