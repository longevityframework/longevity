package longevity.integration.subdomain.withAssocList

import longevity.subdomain._

case class WithAssocList(
  uri: String,
  associated: List[Assoc[Associated]])
extends RootEntity

object WithAssocList extends RootEntityType[WithAssocList] {
  key("uri")
}

