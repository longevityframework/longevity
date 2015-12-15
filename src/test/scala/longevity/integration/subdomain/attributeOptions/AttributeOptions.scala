package longevity.integration.subdomain.attributeOptions

import com.github.nscala_time.time.Imports._
import longevity.subdomain._

case class AttributeOptions(
  uri: String,
  boolean: Option[Boolean],
  char: Option[Char],
  double: Option[Double],
  float: Option[Float],
  int: Option[Int],
  long: Option[Long],
  string: Option[String],
  dateTime: Option[DateTime])
extends Root

object AttributeOptions extends RootType[AttributeOptions] {
  key("uri")
}
