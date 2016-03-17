package longevity.integration.subdomain.multipleKeys

import longevity.subdomain._

case class MultipleKeys(
  uri: String,
  username: String)
extends Root

object MultipleKeys extends RootType[MultipleKeys] {
  key(prop[String]("uri"))
  key(prop[String]("username"))
  val keySet = kscan(this)
  val indexSet = iscan(this)
}
