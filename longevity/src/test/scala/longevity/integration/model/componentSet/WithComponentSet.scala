package longevity.integration.model.componentSet

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = Set(key(WithComponentSet.props.id)))
case class WithComponentSet(
  id: WithComponentSetId,
  components: Set[Component])
