package longevity.integration.subdomain.withAssocList

import longevity.subdomain._

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
