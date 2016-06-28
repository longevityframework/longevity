package longevity.integration.subdomain.withComponentWithForeignKey

import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.EntityType

case class ComponentWithForeignKey(
  id: String,
  associatedUri: AssociatedId)
extends Entity

object ComponentWithForeignKey extends EntityType[ComponentWithForeignKey]
