package longevity.integration.subdomain.withComponentWithSinglePropComponent

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithComponentWithSinglePropComponent(
  id: WithComponentWithSinglePropComponentId,
  component: ComponentWithSinglePropComponent)
extends Root

object WithComponentWithSinglePropComponent extends RootType[WithComponentWithSinglePropComponent] {
  object props {
    val id = prop[WithComponentWithSinglePropComponentId]("id")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
  }
}
