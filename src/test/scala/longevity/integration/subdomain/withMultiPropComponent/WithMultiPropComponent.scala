package longevity.integration.subdomain.withMultiPropComponent

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithMultiPropComponent(
  id: WithMultiPropComponentId,
  multiPropComponent: MultiPropComponent)
extends Root

object WithMultiPropComponent extends RootType[WithMultiPropComponent] {
  object props {
    val id = prop[WithMultiPropComponentId]("id")
    val multiPropComponent = prop[MultiPropComponent]("multiPropComponent")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
    val multiPropComponent = index(props.multiPropComponent)
  }
}
