package longevity.integration.subdomain.shorthandWithComponent

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class WithShorthandWithComponent(
  id: WithShorthandWithComponentId,
  shorthandWithComponent: ShorthandWithComponent)
extends Persistent

object WithShorthandWithComponent extends PType[WithShorthandWithComponent] {
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
