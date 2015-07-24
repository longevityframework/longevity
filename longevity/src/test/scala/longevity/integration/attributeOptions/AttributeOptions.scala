package longevity.integration.attributeOptions

import com.github.nscala_time.time.Imports._
import longevity.subdomain._

case class AttributeOptions(
  boolean: Option[Boolean],
  char: Option[Char],
  double: Option[Double],
  float: Option[Float],
  int: Option[Int],
  long: Option[Long],
  string: Option[String],
  dateTime: Option[DateTime])
extends RootEntity

object AttributeOptions extends RootEntityType[AttributeOptions]
