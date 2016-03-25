package longevity.integration.subdomain.keyWithAssoc

import longevity.subdomain.Assoc
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class KeyWithAssoc(
  uri: Uri,
  associated: Assoc[Associated])
extends Root

object KeyWithAssoc extends RootType[KeyWithAssoc] {
  object props {
    val associated = prop[Assoc[Associated]]("associated")
  }
  object keys {
    val associated = key(props.associated)
  }
  object indexes {
  }
}
