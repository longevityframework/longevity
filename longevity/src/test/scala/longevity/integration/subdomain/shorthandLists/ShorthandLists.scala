package longevity.integration.subdomain.shorthandLists

import longevity.model.annotations.persistent

@persistent(keySet = Set(key(props.id)))
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
