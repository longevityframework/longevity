package emblem

import org.scalatest._
import emblem.exceptions._

/** [[Extractor extractor]] specifications */
class ExtractorSpec extends FlatSpec with GivenWhenThen with Matchers {

  import emblem.testData.genFailure._
  import emblem.testData.extractors._

  behavior of "emblem.extractorFor"

  it should "throw exception on non case class types" in {
    intercept[TypeIsNotCaseClassException] {
      extractorFor[NotACaseClass, Any]
    }
  }

  it should "throw exception on case classes with multiple param lists" in {
    intercept[CaseClassHasMultipleParamListsException] {
      extractorFor[MultipleParamLists, Any]
    }
  }

  it should "throw exception on inner case classes" in {
    intercept[CaseClassIsInnerClassException] {
      val hasInner = new HasInnerClass
      extractorFor[hasInner.IsInnerCaseClass, Any]
    }
  }

  it should "throw exception on case classes with multiple params (one param list)" in {
    intercept[CaseClassHasMultipleParamsException] {
      extractorFor[MultipleParams, Any]
    }
  }

  it should "throw exception when the requested short type does not match the case class parameter" in {
    intercept[UnexpectedAbbreviatedTypeException] {
      extractorFor[Uri, Int]
    }
  }

  behavior of "the extractor"

  it should "contain type keys for the actual and abbreviated types" in {
    uriExtractor.actualTypeKey should equal (typeKey[Uri])
    uriExtractor.abbreviatedTypeKey should equal (typeKey[String])
  }

  it should "provide conversion methods between the longhand and extractor types" in {
    val uriString = "panda"
    val uri = Uri(uriString)
    uriExtractor.abbreviate(uri) should equal (uriString)
    uriExtractor.unabbreviate(uriString) should equal (uri)
  }

}
