package emblem

import org.scalatest._
import emblem.exceptions._

/** [[Extractor extractor]] specifications */
class ExtractorSpec extends FlatSpec with GivenWhenThen with Matchers {

  import emblem.testData.genFailure._
  import emblem.testData.extractors._

  behavior of "emblem.Extractor"

  it should "throw exception on non case class types" in {
    intercept[TypeIsNotCaseClassException] {
      Extractor[Any, NotACaseClass]
    }
  }

  it should "throw exception on case classes with multiple param lists" in {
    intercept[CaseClassHasMultipleParamListsException] {
      Extractor[Any, MultipleParamLists]
    }
  }

  it should "throw exception on inner case classes" in {
    intercept[CaseClassIsInnerClassException] {
      val hasInner = new HasInnerClass
      Extractor[Any, hasInner.IsInnerCaseClass]
    }
  }

  it should "throw exception on case classes with multiple params (one param list)" in {
    intercept[CaseClassHasMultipleParamsException] {
      Extractor[Any, MultipleParams]
    }
  }

  it should "throw exception when the requested short type does not match the case class parameter" in {
    intercept[UnexpectedDomainTypeException] {
      Extractor[Int, Uri]
    }
  }

  behavior of "the extractor"

  it should "contain type keys for the range and domain types" in {
    uriExtractor.rangeTypeKey should equal (typeKey[Uri])
    uriExtractor.domainTypeKey should equal (typeKey[String])
  }

  it should "provide conversion methods between the longhand and extractor types" in {
    val uriString = "panda"
    val uri = Uri(uriString)
    uriExtractor.apply(uriString) should equal (uri)
    uriExtractor.unapply(uri) should equal (uriString)
  }

}
