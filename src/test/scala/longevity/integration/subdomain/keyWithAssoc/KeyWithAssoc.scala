package longevity.integration.subdomain.keyWithAssoc

import longevity.subdomain._

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
