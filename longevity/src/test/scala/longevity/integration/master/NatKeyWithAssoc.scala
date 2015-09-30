package longevity.integration.master

import longevity.subdomain._

case class NatKeyWithAssoc(
  uri: Uri,
  associated: Assoc[Associated])
extends RootEntity

object NatKeyWithAssoc extends RootEntityType[NatKeyWithAssoc] {
  natKey("uri")
  natKey("associated")
}

