package emblem.testData

import emblem.emblematic.Emblem
import emblem.emblematic.EmblemPool

import emblem.testData.extractors._

/** a handful of emblems used for testing */
object emblems {

  lazy val emblemPool =
    EmblemPool() + friendEmblem + pointEmblem + withBarPropEmblem + withNoExtractorPropEmblem

  case class Friend(uri: Uri, email: Email)
  lazy val friendEmblem = Emblem[Friend]

  case class Point(x: Double, y: Double)
  lazy val pointEmblem = Emblem[Point]

  case class WithBarProp(i: Int, bar: Bar)
  lazy val withBarPropEmblem = Emblem[WithBarProp]

  case class WithNoExtractorProp(i: Int, noExtractor: NoExtractor)
  lazy val withNoExtractorPropEmblem = Emblem[WithNoExtractorProp]

  case class NotInPool()
  lazy val notInPoolEmblem = Emblem[NotInPool]

}
