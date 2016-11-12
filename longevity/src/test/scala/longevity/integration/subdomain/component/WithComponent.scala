package longevity.integration.subdomain.component

import longevity.subdomain.PType

case class WithComponent(
  id: WithComponentId,
  component: Component)

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
