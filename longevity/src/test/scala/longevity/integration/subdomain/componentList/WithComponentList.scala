package longevity.integration.subdomain.componentList

import longevity.subdomain.PType
import longevity.subdomain.mprops

case class WithComponentList(
  id: WithComponentListId,
  components: List[Component])

@mprops object WithComponentList extends PType[WithComponentList] {
  object keys {
    val id = key(props.id)
  }
}
