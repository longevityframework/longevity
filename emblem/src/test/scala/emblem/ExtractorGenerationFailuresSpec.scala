package emblem

import org.scalatest._
import emblem.exceptions._

/** specifications for cases where [[Extractor.apply Extractor generation]] should fail.
 *
 * non-error cases for emblem generation are covered elsewhere in the test suite.
 */
class ExtractorGenerationFailuresSpec extends FlatSpec with GivenWhenThen with Matchers {

  import emblem.testData.genFailure._
  import emblem.testData.extractors._

  behavior of "emblem.Extractor"

  it should "throw exception on non case class types" in {
    intercept[TypeIsNotCaseClassException] {
      Extractor[NotACaseClass, Any]
    }
  }

  it should "throw exception on case classes with multiple param lists" in {
    intercept[CaseClassHasMultipleParamListsException] {
      Extractor[MultipleParamLists, Any]
    }
  }

  it should "throw exception on inner case classes" in {
    intercept[CaseClassIsInnerClassException] {
      val hasInner = new HasInnerClass
      Extractor[hasInner.IsInnerCaseClass, Any]
    }
  }

  it should "throw exception on case classes with multiple params (one param list)" in {
    intercept[CaseClassHasMultipleParamsException] {
      Extractor[MultipleParams, Any]
    }
  }

  it should "throw exception when the requested short type does not match the case class parameter" in {
    intercept[UnexpectedDomainTypeException] {
      Extractor[Uri, Int]
    }
  }

  behavior of "the extractor"

  it should "contain type keys for the range and domain types" in {
    uriExtractor.domainTypeKey should equal (typeKey[Uri])
    uriExtractor.rangeTypeKey should equal (typeKey[String])
  }

  it should "provide conversion methods between the longhand and extractor types" in {
    val uriString = "panda"
    val uri = Uri(uriString)
    uriExtractor.apply(uri) should equal (uriString)
    uriExtractor.inverse(uriString) should equal (uri)
    uriExtractor.unapply(uriString) should equal (Some(uri))
  }

}
