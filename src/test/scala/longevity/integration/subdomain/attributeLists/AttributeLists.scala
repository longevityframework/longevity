package longevity.integration.subdomain.attributeLists

import com.github.nscala_time.time.Imports._
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class AttributeLists(
  id: AttributeListsId,
  boolean: List[Boolean],
  char: List[Char],
  double: List[Double],
  float: List[Float],
  int: List[Int],
  long: List[Long],
  string: List[String],
  dateTime: List[DateTime])
extends Root

object AttributeLists extends RootType[AttributeLists] {
  object props {
    val id = prop[AttributeListsId]("id")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
  }
}
