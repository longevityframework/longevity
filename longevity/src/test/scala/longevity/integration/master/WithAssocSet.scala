package longevity.integration.master

import longevity.domain._

case class WithAssocSet(
  uri: String,
  associated: Set[Assoc[Associated]])
extends Entity

object WithAssocSet extends EntityType[WithAssocSet]
