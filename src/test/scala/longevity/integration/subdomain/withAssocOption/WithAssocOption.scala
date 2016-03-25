package longevity.integration.subdomain.withAssocOption

import longevity.subdomain.Assoc
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithAssocOption(
  uri: String,
  associated: Option[Assoc[Associated]])
extends Root

object WithAssocOption extends RootType[WithAssocOption] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
