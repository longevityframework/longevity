package longevity.testUtil

import emblem._
import longevity.testUtil.shorthands._

/** a handful of emblems used for testing */
object emblems {

  lazy val pool =
    TypeKeyMap[HasEmblem, Emblem]() + pointEmblem + userEmblem + withNoShorthandPropEmblem + withBarPropEmblem

  case class Point(x: Double, y: Double) extends HasEmblem
  lazy val pointEmblem = emblemFor[Point]

  case class User(uri: Uri, email: Email) extends HasEmblem
  lazy val userEmblem = emblemFor[User]

  case class WithNoShorthandProp(i: Int, noShorthand: NoShorthand) extends HasEmblem
  lazy val withNoShorthandPropEmblem = emblemFor[WithNoShorthandProp]

  case class WithBarProp(i: Int, bar: Bar) extends HasEmblem
  lazy val withBarPropEmblem = emblemFor[WithBarProp]

  case class NotInPool() extends HasEmblem
  lazy val notInPoolEmblem = emblemFor[NotInPool]

}
