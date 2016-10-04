package longevity.integration.subdomain.componentList

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.RootType

case class WithComponentList(
  id: WithComponentListId,
  components: List[Component])
extends Root

object WithComponentList extends RootType[WithComponentList] {
  object props {
    val id = prop[WithComponentListId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
