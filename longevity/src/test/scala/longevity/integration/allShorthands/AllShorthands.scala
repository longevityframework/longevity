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
extends Entity

object AllShorthands extends EntityType[AllShorthands]
