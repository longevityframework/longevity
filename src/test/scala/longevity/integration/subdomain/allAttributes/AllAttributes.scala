package longevity.integration.subdomain.allAttributes

import org.joda.time.DateTime
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class AllAttributes(
  id: AllAttributesId,
  boolean: Boolean,
  char: Char,
  double: Double,
  float: Float,
  int: Int,
  long: Long,
  string: String,
  dateTime: DateTime)
extends Root

object AllAttributes extends RootType[AllAttributes] {
  object props {
    val id = prop[AllAttributesId]("id")
    val boolean = prop[Boolean]("boolean")
    val char = prop[Char]("char")
    val double = prop[Double]("double")
    val float = prop[Float]("float")
    val int = prop[Int]("int")
    val long = prop[Long]("long")
    val string = prop[String]("string")
    val dateTime = prop[DateTime]("dateTime")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
    val boolean = index(props.boolean)
    val char = index(props.char)
    val double = index(props.double)
    val float = index(props.float)
    val int = index(props.int)
    val long = index(props.long)
    val string = index(props.string)
    val dateTime = index(props.dateTime)
  }
}
