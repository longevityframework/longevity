package longevity.integration.master

import longevity.subdomain._

case class WithAssoc(
  uri: String,
  associated: Assoc[Associated])
extends Entity

object WithAssoc extends EntityType[WithAssoc]
