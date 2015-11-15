package longevity.integration.subdomain.keyWithAssoc

import longevity.subdomain._

case class KeyWithAssoc(
  uri: Uri,
  associated: Assoc[Associated])
extends RootEntity

object KeyWithAssoc extends RootEntityType[KeyWithAssoc] {
  key("associated")
}

