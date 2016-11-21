package longevity.integration.subdomain.componentOption

import longevity.subdomain.PType
import longevity.subdomain.mprops

case class WithComponentOption(
  id: WithComponentOptionId,
  component: Option[Component])

@mprops object WithComponentOption extends PType[WithComponentOption] {
  object keys {
    val id = key(props.id)
  }
}
