package longevity.integration.subdomain.shorthandOptions

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(props.id)))
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
