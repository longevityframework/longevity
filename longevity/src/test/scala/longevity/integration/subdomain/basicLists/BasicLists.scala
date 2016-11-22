package longevity.integration.subdomain.basicLists

import org.joda.time.DateTime
import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(BasicLists.props.id)))
case class BasicLists(
  id: BasicListsId,
  boolean: List[Boolean],
  char: List[Char],
  double: List[Double],
  float: List[Float],
  int: List[Int],
  long: List[Long],
  string: List[String],
  dateTime: List[DateTime])
