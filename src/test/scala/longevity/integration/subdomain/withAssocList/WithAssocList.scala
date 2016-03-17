package longevity.integration.subdomain.withAssocList

import longevity.subdomain._

case class WithAssocList(
  uri: String,
  associated: List[Assoc[Associated]])
extends Root

object WithAssocList extends RootType[WithAssocList] {
  key(prop[String]("uri"))
  val keySet = kscan(this)
  val indexSet = iscan(this)
}
