package longevity.integration.subdomain.basicOptions

import com.github.nscala_time.time.Imports._
import longevity.subdomain.PType

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

object BasicOptions extends PType[BasicOptions] {
  object props {
    val id = prop[BasicOptionsId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
