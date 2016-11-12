package longevity.integration.subdomain.basics

import org.joda.time.DateTime
import longevity.subdomain.PType

case class Basics(
  id: BasicsId,
  boolean: Boolean,
  char: Char,
  double: Double,
  float: Float,
  int: Int,
  long: Long,
  string: String,
  dateTime: DateTime)

object Basics extends PType[Basics] {
  object props {
    val id = prop[BasicsId]("id")
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
