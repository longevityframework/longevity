package longevity.integration.model.shorthandLists

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class ShorthandLists(
  id: ShorthandListsId,
  boolean: List[BooleanShorthand],
  char: List[CharShorthand],
  double: List[DoubleShorthand],
  float: List[FloatShorthand],
  int: List[IntShorthand],
  long: List[LongShorthand],
  string: List[StringShorthand],
  dateTime: List[DateTimeShorthand])

object ShorthandLists {
  implicit lazy val idKey = key(props.id)
}
