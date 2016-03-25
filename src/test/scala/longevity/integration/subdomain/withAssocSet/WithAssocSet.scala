package longevity.integration.subdomain.withAssocSet

import longevity.subdomain.Assoc
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithAssocSet(
  uri: String,
  associated: Set[Assoc[Associated]])
extends Root

object WithAssocSet extends RootType[WithAssocSet] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
