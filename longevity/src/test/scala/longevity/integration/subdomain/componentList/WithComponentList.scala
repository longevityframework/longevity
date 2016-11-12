package longevity.integration.subdomain.componentList

import longevity.subdomain.PType

case class WithComponentList(
  id: WithComponentListId,
  components: List[Component])

object WithComponentList extends PType[WithComponentList] {
  object props {
    val id = prop[WithComponentListId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
