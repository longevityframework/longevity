package longevity.integration.subdomain.attributeOptions

import com.github.nscala_time.time.Imports._
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

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
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
