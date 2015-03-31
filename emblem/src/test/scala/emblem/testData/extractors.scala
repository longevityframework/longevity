package emblem.testData

import emblem.imports._

/** a handful of extractors used for testing */
object extractors {

  lazy val extractorPool =
    ExtractorPool(emailExtractor, markdownExtractor, radiansExtractor, uriExtractor, zipcodeExtractor)

  case class Email(email: String)
  lazy val emailExtractor = Extractor[Email, String]

  case class Markdown(markdown: String)
  lazy val markdownExtractor = Extractor[Markdown, String]

  case class Radians(radians: Double)
  lazy val radiansExtractor = Extractor[Radians, Double]

  case class Uri(uri: String)
  lazy val uriExtractor = Extractor[Uri, String]

  case class Zipcode(zipcode: Int)
  lazy val zipcodeExtractor = Extractor[Zipcode, Int]

  // failure cases

  case class NoExtractor(noExtractor: String)

  trait Foo
  case class Bar(foo: Foo)
  lazy val barExtractor = Extractor[Foo, Bar]

}
