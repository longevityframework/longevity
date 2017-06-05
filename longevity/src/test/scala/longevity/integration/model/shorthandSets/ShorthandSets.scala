package longevity.integration.model.shorthandSets

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class ShorthandSets(
  id: ShorthandSetsId,
  boolean: Set[BooleanShorthand],
  char: Set[CharShorthand],
  double: Set[DoubleShorthand],
  float: Set[FloatShorthand],
  int: Set[IntShorthand],
  long: Set[LongShorthand],
  string: Set[StringShorthand],
  dateTime: Set[DateTimeShorthand])

object ShorthandSets {
  implicit val idKey = key(props.id)
}
