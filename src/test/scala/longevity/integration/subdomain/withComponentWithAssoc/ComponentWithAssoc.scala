package longevity.integration.subdomain.withComponentWithAssoc

import longevity.subdomain.Assoc
import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.EntityType

case class ComponentWithAssoc(
  uri: String,
  associated: Assoc[Associated])
extends Entity

object ComponentWithAssoc extends EntityType[ComponentWithAssoc]
