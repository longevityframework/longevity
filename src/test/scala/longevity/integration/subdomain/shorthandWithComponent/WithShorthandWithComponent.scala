package longevity.integration.subdomain.shorthandWithComponent

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithShorthandWithComponent(
  id: WithShorthandWithComponentId,
  shorthandWithComponent: ShorthandWithComponent)
extends Root

object WithShorthandWithComponent extends RootType[WithShorthandWithComponent] {
  object props {
    val id = prop[WithShorthandWithComponentId]("id")
    val shorthandWithComponent = prop[ShorthandWithComponent]("shorthandWithComponent")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
    val shorthandWithComponent = index(props.shorthandWithComponent)
  }
}
