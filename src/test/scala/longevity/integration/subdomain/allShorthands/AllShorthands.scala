package longevity.integration.subdomain.allShorthands

import longevity.subdomain._

import shorthands._

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
extends Root

object AllShorthands extends RootType[AllShorthands] {
  key(prop[String]("uri"))
  index(prop[BooleanShorthand]("boolean"))
  index(prop[CharShorthand]("char"))
  index(prop[DoubleShorthand]("double"))
  index(prop[FloatShorthand]("float"))
  index(prop[IntShorthand]("int"))
  index(prop[LongShorthand]("long"))
  index(prop[StringShorthand]("string"))
  index(prop[DateTimeShorthand]("dateTime"))
}
