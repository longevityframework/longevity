package longevity.integration.subdomain.componentOption

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class WithComponentOption(
  id: WithComponentOptionId,
  component: Option[Component])
extends Persistent

object WithComponentOption extends PType[WithComponentOption] {
  object props {
    val id = prop[WithComponentOptionId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
