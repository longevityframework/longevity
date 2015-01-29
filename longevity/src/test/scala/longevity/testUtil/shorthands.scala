package longevity.testUtil

import emblem._

/** a handful of shorthands used for testing */
object shorthands {

  lazy val pool =
    new ShorthandPool(emailShorthand, markdownShorthand, radiansShorthand, uriShorthand, zipcodeShorthand)

  case class Email(email: String)
  lazy val emailShorthand = shorthandFor[Email, String]

  case class Markdown(markdown: String)
  lazy val markdownShorthand = shorthandFor[Markdown, String]

  case class Radians(radians: Double)
  lazy val radiansShorthand = shorthandFor[Radians, Double]

  case class Uri(uri: String)
  lazy val uriShorthand = shorthandFor[Uri, String]

  case class Zipcode(zipcode: Int)
  lazy val zipcodeShorthand = shorthandFor[Zipcode, Int]

  // failure cases

  case class NoShorthand(noShorthand: String)

  trait Foo
  case class Bar(foo: Foo)
  lazy val barShorthand = shorthandFor[Bar, Foo]

}
