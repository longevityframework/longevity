package longevity.integration.subdomain.allAttributes

import com.github.nscala_time.time.Imports._
import longevity.subdomain._
import shorthands._

case class AllAttributes(
  uri: String,
  boolean: Boolean,
  char: Char,
  double: Double,
  float: Float,
  int: Int,
  long: Long,
  string: String,
  dateTime: DateTime)
extends Root

object AllAttributes extends RootType[AllAttributes] {
  key("uri")
}
