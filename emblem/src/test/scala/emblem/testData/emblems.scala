package emblem.testData

import emblem._
import shorthands._

/** a handful of emblems used for testing */
object emblems {

  lazy val emblemPool =
    TypeKeyMap[HasEmblem, Emblem]() + friendEmblem + pointEmblem + withBarPropEmblem + withNoShorthandPropEmblem

  case class Friend(uri: Uri, email: Email) extends HasEmblem
  lazy val friendEmblem = emblemFor[Friend]

  case class Point(x: Double, y: Double) extends HasEmblem
  lazy val pointEmblem = emblemFor[Point]

  case class WithBarProp(i: Int, bar: Bar) extends HasEmblem
  lazy val withBarPropEmblem = emblemFor[WithBarProp]

  case class WithNoShorthandProp(i: Int, noShorthand: NoShorthand) extends HasEmblem
  lazy val withNoShorthandPropEmblem = emblemFor[WithNoShorthandProp]

  case class NotInPool() extends HasEmblem
  lazy val notInPoolEmblem = emblemFor[NotInPool]

}
