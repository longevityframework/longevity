package longevity.integration.subdomain.withAssocList

import longevity.subdomain.Assoc
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithAssocList(
  uri: String,
  associated: List[Assoc[Associated]])
extends Root

object WithAssocList extends RootType[WithAssocList] { 
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
