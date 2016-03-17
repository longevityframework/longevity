package longevity.integration.subdomain.keyWithAssoc

import longevity.subdomain._

case class KeyWithAssoc(
  uri: Uri,
  associated: Assoc[Associated])
extends Root

object KeyWithAssoc extends RootType[KeyWithAssoc] {
  key(prop[Assoc[Associated]]("associated"))
  val keySet = kscan(this)
  val indexSet = iscan(this)
}
