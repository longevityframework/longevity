package longevity.integration.model.componentList

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = Set(key(WithComponentList.props.id)))
case class WithComponentList(
  id: WithComponentListId,
  components: List[Component])
