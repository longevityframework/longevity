package longevity.integration.subdomain.withComponentWithShorthands

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithComponentWithShorthands(
  uri: String,
  component: ComponentWithShorthands)
extends Root

object WithComponentWithShorthands extends RootType[WithComponentWithShorthands] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
