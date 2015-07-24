package longevity.integration.allAttributes

import com.github.nscala_time.time.Imports._
import longevity.subdomain._

case class AllAttributes(
  boolean: Boolean,
  char: Char,
  double: Double,
  float: Float,
  int: Int,
  long: Long,
  string: String,
  dateTime: DateTime)
extends RootEntity

object AllAttributes extends RootEntityType[AllAttributes]
