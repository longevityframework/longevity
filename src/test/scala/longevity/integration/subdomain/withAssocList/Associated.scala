package longevity.integration.subdomain.withAssocList

import longevity.subdomain._

case class Associated(uri: String) extends Root

object Associated extends RootType[Associated] {
  key(prop[String]("uri"))
  val keySet = kscan(this)
  val indexSet = iscan(this)
}
