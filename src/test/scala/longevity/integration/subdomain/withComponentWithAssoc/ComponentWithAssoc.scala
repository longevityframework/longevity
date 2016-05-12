package longevity.integration.subdomain.withComponentWithAssoc

import longevity.subdomain.Assoc
import longevity.subdomain.entity.Entity
import longevity.subdomain.entity.EntityType

case class ComponentWithAssoc(
  uri: String,
  associated: Assoc[Associated])
extends Entity

object ComponentWithAssoc extends EntityType[ComponentWithAssoc]
