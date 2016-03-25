package longevity.integration.subdomain.withAssoc

import shorthands._
import longevity.subdomain.Assoc
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithAssoc(
  uri: String,
  associated: Assoc[Associated])
extends Root

object WithAssoc extends RootType[WithAssoc] {
  object props {
    val uri = prop[String]("uri")
    val associated = prop[Assoc[Associated]]("associated")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
    val uri = index(props.uri)
    val associated = index(props.associated)
  }
}
