package longevity.integration.model.componentWithList

import longevity.model.annotations.persistent

@persistent(keySet = Set(key(WithComponentWithList.props.id)))
case class WithComponentWithList(
  id: WithComponentWithListId,
  component: Component)
