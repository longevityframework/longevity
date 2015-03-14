package longevity.integration.master

import longevity.domain._

case class WithAssocSet(
  uri: String,
  associated: Set[Assoc[Associated]])
extends RootEntity

object WithAssocSet extends RootEntityType[WithAssocSet]
