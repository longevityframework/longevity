package longevity.integration.subdomain.component

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.PType

case class WithComponent(
  id: WithComponentId,
  component: Component)
extends Root

object WithComponent extends PType[WithComponent] {
  object props {
    val id = prop[WithComponentId]("id")
    val component = prop[Component]("component")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
    val component = index(props.component)
  }
}
