package longevity.integration.withAssocOption

import longevity.subdomain._

case class WithAssocOption(
  uri: String,
  associated: Option[Assoc[Associated]])
extends RootEntity

object WithAssocOption extends RootEntityType[WithAssocOption]
