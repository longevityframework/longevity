package longevity.integration.subdomain.componentWithList

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithComponentWithList(
  id: WithComponentWithListId,
  component: Component)
extends Root

object WithComponentWithList extends RootType[WithComponentWithList] {
  object props {
    val id = prop[WithComponentWithListId]("id")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
  }
}
