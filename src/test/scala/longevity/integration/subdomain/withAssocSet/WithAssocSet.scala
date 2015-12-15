package longevity.integration.subdomain.withAssocSet

import longevity.subdomain._

case class WithAssocSet(
  uri: String,
  associated: Set[Assoc[Associated]])
extends Root

object WithAssocSet extends RootType[WithAssocSet] {
  key("uri")
}

