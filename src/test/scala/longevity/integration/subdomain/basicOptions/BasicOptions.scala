package longevity.integration.subdomain.basicOptions

import com.github.nscala_time.time.Imports._
import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.RootType

case class BasicOptions(
  id: BasicOptionsId,
  boolean: Option[Boolean],
  char: Option[Char],
  double: Option[Double],
  float: Option[Float],
  int: Option[Int],
  long: Option[Long],
  string: Option[String],
  dateTime: Option[DateTime])
extends Root

object BasicOptions extends RootType[BasicOptions] {
  object props {
    val id = prop[BasicOptionsId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
