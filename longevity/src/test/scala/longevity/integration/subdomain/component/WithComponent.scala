package longevity.integration.subdomain.component

import longevity.model.annotations.persistent

@persistent(
  keySet = Set(key(props.id)),
  indexSet = Set(index(props.component), index(props.foo)))
case class WithComponent(
  id: WithComponentId,
  foo: String,
  component: Component)
