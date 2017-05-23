package longevity.integration.model.component

import longevity.model.annotations.persistent

@persistent[DomainModel](
  keySet = Set(key(props.id)),
  indexSet = Set(index(props.component), index(props.foo)))
case class WithComponent(
  id: WithComponentId,
  foo: String,
  component: Component)
