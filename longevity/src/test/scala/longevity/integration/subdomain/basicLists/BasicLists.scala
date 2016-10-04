package longevity.integration.subdomain.basicLists

import com.github.nscala_time.time.Imports._
import longevity.subdomain.Persistent
import longevity.subdomain.PType

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
extends Persistent

object BasicLists extends PType[BasicLists] {
  object props {
    val id = prop[BasicListsId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
