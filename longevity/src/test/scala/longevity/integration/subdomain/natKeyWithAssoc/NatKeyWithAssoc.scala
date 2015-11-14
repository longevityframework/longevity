package longevity.integration.subdomain.natKeyWithAssoc

import longevity.subdomain._

case class NatKeyWithAssoc(
  uri: Uri,
  associated: Assoc[Associated])
extends RootEntity

object NatKeyWithAssoc extends RootEntityType[NatKeyWithAssoc] {
  natKey("associated")
}

