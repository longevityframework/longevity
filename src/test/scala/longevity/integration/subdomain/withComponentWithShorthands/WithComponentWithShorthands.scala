package longevity.integration.subdomain.withComponentWithShorthands

import longevity.subdomain._

case class WithComponentWithShorthands(
  uri: String,
  component: ComponentWithShorthands)
extends Root

object WithComponentWithShorthands extends RootType[WithComponentWithShorthands] {
  key("uri")
}

