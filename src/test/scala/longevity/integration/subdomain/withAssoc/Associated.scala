package longevity.integration.subdomain.withAssoc

import longevity.subdomain._
import shorthands._

case class Associated(uri: String) extends Root

object Associated extends RootType[Associated] {
  key(prop[String]("uri"))
  val keySet = kscan(this)
  val indexSet = iscan(this)
}
