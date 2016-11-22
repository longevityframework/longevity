package longevity.integration.subdomain.componentWithForeignKey

import longevity.subdomain.annotations.component

@component
case class ComponentWithForeignKey(
  id: String,
  associatedId: AssociatedId)
