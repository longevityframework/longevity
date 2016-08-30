package longevity.integration.subdomain.componentShorthands

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithComponentWithShorthand(
  id: WithComponentWithShorthandId,
  component: ComponentWithShorthand)
extends Root

object WithComponentWithShorthand extends RootType[WithComponentWithShorthand] {
  object props {
    val id = prop[WithComponentWithShorthandId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
