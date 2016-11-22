package longevity.integration.subdomain.componentWithForeignKey

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class WithComponentWithForeignKey(
  id: WithComponentWithForeignKeyId,
  component: ComponentWithForeignKey)
