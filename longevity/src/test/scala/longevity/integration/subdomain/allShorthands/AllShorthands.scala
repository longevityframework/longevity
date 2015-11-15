package longevity.integration.subdomain.allShorthands

import longevity.subdomain._

case class AllShorthands(
  uri: String,
  boolean: BooleanShorthand,
  char: CharShorthand,
  double: DoubleShorthand,
  float: FloatShorthand,
  int: IntShorthand,
  long: LongShorthand,
  string: StringShorthand,
  dateTime: DateTimeShorthand)
extends RootEntity

object AllShorthands extends RootEntityType[AllShorthands] {
  key("uri")
}

