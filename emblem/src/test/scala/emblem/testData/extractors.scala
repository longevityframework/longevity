package emblem.testData

import emblem._

/** a handful of extractors used for testing */
object extractors {

  lazy val extractorPool =
    ExtractorPool(emailExtractor, markdownExtractor, radiansExtractor, uriExtractor, zipcodeExtractor)

  case class Email(email: String)
  lazy val emailExtractor = extractorFor[String, Email]

  case class Markdown(markdown: String)
  lazy val markdownExtractor = extractorFor[String, Markdown]

  case class Radians(radians: Double)
  lazy val radiansExtractor = extractorFor[Double, Radians]

  case class Uri(uri: String)
  lazy val uriExtractor = extractorFor[String, Uri]

  case class Zipcode(zipcode: Int)
  lazy val zipcodeExtractor = extractorFor[Int, Zipcode]

  // failure cases

  case class NoExtractor(noExtractor: String)

  trait Foo
  case class Bar(foo: Foo)
  lazy val barExtractor = extractorFor[Bar, Foo]

}
