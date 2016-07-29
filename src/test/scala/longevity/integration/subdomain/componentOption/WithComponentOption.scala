package longevity.integration.subdomain.componentOption

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithComponentOption(
  id: WithComponentOptionId,
  component: Option[Component])
extends Root

object WithComponentOption extends RootType[WithComponentOption] {
  object props {
    val id = prop[WithComponentOptionId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
