package longevity.integration.subdomain.oneShorthand

import longevity.subdomain._

case class OneShorthand(id: String, uri: Uri) extends Root

object OneShorthand extends RootType[OneShorthand] {
  key(prop[String]("id"))
  val keySet = kscan(this)
  val indexSet = iscan(this)
}
