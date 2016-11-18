package longevity.integration.subdomain.component

import longevity.subdomain.PType
import longevity.subdomain.mprops

case class WithComponent(
  id: WithComponentId,
  component: Component)

@mprops object WithComponent extends PType[WithComponent] {
  object keys {
    val id = key(props.id)
  }
  object indexes {
    val component = index(props.component)
  }
}
