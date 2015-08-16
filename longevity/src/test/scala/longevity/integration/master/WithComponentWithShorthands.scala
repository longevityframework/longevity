package longevity.integration.master

import longevity.subdomain._

case class WithComponentWithShorthands(
  uri: String,
  component: ComponentWithShorthands)
extends RootEntity

object WithComponentWithShorthands extends RootEntityType[WithComponentWithShorthands]
