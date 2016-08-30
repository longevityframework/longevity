package longevity.integration.subdomain.componentWithForeignKey

import longevity.subdomain.embeddable.Entity

case class ComponentWithForeignKey(
  id: String,
  associatedId: AssociatedId)
extends Entity
