package longevity.integration.withComponentWithAssoc

import longevity.subdomain._

case class ComponentWithAssoc(
  uri: String,
  associated: Assoc[Associated])
extends Entity

object ComponentWithAssoc extends EntityType[ComponentWithAssoc]
