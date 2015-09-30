package longevity.integration.master

import com.github.nscala_time.time.Imports._
import longevity.subdomain._

case class AttributeSets(
  uri: String,
  boolean: Set[Boolean],
  char: Set[Char],
  double: Set[Double],
  float: Set[Float],
  int: Set[Int],
  long: Set[Long],
  string: Set[String],
  dateTime: Set[DateTime])
extends RootEntity

object AttributeSets extends RootEntityType[AttributeSets] {
  natKey("uri")
}
