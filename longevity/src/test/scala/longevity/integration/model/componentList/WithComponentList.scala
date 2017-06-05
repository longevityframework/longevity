package longevity.integration.model.componentList

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithComponentList(
  id: WithComponentListId,
  components: List[Component])

object WithComponentList {
  implicit lazy val idKey = key(props.id)
}
