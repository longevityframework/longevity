package longevity.integration.subdomain.withComponentWithAssoc

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithComponentWithAssoc(
  uri: String,
  component: ComponentWithAssoc)
extends Root

object WithComponentWithAssoc extends RootType[WithComponentWithAssoc] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
