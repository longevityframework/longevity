package longevity.integration.subdomain.componentSet

import longevity.model.annotations.persistent

@persistent(keySet = Set(key(WithComponentSet.props.id)))
case class WithComponentSet(
  id: WithComponentSetId,
  components: Set[Component])
