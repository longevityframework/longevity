package emblem.emblematic

import emblem.exceptions.DuplicateExtractorsException
import emblem.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.OptionValues.convertOptionToValuable

/** [[ExtractorPool extractor pool]] specifications */
class ExtractorPoolSpec extends FlatSpec with GivenWhenThen with Matchers {

  import emblem.testData.extractors._

  behavior of "the extractor pool"

  it should "provide a sequence of extractors in the pool" in {
    val extractorSeq = extractorPool.values
    extractorSeq.size should equal (5)
    extractorSeq should contain (uriExtractor)
    extractorSeq should contain (emailExtractor)
  }

  it should "provide a means to look up extractors by Long type" in {
    extractorPool.get(typeKey[Uri]).value should equal (uriExtractor)
    extractorPool.get(typeKey[Email]).value should equal (emailExtractor)
    extractorPool.get(typeKey[NoExtractor]) should equal (None)
  }

  behavior of "the extractor pool constructor"
  it should "throw exception if there are multiple longhands represented in the pool" in {
    val extraneousExtractor = Extractor[Email, String]
    intercept[DuplicateExtractorsException] {
      ExtractorPool(uriExtractor, emailExtractor, extraneousExtractor)
    }
  }

}
