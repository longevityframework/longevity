package longevity.integration.allShorthands

import longevity.domain._

case class AllShorthands(
  boolean: BooleanShorthand,
  char: CharShorthand,
  double: DoubleShorthand,
  float: FloatShorthand,
  int: IntShorthand,
  long: LongShorthand,
  string: StringShorthand)
extends RootEntity

object AllShorthands extends RootEntityType[AllShorthands]
