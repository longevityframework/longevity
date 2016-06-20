package longevity.integration.subdomain.withComponentWithShorthands

import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.EntityType

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
