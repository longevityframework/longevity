package longevity.integration.subdomain.allAttributes

import com.github.nscala_time.time.Imports._
import longevity.subdomain._
import shorthands._

case class AllAttributes(
  uri: String,
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
  key(prop[String]("uri"))
  index(prop[Boolean]("boolean"))
  index(prop[Char]("char"))
  index(prop[Double]("double"))
  index(prop[Float]("float"))
  index(prop[Int]("int"))
  index(prop[Long]("long"))
  index(prop[String]("string"))
  index(prop[DateTime]("dateTime"))
}
