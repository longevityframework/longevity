package longevity.integration.subdomain.withComponentWithAssoc

import longevity.subdomain._

case class WithComponentWithAssoc(
  uri: String,
  component: ComponentWithAssoc)
extends RootEntity

object WithComponentWithAssoc extends RootEntityType[WithComponentWithAssoc] {
  key("uri")
}

