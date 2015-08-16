package longevity.integration.master

import longevity.subdomain._

case class ComponentWithShorthands(
  boolean: BooleanShorthand,
  char: CharShorthand,
  double: DoubleShorthand,
  float: FloatShorthand,
  int: IntShorthand,
  long: LongShorthand,
  string: StringShorthand,
  dateTime: DateTimeShorthand)
extends Entity

object ComponentWithShorthands extends EntityType[ComponentWithShorthands]
