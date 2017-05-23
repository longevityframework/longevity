package longevity.integration.model.shorthandOptions

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = Set(key(props.id)))
case class ShorthandOptions(
  id: ShorthandOptionsId,
  boolean: Option[BooleanShorthand],
  char: Option[CharShorthand],
  double: Option[DoubleShorthand],
  float: Option[FloatShorthand],
  int: Option[IntShorthand],
  long: Option[LongShorthand],
  string: Option[StringShorthand],
  dateTime: Option[DateTimeShorthand])
