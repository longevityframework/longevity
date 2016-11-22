package longevity.integration.subdomain.componentWithSet

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class WithComponentWithSet(
  id: WithComponentWithSetId,
  component: Component)
