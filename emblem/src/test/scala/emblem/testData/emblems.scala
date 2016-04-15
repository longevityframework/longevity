package emblem.testData

import emblem.Emblem
import emblem.EmblemPool
import emblem.HasEmblem
import emblem.testData.extractors._

/** a handful of emblems used for testing */
object emblems {

  lazy val emblemPool =
    EmblemPool() + friendEmblem + pointEmblem + withBarPropEmblem + withNoExtractorPropEmblem

  case class Friend(uri: Uri, email: Email) extends HasEmblem
  lazy val friendEmblem = Emblem[Friend]

  case class Point(x: Double, y: Double) extends HasEmblem
  lazy val pointEmblem = Emblem[Point]

  case class WithBarProp(i: Int, bar: Bar) extends HasEmblem
  lazy val withBarPropEmblem = Emblem[WithBarProp]

  case class WithNoExtractorProp(i: Int, noExtractor: NoExtractor) extends HasEmblem
  lazy val withNoExtractorPropEmblem = Emblem[WithNoExtractorProp]

  case class NotInPool() extends HasEmblem
  lazy val notInPoolEmblem = Emblem[NotInPool]

}
