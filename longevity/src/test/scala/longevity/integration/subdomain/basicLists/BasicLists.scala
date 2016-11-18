package longevity.integration.subdomain.basicLists

import org.joda.time.DateTime
import longevity.subdomain.PType
import longevity.subdomain.mprops

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

@mprops object BasicLists extends PType[BasicLists] {
  object keys {
    val id = key(props.id)
  }
}
