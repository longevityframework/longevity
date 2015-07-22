package longevity.integration.master

import longevity.subdomain._

case class WithComponentOption(
  uri: String,
  component: Option[Component])
extends RootEntity

object WithComponentOption extends RootEntityType[WithComponentOption]
