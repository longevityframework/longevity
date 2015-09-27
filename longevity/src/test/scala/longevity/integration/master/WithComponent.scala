package longevity.integration.master

import longevity.subdomain._

case class WithComponent(
  uri: String,
  component: Component)
extends RootEntity

object WithComponent extends RootEntityType[WithComponent] {
  natKey("uri")
}

