package longevity.integration.subdomain.componentWithForeignKey

import longevity.model.annotations.component

@component
case class ComponentWithForeignKey(
  id: String,
  associatedId: AssociatedId)
