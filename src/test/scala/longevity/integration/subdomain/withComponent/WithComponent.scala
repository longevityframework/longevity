package longevity.integration.subdomain.withComponent

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithComponent(
  id: WithComponentId,
  component: Component)
extends Root

object WithComponent extends RootType[WithComponent] {
  object props {
    val id = prop[WithComponentId]("id")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
  }
}
