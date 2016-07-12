package longevity.integration.subdomain.componentWithForeignKey

import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.EntityType

case class ComponentWithForeignKey(
  id: String,
  associatedId: AssociatedId)
extends Entity

object ComponentWithForeignKey extends EntityType[ComponentWithForeignKey]
