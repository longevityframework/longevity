package longevity.integration.subdomain.withComponentOption

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithComponentOption(
  uri: String,
  component: Option[Component])
extends Root

object WithComponentOption extends RootType[WithComponentOption] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
