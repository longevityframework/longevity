package longevity.integration.subdomain.componentShorthands

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithComponentShorthands(
  id: WithComponentShorthandsId,
  component: ComponentShorthands)
extends Root

object WithComponentShorthands extends RootType[WithComponentShorthands] {
  object props {
    val id = prop[WithComponentShorthandsId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
