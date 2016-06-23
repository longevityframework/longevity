package emblem.testData

import emblem.emblematic.Emblem
import emblem.emblematic.EmblemPool

/** a handful of emblems used for testing */
object emblems {

  lazy val emblemPool = EmblemPool(
    friendEmblem, pointEmblem, withBarPropEmblem,
    emailEmblem, markdownEmblem, radiansEmblem, uriEmblem, zipcodeEmblem)

  case class Friend(uri: Uri, email: Email)
  lazy val friendEmblem = Emblem[Friend]

  case class Point(x: Double, y: Double)
  lazy val pointEmblem = Emblem[Point]

  case class WithBarProp(i: Int, bar: Bar)
  lazy val withBarPropEmblem = Emblem[WithBarProp]

  case class NotInPool()
  lazy val notInPoolEmblem = Emblem[NotInPool]

  case class Email(email: String)
  lazy val emailEmblem = Emblem[Email]

  case class Markdown(markdown: String)
  lazy val markdownEmblem = Emblem[Markdown]

  case class Radians(radians: Double)
  lazy val radiansEmblem = Emblem[Radians]

  case class Uri(uri: String)
  lazy val uriEmblem = Emblem[Uri]

  case class Zipcode(zipcode: Int)
  lazy val zipcodeEmblem = Emblem[Zipcode]

  trait Foo
  case class Bar(foo: Foo)
  lazy val barEmblem = Emblem[Bar]

}
