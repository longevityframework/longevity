package longevity.integration.withAssoc

import longevity.domain._

case class WithAssoc(
  uri: String,
  associated: Assoc[Associated])
extends RootEntity

object WithAssoc extends RootEntityType[WithAssoc]
