package longevity.integration.withAssoc

import longevity.domain._

case class WithAssoc(
  uri: String,
  associated: Assoc[Associated])
extends Entity

object WithAssoc extends EntityType[WithAssoc]
